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
*/

package com.blogspot.jabelarminecraft.magicbeans.structures;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * @author jabelar
 *
 */
public class StructureCastleJaden extends Structure
{

	public boolean hasSpawnedCastle = false;

	public StructureCastleJaden()
	{
		super("castleJaden");
	}
	
	@Override
	public void populateItems() 
	{
        // DEBUG
        System.out.println("Finished populating items in structure.");
		finishedPopulatingItems = true;
	}

	@Override
	public void customizeTileEntity(BlockPos parPos) 
	{
	    // DEBUG
	    System.out.println("Customizing tile entity");
		Block theBlock = theWorld.getBlockState(parPos).getBlock();
		if (theBlock == Blocks.DISPENSER)
		{
		    // DEBUG
		    System.out.println("Populating dispenser");
			TileEntityDispenser theTileEntity = (TileEntityDispenser) theWorld.getTileEntity(parPos);
			if (theTileEntity != null)
			{
			    // DEBUG
			    System.out.println("Found tile entity");
				int inventorySize = theTileEntity.getSizeInventory();
				// DEBUG
				System.out.println("With inventory size = "+inventorySize);
				for (int i=0; i < inventorySize; i++)
				{
				    // DEBUG
				    System.out.println("Setting contents for slot = "+i);
					theTileEntity.setInventorySlotContents(i, new ItemStack(Items.ARROW, 5));
					// DEBUG
					System.out.println("The slot contents are "+theTileEntity.getStackInSlot(i));
				}
			}
			else
			{
				// DEBUG
				System.out.println("StructureCastleTalia customizeTileEntity() the entity is null!");
			}
			
			theTileEntity.markDirty();
		}
		if (theBlock == Blocks.BREWING_STAND)
		{
			TileEntityBrewingStand theTileEntity = (TileEntityBrewingStand) theWorld.getTileEntity(parPos);	
			if (theTileEntity != null)
			{
				// got potion damage values from http://minecraft.gamepedia.com/Potion#Data_value_table
				for (int slot = 0; slot<theTileEntity.getSizeInventory(); slot++)
				{
					int chanceOfPotionType = theWorld.rand.nextInt(10);
					if (chanceOfPotionType <= 3) // randomize potion
					{
						// fire resistance
						theTileEntity.setInventorySlotContents(slot, new ItemStack(Items.POTIONITEM, 1, 8259));
					}
					else if (chanceOfPotionType <= 8) 
					{
						// regeneration
						theTileEntity.setInventorySlotContents(slot, new ItemStack(Items.POTIONITEM, 1, 8257));
					}
					else
					{
						// water breathing
						theTileEntity.setInventorySlotContents(slot, new ItemStack(Items.POTIONITEM, 1, 8269));
					}
				}
			}
			
			theTileEntity.markDirty();
		}	
	}

	@Override
	public void populateEntities()
	{
		if (!theWorld.isRemote)
		{
			String entityToSpawnName = "golden_goose";
	        String entityToSpawnNameFull = MagicBeans.MODID+"."+entityToSpawnName;
	        if (EntityList.NAME_TO_CLASS.containsKey(entityToSpawnNameFull))
	        {
	            EntityLiving entityToSpawn = (EntityLiving) EntityList
	                  .createEntityByName(entityToSpawnNameFull, theWorld);
	            entityToSpawn.setLocationAndAngles(startX+24, startY+10, startZ+22, 
	                  MathHelper.wrapDegrees(theWorld.rand.nextFloat()
	                  * 360.0F), 0.0F);
	            ((EntityAgeable)entityToSpawn).setGrowingAge(0);
	            theWorld.spawnEntityInWorld(entityToSpawn);
	            entityToSpawn.playLivingSound();
	            // DEBUG
	            System.out.println("Populating golden goose at "+entityToSpawn.posX+", "+entityToSpawn.posY+", "+entityToSpawn.posZ);
	        }
	        else
	        {
	            //DEBUG
	            System.out.println("Entity not found "+entityToSpawnName);
	        }

			entityToSpawnName = "giant";
	        entityToSpawnNameFull = MagicBeans.MODID+"."+entityToSpawnName;
	        if (EntityList.NAME_TO_CLASS.containsKey(entityToSpawnNameFull))
	        {
	            EntityLiving entityToSpawn = (EntityLiving) EntityList
	                  .createEntityByName(entityToSpawnNameFull, theWorld);
	            entityToSpawn.setLocationAndAngles(startX+24, startY+9, startZ+32, 
	                  MathHelper.wrapDegrees(theWorld.rand.nextFloat()
	                  * 360.0F), 0.0F);
	            theWorld.spawnEntityInWorld(entityToSpawn);
	            entityToSpawn.playLivingSound();
	            // DEBUG
	            System.out.println("Populating giant at "+entityToSpawn.posX+", "+entityToSpawn.posY+", "+entityToSpawn.posZ);
	        }
	        else
	        {
	            //DEBUG
	            System.out.println("Entity not found "+entityToSpawnName);
	        }

	        // DEBUG
	        System.out.println("Finished populating entities in structure.");
	        finishedPopulatingEntities = true;
		}
	}
}
