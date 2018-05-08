package serwer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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


	
	public FILReplyLocationElement[] findLocation(double latitude, double longitude, double radius, String[] tags) {

		List<Long> tags_ID = new ArrayList<Long>();
		List<FILReplyLocationElement> result = new ArrayList<FILReplyLocationElement>();
		if (tags.length > 0) {
			tags_ID = findTags(tags);
		}

		double deltaLat = radius * 0.00001451;
		double deltaLong = radius * 0.00000878;

		try {
			String query = "SELECT Location_ID, Location_Name, Latitude_cord, Longitude_cord, Tags, Description "
					+ "FROM Location " + "WHERE (Latitude_cord BETWEEN ? AND ?) AND (Longitude_cord BETWEEN ? AND ?)";
			if (tags_ID.size() > 0) {
				query = query.concat(" AND (Tags LIKE '%" + Long.toString(tags_ID.get(0)) + "%'");
				if (tags_ID.size() > 1) {
					for (int i = 1; i < tags_ID.size(); i++) {
						query = query.concat(" OR Tags LIKE '%" + Long.toString(tags_ID.get(i)) + "%'");
					}

				}
				query = query.concat(") GROUP BY Location_ID;");
			}
			else {
				query = query.concat(" GROUP BY Location_ID;");
			}
			
			
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, Double.toString(latitude - deltaLat));
			ps.setString(2, Double.toString(latitude + deltaLat));
			ps.setString(3, Double.toString(longitude - deltaLong));
			ps.setString(4, Double.toString(longitude + deltaLong));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(new FILReplyLocationElement(rs.getString("Location_Name"), rs.getDouble("Latitude_cord"),
						rs.getDouble("Longitude_cord"), rs.getString("Description")));

			}
			rs.close();
			ps.close();
			c.close();

		} catch (SQLException e) {
			System.err.println("Cannot execute this find place statment");
			e.printStackTrace();
		}
		return result.toArray(new FILReplyLocationElement[result.size()]);
	}

	public FILReplyEventElement[] findEvent(double latitude, double longitude, double radius, String[] tags) {

		List<Long> tags_ID = new ArrayList<Long>();
		List<FILReplyEventElement> result = new ArrayList<FILReplyEventElement>();
		if (tags.length > 0) {
			tags_ID = findTags(tags);
		}

		
		double deltaLat = radius * 0.00001451;
		double deltaLong = radius * 0.00000878;

		try {
			String query = "SELECT Event_ID, Event_Name, Latitude_cord, Longitude_cord, Date, Time, Tags, Description "
					+ "FROM Events "
					+ "WHERE Time >= ? AND Date == ? AND (Latitude_cord BETWEEN ? AND ?) AND (Longitude_cord BETWEEN ? AND ?)";
			if (tags_ID.size() > 0) {
				query = query.concat(" AND (Tags LIKE '%" + Long.toString(tags_ID.get(0)) + "%'");
				if (tags_ID.size() > 1) {
					for (int i = 1; i < tags_ID.size(); i++) {
						query = query.concat(" OR Tags LIKE '%" + Long.toString(tags_ID.get(i)) + "%'");
					}

				}
				query = query.concat(") GROUP BY Event_ID;");
			}
			else {
				query = query.concat(" GROUP BY Event_ID;");
			}
			

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat date = new SimpleDateFormat("yyyy/mm/dd");

			PreparedStatement ps = c.prepareStatement(query);

			ps.setString(1, time.format(cal.getTime()));
			ps.setString(2, date.format(cal.getTime()));
			ps.setString(3, Double.toString(latitude - deltaLat));
			ps.setString(4, Double.toString(latitude + deltaLat));
			ps.setString(5, Double.toString(longitude - deltaLong));
			ps.setString(6, Double.toString(longitude + deltaLong));

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(new FILReplyEventElement(rs.getString("Event_Name"), rs.getDouble("Latitude_cord"),
						rs.getDouble("Longitude_cord"), rs.getString("Date"), rs.getString("Time"),
						rs.getString("Description")));

			}
			rs.close();
			ps.close();
			c.close();

		} catch (SQLException e) {
			System.err.println("Cannot execute this find event statment");
			e.printStackTrace();
		}

		return result.toArray(new FILReplyEventElement[result.size()]);

	}

	public boolean addLocation(Location location) {
		boolean correct = false;
		try {
			PreparedStatement ps = c.prepareStatement(
					"INSERT INTO Location (Location_ID, Location_Name, Latitude_cord, Longitude_cord, Tags, Owner, Description)"
							+ " VALUES (?,?,?,?,?,?,?);");
			ps.setString(1, null);
			ps.setString(2, location.getName());
			ps.setDouble(3, location.getLatitude());
			ps.setDouble(4, location.getLongitude());
			ps.setString(5, location.getTags());
			ps.setString(6, location.getOwner());
			ps.setString(7, location.getDescription());
			ps.executeUpdate();

			ps.close();
			c.close();
			correct = true;

		} catch (SQLException e) {
			System.err.println("Cannot add place");
			e.printStackTrace();
		}
		return correct;
	}

	public boolean addEvent(Event event) {
		boolean correct = false;
		try {
			PreparedStatement ps = c.prepareStatement(
					"INSERT INTO Events (Event_ID, Event_Name, Latitude_cord, Longitude_cord, Date, Time, Tags, Owner, Description)"
							+ " VALUES (?,?,?,?,?,?,?,?,?);");
			ps.setString(1, null);
			ps.setString(2, event.getName());
			ps.setDouble(3, event.getLatitude());
			ps.setDouble(4, event.getLongitude());
			ps.setString(5, event.getDate());
			ps.setString(6, event.getTime());
			ps.setString(7, event.getTags());
			ps.setString(8, event.getOwner());
			ps.setString(9, event.getDescription());
			ps.executeUpdate();

			ps.close();
			c.close();

			correct = true;
		} catch (SQLException e) {
			System.err.println("Cannot add event");
			e.printStackTrace();
		}
		return correct;
	}

	public Event[] findOwnEvent(String login) {

		List<Event> result = new ArrayList<Event>();
		try {
			String query = "SELECT * FROM Events WHERE Owner = ? GROUP BY Event_ID";

			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, login);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				result.add(new Event(rs.getInt("Event_ID"), rs.getString("Event_Name"), rs.getDouble("Latitude_cord"),
						rs.getDouble("Longitude_cord"), rs.getString("Date"), rs.getString("Time"),
						rs.getString("Tags"), login, rs.getString("Description")));

			}
			rs.close();
			ps.close();
			c.close();

		} catch (SQLException e) {
			System.err.println("Cannot find own locations");
			e.printStackTrace();
		}
		return result.toArray(new Event[result.size()]);
	}

	public Location[] findOwnLocation(String login) {

		List<Location> result = new ArrayList<Location>();
		try {
			String query = "SELECT * FROM Location WHERE Owner = ? GROUP BY Location_ID";

			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, login);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				result.add(new Location(rs.getInt("Location_ID"), rs.getString("Location_Name"),
						rs.getDouble("Latitude_cord"), rs.getDouble("Longitude_cord"), rs.getString("Tags"), login,
						rs.getString("Description")));
			}
			rs.close();
			ps.close();
			c.close();

		} catch (SQLException e) {
			System.err.println("Cannot find own locations");
			e.printStackTrace();
		}
		return result.toArray(new Location[result.size()]);
	}

	protected List<Long> findTags(String[] tags) {

		List<Long> tags_ID = new ArrayList<Long>();
		try {
			String query = "SELECT * FROM Tags WHERE ";
			for (int i = 0; i < tags.length - 1; i++) {
				query = query.concat(" Tag LIKE '" + tags[i] + "' OR");
			}
			query = query.concat(" Tag LIKE '" + tags[tags.length - 1] + "' GROUP BY Tag_ID;");

			PreparedStatement ps = c.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
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

	Operations() {

	}
}
