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

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
// import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
// import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMagicBeanStalk extends BlockCropMagicBeans // implements ITileEntityProvider
{
    protected static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.5F-0.125F, 0.0F, 0.5F-0.125F, 0.5F+0.125F, 1.0F, 0.5F+0.125F);

    public BlockMagicBeanStalk()
    {
    	super(); 
    	// DEBUG
    	System.out.println("BlockMagicBeanStalk constructor()");

    	// Basic block setup
        setUnlocalizedName("magicbeanstalk");
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
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());
        return super.canPlaceBlockAt(worldIn, pos) && soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
    }
    
    @Override
	public String getHarvestTool(IBlockState parState)
    {
        return null; // anything can harvest this block. should change to hatchet later
    }
        

    /**
     * The type of render function that is called for this block
     */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL; // 1 seems to be for liquids, 2 for chests, 3 for normal.
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
	public boolean canSustainBush(IBlockState state)
    {
        if (state.getBlock() == MagicBeans.blockMagicBeanStalk || state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.FARMLAND)
        {
        	return true;
        }
        else
        {
        	return super.canSustainBush(state);
        }
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOX);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BOUNDING_BOX;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
	public void updateTick(World parWorld, BlockPos parPos, IBlockState parState, Random parRand)
    {
    	super.updateTick(parWorld, parPos, parState, parRand);
    }

	@Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity)
	{
		return true;
	}
}