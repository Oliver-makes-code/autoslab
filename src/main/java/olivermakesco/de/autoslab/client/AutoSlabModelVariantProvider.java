package olivermakesco.de.autoslab.client;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import olivermakesco.de.autoslab.AutoSlabMod;
import olivermakesco.de.autoslab.VerticalSlabBlock;
import org.jetbrains.annotations.Nullable;

public record AutoSlabModelVariantProvider(ResourceManager manager) implements ModelVariantProvider {
	public static final Identifier baseSlabModelId = new Identifier(AutoSlabMod.MODID, "slab");

	public static ModelIdentifier variant(String variant) {
		return new ModelIdentifier(baseSlabModelId, variant);
	}

	@Override
	public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) {
		if (Registry.BLOCK.get(new Identifier(modelId.getNamespace(), modelId.getPath())) instanceof VerticalSlabBlock block) {
			if (block == AutoSlabMod.dummyBlock) return null;
			var baseModel = context.loadModel(variant(modelId.getVariant()));
			var parentModel = context.loadModel(BlockModels.getModelId(block.parent.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE)));
			return new DoubleSlabUnbakedModel(baseModel, parentModel);
		}
		return null;
	}
}
