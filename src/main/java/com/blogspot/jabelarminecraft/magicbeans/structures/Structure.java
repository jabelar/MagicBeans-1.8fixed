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

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.ModWorldData;
import com.blogspot.jabelarminecraft.magicbeans.blocks.BlockMagicBeanStalk;
import com.blogspot.jabelarminecraft.magicbeans.utilities.Utilities;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class Structure implements IStructure
{
    protected String theStructureName;
    
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
    public boolean finishedPopulatingTileEntities = false; // for putting stuff into tile entity containers
    public boolean finishedPopulatingItems = false; // items into inventories and such
    public boolean finishedPopulatingEntities = false; // default entities that inhabit structure
    protected int ticksGenerating = 0;

    String[][][] blockNameArray = null;
    int[][][] blockMetaArray = null;

    BufferedReader readIn;
    
    StructureSparseArrayElement[] theSparseArrayBasic = new StructureSparseArrayElement[64 * 64 * 64];
    StructureSparseArrayElement[] theSparseArrayMeta = new StructureSparseArrayElement[64 * 64 * 64];
    StructureSparseArrayElement[] theSparseArraySpecial = new StructureSparseArrayElement[64 * 64 * 64];
    StructureSparseArrayElement[] theSparseArrayTileEntities = new StructureSparseArrayElement[64 * 64 * 64];
    
    int numSparseElementsBasic = 0;
    int numSparseElementsMeta = 0;
    int numSparseElementsSpecial = 0;
    int numSparseElementsTileEntities = 0;

    public Structure(String parName)
    {
        theStructureName = parName;
        // Remember to put following in the init handling of common proxy *after* the blocks are registered
        //  readArrays(theStructureName);
        //  makeSparseArray();
    }
    
    @Override
    public String getName()
    {
        return theStructureName;
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
    public void makeSparseArrays()
    {
        Block theBlock = null;
        // DEBUG
        System.out.println("Starting to make sparse array for basic blocks");
        for (int indY=0; indY < dimY; indY++)
        {
            for (int indX=0; indX < dimX; indX++)
            {
                for (int indZ=0; indZ < dimZ; indZ++) 
                {
                    if (!blockNameArray[indX][indY][indZ].equals("minecraft:air"))
                    {
                        theBlock = Block.getBlockFromName(blockNameArray[indX][indY][indZ]);
                        if (theBlock == null) System.out.println("Block unexpectedly null at "+indX+", "+indY+", "+indZ);
                        if (blockMetaArray[indX][indY][indZ] == 0 
                                && theBlock != Blocks.TRIPWIRE)
                        {
                            theSparseArrayBasic[numSparseElementsBasic] = 
                                    new StructureSparseArrayElement(
                                          theBlock,
                                          0,
                                          indX,
                                          indY,
                                          indZ
                                          );
                            numSparseElementsBasic++;
                        }
                        else if (blockMetaArray[indX][indY][indZ] > 0)
                        {
                            theSparseArrayMeta[numSparseElementsMeta] = 
                                    new StructureSparseArrayElement(
                                          theBlock,
                                          blockMetaArray[indX][indY][indZ],
                                          indX,
                                          indY,
                                          indZ
                                          );
                            numSparseElementsMeta++;
                        }
                        else // must be trip wire
                        {
                            theSparseArraySpecial[numSparseElementsSpecial] = 
                                    new StructureSparseArrayElement(
                                          theBlock,
                                          blockMetaArray[indX][indY][indZ],
                                          indX,
                                          indY,
                                          indZ
                                          );
                            numSparseElementsSpecial++;
                        }
                        
                        if (theBlock.hasTileEntity())
                        {
                            theSparseArrayTileEntities[numSparseElementsTileEntities] = 
                                    new StructureSparseArrayElement(
                                          theBlock,
                                          blockMetaArray[indX][indY][indZ],
                                          indX,
                                          indY,
                                          indZ
                                          );
                            numSparseElementsTileEntities++;
                        }
                    }
                }
            }
        }
        // DEBUG
        System.out.println("Finished making sparse array for basic blocks, with number of elements = "+numSparseElementsBasic);
        System.out.println("Finished making sparse array for meta blocks, with number of elements = "+numSparseElementsMeta);
        System.out.println("Finished making sparse array for special blocks, with number of elements = "+numSparseElementsSpecial);
        System.out.println("Finished making sparse array for tile entities, with number of elements = "+numSparseElementsTileEntities);
    }
    
    @Override
    public void generateSparse(TileEntity parEntity, int parOffsetX, int parOffsetY, int parOffsetZ) 
    {
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
        if (ModWorldData.get(theWorld).getHasCastleSpawned())
        {
            // DEBUG
            System.out.println("Castle has already spawned");
            return;
        }

        // DEBUG
        System.out.println("Starting to generate with sparse array");
        
        startX = theTileEntity.getPos().getX()-9; // +parOffsetX;
        startY = theTileEntity.getPos().getY()-3; // +parOffsetY;
        startZ = theTileEntity.getPos().getZ()-12; // +parOffsetZ;

        totalVolume = dimX * dimY * dimZ;
        
        // DEBUG
        System.out.println("Starting to generate basic blocks");
        long startTime = System.currentTimeMillis();
        Block theBlock = null;
        BlockPos theBlockPos = null;
        StructureSparseArrayElement theElement = null;
        for (int index = 0; index < numSparseElementsBasic; index++)
        {
            theElement = theSparseArrayBasic[index];
            theBlock = theElement.theBlock;
            theBlockPos = new BlockPos(startX+theElement.posX, startY+theElement.posY, startZ+theElement.posZ);
            // need to set the occasional block with normal method to ensure lighting updates
            if (index % 100 == 0) // every 500 blocks
            {
                theWorld.setBlockState(theBlockPos, theBlock.getStateFromMeta(0), 3);
            }
            else
            {
                Utilities.setBlockStateFast(theWorld, theBlockPos, theBlock.getStateFromMeta(0), 3);
            }
//            if (theBlock.hasTileEntity())
//            {
//                customizeTileEntity(theBlockPos);
//            }
        }

        // DEBUG
        System.out.println("Starting to generate meta blocks");
        int theMetaData = 0;
        for (int index = 0; index < numSparseElementsMeta; index++)
        {
            theElement = theSparseArrayMeta[index];
            theBlock = theElement.theBlock;
            theMetaData = theElement.theMetaData;
            theBlockPos = new BlockPos(startX+theElement.posX, startY+theElement.posY, startZ+theElement.posZ);
            if (index % 100 == 0) // every 500 blocks
            {
                theWorld.setBlockState(theBlockPos, theBlock.getStateFromMeta(theMetaData), 3);
            }
            else
            {
                 Utilities.setBlockStateFast(theWorld, theBlockPos, theBlock.getStateFromMeta(theMetaData), 3);
            }
//            if (theBlock.hasTileEntity())
//            {
//                customizeTileEntity(theBlockPos);
//            }
        }

        // DEBUG
        System.out.println("Starting to generate special blocks");
        for (int index = 0; index < numSparseElementsSpecial; index++)
        {
            theElement = theSparseArraySpecial[index];
            theBlock = theElement.theBlock;
            theMetaData = theElement.theMetaData;
            theBlockPos = new BlockPos(startX+theElement.posX, startY+theElement.posY, startZ+theElement.posZ);
            // DEBUG
            System.out.println("Placing tripwire with meta data = "+theMetaData+"at position "+(startX+theElement.posX)+", "+(startY+theElement.posY)+", "+(startZ+theElement.posZ));
//            if (index % 100 == 0) // every 500 blocks
//            {
//                theWorld.setBlockState(theBlockPos, theBlock.getStateFromMeta(theMetaData), 3);
//            }
//            else
//            {
//                Utilities.setBlockStateFast(theWorld, theBlockPos, theBlock.getStateFromMeta(theMetaData), 3);
//            }
            if (index % 100 == 0) // every 500 blocks
            {
                theWorld.setBlockState(theBlockPos, theBlock.getDefaultState(), 3);
            }
            else
            {
                Utilities.setBlockStateFast(theWorld, theBlockPos, theBlock.getDefaultState(), 3);
            }
//            if (theBlock.hasTileEntity())
//            {
//                customizeTileEntity(theBlockPos);
//            }
        }
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time to loop 11k blocks = "+totalTime+" milliseconds");
        
        // DEBUG
        System.out.println("Populating tile entities");
        populateTileEntities();

        // DEBUG
        System.out.println("Populating items");
        populateItems();

        // DEBUG
        System.out.println("Populating Entities");
        populateEntities();

        // DEBUG
        System.out.println("Structure setting MagicBeansWorldData hasCastleBeenSpawned to true");
        ModWorldData.get(theWorld).setHasCastleSpawned(true);
        theWorld.getClosestPlayer(startX, startY, startZ, -1, false).addChatMessage(new TextComponentString(Utilities.stringToRainbow("Look up! Something happened at the top of the bean stalk.")));
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
        if (ModWorldData.get(theWorld).getHasCastleSpawned())
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
                            Utilities.setBlockStateFast(theWorld, thePos, Block.getBlockFromName(blockName).getDefaultState(), 3);
//                        	theWorld.setBlockState(thePos, Block.getBlockFromName(blockName).getDefaultState());
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
                    Utilities.setBlockStateFast(theWorld, thePos, theBlock.getStateFromMeta(theMetadata), 3);
//                    theWorld.setBlockState(thePos, theBlock.getStateFromMeta(theMetadata));
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

                    Utilities.setBlockStateFast(theWorld, thePos, Block.getBlockFromName(blockName).getDefaultState(), 3);
//                    theWorld.setBlockState(thePos, Block.getBlockFromName(blockName).getDefaultState());
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
    
    @Override
    public void populateTileEntities()
    {
        StructureSparseArrayElement theElement = null;
        BlockPos theBlockPos = null;
        for (int index = 0; index < numSparseElementsTileEntities; index++)
        {
            theElement = theSparseArrayTileEntities[index];
            theBlockPos = new BlockPos(startX+theElement.posX, startY+theElement.posY, startZ+theElement.posZ);
            customizeTileEntity(theBlockPos);
        }
        
        finishedPopulatingTileEntities = true;
    }

    @Override
    public void populateItems()
    {
         finishedPopulatingItems = true;
    }
    
    @Override
    public void populateEntities()
    {
        finishedPopulatingEntities = true;
    }

    public void generate(TileEntity parEntity, int parOffsetX, int parOffsetY, int parOffsetZ) 
    {
        TileEntity theEntity = parEntity;
        theWorld = theEntity.getWorld();
        if (theWorld.isRemote)
        {
            return;
        }

        startX = theEntity.getPos().getX();
        startY = theEntity.getPos().getY();
        startZ = theEntity.getPos().getZ();
        
        BlockPos thePos = null;
        Block theBlock = null;
        
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
                            thePos = new BlockPos(startX+parOffsetX+indX, startY+parOffsetY+indY, startZ+parOffsetZ+indZ);
                            theBlock = Block.getBlockFromName(blockName);
                            theWorld.setBlockState(thePos, theBlock.getDefaultState());
                            if (theBlock.hasTileEntity())
                            {
                                customizeTileEntity(thePos);
                            }
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
                        thePos = new BlockPos(startX+parOffsetX+indX, startY+parOffsetY+indY, startZ+parOffsetZ+indZ);
                        theBlock = Block.getBlockFromName(blockNameArray[indX][indY][indZ]);
                        theWorld.setBlockState(thePos, theBlock.getStateFromMeta(blockMetaArray[indX][indY][indZ]));
                        if (theBlock.hasTileEntity())
                        {
                            customizeTileEntity(thePos);
                        }
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
                        thePos = new BlockPos(startX+parOffsetX+indX, startY+parOffsetY+indY, startZ+parOffsetZ+indZ);
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
    public void generate(TileEntity parEntity, int parOffsetX, int parOffsetY, int parOffsetZ, boolean parSparse) 
    {
        if (parSparse)
        {
            generateSparse(parEntity, parOffsetZ, parOffsetZ, parOffsetZ);
        }
        else
        {
            generate(parEntity, parOffsetZ, parOffsetZ, parOffsetZ);
        }
    }
}
