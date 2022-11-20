package olivermakesco.de.autoslab.client;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockRenderView;

import java.util.function.Supplier;

public class DoubleSlabBakedModel extends ForwardingBakedModel {
	private final BakedModel parent;
	public DoubleSlabBakedModel(BakedModel baseModel, BakedModel parentModel) {
		wrapped = baseModel;
		parent = parentModel;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<RandomGenerator> randomSupplier, RenderContext context) {
		super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
	}
}
