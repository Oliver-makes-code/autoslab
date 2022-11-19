package olivermakesco.de.autoslab.mixin;

import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
public class Mixin_ServerPlayerInteractionManager {
	@Shadow
	@Final
	protected ServerPlayerEntity player;

	@Shadow
	protected ServerWorld world;

	@Redirect(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
	private boolean tryBreakSlab(ServerWorld instance, BlockPos pos, boolean b) {
		var breakState = instance.getBlockState(pos);
		if (breakState.getBlock() instanceof SlabBlock) {
			SlabType slabType = breakState.get(SlabBlock.TYPE);
			if (slabType != SlabType.DOUBLE) return instance.removeBlock(pos, b);
			var entity = player;
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
			var breakType = switch (side) {
				case UP -> SlabType.BOTTOM;
				case DOWN -> SlabType.TOP;
				default -> {
					var ypos = cast.getPos().y;
					var yoffset = ((ypos % 1) + 1) % 1;
					if (yoffset > 0.5) yield SlabType.BOTTOM;
					else yield SlabType.TOP;
				}
			};
			var removed = instance.removeBlock(pos, b);
			world.setBlockState(pos, breakState.with(SlabBlock.TYPE, breakType));
			return removed;
		}
		return instance.removeBlock(pos, b);
	}
}
