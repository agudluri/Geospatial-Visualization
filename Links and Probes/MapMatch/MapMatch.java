import java.io.*;
import java.util.*;

/**
 * The idea here is, you first find the closest probe node to a certain link node. Once you have that, you can find the second closest
 * link node to the probe. Once you have both, you can form a triangle with the probe and can now find the map matched point.
 * 
 * @author Aman Gudluri
 *
 */
public class MapMatch{

	String[] LatAndLongs;
	double MatchedLat=0;
	double MatchedLon=0;

	public static void main(String[] args) {
		new MapMatch().run();
	}

	public void run(){
		
		int LinkID = //insert link ID here;
		double Lat1 = //insert first link node's latitude here;
		double Lat2 = //insert first link node's longitude here;
		double ProbeLat = //the closest probe points latitude goes here;
		double ProbeLon = //the closest probe points latitude goes here;
		
		String secondpoint = SecondPoint(LinkID, Lat1, Lat2, ProbeLat, ProbeLon);
		String secondpointsplit = secondpoint.split("/");
		String match = Match(Lat1, Lat2, secondpointsplit[0], secondpointsplit[1], ProbeLat, ProbeLon);

	}

	/**
	 * A method to calculate the second Link node if you have the first Link node and its corresponding probe point
	 * (This is to form a triangle to find the map matched point)
	 * 
	 * @param LinkID, the ID of the link
	 * @param Lat1, the latitude of the first link node
	 * @param Lat2, the longitude of the first link node
	 * @param ProbeLat, the latitude of the closest probe
	 * @param ProbeLon, the longitude of the closest probe
	 * @return A String with the second closest Link node to the probe
	 */
	public String SecondPoint(int LinkID, double Lat1, double Lat2, double ProbeLat, double ProbeLon) {
		
		/*
		 * The idea is, if you have a link node and its corresponding probe node, you could find the second
		 * link node by calculating the second closest link node(from the probe) of all the nodes in the link.
		 */

		ArrayList<Double> distances = new ArrayList<Double>();
		String result = null;
		Scanner LinkDataScanner;

		try {
			LinkDataScanner = new Scanner(new File("Partition6467LinkData.csv"));

			while (LinkDataScanner.hasNext()) {
				String[] data = LinkDataScanner.nextLine().split(",");

				String ID = data[0];

				if (ID.equals(LinkID)) {
					LatAndLongs = data[14].split("\\|");

					int i = 0;

					while (i < LatAndLongs.length) {

						String[] LatAndLong = LatAndLongs[i].split("/");

						double Lat = Double.parseDouble(LatAndLong[0]);
						double Lon = Double.parseDouble(LatAndLong[1]);

						double distance = Distance(Lat, Lon, ProbeLat, ProbeLon, "M"); //Calculating distances from link node to the probe node
						distances.add(distance);

						i++;
					}
					break;
				} // if
			}

			Collections.sort(distances);
			double SecondMinDistance = distances.get(1); //sorting the list and getting the second least number
			
			/*
			 * calculating distances again and checking if the distance is equal to the second least distance
			 * If yes, then that point would be the second link node we're looking for.
			 */
			int i = 0;
			while (i < LatAndLongs.length) {
				String[] LatAndLong = LatAndLongs[i].split("/");

				double Lat = Double.parseDouble(LatAndLong[0]);
				double Lon = Double.parseDouble(LatAndLong[1]);

				double distance = Distance(Lat, Lon, ProbeLat, ProbeLon, "M");

				if (distance == SecondMinDistance) {
					result = Lat + "/" + Lon;
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * A method to calculate the map matched point if you have two link nodes and the probe point.
	 * 
	 * @param LinkLat1, the latitude of the first link node
	 * @param LinkLon1, the longitude of the first link node
	 * @param LinkLat2, the latitude of the second link node
	 * @param LinkLon2, the longitude of the second link node
	 * @param ProbeLat, the latitude of the probe
	 * @param ProbeLon, the longitude of the probe
	 * 
	 * @return a string with the latitude and longitude of the map-matched point
	 */
	public String match(double LinkLat1, double LinkLon1, double LinkLat2, double LinkLon2, double ProbeLat, double ProbeLon) {
		
		StringBuilder MatchString = new StringBuilder();

		double cd = Distance(LinkLat1, LinkLon1, LinkLat2, LinkLon2, "M");
		double dp = Distance(LinkLat2, LinkLon2, ProbeLat, ProbeLon, "M");
		double pc = Distance(ProbeLat, ProbeLon, LinkLat1, LinkLon1, "M");

		double halfP = (cd + dp + pc) * 0.5; //half perimeter

		double area = Math.sqrt(halfP * (halfP - cd) * (halfP - dp) * (halfP - pc));

		double pm = 2 * area / cd; //This is the perpendicular distance from the map matched point to the link

		double hypotenuse = Math.sqrt((pc * pc) - (pm * pm)); //using Pythogoras theorem

		double t = hypotenuse / cd;

		 MatchedLat = (1 - t) * LinkLat2 + t * LinkLat1;
		 MatchedLon = (1 - t) * LinkLon2 + t * LinkLon1;

		MatchString.append(MatchedLat + "/" + MatchedLon);
		
		return MatchString.toString();
	}

	/**
	 * A method to find the distance between two GPS coordinates.
	 * 
	 * @param Lat1, the latitude of the first point
	 * @param Lon1, the longitude of the first point
	 * @param Lat2, the latitude of the second point
	 * @param Lon2, the longitude of the second point
	 * @param unit, the unit you want the distance in. 
	 * "K" for Kilometers, "M" for Miles, "N" for Nautical Miles
	 * 
	 * @return the distance between the two points
	 */
	private double Distance(double Lat1, double Lon1, double Lat2, double Lon2, String unit) {
		double theta = Lon1 - Lon2;
		double dist = Math.sin(deg2rad(Lat1)) * Math.sin(deg2rad(Lat2))
				+ Math.cos(deg2rad(Lat1)) * Math.cos(deg2rad(Lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == "K") {
			dist = dist * 1.609344;
		} else if (unit == "N") {
			dist = dist * 0.8684;
		}

		return (dist);
	}

	/**
	 * A method to convert degrees to radians.
	 * 
	 * @param deg, the value in degrees
	 * @return the value in radians
	 */
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/**
	 * A method to convert radians to degrees.
	 * 
	 * @param rad, the value in radians
	 * @return a double value in degrees
	 */
	private double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

}
