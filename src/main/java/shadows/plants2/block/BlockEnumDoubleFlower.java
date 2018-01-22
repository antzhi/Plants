package shadows.plants2.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.event.RegistryEvent.Register;
import shadows.placebo.Placebo;
import shadows.placebo.interfaces.IFlowerEnum;
import shadows.placebo.itemblock.ItemBlockEnum;
import shadows.placebo.util.PlaceboUtil;
import shadows.plants2.Plants2;
import shadows.plants2.data.Constants;
import shadows.plants2.util.PlantUtil;

public class BlockEnumDoubleFlower<E extends Enum<E> & IFlowerEnum> extends BlockEnumFlower<E> {

	public static final PropertyBool UPPER = PropertyBool.create("upper");

	public BlockEnumDoubleFlower(EnumPlantType plantType, E type) {
		super(plantType, type);
		setDefaultState(getDefaultState().withProperty(UPPER, false));
	}

	@Override
	public ItemBlock createItemBlock() {
		return new ItemBlockEnum<>(this);
	}

	@Override
	public void initModels(ModelRegistryEvent e) {
		PlaceboUtil.sMRL("double_plants", this, 0, "inventory=true,type=" + type.getName() + ",upper=true");
		Placebo.PROXY.useRenamedMapper(this, "double_plants");
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
	}

	@Override
	protected void checkAndDropBlock(World world, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(world, pos, state)) {
			boolean flag = state.getValue(UPPER);
			BlockPos blockpos = flag ? pos : pos.up();
			BlockPos blockpos1 = flag ? pos.down() : pos;
			Block block = flag ? this : world.getBlockState(blockpos).getBlock();
			Block block1 = flag ? world.getBlockState(blockpos1).getBlock() : this;

			if (!flag) this.dropBlockAsItem(world, pos, state, 0);

			if (block == this) {
				world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
			}

			if (block1 == this) {
				world.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
			}
		}
	}

	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		if (state.getBlock() != this) return super.canBlockStay(world, pos, state);
		if (state.getValue(UPPER)) {
			return world.getBlockState(pos.down()).getBlock() == this;
		} else {
			IBlockState iblockstate = world.getBlockState(pos.up());
			return iblockstate.getBlock() == this && super.canBlockStay(world, pos, iblockstate);
		}
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		if (state.getValue(UPPER)) {
			return null;
		} else {
			return Item.getItemFromBlock(this);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos.up(), state.withProperty(UPPER, true), 2);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (state.getValue(UPPER)) {
			if (world.getBlockState(pos.down()).getBlock() == this) {
				if (player.capabilities.isCreativeMode) {
					world.setBlockToAir(pos.down());
				} else {
					world.destroyBlock(pos.down(), true);
				}
			}
		} else if (world.getBlockState(pos.up()).getBlock() == this) {
			world.setBlockState(pos.up(), Blocks.AIR.getDefaultState(), 2);
		}
	}

	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, damageDropped(state));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(UPPER, meta == 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if (state.getValue(UPPER)) return 0;
		return 1;
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, Constants.INV, UPPER);
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (state.getBlock() == this && !state.getValue(UPPER) && world.getBlockState(pos.up()).getBlock() == this) world.setBlockToAir(pos.up());
		return world.setBlockToAir(pos);
	}

	@Override
	public boolean placeStateAt(IBlockState state, World world, BlockPos pos) {
		if (world.isAirBlock(pos.up()) && world.setBlockState(pos, state.withProperty(UPPER, false))) {
			this.onBlockPlacedBy(world, pos, state, null, null);
			return true;
		}
		return false;
	}

	@Override
	public void initRecipes(Register<IRecipe> event) {
		if (type.useForRecipes()) Plants2.HELPER.addShapeless(PlantUtil.getDyeForEnum(type.getColor(), 4), new ItemStack(this));
	}

}
