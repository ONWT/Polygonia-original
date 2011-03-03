package org.gotdns.noobs.Polygonias;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.bukkit.entity.Player;

/**
 * @author noobs
 * 
 */

public final class Polygonia {
	// TODO: Make singleton out off this class
	// TODO:Player specific flags
	private static Polygonia instance=null;
	private static final String ZONE_FILE = "zones.txt";
	private static File ZoneFile;
	private String tag = "";
	private String name = "";

	private int floor = 0;
	private int ceiling = 128;
	private String world = "";
	private Polygon polygon = new Polygon();
	private Rectangle boundingBox = new Rectangle();
	private String enterText = "";
	private String exitText = "";
	private Polygonia parent = null;
	private static Map<String,Polygonia> Zones = new HashMap<String,Polygonia>();
	private Map<String, Polygonia> children = new HashMap<String, Polygonia>();
	private Set<String> owner = new HashSet<String>();
	private Set<String> member = new HashSet<String>();
	private LinkedList<Point> pointlist = new LinkedList<Point>();
	private boolean hasParentFlag = false;
	private boolean hasPVP = false;
	private boolean hasRegen = false;
	private Date lastRegen = new Date();
	private int regenAmount = 0;
	private int regenDelay = 0;
	private int regenInterval = 500;

	protected Polygonia() {
	}

	public static Polygonia getInstance() {
	      if(instance == null) {
	         instance = new Polygonia();
	      }
	      return instance;
	   }
	private void AddToList(Polygonia newP) {
		if (Zones.get(this.getTag()) == null) {
			Zones.put(this.getTag(),newP);
		} else {
			Zones.remove(this.getTag());
			Zones.put(this.getTag(),newP);
		}
	}

	public void delZone(String Tag) throws Exception
	{
		Zones.remove(Tag);
	}
	
	public Polygonia addZone(String zoneData) throws Exception
	{
		Polygonia newP;
		if (zoneData==null)
		{
			newP = new Polygonia();
			AddToList(newP);
			return newP;
		}
		String[] split = zoneData.split("\\|");
		if (split.length==1)
		{
			String tag=zoneData;
			if (Zones.containsKey(tag))
			{
				newP=Zones.get(tag);
			}else
			{
				newP = new Polygonia();
				AddToList(newP);
			}
			newP.tag=zoneData;
			newP.name=zoneData;
			newP.enterText="You are now entering "+ this.name;
			newP.exitText="You are now exiting "+ this.name;
			return newP;
		}
		if (split.length == 10) {
			String tag=split[0].replaceAll("[^a-zA-Z0-9]", "");
			if (Zones.containsKey(tag))
			{
				newP=Zones.get(tag);
			}else
			{
				newP = new Polygonia();
				AddToList(newP);
			}
			newP.tag = split[0].replaceAll("[^a-zA-Z0-9]", "");
			newP.world = split[1];
			newP.name = split[2];
			newP.enterText = split[4];
			newP.exitText = split[5];
			newP.floor = Integer.valueOf(split[6]).intValue();
			newP.ceiling = Integer.valueOf(split[7]).intValue();
			newP.parent = null;
			newP.children = null;

			buildFlags(split[3],newP);
			buildChildren(split[8],newP);
			buildPolygon(split[9],newP);

			newP.boundingBox = this.polygon.getBounds();
			reconcileChildren();
			System.out.println("Created Zone [" + this.name + "]");
			return newP;
		}
		if (split.length > 10) {
			String tag=split[0].replaceAll("[^a-zA-Z0-9]", "");
			if (Zones.containsKey(tag))
			{
				newP=Zones.get(tag);
			}else
			{
				newP = new Polygonia();
				AddToList(newP);
			}
			newP.tag = split[0].replaceAll("[^a-zA-Z0-9]", "");
			newP.world = split[1];
			newP.name = split[2];
			newP.enterText = split[4];
			newP.exitText = split[5];
			newP.floor = Integer.valueOf(split[6]).intValue();
			newP.ceiling = Integer.valueOf(split[7]).intValue();
			newP.parent = null;
			newP.children = null;

			buildFlags(split[3],newP);
			buildChildren(split[8],newP);
			buildPolygon(split[9],newP);
			buildOwners(split[10],newP);
			buildMembers(split[11],newP);

			this.boundingBox = this.polygon.getBounds();
			reconcileChildren();
			System.out.println("Created Zone [" + this.name + "]");
			return newP;
		}
	throw new Exception("Not a valid zone data string");
	}
	private void buildMembers(String data, Polygonia newP) {
		if (data.length() > 0) {
			String[] dataList = data.split("\\s");

			for (int i = 0; i < dataList.length; i++) {
				newP.member.add(dataList[1]);
			}
		}
	}
	
	/**
	 * @param data String data of owners separated by space
	 * @param newP Polygon to build Owners for
	 */
	private void buildOwners(String data, Polygonia newP) {
		if (data.length() > 0) {
			String[] dataList = data.split("\\s");

			for (int i = 0; i < dataList.length; i++) {
				newP.owner.add(dataList[1]);
			}
		}
	}

	/**
	 * @param data String data of Flags separated by space
	 * @param newP Polygon to build Flags for
	 */
	private void buildFlags(String data, Polygonia newP) {
		if (data.length() > 0) {
			String[] dataList = data.split("\\s");

			for (int i = 0; i < dataList.length; i++) {
				String[] split = dataList[i].split(":");
				String flag = split[0].toLowerCase();

				if (flag.equals("pvp")) {
					newP.hasPVP = split[1].equalsIgnoreCase("true");
				} else if (flag.equals("regen")) {
					if (split.length > 2) {
						if (split.length > 3) {
							setRegen(split[1] + " " + split[2] + " " + split[3],newP);
						} else {
							setRegen(split[1] + " " + split[2] + " 0",newP);
						}
					} else {
						setRegen(split[1] + " 1",newP);
					}
				} else
					flag.equals("mobs");
			}
		}
	}

	protected void setRegen(String value, Polygonia newP) {
		String[] split = value.split("\\s");
		int interval = 0;
		int amount = 0;
		int delay = 0;

		if (split.length > 1) {
			interval = Integer.valueOf(split[0]).intValue();
			amount = Integer.valueOf(split[1]).intValue();
			if (split.length > 2) {
				delay = Integer.valueOf(split[2]).intValue();
			}
		}

		if (amount != 0) {
			newP.hasRegen = true;
			newP.regenInterval = interval;
			newP.regenAmount = amount;
			newP.regenDelay = delay;
		} else {
			newP.hasRegen = false;
			newP.regenInterval = 0;
			newP.regenDelay = 0;
			newP.regenAmount = 0;
		}
	}
	/**
	 * Loads zones from file zones.txt at path
	 * @param path 
	 */
	public static void loadZones(File path) throws IOException {
		if (path != null) {
			File file = new File(path + File.separator + ZONE_FILE);
			ZoneFile = file;
		}else{
			throw new IOException("path not defined");
		}

		try {
			Scanner scanner = new Scanner(ZoneFile);
			Zones.clear();
			Zones.clear();
			try {
				while (scanner.hasNext()) {
					String line = scanner.nextLine().trim();
					if ((!line.startsWith("#")) && (!line.isEmpty())) {
						instance.addZone(line);
					}
				}
			} finally {
				scanner.close();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		reconcileChildren();
	}
	
	private static void reconcileChildren() {
		for (Polygonia zone : Zones.values()) {
			if (zone.hasChildren()) {
				for (Polygonia childZone : zone.children.values()) {
					childZone.setParent(zone);
					zone.addChild(childZone);

					Zones.remove(childZone.getTag());
					Zones.put(childZone.getTag(), childZone);
				}
			}
			Zones.remove(zone.getTag());
			Zones.put(zone.getTag(), zone);
		}
	}
	
	public void setRegen(String value) {
		String[] split = value.split("\\s");
		int interval = 0;
		int amount = 0;
		int delay = 0;

		if (split.length > 1) {
			interval = Integer.valueOf(split[0]).intValue();
			amount = Integer.valueOf(split[1]).intValue();
			if (split.length > 2) {
				delay = Integer.valueOf(split[2]).intValue();
			}
		}

		if (amount != 0) {
			this.hasRegen = true;
			this.regenInterval = interval;
			this.regenAmount = amount;
			this.regenDelay = delay;
		} else {
			this.hasRegen = false;
			this.regenInterval = 0;
			this.regenDelay = 0;
			this.regenAmount = 0;
		}
	}

	private void buildChildren(String data,Polygonia newP) {
		if (data.length() > 0) {
			String[] dataList = data.split("\\s");

			for (int i = 0; i < dataList.length; i++) {
				try {
					newP.children.put(dataList[i],addZone(dataList[i]));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public Polygonia getZone(String tag)
	{
		return Zones.get(tag);
	}
	public String getTag() {
		return this.tag;
	}

	public String getName() {
		return this.name;
	}

	public int getFloor() {
		return this.floor;
	}

	public int getCeiling() {
		return this.ceiling;
	}

	public Polygon getPolygon() {
		return this.polygon;
	}

	public String getEnterText() {
		return this.enterText;
	}

	public String getExitText() {
		return this.exitText;
	}

	public String getWorld() {
		return this.world;
	}

	public Polygonia getParent() {
		return this.parent;
	}

	public Map<String, Polygonia> getChildren() {
		return this.children;
	}

	public boolean isOwner(PolygoniaPlayer p) {
		return this.owner.contains(p.getName());
	}
	public boolean isOwner(Player p) {
		return this.owner.contains(p.getName());
	}

	public boolean isMember(PolygoniaPlayer p) {
		return this.member.contains(p.getName());
	}
	public boolean isMember(Player p) {
		return this.member.contains(p.getName());
	}

	public void addOwner(PolygoniaPlayer epicZonePlayer) {
		this.owner.add(epicZonePlayer.getName());
	}

	public void addMember(PolygoniaPlayer p) {
		this.member.add(p.getName());
	}

	public void delOwner(PolygoniaPlayer epicZonePlayer) {
		this.owner.remove(epicZonePlayer.getName());
	}

	public void delMember(PolygoniaPlayer p) {
		this.member.remove(p);
	}

	public Set<String> getOwners() {
		return owner;
	}

	public Set<String> getMembers() {
		return member;
	}

	/**
	 * Checks if the children list isEmpty and returns the opposite
	 * 
	 * @return If the zone has children
	 */
	public boolean hasChildren() {
		if (this.children != null)
			return !this.children.isEmpty();
		else
			return false;
	}

	public boolean hasParent() {
		return this.hasParentFlag;
	}

	public boolean hasRegen() {
		return this.hasRegen;
	}

	public void addChild(Polygonia childZone) {
		if (this.children == null)
			this.children = new HashMap<String, Polygonia>();
		this.children.put(childZone.getTag(), childZone);
	}

	public void removeChild(String tag) {
		if (this.children != null) {
			this.children.remove(tag);
		}
	}

	public void setWorld(String value) {
		this.world = value;
	}

	public void setTag(String value) {
		this.tag = value;
	}

	public void setName(String value) {
		this.name = value;
	}

	public void setFloor(int value) {
		this.floor = value;
	}

	public void setCeiling(int value) {
		this.ceiling = value;
	}

	public void setEnterText(String value) {
		this.enterText = value;
	}

	public void setExitText(String value) {
		this.exitText = value;
	}

	public boolean pointWithin(Point point) {
		boolean result = false;
		if (this.boundingBox.contains(point)) {
			if (this.polygon.contains(point)) {
				result = true;
			}
		}
		return result;
	}

	public void setParent(Polygonia parent) {
		this.parent = parent;
		this.hasParentFlag = true;
	}

	private void buildPolygon(String data,Polygonia newP) {
		String[] dataList = data.split("\\s");
		newP.polygon = new Polygon();
		for (int i = 0; i < dataList.length; i++) {
			String[] split = dataList[i].split(":");
			newP.polygon.addPoint(Integer.valueOf(split[0]).intValue(), Integer
					.valueOf(split[1]).intValue());
		}
	}

	public void addPoint(Point point) {
		if (this.polygon == null)
			this.polygon = new Polygon();
		this.polygon.addPoint(point.x, point.y);
	}

	public void clearPolyPoints() {
		this.polygon = new Polygon();
	}

	public void rebuildBoundingBox() {
		this.boundingBox = this.polygon.getBounds();
	}

	public boolean hasPVP() {
		return this.hasPVP;
	}

	public int getRegenAmount() {
		return this.regenAmount;
	}

	public int getRegenDelay() {
		return this.regenDelay;
	}

	public int getRegenInterval() {
		return this.regenInterval;
	}

	public void Regen() {
		Calendar cal = Calendar.getInstance();
		cal.add(14, this.regenInterval);
		this.lastRegen = cal.getTime();
	}

	public Date getAdjustedRegenDelay() {
		Calendar cal = Calendar.getInstance();
		cal.add(14, -this.regenDelay);
		return cal.getTime();
	}

	public boolean timeToRegen() {
		if (this.hasRegen) {
			if (this.lastRegen.before(new Date())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param pointlist
	 *            the pointlist to set
	 */
	public void setPointlist(LinkedList<Point> pointlist) {
		this.pointlist = pointlist;
	}

	/**
	 * @return the pointlist
	 */
	public LinkedList<Point> getPointlist() {
		return pointlist;
	}
	
	public static void SaveZones() {
		try {
			String data = BuildZoneData();
			System.out.println("Data to save: " + data);
			Writer output = new BufferedWriter(new FileWriter(ZoneFile, false));
			try {
				output.write(data);
			} finally {
				output.close();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static String BuildZoneData() {
		String result = "#Zone Tag|World|Zone Name|Flags|Enter Message|Exit Message|Floor|Ceiling|Child Zones|PointList\n";
		String line = "";

		for (Polygonia z : Zones.values()) {
			line = z.getTag() + "|";
			line = line + z.getWorld() + "|";
			line = line + z.getName() + "|";
			line = line + BuildFlags(z) + "|";
			line = line + z.getEnterText() + "|";
			line = line + z.getExitText() + "|";
			line = line + z.getFloor() + "|";
			line = line + z.getCeiling() + "|";
			line = line + BuildChildren(z) + "|";
			line = line + BuildPointList(z) + "|";
			line = line + BuildOwners(z) + "|";
			line = line + BuildMembers(z) + "\n";
			result = result + line;
		}

		return result;
	}

	private static String BuildMembers(Polygonia z) {
		String result = "";
		for (String p : z.getMembers()) {
			result = result + " " + p;
		}
		return result;
	}

	private static String BuildOwners(Polygonia z) {
		String result = "";
		for (String p : z.getOwners()) {
			result = result + " " + p;
		}
		return result;
	}

	private static String BuildFlags(Polygonia z) {
		String result = "";

		if (z.hasPVP())
			result = result + "pvp:true ";
		else {
			result = result + "pvp:false ";
		}

		if (z.hasRegen()) {
			if (z.getRegenDelay() > 0) {
				result = result + "regen:" + z.getRegenInterval() + ":"
						+ z.getRegenAmount() + ":" + z.getRegenDelay() + " ";
			} else {
				result = result + "regen:" + z.getRegenInterval() + ":"
						+ z.getRegenAmount() + " ";
			}

		}

		return result;
	}

	private static String BuildChildren(Polygonia z) {
		String result = "";

		for (Polygonia zone : z.getChildren().values()) {
			result = result + zone.getTag() + " ";
		}

		return result;
	}

	private static String BuildPointList(Polygonia z) {
		String result = "";
		Polygon poly = z.getPolygon();

		for (int i = 0; i < poly.npoints; i++) {
			result = result + poly.xpoints[i] + ":" + poly.ypoints[i] + " ";
		}

		return result;
	}

	public static Polygonia getZoneForPoint(Player player, PolygoniaPlayer ezp,
			int playerHeight, Point playerPoint, String worldName) {
		Polygonia result = null;
		String resultTag = "";

		for (Polygonia zone: Zones.values()) {
			resultTag = isPointInZone(zone, playerHeight, playerPoint,worldName);
			if (resultTag.length() <= 0)
				continue;
			result = Zones.get(resultTag);
			break;
		}

		return result;
	}

	public static String isPointInZone(Polygonia zone, int playerHeight,
			Point playerPoint, String worldName) {
		String result = "";

		if (zone.hasChildren()) {
			for (Polygonia ChildZone : zone.getChildren().values()) {
				result = isPointInZone(
						(Polygonia) zone.getChildren().get(ChildZone.getTag()),
						playerHeight, playerPoint, worldName);
				if (result.length() > 0) {
					return result;
				}
			}
		}

		if (worldName.equalsIgnoreCase(zone.getWorld())) {
			if ((playerHeight >= zone.getFloor())
					&& (playerHeight <= zone.getCeiling())) {
				if (zone.pointWithin(playerPoint)) {
					result = zone.getTag();
				}
			}
		}

		return result;
	}

	public static boolean pointWithinBorder(Point point, Player player) {
		if (General.config.enableRadius) {
			PolygoniaPlayer ezp = General.getPlayer(player.getName());
			double xsquared = point.x * point.x;
			double ysquared = point.y * point.y;
			double distanceFromCenter = Math.sqrt(xsquared + ysquared);

			ezp.setDistanceFromCenter((int) distanceFromCenter);

			if (distanceFromCenter <= General.config.mapRadius) {
				if (ezp.getPastBorder()) {
					General.WarnPlayer(player, ezp,
							"You are inside the map radius border.");
					ezp.setPastBorder(false);
				}
				return true;
			}

			if (Polygonias.permissions.has(player, "epiczones.ignoremapradius")) {
				if (!ezp.getPastBorder()) {
					General.WarnPlayer(player, ezp,
							"You are outside the map radius border.");
					ezp.setPastBorder(true);
				}
				return true;
			}

			return false;
		}

		return true;
	}

	public void setPVP(boolean booleanValue) {
		this.hasPVP=booleanValue;
	}
}