package org.gotdns.noobs.Ploygonias;

import java.io.File;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Ploygonias extends JavaPlugin
{
  private final PloygoniasPlayerListener playerListener = new PloygoniasPlayerListener(this);
  private final PloygoniasBlockListener blockListener = new PloygoniasBlockListener(this);
  private final PloygoniasEntityListener entityListener = new PloygoniasEntityListener(this);
  private final PloygoniasVehicleListener vehicleListener = new PloygoniasVehicleListener(this);
  private final PloygoniasRegen regen = new PloygoniasRegen(this);

  private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
  private static final String CONFIG_FILE = "config.yml";
  public static PermissionHandler permissions;


  public void onEnable() {
	File file = new File(this.getDataFolder() + File.separator + CONFIG_FILE);
	General.config = new PloygoniasConfig(file);
    PluginDescriptionFile pdfFile = getDescription();
    try
    {
      PluginManager pm = getServer().getPluginManager();

      setupPermissions();
      checkConfigDir();
      General.config.load();
      General.config.save();
      General.loadZones(getDataFolder());

      pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
      pm.registerEvent(Event.Type.PLAYER_TELEPORT, this.playerListener, Event.Priority.Normal, this);
      pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Monitor, this);
      pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Monitor, this);
      pm.registerEvent(Event.Type.PLAYER_COMMAND, this.playerListener, Event.Priority.Normal, this);
      pm.registerEvent(Event.Type.PLAYER_ITEM, this.playerListener, Event.Priority.Normal, this);

      pm.registerEvent(Event.Type.BLOCK_DAMAGED, this.blockListener, Event.Priority.Normal, this);
      pm.registerEvent(Event.Type.BLOCK_PLACED, this.blockListener, Event.Priority.Normal, this);

      pm.registerEvent(Event.Type.ENTITY_DAMAGED, this.entityListener, Event.Priority.Normal, this);

      pm.registerEvent(Event.Type.VEHICLE_MOVE, this.vehicleListener, Event.Priority.Normal, this);

      getServer().getScheduler().scheduleAsyncRepeatingTask(this, this.regen, 10L, 10L);

      for (Player p : getServer().getOnlinePlayers())
      {
        General.addPlayer(p.getEntityId(), p.getName());
      }

      System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
    }
    catch (Throwable e) {
      System.out.println("[" + pdfFile.getName() + "]" + " error starting: " + 
        e.getMessage() + " Disabling plugin");
      getServer().getPluginManager().disablePlugin(this);
    }
  }

  public void onDisable() {
    PluginDescriptionFile pdfFile = getDescription();
    System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled.");
  }
  public boolean isDebugging(Player player) {
    if (this.debugees.containsKey(player)) {
      return ((Boolean)this.debugees.get(player)).booleanValue();
    }
    return false;
  }

  public void setDebugging(Player player, boolean value)
  {
    this.debugees.put(player, Boolean.valueOf(value));
  }

  public void setupPermissions() throws Exception {
    Plugin test = getServer().getPluginManager().getPlugin("Permissions");
    if (test != null)
    {
      if (!test.isEnabled()) getServer().getPluginManager().enablePlugin(test);
      permissions = ((Permissions)test).getHandler();
    }
    else {
      throw new Exception("Permission plugin not available.");
    }
  }

  private void checkConfigDir() throws Exception
  {
    File dir = getDataFolder();

    if ((!dir.isDirectory()) && (!dir.mkdirs()))
      throw new Exception("Could not make configuration directory " + 
        dir.getPath());
  }
}