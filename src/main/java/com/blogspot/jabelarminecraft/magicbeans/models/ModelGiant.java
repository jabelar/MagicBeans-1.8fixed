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

package com.blogspot.jabelarminecraft.magicbeans.models;

import com.blogspot.jabelarminecraft.magicbeans.entities.EntityGiant;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * @author jabelar
 *
 */
public class ModelGiant extends ModelBiped
{
    public ModelRenderer modelNose;
    
    protected int cycleIndex = 0;
    protected float[][] cycleSpecialAttack = new float[][] {
    		{  0F,   0F}, // right arm, left arm
    		{ 15F, -15F},
    		{ 30F, -30F},
    		{ 45F, -45F},
    		{ 60F, -60F},
    		{ 75F, -75F},
    		{ 90F, -90F},
     		{ 105F, -105F},
    		{ 130F, -130F},
    		{ 130F, -130F},
    		{ 130F, -130F},
    		{ 130F, -130F},
    		{ 130F, -130F},
    		{ 130F, -130F},
    		{ 105F, -105F},
    		{ 90F, -90F},
    		{ 75F, -75F},
    		{ 60F, -60F},
    		{ 45F, -45F},
    		{ 30F, -30F},
    		{ 15F, -15F},
    };

    public ModelGiant()
    {
        this(0.0F);
    }

    public ModelGiant(float parScaleFactor)
    {
        super(parScaleFactor, 0.0F, 64, 32);
        // DEBUG
        System.out.println("ModelGiant() constructor");
        modelNose = new ModelRenderer(this, 24, 0);
        modelNose.addBox(-1.0F, -3.5F, -6.0F, 2, 4, 2, parScaleFactor);
        modelNose.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHead.addChild(modelNose);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
	public void render(Entity parEntity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
    	renderGiant((EntityGiant)parEntity, par2, par3, par4, par5, par6, par7);
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
    public void renderGiant(EntityGiant parEntity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        setRotationAngles(parEntity, par2, par3, par4, par5, par6, par7, parEntity);

        // scale the whole thing for big or small entities
        GL11.glPushMatrix();
        GL11.glTranslatef(0F, 1.5F-1.5F*parEntity.getScaleFactor(), 0F); 
    	GL11.glScalef(parEntity.getScaleFactor(), parEntity.getScaleFactor(), parEntity.getScaleFactor());

        bipedHead.render(par7);
        bipedBody.render(par7);
        bipedRightArm.render(par7);
        bipedLeftArm.render(par7);
        bipedRightLeg.render(par7);
        bipedLeftLeg.render(par7);
        bipedHeadwear.render(par7);

        // don't forget to pop the matrix for overall scaling
        GL11.glPopMatrix();
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(EntityGiant parGiant, float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_)
    {
        bipedHead.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
        bipedHead.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
        bipedHeadwear.rotateAngleY = bipedHead.rotateAngleY;
        bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX;
        bipedRightArm.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 2.0F * p_78087_2_ * 0.5F;
        bipedLeftArm.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 2.0F * p_78087_2_ * 0.5F;
        bipedRightArm.rotateAngleZ = 0.0F;
        bipedLeftArm.rotateAngleZ = 0.0F;
        bipedRightLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
        bipedLeftLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_;
        bipedRightLeg.rotateAngleY = 0.0F;
        bipedLeftLeg.rotateAngleY = 0.0F;

        if (isRiding)
        {
            bipedRightArm.rotateAngleX += -((float)Math.PI / 5F);
            bipedLeftArm.rotateAngleX += -((float)Math.PI / 5F);
            bipedRightLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
            bipedLeftLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
            bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10F);
        }

            bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
            bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);

        bipedRightArm.rotateAngleY = 0.0F;
        bipedLeftArm.rotateAngleY = 0.0F;
        float f6;
        float f7;

        if (swingProgress > -9990.0F)
        {
            f6 = swingProgress;
            bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
            bipedRightArm.rotationPointZ = MathHelper.sin(bipedBody.rotateAngleY) * 5.0F;
            bipedRightArm.rotationPointX = -MathHelper.cos(bipedBody.rotateAngleY) * 5.0F;
            bipedLeftArm.rotationPointZ = -MathHelper.sin(bipedBody.rotateAngleY) * 5.0F;
            bipedLeftArm.rotationPointX = MathHelper.cos(bipedBody.rotateAngleY) * 5.0F;
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleX += bipedBody.rotateAngleY;
            f6 = 1.0F - swingProgress;
            f6 *= f6;
            f6 *= f6;
            f6 = 1.0F - f6;
            f7 = MathHelper.sin(f6 * (float)Math.PI);
            float f8 = MathHelper.sin(swingProgress * (float)Math.PI) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
            bipedRightArm.rotateAngleX = (float)(bipedRightArm.rotateAngleX - (f7 * 1.2D + f8));
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY * 2.0F;
            bipedRightArm.rotateAngleZ = MathHelper.sin(swingProgress * (float)Math.PI) * -0.4F;
        }
        else
        {
            bipedBody.rotateAngleX = 0.0F;
            bipedRightLeg.rotationPointZ = 0.1F;
            bipedLeftLeg.rotationPointZ = 0.1F;
            bipedRightLeg.rotationPointY = 12.0F;
            bipedLeftLeg.rotationPointY = 12.0F;
            bipedHead.rotationPointY = 0.0F;
            bipedHeadwear.rotationPointY = 0.0F;
        }

        bipedRightArm.rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;

//        if (aimedBow)
//        {
//            f6 = 0.0F;
//            f7 = 0.0F;
//            bipedRightArm.rotateAngleZ = 0.0F;
//            bipedLeftArm.rotateAngleZ = 0.0F;
//            bipedRightArm.rotateAngleY = -(0.1F - f6 * 0.6F) + bipedHead.rotateAngleY;
//            bipedLeftArm.rotateAngleY = 0.1F - f6 * 0.6F + bipedHead.rotateAngleY + 0.4F;
//            bipedRightArm.rotateAngleX = -((float)Math.PI / 2F) + bipedHead.rotateAngleX;
//            bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F) + bipedHead.rotateAngleX;
//            bipedRightArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
//            bipedLeftArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
//            bipedRightArm.rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
//            bipedLeftArm.rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
//            bipedRightArm.rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
//            bipedLeftArm.rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
//        }
        
        if (parGiant.getSpecialAttackTimer() > 0)
        {
//        	// DEBUG
//        	System.out.println("Rendering during special attack with attack timer = "+parGiant.getSpecialAttackTimer());
        	bipedRightArm.rotateAngleZ = degToRad(cycleSpecialAttack[parGiant.getSpecialAttackTimer()][0]);
        	bipedLeftArm.rotateAngleZ = degToRad(cycleSpecialAttack[parGiant.getSpecialAttackTimer()][1]);
        	bipedRightArm.rotateAngleX = -degToRad(cycleSpecialAttack[parGiant.getSpecialAttackTimer()][0])/2.0F;
        	bipedLeftArm.rotateAngleX = degToRad(cycleSpecialAttack[parGiant.getSpecialAttackTimer()][1])/2.0F;
        }
    }
    
    protected float degToRad(float degrees)
    {
        return degrees * (float)Math.PI / 180 ;
    }
}
