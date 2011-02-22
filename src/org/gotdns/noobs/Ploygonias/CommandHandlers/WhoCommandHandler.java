package org.gotdns.noobs.Ploygonias.CommandHandlers;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.gotdns.noobs.Ploygonias.General;
import org.gotdns.noobs.Ploygonias.Ploygonia;
import org.gotdns.noobs.Ploygonias.PloygoniaPlayer;
import org.gotdns.noobs.Ploygonias.Ploygonias;


public class WhoCommandHandler
{
  public static void Process(String[] data, PlayerChatEvent event)
  {
    int pageNumber = 1;

    if (Ploygonias.permissions.has(event.getPlayer(), "epiczones.who"))
    {
      if (data.length > 1)
      {
        if (data[1].equalsIgnoreCase("all"))
        {
          if (data.length > 2)
          {
            try
            {
              pageNumber = Integer.parseInt(data[2]);
            }
            catch (NumberFormatException nfe)
            {
              pageNumber = 1;
            }
          }
          buildWho(General.getPlayer(event.getPlayer().getName()), event.getPlayer(), pageNumber, true);
          return;
        }

        try
        {
          pageNumber = Integer.parseInt(data[1]);
        }
        catch (NumberFormatException nfe)
        {
          pageNumber = 1;
        }
      }

      buildWho(General.getPlayer(event.getPlayer().getName()), event.getPlayer(), pageNumber, false);
      event.setCancelled(true);
    }
  }

  private static void buildWho(PloygoniaPlayer ezp, Player player, int pageNumber, boolean allZones)
  {
    Ploygonia currentZone = General.getPlayer(player.getName()).getCurrentZone();
    if (currentZone == null) allZones = true;
    ArrayList<PloygoniaPlayer> players = getPlayers(currentZone, allZones);
    int playersPerPage = 8;
    int playerCount = players.size();

    if (allZones)
    {
      player.sendMessage(playerCount + " Players Online [Page " + pageNumber + " of " + ((int)Math.ceil(playerCount / playersPerPage) + 1) + "]");
      for (int i = (pageNumber - 1) * playersPerPage; i < pageNumber * playersPerPage; i++)
      {
        if (players.size() <= i)
          continue;
        player.sendMessage(buildWhoPlayerName(ezp, players, i, allZones));
      }

    }
    else
    {
      player.sendMessage(playerCount + " Players Online in " + currentZone.getName() + " [Page " + pageNumber + " of " + ((int)Math.ceil(playerCount / playersPerPage) + 1) + "]");
      for (int i = (pageNumber - 1) * playersPerPage; i < pageNumber * playersPerPage; i++)
      {
        if (players.size() <= i)
          continue;
        player.sendMessage(buildWhoPlayerName(ezp, players, i, allZones));
      }
    }
  }

  private static String buildWhoPlayerName(PloygoniaPlayer ezp, ArrayList<PloygoniaPlayer> players, int index, boolean allZones)
  {
    if (allZones)
    {
      if (((PloygoniaPlayer)players.get(index)).getCurrentZone() != null)
      {
        return ((PloygoniaPlayer)players.get(index)).getName() + " - " + ((PloygoniaPlayer)players.get(index)).getCurrentZone().getName() + " - Distance: " + CalcDist(ezp, (PloygoniaPlayer)players.get(index));
      }

      return ((PloygoniaPlayer)players.get(index)).getName() + " - Distance: " + CalcDist(ezp, (PloygoniaPlayer)players.get(index));
    }

    return ((PloygoniaPlayer)players.get(index)).getName() + " - Distance: " + CalcDist(ezp, (PloygoniaPlayer)players.get(index));
  }

  private static int CalcDist(PloygoniaPlayer player1, PloygoniaPlayer player2)
  {
    int result = 0;

    if (!player1.getName().equals(player2.getName()))
    {
      int aSquared = player1.getDistanceFromCenter() * player1.getDistanceFromCenter();
      int bSquared = player2.getDistanceFromCenter() * player2.getDistanceFromCenter();
      int cSquared = aSquared + bSquared;

      result = (int)Math.sqrt(cSquared);
    }

    return result;
  }

  private static ArrayList<PloygoniaPlayer> getPlayers(Ploygonia currentZone, boolean allZones)
  {
    if (allZones)
    {
      return General.myPlayers;
    }

    ArrayList<PloygoniaPlayer> result = new ArrayList<PloygoniaPlayer>();
    for (PloygoniaPlayer ezp : General.myPlayers)
    {
      if ((result.contains(ezp)) || (!ezp.getCurrentZone().equals(currentZone)))
        continue;
      result.add(ezp);
    }

    return result;
  }
}