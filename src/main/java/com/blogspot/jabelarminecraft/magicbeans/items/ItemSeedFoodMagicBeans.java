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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class ItemSeedFoodMagicBeans extends ItemFood implements IPlantable
{
    private final IBlockState theBlockPlant;

    public ItemSeedFoodMagicBeans(int parHealAmount, float parSaturationModifier, 
          Block parBlockPlant)
    {
        super(parHealAmount, parSaturationModifier, false);
        theBlockPlant = parBlockPlant.getDefaultState();
    }

    @Override
    public EnumActionResult onItemUse(ItemStack parItemStack, EntityPlayer parPlayer, 
          World parWorld, BlockPos parPos, EnumHand parHand, EnumFacing parSide, float parHitX, 
          float parHitY, float parHitZ)
    {
//    	// DEBUG
//    	System.out.println("ItemMagicBeans onItemUse()");
    	
        // only plant on top of a block
        if (parSide != EnumFacing.UP)
        {
//        	// DEBUG
//        	System.out.println("Can't plant since not top of block");
        	
            return EnumActionResult.FAIL;
        }        
        // check if player can edit the block on ground and block where
        // plant will grow.  Note that the canPlayerEdit class doesn't seem to 
        // be affected by the position parameters and really just checks player 
        // and item capability to edit
        else if (parPlayer.canPlayerEdit(parPos, parSide, parItemStack)) //  .canPlayerEdit(parX, parY+1, parZ, par7, parItemStack))
        {
//        	// DEBUG
//        	System.out.println("Player is allowed to edit");
        	
            // check that the soil is a type that can sustain the plant
            // and check that there is air above to give plant room to grow
            if (parWorld.getBlockState(parPos).getBlock() == Blocks.FARMLAND 
            		&& parWorld.isAirBlock(parPos.offset(parSide)))
            {
//            	// DEBUG
//            	System.out.println("Block can sustain plant so planting");
            	
                // place the plant block
            	if (theBlockPlant==null)
            	{
            		System.out.println("The plant block is null!");
            	}
                parWorld.setBlockState(parPos.offset(parSide), theBlockPlant);
                // decrement the seed item stack                
                --parItemStack.stackSize;
                return EnumActionResult.PASS;
            }
            else
            {
//            	// DEBUG
//            	System.out.println("This block cannot sustain the plant");
            	
                return EnumActionResult.FAIL;
            }
        }
        else
        {
//        	// DEBUG
//        	System.out.println("Player is not allowed to edit");
        	
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Crop;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos)
    {
        return theBlockPlant;
    }
}