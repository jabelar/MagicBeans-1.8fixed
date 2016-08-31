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

package com.blogspot.jabelarminecraft.magicbeans.particles;

import net.minecraft.client.particle.ParticleCrit;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author jabelar
 *
 */
@SideOnly(Side.CLIENT)
public class ParticleFXMysterious extends ParticleCrit
{

	/**
	 * @param parWorld
	 * @param parX
	 * @param parY
	 * @param parZ
	 * @param parMotionX
	 * @param parMotionY
	 * @param parMotionZ
	 */
	public ParticleFXMysterious(World parWorld,
			double parX, double parY, double parZ,
			double parMotionX, double parMotionY, double parMotionZ) 
	{
        super(parWorld, parX, parY, parZ, 0.0D, 0.0D, 0.0D);
		motionX = parMotionX;
		motionY = parMotionY;
		motionZ = parMotionZ;
		setParticleTextureIndex(82); // same as happy villager
		particleScale = 2.0F;
		setRBGColorF(0x88, 0x00, 0x88);
	}
	
	@Override
    public void onUpdate()
	    {
	        this.prevPosX = this.posX;
	        this.prevPosY = this.posY;
	        this.prevPosZ = this.posZ;

	        if (this.particleAge++ >= this.particleMaxAge)
	        {
	            this.setExpired();
	        }

	        this.motionY -= 0.04D * this.particleGravity;
	        this.moveEntity(this.motionX, this.motionY, this.motionZ);
	        this.motionX *= 0.9800000190734863D;
	        this.motionY *= 0.9800000190734863D;
	        this.motionZ *= 0.9800000190734863D;

	        if (this.isCollided)
	        {
	            this.motionX *= 0.699999988079071D;
	            this.motionZ *= 0.699999988079071D;
	        }
	    }
}
