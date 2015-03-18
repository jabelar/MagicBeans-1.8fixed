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

package com.blogspot.jabelarminecraft.magicbeans.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
// import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
// import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;

public class BlockMagicBeanStalk extends BlockCropMagicBeans // implements ITileEntityProvider
{

    public BlockMagicBeanStalk()
    {
    	super(); 
    	// DEBUG
    	System.out.println("BlockMagicBeanStalk constructor()");

    	// Basic block setup
        setUnlocalizedName("magicbeanstalk");
    	setBlockBounds(0.5F-0.22F, 0.0F, 0.5F-0.22F, 0.5F+0.22F, 1.0F, 0.5F+0.22F);
    }
    
    // identifies what food (ItemFood or ItemSeedFood type) is harvested from this
    @Override
	public Item getItemDropped(IBlockState parState, Random parRand, int parFortune)
    {
        return MagicBeans.magicBeans;
    }
    
    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random parRand)
    {
        return 0; // will make this a configurable quantity
    }
     
    @Override
	public boolean canPlaceBlockAt(World parWorld, BlockPos parPos) 
    {
        Block block = parWorld.getBlockState(parPos).getBlock();
        return block.canSustainPlant(parWorld, parPos.add(0, -1, 0), EnumFacing.UP, this) || block == this;
    }
    
    @Override
	public String getHarvestTool(IBlockState parState)
    {
        return null; // anything can harvest this block. should change to hatchet later
    }
        
    /**
     * is the block grass, dirt or farmland
     */
    // can plant on itself as this allows the bean stalk to grow very tall
    @Override
	protected boolean canPlaceBlockOn(Block parBlock)
    {
        return parBlock == MagicBeans.blockMagicBeanStalk || parBlock == Blocks.grass || parBlock == Blocks.dirt || parBlock == Blocks.farmland;
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType()
    {
        return 3; // 1 seems to be for liquids, 2 for chests, 3 for normal.
    }

    /**
     * Determines if this block can support the passed in plant, allowing it to be planted and grow.
     * Some examples:
     *   Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
     *   Cacti checks if its a cacti, or if its sand
     *   Nether types check for soul sand
     *   Crops check for tilled soil
     *   Caves check if it's a solid surface
     *   Plains check if its grass or dirt
     *   Water check if its still water
     *
     * @param parWorld The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @param parSide The direction relative to the given position the plant wants to be, typically its UP
     * @param parPlantable The plant that wants to check
     * @return True to allow the plant to be planted/stay.
     */
    @Override
	public boolean canSustainPlant(IBlockAccess parWorld, BlockPos parPos, EnumFacing parSide, IPlantable parPlantable)
    {
        if (parWorld.getBlockState(parPos.add(0, 1, 0)) == MagicBeans.blockMagicBeanStalk)
        {
        	return true;
        }
        else
        {
        	return super.canSustainPlant(parWorld, parPos, parSide, parPlantable);
        }
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    @Override
	public AxisAlignedBB getCollisionBoundingBox(World parWorld, BlockPos parPos, IBlockState parState)
    {
        return AxisAlignedBB.fromBounds(parPos.getX() + minX, parPos.getY() + minY, parPos.getZ() + minZ, parPos.getX() + maxX, parPos.getY() + maxY, parPos.getZ() + maxZ);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
	public void updateTick(World parWorld, BlockPos parPos, IBlockState parState, Random parRand)
    {
    	super.updateTick(parWorld, parPos, parState, parRand);
    }

//	@Override
//	public TileEntity createNewTileEntity(World parWorld, int parMetadata) 
//	{
//		// DEBUG
//		System.out.println("BlockMagicBeans createNewTileEntity()");
//		return new TileEntityMagicBeanStalk();
//	}

	@Override
	public boolean isLadder(IBlockAccess parWord, BlockPos parPos, EntityLivingBase parEntity)
	{
		return true;
	}
}