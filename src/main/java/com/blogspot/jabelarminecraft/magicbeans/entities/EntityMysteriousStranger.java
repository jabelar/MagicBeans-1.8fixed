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

package com.blogspot.jabelarminecraft.magicbeans.entities;

import javax.annotation.Nullable;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.gui.GuiMysteriousStranger;
import com.blogspot.jabelarminecraft.magicbeans.utilities.Utilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * @author jabelar
 *
 */
public class EntityMysteriousStranger extends EntityCreature implements IEntity, IEntityAdditionalSpawnData
{
    protected static final SoundEvent SOUND_EVENT_HAGGLE = new SoundEvent(new ResourceLocation("mob.villager.haggle"));
    protected static final SoundEvent SOUND_EVENT_AMBIENT = new SoundEvent(new ResourceLocation("mob.villager.idle"));
    protected static final SoundEvent SOUND_EVENT_HURT = new SoundEvent(new ResourceLocation("mob.villager.hit"));
    protected static final SoundEvent SOUND_EVENT_DEATH = new SoundEvent(new ResourceLocation("mob.villager.death"));

    protected NBTTagCompound syncDataCompound = new NBTTagCompound();
    protected EntityFamilyCow cowSummonedBy = null;
    protected EntityPlayer thePlayer = null;

	public EntityMysteriousStranger(World parWorld) 
	{
		super(parWorld);
				
		setupAI();
		// DEBUG
		System.out.println("EntityMysteriousStranger constructor for entity ID = "+getEntityId());
	}

	/**
	 * @param parWorld
	 */
	public EntityMysteriousStranger(World parWorld, EntityFamilyCow parCowSummonedBy, EntityPlayer parPlayer) 
	{
		super(parWorld);
		
		cowSummonedBy = parCowSummonedBy;
		thePlayer = parPlayer;
		
		setupAI();
		// DEBUG
		System.out.println("EntityMysteriousStranger constructor for entity ID = "+getEntityId());
	}

	// you don't have to call this as it is called automatically during EntityLiving subclass creation
	@Override
	protected void applyEntityAttributes()
	{
	    super.applyEntityAttributes(); 

	    // standard attributes registered to EntityLivingBase
	    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
	    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D); // doesnt' move
	    getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.8D);
	    getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
        
        // create particles
        if (ticksExisted % 5 == 0)
        {
            MagicBeans.proxy.generateMysteriousParticles(this);
        }
		
		// check if cow happened to get killed
		if (getCowSummonedBy() == null)
		{
			if (getPlayerSummonedBy()!=null) // handle case of creative mode creating mysterious stranger
			{
				if (worldObj.isRemote)
				{
					getPlayerSummonedBy().addChatMessage(new TextComponentString(Utilities.stringToRainbow("When your family cow died, the mysterious stranger vanished as quickly as he appeared!")));				
				}
				else
				{
					setDead();
				}
			}
		}
	}
	
	@Override
	public void setDead()
	{
		if (cowSummonedBy != null) // i.e. mysterious stranger dying for some reason before player trades cow for beans
		{
			cowSummonedBy.setHasSpawnedMysteriousStranger(false); // allow it to spawn another one
		}
		super.setDead();
	}
	
    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer parPlayer, Vec3d vec, @Nullable ItemStack stack, EnumHand hand)
	{
		this.collideWithNearbyEntities();;
		if (parPlayer.worldObj.isRemote)
		{
			playSound(SOUND_EVENT_HAGGLE, 1.0F, 1.0F);
			Minecraft.getMinecraft().displayGuiScreen(new GuiMysteriousStranger(this));
		}
		return EnumActionResult.SUCCESS;
		
	}


	@Override
	public void setupAI() 
	{
		// no AI needed as this entity just stays in one place
		clearAITasks();
		tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
	}

	@Override
	public void clearAITasks() 
	{
		tasks.taskEntries.clear();
		targetTasks.taskEntries.clear();
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
	protected SoundEvent getAmbientSound()
    {
        return SOUND_EVENT_AMBIENT;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected SoundEvent getHurtSound()
    {
        return SOUND_EVENT_HURT;
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected SoundEvent getDeathSound()
    {
        return SOUND_EVENT_DEATH;
    }
	
    @Override
    public void readEntityFromNBT(NBTTagCompound parCompound)
    {
    	super.readEntityFromNBT(parCompound);
    	syncDataCompound = (NBTTagCompound) parCompound.getTag("syncDataCompound");
        // DEBUG
        System.out.println("EntityMysteriousStranger readEntityFromNBT");
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound parCompound)
    {
    	super.writeEntityToNBT(parCompound);
    	parCompound.setTag("syncDataCompound", syncDataCompound);
        // DEBUG
        System.out.println("EntityMysteriousStranger writeEntityToNBT");
    }    

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#initExtProps()
	 */
	@Override
	public void initSyncDataCompound() 
	{
		// don't use setters because it might be too early to send sync packet
        syncDataCompound.setFloat("scaleFactor", 1.0F);
        syncDataCompound.setInteger("cowSummonedById", cowSummonedBy.getEntityId());
        syncDataCompound.setInteger("playerSummonedById", thePlayer.getEntityId());		
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
	
	public EntityFamilyCow getCowSummonedBy()
	{
		int cowSummonedById = syncDataCompound.getInteger("cowSummonedById");

		// DEBUG
		// System.out.println("EntityMysteriousStranger getCowSummonedBy = "+cowSummonedById+", on world.isRemote = "+worldObj.isRemote);
		return (EntityFamilyCow) worldObj.getEntityByID(cowSummonedById);
	}
	
	public void setCowSummonedBy(EntityFamilyCow parCowMagicBeans)
	{
		cowSummonedBy = parCowMagicBeans;
		int cowSummonedById = parCowMagicBeans.getEntityId();
		
		// DEBUG
		System.out.println("EntityMysteriousStranger setCowSummonedBy = "+cowSummonedById+", on world.isRemote = "+worldObj.isRemote);

		syncDataCompound.setInteger("cowSummonedById", cowSummonedById);
	       
        // don't forget to sync client and server
        sendEntitySyncPacket();
	}

	public EntityPlayer getPlayerSummonedBy() 
	{
		int playerSummonedById = syncDataCompound.getInteger("playerSummonedById");

		// DEBUG
		// System.out.println("EntityMysteriousStranger getPlayerSummonedBy = "+playerSummonedById+", on world.isRemote = "+worldObj.isRemote);
		return (EntityPlayer) worldObj.getEntityByID(playerSummonedById);
	}

	public void setPlayerSummonedBy(EntityPlayer parPlayerSummonedBy) 
	{
		thePlayer = parPlayerSummonedBy;
		int playerSummonedById = parPlayerSummonedBy.getEntityId();
		
		// DEBUG
		System.out.println("EntityMysteriousStranger setPlayerSummonedBy = "+playerSummonedById+", on world.isRemote = "+worldObj.isRemote);

		syncDataCompound.setInteger("playerSummonedById", playerSummonedById);
	       
        // don't forget to sync client and server
        sendEntitySyncPacket();
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
		System.out.println("EntityMysteriousStranger spawn data received, scaleFactor = "+getScaleFactor());
	}

}
