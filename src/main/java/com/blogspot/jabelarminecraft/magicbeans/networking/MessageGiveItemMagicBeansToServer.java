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
import com.blogspot.jabelarminecraft.magicbeans.entities.EntityFamilyCow;
import com.blogspot.jabelarminecraft.magicbeans.utilities.Utilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author jabelar
 *
 */
public class MessageGiveItemMagicBeansToServer implements IMessage 
{
    
    private EntityFamilyCow entityFamilyCow;
    private static int entityID;

    public MessageGiveItemMagicBeansToServer() 
    { 
    	// need this constructor
    }

    public MessageGiveItemMagicBeansToServer(EntityFamilyCow parCowMagicBeans) 
    {
        entityFamilyCow = parCowMagicBeans;
        // DEBUG
        System.out.println("MessageGiveItemToServer constructor");
    }

    @Override
    public void fromBytes(ByteBuf buf) 
    {
    	entityID = ByteBufUtils.readVarInt(buf, 4);
    }

    @Override
    public void toBytes(ByteBuf buf) 
    {
    	entityID = entityFamilyCow.getEntityId();
    	ByteBufUtils.writeVarInt(buf, entityID, 4);
    }

    public static class Handler implements IMessageHandler<MessageGiveItemMagicBeansToServer, IMessage> 
    {
        
        @Override
        public IMessage onMessage(MessageGiveItemMagicBeansToServer message, MessageContext ctx) 
        {
            // Know it will be on the server so make it thread-safe
            final EntityPlayerMP thePlayer = (EntityPlayerMP) MagicBeans.proxy.getPlayerEntityFromContext(ctx);
            thePlayer.getServer().addScheduledTask(
                    new Runnable()
                    {
                        @Override
                        public void run() 
                        {
                            if (thePlayer.inventory.getFirstEmptyStack() != -1) // check for room in inventory
                            {
                                thePlayer.inventory.addItemStackToInventory(new ItemStack(MagicBeans.magicBeans, 1));
                                Entity theEntity = thePlayer.worldObj.getEntityByID(entityID);
                                theEntity.setDead();            
                            }
                            else
                            {
                                thePlayer.addChatMessage(new TextComponentString("Your inventory is full!  Come back for your "
                                        +Utilities.stringToRainbow("Magic Beans")+" later."));
                            }
                            return; 
                        }
                }
            );
            return null; // no response in this case
        }
    }
 }
