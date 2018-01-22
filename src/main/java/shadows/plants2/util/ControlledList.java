package shadows.plants2.util;

import java.util.ArrayList;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.config.Configuration;
import shadows.plants2.Plants2;

public class ControlledList extends ArrayList<IBlockState> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(IBlockState state) {
		if (Plants2.ALT_CONFIG.getBoolean("Allow " + translate(state), Configuration.CATEGORY_GENERAL, true, "If this blockstate will generate")) return super.add(state);
		return false;
	}

	@Override
	public void add(int index, IBlockState state) {
		if (Plants2.ALT_CONFIG.getBoolean("Allow " + translate(state), Configuration.CATEGORY_GENERAL, true, "If this blockstate will generate")) super.add(index, state);
	}

	private String translate(IBlockState state) {
		return state.getBlock().getRegistryName().getResourcePath();
	}

}
