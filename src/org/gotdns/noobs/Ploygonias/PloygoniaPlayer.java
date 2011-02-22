package org.gotdns.noobs.Ploygonias;

import java.util.Calendar;
import java.util.Date;
import org.bukkit.Location;

public class PloygoniaPlayer
{
  private Ploygonia currentZone;
  private int entityID;
  private String name;
  private Location currentLocation;
  private Date lastWarned = new Date();
  private int distanceFromCenter;
  private boolean teleporting = false;
  private Date lastCheck = new Date();
  private PloygoniaMode mode = PloygoniaMode.None;
  private Ploygonia editZone = null;
  private boolean pastBorder = false;
  private Date enteredZone = new Date();

  public Ploygonia getCurrentZone() {
    return this.currentZone; } 
  public int getEntityID() { return this.entityID; } 
  public String getName() { return this.name; } 
  public Location getCurrentLocation() { return this.currentLocation; } 
  public Date getLastWarned() { return this.lastWarned; } 
  public Date getLastCheck() { return this.lastCheck; } 
  public int getDistanceFromCenter() { return this.distanceFromCenter; } 
  public boolean isTeleporting() { return this.teleporting; } 
  public PloygoniaMode getMode() { return this.mode; } 
  public Ploygonia getEditZone() { return this.editZone; } 
  public boolean getPastBorder() { return this.pastBorder; } 
  public Date getEnteredZone() { return this.enteredZone;
  }

  public void setPastBorder(boolean value)
  {
    this.pastBorder = value;
  }

  public void setEntityID(int value)
  {
    this.entityID = value;
  }

  public void setMode(PloygoniaMode value)
  {
    this.mode = value;
  }

  public void setEditZone(Ploygonia value)
  {
    this.editZone = value;
  }

  public PloygoniaPlayer(int entityID, String name)
  {
    this.entityID = entityID;
    this.name = name;
  }

  public void setCurrentZone(Ploygonia z)
  {
    this.currentZone = z;
    this.enteredZone = new Date();
  }

  public void setDistanceFromCenter(int distance)
  {
    this.distanceFromCenter = distance;
  }

  public void setCurrentLocation(Location l)
  {
    this.currentLocation = l;
  }

  public void Warn()
  {
    Calendar cal = Calendar.getInstance();
    cal.add(13, 2);
    this.lastWarned = cal.getTime();
  }

  public void Check()
  {
    Calendar cal = Calendar.getInstance();
    cal.add(14, 500);
    this.lastCheck = cal.getTime();
  }

  public void setIsTeleporting(boolean value)
  {
    this.teleporting = value;
  }

  public static enum PloygoniaMode
  {
    None, ZoneDraw, ZoneEdit, ZoneDrawConfirm, ZoneDeleteConfirm;
  }
}