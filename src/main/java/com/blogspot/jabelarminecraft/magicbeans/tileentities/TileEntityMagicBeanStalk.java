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

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.ModWorldData;
import com.blogspot.jabelarminecraft.magicbeans.blocks.BlockCropMagicBeans;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityMagicBeanStalk extends TileEntity implements ITickable
{
	protected int ticksExisted = 0 ;
	protected int growStage = 0 ;

    @Override
	public void readFromNBT(NBTTagCompound parTagCompound)
    {
    	super.readFromNBT(parTagCompound);
        ticksExisted = parTagCompound.getInteger("ticksExisted");
    }

    @Override
	public NBTTagCompound writeToNBT(NBTTagCompound parTagCompound)
    {
    	super.writeToNBT(parTagCompound);
        parTagCompound.setInteger("ticksExisted", ticksExisted);
        return parTagCompound;
    }
    	
	@Override
	public void update()
	{
		if (worldObj.isRemote || ModWorldData.get(worldObj).getHasCastleSpawned())
		{
			return;
		}
		
		++ticksExisted;
		markDirty();
		
		growStage = Math.min(7, ticksExisted/MagicBeans.configTicksPerGrowStage);

//		// DEBUG
//		System.out.println("TileEntityMagicBeans update() with growStage = "+growStage);
		
		BlockCropMagicBeans theCrop = (BlockCropMagicBeans)worldObj.getBlockState(pos).getBlock();
		theCrop.grow(worldObj, pos, growStage);
		
		if (ticksExisted >= MagicBeans.configTicksPerGrowStage * 8) 
		{
			// check if still need to grow to get to max height (default cloud level)
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
				MagicBeans.structureCastle.shouldGenerate = true;
//				MagicBeans.structureCastle.generate(this, pos.getX(), pos.getY(), pos.getZ(), true);
				MagicBeans.structureCastle.generateTick(this, 5, -2, 5);
			}
		}
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
	    return (oldState.getBlock() != newSate.getBlock());
	}
	
	public int getGrowStage()
	{
		return growStage;
	}
}

