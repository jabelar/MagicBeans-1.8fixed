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

package com.blogspot.jabelarminecraft.magicbeans.items;

import java.util.List;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.utilities.Utilities;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author jabelar
 *
 */
public class MagicBeansMonsterPlacer extends ItemMonsterPlacer
{
    protected int colorBase = 0x000000;
    protected int colorSpots = 0xFFFFFF;
    protected String entityToSpawnName = "";
    protected String entityToSpawnNameFull = "";
    protected EntityLiving entityToSpawn = null;

    public MagicBeansMonsterPlacer()
    {
        super();
    }
    
    public MagicBeansMonsterPlacer(String parEntityToSpawnName, int parPrimaryColor, 
          int parSecondaryColor)
    {
        setUnlocalizedName("spawn_egg_"+parEntityToSpawnName);
        setHasSubtypes(false);
        maxStackSize = 64;
        setCreativeTab(CreativeTabs.MISC);
        setEntityToSpawnName(parEntityToSpawnName);
        colorBase = parPrimaryColor;
        colorSpots = parSecondaryColor;

        // DEBUG
        System.out.println("Spawn egg constructor for "+entityToSpawnName+" with unlocalized name "+getUnlocalizedName());
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item parItem, CreativeTabs parTab, List parListSubItems)
    {
    	parListSubItems.add(new ItemStack(this, 1));
    }

    
    /**
     * Called when a Block is right-clicked with this Item
     *  
     * @param pos The block being right-clicked
     * @param side The side being right-clicked
     */
    @Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return EnumActionResult.PASS;
        }
        else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack))
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);

            if (iblockstate.getBlock() == Blocks.MOB_SPAWNER)
            {
                TileEntity tileentity = worldIn.getTileEntity(pos);

                if (tileentity instanceof TileEntityMobSpawner)
                {
                    MobSpawnerBaseLogic mobspawnerbaselogic = ((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic();
                    mobspawnerbaselogic.setEntityName(getEntityIdFromItem(stack));
                    tileentity.markDirty();
                    worldIn.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);

                    if (!playerIn.capabilities.isCreativeMode)
                    {
                        --stack.stackSize;
                    }

                    return EnumActionResult.PASS;
                }
            }

            pos = pos.offset(side);
            double d0 = 0.0D;

            if (side == EnumFacing.UP && iblockstate instanceof BlockFence)
            {
                d0 = 0.5D;
            }

            Entity entity = spawnEntity(worldIn, pos.getX() + 0.5D, pos.getY() + d0, pos.getZ() + 0.5D);

            if (entity != null)
            {
                if (entity instanceof EntityLivingBase && stack.hasDisplayName())
                {
                    entity.setCustomNameTag(stack.getDisplayName());
                }

                if (!playerIn.capabilities.isCreativeMode)
                {
                    --stack.stackSize;
                }
            }

            return EnumActionResult.PASS;
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        if (worldIn.isRemote)
        {
            return new ActionResult(EnumActionResult.PASS, itemStackIn);
        }
        else
        {
            RayTraceResult rayTraceResult = this.rayTrace(worldIn, playerIn, true);

            if (rayTraceResult == null)
            {
                return new ActionResult(EnumActionResult.PASS, itemStackIn);
            }
            else
            {
                if (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    BlockPos blockpos = rayTraceResult.getBlockPos();

                    if (!worldIn.isBlockModifiable(playerIn, blockpos))
                    {
                        return new ActionResult(EnumActionResult.PASS, itemStackIn);
                    }

                    if (!playerIn.canPlayerEdit(blockpos, rayTraceResult.sideHit, itemStackIn))
                    {
                        return new ActionResult(EnumActionResult.PASS, itemStackIn);
                    }

                    if (worldIn.getBlockState(blockpos).getBlock() instanceof BlockLiquid)
                    {
                        Entity entity = spawnEntity(worldIn, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D);

                        if (entity != null)
                        {
                            if (entity instanceof EntityLivingBase && itemStackIn.hasDisplayName())
                            {
                                ((EntityLiving)entity).setCustomNameTag(itemStackIn.getDisplayName());
                            }

                            if (!playerIn.capabilities.isCreativeMode)
                            {
                                --itemStackIn.stackSize;
                            }

                            playerIn.addStat(StatList.getObjectUseStats(this));
                        }
                    }
                }
                return new ActionResult(EnumActionResult.PASS, itemStackIn);
            }
        }
    }
    
    /**
     * Spawns the creature specified by the egg's type in the location specified by 
     * the last three parameters.
     * Parameters: world, x, y, z.
     */
    public Entity spawnEntity(World parWorld, double parX, double parY, double parZ)
    {
     
       if (!parWorld.isRemote) // never spawn entity on client side
       {
            entityToSpawnNameFull = MagicBeans.MODID+"."+entityToSpawnName;
            if (EntityList.NAME_TO_CLASS.containsKey(entityToSpawnNameFull))
            {
                entityToSpawn = (EntityLiving) EntityList
                      .createEntityByName(entityToSpawnNameFull, parWorld);
                entityToSpawn.setLocationAndAngles(parX, parY, parZ, 
                      MathHelper.wrapDegrees(parWorld.rand.nextFloat()
                      * 360.0F), 0.0F);
                parWorld.spawnEntityInWorld(entityToSpawn);
                entityToSpawn.playLivingSound();
            }
            else
            {
                //DEBUG
                System.out.println("Entity not found "+entityToSpawnName);
            }
        }
      
        return entityToSpawn;
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public int getColorFromItemStack(ItemStack par1ItemStack, int parRenderPass)
//    {
//        this.
//        return (parRenderPass == 0) ? colorBase : colorSpots;
//    }
   
    @Override
    // Doing this override means that there is no localization for language
    // unless you specifically check for localization here and convert
    public String getItemStackDisplayName(ItemStack par1ItemStack)
    {
        return Utilities.stringToRainbow("Spawn "+new TextComponentTranslation("entity."+MagicBeans.MODID+"."+entityToSpawnName+".name").getFormattedText());
    }  
    
    public void setColors(int parColorBase, int parColorSpots)
    {
     colorBase = parColorBase;
     colorSpots = parColorSpots;
    }
    
    public int getColorBase()
    {
     return colorBase;
    }
    
    public int getColorSpots()
    {
     return colorSpots;
    }
    
    public void setEntityToSpawnName(String parEntityToSpawnName)
    {
        entityToSpawnName = parEntityToSpawnName;
        entityToSpawnNameFull = MagicBeans.MODID+"."+entityToSpawnName; 
    }

}