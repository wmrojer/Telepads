package telepads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import telepads.block.BlockTelepad;
import telepads.block.TETelepad;
import telepads.item.ItemPadLocations;
import telepads.util.DataTracker;
import telepads.util.PadEvents;
import telepads.util.TelePadGuiHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import telepads.info.ModInfo;
import telepads.config.ConfigHandler;

@Mod(modid = ModInfo.MODID, name = ModInfo.NAME, version = ModInfo.VERSION, guiFactory = ModInfo.GUI_FACTORY_CLASS, dependencies=ModInfo.DEPENDENCY)
public class Telepads {

	public static BlockTelepad telepad;
	public static ItemPadLocations register;

	@SidedProxy(serverSide = ModInfo.SERVER_PROXY_CLASS, clientSide = ModInfo.CLIENT_PROXY_CLASS)
	public static TelepadProxyServer proxy;
	public static Telepads instance;

	public static FMLEventChannel Channel;
	public static final String packetChannel = "TelepadPackets";

	public static Logger log = LogManager.getLogger(ModInfo.MODID);

	private static Boolean illegalRecipe = false;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e){

    	ConfigHandler.init(e.getSuggestedConfigurationFile());
    	FMLCommonHandler.instance().bus().register(new ConfigHandler());

    	register = (ItemPadLocations) new ItemPadLocations().setUnlocalizedName("register");
		GameRegistry.registerItem(register, "ItemPadLocations");

		telepad = (BlockTelepad) new BlockTelepad(Material.wood).setBlockName("telepad").setLightLevel(0.2f).setCreativeTab(CreativeTabs.tabTransport).setBlockUnbreakable().setBlockTextureName("wool_colored_pink");
		GameRegistry.registerBlock(telepad, "TelePad");
	}
	
	@EventHandler
	public void load(FMLInitializationEvent evt){

		instance = this;

		proxy.registerSound();

		Object glass = getRecipeObject(ConfigHandler.recipeItem1);
		Object enderPearl = getRecipeObject(ConfigHandler.recipeItem2);
		Object redstone = getRecipeObject(ConfigHandler.recipeItem3);
		Object iron = getRecipeObject(ConfigHandler.recipeItem4);
		if (!illegalRecipe) {
			GameRegistry.addRecipe(new ItemStack(telepad, ConfigHandler.numPadsCrafted), new Object[] {"GGG", "RER", "RIR",
				'G', glass, 'R', redstone, 'E', enderPearl, 'I', iron});
			log.info("Telepads crafting recipe created");
		}

		proxy.registerTileEntity();
		GameRegistry.registerTileEntity(TETelepad.class, "TETelepad");
		proxy.registerItemRenderer();

		Channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(packetChannel);
		proxy.registerPacketHandlers();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new TelePadGuiHandler());
		FMLCommonHandler.instance().bus().register(new DataTracker());
		MinecraftForge.EVENT_BUS.register(new PadEvents());
	}

	private static Object getRecipeObject(String name) {
		Object t = null;
		if (Item.itemRegistry.containsKey(name)) {
			t = Item.itemRegistry.getObject(name);
		} else if(Block.blockRegistry.containsKey(name)) {
			t = Block.blockRegistry.getObject(name);
		} else {
			illegalRecipe = true;
			log.error("Unknown item/block used for recipe! " + name);
		}
		return t;
	}
}
