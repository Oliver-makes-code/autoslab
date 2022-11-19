package olivermakesco.de.autoslab.client;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockRenderView;
import org.quiltmc.loader.impl.lib.sat4j.specs.IConstr;

import java.util.function.Supplier;

public class DoubleSlabBakedModel extends ForwardingBakedModel {
	public DoubleSlabBakedModel(BakedModel baseModel) {
		this.wrapped = baseModel;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<RandomGenerator> randomSupplier, RenderContext context) {
		context.pushTransform(quad -> {
			return true;
		});
		context.popTransform();
	}
}
