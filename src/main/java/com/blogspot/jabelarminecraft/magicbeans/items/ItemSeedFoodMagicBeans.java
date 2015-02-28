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
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class ItemSeedFoodMagicBeans extends ItemFood implements IPlantable
{
    private final IBlockState theBlockPlant;
    /**
     * Block ID of the soil this seed food should be planted on.
     */
    private final Block soilId;

    public ItemSeedFoodMagicBeans(int parHealAmount, float parSaturationModifier, 
          Block parBlockPlant, Block parSoilBlock)
    {
        super(parHealAmount, parSaturationModifier, false);
        theBlockPlant = parBlockPlant.getDefaultState();
        soilId = parSoilBlock;
    }

    @Override
    public boolean onItemUse(ItemStack parItemStack, EntityPlayer parPlayer, 
          World parWorld, BlockPos parPos, EnumFacing parSide, float parHitX, 
          float parHitY, float parHitZ)
    {
    	// DEBUG
    	System.out.println("ItemMagicBeans onItemUse()");
    	
        // only plant on top of a block
        if (parSide != EnumFacing.UP)
        {
        	// DEBUG
        	System.out.println("Can't plant since not top of block");
        	
            return false;
        }        
        // check if player can edit the block on ground and block where
        // plant will grow.  Note that the canPlayerEdit class doesn't seem to 
        // be affected by the position parameters and really just checks player 
        // and item capability to edit
        else if (parPlayer.func_175151_a(parPos, parSide, parItemStack)) //  .canPlayerEdit(parX, parY+1, parZ, par7, parItemStack))
        {
        	// DEBUG
        	System.out.println("Player is allowed to edit");
        	
            // check that the soil is a type that can sustain the plant
            // and check that there is air above to give plant room to grow
            if (true) // (parWorld.getBlockState(parPos).getBlock().canSustainPlant(parWorld, 
                  // parPos, EnumFacing.UP, this) && parWorld
                  // .isAirBlock(parPos))
            {
            	// DEBUG
            	System.out.println("Block can sustain plant so planting");
            	
                // place the plant block
            	if (theBlockPlant==null)
            	{
            		System.out.println("The plant block is null!");
            	}
                parWorld.setBlockState(parPos, theBlockPlant);
                // decrement the seed item stack                
                --parItemStack.stackSize;
                return true;
            }
            else
            {
            	// DEBUG
            	System.out.println("This block cannot sustain the plant");
            	
                return false;
            }
        }
        else
        {
        	// DEBUG
        	System.out.println("Player is not allowed to edit");
        	
            return false;
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

//    @Override
//    public int getPlantMetadata(IBlockAccess world, BlockPos pos)
//    {
//        return 0;
//    }

    public Block getSoilId()
    {
        return soilId;
     }
}