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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityGoldenEggThrown extends EntityThrowable
{  

    public EntityGoldenEggThrown(World par1World)
    {
        super(par1World);
    }

    public EntityGoldenEggThrown(World par1World, EntityLivingBase par2EntityLivingBase)
    {
        super(par1World, par2EntityLivingBase);
        // DEBUG
        System.out.println("Constructing on client side ="+worldObj.isRemote);
        System.out.println("Constructor: Thrown egg entity position ="+posX+", "+posY+", "+posZ);
        System.out.println("Constructor: Thrown egg entity motion ="+motionX+", "+motionY+", "+motionZ);
        
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onImpact(RayTraceResult parRayTraceResult)
    {
        if (parRayTraceResult.entityHit != null)
        {
            parRayTraceResult.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
        }

        if (!worldObj.isRemote) // never spawn entity on client side
        {
    	    // should sometimes give gold ingot
            // should sometimes (very rarely) give baby golden goose
        	int dropItem = MathHelper.getRandomIntegerInRange(rand, 0, 12);
        	if (dropItem == 0) // 1 in 12 chance of spawning entity, (chicken egg is 1 in 8)
        	{
                EntityAnimal entityToSpawn = new EntityGoldenGoose(worldObj); // (EntityAnimal) EntityList.createEntityByName("magicbeans.Golden Goose", worldObj);
                entityToSpawn.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
                entityToSpawn.setGrowingAge(-24000); // start as child
                worldObj.spawnEntityInWorld(entityToSpawn);
        	}
        	else
        	{
                dropItem(Items.GOLD_INGOT, 1);
                // DEBUG
                System.out.println("Good as gold");
        	}
        }

        for (int j = 0; j < 8; ++j)
        {
            worldObj.spawnParticle(EnumParticleTypes.SNOWBALL, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        }

        if (!worldObj.isRemote)
        {
            setDead();
        }
    }
}