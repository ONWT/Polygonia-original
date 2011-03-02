package org.gotdns.noobs.Polygonias.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.gotdns.noobs.Polygonias.PolygoniasConfig;


public class iDatabase {

	private Connection Connection;

	public iDatabase() throws Exception {
		if(PolygoniasConfig.Database_Type.equalsIgnoreCase("sqlite")) {
			Class.forName("org.sqlite.JDBC");
            Connection = DriverManager.getConnection("jdbc:sqlite:" + PolygoniasConfig.Plugin_Directory + File.separator + PolygoniasConfig.SQL_Database + ".sqlite");
		} else if(PolygoniasConfig.Database_Type.equalsIgnoreCase("mysql")){
			Class.forName("com.mysql.jdbc.Driver");
			Connection = DriverManager.getConnection("jdbc:mysql://" + PolygoniasConfig.SQL_Hostname + ":" + PolygoniasConfig.SQL_Port + "/" + PolygoniasConfig.SQL_Database, PolygoniasConfig.SQL_Username, PolygoniasConfig.SQL_Password);
		}
	}

    public ResultSet resultQuery(String sql) {
		try {
			PreparedStatement ps = this.Connection.prepareStatement(sql);
			return ps.executeQuery();
		} catch(Exception e) {
            e.printStackTrace();
			return null;
		}
    }

	public ResultSet resultQuery(String sql, Object[] parameters) {
		try {
			PreparedStatement ps = this.Connection.prepareStatement(sql);
			for(int i = 0; i < parameters.length; i++)
				ps.setString(i + 1, parameters[i].toString());
			return ps.executeQuery();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    public boolean executeQuery(String sql) {
		try {
			PreparedStatement ps = this.Connection.prepareStatement(sql);
			ps.executeUpdate(sql);
			return true;
		} catch(Exception e) {
            e.printStackTrace();
			return false;
		}
    }

	public boolean executeQuery(String sql, Object[] parameters) {
		try {
			PreparedStatement ps = this.Connection.prepareStatement(sql);
			for(int i = 0; i < parameters.length; i++)
				ps.setString(i + 1, parameters[i].toString());
			ps.executeUpdate();
			return true;
		} catch(Exception e) {
            e.printStackTrace();
			return false;
		}
	}

	public Connection getConnection() {
		return Connection;
	}

}
