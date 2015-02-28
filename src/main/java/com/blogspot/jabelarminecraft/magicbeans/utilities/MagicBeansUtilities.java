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

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.networking.MessageSyncEntityToClient;
import com.blogspot.jabelarminecraft.magicbeans.networking.MessageSyncEntityToServer;

/**
 * @author jabelar
 *
 */
public class MagicBeansUtilities 
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
		EnumChatFormatting[] colorChar = 
			{
			EnumChatFormatting.RED,
			EnumChatFormatting.GOLD,
			EnumChatFormatting.YELLOW,
			EnumChatFormatting.GREEN,
			EnumChatFormatting.AQUA,
			EnumChatFormatting.BLUE,
			EnumChatFormatting.LIGHT_PURPLE,
			EnumChatFormatting.DARK_PURPLE
			};
		for (int i = 0; i < stringLength; i++)
		{
			outputString = outputString+colorChar[i%8]+parString.substring(i, i+1);
		}
		// return color to a common one after (most chat is white, but for other GUI might want black)
		if (parReturnToBlack)
		{
			return outputString+EnumChatFormatting.BLACK;
		}
		return outputString+EnumChatFormatting.WHITE;
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
				outputString = outputString+EnumChatFormatting.WHITE+parString.substring(i, i+1);				
			}
			else if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==1)
			{
				outputString = outputString+EnumChatFormatting.YELLOW+parString.substring(i, i+1);				
			}
			else if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==87)
			{
				outputString = outputString+EnumChatFormatting.YELLOW+parString.substring(i, i+1);				
			}
			else
			{
				outputString = outputString+EnumChatFormatting.GOLD+parString.substring(i, i+1);								
			}
		}
		// return color to a common one after (most chat is white, but for other GUI might want black)
		if (parReturnToBlack)
		{
			return outputString+EnumChatFormatting.BLACK;
		}
		return outputString+EnumChatFormatting.WHITE;
	}

	// by default return to white (for chat formatting).
	public static String stringToGolden(String parString, int parShineLocation)
	{
		return stringToGolden(parString, parShineLocation, false);
	}

	public static Entity getEntityByID(int entityID, World world)        
	{         
	    for(Object o: world.getLoadedEntityList())                
	    {                        
	        if(((Entity)o).getEntityId() == entityID)                        
	        {   
	        	// DEBUG
	            // System.out.println("Found the entity");                                
	            return ((Entity)o);                        
	        }                
	    }                
	    return null;        
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
	
    public static void sendEntitySyncPacketToClient(IEntityMagicBeans parEntity) 
    {
    	Entity theEntity = (Entity)parEntity;
        if (!theEntity.worldObj.isRemote)
        {
        	// DEBUG
        	System.out.println("sendEntitySyncPacket from server for entity ID ="+theEntity.getEntityId());
            MagicBeans.network.sendToAll(new MessageSyncEntityToClient(theEntity.getEntityId(), parEntity.getSyncDataCompound()));           
        }
    }

    public static void sendEntitySyncPacketToServer(IEntityMagicBeans parEntity) 
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
    			.getHeight(intX & 15, intZ & 15);
    	
    	return height;
    }
}

