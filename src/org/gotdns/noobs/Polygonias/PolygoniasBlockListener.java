package org.gotdns.noobs.Polygonias;

import java.awt.Point;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PolygoniasBlockListener extends BlockListener {
	private static final String NO_PERM_DESTROY = "You do not have permissions to destroy in this zone.";
	private static final String NO_PERM_DESTROY_BORDER = "You do not have permissions to destroy outside the border of the map.";
	private static final String NO_PERM_BUILD = "You do not have permissions to build in this zone.";
	private static final String NO_PERM_BUILD_BORDER = "You do not have permissions to build outside the border of the map.";

	public PolygoniasBlockListener(Polygonias plugin) {
	}

	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		PolygoniaPlayer ezp = General.getPlayer(player.getName());
		Point blockPoint = new Point(
				event.getBlock().getLocation().getBlockX(), event.getBlock()
						.getLocation().getBlockZ());
		String worldName = player.getWorld().getName();
		int blockHeight = event.getBlock().getLocation().getBlockY();
		boolean hasPerms = false;
		Polygonia currentZone = null;

		if (Polygonia.pointWithinBorder(blockPoint, player)) {
			Polygonia.getInstance();
			currentZone = Polygonia.getZoneForPoint(player, ezp, blockHeight,
					blockPoint, worldName);
			hasPerms = General.hasPermissions(player, currentZone, "destroy");

			if (!hasPerms) {
				if (ezp.getLastWarned().before(new Date())) {
					player.sendMessage(NO_PERM_DESTROY);
					ezp.Warn();
				}
				event.setCancelled(true);
			}
		} else {
			if (ezp.getLastWarned().before(new Date())) {
				player.sendMessage(NO_PERM_DESTROY_BORDER);
				ezp.Warn();
			}
			event.setCancelled(true);
		}
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		PolygoniaPlayer ezp = General.getPlayer(player.getName());
		Point blockPoint = new Point(
				event.getBlock().getLocation().getBlockX(), event.getBlock()
						.getLocation().getBlockZ());
		String worldName = player.getWorld().getName();
		int blockHeight = event.getBlock().getLocation().getBlockY();
		boolean hasPerms = false;

		Polygonia currentZone = null;

		if (Polygonia.pointWithinBorder(blockPoint, player)) {
			currentZone = Polygonia.getZoneForPoint(player, ezp, blockHeight,
					blockPoint, worldName);
			hasPerms = General.hasPermissions(player, currentZone, "build");

			if (!hasPerms) {
				if (ezp.getLastWarned().before(new Date())) {
					player.sendMessage(NO_PERM_BUILD);
					ezp.Warn();
				}
				event.setBuild(false);
				event.setCancelled(true);
			}
		} else {
			if (ezp.getLastWarned().before(new Date())) {
				player.sendMessage(NO_PERM_BUILD_BORDER);
				ezp.Warn();
			}
			event.setBuild(false);
			event.setCancelled(true);
		}
	}
}