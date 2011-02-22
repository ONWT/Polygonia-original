package org.gotdns.noobs.Ploygonias.CommandHandlers;

import org.bukkit.event.player.PlayerChatEvent;
import org.gotdns.noobs.Ploygonias.General;
import org.gotdns.noobs.Ploygonias.Ploygonias;


public class ReloadCommandHandler
{
  public static void Process(String[] data, PlayerChatEvent event, Ploygonias plugin)
  {
    if (Ploygonias.permissions.has(event.getPlayer(), "epiczones.admin"))
    {
      try
      {
        plugin.setupPermissions();
        General.config.load();
        General.config.save();
        General.loadZones(null);
        event.getPlayer().sendMessage("Ploygonias Reloaded.");
        event.setCancelled(true);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}