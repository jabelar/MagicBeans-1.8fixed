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

package com.blogspot.jabelarminecraft.magicbeans.renderers;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.entities.EntityGiant;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * @author jabelar
 *
 */
@SideOnly(Side.CLIENT)
public class RenderGiant extends RenderLiving
{
    protected ResourceLocation giantTexture;
    protected float[] cycleDeathFall = new float[] 
    		{  0F , -2F , -5F , -10F , -15F, -12F, -10F, -5F , -2F ,  0F ,  2F ,  4F ,  6F,  8F, 
    		  11F , 15F , 20F , 35F , 55F , 80F , 90F };

    public RenderGiant(RenderManager parRenderManager, ModelBase par1ModelBase, float parShadowSize)
    {
        super(parRenderManager, par1ModelBase, parShadowSize);
        setEntityTexture();        
    }
 
    @Override
    protected void preRenderCallback(EntityLivingBase entity, float f)
    {
        preRenderCallbackGiant((EntityGiant) entity, f);
    }
  
    protected void preRenderCallbackGiant(EntityGiant entity, float f)
    {    	
        // some people do some G11 transformations or blends here, like you can do
        // GL11.glScalef(2F, 2F, 2F); to scale up the entity
        // which is used for Slime entities.  I suggest having the entity cast to
        // your custom type to make it easier to access fields from your 
        // custom entity, eg. GL11.glScalef(entity.scaleFactor, entity.scaleFactor, 
        // entity.scaleFactor); 
    }

    protected void setEntityTexture()
    {
        giantTexture = new ResourceLocation(MagicBeans.MODID+":textures/entities/giant.png");
    }

    /**
    * Returns the location of an entity's texture. Doesn't seem to be called 
    * unless you call Render.bindEntityTexture.
    */
    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return giantTexture;
    }
    
    @Override
	protected void rotateCorpse(EntityLivingBase parEntityLivingBase, float parNotUsed, float p_77043_3_, float p_77043_4_)
    {
        GL11.glRotatef(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);

        if (parEntityLivingBase.deathTime > 0)
        {
            GL11.glRotatef(cycleDeathFall[parEntityLivingBase.deathTime], 0.0F, 0.0F, 1.0F);
        }
    }
 }
