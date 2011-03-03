package org.gotdns.noobs.Polygonias;

import java.util.ArrayList;
import java.util.Date;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class General {
	public static ArrayList<PolygoniaPlayer> myPlayers = new ArrayList<PolygoniaPlayer>();
	public static PolygoniasConfig config;
	public static final String NO_PERM_ENTER = "You do not have permission to enter ";
	public static final String NO_PERM_BORDER = "You have reached the border of the map.";

	public static PolygoniaPlayer getPlayer(String name) {
		for (PolygoniaPlayer ezp : myPlayers) {
			if (ezp.getName().equalsIgnoreCase(name)) {
				return ezp;
			}
		}
		return null;
	}

	public static PolygoniaPlayer getPlayer(int entityID) {
		for (PolygoniaPlayer ezp : myPlayers) {
			if (ezp.getEntityID() == entityID) {
				return ezp;
			}
		}
		return null;
	}
	public static PolygoniaPlayer getPlayer(CommandSender sender) {
		Player p = (Player) sender;
		for (PolygoniaPlayer ezp : myPlayers) {
			if (ezp.getEntityID() == p.getEntityId()) {
				return ezp;
			}
		}
		return null;
	}

	public static void addPlayer(int entityID, String name) {
		myPlayers.add(new PolygoniaPlayer(entityID, name));
	}

	public static void removePlayer(int entityID) {
		int index = -1;

		for (int i = 0; i < myPlayers.size(); i++) {
			if (((PolygoniaPlayer) myPlayers.get(i)).getEntityID() != entityID)
				continue;
			index = i;
			break;
		}

		if (index > -1)
			myPlayers.remove(index);
	}

	public static boolean hasPermissions(Player player, Polygonia zone,
			String flag) {
		if (!Polygonias.permissions.has(player, "epiczones.ignorepermissions")) {
			if (zone == null) {
				return getDefaultPerm(flag);
			}
			if (zone.isOwner(player)) {
				return true;
			}
			if (zone.isMember(player)) {
				// TODO:make a flag so that you can say if members can make
				// subZones.
				return true;
			}
			if (Polygonias.permissions.has(player, "epiczones." + zone.getTag()
					+ "." + flag + ".deny")) {
				return false;
			}
			if (Polygonias.permissions.has(player, "epiczones." + zone.getTag()
					+ "." + flag)) {
				player.sendMessage("permission allowed");
				return true;
			}
			if (zone.hasParent()) {
				return hasPermissions(player, zone.getParent(), flag);
			}

			return false;
		} else {
			return true;
		}
	}

	private static boolean getDefaultPerm(String flag) {
		if (flag.equals("entry"))
			return config.defaultEnter;
		if (flag.equals("destroy"))
			return config.defaultDestroy;
		if (flag.equals("build")) {
			return config.defaultBuild;
		}
		return false;
	}

	public static void WarnPlayer(Player player, PolygoniaPlayer ezp,
			String message) {
		if (ezp.getLastWarned().before(new Date())) {
			player.sendMessage(message);
			ezp.Warn();
		}
	}

	public static boolean ShouldCheckPlayer(PolygoniaPlayer ezp) {
		return ezp.getLastCheck().before(new Date());
	}

	public static void Regen() {
	}

}