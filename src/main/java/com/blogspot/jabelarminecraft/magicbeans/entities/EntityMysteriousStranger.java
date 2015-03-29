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

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import com.blogspot.jabelarminecraft.magicbeans.gui.GuiMysteriousStranger;
import com.blogspot.jabelarminecraft.magicbeans.particles.EntityParticleFXMysterious;
import com.blogspot.jabelarminecraft.magicbeans.utilities.MagicBeansUtilities;

/**
 * @author jabelar
 *
 */
public class EntityMysteriousStranger extends EntityCreature implements IEntityMagicBeans, IEntityAdditionalSpawnData
{
    private NBTTagCompound syncDataCompound = new NBTTagCompound();
    private EntityCowMagicBeans cowSummonedBy = null;
    private EntityPlayer thePlayer = null;

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
	public EntityMysteriousStranger(World parWorld, EntityCowMagicBeans parCowSummonedBy, EntityPlayer parPlayer) 
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
	    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
	    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0D); // doesnt' move
	    getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.8D);
	    getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);

	    // need to register any additional attributes
//	   getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
//	   getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (worldObj.isRemote && rand.nextFloat()<0.1F)
		{
			double var4 = rand.nextGaussian() * 0.02D;
			double var6 = rand.nextGaussian() * 0.02D;
			double var8 = rand.nextGaussian() * 0.02D;
			EntityFX particleMysterious = new EntityParticleFXMysterious(worldObj, posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, var4, var6, var8);
			Minecraft.getMinecraft().effectRenderer.addEffect(particleMysterious);
		}
		
		// check if cow happened to get killed
		if (getCowSummonedBy() == null)
		{
			if (getPlayerSummonedBy()!=null) // handle case of creative mode creating mysterious stranger
			{
				if (worldObj.isRemote)
				{
					getPlayerSummonedBy().addChatMessage(new ChatComponentText(MagicBeansUtilities.stringToRainbow("When your family cow died, the mysterious stranger vanished as quickly as he appeared!")));				
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
	
	@Override
	public boolean interact(EntityPlayer parPlayer)
	{
		this.collideWithNearbyEntities();;
		if (parPlayer.worldObj.isRemote)
		{
			playSound("mob.villager.haggle", 1.0F, 1.0F);
			Minecraft.getMinecraft().displayGuiScreen(new GuiMysteriousStranger(this));
		}
		return false;
		
	}

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#setupAI()
	 */
	@Override
	public void setupAI() 
	{
		// no AI needed as this entity just stays in one place
	}

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#clearAITasks()
	 */
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
	protected String getLivingSound()
    {
        return "mob.villager.idle";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return "mob.villager.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return "mob.villager.death";
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
	
	public EntityCowMagicBeans getCowSummonedBy()
	{
		int cowSummonedById = syncDataCompound.getInteger("cowSummonedById");

		// DEBUG
		// System.out.println("EntityMysteriousStranger getCowSummonedBy = "+cowSummonedById+", on world.isRemote = "+worldObj.isRemote);
		return (EntityCowMagicBeans) worldObj.getEntityByID(cowSummonedById);
	}
	
	public void setCowSummonedBy(EntityCowMagicBeans parCowMagicBeans)
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
		MagicBeansUtilities.sendEntitySyncPacketToClient(this);
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
