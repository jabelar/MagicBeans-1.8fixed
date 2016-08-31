package com.blogspot.jabelarminecraft.magicbeans;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class ModWorldData extends WorldSavedData 
{

	private static final String IDENTIFIER = MagicBeans.MODID;
	
	private boolean hasCastleSpawned = false;
	private boolean familyCowHasGivenLead = false;
	
	public ModWorldData() 
	{
		this(IDENTIFIER);
	}
	
	public ModWorldData(String parIdentifier) 
	{
		super(parIdentifier);
		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		// DEBUG
		System.out.println("MagicBeansWorldData readFromNBT");
		
		hasCastleSpawned = nbt.getBoolean("hasCastleSpawned");
		familyCowHasGivenLead = nbt.getBoolean("familyCowHasGivenLead");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) 
	{
		// DEBUG
		System.out.println("MagicBeansWorldData writeToNBT");
		
		nbt.setBoolean("hasCastleSpawned", hasCastleSpawned);
		nbt.setBoolean("familyCowHasGivenLead", familyCowHasGivenLead);
		return nbt;
	}
	
	public boolean getHasCastleSpawned() 
	{
		return hasCastleSpawned;
	}
	
	public void setHasCastleSpawned(boolean parHasCastleSpawned) 
	{
		// DEBUG
		System.out.println("World Data setHasCastleSpawned = "+parHasCastleSpawned);
		if (!hasCastleSpawned) 
		{
			hasCastleSpawned = true;
			markDirty();
			// new PacketWorldData(this).sendToAll(); shouldn't need to send packet as this field is only used on server side
		}
	}
	
	public boolean getFamilyCowHasGivenLead() 
	{
		return familyCowHasGivenLead;
	}
	
	public void setFamilyCowHasGivenLead(boolean parFamilyCowHasGivenLead) 
	{
		// DEBUG
		System.out.println("World Data familyCowHasGivenLead = "+parFamilyCowHasGivenLead);
		if (!familyCowHasGivenLead) 
		{
			familyCowHasGivenLead = true;
			markDirty();
			// new PacketWorldData(this).sendToAll(); shouldn't need to send packet as this field is only used on server side
		}
	}
		
	public static ModWorldData get(World world) 
	{
		ModWorldData data = (ModWorldData)world.loadItemData(ModWorldData.class, IDENTIFIER);
		if (data == null) 
		{
			// DEBUG
			System.out.println("MagicBeansWorldData didn't exist so creating it");
			
			data = new ModWorldData();
			world.setItemData(IDENTIFIER, data);
		}
		return data;
	}
}