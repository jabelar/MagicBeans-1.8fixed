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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.utilities.MagicBeansUtilities;

/**
 * @author jabelar
 *
 */
public class MagicBeansMonsterPlacer extends ItemMonsterPlacer
{
    @SideOnly(Side.CLIENT)
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
        setCreativeTab(CreativeTabs.tabMisc);
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
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else if (!playerIn.func_175151_a(pos.offset(side), side, stack))
        {
            return false;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);

            if (iblockstate.getBlock() == Blocks.mob_spawner)
            {
                TileEntity tileentity = worldIn.getTileEntity(pos);

                if (tileentity instanceof TileEntityMobSpawner)
                {
                    MobSpawnerBaseLogic mobspawnerbaselogic = ((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic();
                    mobspawnerbaselogic.setEntityName(EntityList.getStringFromID(stack.getMetadata()));
                    tileentity.markDirty();
                    worldIn.markBlockForUpdate(pos);

                    if (!playerIn.capabilities.isCreativeMode)
                    {
                        --stack.stackSize;
                    }

                    return true;
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

            return true;
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        if (worldIn.isRemote)
        {
            return itemStackIn;
        }
        else
        {
            MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, true);

            if (movingobjectposition == null)
            {
                return itemStackIn;
            }
            else
            {
                if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    BlockPos blockpos = movingobjectposition.func_178782_a();

                    if (!worldIn.isBlockModifiable(playerIn, blockpos))
                    {
                        return itemStackIn;
                    }

                    if (!playerIn.func_175151_a(blockpos, movingobjectposition.field_178784_b, itemStackIn))
                    {
                        return itemStackIn;
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

                            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        }
                    }
                }
                return itemStackIn;
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
            if (EntityList.stringToClassMapping.containsKey(entityToSpawnNameFull))
            {
                entityToSpawn = (EntityLiving) EntityList
                      .createEntityByName(entityToSpawnNameFull, parWorld);
                entityToSpawn.setLocationAndAngles(parX, parY, parZ, 
                      MathHelper.wrapAngleTo180_float(parWorld.rand.nextFloat()
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

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int parRenderPass)
    {
        return (parRenderPass == 0) ? colorBase : colorSpots;
    }
   
    @Override
    // Doing this override means that there is no localization for language
    // unless you specifically check for localization here and convert
    public String getItemStackDisplayName(ItemStack par1ItemStack)
    {
        return MagicBeansUtilities.stringToRainbow("Spawn "+StatCollector.translateToLocal("entity."+MagicBeans.MODID+"."+entityToSpawnName+".name"));
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