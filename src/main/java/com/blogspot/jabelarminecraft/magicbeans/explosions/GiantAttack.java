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

package com.blogspot.jabelarminecraft.magicbeans.explosions;

import java.util.List;

import com.blogspot.jabelarminecraft.magicbeans.entities.EntityGiant;
import com.blogspot.jabelarminecraft.magicbeans.entities.EntityGoldenGoose;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * @author jabelar
 *
 */
public class GiantAttack 
{
    protected static final SoundEvent SOUND_EFFECT_ATTACK = new SoundEvent(new ResourceLocation("random.explode"));
    
    protected final World worldObj;
    public double attackOriginX;
    public double attackOriginY;
    public double attackOriginZ;
    public EntityGiant theGiant;
    public float attackRange;

    public GiantAttack(EntityGiant parEntity, float parAttackRange)
    {
        theGiant = parEntity;
        worldObj = theGiant.worldObj;
        attackRange = parAttackRange;
        attackOriginX = theGiant.posX;
        attackOriginY = theGiant.posY;
        attackOriginZ = theGiant.posZ;
    }

    public void doGiantAttack(int parMaxDamage)
    {
    	if (worldObj.isRemote)
    	{
    		// DEBUG
    		System.out.println("doGiantAttack on client side");
    		return;
    	}
    	
    	// DEBUG
    	System.out.println("Giant special attack!");
    	
        double rangeFactor;
        double knockbackFactorX;
        double knockbackFactorY;
        double knockbackFactorZ;
        double knockbackMagnitude;
        
        attackOriginX = theGiant.posX;
        attackOriginY = theGiant.posY;
        attackOriginZ = theGiant.posZ;

        int minX = MathHelper.floor_double(attackOriginX - attackRange - 1.0D);
        int maxX = MathHelper.floor_double(attackOriginX + attackRange + 1.0D);
        int minY = MathHelper.floor_double(attackOriginY - attackRange - 1.0D);
        int maxY = MathHelper.floor_double(attackOriginY + attackRange + 1.0D);
        int minZ = MathHelper.floor_double(attackOriginZ - attackRange - 1.0D);
        int maxZ = MathHelper.floor_double(attackOriginZ + attackRange + 1.0D);
        
        // DEBUG
        System.out.println("Attack region = "+minX+", "+minY+", "+minZ+" to "+maxX+", "+maxY+", "+maxZ);
        
        List entityList = worldObj.getEntitiesWithinAABBExcludingEntity(theGiant, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
        Vec3d vec3 = new Vec3d(attackOriginX, attackOriginY, attackOriginZ);

        // DEBUG
        System.out.println("Found "+entityList.size()+" entities in attack range");
        
        for (int indX = 0; indX < entityList.size(); ++indX)
        {
            Entity theEntity = (Entity)entityList.get(indX);
            rangeFactor = theEntity.getDistance(attackOriginX, attackOriginY, attackOriginZ) / attackRange;

            // DEBUG
            System.out.println("Distance from attack = "+rangeFactor);
            
           if (rangeFactor <= 1.0D) // within range
            {
                knockbackFactorX = theEntity.posX - attackOriginX;
                knockbackFactorY = theEntity.posY + theEntity.getEyeHeight() - attackOriginY;
                knockbackFactorZ = theEntity.posZ - attackOriginZ;
                knockbackMagnitude = MathHelper.sqrt_double(knockbackFactorX * knockbackFactorX + knockbackFactorY * knockbackFactorY + knockbackFactorZ * knockbackFactorZ);

                if (knockbackMagnitude != 0.0D)
                {
                	
                	// knock back
                    knockbackFactorX /= knockbackMagnitude;
                    knockbackFactorY /= knockbackMagnitude;
                    knockbackFactorZ /= knockbackMagnitude;
                    double protectionFromBlocks = worldObj.getBlockDensity(vec3, theEntity.getEntityBoundingBox());
                    theEntity.motionX += knockbackFactorX / 2;
                    theEntity.motionY += knockbackFactorY / 2;
                    theEntity.motionZ += knockbackFactorZ / 2;
                    if (!(theEntity instanceof EntityGoldenGoose) && !(theEntity instanceof EntityItem)) // don't damage golden goose or items
                    {
	                    double fadeOverDistance = (1.0D - rangeFactor) * protectionFromBlocks;
	                    theEntity.attackEntityFrom(DamageSource.causeMobDamage(theGiant), ((int)(1.0D + parMaxDamage * fadeOverDistance)));
	                	// DEBUG
	                	System.out.println("Hit entity with damage = "+((int)(1.0D + parMaxDamage * fadeOverDistance)));
                    }
                }
            }
        }
        worldObj.playSound(attackOriginX, attackOriginY, attackOriginZ, SOUND_EFFECT_ATTACK, SoundCategory.AMBIENT, 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F, true);
        worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, attackOriginX, attackOriginY, attackOriginZ, 1.0D, 0.0D, 0.0D, new int[0]);
    }

    /**
     * Returns either the entity that placed the explosive block, the entity that caused the explosion or null.
     */
    public EntityLivingBase getExplosivePlacedBy()
    {
        return theGiant;
    }
}