package com.bukkit.epicsaga.EpicZones;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class EpicZonesRegen
  implements Runnable
{
  private final EpicZones plugin;
  private final int MAX_HEALTH = 20;

  EpicZonesRegen(EpicZones instance)
  {
    this.plugin = instance;
  }

  public void run()
  {
    ArrayList<String> regenZoneTags = new ArrayList<String>();

    for (Player player : this.plugin.getServer().getOnlinePlayers())
    {
      if (player.getHealth() > MAX_HEALTH)
        continue;
      EpicZonePlayer ezp = General.getPlayer(player.getEntityId());
      if (ezp == null)
        continue;
      EpicZone zone = ezp.getCurrentZone();
      if (zone == null)
        continue;
      if (!zone.timeToRegen())
        continue;
      if (ezp.getEnteredZone().before(zone.getAdjustedRegenDelay()))
      {
        player.setHealth(player.getHealth() + zone.getRegenAmount());
        if (player.getHealth() > MAX_HEALTH)
        {
          player.setHealth(MAX_HEALTH);
        }
      }
      if (regenZoneTags.contains(zone.getTag()))
        continue;
      regenZoneTags.add(zone.getTag());
    }

    for (int i = 0; i < regenZoneTags.size(); i++)
    {
      ((EpicZone)General.myZones.get(regenZoneTags.get(i))).Regen();
    }
  }
}