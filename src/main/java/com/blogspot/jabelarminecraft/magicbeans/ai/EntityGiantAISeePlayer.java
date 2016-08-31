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

package com.blogspot.jabelarminecraft.magicbeans.ai;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.entities.EntityGiant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;


public class EntityGiantAISeePlayer extends EntityAIBase
{
    protected final EntityGiant theGiant;
    protected EntityPlayer thePlayer;
    protected final World worldObject;
    protected double followDistance;

    public EntityGiantAISeePlayer(EntityGiant parEntityGiant)
    {
        theGiant = parEntityGiant;
        worldObject = parEntityGiant.worldObj;
        // setMutexBits(2);
    	followDistance = theGiant.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
	public boolean shouldExecute()
    {        

        thePlayer = worldObject.getClosestPlayerToEntity(theGiant, followDistance);
        if (thePlayer == null || ((EntityPlayerMP)thePlayer).interactionManager.isCreative())
        {
          	return false;
        }
        return theGiant.canEntityBeSeen(thePlayer);
   }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
	public void startExecuting()
    {
    	// target the player
        theGiant.setAttackTarget(thePlayer);
        // DEBUG
        System.out.println("The Giant sees you!");
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
	public boolean continueExecuting()
    {
        if (thePlayer == null || ((EntityPlayerMP)thePlayer).interactionManager.isCreative()
        		|| thePlayer.isDead)
        {
        	return false;
        }
        
        if (theGiant.canEntityBeSeen(thePlayer))
        {
        	return true;
        }
        else
        {
        	theGiant.setAttackTarget(null);
        	return false;
        }
    }

    /**
     * Resets the task
     */
    @Override
	public void resetTask()
    {
    	// DEBUG
    	System.out.println("Resetting task");
        theGiant.setAttackTarget(null);
        theGiant.setSpecialAttackTimer(0);
        thePlayer = null;
    }

    /**
     * Updates the task
     */
    @Override
	public void updateTask()
    {
    	
    	if (theGiant.ticksExisted%200 == 0) 
    	{ // every 10 seconds
			if (theGiant.worldObj.rand.nextInt(10)<8) 
			{ // 80% chance
				if (!theGiant.isInWater()) 
				{
					// DEBUG
					System.out.println("Giant jump attack!");
					theGiant.setSpecialAttackTimer(20);
				}
			}
    	}
		if (theGiant.getSpecialAttackTimer() == 20)
		{
			theGiant.jump();
		}
    	if (theGiant.getSpecialAttackTimer() == 1)
    	{
    		theGiant.getSpecialAttack().doGiantAttack(MagicBeans.configGiantAttackDamage*3);
    	}
    	if (theGiant.getSpecialAttackTimer() > 0)
    	{
    		theGiant.decrementSpecialAttackTimer();
    	}

        theGiant.getLookHelper().setLookPosition(thePlayer.posX, thePlayer.posY + thePlayer.getEyeHeight(), thePlayer.posZ, 10.0F, theGiant.getVerticalFaceSpeed());
    }

}