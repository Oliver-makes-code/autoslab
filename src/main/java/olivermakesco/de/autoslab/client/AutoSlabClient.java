package olivermakesco.de.autoslab.client;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class AutoSlabClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(AutoSlabModelVariantProvider::new);
	}
}
