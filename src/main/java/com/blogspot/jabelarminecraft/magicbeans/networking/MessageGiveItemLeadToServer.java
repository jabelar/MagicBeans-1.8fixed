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

package com.blogspot.jabelarminecraft.magicbeans.networking;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.ModWorldData;
import com.blogspot.jabelarminecraft.magicbeans.utilities.Utilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author jabelar
 *
 */
public class MessageGiveItemLeadToServer implements IMessage 
{
    
    public MessageGiveItemLeadToServer() 
    { 
    	// need this constructor
    }

    @Override
    public void fromBytes(ByteBuf buf) 
    {
    	// DEBUG
    	System.out.println("fromBytes");
    }

    @Override
    public void toBytes(ByteBuf buf) 
    {
    	// DEBUG
    	System.out.println("toBytes");
    }

    public static class Handler implements IMessageHandler<MessageGiveItemLeadToServer, IMessage> 
    {       
        @Override
        public IMessage onMessage(MessageGiveItemLeadToServer message, MessageContext ctx) 
        {
            // Know it will be on the server so make it thread-safe
            final EntityPlayerMP thePlayer = (EntityPlayerMP) MagicBeans.proxy.getPlayerEntityFromContext(ctx);
            thePlayer.getServer().addScheduledTask(
                    new Runnable()
                    {
                        @Override
                        public void run() 
                        {
                            if (ModWorldData.get(thePlayer.worldObj).getFamilyCowHasGivenLead())
                            {
                                // DEBUG
                                System.out.println("Player already got one free lead, so not giving another");
                                return;
                            }
                            if (thePlayer.inventory.getFirstEmptyStack() != -1) // check for room in inventory
                            {
                                thePlayer.inventory.addItemStackToInventory(new ItemStack(Items.LEAD, 1));
                                ModWorldData.get(thePlayer.worldObj).setFamilyCowHasGivenLead(true);
                            }
                            else if (!thePlayer.inventory.hasItemStack(new ItemStack(Items.LEAD))) // full but doesn't already have a lead              
                            {
                                thePlayer.addChatMessage(new TextComponentString("Your inventory is full!  Interact again with the "
                                        +Utilities.stringToRainbow("Family Cow")
                                        +" later when you have room in your inventory to get a lead."));
                            }
                            return; 
                        }
                }
            );
            return null; // no response in this case
        }
    }
 }
