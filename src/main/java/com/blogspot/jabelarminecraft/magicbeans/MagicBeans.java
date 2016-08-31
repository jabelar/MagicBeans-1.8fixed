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

import java.io.File;

import com.blogspot.jabelarminecraft.magicbeans.armor.ItemArmorSafeFalling;
import com.blogspot.jabelarminecraft.magicbeans.blocks.BlockCloud;
import com.blogspot.jabelarminecraft.magicbeans.blocks.BlockMagicBeanStalk;
import com.blogspot.jabelarminecraft.magicbeans.items.ItemGoldenEgg;
import com.blogspot.jabelarminecraft.magicbeans.items.ItemGoldenGooseMeat;
import com.blogspot.jabelarminecraft.magicbeans.items.ItemMagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.items.MagicBeansMonsterPlacer;
import com.blogspot.jabelarminecraft.magicbeans.materials.MaterialCloud;
import com.blogspot.jabelarminecraft.magicbeans.proxy.CommonProxy;
import com.blogspot.jabelarminecraft.magicbeans.structures.Structure;
import com.blogspot.jabelarminecraft.magicbeans.structures.StructureCastleJaden;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.stats.Achievement;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = 
      MagicBeans.MODID, 
      name = MagicBeans.MODNAME, 
      version = MagicBeans.MODVERSION,
      guiFactory = "com.blogspot.jabelarminecraft."+MagicBeans.MODID+".gui.GuiFactory")
public class MagicBeans
{
    public static final String MODID = "magicbeans";
    public static final String MODNAME = "Magic Beans";
    public static final String MODVERSION = "1.0.0";
    public static final String MODDESCRIPTION = "Start the adventure by feeding a golden carrot to a grown-up cow.";
    public static final String MODAUTHOR = "jabelar";
    public static final String MODCREDITS = "Taliaailat, Jnaejnae";
    public static final String MODURL = "www.jabelarminecraft.blogspot.com";
    public static final String MODLOGO = "jackandbeanstalk.png";

    // this is tag used for sub-compound in extended properties and packet syncing
	public final static String EXT_PROPS_NAME = "extendedPropertiesMagicBeans";
 
	// use a named channel to identify packets related to this mod
    public static final String NETWORK_CHANNEL_NAME = "MagicBeans";
	public static FMLEventChannel channel;
	// networking
	public static SimpleNetworkWrapper network;

    // set up configuration properties (will be read from config file in preInit)
    public static File configFile;
    public static Configuration config;
    public static int configGiantHealth = 10;
	public static boolean configGiantCanRegen = true;
    public static int configGiantAttackDamage = 4;
	public static int configMaxStalkHeight = 136;
	public static int configTicksPerGrowStage = 20;
	public static int configTimeUntilNextEgg = 900;
	public static int configCowTextureType = 0;
    
    // instantiate materials
    public final static Material materialCloud = new MaterialCloud();
    // see custom armor tutorial at: http://bedrockminer.jimdo.com/modding-tutorials/basic-modding/custom-armor/
    public final static ArmorMaterial SAFEFALLINGLEATHER = EnumHelper.addArmorMaterial("SAFEFALLINGLEATHER", "safe_falling", 5, new int[]{2, 6, 5, 2}, 15, null, 0F);
    
    // instantiate blocks
    // need to instantiate beanstalk block before item as the item constructor associates with block
    public final static Block blockMagicBeanStalk = new BlockMagicBeanStalk();
	public final static Block blockCloud = new BlockCloud();

    // instantiate items
	// important to do this after blocks where item is associate with custom block
    public final static ItemGoldenEgg itemGoldenEgg = new ItemGoldenEgg();
    public final static Item magicBeans = new ItemMagicBeans();
    // public final static ItemArmor bootsOfSafeFalling = (ItemArmor) new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 1, 3).setUnlocalizedName("bootsofsafefalling").setTextureName("minecraft:chainmail_boots");
    // public final static ItemArmor helmetOfSafeFalling = new ItemArmorSafeFalling("helmet_safe_falling", SAFEFALLINGLEATHER, "safe_falling", 0);
    // public final static ItemArmor chestplateOfSafeFalling = new ItemArmorSafeFalling("chestplate_safe_falling", SAFEFALLINGLEATHER, "safe_falling", 1);
    // public final static ItemArmor leggingsOfSafeFalling = new ItemArmorSafeFalling("leggings_safe_falling", SAFEFALLINGLEATHER, "safe_falling", 2);
    public final static ItemArmorSafeFalling bootsOfSafeFalling = (ItemArmorSafeFalling) new ItemArmorSafeFalling(SAFEFALLINGLEATHER, 3, EntityEquipmentSlot.FEET).setUnlocalizedName("boots_safe_falling");
    public final static ItemGoldenGooseMeat goldenGooseMeat = new ItemGoldenGooseMeat(4, 1.2F, false);
    
    public final static MagicBeansMonsterPlacer spawnFamilyCow = new MagicBeansMonsterPlacer("family_cow",  0x4EF56D, 0xFCFC03);
    public final static MagicBeansMonsterPlacer spawnGiant = new MagicBeansMonsterPlacer("giant", 0xDB9112, 0x0AC798);
    public final static MagicBeansMonsterPlacer spawnMysteriousStranger = new MagicBeansMonsterPlacer("mysterious_stranger", 0x8C6620, 0xA100B3);
    public final static MagicBeansMonsterPlacer spawnGoldenGoose = new MagicBeansMonsterPlacer("golden_goose", 0xFCF6A2, 0xF5E400);
   
    // instantiate structures
    // important to do this after blocks in case structure uses custom block
    public final static Structure structureCastle = new StructureCastleJaden();

    public static Achievement achievementStartMagicBeans;
    public static Achievement achievementGiantSlayer;

    // instantiate achievements
    
    
    // instantiate the mod
    @Instance(MODID)
    
    public static MagicBeans instance;
    
    // create custom creativetab for mod items
    //public static CreativeTabs tabMagicBeansPlus = new MagicBeansCreativeTab("MagicBeans");
    
    // instantiate blocks
    //public final static Block blockTomato = new BlockTomato();

    // instantiate items
    //public final static Item tomato = new ItemTomato();
    
    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="com.blogspot.jabelarminecraft.magicbeans.proxy.ClientProxy", serverSide="com.blogspot.jabelarminecraft.magicbeans.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    // Version checking instance
	public static VersionChecker versionChecker;
	public static boolean haveWarnedVersionOutOfDate = false;
            
    @EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the GameRegistry."
    public void fmlLifeCycleEvent(FMLPreInitializationEvent event) 
    {   	
        // DEBUG
        System.out.println("preInit()"+event.getModMetadata().name);
                
        // hard-code mod information so don't need mcmod.info file
        event.getModMetadata().autogenerated = false ; // stops it from complaining about missing mcmod.info
        event.getModMetadata().credits = TextFormatting.BLUE+MODCREDITS;
        event.getModMetadata().authorList.add(TextFormatting.RED+MODAUTHOR);
        event.getModMetadata().description = TextFormatting.YELLOW+MODDESCRIPTION;
        event.getModMetadata().url = MODURL;
        event.getModMetadata().logoFile = MODLOGO;
        
        proxy.fmlLifeCycleEvent(event);
    }

	@EventHandler
    // Do your mod setup. Build whatever data structures you care about. Register recipes."
    // Register network handlers
    public void fmlLifeCycleEvent(FMLInitializationEvent event) 
    {
    	
        // DEBUG
        System.out.println("init()");
        
        proxy.fmlLifeCycleEvent(event);
    }

	@EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void fmlLifeCycle(FMLPostInitializationEvent event) 
	{
        // DEBUG
        System.out.println("postInit()");
        
        proxy.fmlLifeCycleEvent(event);
    }

	@EventHandler
	public void fmlLifeCycle(FMLServerAboutToStartEvent event)
	{
        // DEBUG
        System.out.println("Server about to start");
        
		proxy.fmlLifeCycleEvent(event);
	}

	@EventHandler
	// register server commands
	// refer to tutorial at http://www.minecraftforge.net/wiki/Server_Command#Mod_Implementation
	public void fmlLifeCycle(FMLServerStartingEvent event)
	{
        // DEBUG
        System.out.println("Server starting");
        
		proxy.fmlLifeCycleEvent(event);
	}

	@EventHandler
	public void fmlLifeCycle(FMLServerStartedEvent event)
	{
        // DEBUG
        System.out.println("Server started");
        
		proxy.fmlLifeCycleEvent(event);
	}

	@EventHandler
	public void fmlLifeCycle(FMLServerStoppingEvent event)
	{
        // DEBUG
        System.out.println("Server stopping");
        
		proxy.fmlLifeCycleEvent(event);
	}

	@EventHandler
	public void fmlLifeCycle(FMLServerStoppedEvent event)
	{
        // DEBUG
        System.out.println("Server stopped");
        
		proxy.fmlLifeCycleEvent(event);
	}

}
