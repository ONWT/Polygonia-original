package org.gotdns.noobs.Polygonias.CommandHandlers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.gotdns.noobs.Polygonias.General;
import org.gotdns.noobs.Polygonias.Polygonia;
import org.gotdns.noobs.Polygonias.PolygoniaPlayer;
import org.gotdns.noobs.Polygonias.Polygonias;

public class ZoneCommandHandler {
	public static enum Commands{
		create,save,flag,floor,ceiling,addowner,
		addmember,removeowner,removemember,createchild,
		addchildren,removechildren,name,enter,exit,draw,
		confirm,edit,world,cancel,delete;
	}
	public static void Process(String[] data, PlayerChatEvent event) {
		event.setCancelled(true);
		PolygoniaPlayer ezp = General
				.getPlayer(event.getPlayer().getEntityId());
		int playerID = ezp.getEntityID();
		if(data.length>1)
		{
		boolean admin=isAdmin(event.getPlayer());
		switch (Commands.valueOf(data[1].toLowerCase())) {
		case create:
			if(admin)
				Create(data, event, ezp, playerID);
			break;
		case save:
				Save(data, event, ezp, playerID);
			break;
		case flag:
				Flag(data, event, ezp, playerID);
			break;
		case floor:
				Floor(data, event, ezp, playerID);
			break;
		case ceiling:
				Ceiling(data, event, ezp, playerID);
			break;
		case addowner:
				addowner(data, event, ezp, playerID);
			break;
		case addmember:
				addmember(data, event, ezp, playerID);
			break;
		case removeowner:
				removeowner(data, event, ezp, playerID);
			break;
		case removemember:
				removemember(data, event, ezp, playerID);
			break;
		case createchild:
				CreateChild(data, event, ezp, playerID);
			break;
		case addchildren:
			if(admin)
				AddChildren(data, event, ezp, playerID);
			break;
		case removechildren:
				RemoveChildren(data, event, ezp, playerID);
			break;
		case name:
				Name(data, event, ezp, playerID);
			break;
		case enter:
				EnterMessage(data, event, ezp, playerID);
			break;
		case exit:
				LeaveMessage(data, event, ezp, playerID);
			break;
		case draw:
			if(admin)
				Draw(data, event, ezp, playerID);
			break;
		case confirm:
				Confirm(data, event, ezp, playerID);
			break;
		case edit:
			if(isOwner(event.getPlayer(), General.myZones.get(data[2])))
				Edit(data, event, ezp, playerID);
			break;
		case world:
			if(admin)
				World(data, event, ezp, playerID);
			break;
		case cancel:
				Cancel(data, event, ezp, playerID);
			break;
		case delete:
				Delete(data, event, ezp, playerID);
			break;
		default:
			Help(event, ezp, playerID);
			break;
		}
		}else
		{Help(event, ezp, playerID);}
	}

	private static boolean isOwner(Player player,
			Polygonia polygonia) {
		if(Polygonias.permissions.has(player,"epiczones.admin"))
			return true;
		else if(polygonia.isOwner(player)&&polygonia.hasParent())
			return true;
		else if(polygonia.hasParent())
			if(polygonia.getParent().isOwner(player))
				return true;
		
		return false;
	}
	private static boolean isAdmin(Player player)
	{
	if(Polygonias.permissions.has(player,"epiczones.admin"))
		return true;
	else
		return false;
	}


	private static void Set(int playerID, String propertyName, Object value) {
		if (propertyName.equals("editzone"))
			General.getPlayer(playerID).setEditZone((Polygonia) value);
		else if (propertyName.equals("mode"))
			General.getPlayer(playerID).setMode(
					(PolygoniaPlayer.PloygoniaMode) value);
		else if (propertyName.equals("flag:pvp"))
			General.getPlayer(playerID).getEditZone()
					.setPVP(Boolean.valueOf((String) value).booleanValue());
		else if (propertyName.equals("flag:regen"))
			General.getPlayer(playerID).getEditZone().setRegen((String) value);
		else if (propertyName.equals("floor"))
			General.getPlayer(playerID).getEditZone()
					.setFloor(((Integer) value).intValue());
		else if (propertyName.equals("ceiling"))
			General.getPlayer(playerID).getEditZone()
					.setCeiling(((Integer) value).intValue());
		else if (propertyName.equals("entermessage"))
			General.getPlayer(playerID).getEditZone()
					.setEnterText((String) value);
		else if (propertyName.equals("exitmessage"))
			General.getPlayer(playerID).getEditZone()
					.setExitText((String) value);
		else if (propertyName.equals("name"))
			General.getPlayer(playerID).getEditZone().setName((String) value);
		else if (propertyName.equals("addchild"))
			General.getPlayer(playerID).getEditZone()
					.addChild((Polygonia) value);
		else if (propertyName.equals("addchildtag"))
			General.getPlayer(playerID).getEditZone().getChildrenTags()
					.add((String) value);
		else if (propertyName.equals("removechild"))
			General.getPlayer(playerID).getEditZone()
					.removeChild((String) value);
		else if (propertyName.equals("addowner"))
			General.getPlayer(playerID).getEditZone()
					.addOwner(General.getPlayer((String) value));
		else if (propertyName.equals("addmember"))
			General.getPlayer(playerID).getEditZone()
					.addMember(General.getPlayer((String) value));
		else if (propertyName.equals("removeowner"))
			General.getPlayer(playerID).getEditZone()
					.delOwner(General.getPlayer((String) value));
		else if (propertyName.equals("removemember"))
			General.getPlayer(playerID).getEditZone()
					.delMember(General.getPlayer((String) value));
		else if (propertyName.equals("clearpoints"))
			General.getPlayer(playerID).getEditZone().clearPolyPoints();
		else if (propertyName.equals("boundingbox"))
			General.getPlayer(playerID).getEditZone().rebuildBoundingBox();
		else if (propertyName.equals("world"))
			General.getPlayer(playerID).getEditZone().setWorld((String) value);
	}

	private static void addmember(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if (data.length > 2) {
				for (int i = 2; i < data.length; i++) {
					Set(playerID, "addmember", data[i]);
					SendMessage(event, "Zone Updated. Added member:" + data[i]);
				}
			}
		} else {
			Help(event, ezp, playerID);
		}

	}

	private static void addowner(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				for (int i = 2; i < data.length; i++) {
					Set(playerID, "addowner", data[i]);
					SendMessage(event, "Zone Updated. Added owner:" + data[i]);
				}
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void removemember(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if (data.length > 2) {
				for (int i = 2; i < data.length; i++) {
					Set(playerID, "removemember", data[i]);
					SendMessage(event, "Zone Updated. Removed member:"
							+ data[i]);
				}
			}
		} else {
			Help(event, ezp, playerID);
		}

	}

	private static void removeowner(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				for (int i = 2; i < data.length; i++) {
					Set(playerID, "removeowner", data[i]);
					SendMessage(event, "Zone Updated. Removed owner:" + data[i]);
				}
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void SendMessage(PlayerChatEvent event, String message) {
		event.getPlayer().sendMessage(message);
	}

	private static void Create(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.None) {
			if ((data.length > 2) && (data[2].length() > 0)) {
				String tag = data[2].replaceAll("[^a-zA-Z0-9]", "");
				if (General.myZones.get(tag) == null) {
					Polygonia zone = new Polygonia(tag);
					Set(playerID, "editzone", zone);
					Set(playerID, "mode",
							PolygoniaPlayer.PloygoniaMode.ZoneDraw);
					Set(playerID, "world", event.getPlayer().getWorld()
							.getName());
					SendMessage(
							event,
							"Zone Created. Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				} else {
					SendMessage(event, "A zone already exists with the tag ["
							+ tag + "]");
				}
			} else {
				Help(event, ezp, playerID);
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void CreateChild(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {

				String tag = data[2].replaceAll("[^a-zA-Z0-9]", "");
				String ptag = ezp.getEditZone().getTag();
				if (General.myZones.get(tag) == null) {
					Polygonia zone = new Polygonia(tag);
					//zone.setParent(General.myZones.get(ptag));
					Set(playerID, "addchildtag", tag);
					Set(playerID, "addchild", General.myZones.get(tag));
					Set(playerID, "editzone", zone);
					Set(playerID, "mode",
							PolygoniaPlayer.PloygoniaMode.ZoneDrawChild);
					Set(playerID, "world", event.getPlayer().getWorld()
							.getName());
					SendMessage(
							event,
							"Zone Created. Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				}
			} else {
				SendMessage(event, "You are currently not editing a zone");
				Help(event, ezp, playerID);
			}
	}
	
	private static void Save(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDraw
				|| ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDrawChild) {
			if (ezp.getEditZone().getPolygon().npoints > 2) {
				Set(playerID, "mode", PolygoniaPlayer.PloygoniaMode.ZoneEdit);
				Set(playerID, "boundingbox", "");
				SendMessage(
						event,
						"Drawing Complete. It's reccomended you set the name of your zone now with /zone name [value], or type /zone for more options.");
			} else {
				SendMessage(event,
						"You must draw at least 3 points before you can move on.");
			}
		} else if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if (General.myZones.get(ezp.getEditZone().getTag()) == null) {
				General.myZones.put(ezp.getEditZone().getTag(),
						ezp.getEditZone());
				General.myZoneTags.add(ezp.getEditZone().getTag());
			} else {
				General.myZones.remove(ezp.getEditZone().getTag());
				General.myZones.put(ezp.getEditZone().getTag(),
						ezp.getEditZone());
			}
			//General.SaveZones();
			Set(playerID, "mode", PolygoniaPlayer.PloygoniaMode.None);
			SendMessage(event, "Zone Saved.");
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void Flag(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if ((data.length > 3) && (data[2].length() > 0)
					&& (data[3].length() > 0)) {
				String flag = data[2];
				String value = "";
				for (int i = 3; i < data.length; i++) {
					value = value + data[i] + " ";
				}
				if (ValidFlag(flag)) {
					Set(playerID, "flag:" + flag.toLowerCase(), value);
					SendMessage(event, "Zone Updated. Flag:" + flag
							+ " set to: " + value);
				} else {
					SendMessage(event, "The flag [" + flag
							+ "] is not a valid flag.");
					SendMessage(event, "Valid flags are: pvp");
				}
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void Floor(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if ((data.length > 2) && (IsNumeric(data[2]))) {
				Integer value = Integer.valueOf(Integer.parseInt(data[2]));
				Set(playerID, "floor", value);
				SendMessage(event, "Zone Updated. Floor to: " + value);
			} else {
				SendMessage(event, "[" + data[2]
						+ "] is not a valid value for floor.");
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void Ceiling(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if ((data.length > 2) && (IsNumeric(data[2]))) {
				Integer value = Integer.valueOf(Integer.parseInt(data[2]));
				Set(playerID, "ceiling", value);
				SendMessage(event, "Zone Updated. Ceiling to: " + value);
			} else {
				SendMessage(event, "[" + data[2]
						+ "] is not a valid value for ceiling.");
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void AddChildren(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if (data.length > 2) {
				for (int i = 2; i < data.length; i++) {
					String tag = data[i].replaceAll("[^a-zA-Z0-9]", "");
					if ((tag.length() <= 0)
							|| (General.myZones.get(tag) == null))
						continue;
					Set(playerID, "addchildtag", tag);
					Set(playerID, "addchild", General.myZones.get(tag));
				}
				SendMessage(event, "Zone Children Updated.");
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void RemoveChildren(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if (data.length > 2) {
				for (int i = 2; i < data.length; i++) {
					String tag = data[i].replaceAll("[^a-zA-Z0-9]", "");
					if (tag.length() <= 0)
						continue;
					Set(playerID, "removechild", tag);
				}

				SendMessage(event, "Zone Children Updated.");
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void Name(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if (data.length > 2) {
				String message = "";
				for (int i = 2; i < data.length; i++) {
					message = message + data[i] + " ";
				}
				if (message.length() > 0) {
					Set(playerID, "name", message.trim());
					SendMessage(event, "Zone Updated. Name set to: " + message);
				}
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void EnterMessage(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if (data.length > 2) {
				String message = "";
				for (int i = 2; i < data.length; i++) {
					message = message + data[i] + " ";
				}
				if (message.length() > 0) {
					Set(playerID, "entermessage", message.trim());
					SendMessage(event, "Zone Updated. Enter message set to: "
							+ message);
				}
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void LeaveMessage(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if (data.length > 2) {
				String message = "";
				for (int i = 2; i < data.length; i++) {
					message = message + data[i] + " ";
				}
				if (message.length() > 0) {
					Set(playerID, "exitmessage", message.trim());
					SendMessage(event, "Zone Updated. Exit message set to: "
							+ message);
				}
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void Draw(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			Set(playerID, "mode", PolygoniaPlayer.PloygoniaMode.ZoneDrawConfirm);
			SendMessage(
					event,
					"WARNING! Entering draw mode will erase all points for the zone! type /zone draw confirm or /zone draw deny.");
		} else if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDrawConfirm) {
			if (data.length > 2) {
				if (data[2].equalsIgnoreCase("confirm")) {
					Set(playerID, "mode",
							PolygoniaPlayer.PloygoniaMode.ZoneDraw);
					SendMessage(
							event,
							"Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				} else if (data[2].equalsIgnoreCase("deny")) {
					Set(playerID, "mode",
							PolygoniaPlayer.PloygoniaMode.ZoneEdit);
					SendMessage(event,
							"Draw Mode canceled, back in Edit Mode. type /zone for more options.");
				}
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void World(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			if (data.length > 2) {
				if (data[2].length() > 0) {
					Set(playerID, "world", data[2]);
					SendMessage(event, "Zone Updated. World set to: " + data[2]);
				}
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void Confirm(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDeleteConfirm) {
			General.myZoneTags.remove(ezp.getEditZone().getTag());
			General.SaveZones();
			General.loadZones(null);
			SendMessage(event, "Zone [" + ezp.getEditZone().getTag()
					+ "] has been deleted.");
			Set(playerID, "mode", PolygoniaPlayer.PloygoniaMode.None);
			Set(playerID, "editzone", null);
		} else if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDrawConfirm) {
			Set(playerID, "mode", PolygoniaPlayer.PloygoniaMode.ZoneDraw);
			Set(playerID, "clearpoints", "");
			SendMessage(
					event,
					"Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void Edit(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.None) {
			if (data.length > 2) {
				if (data[2].length() > 0) {
					String tag = data[2].replaceAll("[^a-zA-Z0-9]", "");
					if (General.myZones.get(data[2]) != null) {

						Set(playerID, "editzone", new Polygonia(
								(Polygonia) General.myZones.get(tag)));
						Set(playerID, "mode",
								PolygoniaPlayer.PloygoniaMode.ZoneEdit);
						SendMessage(event, "Editing Zone: " + tag);
					} else {
						SendMessage(event, "No sutch zone as: " + tag);
					}
				}
			}
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void Cancel(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if ((ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit)
				|| (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDraw)) {
			Set(playerID, "mode", PolygoniaPlayer.PloygoniaMode.None);
			Set(playerID, "editzone", null);
			SendMessage(event,
					"Zone modification cancelled, no changes were saved.");
		} else if ((ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDrawConfirm)
				|| (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDeleteConfirm)) {
			Set(playerID, "mode", PolygoniaPlayer.PloygoniaMode.ZoneEdit);
			SendMessage(event,
					"Draw Mode canceled, back in Edit Mode. type /zone for more options.");
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void Delete(String[] data, PlayerChatEvent event,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			Set(playerID, "mode",
					PolygoniaPlayer.PloygoniaMode.ZoneDeleteConfirm);
			SendMessage(event, "To continue deleting the zone ["
					+ ezp.getEditZone().getTag() + "] type /zone confirm.");
		} else {
			Help(event, ezp, playerID);
		}
	}

	private static void Help(PlayerChatEvent event, PolygoniaPlayer ezp,
			int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneEdit) {
			SendMessage(event,
					"You are currently in Edit mode. The following commands are available.");
			SendMessage(event,
					"/zone name [value] - Sets the name of the zone you are currently editing.");
			SendMessage(event,
					"/zone flag [pvp] [true|false] - Sets the indicated flag true or false.");
			SendMessage(event,
					"/zone floor [value] Sets the floor of the zone you are currently editing.");
			SendMessage(event,
					"/zone ceiling [value] Sets the ceiling of the zone you are currently editing.");
			SendMessage(
					event,
					"/zone addchildren|removechildren [value] [value]... - Adds or removes children from the zone you are currently editing.");
			SendMessage(
					event,
					"/zone addowner|removeowner [value] [value]... - Adds or removes owner from the zone you are currently editing.");
			SendMessage(
					event,
					"/zone addmember|removemember [value] [value]... - Adds or removes member from the zone you are currently editing.");
			SendMessage(
					event,
					"/zone enter|exit [value] Sets the enter or exit message of the zone you are currently editing.");
			SendMessage(event,
					"/zone world [value] world of the zone you are currently editing.");
			SendMessage(event,
					"/zone draw - Prompts you to go back into Draw mode.");
			SendMessage(event,
					"/zone cancel - Discards all changes for the current zone you are editing.");
			SendMessage(event,
					"/zone delete - Deletes the zone you are currently editing.");
			SendMessage(
					event,
					"/zone save - Saves all changes for the current zone you are editing, and dumps you out of edit mode.");
		} else if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDraw) {
			SendMessage(event,
					"You are currently in Draw mode. The following commands are available.");
			SendMessage(event,
					"/zone save - Saves the point data you have drawn and puts you into Edit mode.");
			SendMessage(
					event,
					"/zone cancel - Discards all changes for the current zone you are editing and dumps you out of Draw and Edit mode.");
		} else if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDrawChild) {
			SendMessage(event,
					"You are currently in Draw mode. The following commands are available.");
			SendMessage(event,
					"/zone save - Saves the point data you have drawn and puts you into Edit mode.");
			SendMessage(
					event,
					"/zone cancel - Discards all changes for the current zone you are editing and dumps you out of Draw and Edit mode.");
		} else if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDrawConfirm) {
			SendMessage(event,
					"You are currently in Draw Confirm mode. The following commands are available.");
			SendMessage(
					event,
					"/zone confirm - Clears point data for the current zone and puts you into Draw mode.");
			SendMessage(event, "/zone cancel - Puts you back into EditMode.");
		} else if (ezp.getMode() == PolygoniaPlayer.PloygoniaMode.ZoneDrawConfirm) {
			SendMessage(
					event,
					"You are currently in Delete Confirm mode. The following commands are available.");
			SendMessage(event,
					"/zone confirm - Deletes the zone you are currently editing.");
			SendMessage(event, "/zone cancel - Puts you back into EditMode.");
		} else {
			SendMessage(event,
					"To use the /zone command, type one of the following commands.");
			SendMessage(event,
					"/zone edit [tag] - Edits an existing zone and puts you into Edit mode.");
			SendMessage(event,
					"/zone create [tag] - Creates a new zone and puts you into Draw mode.");
		}
	}

	private static boolean ValidFlag(String flag) {
		if (flag.equals("pvp"))
			return true;

		return flag.equals("regen");
	}

	private static boolean IsNumeric(String data) {
		return data.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+");
	}
}