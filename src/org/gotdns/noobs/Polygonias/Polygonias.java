package org.gotdns.noobs.Polygonias;

import java.io.File;
import java.util.HashMap;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;

public class Polygonias extends JavaPlugin {
	private final PolygoniasPlayerListener playerListener = new PolygoniasPlayerListener(
			this);
	private final PolygoniasBlockListener blockListener = new PolygoniasBlockListener(
			this);
	private final PolygoniasEntityListener entityListener = new PolygoniasEntityListener(
			this);
	private final PolygoniasVehicleListener vehicleListener = new PolygoniasVehicleListener(
			this);
	private final PolygoniasRegen regen = new PolygoniasRegen(this);

	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	private static final String CONFIG_FILE = "config.yml";
	public static PermissionHandler permissions;

	public void onEnable() {
		File file = new File(this.getDataFolder() + File.separator
				+ CONFIG_FILE);
		General.config = new PolygoniasConfig(file);
		PluginDescriptionFile pdfFile = getDescription();
		try {
			PluginManager pm = getServer().getPluginManager();

			setupPermissions();
			checkConfigDir();
			General.config.load();
			PolygoniasConfig.Plugin_Directory=this.getDataFolder().getPath();
			General.config.save();
			Polygonia.getInstance().loadZones(getDataFolder());

			pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener,
					Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.PLAYER_TELEPORT, this.playerListener,
					Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener,
					Event.Priority.Monitor, this);
			pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener,
					Event.Priority.Monitor, this);
			pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener,
					Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.PLAYER_ITEM, this.playerListener,
					Event.Priority.Normal, this);

			pm.registerEvent(Event.Type.BLOCK_DAMAGED, this.blockListener,
					Event.Priority.Normal, this);
			pm.registerEvent(Event.Type.BLOCK_PLACED, this.blockListener,
					Event.Priority.Normal, this);

			pm.registerEvent(Event.Type.ENTITY_DAMAGED, this.entityListener,
					Event.Priority.Normal, this);

			pm.registerEvent(Event.Type.VEHICLE_MOVE, this.vehicleListener,
					Event.Priority.Normal, this);

			getServer().getScheduler().scheduleAsyncRepeatingTask(this,
					this.regen, 10L, 10L);

			for (Player p : getServer().getOnlinePlayers()) {
				General.addPlayer(p.getEntityId(), p.getName());
			}
			System.out.println("Enbeled plugin"+"[" + pdfFile.getName() + "]");
		} catch (Throwable e) {
			System.out.println("[" + pdfFile.getName() + "]"
					+ " error starting: " + e.getMessage() + " Cause: "
					+ e.getCause() + " Disabling plugin");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		Polygonia.getInstance().SaveZones();
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is disabled.");
	}

	@Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		
		return false;
	}
	public boolean isDebugging(Player player) {
		if (this.debugees.containsKey(player)) {
			return ((Boolean) this.debugees.get(player)).booleanValue();
		}
		return false;
	}

	public void setDebugging(Player player, boolean value) {
		this.debugees.put(player, Boolean.valueOf(value));
	}

	public void setupPermissions() {
		Plugin p = this.getServer().getPluginManager().getPlugin("GroupManager");
        if (p != null) {
            if (!p.isEnabled()) {
                this.getServer().getPluginManager().enablePlugin(p);
            }
            GroupManager gm = (GroupManager) p;
            permissions = gm.getPermissionHandler();
        } else {
            this.getPluginLoader().disablePlugin(this);
        }
	}

	private void checkConfigDir() throws Exception {
		File dir = getDataFolder();

		if ((!dir.isDirectory()) && (!dir.mkdirs()))
			throw new Exception("Could not make configuration directory "
					+ dir.getPath());
	}
}