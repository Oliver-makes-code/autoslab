package olivermakesco.de.autoslab.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import olivermakesco.de.autoslab.AutoSlabMod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public record DoubleSlabUnbakedModel(UnbakedModel baseModel, UnbakedModel parentModel) implements UnbakedModel {

	@Override
	public Collection<Identifier> getModelDependencies() {
		var parentDeps = parentModel.getModelDependencies();
		var baseDeps = baseModel.getModelDependencies();
		parentDeps.addAll(baseDeps);

		return parentDeps;
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
		var parentDeps = parentModel.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences);
		var baseDeps = baseModel.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences);
		parentDeps.addAll(baseDeps);

		return parentDeps;
	}

	@Nullable
	@Override
	public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
		return new DoubleSlabBakedModel(baseModel.bake(loader, textureGetter, rotationContainer, modelId), parentModel.bake(loader, textureGetter, rotationContainer, modelId));
	}
}
