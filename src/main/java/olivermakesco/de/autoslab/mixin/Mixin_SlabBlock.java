package olivermakesco.de.autoslab.mixin;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlabBlock.class)
public class Mixin_SlabBlock extends Block {
	@Shadow
	@Final
	public static EnumProperty<SlabType> TYPE;

	@Shadow
	@Final
	protected static VoxelShape TOP_SHAPE;

	@Shadow
	@Final
	protected static VoxelShape BOTTOM_SHAPE;

	public Mixin_SlabBlock(Settings settings) {
		super(settings);
		throw new UnsupportedOperationException("Someone tried to instantiate a mixin!");
	}

	@Inject(at = @At("RETURN"), method = "getOutlineShape", cancellable = true)
	private void autoslab$getBetterOutline(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
		if (!(context instanceof EntityShapeContext entityContext)) return;
		SlabType slabType = state.get(TYPE);
		if (slabType != SlabType.DOUBLE) return;
		var entity = entityContext.getEntity();
		if (entity == null) return;
		Vec3d vec3d = entity.getCameraPosVec(0);
		Vec3d vec3d2 = entity.getRotationVec(0);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * 5, vec3d2.y * 5, vec3d2.z * 5);
		var cast = entity.world
				.raycast(
						new RaycastContext(
								vec3d, vec3d3, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity
						)
				);
		var side = cast.getSide();
		switch (side) {
			case UP -> cir.setReturnValue(TOP_SHAPE);
			case DOWN -> cir.setReturnValue(BOTTOM_SHAPE);
			default -> {
				var ypos = cast.getPos().y;
				var yoffset = ((ypos % 1) + 1) % 1;
				if (yoffset > 0.5) cir.setReturnValue(TOP_SHAPE);
				else cir.setReturnValue(BOTTOM_SHAPE);
			}
		}
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		SlabType slabType = state.get(TYPE);
		return switch (slabType) {
			case DOUBLE -> VoxelShapes.fullCube();
			case TOP -> TOP_SHAPE;
			default -> BOTTOM_SHAPE;
		};
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, state.with(TYPE, SlabType.TOP), blockEntity, stack);
	}
}
