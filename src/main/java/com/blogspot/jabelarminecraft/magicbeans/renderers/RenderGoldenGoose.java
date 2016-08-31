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

package com.blogspot.jabelarminecraft.magicbeans.renderers;

import com.blogspot.jabelarminecraft.magicbeans.entities.EntityGoldenGoose;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGoldenGoose extends RenderLiving
{
    protected static final ResourceLocation goldenGooseTextures = new ResourceLocation("magicbeans:textures/entities/golden_goose.png");

    public RenderGoldenGoose(RenderManager parRenderManager, ModelBase par1ModelBase, float par2)
    {
        super(parRenderManager, par1ModelBase, par2);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(EntityGoldenGoose parEntityGoldenGoose, double par2, double par4, double par6, float par8, float par9)
    {
        super.doRender(parEntityGoldenGoose, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityGoldenGoose par1EntityGoldenGoose)
    {
        return goldenGooseTextures;
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float handleRotationFloat(EntityGoldenGoose par1EntityGoldenGoose, float par2)
    {
        float f1 = par1EntityGoldenGoose.rotationFloat3 + (par1EntityGoldenGoose.rotationFloat1 - par1EntityGoldenGoose.rotationFloat3) * par2;
        float f2 = par1EntityGoldenGoose.rotationFloat2 + (par1EntityGoldenGoose.destPos - par1EntityGoldenGoose.rotationFloat2) * par2;
        return (MathHelper.sin(f1) + 1.0F) * f2;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
	public void doRender(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityGoldenGoose)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    @Override
	protected float handleRotationFloat(EntityLivingBase par1EntityLivingBase, float par2)
    {
        return this.handleRotationFloat((EntityGoldenGoose)par1EntityLivingBase, par2);
    }
//
//    /**
//     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
//     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
//     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
//     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
//     */
//    @Override
//	public void doRender(EntityLivingBase par1Entity, double par2, double par4, double par6, float par8, float par9)
//    {
//        this.doRender((EntityGoldenGoose)par1Entity, par2, par4, par6, par8, par9);
//    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityGoldenGoose)par1Entity);
    }

//    /**
//     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
//     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
//     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
//     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
//     */
//    @Override
//	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
//    {
//        this.doRender((EntityGoldenGoose)par1Entity, par2, par4, par6, par8, par9);
//    }
}