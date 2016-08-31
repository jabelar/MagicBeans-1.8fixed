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

package com.blogspot.jabelarminecraft.magicbeans.commands;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.structures.Structure;
import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandStructure implements ICommand
{
	private final List aliases;
	World theWorld;
	Entity thePlayer;
	
	int dimX;
	int dimY;
	int dimZ;

	String[][][] blockNameArray = null;
	int[][][] blockMetaArray = null;

	BufferedReader readIn;
	
	// TODO
	// ultimately need to pass structures by name to make more generic
	
	public CommandStructure()
	{
		    aliases = new ArrayList();
		    aliases.add("structure");
		    aliases.add("struct");
	}
	
	@Override
	public int compareTo(ICommand parICommand) 
	{
		return 0;
	}

	@Override
	public String getCommandName() 
	{
		return "structure";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) 
	{
		return "structure <int> <int> <int>"; // use the ints if offset required
	}

	@Override
	public List getCommandAliases() 
	{
		return this.aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] argString) 
	{
		theWorld = sender.getEntityWorld();
	    thePlayer = sender.getCommandSenderEntity();

		
		if (theWorld.isRemote)
		{
			System.out.println("Not processing on Client side");
		}
		else
		{
			System.out.println("Processing on Server side");

			if(argString.length==0)
			{
				Structure theStructure = MagicBeans.structureCastle;
//				theStructure.generate(thePlayer, 0, -2, 0);
			}
			if(argString.length == 1)
			{
			    sender.addChatMessage(new TextComponentString("Generating Structure"));

			    readArrays(argString[0]);
			    regenerate(0, 0, 0);
			}
			if (argString.length == 4) // offsets provided
			{
			    readArrays(argString[0]);
				regenerate(Integer.valueOf(argString[1]), Integer.valueOf(argString[2]), Integer.valueOf(argString[3]));
			}
		}
	}
	
	protected void readArrays(String parName)
	{
	    try 
	    {
	    	System.out.println("trying to read file = "+parName+".txt");
			readIn = new BufferedReader(new FileReader(parName+".txt"));
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

	protected void regenerate(int parOffsetX, int parOffsetY, int parOffsetZ) 
	{
		BlockPos pos = thePlayer.getPosition();
		
		int startX = pos.getX();
		int startY = pos.getY();
		int startZ = pos.getZ();
			 
		// first process blocks with default metadata
	    for (int indY = 0; indY < dimY; indY++) // Y first to organize in vertical layers
	    {
	    	for (int indX = 0; indX < dimX; indX++)
	    	{
	    		for (int indZ = 0; indZ < dimZ; indZ++)
	    		{
	    			int metaData = blockMetaArray[indX][indY][indZ];
	    			if (metaData==0)
	    			{
	    				String blockName = blockNameArray[indX][indY][indZ];
	    				if (!(blockName.equals("minecraft:tripwire"))) // tripwire/string needs to be placed after other blocks
	    				{
		    				Block theBlock = Block.getBlockFromName(blockName);
	    					theWorld.setBlockState(pos.add(parOffsetX+indX, parOffsetY+indY, parOffsetZ+indZ), 
	    							theBlock.getStateFromMeta(metaData), 2);
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
	    			int metaData = blockMetaArray[indX][indY][indZ];
	    			if (!(metaData==0))
	    			{
	    				String blockName = blockNameArray[indX][indY][indZ];
	    				Block theBlock = Block.getBlockFromName(blockName);
    					theWorld.setBlockState(pos.add(parOffsetX+indX, parOffsetY+indY, parOffsetZ+indZ), 
    							theBlock.getStateFromMeta(metaData), 2);
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
    	    			int metaData = blockMetaArray[indX][indY][indZ];
	    				Block theBlock = Block.getBlockFromName(blockName);
    					theWorld.setBlockState(pos.add(parOffsetX+indX, parOffsetY+indY, parOffsetZ+indZ), 
    							theBlock.getStateFromMeta(metaData), 2);
    				}	    			
	    		}
	    	}
	    }		
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public boolean checkPermission(MinecraftServer server,
            ICommandSender sender)
    {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server,
            ICommandSender sender, String[] args, BlockPos pos)
    {
        return null;
    }
}
