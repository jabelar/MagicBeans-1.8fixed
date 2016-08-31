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

package com.blogspot.jabelarminecraft.magicbeans.gui;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.utilities.Utilities;
/**
 * @author jabelar
 *
 */
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiMessageDialog;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig 
{
    public GuiConfig(GuiScreen parent) 
    {
        super(parent,
                new ConfigElement(MagicBeans.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                MagicBeans.MODID, 
                false, 
                false, 
                Utilities.stringToGolden("Play Magic Beans Any Way You Want", 13));
    	titleLine2 = MagicBeans.configFile.getAbsolutePath();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	title = Utilities.stringToGolden("Play Magic Beans Any Way You Want", 13);
    	super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 2000)
        {
        	// DEBUG
        	System.out.println("Pressed DONE button");
            boolean flag = true;
            try
            {
                if ((configID != null || parentScreen == null || !(parentScreen instanceof GuiConfig)) 
                        && (entryList.hasChangedEntry(true)))
                {
                	// DEBUG
                	System.out.println("Saving config elements");
                    boolean requiresMcRestart = entryList.saveConfigElements();

                    if (Loader.isModLoaded(modID))
                    {
                        ConfigChangedEvent event = new OnConfigChangedEvent(modID, configID, isWorldRunning, requiresMcRestart);
                        FMLCommonHandler.instance().bus().post(event);
                        if (!event.getResult().equals(Result.DENY))
                            FMLCommonHandler.instance().bus().post(new PostConfigChangedEvent(modID, configID, isWorldRunning, requiresMcRestart));
                        
                        if (requiresMcRestart)
                        {
                            flag = false;
                            mc.displayGuiScreen(new GuiMessageDialog(parentScreen, "fml.configgui.gameRestartTitle", 
                                    new TextComponentString(I18n.format("fml.configgui.gameRestartRequired")), "fml.configgui.confirmRestartMessage"));
                        }
                        
                        if (parentScreen instanceof GuiConfig)
                            ((GuiConfig) parentScreen).needsRefresh = true;
                    }
                }
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            
            if (flag)
                mc.displayGuiScreen(parentScreen);
        }
    }    
}