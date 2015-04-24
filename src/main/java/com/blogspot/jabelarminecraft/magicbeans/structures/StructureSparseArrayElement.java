/**
    Copyright (C) 2015 by jabelar

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

package com.blogspot.jabelarminecraft.magicbeans.structures;

import net.minecraft.block.Block;

/**
 * @author jabelar
 *
 */
public class StructureSparseArrayElement
{
    public Block theBlock;
    public int theMetaData;
    public int posX;
    public int posY;
    public int posZ;

    public StructureSparseArrayElement(Block parBlock, int parMetaData, int parX, int parY, int parZ)
    {
        theBlock = parBlock;
        theMetaData = parMetaData;
        posX = parX;
        posY = parY;
        posZ = parZ;
    }
}
