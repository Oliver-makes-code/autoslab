package olivermakesco.de.autoslab;

import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class VerticalSlabBlock extends Block implements Waterloggable {
	public static final EnumProperty<SlabType> TYPE = Properties.SLAB_TYPE;
	public static final BooleanProperty EAST = Properties.EAST;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public static final VoxelShape NORTH_SHAPE = createCuboidShape(0, 0, 8, 16, 16, 16);
	public static final VoxelShape SOUTH_SHAPE = createCuboidShape(0, 0, 0, 16, 16, 8);
	public static final VoxelShape EAST_SHAPE = createCuboidShape(0, 0, 0, 8, 16, 16);
	public static final VoxelShape WEST_SHAPE = createCuboidShape(8, 0, 0, 16, 16, 16);

	public final SlabBlock parent;

	public VerticalSlabBlock(Settings settings, SlabBlock parent) {
		super(settings);
		setDefaultState(
				getDefaultState()
						.with(TYPE, SlabType.BOTTOM)
						.with(WATERLOGGED, false)
						.with(EAST, false)
		);
		this.parent = parent;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(TYPE, WATERLOGGED, EAST);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		SlabType slabType = state.get(TYPE);
		return switch (slabType) {
			case DOUBLE -> VoxelShapes.fullCube();
			case TOP -> pickDir(state.get(EAST), EAST_SHAPE, NORTH_SHAPE);
			default -> pickDir(state.get(EAST), WEST_SHAPE, SOUTH_SHAPE);
		};
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		SlabType slabType = state.get(TYPE);
		return switch (slabType) {
			case DOUBLE -> VoxelShapes.fullCube();
			case TOP -> pickDir(state.get(EAST), EAST_SHAPE, NORTH_SHAPE);
			default -> pickDir(state.get(EAST), WEST_SHAPE, SOUTH_SHAPE);
		};
	}

	public VoxelShape pickDir(boolean east, VoxelShape eastState, VoxelShape westState) {
		return east ? eastState : westState;
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return state.get(TYPE) != SlabType.DOUBLE;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		BlockState blockState = ctx.getWorld().getBlockState(blockPos);
		if (blockState.isOf(this)) {
			return blockState.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
		} else {
			FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
			BlockState blockState2 = getDefaultState().with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
			Direction direction = ctx.getSide();
			return switch (direction) {
				case NORTH -> blockState2.with(TYPE, SlabType.TOP);
				case SOUTH -> blockState2.with(TYPE, SlabType.BOTTOM);
				case EAST -> blockState2.with(TYPE, SlabType.TOP).with(EAST, true);
				case WEST -> blockState2.with(TYPE, SlabType.BOTTOM).with(EAST, true);
				// TODO
				default -> {
					var playerFacing = ctx.getPlayerFacing();
					yield switch (playerFacing) {
						case NORTH -> blockState2.with(TYPE, SlabType.BOTTOM);
						case SOUTH -> blockState2.with(TYPE, SlabType.TOP);
						case EAST -> blockState2.with(TYPE, SlabType.BOTTOM).with(EAST, true);
						case WEST -> blockState2.with(TYPE, SlabType.TOP).with(EAST, true);
						default -> blockState2;
					};
				}
			};
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		return state.get(TYPE) != SlabType.DOUBLE && Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return state.get(TYPE) != SlabType.DOUBLE && Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
			BlockState state,
			Direction direction,
			BlockState neighborState,
			WorldAccess world,
			BlockPos pos,
			BlockPos neighborPos
	) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return type == NavigationType.WATER && world.getFluidState(pos).isIn(FluidTags.WATER);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		ItemStack itemStack = context.getStack();
		SlabType slabType = state.get(TYPE);
		if (slabType == SlabType.DOUBLE || !itemStack.isOf(this.asItem())) {
			return false;
		} else if (context.canReplaceExisting()) {
			//TODO
			return false;
		} else {
			return true;
		}
	}
}
