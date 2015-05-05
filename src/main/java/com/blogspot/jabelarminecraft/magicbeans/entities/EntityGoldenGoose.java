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

package com.blogspot.jabelarminecraft.magicbeans.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.utilities.Utilities;

public class EntityGoldenGoose extends EntityAnimal implements IEntity, IEntityAdditionalSpawnData
{
    private NBTTagCompound syncDataCompound = new NBTTagCompound();

    public float rotationFloat1;
    public float destPos;
    public float rotationFloat2;
    public float rotationFloat3;
    public float rotationIncrementFactor = 1.0F;
    /** The time until the next egg is spawned. */
    public int timeUntilNextEgg;

    public EntityGoldenGoose(World parWorld)
    {
        super(parWorld);
        setSize(0.6F, 1.4F); // twice size of EntityChicken
        isImmuneToFire = true ;
        timeUntilNextEgg = rand.nextInt(MagicBeans.configTimeUntilNextEgg) + MagicBeans.configTimeUntilNextEgg;
        setupAI();
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
	public void onLivingUpdate()
    {
        super.onLivingUpdate();
        rotationFloat3 = rotationFloat1;
        rotationFloat2 = destPos;
        destPos = (float)(destPos + (onGround ? -1 : 4) * 0.3D);

        if (destPos < 0.0F)
        {
            destPos = 0.0F;
        }

        if (destPos > 1.0F)
        {
            destPos = 1.0F;
        }

        if (!onGround && rotationIncrementFactor < 1.0F)
        {
            rotationIncrementFactor = 1.0F;
        }

        rotationIncrementFactor = (float)(rotationIncrementFactor * 0.9D);

        if (!onGround && motionY < 0.0D)
        {
            motionY *= 0.6D;
        }

        rotationFloat1 += rotationIncrementFactor * 2.0F;
        
        // if on server and egg timer is up, lay egg
        if (!worldObj.isRemote)
        {
        	// DEBUG
        	// System.out.println("Time until next egg ="+timeUntilNextEgg);
            if (!isChild() && --timeUntilNextEgg <= 0)
            {

                playSound("mob.chicken.plop", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
                dropItem(MagicBeans.itemGoldenEgg, 1);
                // DEBUG
                System.out.println("Laid golden egg");
                timeUntilNextEgg = rand.nextInt(600) + 600;
            }
        }
    }
    
    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    @Override
    public void fall(float par1, float par2) { } // Does not fall normally, doesn't take fall damage.

    @Override
	public boolean interact(EntityPlayer parPlayer) 
    {
    	if (parPlayer.getCurrentEquippedItem() == null)
		{
			return false; 
		}
    	
    	Item theItem = parPlayer.getCurrentEquippedItem().getItem(); 
    	float healAmount = 0.0F;
        short growAmount = 0; // this is multiplied by 20 for actual age change
        boolean usedItem = false;

        if (theItem == Items.gold_ingot)
        {
            healAmount = 2.0F;
            growAmount = 60;
        }
        else if (theItem == Items.gold_nugget)
        {
            healAmount = 1.0F;
            growAmount = 30;
        }
        else if (Block.getBlockFromItem(theItem) == Blocks.gold_block)
        {
            healAmount = 7.0F;
            growAmount = 180;
        }
        else if (Block.getBlockFromItem(theItem) == Blocks.hay_block)
        {
            healAmount = 20.0F;
            growAmount = 180;
        }
        else if (theItem == Items.apple)
        {
            healAmount = 3.0F;
            growAmount = 60;
        }
        else if (theItem == Items.golden_carrot)
        {
            healAmount = 4.0F;
            growAmount = 60;

            if (getGrowingAge() >= 0)
            {
            	setInLove(parPlayer); // mating item
            }
        }
        else if (theItem == Items.golden_apple)
        {
            healAmount = 10.0F;
            growAmount = 240;

            if (getGrowingAge() >= 0)
            {
                setInLove(parPlayer); // mating item
            }
        }

        if (this.getHealth() < this.getMaxHealth() && healAmount > 0.0F)
        {
            heal(healAmount);
            usedItem = true;
        }
        
        if (isChild())
        {
            addGrowth(growAmount);
            usedItem = true;
        }

        if (usedItem)
        {
            worldObj.playSoundAtEntity(this, MagicBeans.MODID+":mob.goose.eat", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
        }

        if (usedItem)
        {
            if (!parPlayer.capabilities.isCreativeMode && --parPlayer.getCurrentEquippedItem().stackSize == 0)
            {
                parPlayer.inventory.setInventorySlotContents(parPlayer.inventory.currentItem, (ItemStack)null);
            }

            return true;
        }

        return super.interact(parPlayer);
    }

    @Override
	public boolean isBreedingItem(ItemStack parItemStack)
    {
    	if (parItemStack != null)
    	{
    		if (parItemStack.getItem() == Items.golden_apple || parItemStack.getItem() == Items.golden_carrot)
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    @Override
	public int getTalkInterval()
    {
        return 20*15; // quiet for at least 15 seconds
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
	protected String getLivingSound()
    {
        return "magicbeans:mob.goose.honk";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return "magicbeans:mob.goose.death";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return "magicbeans:mob.goose.death";
    }

    @Override
	protected void playStepSound(BlockPos parPos, Block parBlock)
    {
        playSound("mob.chicken.step", 0.15F, 1.0F);
    }

    @Override
	protected Item getDropItem()
    {
        return MagicBeans.goldenGooseMeat;
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    @Override
	protected void dropFewItems(boolean par1, int par2)
    {
    	dropItem(getDropItem(), 1);
    }

    @Override
	public EntityGoldenGoose createChild(EntityAgeable par1EntityAgeable)
    {
        return new EntityGoldenGoose(worldObj);
    }

	@Override
	public void setupAI() 
	{
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 1.4D));
        tasks.addTask(2, new EntityAIMate(this, 1.0D));
        tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.wheat_seeds, false));
        tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        tasks.addTask(5, new EntityAIWander(this, 1.0D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
	}

	@Override
	public void clearAITasks() 
	{
		tasks.taskEntries.clear();
		targetTasks.taskEntries.clear();
	}
	
    @Override
    public void readEntityFromNBT(NBTTagCompound parCompound)
    {
    	super.readEntityFromNBT(parCompound);
    	syncDataCompound = (NBTTagCompound) parCompound.getTag("syncDataCompound");
        // DEBUG
        System.out.println("EntityGoldenGoose readEntityFromNBT");
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound parCompound)
    {
    	super.writeEntityToNBT(parCompound);
    	parCompound.setTag("syncDataCompound", syncDataCompound);
        // DEBUG
        System.out.println("EntityGoldenGoose writeEntityToNBT");
    }

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#initExtProps()
	 */
	@Override
	public void initSyncDataCompound() 
	{
		// don't use setters because it might be too early to send sync packet
        syncDataCompound.setFloat("scaleFactor", 1.3F);
	}

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#getExtProps()
	 */
    @Override
    public NBTTagCompound getSyncDataCompound()
    {
        return syncDataCompound;
    }

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#setExtProps(net.minecraft.nbt.NBTTagCompound)
	 */
    @Override
    public void setSyncDataCompound(NBTTagCompound parCompound) 
    {
        syncDataCompound = parCompound;
        
        // probably need to be careful sync'ing here as this is called by
        // sync process itself -- don't create infinite loop
    }

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#setScaleFactor(float)
	 */
    @Override
    public void setScaleFactor(float parScaleFactor)
    {
        syncDataCompound.setFloat("scaleFactor", Math.abs(parScaleFactor));
       
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#getScaleFactor()
	 */
    @Override
    public float getScaleFactor()
    {
        return syncDataCompound.getFloat("scaleFactor");
    }
	
	@Override
	public void sendEntitySyncPacket()
	{
		Utilities.sendEntitySyncPacketToClient(this);
	}
	
	/* (non-Javadoc)
	 * @see net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData#writeSpawnData(io.netty.buffer.ByteBuf)
	 */
	@Override
	public void writeSpawnData(ByteBuf parBuffer) 
	{
		initSyncDataCompound();
		ByteBufUtils.writeTag(parBuffer, syncDataCompound);		
	}

	/* (non-Javadoc)
	 * @see net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData#readSpawnData(io.netty.buffer.ByteBuf)
	 */
	@Override
	public void readSpawnData(ByteBuf parBuffer) 
	{
		syncDataCompound = ByteBufUtils.readTag(parBuffer);	
		// DEBUG
		System.out.println("EntityGoldenGoose spawn data received, scaleFactor = "+getScaleFactor());
	}

}