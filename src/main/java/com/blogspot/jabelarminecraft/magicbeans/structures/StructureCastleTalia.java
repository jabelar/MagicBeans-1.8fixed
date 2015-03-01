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

import static net.minecraftforge.common.ChestGenHooks.DUNGEON_CHEST;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;

/**
 * @author jabelar
 *
 */
public class StructureCastleTalia extends Structure
{

	public boolean hasSpawnedCastle = false;

	public StructureCastleTalia()
	{
		super("taliaCastle2");
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
		Block theBlock = theWorld.getBlockState(parPos).getBlock();
		if (theBlock == Blocks.chest)
		{
			TileEntityChest theTileEntity = (TileEntityChest) theWorld.getTileEntity(parPos);
			// populate regular dungeon chest items
			if (theTileEntity != null)
			{
				WeightedRandomChestContent.generateChestContents(theWorld.rand, ChestGenHooks.getItems(DUNGEON_CHEST, theWorld.rand), theTileEntity, ChestGenHooks.getCount(DUNGEON_CHEST, theWorld.rand));
			}
			else
			{
				// DEBUG
				System.out.println("StructureCastleTalia customizeTileEntity() the entity is null!");
			}
		}
		if (theBlock == Blocks.furnace)
		{
			TileEntityFurnace theTileEntity = (TileEntityFurnace) theWorld.getTileEntity(parPos);
			if (theTileEntity != null)
			{
				int chanceOfMeatType = theWorld.rand.nextInt(10);
				if (chanceOfMeatType <= 3) // randomize meat
				{
					theTileEntity.setInventorySlotContents(0, new ItemStack(Items.beef, 5));
					theTileEntity.setInventorySlotContents(1, new ItemStack(Items.coal, 1)); 
				}
				else if (chanceOfMeatType <=8)
				{
					theTileEntity.setInventorySlotContents(0, new ItemStack(Items.chicken, 5));
					theTileEntity.setInventorySlotContents(1, new ItemStack(Items.coal, 1)); 
				}
				else
				{
					theTileEntity.setInventorySlotContents(0, new ItemStack(Items.porkchop, 5));
					theTileEntity.setInventorySlotContents(1, new ItemStack(Items.coal, 1)); 
				}
			}
			else
			{
				// DEBUG
				System.out.println("StructureCastleTalia customizeTileEntity() the entity is null!");
			}
		}
		if (theBlock == Blocks.dispenser)
		{
			TileEntityDispenser theTileEntity = (TileEntityDispenser) theWorld.getTileEntity(parPos);
			if (theTileEntity != null)
			{
				int inventorySize = theTileEntity.getSizeInventory();
				for (int i=0; i < inventorySize; i++)
				{
					theTileEntity.setInventorySlotContents(i, new ItemStack(Items.arrow, 5));
				}
			}
			else
			{
				// DEBUG
				System.out.println("StructureCastleTalia customizeTileEntity() the entity is null!");
			}
		}
		if (theBlock == Blocks.brewing_stand)
		{
			TileEntityBrewingStand theTileEntity = (TileEntityBrewingStand) theWorld.getTileEntity(parPos);	
			if (theTileEntity != null)
			{
				// got potion damage values from http://minecraft.gamepedia.com/Potion#Data_value_table
				int chanceOfPotionType = theWorld.rand.nextInt(10);
				if (chanceOfPotionType <= 3) // randomize potion
				{
					// fire resistance
					theTileEntity.setInventorySlotContents(0, new ItemStack(Items.potionitem, 3, 8259));
				}
				else if (chanceOfPotionType <= 8) 
				{
					// regeneration
					theTileEntity.setInventorySlotContents(0, new ItemStack(Items.potionitem, 3, 8257));
				}
				else
				{
					// water breathing
					theTileEntity.setInventorySlotContents(0, new ItemStack(Items.potionitem, 3, 8269));
				}
			}
			else
			{
				// DEBUG
				System.out.println("StructureCastleTalia customizeTileEntity() the entity is null!");
			}
		}
	}

	@Override
	public void populateEntities()
	{
		if (!theWorld.isRemote)
		{
			String entityToSpawnName = "golden_goose";
	        String entityToSpawnNameFull = MagicBeans.MODID+"."+entityToSpawnName;
	        if (EntityList.stringToClassMapping.containsKey(entityToSpawnNameFull))
	        {
	            EntityLiving entityToSpawn = (EntityLiving) EntityList
	                  .createEntityByName(entityToSpawnNameFull, theWorld);
	            entityToSpawn.setLocationAndAngles(startX+12, startY+10, startZ+22, 
	                  MathHelper.wrapAngleTo180_float(theWorld.rand.nextFloat()
	                  * 360.0F), 0.0F);
	            ((EntityAgeable)entityToSpawn).setGrowingAge(0);
	            theWorld.spawnEntityInWorld(entityToSpawn);
	            entityToSpawn.playLivingSound();
	        }
	        else
	        {
	            //DEBUG
	            System.out.println("Entity not found "+entityToSpawnName);
	        }

			entityToSpawnName = "giant";
	        entityToSpawnNameFull = MagicBeans.MODID+"."+entityToSpawnName;
	        if (EntityList.stringToClassMapping.containsKey(entityToSpawnNameFull))
	        {
	            EntityLiving entityToSpawn = (EntityLiving) EntityList
	                  .createEntityByName(entityToSpawnNameFull, theWorld);
	            entityToSpawn.setLocationAndAngles(startX+13, startY+9, startZ+20, 
	                  MathHelper.wrapAngleTo180_float(theWorld.rand.nextFloat()
	                  * 360.0F), 0.0F);
	            theWorld.spawnEntityInWorld(entityToSpawn);
	            entityToSpawn.playLivingSound();
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
