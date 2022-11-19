package olivermakesco.de.autoslab;

import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoSlabMod implements ModInitializer {
	public static final String modid = "autoslab";
	public static final Logger logger = LoggerFactory.getLogger(modid);
	public static final VerticalSlabBlock TEST = new VerticalSlabBlock(QuiltBlockSettings.copyOf(Blocks.OAK_SLAB), Blocks.OAK_SLAB);
	public static final BlockItem TEST_ITEM = new BlockItem(TEST, new QuiltItemSettings());

	@Override
	public void onInitialize(ModContainer mod) {
		Registry.register(Registry.BLOCK, new Identifier(modid, "test_slab"), TEST);
		Registry.register(Registry.ITEM, new Identifier(modid, "test_slab"), TEST_ITEM);
	}
}
