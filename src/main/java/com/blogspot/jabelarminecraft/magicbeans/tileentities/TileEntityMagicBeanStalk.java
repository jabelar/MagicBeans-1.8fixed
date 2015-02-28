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

package com.blogspot.jabelarminecraft.magicbeans.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.MagicBeansWorldData;
import com.blogspot.jabelarminecraft.magicbeans.blocks.BlockMagicBeanStalk;

public class TileEntityMagicBeanStalk extends TileEntity
{
	protected int ticksExisted = 0 ;	

    @Override
	public void readFromNBT(NBTTagCompound parTagCompound)
    {
    	super.readFromNBT(parTagCompound);
        ticksExisted = parTagCompound.getInteger("ticksExisted");
    }

    @Override
	public void writeToNBT(NBTTagCompound parTagCompound)
    {
    	super.writeToNBT(parTagCompound);
        parTagCompound.setInteger("ticksExisted", ticksExisted);
    }
    	
	public void update()
	{
		if (worldObj.isRemote || MagicBeansWorldData.get(worldObj).getHasCastleSpwaned())
		{
			return;
		}
		
		++ticksExisted;
		markDirty();
		
		worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(((BlockMagicBeanStalk)MagicBeans.blockMagicBeanStalk).AGE, Integer.valueOf(Math.min(7,  ticksExisted / MagicBeans.configTicksPerGrowStage))), 2);
		if (ticksExisted >= MagicBeans.configTicksPerGrowStage * 9) 
		{
			// check if higher than clouds
			if (this.getPos().getY() < MagicBeans.configMaxStalkHeight)
			{
	    		// check if can build next growing position
	    	    if(worldObj.isAirBlock(pos.add(0, 1, 0)))
	    	    {
	    	    	// DEBUG
	    	    	// System.out.println("Beanstalk still growing, hasSpawnedCastle = "+hasSpawnedCastle);
	    	        worldObj.setBlockState(pos.add(0, 1, 0), MagicBeans.blockMagicBeanStalk.getDefaultState());	    	        
	    	    }   		
 			}
			else // fully grown
			{
				MagicBeans.structureCastleTalia.shouldGenerate = true;
				MagicBeans.structureCastleTalia.generateTick(this, 5, -2, 5);
			}
		}
	}
}
