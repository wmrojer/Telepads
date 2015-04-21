package telepads.config;

import java.io.File;

import telepads.Telepads;
import telepads.info.ModInfo;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static Configuration config;

	public static int teleportDelay = 5;
	public static int numPadsCrafted = 2;
	public static int[] blacklistedDimensions = { 1 };
	public static String recipeItem1;
	public static String recipeItem2;
	public static String recipeItem3;
	public static String recipeItem4;
	
	public static void init(File file) {
		if (config == null)
		{
			config = new Configuration(file);
			try 
			{
				config.load();
			}
			catch (Exception e)
			{
				Telepads.log.error("Error loading config! " + e.getMessage());
			}
			updateConfig();
		}
	}

	private static void updateConfig()
	{
		teleportDelay = config.get(Configuration.CATEGORY_GENERAL, "Teleportation Delay", 5, "How many seconds it takes the telepads to initialize teleportation").setRequiresMcRestart(true).getInt();
		numPadsCrafted = config.get(Configuration.CATEGORY_GENERAL, "Number of Telepads crafted", 2, "The number of telepads crafted from the crafting recipe").setRequiresMcRestart(true).getInt();
		blacklistedDimensions = config.get(Configuration.CATEGORY_GENERAL, "Blacklisted Dimensions", new int[] {1}, "List of dimensions that Telepads are not allowed to be placed").setRequiresMcRestart(true).getIntList();

		recipeItem1 = config.get(Configuration.CATEGORY_GENERAL, "Recipe Item 1", "minecraft:glass", "Item 1 for the crafting recipe for Telepads").setRequiresMcRestart(true).getString();
		recipeItem2 = config.get(Configuration.CATEGORY_GENERAL, "Recipe Item 2", "minecraft:ender_pearl", "Item 2 for the crafting recipe for Telepads").setRequiresMcRestart(true).getString();
		recipeItem3 = config.get(Configuration.CATEGORY_GENERAL, "Recipe Item 3", "minecraft:redstone", "Item 3 for the crafting recipe for Telepads").setRequiresMcRestart(true).getString();
		recipeItem4 = config.get(Configuration.CATEGORY_GENERAL, "Recipe Item 4", "minecraft:iron_block", "Item 4 for the crafting recipe for Telepads").setRequiresMcRestart(true).getString();
		
		if (config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.modID.equalsIgnoreCase(ModInfo.MODID))
		{
			updateConfig();
		}
	}
}
