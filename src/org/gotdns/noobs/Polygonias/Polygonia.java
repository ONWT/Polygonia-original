package org.gotdns.noobs.Polygonias;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

/**
 * @author noobs
 * 
 */
public class Polygonia {
	// TODO: Make singleton out off this class
	// TODO:Player specific flags
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
	private Map<String, Polygonia> children = new HashMap<String, Polygonia>();
	private Set<String> childrenNames = new HashSet<String>();
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

	public Polygonia() {
	}

	// TODO: Remove ugliness
	public Polygonia(Polygonia prime) {
		this.tag = prime.tag;
		this.name = prime.name;
		this.floor = prime.floor;
		this.ceiling = prime.ceiling;
		this.world = prime.world;
		this.polygon = prime.polygon;
		this.boundingBox = prime.boundingBox;
		this.enterText = prime.enterText;
		this.exitText = prime.exitText;
		this.parent = prime.parent;
		this.children = prime.children;
		this.childrenNames = prime.childrenNames;
		this.hasParentFlag = prime.hasParentFlag;
		this.hasPVP = prime.hasPVP;
		this.hasRegen = prime.hasRegen;
		this.lastRegen = prime.lastRegen;
		this.regenAmount = prime.regenAmount;
		this.regenDelay = prime.regenDelay;
		this.regenInterval = prime.regenInterval;
	}

	public Polygonia(String zoneData) {
		String[] split = zoneData.split("\\|");
		if (split.length==1)
		{
			this.tag=zoneData;
			this.name=zoneData;
			this.enterText="You are now entering "+ this.name;
			this.exitText="You are now exiting "+ this.name;
			if (General.myZones.get(this.getTag()) == null) {
				General.myZones.put(this.getTag(),this);
				General.myZoneTags.add(this.getTag());
			} else {
				General.myZones.remove(this.getTag());
				General.myZones.put(this.getTag(),this);
			}
		}
		if (split.length == 10) {
			this.tag = split[0].replaceAll("[^a-zA-Z0-9]", "");
			this.world = split[1];
			this.name = split[2];
			this.enterText = split[4];
			this.exitText = split[5];
			this.floor = Integer.valueOf(split[6]).intValue();
			this.ceiling = Integer.valueOf(split[7]).intValue();
			this.parent = null;
			this.children = null;

			buildFlags(split[3]);
			buildChildren(split[8]);
			buildPolygon(split[9]);

			this.boundingBox = this.polygon.getBounds();

			System.out.println("Created Zone [" + this.name + "]");
		}
		if (split.length > 10) {
			this.tag = split[0].replaceAll("[^a-zA-Z0-9]", "");
			this.world = split[1];
			this.name = split[2];
			this.enterText = split[4];
			this.exitText = split[5];
			this.floor = Integer.valueOf(split[6]).intValue();
			this.ceiling = Integer.valueOf(split[7]).intValue();
			this.parent = null;
			this.children = null;

			buildFlags(split[3]);
			buildChildren(split[8]);
			buildPolygon(split[9]);
			buildOwners(split[10]);
			buildMembers(split[11]);

			this.boundingBox = this.polygon.getBounds();

			System.out.println("Created Zone [" + this.name + "]");
		}
	}

	private void buildMembers(String data) {
		if (data.length() > 0) {
			String[] dataList = data.split("\\s");

			for (int i = 0; i < dataList.length; i++) {
				this.member.add(dataList[1]);
			}
		}
	}

	private void buildOwners(String data) {
		if (data.length() > 0) {
			String[] dataList = data.split("\\s");

			for (int i = 0; i < dataList.length; i++) {
				this.owner.add(dataList[1]);
			}
		}
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

	public Set<String> getChildrenTags() {
		return this.childrenNames;
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

	private void buildFlags(String data) {
		if (data.length() > 0) {
			String[] dataList = data.split("\\s");

			for (int i = 0; i < dataList.length; i++) {
				String[] split = dataList[i].split(":");
				String flag = split[0].toLowerCase();

				if (flag.equals("pvp")) {
					this.hasPVP = split[1].equalsIgnoreCase("true");
				} else if (flag.equals("regen")) {
					if (split.length > 2) {
						if (split.length > 3) {
							setRegen(split[1] + " " + split[2] + " " + split[3]);
						} else {
							setRegen(split[1] + " " + split[2] + " 0");
						}
					} else {
						setRegen(split[1] + " 1");
					}
				} else
					flag.equals("mobs");
			}
		}
	}

	public void setPVP(boolean value) {
		this.hasPVP = value;
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

	private void buildChildren(String data) {
		if (data.length() > 0) {
			String[] dataList = data.split("\\s");

			for (int i = 0; i < dataList.length; i++) {
				this.childrenNames.add(dataList[i]);
			}
		}
	}

	private void buildPolygon(String data) {
		String[] dataList = data.split("\\s");
		this.polygon = new Polygon();
		for (int i = 0; i < dataList.length; i++) {
			String[] split = dataList[i].split(":");
			this.polygon.addPoint(Integer.valueOf(split[0]).intValue(), Integer
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
}