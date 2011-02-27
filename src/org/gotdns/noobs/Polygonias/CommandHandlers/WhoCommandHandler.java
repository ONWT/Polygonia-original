package org.gotdns.noobs.Polygonias.CommandHandlers;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.gotdns.noobs.Polygonias.General;
import org.gotdns.noobs.Polygonias.Polygonia;
import org.gotdns.noobs.Polygonias.PolygoniaPlayer;
import org.gotdns.noobs.Polygonias.Polygonias;

public class WhoCommandHandler {
	public static void Process(String[] data, PlayerChatEvent event) {
		int pageNumber = 1;

		if (Polygonias.permissions.has(event.getPlayer(), "epiczones.who")) {
			if (data.length > 1) {
				if (data[1].equalsIgnoreCase("all")) {
					if (data.length > 2) {
						try {
							pageNumber = Integer.parseInt(data[2]);
						} catch (NumberFormatException nfe) {
							pageNumber = 1;
						}
					}
					buildWho(General.getPlayer(event.getPlayer().getName()),
							event.getPlayer(), pageNumber, true);
					return;
				}

				try {
					pageNumber = Integer.parseInt(data[1]);
				} catch (NumberFormatException nfe) {
					pageNumber = 1;
				}
			}

			buildWho(General.getPlayer(event.getPlayer().getName()),
					event.getPlayer(), pageNumber, false);
			event.setCancelled(true);
		}
	}

	private static void buildWho(PolygoniaPlayer ezp, Player player,
			int pageNumber, boolean allZones) {
		Polygonia currentZone = General.getPlayer(player.getName())
				.getCurrentZone();
		if (currentZone == null)
			allZones = true;
		ArrayList<PolygoniaPlayer> players = getPlayers(currentZone, allZones);
		int playersPerPage = 8;
		int playerCount = players.size();

		if (allZones) {
			player.sendMessage(playerCount + " Players Online [Page "
					+ pageNumber + " of "
					+ ((int) Math.ceil(playerCount / playersPerPage) + 1) + "]");
			for (int i = (pageNumber - 1) * playersPerPage; i < pageNumber
					* playersPerPage; i++) {
				if (players.size() <= i)
					continue;
				player.sendMessage(buildWhoPlayerName(ezp, players, i, allZones));
			}

		} else {
			player.sendMessage(playerCount + " Players Online in "
					+ currentZone.getName() + " [Page " + pageNumber + " of "
					+ ((int) Math.ceil(playerCount / playersPerPage) + 1) + "]");
			for (int i = (pageNumber - 1) * playersPerPage; i < pageNumber
					* playersPerPage; i++) {
				if (players.size() <= i)
					continue;
				player.sendMessage(buildWhoPlayerName(ezp, players, i, allZones));
			}
		}
	}

	private static String buildWhoPlayerName(PolygoniaPlayer ezp,
			ArrayList<PolygoniaPlayer> players, int index, boolean allZones) {
		if (allZones) {
			if (((PolygoniaPlayer) players.get(index)).getCurrentZone() != null) {
				return ((PolygoniaPlayer) players.get(index)).getName()
						+ " - "
						+ ((PolygoniaPlayer) players.get(index))
								.getCurrentZone().getName() + " - Distance: "
						+ CalcDist(ezp, (PolygoniaPlayer) players.get(index));
			}

			return ((PolygoniaPlayer) players.get(index)).getName()
					+ " - Distance: "
					+ CalcDist(ezp, (PolygoniaPlayer) players.get(index));
		}

		return ((PolygoniaPlayer) players.get(index)).getName()
				+ " - Distance: "
				+ CalcDist(ezp, (PolygoniaPlayer) players.get(index));
	}

	private static int CalcDist(PolygoniaPlayer player1, PolygoniaPlayer player2) {
		int result = 0;

		if (!player1.getName().equals(player2.getName())) {
			int aSquared = player1.getDistanceFromCenter()
					* player1.getDistanceFromCenter();
			int bSquared = player2.getDistanceFromCenter()
					* player2.getDistanceFromCenter();
			int cSquared = aSquared + bSquared;

			result = (int) Math.sqrt(cSquared);
		}

		return result;
	}

	private static ArrayList<PolygoniaPlayer> getPlayers(Polygonia currentZone,
			boolean allZones) {
		if (allZones) {
			return General.myPlayers;
		}

		ArrayList<PolygoniaPlayer> result = new ArrayList<PolygoniaPlayer>();
		for (PolygoniaPlayer ezp : General.myPlayers) {
			if ((result.contains(ezp))
					|| (!ezp.getCurrentZone().equals(currentZone)))
				continue;
			result.add(ezp);
		}

		return result;
	}
}