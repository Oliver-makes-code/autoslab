package olivermakesco.de.autoslab;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
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

public class AutoSlabMod implements ModInitializer {
	public static final String MODID = "autoslab";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final HashMap<Identifier, VerticalSlabBlock> GENERATED_BLOCKS = new HashMap<>();
	public static final HashMap<Identifier, BlockItem> GENERATED_ITEMS = new HashMap<>();
	public static final VerticalSlabBlock dummyBlock = new VerticalSlabBlock(QuiltBlockSettings.copyOf(Blocks.OAK_SLAB), (SlabBlock)Blocks.OAK_SLAB);

	@Override
	public void onInitialize(ModContainer mod) {
		Registry.register(Registry.BLOCK, new Identifier(MODID, "slab"), dummyBlock);
		RegistryMonitor.create(Registry.BLOCK).forAll(blockCtx -> {
			var block = blockCtx.value();
			if (!(block instanceof SlabBlock slabBlock)) return;
			var id = blockCtx.id();
			var extendedId = extend(id);
			LOGGER.info("Registering vertical slab: "+id+" => "+extendedId);
			var verticalSlab = new VerticalSlabBlock(QuiltBlockSettings.copyOf(block), slabBlock);
			var item = new BlockItem(verticalSlab, new QuiltItemSettings());
			Registry.register(Registry.BLOCK, extendedId, verticalSlab);
			GENERATED_BLOCKS.put(id, verticalSlab);
			Registry.register(Registry.ITEM, extendedId, item);
			GENERATED_ITEMS.put(id, item);
		});

	}

	public static Identifier extend(Identifier base) {
		return new Identifier(MODID, "generated/"+base.getNamespace()+"/"+base.getPath());
	}
}
