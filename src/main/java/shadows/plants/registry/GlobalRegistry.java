package shadows.plants.registry;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import shadows.plants.common.IMetaPlant;
import shadows.plants.registry.modules.CosmeticModule;
import shadows.plants.registry.modules.ModuleController;
import shadows.plants.util.Data;

public class GlobalRegistry {
	/*
	 * This class handles all registration, following registry inheritance. This
	 * class is at the top of the list, everything from this mod goes through
	 * here.
	 */

	public static void init() {
		ItemRegistry.init();
		BlockRegistry.init();
		OredictRegistry.init();
		RecipeRegistry.init();
	}

	public static void initModels() {
		BlockRegistry.initModels(ModuleController.getAllBlocks());
		ItemRegistry.initModels(ModuleController.getAllItems());
	}

	public static final CreativeTabs TAB = new CreativeTabs(Data.MODID) {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(CosmeticModule.cosmetic_1);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void displayAllRelevantItems(List<ItemStack> list) {
			for (Block block : ModuleController.getAllBlocks()) {
				int i = 0;
				if (block instanceof IMetaPlant)
					i = ((IMetaPlant) block).getMaxData();
				for (int k = 0; k <= i; k++) {
					list.add(new ItemStack(block, 1, k));
				}
			}
		}
	};

	public static final CreativeTabs TAB_I = new CreativeTabs(Data.MODID + (".items")) {
		@Override
		public Item getTabIconItem() {
			return CosmeticModule.pineapple;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void displayAllRelevantItems(List<ItemStack> list) {
			for (Item item : ModuleController.getAllItems()) {
				list.add(new ItemStack(item));
			}
		}
	};

}
