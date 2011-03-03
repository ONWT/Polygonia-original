package org.gotdns.noobs.Polygonias.CommandHandlers;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
	public static boolean Process(String[] data, CommandSender sender) {
		if (sender instanceof Player) {
			Player mplayer=(Player) sender;
			PolygoniaPlayer ezp = General
			.getPlayer(sender);
			int playerID = ezp.getEntityID();
			if(data.length>1)
			{
				boolean admin=isAdmin(mplayer);
				switch (Commands.valueOf(data[0].toLowerCase())) {
				case create:
					if(admin)
					{
						Create(data,mplayer, ezp, playerID);
						return true;
					}
					break;
				case save:
					Save(data, mplayer, ezp, playerID);
					return true;
				case flag:
					Flag(data, mplayer, ezp, playerID);
					return true;
				case floor:
					Floor(data, mplayer, ezp, playerID);
					return true;
				case ceiling:
					Ceiling(data, mplayer, ezp, playerID);
					return true;
				case addowner:
					addowner(data, mplayer, ezp, playerID);
					return true;
				case addmember:
					addmember(data, mplayer, ezp, playerID);
					return true;
				case removeowner:
					removeowner(data, mplayer, ezp, playerID);
					return true;
				case removemember:
					removemember(data, mplayer, ezp, playerID);
					return true;
				case createchild:
					CreateChild(data, mplayer, ezp, playerID);
					return true;
				case addchildren:
					if(admin)
					{
						AddChildren(data, mplayer, ezp, playerID);
						return true;
					}
					break;
				case removechildren:
					RemoveChildren(data, mplayer, ezp, playerID);
					return true;
				case name:
					Name(data, mplayer, ezp, playerID);
					return true;
				case enter:
					EnterMessage(data, mplayer, ezp, playerID);
					return true;
				case exit:
					LeaveMessage(data, mplayer, ezp, playerID);
					return true;
				case draw:
					if(admin)
					{
						Draw(data, mplayer, ezp, playerID);
						return true;
					}
					break;
				case confirm:
					Confirm(data, mplayer, ezp, playerID);
					return true;
				case edit:
					if(isOwner(mplayer, Polygonia.getInstance().getZone(data[2])))
					{
						Edit(data, mplayer, ezp, playerID);
						return true;
					}
					break;
				case world:
					if(admin)
					{
						World(data, mplayer, ezp, playerID);
						return true;
					}
					break;
				case cancel:
					Cancel(data, mplayer, ezp, playerID);
					return true;
				case delete:
					Delete(data, mplayer, ezp, playerID);
					return true;
				default:
					Help(mplayer, ezp, playerID);
					break;
				}
			}else
			{
				Help(mplayer, ezp, playerID);
			}
			return false;
		}
		return false;
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
					(PolygoniaPlayer.PolygoniaMode) value);
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

	private static void addmember(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				for (int i = 1; i < data.length; i++) {
					Set(playerID, "addmember", data[i]);
					SendMessage(mplayer, "Zone Updated. Added member:" + data[i]);
				}
			}
		} else {
			Help(mplayer, ezp, playerID);
		}

	}

	private static void addowner(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				for (int i = 1; i < data.length; i++) {
					Set(playerID, "addowner", data[i]);
					SendMessage(mplayer, "Zone Updated. Added owner:" + data[i]);
				}
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void removemember(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				for (int i = 1; i < data.length; i++) {
					Set(playerID, "removemember", data[i]);
					SendMessage(mplayer, "Zone Updated. Removed member:"
							+ data[i]);
				}
			}
		} else {
			Help(mplayer, ezp, playerID);
		}

	}

	private static void removeowner(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				for (int i = 1; i < data.length; i++) {
					Set(playerID, "removeowner", data[i]);
					SendMessage(mplayer, "Zone Updated. Removed owner:" + data[i]);
				}
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void SendMessage(Player mplayer, String message) {
		mplayer.sendMessage(message);
	}

	private static void Create(String[] data,
			Player mplayer, PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.None) {
			if ((data.length > 1) && (data[1].length() > 0)) {
				String tag = data[1].replaceAll("[^a-zA-Z0-9]", "");
				if (Polygonia.getInstance().getZone(tag) == null) {
					Polygonia zone=null;
					try {
						zone = Polygonia.getInstance().addZone(tag);
					} catch (Exception e) {
						System.out.println("Addzone failed in create whit:" +e.getMessage());
						e.printStackTrace();
					}
					Set(playerID, "editzone", zone);
					Set(playerID, "mode",PolygoniaPlayer.PolygoniaMode.ZoneDraw);
					Set(playerID, "world", mplayer.getWorld().getName());
					SendMessage(mplayer,
							"Zone Created. Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				} else {
					SendMessage(mplayer, "A zone already exists with the tag ["+ tag + "]");
				}
			} else {
				Help(mplayer, ezp, playerID);
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void CreateChild(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {

				String tag = data[1].replaceAll("[^a-zA-Z0-9]", "");
				String ptag = ezp.getEditZone().getTag();
				if (Polygonia.getInstance().getZone(tag) == null) {
					Polygonia zone=null;
					try {
						zone = Polygonia.getInstance().addZone(tag);
					} catch (Exception e) {
						System.out.println("Addzone failed in CreateChild whit:" +e.getMessage());
						e.printStackTrace();
					}
					zone.setParent(Polygonia.getInstance().getZone(ptag));
					Set(playerID, "addchild", Polygonia.getInstance().getZone(tag));
					Set(playerID, "editzone", zone);
					Set(playerID, "mode",PolygoniaPlayer.PolygoniaMode.ZoneDrawChild);
					Set(playerID, "world", mplayer.getWorld().getName());
					SendMessage(
							mplayer,
							"Zone Created. Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				}
			} else {
				SendMessage(mplayer, "You are currently not editing a zone");
				Help(mplayer, ezp, playerID);
			}
	}
	
	private static void Save(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDraw
				|| ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDrawChild) {
			if (ezp.getEditZone().getPolygon().npoints > 2) {
				Set(playerID, "mode", PolygoniaPlayer.PolygoniaMode.ZoneEdit);
				Set(playerID, "boundingbox", "");
				SendMessage(
						mplayer,
						"Drawing Complete. It's reccomended you set the name of your zone now with /zone name [value], or type /zone for more options.");
			} else {
				SendMessage(mplayer,
						"You must draw at least 3 points before you can move on.");
			}
		} else if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			//General.SaveZones();
			Set(playerID, "mode", PolygoniaPlayer.PolygoniaMode.None);
			SendMessage(mplayer, "Zone Saved.");
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void Flag(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if ((data.length > 2) && (data[1].length() > 0)
					&& (data[2].length() > 0)) {
				String flag = data[1];
				String value = "";
				for (int i = 2; i < data.length; i++) {
					value = value + data[i] + " ";
				}
				if (ValidFlag(flag)) {
					Set(playerID, "flag:" + flag.toLowerCase(), value);
					SendMessage(mplayer, "Zone Updated. Flag:" + flag
							+ " set to: " + value);
				} else {
					SendMessage(mplayer, "The flag [" + flag
							+ "] is not a valid flag.");
					SendMessage(mplayer, "Valid flags are: pvp");
				}
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void Floor(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if ((data.length > 1) && (IsNumeric(data[1]))) {
				Integer value = Integer.valueOf(Integer.parseInt(data[2]));
				Set(playerID, "floor", value);
				SendMessage(mplayer, "Zone Updated. Floor to: " + value);
			} else {
				SendMessage(mplayer, "[" + data[1]
						+ "] is not a valid value for floor.");
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void Ceiling(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if ((data.length > 1) && (IsNumeric(data[1]))) {
				Integer value = Integer.valueOf(Integer.parseInt(data[2]));
				Set(playerID, "ceiling", value);
				SendMessage(mplayer, "Zone Updated. Ceiling to: " + value);
			} else {
				SendMessage(mplayer, "[" + data[1]
						+ "] is not a valid value for ceiling.");
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void AddChildren(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				for (int i = 1; i < data.length; i++) {
					String tag = data[i].replaceAll("[^a-zA-Z0-9]", "");
					if ((tag.length() <= 0)
							|| (Polygonia.getInstance().getZone(tag) == null))
						continue;
					Set(playerID, "addchildtag", tag);
					Set(playerID, "addchild", Polygonia.getInstance().getZone(tag));
				}
				SendMessage(mplayer, "Zone Children Updated.");
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void RemoveChildren(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				for (int i = 1; i < data.length; i++) {
					String tag = data[i].replaceAll("[^a-zA-Z0-9]", "");
					if (tag.length() <= 0)
						continue;
					Set(playerID, "removechild", tag);
				}

				SendMessage(mplayer, "Zone Children Updated.");
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void Name(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				String message = "";
				for (int i = 1; i < data.length; i++) {
					message = message + data[i] + " ";
				}
				if (message.length() > 0) {
					Set(playerID, "name", message.trim());
					SendMessage(mplayer, "Zone Updated. Name set to: " + message);
				}
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void EnterMessage(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				String message = "";
				for (int i = 1; i < data.length; i++) {
					message = message + data[i] + " ";
				}
				if (message.length() > 0) {
					Set(playerID, "entermessage", message.trim());
					SendMessage(mplayer, "Zone Updated. Enter message set to: "
							+ message);
				}
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void LeaveMessage(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				String message = "";
				for (int i = 1; i < data.length; i++) {
					message = message + data[i] + " ";
				}
				if (message.length() > 0) {
					Set(playerID, "exitmessage", message.trim());
					SendMessage(mplayer, "Zone Updated. Exit message set to: "
							+ message);
				}
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void Draw(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			Set(playerID, "mode", PolygoniaPlayer.PolygoniaMode.ZoneDrawConfirm);
			SendMessage(
					mplayer,
					"WARNING! Entering draw mode will erase all points for the zone! type /zone draw confirm or /zone draw deny.");
		} else if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDrawConfirm) {
			if (data.length > 1) {
				if (data[1].equalsIgnoreCase("confirm")) {
					Set(playerID, "mode",
							PolygoniaPlayer.PolygoniaMode.ZoneDraw);
					SendMessage(
							mplayer,
							"Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
				} else if (data[1].equalsIgnoreCase("deny")) {
					Set(playerID, "mode",
							PolygoniaPlayer.PolygoniaMode.ZoneEdit);
					SendMessage(mplayer,
							"Draw Mode canceled, back in Edit Mode. type /zone for more options.");
				}
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void World(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			if (data.length > 1) {
				if (data[1].length() > 0) {
					Set(playerID, "world", data[2]);
					SendMessage(mplayer, "Zone Updated. World set to: " + data[2]);
				}
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void Confirm(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDeleteConfirm) {
			try {
				Polygonia.getInstance().delZone(ezp.getEditZone().getTag());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Polygonia.SaveZones();
			try {
				Polygonia.loadZones(null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SendMessage(mplayer, "Zone [" + ezp.getEditZone().getTag()
					+ "] has been deleted.");
			Set(playerID, "mode", PolygoniaPlayer.PolygoniaMode.None);
			Set(playerID, "editzone", null);
		} else if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDrawConfirm) {
			Set(playerID, "mode", PolygoniaPlayer.PolygoniaMode.ZoneDraw);
			Set(playerID, "clearpoints", "");
			SendMessage(
					mplayer,
					"Start drawing your zone with the zone edit tool. Type /zone save when you are done drawing.");
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void Edit(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.None) {
			if (data.length > 1) {
				if (data[1].length() > 0) {
					String tag = data[1].replaceAll("[^a-zA-Z0-9]", "");
					if (Polygonia.getInstance().getZone(data[1]) != null) {

						Set(playerID, "editzone", Polygonia.getInstance().getZone(tag));
						Set(playerID, "mode",
								PolygoniaPlayer.PolygoniaMode.ZoneEdit);
						SendMessage(mplayer, "Editing Zone: " + tag);
					} else {
						SendMessage(mplayer, "No sutch zone as: " + tag);
					}
				}
			}
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void Cancel(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if ((ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit)
				|| (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDraw)) {
			Set(playerID, "mode", PolygoniaPlayer.PolygoniaMode.None);
			Set(playerID, "editzone", null);
			SendMessage(mplayer,
					"Zone modification cancelled, no changes were saved.");
		} else if ((ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDrawConfirm)
				|| (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDeleteConfirm)) {
			Set(playerID, "mode", PolygoniaPlayer.PolygoniaMode.ZoneEdit);
			SendMessage(mplayer,
					"Draw Mode canceled, back in Edit Mode. type /zone for more options.");
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void Delete(String[] data, Player mplayer,
			PolygoniaPlayer ezp, int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			Set(playerID, "mode",
					PolygoniaPlayer.PolygoniaMode.ZoneDeleteConfirm);
			SendMessage(mplayer, "To continue deleting the zone ["
					+ ezp.getEditZone().getTag() + "] type /zone confirm.");
		} else {
			Help(mplayer, ezp, playerID);
		}
	}

	private static void Help(Player mplayer, PolygoniaPlayer ezp,
			int playerID) {
		if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneEdit) {
			SendMessage(mplayer,
					"You are currently in Edit mode. The following commands are available.");
			SendMessage(mplayer,
					"/zone name [value] - Sets the name of the zone you are currently editing.");
			SendMessage(mplayer,
					"/zone flag [pvp] [true|false] - Sets the indicated flag true or false.");
			SendMessage(mplayer,
					"/zone floor [value] Sets the floor of the zone you are currently editing.");
			SendMessage(mplayer,
					"/zone ceiling [value] Sets the ceiling of the zone you are currently editing.");
			SendMessage(
					mplayer,
					"/zone addchildren|removechildren [value] [value]... - Adds or removes children from the zone you are currently editing.");
			SendMessage(
					mplayer,
					"/zone addowner|removeowner [value] [value]... - Adds or removes owner from the zone you are currently editing.");
			SendMessage(
					mplayer,
					"/zone addmember|removemember [value] [value]... - Adds or removes member from the zone you are currently editing.");
			SendMessage(
					mplayer,
					"/zone enter|exit [value] Sets the enter or exit message of the zone you are currently editing.");
			SendMessage(mplayer,
					"/zone world [value] world of the zone you are currently editing.");
			SendMessage(mplayer,
					"/zone draw - Prompts you to go back into Draw mode.");
			SendMessage(mplayer,
					"/zone cancel - Discards all changes for the current zone you are editing.");
			SendMessage(mplayer,
					"/zone delete - Deletes the zone you are currently editing.");
			SendMessage(
					mplayer,
					"/zone save - Saves all changes for the current zone you are editing, and dumps you out of edit mode.");
		} else if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDraw) {
			SendMessage(mplayer,
					"You are currently in Draw mode. The following commands are available.");
			SendMessage(mplayer,
					"/zone save - Saves the point data you have drawn and puts you into Edit mode.");
			SendMessage(
					mplayer,
					"/zone cancel - Discards all changes for the current zone you are editing and dumps you out of Draw and Edit mode.");
		} else if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDrawChild) {
			SendMessage(mplayer,
					"You are currently in Draw mode. The following commands are available.");
			SendMessage(mplayer,
					"/zone save - Saves the point data you have drawn and puts you into Edit mode.");
			SendMessage(
					mplayer,
					"/zone cancel - Discards all changes for the current zone you are editing and dumps you out of Draw and Edit mode.");
		} else if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDrawConfirm) {
			SendMessage(mplayer,
					"You are currently in Draw Confirm mode. The following commands are available.");
			SendMessage(
					mplayer,
					"/zone confirm - Clears point data for the current zone and puts you into Draw mode.");
			SendMessage(mplayer, "/zone cancel - Puts you back into EditMode.");
		} else if (ezp.getMode() == PolygoniaPlayer.PolygoniaMode.ZoneDrawConfirm) {
			SendMessage(
					mplayer,
					"You are currently in Delete Confirm mode. The following commands are available.");
			SendMessage(mplayer,
					"/zone confirm - Deletes the zone you are currently editing.");
			SendMessage(mplayer, "/zone cancel - Puts you back into EditMode.");
		} else {
			SendMessage(mplayer,
					"To use the /zone command, type one of the following commands.");
			SendMessage(mplayer,
					"/zone edit [tag] - Edits an existing zone and puts you into Edit mode.");
			SendMessage(mplayer,
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