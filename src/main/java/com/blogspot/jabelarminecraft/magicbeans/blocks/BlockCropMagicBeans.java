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

import com.blogspot.jabelarminecraft.magicbeans.tileentities.TileEntityMagicBeanStalk;
import net.minecraft.block.BlockBush;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
// import net.minecraft.util.IIcon;

public class BlockCropMagicBeans extends BlockBush implements ITileEntityProvider
{

    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

    protected boolean isFullyGrown = false;

    public BlockCropMagicBeans()
    {
    	this(Material.WOOD); // chop like wood and block movement
    }
    
    public BlockCropMagicBeans(Material parMaterial)
    {
    	super(parMaterial);
        // Basic block setup
        setDefaultState(blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        setTickRandomly(false);
        setCreativeTab((CreativeTabs)null);
        setHardness(0.0F);
        setSoundType(SoundType.PLANT);
        disableStats();
    }

	/**
     * is the block grass, dirt or farmland
     */
    @Override
    protected boolean canSustainBush(IBlockState parIBlockState)
    {
        return parIBlockState.getBlock() == Blocks.FARMLAND;
    }

    @Override
	public void updateTick(World parWorld, BlockPos parPos, IBlockState parState, Random parRand)
    {
//    	// DEBUG
//    	System.out.println("BlockCropMagicBeans updateTick()");
    	
        super.updateTick(parWorld, parPos, parState, parRand); // this just checks canBlockStay and deletes block if it can't
    }
    
    @Override
	public boolean canBlockStay(World parWorld, BlockPos parBlockPos, IBlockState parIBlockState)
    {
        return (parWorld.getLight(parBlockPos) >= 8 || parWorld.canSeeSky(parBlockPos)) && parWorld.getBlockState(parBlockPos.down()).getBlock().canSustainPlant(parIBlockState, parWorld, parBlockPos.down(), net.minecraft.util.EnumFacing.UP, this);
    }


    public boolean isFullyGrown()
    {
    	return isFullyGrown;
    }    

    /**
     * The type of render function that is called for this block
     */
    @Override
     public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL; // This has changed in 1.8.  1 seems to be liquids, 2 seems to be chest, 3 normal block
    }

	public void grow(World parWorld, BlockPos parPos, int parGrowStage)
	{
//		// DEBUG
//		System.out.println("BlockCropMagicBeans custom grow()");
		
		parWorld.setBlockState(parPos, parWorld.getBlockState(parPos).withProperty(AGE, Integer.valueOf(parGrowStage)));
	}
	
	 /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
	public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
	public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE).intValue();
    }

    @Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {AGE});
    }
    
	@Override
	public TileEntity createNewTileEntity(World parWorld, int parMetadata) 
	{
//		// DEBUG
//		System.out.println("BlockMagicBeans createNewTileEntity()");
		return new TileEntityMagicBeanStalk();
	}

}