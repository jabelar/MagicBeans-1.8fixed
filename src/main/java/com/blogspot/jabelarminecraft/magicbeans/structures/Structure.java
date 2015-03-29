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

package com.blogspot.jabelarminecraft.magicbeans.structures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.ModWorldData;
import com.blogspot.jabelarminecraft.magicbeans.blocks.BlockMagicBeanStalk;

public class Structure implements IStructure
{
	protected String theName;
	
	protected World theWorld;
	protected Entity theEntity;
	protected TileEntity theTileEntity;
	
	protected int dimX;
	protected int dimY;
	protected int dimZ;
	protected int totalVolume;
	
	protected int startX;
	protected int startY;
	protected int startZ;
	protected BlockPos startPos;
	
	protected int cloudMarginX = 15;
	protected int cloudMarginZ = 15;
	
	public boolean shouldGenerate = false;
	public boolean finishedGeneratingCloud = false; // cloud generation, this is unique to this mod
	public boolean finishedGeneratingBasic = false; // basic block generation
	public boolean finishedGeneratingMeta = false; // blocks with metadata generation
	public boolean finishedGeneratingSpecial = false; // special blocks like tripwire
	public boolean finishedPopulatingItems = false; // items into inventories and such
	public boolean finishedPopulatingEntities = false; // default entities that inhabit structure
	protected int ticksGenerating = 0;

	String[][][] blockNameArray = null;
	int[][][] blockMetaArray = null;

	BufferedReader readIn;

	public Structure(String parName)
	{
		theName = parName;
		readArrays(theName);
	}
	
	@Override
	public String getName()
	{
		return theName;
	}
	
	@Override
	public int getDimX()
	{
		return dimX;
	}
				
	@Override
	public int getDimY()
	{
		return dimY;
	}
				
	@Override
	public int getDimZ()
	{
		return dimZ;
	}
				
	@Override
	public String[][][] getBlockNameArray()
	{
		return blockNameArray;
	}
	
	@Override
	public int[][][] getBlockMetaArray()
	{
		return blockMetaArray;
	}
				
	@Override
	public void readArrays(String parName)
	{
	    try 
	    {
	    	System.out.println("Reading file = "+parName+".txt");
			readIn = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
					.getResourceAsStream("assets/magicbeans/structures/"+parName+".txt"), "UTF-8"));
		    dimX = Integer.valueOf(readIn.readLine());
		    dimY = Integer.valueOf(readIn.readLine());
		    dimZ = Integer.valueOf(readIn.readLine());
		    blockNameArray = new String[dimX][dimY][dimZ];
		    blockMetaArray = new int[dimX][dimY][dimZ];
		    System.out.println("Dimensions of structure = "+dimX+", "+dimY+", "+dimZ);
		    for (int indY = 0; indY < dimY; indY++) // Y first to organize in vertical layers
		    {
		    	for (int indX = 0; indX < dimX; indX++)
		    	{
		    		for (int indZ = 0; indZ < dimZ; indZ++)
		    		{
		    			blockNameArray[indX][indY][indZ] = readIn.readLine();
		    			blockMetaArray[indX][indY][indZ] = Integer.valueOf(readIn.readLine());
		    		}
		    	}
		    }
		} 
	    catch (FileNotFoundException e) 
	    {
			e.printStackTrace();
		} 
	    catch (IOException e) 
	    {
			e.printStackTrace();
		}
	    
	    try 
	    {
			readIn.close();
		} 
	    catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public void generateTick(TileEntity parEntity, int parOffsetX, int parOffsetY, int parOffsetZ) 
	{
		// DEBUG
		//System.out.println("Structure generateTick, finishedPopulatingEntities ="+finishedPopulatingEntities);
		
		// exit if generating not started
		if (!shouldGenerate)
		{
			return;
		}
		
		theTileEntity = parEntity;
		theWorld = theTileEntity.getWorld();

		if (theWorld.isRemote)
		{
			return;
		}

		// exit if finished
		if (ModWorldData.get(theWorld).getHasCastleSpwaned())
		{
			// DEBUG
			System.out.println("Castle has already spawned");
			return;
		}

		startX = theTileEntity.getPos().getX()-9; // +parOffsetX;
		startY = theTileEntity.getPos().getY()-3; // +parOffsetY;
		startZ = theTileEntity.getPos().getZ()-12; // +parOffsetZ;
		
		totalVolume = dimX * dimY * dimZ;
		
		if (!finishedGeneratingBasic)
		{
			// DEBUG
			System.out.println("Generating basic blocks");
			generateBasicBlocksTick();
		}
		else if (!finishedGeneratingMeta)
		{
			// DEBUG
			System.out.println("Generating metadata blocks");
			generateMetaBlocksTick();
		}
		else if (!finishedGeneratingSpecial)
		{
			// DEBUG
			System.out.println("Generating special blocks");
			generateSpecialBlocksTick();
		}
		else if (!finishedPopulatingItems)
		{
			// DEBUG
			System.out.println("Populating items");
			populateItems();
		}
		else if (!finishedPopulatingEntities)
		{
			// DEBUG
			System.out.println("Populating Entities");
			populateEntities();
		}
		else
		{
			// DEBUG
			System.out.println("Structure setting MagicBeansWorldData hasCastleBeenSpawned to true");
			ModWorldData.get(theWorld).setHasCastleSpawned(true);
		}
	}
	
	@Override
	public void generateBasicBlocksTick() 
	{
		int indY = ticksGenerating/(dimX*dimZ);

		for (int indX = 0; indX < dimX; indX++)
		{
			for (int indZ = 0; indZ < dimZ; indZ++)
			{
				// DEBUG
				// System.out.println("Generating basic blocks at "+indY+", "+indX+", "+indZ);

				if (blockMetaArray[indX][indY][indZ]==0) // check for basic block
				{
					String blockName = blockNameArray[indX][indY][indZ];
					if (!(blockName.equals("minecraft:tripwire"))) // tripwire/string needs to be placed after other blocks
					{
						BlockPos thePos = new BlockPos(startX+indX, startY+indY, startZ+indZ);
						// perform some block substitutions if interested
						if (blockName.equals("minecraft:dirt") || blockName.equals("minecraft:grass"))
						{
							theWorld.setBlockState(thePos, MagicBeans.blockCloud.getDefaultState());
						}
						else if (!(theWorld.getBlockState(thePos).getBlock() instanceof BlockMagicBeanStalk))
						{
							theWorld.setBlockState(thePos, Block.getBlockFromName(blockName).getDefaultState());
						}
					}
				}
			}
		}
		
		ticksGenerating += dimX * dimZ;
		if (ticksGenerating >= totalVolume)
		{
			// DEBUG
			System.out.println("Finishing generation basic blocks with dimX = "+dimX+" dimY = "+dimY+" dimZ = "+dimZ);
			finishedGeneratingBasic = true;
			ticksGenerating = 0;
		}
	}

	@Override
	public void generateMetaBlocksTick() 
	{
		int indY = ticksGenerating/(dimX*dimZ);

		for (int indX = 0; indX < dimX; indX++)
		{
			for (int indZ = 0; indZ < dimZ; indZ++)
			{
				// DEBUG
				// System.out.println("Generating meta blocks at "+indY+", "+indX+", "+indZ);
	
				if (!(blockMetaArray[indX][indY][indZ]==0))
				{
					Block theBlock = Block.getBlockFromName(blockNameArray[indX][indY][indZ]);
					BlockPos thePos = new BlockPos(startX+indX, startY+indY, startZ+indZ);
					int theMetadata = blockMetaArray[indX][indY][indZ];
					theWorld.setBlockState(thePos, theBlock.getStateFromMeta(theMetadata));
					if (theBlock.hasTileEntity())
					{
						customizeTileEntity(thePos);
					}
				}	
			}
		}
		
		ticksGenerating += dimX * dimZ;
		if (ticksGenerating >= totalVolume)
		{
			// DEBUG
			System.out.println("Finishing generation meta blocks with dimX = "+dimX+" dimY = "+dimY+" dimZ = "+dimZ);
			finishedGeneratingMeta = true;
			ticksGenerating = 0;
		}
	}

	/**
	 * In this method you can do additional processing for a tile entity
	 * such as putting contents into the inventory.
	 */
	@Override
	public void customizeTileEntity(BlockPos parPos) 
	{
		
	}

	@Override
	public void generateSpecialBlocksTick() 
	{
		int indY = ticksGenerating/(dimX*dimZ);

		for (int indX = 0; indX < dimX; indX++)
		{
			for (int indZ = 0; indZ < dimZ; indZ++)
			{
				// DEBUG
				// System.out.println("Generating special blocks at "+indY+", "+indX+", "+indZ);
	
				String blockName = blockNameArray[indX][indY][indZ];
				if (blockName.equals("minecraft:tripwire"))
				{
					BlockPos thePos = new BlockPos(startX+indX, startY+indY, startZ+indZ);

					theWorld.setBlockState(thePos, Block.getBlockFromName(blockName).getDefaultState());
				}	    	
			}
		}
		
		ticksGenerating += dimX * dimZ;
		if (ticksGenerating >= totalVolume)
		{
			// DEBUG
			System.out.println("Finishing generation special blocks with dimX = "+dimX+" dimY = "+dimY+" dimZ = "+dimZ);
			finishedGeneratingSpecial = true;
			ticksGenerating = 0;
		}
	}

	public void generateCloudTick() 
	{
		// DEBUG
		System.out.println("Generating cloud");

		int posX = startX-cloudMarginX+ticksGenerating/(dimZ+2*cloudMarginZ);

		for (int indZ = startZ-cloudMarginZ; indZ < startZ+dimZ+cloudMarginZ; indZ++)
		{
			// DEBUG
			// System.out.println("Generating cloud blocks at "+parX+", "+parY+", "+indZ);
			// let the beanstalk go through the clouds
			if (!((Math.abs(posX-theTileEntity.getPos().getX())<2)&&(Math.abs(indZ-theTileEntity.getPos().getZ())<2)))
			{
				BlockPos thePos = new BlockPos(posX, startY+1, indZ);

				theWorld.setBlockState(thePos, MagicBeans.blockCloud.getDefaultState());
			}
		}
		ticksGenerating += dimZ+2*cloudMarginZ;
		if (ticksGenerating >= (dimX+2*cloudMarginX) * (dimZ+2*cloudMarginZ))
		{
			finishedGeneratingCloud = true;
			ticksGenerating = 0;
		}
	}

	@Override
	public void populateItems()
	{
        // DEBUG
        System.out.println("Finished populating items in structure.");
		finishedPopulatingItems = true;
	}
	
	@Override
	public void populateEntities()
	{
		finishedPopulatingEntities = true;
	}

	
	public void generateCloud(World parWorld, int parX, int parY, int parZ, int parCloudSize) 
	{	
		// DEBUG
		System.out.println("Generating cloud");
		
		if (parWorld.isRemote)
		{
			return;
		}

		for (int indX = parX-parCloudSize/2; indX < parX+parCloudSize/2; indX++)
		{
			for (int indZ = parZ-parCloudSize/2; indZ < parZ+parCloudSize/2; indZ++)
			{
				BlockPos thePos = new BlockPos(indX, parY-1, indZ);
				
				// parWorld.setBlockToAir(thePos);
				parWorld.setBlockState(thePos, MagicBeans.blockCloud.getDefaultState());
			}
		}
	}

	public void generate(Entity parEntity, int parOffsetX, int parOffsetY, int parOffsetZ) 
	{
		Entity theEntity = parEntity;
		theWorld = theEntity.worldObj;
		if (theWorld.isRemote)
		{
			return;
		}

		startX = (int) theEntity.posX;
		startY = (int) theEntity.posY;
		startZ = (int) theEntity.posZ;
	    
	    for (int indY = 0; indY < dimY; indY++) // Y first to organize in vertical layers
	    {
	    	for (int indX = 0; indX < dimX; indX++)
	    	{
	    		for (int indZ = 0; indZ < dimZ; indZ++)
	    		{
	    			if (blockMetaArray[indX][indY][indZ]==0)
	    			{
	    				String blockName = blockNameArray[indX][indY][indZ];
	    				if (!(blockName.equals("minecraft:tripwire"))) // tripwire/string needs to be placed after other blocks
	    				{
	    					BlockPos thePos = new BlockPos(startX+parOffsetX+indX, startY+parOffsetY+indY, startZ+parOffsetZ+indZ);
							theWorld.setBlockState(thePos, Block.getBlockFromName(blockName).getDefaultState());
	    				}
	    			}	    			
	    		}
	    	}
	    }
	    // best to place metadata blocks after non-metadata blocks as they need to attach, etc.
	    for (int indY = 0; indY < dimY; indY++) // Y first to organize in vertical layers
	    {
	    	for (int indX = 0; indX < dimX; indX++)
	    	{
	    		for (int indZ = 0; indZ < dimZ; indZ++)
	    		{
	    			if (!(blockMetaArray[indX][indY][indZ]==0))
	    			{
    					BlockPos thePos = new BlockPos(startX+parOffsetX+indX, startY+parOffsetY+indY, startZ+parOffsetZ+indZ);
						theWorld.setBlockState(thePos, Block.getBlockFromName(blockNameArray[indX][indY][indZ])
								.getStateFromMeta(blockMetaArray[indX][indY][indZ]));
	    			}	    			
	    		}
	    	}
	    }
	    // some blocks with 0 metadata, like string/tripwire, require other blocks to be placed already, so do them again as last pass.
	    for (int indY = 0; indY < dimY; indY++) // Y first to organize in vertical layers
	    {
	    	for (int indX = 0; indX < dimX; indX++)
	    	{
	    		for (int indZ = 0; indZ < dimZ; indZ++)
	    		{
    				String blockName = blockNameArray[indX][indY][indZ];
    				if (blockName.equals("minecraft:tripwire"))
    				{
    					BlockPos thePos = new BlockPos(startX+parOffsetX+indX, startY+parOffsetY+indY, startZ+parOffsetZ+indZ);
						theWorld.setBlockState(thePos, Block.getBlockFromName(blockName).getDefaultState());
    				}	    			
	    		}
	    	}
	    }		
	}
	
	/**
	 * @param tileEntityMagicBeanStalk
	 * @param parOffsetX
	 * @param parOffsetY
	 * @param parOffsetZ
	 */
	public void generate(TileEntity parEntity, int parOffsetX, int parOffsetY, int parOffsetZ) 
	{
		TileEntity theEntity = parEntity;
		theWorld = theEntity.getWorld();

		// DEBUG
		System.out.println("Generating castle in the clouds. IsRemote = "+theWorld.isRemote);

		if (theWorld.isRemote)
		{
			return;
		}

		startX = theEntity.getPos().getX()+parOffsetX;
		startY = theEntity.getPos().getY()+parOffsetY;
		startZ = theEntity.getPos().getZ()+parOffsetZ;
		
		// generate the cloud
		generateCloud(theWorld, startX, startY, startZ, 75);
	    
	    for (int indY = 0; indY < dimY; indY++) // Y first to organize in vertical layers
	    {
	    	for (int indX = 0; indX < dimX; indX++)
	    	{
	    		for (int indZ = 0; indZ < dimZ; indZ++)
	    		{
	    			if (blockMetaArray[indX][indY][indZ]==0)
	    			{
	    				String blockName = blockNameArray[indX][indY][indZ];
	    				if (!(blockName.equals("minecraft:tripwire"))) // tripwire/string needs to be placed after other blocks
	    				{
	    					BlockPos thePos = new BlockPos(startX+indX, startY+indY, startZ+indZ);
							theWorld.setBlockState(thePos, Block.getBlockFromName(blockName).getDefaultState());
	    				}
	    			}	    			
	    		}
	    	}
	    }
	    // best to place metadata blocks after non-metadata blocks as they need to attach, etc.
	    for (int indY = 0; indY < dimY; indY++) // Y first to organize in vertical layers
	    {
	    	for (int indX = 0; indX < dimX; indX++)
	    	{
	    		for (int indZ = 0; indZ < dimZ; indZ++)
	    		{
	    			if (!(blockMetaArray[indX][indY][indZ]==0))
	    			{
    					BlockPos thePos = new BlockPos(startX+indX, startY+indY, startZ+indZ);
						theWorld.setBlockState(thePos, Block.getBlockFromName(blockNameArray[indX][indY][indZ])
								.getStateFromMeta(blockMetaArray[indX][indY][indZ]));
	    			}	    			
	    		}
	    	}
	    }
	    // some blocks with 0 metadata, like string/tripwire, require other blocks to be placed already, so do them again as last pass.
	    for (int indY = 0; indY < dimY; indY++) // Y first to organize in vertical layers
	    {
	    	for (int indX = 0; indX < dimX; indX++)
	    	{
	    		for (int indZ = 0; indZ < dimZ; indZ++)
	    		{
    				String blockName = blockNameArray[indX][indY][indZ];
    				if (blockName.equals("minecraft:tripwire"))
    				{
    					BlockPos thePos = new BlockPos(startX+indX, startY+indY, startZ+indZ);
						theWorld.setBlockState(thePos, Block.getBlockFromName(blockName).getDefaultState());
    				}	    			
	    		}
	    	}
	    }		
	}


}
