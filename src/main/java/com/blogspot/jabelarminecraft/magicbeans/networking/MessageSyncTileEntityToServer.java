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

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.blogspot.jabelarminecraft.magicbeans.tileentities.TileEntityMagicBeanStalk;

/**
 * @author jabelar
 *
 */
public class MessageSyncTileEntityToServer implements IMessage 
{
    
    private TileEntityMagicBeanStalk theTileEntity;

    public MessageSyncTileEntityToServer() 
    { 
    	// need this constructor
    }

    public MessageSyncTileEntityToServer(TileEntityMagicBeanStalk parTileEntity) 
    {
        theTileEntity = parTileEntity;
        // DEBUG
        System.out.println("MyMessage constructor");
    }

    @Override
    public void fromBytes(ByteBuf buf) 
    {
//        text = ByteBufUtils.readUTF8String(buf); // this class is very useful in general for writing more complex objects
    	// DEBUG
//    	System.out.println("fromBytes = "+text);
    }

    @Override
    public void toBytes(ByteBuf buf) 
    {
//        ByteBufUtils.writeUTF8String(buf, text);
//        ByteBufUtils.writeTag(buf, theTileEntity.;);
        // DEBUG
        System.out.println("toBytes encoded");
    }

    public static class Handler implements IMessageHandler<MessageSyncTileEntityToServer, IMessage> 
    {
        @Override
        public IMessage onMessage(MessageSyncTileEntityToServer message, MessageContext ctx) 
        {
//            System.out.println(String.format("Received %s from %s", message.text, MagicBeans.proxy.getPlayerEntityFromContext(ctx).getDisplayName()));
            return null; // no response in this case
        }
    }
}
