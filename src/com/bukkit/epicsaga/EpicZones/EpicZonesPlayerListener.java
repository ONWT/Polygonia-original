package com.bukkit.epicsaga.EpicZones;

import java.awt.Point;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.bukkit.epicsaga.EpicZones.CommandHandlers.ReloadCommandHandler;
import com.bukkit.epicsaga.EpicZones.CommandHandlers.WhoCommandHandler;
import com.bukkit.epicsaga.EpicZones.CommandHandlers.ZoneCommandHandler;

public class EpicZonesPlayerListener extends PlayerListener
{
  private final EpicZones plugin;
  private static final String NO_PERM_BUCKET = "You do not have permissions to do that in this zone.";
  private static final int EMPTY_BUCKET = 325;
  private Set<Integer> bucketTypes = new HashSet<Integer>();

  public EpicZonesPlayerListener(EpicZones instance)
  {
    this.plugin = instance;
    this.bucketTypes.add(Integer.valueOf(326));
    this.bucketTypes.add(Integer.valueOf(327));
  }

  public void onPlayerMove(PlayerMoveEvent event)
  {
    Player player = event.getPlayer();
    EpicZonePlayer ezp = General.getPlayer(player.getName());
    int playerHeight = event.getTo().getBlockY();
    Point playerPoint = new Point(event.getTo().getBlockX(), event.getTo().getBlockZ());

    if (General.ShouldCheckPlayer(ezp))
    {
      if (!ezp.isTeleporting())
      {
        if (ezp.getCurrentLocation() == null) ezp.setCurrentLocation(event.getFrom());
        if (!PlayerWithinZoneLogic(player, ezp, playerHeight, playerPoint))
        {
          ezp.setIsTeleporting(true);
          player.teleportTo(ezp.getCurrentLocation());
          ezp.setIsTeleporting(false);
          event.setTo(ezp.getCurrentLocation());
          event.setCancelled(true);
        }
        else
        {
          ezp.setCurrentLocation(event.getFrom());
        }
      }
      ezp.Check();
    }
  }

  public void onPlayerTeleport(PlayerMoveEvent event)
  {
    Player player = event.getPlayer();
    EpicZonePlayer ezp = General.getPlayer(player.getName());
    int playerHeight = event.getTo().getBlockY();
    Point playerPoint = new Point(event.getTo().getBlockX(), event.getTo().getBlockZ());

    if (General.ShouldCheckPlayer(ezp))
    {
      if (!ezp.isTeleporting())
      {
        if (ezp.getEntityID() != player.getEntityId()) ezp.setEntityID(player.getEntityId());
        if (ezp.getCurrentLocation() == null) ezp.setCurrentLocation(event.getFrom());
        if (!PlayerWithinZoneLogic(player, ezp, playerHeight, playerPoint))
        {
          ezp.setIsTeleporting(true);
          player.teleportTo(ezp.getCurrentLocation());
          ezp.setIsTeleporting(false);
          event.setTo(ezp.getCurrentLocation());
          event.setCancelled(true);
        }

        ezp.setCurrentLocation(event.getTo());
      }

      ezp.Check();
    }
  }

  private boolean PlayerWithinZoneLogic(Player player, EpicZonePlayer ezp, int playerHeight, Point playerPoint)
  {
    EpicZone foundZone = null;
    String worldName = player.getWorld().getName();

    if (General.pointWithinBorder(playerPoint, player))
    {
      foundZone = FindZone(player, ezp, playerHeight, playerPoint, worldName);

      if (foundZone != null)
      {
        if ((ezp.getCurrentZone() == null) || (foundZone != ezp.getCurrentZone()))
        {
          if (General.hasPermissions(player, foundZone, "entry"))
          {
            ezp.setCurrentZone(foundZone);
            if (foundZone.getEnterText().length() > 0) player.sendMessage(foundZone.getEnterText());
          }
          else
          {
            General.WarnPlayer(player, ezp, "You do not have permission to enter " + foundZone.getName());
            return false;
          }

        }

      }
      else if (ezp.getCurrentZone() != null)
      {
        if (ezp.getCurrentZone().getExitText().length() > 0) player.sendMessage(ezp.getCurrentZone().getExitText());
        ezp.setCurrentZone(null);
      }

    }
    else
    {
      General.WarnPlayer(player, ezp, "You have reached the border of the map.");
      return false;
    }

    return true;
  }

  private EpicZone FindZone(Player player, EpicZonePlayer ezp, int playerHeight, Point playerPoint, String worldName)
  {
    EpicZone result = null;

    if (ezp.getCurrentZone() != null)
    {
      result = ezp.getCurrentZone();
      String resultTag = General.isPointInZone(result, playerHeight, playerPoint, worldName);
      if (resultTag.length() > 0)
      {
        if (!resultTag.equalsIgnoreCase(ezp.getCurrentZone().getTag()))
        {
          result = (EpicZone)General.myZones.get(resultTag);
        }
      }
      else
      {
        result = null;
      }

    }
    else
    {
      result = General.getZoneForPoint(player, ezp, playerHeight, playerPoint, worldName);
    }

    return result;
  }

  public void onPlayerLogin(PlayerLoginEvent event)
  {
    if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
      return;
    }
    General.addPlayer(event.getPlayer().getEntityId(), event.getPlayer().getName());
  }

  public void onPlayerQuit(PlayerEvent event)
  {
    General.removePlayer(event.getPlayer().getEntityId());
  }

  public void onPlayerCommand(PlayerChatEvent event)
  {
    if (!event.isCancelled())
    {
      String[] split = event.getMessage().split("\\s");
      if (split[0].equalsIgnoreCase("/who")) WhoCommandHandler.Process(split, event);
      else if (split[0].equalsIgnoreCase("/reloadez")) ReloadCommandHandler.Process(split, event, this.plugin);
      else if (split[0].equalsIgnoreCase("/zone")) ZoneCommandHandler.Process(split, event);
    }
  }

  public void onPlayerItem(PlayerItemEvent event)
  {
    if (this.bucketTypes.contains(Integer.valueOf(event.getPlayer().getItemInHand().getTypeId())))
    {
      Player player = event.getPlayer();
      EpicZonePlayer ezp = General.getPlayer(player.getName());
      Point blockPoint = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
      String worldName = player.getWorld().getName();
      int blockHeight = event.getBlockClicked().getLocation().getBlockY();
      boolean hasPerms = false;

      EpicZone currentZone = null;
      if (General.pointWithinBorder(blockPoint, player))
      {
        currentZone = General.getZoneForPoint(player, ezp, blockHeight, blockPoint, worldName);
        hasPerms = General.hasPermissions(player, currentZone, "build");

        if (!hasPerms)
        {
          if (ezp.getLastWarned().before(new Date()))
          {
            player.sendMessage(NO_PERM_BUCKET);
            ezp.Warn();
          }
          event.setCancelled(true);
        }
      }
    }
    else if (event.getPlayer().getItemInHand().getTypeId() == EMPTY_BUCKET)
    {
      Player player = event.getPlayer();
      EpicZonePlayer ezp = General.getPlayer(player.getName());
      Point blockPoint = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
      String worldName = player.getWorld().getName();
      int blockHeight = event.getBlockClicked().getLocation().getBlockY();
      boolean hasPerms = false;

      EpicZone currentZone = null;
      if (General.pointWithinBorder(blockPoint, player))
      {
        currentZone = General.getZoneForPoint(player, ezp, blockHeight, blockPoint, worldName);
        hasPerms = General.hasPermissions(player, currentZone, "destroy");

        if (!hasPerms)
        {
          if (ezp.getLastWarned().before(new Date()))
          {
            player.sendMessage(NO_PERM_BUCKET);
            ezp.Warn();
          }
          event.setCancelled(true);
        }
      }
    }
    else if (event.getPlayer().getItemInHand().getTypeId() == General.config.zoneTool)
    {
      if (General.getPlayer(event.getPlayer().getEntityId()).getMode() == EpicZonePlayer.EpicZoneMode.ZoneDraw)
      {
        Point point = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
        General.getPlayer(event.getPlayer().getEntityId()).getEditZone().addPoint(point);
        event.getPlayer().sendMessage("Point " + point.x + ":" + point.y + " added to zone.");
      }
    }
  }
}