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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import com.blogspot.jabelarminecraft.magicbeans.ModWorldData;
import com.blogspot.jabelarminecraft.magicbeans.ai.EntityCowMagicBeansAIMate;
import com.blogspot.jabelarminecraft.magicbeans.gui.GuiFamilyCow;
import com.blogspot.jabelarminecraft.magicbeans.utilities.Utilities;

/**
 * @author jabelar
 *
 */
public class EntityFamilyCow extends EntityCow implements IEntity, IEntityAdditionalSpawnData
{
    public NBTTagCompound syncDataCompound = new NBTTagCompound();
    
	/**
	 * @param parWorld
	 */
	public EntityFamilyCow(World parWorld) 
	{
		super(parWorld);
		// DEBUG
		System.out.println("EntityCowMagicBeans constructor");
		setupAI();
	}
	
	@Override
	public void setupAI()
	{
        tasks.taskEntries.clear();
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 2.0D));
        tasks.addTask(2, new EntityCowMagicBeansAIMate(this, 1.0D));
        tasks.addTask(3, new EntityAITempt(this, 1.25D, Items.wheat, false));
        tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
        tasks.addTask(5, new EntityAIWander(this, 1.0D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
	}
    
    @Override
	public void onUpdate()
    {
    	super.onUpdate();
    	if (getLeashed() && !worldObj.isRemote && !getHasSpawnedMysteriousStranger())
    	{
    		// chance mysterious stranger will appear
    		if (!getHasSpawnedMysteriousStranger() && (rand.nextFloat() < (1.0F / (30 * 20))))
    		{
        		Entity entityLeashedTo = getLeashedToEntity();
        		if (entityLeashedTo instanceof EntityPlayer)
        		{
	    			EntityPlayer playerLeashedTo = (EntityPlayer) entityLeashedTo;
	    			Vec3 playerLookVector = playerLeashedTo.getLookVec();
	
	    			EntityMysteriousStranger entityToSpawn = new EntityMysteriousStranger(worldObj, this, playerLeashedTo);
	                double spawnX = playerLeashedTo.posX+5*playerLookVector.xCoord;
	                double spawnZ = playerLeashedTo.posZ+5*playerLookVector.zCoord;
	                double spawnY = Utilities.getHeightValue(worldObj, spawnX, spawnZ);
	                
	                // DEBUG
	                System.out.println("Trying to spawn mysterious stranger at "+spawnX+", "+spawnY+", "+spawnZ);
	                BlockPos spawnPos = new BlockPos(spawnX, spawnY, spawnZ);
	                
	                // check to ensure there is open area for stranger to spawn, not underground
	                if (worldObj.canBlockSeeSky(spawnPos))
	                {
	                	entityToSpawn.setLocationAndAngles(spawnX, spawnY, spawnZ, 
		                      MathHelper.wrapAngleTo180_float(rand.nextFloat()
		                      * 360.0F), 0.0F);
		                worldObj.spawnEntityInWorld(entityToSpawn);
		                entityToSpawn.playLivingSound();
		                setHasSpawnedMysteriousStranger(true);
		    			playerLeashedTo.addChatMessage(new ChatComponentText(Utilities.stringToRainbow("A mysterious stranger appears!")));
		        		// DEBUG
		        		System.out.println("A mysterious stranger appears with entity ID = "+entityToSpawn.getEntityId());
	                }
        		}
    		}
    	}
    }
    
    protected MovingObjectPosition getMovingObjectPositionFromPlayer(World parWorld, EntityPlayer parPlayer, double parDistance, boolean parUseLiquids)
    {
        float f = parPlayer.prevRotationPitch + (parPlayer.rotationPitch - parPlayer.prevRotationPitch);
        float f1 = parPlayer.prevRotationYaw + (parPlayer.rotationYaw - parPlayer.prevRotationYaw);
        double vecX = parPlayer.posX ;
        double vecY = parPlayer.posY + parPlayer.getEyeHeight();
        double vecZ = parPlayer.posZ ;
        Vec3 vec3 = new Vec3(vecX, vecY, vecZ);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vec31 = vec3.addVector(f6 * parDistance, f5 * parDistance, f7 * parDistance);
        return parWorld.rayTraceBlocks(vec3, vec31, parUseLiquids, !parUseLiquids, false);
    }


    @Override
	protected Item getDropItem()
    {
        return Items.leather;
    }
 
    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    @Override
	protected void dropFewItems(boolean parRecentlyHit, int parLootingLevel)
    {
        int j = rand.nextInt(3) + rand.nextInt(1 + parLootingLevel);
        int k;

        dropItem(getDropItem(), 1);
 
        for (k = 0; k < j; ++k)
        {
            if (isBurning())
            {
                dropItem(Items.cooked_beef, 1);
            }
            else
            {
                dropItem(Items.beef, 1);
            }
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
	public boolean interact(EntityPlayer parPlayer)
    {
    	// check if have already spawned castle
    	if (!ModWorldData.get(worldObj).getHasCastleSpawned())
    	{
	    	// Family cow doesn't provide milk (that's why your mother wants you to sell it)
	    	// don't open gui if holding items, e.g. wheat that should incite mating instead
	    	// also don't open gui if already gone through gui to get a lead
	    	if (parPlayer.getCurrentEquippedItem() == null || parPlayer.getCurrentEquippedItem().getItem() == Items.bucket)
	    	{
				collideWithNearbyEntities();
				if (parPlayer.worldObj.isRemote)
				{
					Minecraft.getMinecraft().displayGuiScreen(new GuiFamilyCow());
				}
	    	}
	    	else // act like normal cow
	    	{
	    		super.interact(parPlayer);
	    	}
    	}
    	else // act like normal cow
    	{
    		super.interact(parPlayer);
    	}
		return false;
    }
    
    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    @Override
	public boolean canMateWith(EntityAnimal parAnimal)
    {
    	// want to allow mating with regular cows as well as itself
    	return parAnimal == this ? false :(parAnimal instanceof EntityCow && isInLove() && parAnimal.isInLove());
    }

    @Override
	public EntityCow createChild(EntityAgeable parAgeable)
    {
        return new EntityFamilyCow(worldObj);
    }

    @Override
	public boolean canDespawn()
    {
    	return false;
    }
    
    @Override
	public int getMaxSpawnedInChunk()
    {
    	return 1;
    }

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#clearAITasks()
	 */
	@Override
	public void clearAITasks() {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void readEntityFromNBT(NBTTagCompound parCompound)
    {
    	super.readEntityFromNBT(parCompound);
    	syncDataCompound = (NBTTagCompound) parCompound.getTag("syncDataCompound");
        // DEBUG
        // System.out.println("EntityCowMagicBeans readEntityFromNBT");
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound parCompound)
    {
    	super.writeEntityToNBT(parCompound);
    	parCompound.setTag("syncDataCompound", syncDataCompound);
        // DEBUG
        // System.out.println("EntityCowMagicBeans writeEntityToNBT");
    }
    
	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#initExtProps()
	 */
	@Override
	public void initSyncDataCompound() 
	{
		// don't use setters because it might be too early to send sync packet
        syncDataCompound.setFloat("scaleFactor", 1.0F);
        syncDataCompound.setBoolean("hasSpawnedMysteriousStranger", false);
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

    public boolean getHasSpawnedMysteriousStranger()
    {
        return syncDataCompound.getBoolean("hasSpawnedMysteriousStranger");
    }
    
    public void setHasSpawnedMysteriousStranger(boolean parHasSpawnedMysteriousStranger)
    {
        syncDataCompound.setBoolean("hasSpawnedMysteriousStranger", parHasSpawnedMysteriousStranger);
       
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#sendEntitySyncPacket()
	 */
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
		System.out.println("EntityCowMagicBeans spawn data received, scaleFactor = "+getScaleFactor()+", hasSpawnedMysteriousStranger = "+getHasSpawnedMysteriousStranger());
	}

}
