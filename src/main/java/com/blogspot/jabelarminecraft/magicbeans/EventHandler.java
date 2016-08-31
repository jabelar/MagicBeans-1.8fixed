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

package com.blogspot.jabelarminecraft.magicbeans;

import com.blogspot.jabelarminecraft.magicbeans.entities.EntityFamilyCow;
import com.blogspot.jabelarminecraft.magicbeans.gui.GuiConfig;
import com.blogspot.jabelarminecraft.magicbeans.utilities.Utilities;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ForgeChunkManager.ForceChunkEvent;
import net.minecraftforge.common.ForgeChunkManager.UnforceChunkEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.living.ZombieEvent;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerRegisterEvent;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidEvent.FluidDrainingEvent;
import net.minecraftforge.fluids.FluidEvent.FluidFillingEvent;
import net.minecraftforge.fluids.FluidEvent.FluidMotionEvent;
import net.minecraftforge.fluids.FluidEvent.FluidSpilledEvent;
import net.minecraftforge.fluids.FluidRegistry.FluidRegisterEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class EventHandler 
{
    /*
     * Miscellaneous events
     */    

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ForceChunkEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(UnforceChunkEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(AnvilUpdateEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(CommandEvent event)
    {
        // DEBUG
        System.out.println("Your wish is my command");
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ServerChatEvent event)
    {
        
    }
    
    /*
     * Brewing events
     */
        
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(PotionBrewEvent event)
    {
        
    }
    
    /*
     * Entity related events
     */
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EnteringChunk event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EntityConstructing event)
    {
//        // Register extended entity properties
//
//        if (event.getEntity() instanceof IExtendedEntityProperties)
//        {
//            // DEBUG
//            System.out.println("EntityConstructing registering IEntityMagicBeans extended properties");
////            ((IEntityMagicBeans)event.getEntity()).initExtProps();
////            event.getEntity().registerExtendedProperties(MagicBeans.EXT_PROPS_NAME, (IExtendedEntityProperties) event.getEntity());
//        }
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EntityJoinWorldEvent event)
    {
    	World world = event.getWorld();
    	if (world.isRemote)
    	{
    		return;
    	}
    	
    	Entity theEntity = event.getEntity();
    	if (theEntity instanceof EntityCreeper)
    	{
//    		// DEBUG
//    		System.out.println("EntityJoinWorldEvent creeper at height = "+theEntity.posY);
    		if (theEntity.posZ > MagicBeans.configMaxStalkHeight)
    		{
    			// assumes that must be in castle at this height
    			event.setCanceled(true); // creepers would wreck castle
    			return;
    		}
    	}
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EntityStruckByLightningEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(PlaySoundAtEntityEvent event)
    {
        
    }

    /*
     * Item events (these extend EntityEvent)
     */
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ItemExpireEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ItemTossEvent event)
    {
        
    }
    
    /*
     * Living events (extend EntityEvent)
     */

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(LivingJumpEvent event)
    {

    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(LivingUpdateEvent event)
    {
        // This event has an Entity variable, access it like this: event.getEntity();
        // and can check if for player with if (event.getEntity() instanceof EntityPlayer)
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EnderTeleportEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(LivingAttackEvent event)
    {

    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(LivingDeathEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(LivingDropsEvent event)
    {

    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(LivingFallEvent event)
    {
    	if (!event.getEntityLiving().worldObj.isRemote && event.getEntityLiving() instanceof EntityPlayer)
    	{    		
	    	EntityPlayer thePlayer = (EntityPlayer) event.getEntityLiving();
	    	boolean isWearingBootsOfSafeFalling = thePlayer.getItemStackFromSlot(EntityEquipmentSlot.FEET) != null && thePlayer.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == MagicBeans.bootsOfSafeFalling;
	    	// boolean isWearingLeggingsOfSafeFalling = thePlayer.getCurrentArmor(1) != null && thePlayer.getCurrentArmor(1).getItem() == MagicBeans.leggingsOfSafeFalling;
	    	// boolean isWearingChestPlateOfSafeFalling = thePlayer.getCurrentArmor(2) != null && thePlayer.getCurrentArmor(2).getItem() == MagicBeans.chestplateOfSafeFalling;
	    	// boolean isWearingHelmetOfSafeFalling = thePlayer.getCurrentArmor(3) != null && thePlayer.getCurrentArmor(3).getItem() == MagicBeans.helmetOfSafeFalling;
	    	
			if (isWearingBootsOfSafeFalling ) // || isWearingLeggingsOfSafeFalling || isWearingChestPlateOfSafeFalling || isWearingHelmetOfSafeFalling)
			{
				// DEBUG
				System.out.println("LivingFallEvent handled due to having safe falling armor equipped");
				event.setDistance(0.0F) ;
			}  
    	}
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(LivingHurtEvent event)
    {

    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(LivingPackSizeEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(LivingSetAttackTargetEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ZombieEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(CheckSpawn event)
    {  	

    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(SpecialSpawn event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(AllowDespawn event)
    {
        
    }
    
    /*
     * Player events (extend LivingEvent)
     */
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(BreakSpeed event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(Clone event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(HarvestCheck event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(NameFormat event)
    {
        if (event.getUsername().equalsIgnoreCase("jnaejnae"))
        {
            event.setDisplayname(event.getUsername()+" the Great and Powerful");
        }        
        else if (event.getUsername().equalsIgnoreCase("MistMaestro"))
        {
            event.setDisplayname(event.getUsername()+" the Wise");
        }    
        else if (event.getUsername().equalsIgnoreCase("taliaailat"))
        {
            event.setDisplayname(event.getUsername()+" the Beautiful");
        }    
        else
        {
            event.setDisplayname(event.getUsername()+" the Ugly");            
        }
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ArrowLooseEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ArrowNockEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(AttackEntityEvent event)
    {

    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(BonemealEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EntityInteractSpecific event)
    {
		World world = event.getWorld();
		if (world.isRemote)
		{
		    // DEBUG
	        System.out.println("EntityInteractSpecific event on client side");

			return;
		}
		
    	// DEBUG
		System.out.println("EntityInteractSpecific entity event on server side");
        
        if (event.getHand() == EnumHand.OFF_HAND)
        {
            // DEBUG
            System.out.println("Interacting with off hand");
            return;
        }
        
        // DEBUG
        System.out.println("Interacting with main hand");
    	
    	Entity theEntity = event.getTarget();

        if (theEntity instanceof EntityCow && !(theEntity instanceof EntityFamilyCow))
        {
        	// DEBUG
        	System.out.println("Interacting with cow");
        	ItemStack theItemStack = event.getEntityPlayer().getHeldItemMainhand();
        	if (theItemStack != null)
        	{
	        	if (theItemStack.getItem() == Items.GOLDEN_CARROT)
	        	{
	        		// DEBUG
	        		// System.out.println("While holding a golden carrot");
	        		if (!ModWorldData.get(world).getHasCastleSpawned())
	        		{
	        			// DEBUG
	        			// System.out.println("Haven't spawned castle yet so okay to make a family cow");
	        				        	    		
        	    		EntityPlayer thePlayer = event.getEntityPlayer();
    	    		
        	    		if (!((EntityCow) theEntity).isChild())
        	    		{
		    	    		thePlayer.addChatMessage(new TextComponentString(Utilities.stringToRainbow("This cow is now your Family Cow!")));
			        
		    	    		EntityFamilyCow entityToSpawn = new EntityFamilyCow(world);
			        		entityToSpawn.setLocationAndAngles(theEntity.posX, theEntity.posY, theEntity.posZ, 
			                    MathHelper.wrapDegrees(world.rand.nextFloat()
			                    * 360.0F), 0.0F);
			        		world.spawnEntityInWorld(entityToSpawn);
			        		
			        		theEntity.setDead();

			        		thePlayer.addStat(MagicBeans.achievementStartMagicBeans, 1);
        	    		}        	    	
	        		}
        		}      		
        	}
        }       
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EntityInteract event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EntityItemPickupEvent event)
    {

    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FillBucketEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ItemTooltipEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(PlayerDestroyItemEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(PlayerDropsEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(PlayerFlyableFallEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(PlayerInteractEvent event)
    {

    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(PlayerContainerEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(PlayerPickupXpEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(PlayerSleepInBedEvent event)
    {
        
    }
    
//    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//    public void onEvent(PlayerUseItemEvent.Finish event)
//    {
//        
//    }
//    
//    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//    public void onEvent(PlayerUseItemEvent.Start event)
//    {
//    	
//    }
//    
//    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//    public void onEvent(PlayerUseItemEvent.Stop event)
//    {
//        
//    }
//    
//    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//    public void onEvent(PlayerUseItemEvent.Tick event)
//    {
//        
//    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(UseHoeEvent event)
    {
        
    }
    
    /*
     * Minecart events (extends EntityEvent)
     */
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(MinecartCollisionEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(MinecartInteractEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(MinecartUpdateEvent event)
    {
        
    }
    
    /*
     * World events
     */

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(WorldEvent.Load event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(WorldEvent.PotentialSpawns event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(WorldEvent.Unload event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(BlockEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(BlockEvent.BreakEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(BlockEvent.HarvestDropsEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ChunkEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ChunkEvent.Save event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ChunkEvent.Unload event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ChunkDataEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ChunkDataEvent.Load event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ChunkDataEvent.Save event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ChunkWatchEvent event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ChunkWatchEvent.Watch event)
    {
        
    }
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ChunkWatchEvent.UnWatch event)
    {
        
    }


    /*
     * Client events
     */    

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(ClientChatReceivedEvent event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(DrawBlockHighlightEvent event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderFogEvent event)
    {
        
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FogDensity event)
    {
    	if(true) 
    	{
	        event.setDensity((float) Math.abs(Math.pow(((event.getEntity().posY-63)/(255-63)),4)));
	        event.setCanceled(true); // must be canceled to affect the fog density   		
    	}
    }

    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FogColors event)
    {

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FOVUpdateEvent event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(GuiOpenEvent event)
    {
        if (event.getGui() instanceof GuiOptions)
        {
        	event.setGui(new GuiConfig(null));        
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(GuiScreenEvent.ActionPerformedEvent event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(GuiScreenEvent.DrawScreenEvent event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(GuiScreenEvent.InitGuiEvent event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(MouseEvent event)
    {

    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderGameOverlayEvent event)
    {
        
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderGameOverlayEvent.Chat event)
    {
    	// This event actually extends Pre

    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderGameOverlayEvent.Post event)
    {
        
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderGameOverlayEvent.Pre event)
    {
    	// you can check which elements of the GUI are being rendered
    	// by checking event.type against things like ElementType.CHAT, ElementType.CROSSHAIRS, etc.
    	// Note that ElementType.All is fired first apparently, then individual elements
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderGameOverlayEvent.Text event)
    {
    	// This event actually extends Pre
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderHandEvent event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderLivingEvent.Post event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderLivingEvent.Pre event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderPlayerEvent.Post event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderPlayerEvent.Pre event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderPlayerEvent.SetArmorModel event)
    {
        
    }

//    @SideOnly(Side.CLIENT)
//    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//    public void onEvent(RenderWorldEvent.Post event)
//    {
//        
//    }
//
//    @SideOnly(Side.CLIENT)
//    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//    public void onEvent(RenderWorldEvent.Pre event)
//    {
//        
//    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(RenderWorldLastEvent event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(TextureStitchEvent.Post event)
    {
        
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(TextureStitchEvent.Pre event)
    {
        
    }
    
    /*
     * Fluid events
     */

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FluidEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FluidContainerRegisterEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FluidDrainingEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FluidFillingEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FluidMotionEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FluidRegisterEvent event)
    {
        
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FluidSpilledEvent event)
    {
        
    }

    /*
     * Ore dictionary events
     */

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(OreRegisterEvent event)
    {
        
    }
    
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PopulateChunkEvent event)
	{
		
	}
	
	// for some reason the PopulateChunkEvents are fired on the main EVENT_BUT
	// even though they are in the terraingen package
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PopulateChunkEvent.Populate event)
	{
		
	}
	
	// for some reason the PopulateChunkEvents are fired on the main EVENT_BUT
	// even though they are in the terraingen package
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PopulateChunkEvent.Post event)
	{ 
		
	}
	
	// for some reason the PopulateChunkEvents are fired on the main EVENT_BUT
	// even though they are in the terraingen package
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PopulateChunkEvent.Pre event)
	{
	}
}

