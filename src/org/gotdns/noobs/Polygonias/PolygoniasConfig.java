package org.gotdns.noobs.Polygonias;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.bukkit.util.config.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class PolygoniasConfig extends Configuration {
	private static final Yaml yaml;
	private File file;
	public int mapRadius = 0;
	public boolean defaultEnter;
	public boolean defaultBuild;
	public boolean defaultDestroy;
	public boolean enableRadius;
	public int zoneTool = 280;
	
	public static String Plugin_Directory;
	// Database Type
	public static String Database_Type = "MySQL";

	// Relational SQL Generics
	public static String SQL_Hostname = "localhost";
	public static String SQL_Port = "3306";
	public static String SQL_Username = "root";
	public static String SQL_Password = "";

	// SQL Generics
	public static String SQL_Database = "minecraft";
	public static String SQL_Table = "iConomy";

	static {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yaml = new Yaml(options);
	}

	public PolygoniasConfig(File file) {
		super(file);

		this.file = file;

		if (file == null)
			throw new IllegalArgumentException("file cannot be null");
	}

	public void setDefaults() {
		this.mapRadius = 1000;
		this.defaultEnter = true;
		this.defaultBuild = true;
		this.defaultDestroy = true;
		this.enableRadius = true;
		this.zoneTool = 280;
	}

	public void load() {
		setDefaults();

		if (this.file == null) {
			throw new IllegalArgumentException("file cannot be null");
		}

		if (!this.file.exists()) {
			try {
				this.file.createNewFile();
				save();
			} catch (IOException localIOException) {
			}
		} else {
			super.load();
			// Database Configuration
			Database_Type = getString("System.Database.Type", Database_Type);

			// MySQL
			SQL_Hostname = getString("System.Database.MySQL.Hostname", SQL_Hostname);
			SQL_Port = getString("System.Database.MySQL.Port", SQL_Port);
			SQL_Username = getString("System.Database.MySQL.Username", SQL_Username);
			SQL_Password = getString("System.Database.MySQL.Password", SQL_Password);

			// SQLite
			SQL_Database = getString("System.Database.Name", SQL_Database);
			SQL_Table = getString("System.Database.Table", SQL_Table);

			this.mapRadius = getInt("mapRadius", this.mapRadius);
			this.defaultEnter = getBoolean("defaultEnter", true);
			this.defaultBuild = getBoolean("defaultBuild", true);
			this.defaultDestroy = getBoolean("defaultDestroy", true);
			this.enableRadius = getBoolean("enableRadius", true);
			this.zoneTool = getInt("zoneTool", this.zoneTool);
		}
	}

	public boolean save() {
		this.root.put("mapRadius", Integer.valueOf(this.mapRadius));
		this.root.put("defaultEnter", Boolean.valueOf(this.defaultEnter));
		this.root.put("defaultBuild", Boolean.valueOf(this.defaultBuild));
		this.root.put("defaultDestroy", Boolean.valueOf(this.defaultDestroy));
		this.root.put("enableRadius", Boolean.valueOf(this.enableRadius));
		this.root.put("zoneTool", Integer.valueOf(this.zoneTool));
		this.root.put("System.Database.MySQL.Hostname", String.valueOf(SQL_Hostname));
		this.root.put("System.Database.MySQL.Port", String.valueOf(SQL_Port));
		this.root.put("System.Database.MySQL.Username", String.valueOf(SQL_Username));
		this.root.put("System.Database.MySQL.Password", String.valueOf(SQL_Password));
		this.root.put("System.Database.Type", String.valueOf(Database_Type));
		this.root.put("System.Database.Name", String.valueOf(SQL_Database));
		this.root.put("System.Database.Table", String.valueOf(SQL_Table));
		try {
			FileOutputStream stream = new FileOutputStream(this.file);
			stream.getChannel().truncate(0L);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					stream));
			try {
				writer.write(yaml.dump(this.root));
			} finally {
				writer.close();
			}

		} catch (IOException e) {
			return false;
		}
		return true;
	}
}