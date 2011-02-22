package org.gotdns.noobs.Ploygonias;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class PloygoniasEntityListener extends EntityListener
{
  public PloygoniasEntityListener(Ploygonias instance)
  {
  }

  public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
  {
    if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
    {
      if ((isPlayer(event.getEntity())) && (isPlayer(event.getDamager())))
      {
        PloygoniaPlayer ezp = General.getPlayer(event.getEntity().getEntityId());
        Ploygonia zone = ezp.getCurrentZone();
        if (zone != null)
        {
          if (!zone.hasPVP())
          {
            event.setCancelled(true);
          }
        }
      }
    }
  }

  public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event)
  {
    if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
    {
      if ((isPlayer(event.getEntity())) && (isPlayer(event.getDamager())))
      {
        PloygoniaPlayer ezp = General.getPlayer(event.getEntity().getEntityId());
        Ploygonia zone = ezp.getCurrentZone();
        if (zone != null)
        {
          if (!zone.hasPVP())
          {
            event.setCancelled(true);
          }
        }
      }
    }
  }

  private boolean isPlayer(Entity entity)
  {
    boolean result = false;

    if (General.getPlayer(entity.getEntityId()) != null)
    {
      result = true;
    }
    else
    {
      result = false;
    }

    return result;
  }
}