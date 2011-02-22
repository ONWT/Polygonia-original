package org.gotdns.noobs.Ploygonias;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class PloygoniasRegen
  implements Runnable
{
  private final Ploygonias plugin;
  private final int MAX_HEALTH = 20;

  PloygoniasRegen(Ploygonias instance)
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
      PloygoniaPlayer ezp = General.getPlayer(player.getEntityId());
      if (ezp == null)
        continue;
      Ploygonia zone = ezp.getCurrentZone();
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
      ((Ploygonia)General.myZones.get(regenZoneTags.get(i))).Regen();
    }
  }
}