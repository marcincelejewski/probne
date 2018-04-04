package serwer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Operations {
	private Connection c;
	private final String url = "jdbc:postgresql://90.156.101.19/nick";
	private final String user = "nick";
	private final String password = "ktoco123";

	public boolean connect() {
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Cannot connect to databsase");
			return false;
		}
		System.out.println("Opened database successfully");
		return true;
	}

	public boolean login(String login, String password) {
		boolean correct = false;
		try {
			PreparedStatement ps = c.prepareStatement("SELECT * FROM Moderators WHERE login = ? AND password = ?;");
			ps.setString(1, login);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				correct = true;
			}
			rs.close();
			ps.close();
			c.close();

		} catch (SQLException e) {
			System.err.println("Cannot execute this login statment");
			e.printStackTrace();
		}
		return correct;
	}

	public String[] findPlace(double latitude, double longitude, double radius, String[] tags) {

		List<Long> tags_ID = new ArrayList<Long>();
		List<String> result = new ArrayList<String>();
		if (tags.length > 0) {
			tags_ID = findTags(tags);
		}

		
		double deltaLat = radius * 0.00001451;
		double deltaLong = radius * 0.00000878;

		try {
			String query = "SELECT Location_ID, Location_Name, Latitude_cord, Longitude_cord, Tags " + "FROM Location "
					+ "WHERE (Latitude_cord BETWEEN ? AND ?) AND (Longitude_cord BETWEEN ? AND ?)";
			if (tags_ID.size() > 0) {
				query.concat(" AND (Tags LIKE '%" + Long.toString(tags_ID.get(0)) + "%'");
				if (tags_ID.size() > 1) {
					for (int i = 1; i < tags_ID.size(); i++) {
						query.concat(" OR Tags LIKE '%" + Long.toString(tags_ID.get(i)) + "%'");
					}

				}
				query.concat(") GROUP BY Tag_ID;");
			}
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, Double.toString(latitude - deltaLat));
			ps.setString(2, Double.toString(latitude + deltaLat));
			ps.setString(3, Double.toString(longitude - deltaLong));
			ps.setString(3, Double.toString(longitude + deltaLong));
			ResultSet rs = ps.executeQuery(query);
			while (rs.next()) {
				String s = rs.getString("Location_Name") + "|" + Double.toString(rs.getDouble("Latitude_cord")) + "|"
						+ Double.toString(rs.getDouble("Latitude_cord")) + "|" + rs.getString("Description");
				result.add(s);

			}
			rs.close();
			ps.close();
			c.close();

		} catch (SQLException e) {
			System.err.println("Cannot execute this login statment");
			e.printStackTrace();
		}

		return result.toArray(new String[result.size()]);
	}

	public boolean findEvent(double latitude, double longitude, double radius, String tags) {

		return true;

	}

	public boolean addPlace(String name, String latitude, String longitude, String tags, String owner, String description )
	{
		boolean correct = false;
		try {
			PreparedStatement ps = c.prepareStatement("INSERT INTO Location (Location_ID, Location_Name, Latitude_cord, Longitude_cord, Tags, Owner, Description)"
					+ " VALUES (?,?,?,?,?,?,?);");
			ps.setString(1, null);
			ps.setString(2, name);
			ps.setString(3, latitude);
			ps.setString(4, longitude);
			ps.setString(5, tags);
			ps.setString(6, owner);
			ps.setString(7, description);
			ps.executeUpdate();
			correct = true;
			
			ps.close();
			c.close();

		} catch (SQLException e) {
			System.err.println("Cannot execute this login statment");
			e.printStackTrace();
		}
		return correct;
	}
	protected List<Long> findTags(String[] tags) {
		List<Long> tags_ID = new ArrayList<Long>();
		try {
			String query = "SELECT * FROM Tags WHERE ";
			for (int i = 0; i < tags.length - 1; i++) {
				query.concat("Tag LIKE '" + tags[i] + "' OR");
			}
			query.concat("Tag LIKE '" + tags[tags.length - 1] + "' GROUP BY Tag_ID;");

			PreparedStatement ps = c.prepareStatement(query);
			ResultSet rs = ps.executeQuery(query);
			while (rs.next()) {
				tags_ID.add(rs.getLong("Tag_ID"));
			}
			rs.close();
			ps.close();
			c.close();

		} catch (SQLException e) {
			System.err.println("Cannot find tags");
			e.printStackTrace();
		}
		return tags_ID;

	}
}
