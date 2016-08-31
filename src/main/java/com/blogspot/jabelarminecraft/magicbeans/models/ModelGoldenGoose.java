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

package com.blogspot.jabelarminecraft.magicbeans.models;

import com.blogspot.jabelarminecraft.magicbeans.entities.EntityGoldenGoose;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelGoldenGoose extends ModelBase
{
    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer rightLeg;
    public ModelRenderer leftLeg;
    public ModelRenderer rightWing;
    public ModelRenderer leftWing;
    public ModelRenderer bill;
    public ModelRenderer chin;

    public ModelGoldenGoose()
    {
        byte b0 = 16;
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
        head.setRotationPoint(0.0F, -1 + b0, -4.0F);
        bill = new ModelRenderer(this, 14, 0);
        bill.addBox(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
        bill.setRotationPoint(0.0F, -1 + b0, -4.0F);
        chin = new ModelRenderer(this, 14, 4);
        chin.addBox(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
        chin.setRotationPoint(0.0F, -1 + b0, -4.0F);
        body = new ModelRenderer(this, 0, 9);
        body.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
        body.setRotationPoint(0.0F, b0, 0.0F);
        rightLeg = new ModelRenderer(this, 26, 0);
        rightLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
        rightLeg.setRotationPoint(-2.0F, 3 + b0, 1.0F);
        leftLeg = new ModelRenderer(this, 26, 0);
        leftLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
        leftLeg.setRotationPoint(1.0F, 3 + b0, 1.0F);
        rightWing = new ModelRenderer(this, 24, 13);
        rightWing.addBox(0.0F, 0.0F, -3.0F, 1, 4, 6);
        rightWing.setRotationPoint(-4.0F, -3 + b0, 0.0F);
        leftWing = new ModelRenderer(this, 24, 13);
        leftWing.addBox(-1.0F, 0.0F, -3.0F, 1, 4, 6);
        leftWing.setRotationPoint(4.0F, -3 + b0, 0.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
	public void render(Entity parEntity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
    	renderGoldenGoose((EntityGoldenGoose)parEntity, par2, par3, par4, par5, par6, par7);
    }
    
    /**
     * Renders specific entity to allow access to any specific fields
     * @param parEntity
     * @param par2
     * @param par3
     * @param par4
     * @param par5
     * @param par6
     * @param par7
     */
    public void renderGoldenGoose(EntityGoldenGoose parEntity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        setRotationAngles(par2, par3, par4, par5, par6, par7, parEntity);

        // scale the whole thing for big or small entities
        GL11.glPushMatrix();
        GL11.glTranslatef(0F, 1.5F-1.5F*parEntity.getScaleFactor(), 0F); 
    	GL11.glScalef(parEntity.getScaleFactor(), parEntity.getScaleFactor(), parEntity.getScaleFactor());

        if (isChild)
        {
            float f6 = 2.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 5.0F * par7, 2.0F * par7);
            head.render(par7);
            bill.render(par7);
            chin.render(par7);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * par7, 0.0F);
            body.render(par7);
            rightLeg.render(par7);
            leftLeg.render(par7);
            rightWing.render(par7);
            leftWing.render(par7);
            GL11.glPopMatrix();
        }
        else
        {
            head.render(par7);
            bill.render(par7);
            chin.render(par7);
            body.render(par7);
            rightLeg.render(par7);
            leftLeg.render(par7);
            rightWing.render(par7);
            leftWing.render(par7);
        }

        // don't forget to pop the matrix for overall scaling
        GL11.glPopMatrix();
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    @Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
    {
        head.rotateAngleX = par5 / (180F / (float)Math.PI);
        head.rotateAngleY = par4 / (180F / (float)Math.PI);
        bill.rotateAngleX = head.rotateAngleX;
        bill.rotateAngleY = head.rotateAngleY;
        chin.rotateAngleX = head.rotateAngleX;
        chin.rotateAngleY = head.rotateAngleY;
        body.rotateAngleX = ((float)Math.PI / 2F);
        rightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        leftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.4F * par2;
        rightWing.rotateAngleZ = par3;
        leftWing.rotateAngleZ = -par3;
    }
}