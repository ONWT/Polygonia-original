package org.gotdns.noobs.Polygonias.CommandHandlers;

import org.bukkit.event.player.PlayerChatEvent;
import org.gotdns.noobs.Polygonias.General;
import org.gotdns.noobs.Polygonias.Polygonia;
import org.gotdns.noobs.Polygonias.Polygonias;

public class ReloadCommandHandler {
	public static void Process(String[] data, PlayerChatEvent event,
			Polygonias plugin) {
		if (Polygonias.permissions.has(event.getPlayer(), "epiczones.admin")) {
			try {
				plugin.setupPermissions();
				General.config.load();
				General.config.save();
				Polygonia.getInstance().loadZones(null);
				event.getPlayer().sendMessage("Polygonias Reloaded.");
				event.setCancelled(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}