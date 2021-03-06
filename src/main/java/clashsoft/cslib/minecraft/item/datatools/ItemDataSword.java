package clashsoft.cslib.minecraft.item.datatools;

import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDataSword extends ItemDataTool
{
	public static float	baseDamage	= 4F;
	
	public float		weaponDamage;
	
	public ItemDataSword(ToolMaterial toolMaterial)
	{
		super(baseDamage, toolMaterial, Collections.EMPTY_SET, "Sword");
		this.setMaxDamage(toolMaterial.getMaxUses());
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.maxStackSize = 1;
		this.weaponDamage = baseDamage + toolMaterial.getDamageVsEntity();
	}
	
	public static boolean isEfficientOnMaterial(Material material)
	{
		return material == Material.plants || material == Material.vine || material == Material.coral || material == Material.leaves || material == Material.gourd;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int metadata)
	{
		if (block == Blocks.web)
		{
			return 15.0F;
		}
		else
		{
			float f = isEfficientOnMaterial(block.getMaterial()) ? 1.5F : 1.0F;
			return super.getDigSpeed(stack, block, metadata) * f;
		}
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase living1, EntityLivingBase living2)
	{
		stack.damageItem(1, living2);
		return true;
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase living)
	{
		if (block.getBlockHardness(world, x, y, z) != 0.0D)
		{
			stack.damageItem(2, living);
		}
		return true;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.block;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 72000;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return stack;
	}
	
	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		return block == Blocks.web;
	}
}
