/**
    Copyright (C) 2014 by jabelar

    This file is part of jabelar's Minecraft Forge modding examples; as such,
    you can redistribute it and/or modify it under the terms of the GNU
    General Public License as published by the Free Software Foundation,
    either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    For a copy of the GNU General Public License see <http://www.gnu.org/licenses/>.

	If you're interested in licensing the code under different terms you can
	contact the author at julian_abelar@hotmail.com 
*/

package com.blogspot.jabelarminecraft.magicbeans.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.blogspot.jabelarminecraft.magicbeans.entities.EntityGoldenEggThrown;
import com.blogspot.jabelarminecraft.magicbeans.utilities.MagicBeansUtilities;

public class ItemGoldenEgg extends Item
{
    public int colorBase;
    public int colorSpots;
    protected EntityGoldenEggThrown entityEgg;
//    protected IIcon theIcon;

    public ItemGoldenEgg() 
    {
		this("Golden Goose", 0xF5E16F, 0xF5F56F);
	}
    
    public ItemGoldenEgg(String parEntityToSpawnName, int parPrimaryColor, int parSecondaryColor)
    {
        setUnlocalizedName("golden_egg");
    	maxStackSize = 16; // same as regular egg
        setCreativeTab(CreativeTabs.tabMaterials);
        colorBase = parPrimaryColor;
        colorSpots = parSecondaryColor;
    }

	/**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (!par3EntityPlayer.capabilities.isCreativeMode)
        {
            --par1ItemStack.stackSize;
        }

        par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!par2World.isRemote)
        {
            entityEgg = new EntityGoldenEggThrown(par2World, par3EntityPlayer);
            par2World.spawnEntityInWorld(entityEgg);
        }

        return par1ItemStack;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int parColorType)
    {
        return (parColorType == 0) ? colorBase : colorSpots;
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public boolean requiresMultipleRenderPasses()
//    {
//        return false;
//    }
    
    @Override
    // Doing this override means that there is no localization for language
    // unless you specifically check for localization here and convert
	public String getItemStackDisplayName(ItemStack par1ItemStack)
    {
    	return MagicBeansUtilities.stringToGolden("Golden Egg", 4);
	}  


//    @Override
//    @SideOnly(Side.CLIENT)
//    public void registerIcons(IIconRegister par1IconRegister)
//    {
//        super.registerIcons(par1IconRegister);
//        theIcon = par1IconRegister.registerIcon(this.getIconString() + "_overlay");
//    }
//    
//    /**
//     * Gets an icon index based on an item's damage value and the given render pass
//     */
//    @Override
//    @SideOnly(Side.CLIENT)
//    public IIcon getIconFromDamageForRenderPass(int par1, int par2)
//    {
//        return par2 > 0 ? theIcon : super.getIconFromDamageForRenderPass(par1, par2);
//    }

}