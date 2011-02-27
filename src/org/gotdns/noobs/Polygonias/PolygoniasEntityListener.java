package org.gotdns.noobs.Polygonias;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class PolygoniasEntityListener extends EntityListener {
	public PolygoniasEntityListener(Polygonias instance) {
	}

	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
			if ((isPlayer(event.getEntity())) && (isPlayer(event.getDamager()))) {
				PolygoniaPlayer ezp = General.getPlayer(event.getEntity()
						.getEntityId());
				Polygonia zone = ezp.getCurrentZone();
				if (zone != null) {
					if (!zone.hasPVP()) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
		if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
			if ((isPlayer(event.getEntity())) && (isPlayer(event.getDamager()))) {
				PolygoniaPlayer ezp = General.getPlayer(event.getEntity()
						.getEntityId());
				Polygonia zone = ezp.getCurrentZone();
				if (zone != null) {
					if (!zone.hasPVP()) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	private boolean isPlayer(Entity entity) {
		boolean result = false;

		if (General.getPlayer(entity.getEntityId()) != null) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}
}