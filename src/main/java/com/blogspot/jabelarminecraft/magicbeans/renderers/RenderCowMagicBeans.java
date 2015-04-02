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

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.entities.EntityFamilyCow;

/**
 * @author jabelar
 *
 */
@SideOnly(Side.CLIENT)
public class RenderCowMagicBeans extends RenderLiving
{

	private final ResourceLocation[] cowMagicBeansTexture = new ResourceLocation[3];

	/**
	 * @param parModelBase
	 * @param parShadowSize
	 */
	public RenderCowMagicBeans(RenderManager parRenderManager, ModelBase parModelBase, float parShadowSize) 
	{
		super(parRenderManager, parModelBase, parShadowSize);
		setEntityTexture();
	}

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float f)
    {
        // preRenderCallbackCowMagicBeans((EntityCowMagicBeans) entity, f);
    }
  
    protected void preRenderCallbackCowMagicBeans(EntityFamilyCow entity, float f)
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
        cowMagicBeansTexture[0] = new ResourceLocation(MagicBeans.MODID+":textures/entities/cow_magic_beans_0.png");
        cowMagicBeansTexture[1] = new ResourceLocation(MagicBeans.MODID+":textures/entities/cow_magic_beans_1.png");
        cowMagicBeansTexture[2] = new ResourceLocation(MagicBeans.MODID+":textures/entities/cow_magic_beans_2.png");
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return cowMagicBeansTexture[MagicBeans.configCowTextureType];
    }
}
