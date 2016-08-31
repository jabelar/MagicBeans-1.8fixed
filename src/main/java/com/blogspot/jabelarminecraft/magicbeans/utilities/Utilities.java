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

package com.blogspot.jabelarminecraft.magicbeans.utilities;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.entities.IEntity;
import com.blogspot.jabelarminecraft.magicbeans.networking.MessageSyncEntityToClient;
import com.blogspot.jabelarminecraft.magicbeans.networking.MessageSyncEntityToServer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.BlockSnapshot;

/**
 * @author jabelar
 *
 */
public class Utilities 
{
	/*
	 * Text Utilities
	 */
	
	public static String stringToRainbow(String parString, boolean parReturnToBlack)
	{
		int stringLength = parString.length();
		if (stringLength < 1)
		{
			return "";
		}
		String outputString = "";
		TextFormatting[] colorChar = 
			{
			TextFormatting.RED,
			TextFormatting.GOLD,
			TextFormatting.YELLOW,
			TextFormatting.GREEN,
			TextFormatting.AQUA,
			TextFormatting.BLUE,
			TextFormatting.LIGHT_PURPLE,
			TextFormatting.DARK_PURPLE
			};
		for (int i = 0; i < stringLength; i++)
		{
			outputString = outputString+colorChar[i%8]+parString.substring(i, i+1);
		}
		// return color to a common one after (most chat is white, but for other GUI might want black)
		if (parReturnToBlack)
		{
			return outputString+TextFormatting.BLACK;
		}
		return outputString+TextFormatting.WHITE;
	}

	// by default return to white (for chat formatting).
	public static String stringToRainbow(String parString)
	{
		return stringToRainbow(parString, false);
	}
	
	public static String stringToGolden(String parString, int parShineLocation, boolean parReturnToBlack)
	{
		int stringLength = parString.length();
		if (stringLength < 1)
		{
			return "";
		}
		String outputString = "";
		for (int i = 0; i < stringLength; i++)
		{
			if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==0)
			{
				outputString = outputString+TextFormatting.WHITE+parString.substring(i, i+1);				
			}
			else if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==1)
			{
				outputString = outputString+TextFormatting.YELLOW+parString.substring(i, i+1);				
			}
			else if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==87)
			{
				outputString = outputString+TextFormatting.YELLOW+parString.substring(i, i+1);				
			}
			else
			{
				outputString = outputString+TextFormatting.GOLD+parString.substring(i, i+1);								
			}
		}
		// return color to a common one after (most chat is white, but for other GUI might want black)
		if (parReturnToBlack)
		{
			return outputString+TextFormatting.BLACK;
		}
		return outputString+TextFormatting.WHITE;
	}

	// by default return to white (for chat formatting).
	public static String stringToGolden(String parString, int parShineLocation)
	{
		return stringToGolden(parString, parShineLocation, false);
	}
	
	/**
	* Based on code from http://pages.cs.wisc.edu/~ltorrey/cs302/examples/PigLatinTranslator.java
	* Method to translate a sentence word by word.
	* @param s The sentence in English
	* @return The pig latin version
	*/
	public static String toPigLatin(String s) 
	{
		String latin = "";
	    int i = 0;
	    while (i<s.length()) 
	    {
	    	// Take care of punctuation and spaces
	    	while (i<s.length() && !isLetter(s.charAt(i))) 
	    	{
	    		latin = latin + s.charAt(i);
	    		i++;
	    	}

	    	// If there aren't any words left, stop.
	    	if (i>=s.length()) break;

	    	// Otherwise we're at the beginning of a word.
	    	int begin = i;
	    	while (i<s.length() && isLetter(s.charAt(i))) 
	    	{
	    		i++;
	    	}

	    	// Now we're at the end of a word, so translate it.
	    	int end = i;
	    	latin = latin + pigWord(s.substring(begin, end));
	    }
	    return latin;
	}

	/**
	* Method to test whether a character is a letter or not.
	* @param c The character to test
	* @return True if it's a letter
	*/
	private static boolean isLetter(char c) 
	{
		return ( (c >='A' && c <='Z') || (c >='a' && c <='z') );
	}

	/**
	* Method to translate one word into pig latin.
	* @param word The word in english
	* @return The pig latin version
	*/
	private static String pigWord(String word) 
	{
		int split = firstVowel(word);
		return word.substring(split)+"-"+word.substring(0, split)+"ay";
	}

	/**
	* Method to find the index of the first vowel in a word.
	* @param word The word to search
	* @return The index of the first vowel
	*/
	private static int firstVowel(String word) 
	{
		word = word.toLowerCase();
	    for (int i=0; i<word.length(); i++)
	    {
	    	if (word.charAt(i)=='a' || word.charAt(i)=='e' ||
	    	      word.charAt(i)=='i' || word.charAt(i)=='o' ||
	              word.charAt(i)=='u')
	    	{
	    		return i;
	    	}
	    }
	    	return 0;
	}
	  
	/*
	 * Networking packet utilities
	 */
	
    public static void sendEntitySyncPacketToClient(IEntity parEntity) 
    {
    	Entity theEntity = (Entity)parEntity;
        if (!theEntity.worldObj.isRemote)
        {
        	// DEBUG
        	System.out.println("sendEntitySyncPacket from server for entity ID ="+theEntity.getEntityId());
            MagicBeans.network.sendToAll(new MessageSyncEntityToClient(theEntity.getEntityId(), parEntity.getSyncDataCompound()));           
        }
    }

    public static void sendEntitySyncPacketToServer(IEntity parEntity) 
    {
    	Entity theEntity = (Entity)parEntity;
        if (theEntity.worldObj.isRemote)
        {
        	// DEBUG
        	System.out.println("sendEntitySyncPacket from client");
            MagicBeans.network.sendToServer(new MessageSyncEntityToServer(theEntity.getEntityId(), parEntity.getSyncDataCompound()));           
        }
    }
    
    /*
     * World utilities
     */
    
    /**
     * Finds the topmost block position at an X, Z position in the world
     * @param parWorld
     * @param parX
     * @param parZ
     * @return
     */
    public static double getHeightValue(World parWorld, double parX, double parZ)
    {
        int intX = MathHelper.floor_double(parX);
        int intZ = MathHelper.floor_double(parZ);

    	int chunkX = intX >> 4;
    	int chunkZ = intZ >> 4;
    	double height = parWorld.getChunkFromChunkCoords(chunkX, chunkZ)
    			.getHeightValue(intX & 15, intZ & 15);
    	
    	return height;
    }
    
    /**
     * Sets the block state at a given location. Flag 1 will cause a block update. Flag 2 will send the change to
     * clients (you almost always want parWorld). Flag 4 prevents the block from being re-rendered, if parWorld is a client
     * world. Flags can be added together.
     *  
     * @param parFlags Flag 1 will cause a block update. Flag 2 will send the change to clients (you almost always want
     * parWorld). Flag 4 prevents the block from being re-rendered, if parWorld is a client world. Flags can be added together.
     */
    public static boolean setBlockStateFast(World parWorld, BlockPos parBlockPos, IBlockState parIBlockState, int parFlags)
    {
        if (!(parBlockPos.getX() >= -30000000 && parBlockPos.getZ() >= -30000000 && parBlockPos.getX() < 30000000 && parBlockPos.getZ() < 30000000 && parBlockPos.getY() >= 0 && parBlockPos.getY() < 256))
        {
            return false;
        }
        else if (!parWorld.isRemote && parWorld.getWorldInfo().getTerrainType() == WorldType.DEBUG_WORLD)
        {
            return false;
        }
        else
        {
            Chunk theChunk = parWorld.getChunkFromBlockCoords(parBlockPos);
//            Block newBlock = parIBlockState.getBlock();

            BlockSnapshot blockSnapshot = null;
            if (parWorld.captureBlockSnapshots && !parWorld.isRemote)
            {
                blockSnapshot = BlockSnapshot.getBlockSnapshot(parWorld, parBlockPos, parFlags);
                parWorld.capturedBlockSnapshots.add(blockSnapshot);
            }

            IBlockState theIBlockState = setBlockStateInChunkFast(theChunk, parBlockPos, parIBlockState);

            if (theIBlockState == null)
            {
                if (blockSnapshot != null) parWorld.capturedBlockSnapshots.remove(blockSnapshot);
                return false;
            }
            else
            {
//                Block block1 = theIBlockState.getBlock();
//
//                if (newBlock.getLightOpacity() != block1.getLightOpacity() || newBlock.getLightValue() != block1.getLightValue())
//                {
//                    parWorld.theProfiler.startSection("checkLight");
//                    parWorld.checkLight(parBlockPos);
//                    parWorld.theProfiler.endSection();
//                }

                if (blockSnapshot == null) // Don't notify clients or update physics while capturing blockstates
                {
                    parWorld.markAndNotifyBlock(parBlockPos, theChunk, theIBlockState, parIBlockState, parFlags); // Modularize client and physic updates
                }

                return true;
            }
        }
    }
    
    public static IBlockState setBlockStateInChunkFast(Chunk parChunk, BlockPos parBlockPos, IBlockState parIBlockState)
    {
        int chunkX = parBlockPos.getX() & 15;
        int chunkY = parBlockPos.getY();
        int chunkZ = parBlockPos.getZ() & 15;
//        int mapKey = chunkZ << 4 | chunkX;

        if (chunkY >= parChunk.getPrecipitationHeight(parBlockPos).getY())
        {
//            // There is no setter for precipitationHeightMap so will need to use reflection 
//            parChunk.precipitationHeightMap[mapKey] = -999;
        }

//        int currentHeight = parChunk.getHeightMap()[mapKey];
        IBlockState existingIBlockState = parChunk.getBlockState(parBlockPos);

        if (existingIBlockState == parIBlockState)
        {
            return null;
        }
        else
        {
            Block newBlock = parIBlockState.getBlock();
            Block existingBlock = existingIBlockState.getBlock();
            ExtendedBlockStorage extendedblockstorage = parChunk.getBlockStorageArray()[chunkY >> 4];
//            boolean isHigherThanCurrentHeight = false;

            if (extendedblockstorage == null)
            {
                if (newBlock == Blocks.AIR)
                {
                    return null;
                }

                extendedblockstorage = parChunk.getBlockStorageArray()[chunkY >> 4] = new ExtendedBlockStorage(chunkY >> 4 << 4, !parChunk.getWorld().provider.getHasNoSky());
//                isHigherThanCurrentHeight = chunkY >= currentHeight;
            }

//            int newLightOpacity = newBlock.getLightOpacity(parChunk.getWorld(), parBlockPos);

            extendedblockstorage.set(chunkX, chunkY & 15, chunkZ, parIBlockState);

            if (!parChunk.getWorld().isRemote)
            {
                if (existingIBlockState.getBlock() != parIBlockState.getBlock()) //Only fire block breaks when the block changes.
                existingBlock.breakBlock(parChunk.getWorld(), parBlockPos, existingIBlockState);
                TileEntity te = parChunk.getTileEntity(parBlockPos, Chunk.EnumCreateEntityType.CHECK);
                if (te != null && te.shouldRefresh(parChunk.getWorld(), parBlockPos, existingIBlockState, parIBlockState)) parChunk.getWorld().removeTileEntity(parBlockPos);
            }
            else if (existingBlock.hasTileEntity(existingIBlockState))
            {
                TileEntity te = parChunk.getTileEntity(parBlockPos, Chunk.EnumCreateEntityType.CHECK);
                if (te != null && te.shouldRefresh(parChunk.getWorld(), parBlockPos, existingIBlockState, parIBlockState))
                parChunk.getWorld().removeTileEntity(parBlockPos);
            }   

            if (extendedblockstorage.get(chunkX, chunkY & 15, chunkZ) != newBlock)
            {
                return null;
            }
            else
            {
//                if (isHigherThanCurrentHeight)
//                {
//                    parChunk.generateSkylightMap();
//                }
//                else
//                {
//                    int existingLightOpacity = newBlock.getLightOpacity(parChunk.getWorld(), parBlockPos);
//
//                    if (newLightOpacity > 0)
//                    {
//                        if (chunkY >= currentHeight)
//                        {
//                            parChunk.relightBlock(chunkX, chunkY + 1, chunkZ);
//                        }
//                    }
//                    else if (chunkY == currentHeight - 1)
//                    {
//                        parChunk.relightBlock(chunkX, chunkY, chunkZ);
//                    }
//
//                    if (newLightOpacity != existingLightOpacity && (newLightOpacity < existingLightOpacity || parChunk.getLightFor(EnumSkyBlock.SKY, parBlockPos) > 0 || parChunk.getLightFor(EnumSkyBlock.BLOCK, parBlockPos) > 0))
//                    {
//                        parChunk.propagateSkylightOcclusion(chunkX, chunkZ);
//                    }
//                }

                TileEntity tileentity;

                if (!parChunk.getWorld().isRemote && existingBlock != newBlock)
                {
                    newBlock.onBlockAdded(parChunk.getWorld(), parBlockPos, parIBlockState);
                }

                if (newBlock.hasTileEntity(parIBlockState))
                {
                    tileentity = parChunk.getTileEntity(parBlockPos, Chunk.EnumCreateEntityType.CHECK);

                    if (tileentity == null)
                    {
                        tileentity = newBlock.createTileEntity(parChunk.getWorld(), parIBlockState);
                        parChunk.getWorld().setTileEntity(parBlockPos, tileentity);
                    }

                    if (tileentity != null)
                    {
                        tileentity.updateContainingBlockInfo();
                    }
                }

                parChunk.setModified(true);
                return existingIBlockState;
            }
        }
    }
}

