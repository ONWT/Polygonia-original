package org.gotdns.noobs.Polygonias;

import java.awt.Point;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class PolygoniasVehicleListener extends VehicleListener {
	private final Vector zero = new Vector(0, 0, 0);

	public PolygoniasVehicleListener(Polygonias instance) {
	}

	public void onVehicleMove(VehicleMoveEvent event) {
		Vehicle vehicle = event.getVehicle();
		Entity passenger = vehicle.getPassenger();

		if (passenger != null) {
			PolygoniaPlayer ezp = General.getPlayer(passenger.getEntityId());

			if (ezp != null) {
				int playerHeight = event.getTo().getBlockY();
				Point playerPoint = new Point(event.getTo().getBlockX(), event
						.getTo().getBlockZ());

				if (General.ShouldCheckPlayer(ezp)) {
					if (!ezp.isTeleporting()) {
						if (ezp.getCurrentLocation() == null)
							ezp.setCurrentLocation(event.getFrom());
						if (!VehicleWithinZoneLogic((Player) passenger, ezp,
								playerHeight, playerPoint)) {
							ezp.setIsTeleporting(true);
							vehicle.teleportTo(ezp.getCurrentLocation());
							vehicle.setVelocity(this.zero);
							ezp.setIsTeleporting(false);
						} else {
							ezp.setCurrentLocation(event.getFrom());
						}
					}
					ezp.Check();
				}
			}
		}
	}

	public static boolean VehicleWithinZoneLogic(Player player,
			PolygoniaPlayer ezp, int playerHeight, Point playerPoint) {
		Polygonia foundZone = null;
		String worldName = player.getWorld().getName();

		if (Polygonia.pointWithinBorder(playerPoint, player)) {
			foundZone = FindZone(player, ezp, playerHeight, playerPoint,
					worldName);

			if (foundZone != null) {
				if ((ezp.getCurrentZone() == null)
						|| (foundZone != ezp.getCurrentZone())) {
					if (General.hasPermissions(player, foundZone, "entry")) {
						ezp.setCurrentZone(foundZone);
						if (foundZone.getEnterText().length() > 0)
							player.sendMessage(foundZone.getEnterText());
					} else {
						General.WarnPlayer(player, ezp,
								"You do not have permission to enter "
										+ foundZone.getName());
						return false;
					}

				}

			} else if (ezp.getCurrentZone() != null) {
				if (ezp.getCurrentZone().getExitText().length() > 0)
					player.sendMessage(ezp.getCurrentZone().getExitText());
				ezp.setCurrentZone(null);
			}

		} else {
			General.WarnPlayer(player, ezp,
					"You have reached the border of the map.");
			return false;
		}

		return true;
	}

	private static Polygonia FindZone(Player player, PolygoniaPlayer ezp,
			int playerHeight, Point playerPoint, String worldName) {
		Polygonia result = null;

		if (ezp.getCurrentZone() != null) {
			result = ezp.getCurrentZone();
			String resultTag = Polygonia.isPointInZone(result, playerHeight,
					playerPoint, worldName);
			if (resultTag.length() > 0) {
				if (!resultTag.equalsIgnoreCase(ezp.getCurrentZone().getTag())) {
					result = Polygonia.getInstance().getZone(resultTag);
				}
			} else {
				result = null;
			}

		} else {
			result = Polygonia.getInstance().getZoneForPoint(player, ezp, playerHeight,
					playerPoint, worldName);
		}

		return result;
	}
}