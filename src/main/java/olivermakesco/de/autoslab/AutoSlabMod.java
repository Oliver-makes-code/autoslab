package olivermakesco.de.autoslab;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.resource.MultiPackResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.quiltmc.qsl.registry.api.event.RegistryMonitor;
import org.quiltmc.qsl.resource.loader.api.InMemoryResourcePack;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.ResourcePackRegistrationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AutoSlabMod implements ModInitializer, ResourcePackRegistrationContext.Callback {
	public static final String MODID = "autoslab";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final HashMap<Identifier, VerticalSlabBlock> GENERATED_BLOCKS = new HashMap<>();
	public static final HashMap<Identifier, BlockItem> GENERATED_ITEMS = new HashMap<>();
	public static final Gson gson = new Gson();

	@Override
	public void onInitialize(ModContainer mod) {
		RegistryMonitor.create(Registry.BLOCK).forAll(blockCtx -> {
			var block = blockCtx.value();
			if (!(block instanceof SlabBlock)) return;
			var id = blockCtx.id();
			var extendedId = extend(id);
			LOGGER.info("Registering vertical slab: "+id+" => "+extendedId);
			var verticalSlab = new VerticalSlabBlock(QuiltBlockSettings.copyOf(block), block);
			var item = new BlockItem(verticalSlab, new QuiltItemSettings());
			Registry.register(Registry.BLOCK, extendedId, verticalSlab);
			GENERATED_BLOCKS.put(id, verticalSlab);
			Registry.register(Registry.ITEM, extendedId, item);
			GENERATED_ITEMS.put(id, item);
		});
		ResourceLoader.get(ResourceType.CLIENT_RESOURCES).getRegisterDefaultResourcePackEvent().register(this);
	}

	public static final String BLOCKSTATE_BASE = """
			{
			  "variants": {
			    "type=bottom,east=false": {
			      "model": "%bottom_north%"
			    },
			    "type=bottom,east=true": {
			      "model": "%bottom_east%"
			    },
			    "type=double": {
			      "model": "%double%"
			    },
			    "type=top,east=false": {
			      "model": "%top_north%"
			    },
			    "type=top,east=true": {
			      "model": "%top_east%"
			    }
			  }
			}
			""";

	@Override
	public void onRegisterPack(@NotNull ResourcePackRegistrationContext context) {
		LOGGER.info("Registering resource pack");
		var manager = context.resourceManager();
		if (!(manager instanceof MultiPackResourceManager multiManager)) return;
		var pack = new InMemoryResourcePack.Named("AutoSlab resources");
		for (var id : GENERATED_BLOCKS.keySet()) {
			var extended = extend(id);
		}
		context.addResourcePack(pack);
	}

	public static Identifier extend(Identifier base) {
		return new Identifier(MODID, "generated/"+base.getNamespace()+"/"+base.getPath());
	}
	public static Identifier blockstate(Identifier base) {
		return new Identifier(base.getNamespace(), "blockstates/"+base.getPath()+".json");
	}
}
