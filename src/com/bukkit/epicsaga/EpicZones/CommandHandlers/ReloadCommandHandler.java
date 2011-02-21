package com.bukkit.epicsaga.EpicZones.CommandHandlers;

import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.epicsaga.EpicZones.EpicZones;
import com.bukkit.epicsaga.EpicZones.General;

public class ReloadCommandHandler
{
  public static void Process(String[] data, PlayerChatEvent event, EpicZones plugin)
  {
    if (EpicZones.permissions.has(event.getPlayer(), "epiczones.admin"))
    {
      try
      {
        plugin.setupPermissions();
        General.config.load();
        General.config.save();
        General.loadZones(null);
        event.getPlayer().sendMessage("EpicZones Reloaded.");
        event.setCancelled(true);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}