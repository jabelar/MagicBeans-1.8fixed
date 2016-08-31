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

import com.blogspot.jabelarminecraft.magicbeans.entities.EntityGoldenEggThrown;
import com.blogspot.jabelarminecraft.magicbeans.utilities.Utilities;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class ItemGoldenEgg extends Item
{
    protected static final SoundEvent SOUND_EVENT_EQUIP = new SoundEvent(new ResourceLocation("random.bow"));
    protected EntityGoldenEggThrown entityEgg;

    public ItemGoldenEgg() 
    {
        setUnlocalizedName("golden_egg");
    	maxStackSize = 16; // same as regular egg
        setCreativeTab(CreativeTabs.MATERIALS);
    }

	/**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, EnumHand parHand)
    {
        if (!par3EntityPlayer.capabilities.isCreativeMode)
        {
            --par1ItemStack.stackSize;
        }

        par2World.playSound(par3EntityPlayer.posX,par3EntityPlayer.posY, par3EntityPlayer.posZ, SOUND_EVENT_EQUIP, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F), true);

        if (!par2World.isRemote)
        {
            entityEgg = new EntityGoldenEggThrown(par2World, par3EntityPlayer);
            par2World.spawnEntityInWorld(entityEgg);
        }

        return new ActionResult(EnumActionResult.PASS, par1ItemStack);
    }
    
//    @Override
//    @SideOnly(Side.CLIENT)
//    public int getColorFromItemStack(ItemStack par1ItemStack, int parColorType)
//    {
//        return (parColorType == 0) ? colorBase : colorSpots;
//    }
    
    @Override
    // Doing this override means that there is no localization for language
    // unless you specifically check for localization here and convert
	public String getItemStackDisplayName(ItemStack par1ItemStack)
    {
    	return Utilities.stringToGolden("Golden Egg", 4);
	}  
}