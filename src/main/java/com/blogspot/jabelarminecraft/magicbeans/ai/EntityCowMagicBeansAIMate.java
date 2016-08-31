/**
    Copyright (C) 2015 by jabelar

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

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

/**
 * @author jabelar
 *
 */
public class EntityCowMagicBeansAIMate extends EntityAIBase
{
    private final EntityAnimal theAnimal;
    World theWorld;
    private EntityAnimal targetMate;
    /** Delay preventing a baby from spawning immediately when two mate-able animals find each other. */
    int spawnBabyDelay;
    /** The speed the creature moves at during mating behavior. */
    double moveSpeed;

    public EntityCowMagicBeansAIMate(EntityAnimal parAnimal, double parMoveSpeed)
    {
        theAnimal = parAnimal;
        theWorld = parAnimal.worldObj;
        moveSpeed = parMoveSpeed;
        setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
	public boolean shouldExecute()
    {
        if (!theAnimal.isInLove())
        {
            return false;
        }
        else
        {
            targetMate = getNearbyMate();
            return targetMate != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
	public boolean continueExecuting()
    {
        return targetMate.isEntityAlive() && targetMate.isInLove() && spawnBabyDelay < 60;
    }

    /**
     * Resets the task
     */
    @Override
	public void resetTask()
    {
        targetMate = null;
        spawnBabyDelay = 0;
    }

    /**
     * Updates the task
     */
    @Override
	public void updateTask()
    {
        theAnimal.getLookHelper().setLookPositionWithEntity(targetMate, 10.0F, theAnimal.getVerticalFaceSpeed());
        theAnimal.getNavigator().tryMoveToEntityLiving(targetMate, moveSpeed);
        ++spawnBabyDelay;

        if (spawnBabyDelay >= 60 && theAnimal.getDistanceSqToEntity(targetMate) < 9.0D)
        {
            spawnBaby();
        }
        
        // DEBUG
        System.out.println("AIMate updateTask() the animal with entityID = "+theAnimal.getEntityId()+" is in love = "+theAnimal.isInLove());
        if (targetMate != null)
        {
        	System.out.println("AIMate updateTask() the mate target with entityID = "+targetMate.getEntityId()+" is in love = "+targetMate.isInLove());
        }
        else
        {
        	System.out.println("AIMate updateTask() there is no mate target");
        }
    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    private EntityAnimal getNearbyMate()
    {
        float matingRange = 8.0F;
        List list = theWorld.getEntitiesWithinAABB(EntityCow.class, theAnimal.getEntityBoundingBox().expand(matingRange, matingRange, matingRange));
        double d0 = Double.MAX_VALUE;
        EntityAnimal theMate = null;
        Iterator iterator = list.iterator();
 
        while (iterator.hasNext())
        {
            EntityAnimal potentialMate = (EntityAnimal)iterator.next();

            if (potentialMate.getEntityId() != theAnimal.getEntityId() && theAnimal.canMateWith(potentialMate) && theAnimal.getDistanceSqToEntity(potentialMate) < d0)
            {
                theMate = potentialMate;
                d0 = theAnimal.getDistanceSqToEntity(potentialMate);
            }
        }

        return theMate;
    }

    /**
     * Spawns a baby animal of the same type.
     */
    private void spawnBaby()
    {
        EntityAgeable entityageable = theAnimal.createChild(targetMate);

        if (entityageable != null)
        {
            EntityPlayer entityplayer = theAnimal.getPlayerInLove();

            if (entityplayer == null && targetMate.getPlayerInLove() != null)
            {
                entityplayer = targetMate.getPlayerInLove();
            }

            if (entityplayer != null)
            {
                entityplayer.addStat(StatList.ANIMALS_BRED);

                if (theAnimal instanceof EntityCow)
                {
                    entityplayer.addStat(AchievementList.BREED_COW);
                }
            }

            theAnimal.setGrowingAge(6000);
            targetMate.setGrowingAge(6000);
            theAnimal.resetInLove();
            targetMate.resetInLove();
            entityageable.setGrowingAge(-24000);
            entityageable.setLocationAndAngles(theAnimal.posX, theAnimal.posY, theAnimal.posZ, 0.0F, 0.0F);
            theWorld.spawnEntityInWorld(entityageable);
            Random random = theAnimal.getRNG();

            for (int i = 0; i < 7; ++i)
            {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                theWorld.spawnParticle(EnumParticleTypes.HEART, theAnimal.posX + random.nextFloat() * theAnimal.width * 2.0F - theAnimal.width, theAnimal.posY + 0.5D + random.nextFloat() * theAnimal.height, theAnimal.posZ + random.nextFloat() * theAnimal.width * 2.0F - theAnimal.width, d0, d1, d2);
            }

            if (theWorld.getGameRules().getBoolean("doMobLoot"))
            {
                theWorld.spawnEntityInWorld(new EntityXPOrb(theWorld, theAnimal.posX, theAnimal.posY, theAnimal.posZ, random.nextInt(7) + 1));
            }
        }
    }
}