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

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
// import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class RenderGoldenEggThrown extends Render
{
//    private final Item itemBasisForEntity;
//    private final int iconIndex;

    public RenderGoldenEggThrown(RenderManager parRenderManager, Item par1Item, int parIconIndex)
    {
    	super(parRenderManager);
//        itemBasisForEntity = par1Item;
//        iconIndex = parIconIndex;
    }

    public RenderGoldenEggThrown(RenderManager parRenderManager, Item par1Item)
    {
        this(parRenderManager, par1Item, 0);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
	public void doRender(Entity parEntity, double parX, double parY, double parZ, float parIgnored1, float parIgnored2)
    {
//        IIcon iicon = itemBasisForEntity.getIconFromDamage(iconIndex);
//        
//        if (iicon != null)
//        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)parX, (float)parY, (float)parZ);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            int l = 0xF5E16F;
            float f5 = (l >> 16 & 255) / 255.0F;
            float f6 = (l >> 8 & 255) / 255.0F;
            float f7 = (l & 255) / 255.0F;
            GL11.glColor4f(f5, f6, f7, 1.0F);
            bindEntityTexture(parEntity);
            Tessellator tessellator = Tessellator.getInstance();

//            if (iicon == ItemPotion.func_94589_d("bottle_splash"))
//            {
//                int i = PotionHelper.func_77915_a(((EntityPotion)parEntity).getPotionDamage(), false);
//                float f2 = (i >> 16 & 255) / 255.0F;
//                float f3 = (i >> 8 & 255) / 255.0F;
//                float f4 = (i & 255) / 255.0F;
//                GL11.glColor3f(f2, f3, f4);
//                GL11.glPushMatrix();
//                invokeTesselator(tessellator, ItemPotion.func_94589_d("overlay"));
//                GL11.glPopMatrix();
//                GL11.glColor3f(1.0F, 1.0F, 1.0F);
//            }

//            invokeTesselator(tessellator, iicon);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
//        }
    }

	/* (non-Javadoc)
	 * @see net.minecraft.client.renderer.entity.Render#getEntityTexture(net.minecraft.entity.Entity)
	 */
	@Override
	protected ResourceLocation getEntityTexture(Entity parEntity) 
	{
		return null;
	}

//    /**
//     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
//     */
//    @Override
//	protected ResourceLocation getEntityTexture(Entity par1Entity)
//    {
//        return TextureMap.locationItemsTexture;
//    }

//    private void invokeTesselator(Tessellator par1Tessellator, IIcon par2Icon)
//    {
//        float minU = par2Icon.getMinU();
//        float maxU = par2Icon.getMaxU();
//        float minV = par2Icon.getMinV();
//        float maxV = par2Icon.getMaxV();
//        float f4 = 1.0F;
//        float f5 = 0.5F;
//        float f6 = 0.25F;
//        GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
//        GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
//        par1Tessellator.startDrawingQuads();
//        par1Tessellator.setNormal(0.0F, 1.0F, 0.0F);
//        par1Tessellator.addVertexWithUV(0.0F - f5, 0.0F - f6, 0.0D, minU, maxV);
//        par1Tessellator.addVertexWithUV(f4 - f5, 0.0F - f6, 0.0D, maxU, maxV);
//        par1Tessellator.addVertexWithUV(f4 - f5, f4 - f6, 0.0D, maxU, minV);
//        par1Tessellator.addVertexWithUV(0.0F - f5, f4 - f6, 0.0D, minU, minV);
//        par1Tessellator.draw();
//    }
}