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
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
// import net.minecraft.util.IIcon;

import com.blogspot.jabelarminecraft.magicbeans.tileentities.TileEntityMagicBeanStalk;

public class BlockCropMagicBeans extends BlockBush implements IGrowable, ITileEntityProvider
{

    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

    protected boolean isFullyGrown = false;

    public BlockCropMagicBeans()
    {
    	this(Material.wood); // chop like wood and block movement
    }
    
    public BlockCropMagicBeans(Material parMaterial)
    {
    	super(parMaterial);
        // Basic block setup
        setDefaultState(blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        setTickRandomly(true);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        setCreativeTab((CreativeTabs)null);
        setHardness(0.0F);
        setStepSound(soundTypeGrass);
        disableStats();
    }

	/**
     * is the block grass, dirt or farmland
     */
    @Override
    protected boolean canPlaceBlockOn(Block parBlockToTest)
    {
        return parBlockToTest == Blocks.farmland;
    }

    @Override
	public void updateTick(World parWorld, BlockPos parPos, IBlockState parState, Random parRand)
    {
    	// DEBUG
    	System.out.println("BlockCropMagicBeans updateTick()");
    	
        super.updateTick(parWorld, parPos, parState, parRand); // this just checks canBlockStay and deletes block if it can't

//        TileEntityMagicBeanStalk theTileEntity = (TileEntityMagicBeanStalk) parWorld.getTileEntity(parPos);
//        int growStage = theTileEntity.getGrowStage();
//
//        // DEBUG
//        System.out.println("Setting grow stage = "+growStage);	
//        
//        parWorld.setBlockState(parPos, parState.withProperty(AGE, Integer.valueOf(growStage)), 2);
    }
    
    public void growCrops(World worldIn, BlockPos p_176487_2_, IBlockState p_176487_3_)
    {
        int i = ((Integer)p_176487_3_.getValue(AGE)).intValue() + MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);

        if (i > 7)
        {
            i = 7;
        }

        worldIn.setBlockState(p_176487_2_, p_176487_3_.withProperty(AGE, Integer.valueOf(i)), 2);
    }
    
    @Override
	public boolean canBlockStay(World worldIn, BlockPos p_180671_2_, IBlockState p_180671_3_)
    {
        return (worldIn.getLight(p_180671_2_) >= 8 || worldIn.canSeeSky(p_180671_2_)) && worldIn.getBlockState(p_180671_2_.offsetDown()).getBlock().canSustainPlant(worldIn, p_180671_2_.offsetDown(), net.minecraft.util.EnumFacing.UP, this);
    }


    public boolean isFullyGrown()
    {
    	return isFullyGrown;
    }    

    /**
     * The type of render function that is called for this block
     */
    @Override
     public int getRenderType()
    {
        return 3; // This has changed in 1.8.  1 seems to be liquids, 2 seems to be chest, 3 normal block
    }

	@Override
	public boolean isStillGrowing(World parWorld, BlockPos parPos,
			IBlockState parState, boolean parWorldIsRemote) 
	{
		// DEBUG
		System.out.println("BlockCropsMagicBeans isStillGrowing()");
		
        return ((Integer)parState.getValue(AGE)).intValue() < 7;
	}

	@Override
	public boolean canUseBonemeal(World parWorld, Random parRand,
			BlockPos parPos, IBlockState parState) 
	{
		return true;
	}

	public void grow(World parWorld, BlockPos parPos, int parGrowStage)
	{
		// DEBUG
		System.out.println("BlockCropMagicBeans custom grow()");
		
		parWorld.setBlockState(parPos, parWorld.getBlockState(parPos).withProperty(AGE, Integer.valueOf(parGrowStage)));
	}
	
	@Override
	public void grow(World parWorld, Random parRand, BlockPos parPos,
			IBlockState parState) 
	{
		// DEBUG
		System.out.println("BlockCropMagicBeans grow()");

        growCrops(parWorld, parPos, parState);

//    	if (!isFullyGrown)
//    	{
//			int i = ((Integer)parState.getValue(AGE)).intValue() + 1;
//			
//			if (i > 7)
//			{
//			    i = 7;
//			}
//			
//			parWorld.setBlockState(parPos, parState.withProperty(AGE, Integer.valueOf(i)));
//       	}
//    	else // fully grown so create the stalk above
//    	{
//    		// check if air above
//    	    if(parWorld.isAirBlock(parPos.add(0, 1, 0)))
//    	    {
//    	        parWorld.setBlockState(parPos.add(0, 1, 0), parState.withProperty(AGE, Integer.valueOf(0)), 2);
//    	    }
//    		
//    	}
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
        return ((Integer)state.getValue(AGE)).intValue();
    }

    @Override
	protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {AGE});
    }
    
	@Override
	public TileEntity createNewTileEntity(World parWorld, int parMetadata) 
	{
		// DEBUG
		System.out.println("BlockMagicBeans createNewTileEntity()");
		return new TileEntityMagicBeanStalk();
	}

}