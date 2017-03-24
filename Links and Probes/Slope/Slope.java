/**
 * The idea is to find the slope of point1 with respect to point2. Slope here is nothing but Tan theta, which is
 * the opposite side to the adjacent side in a triangle. To find the opposite side, we take the absolute difference
 * between the elevations of both the map-matched points, which would technically be the elevations of their 
 * corresponding probes.
 * 
 * @author Aman Gudluri
 *
 */
public class Slope {
	
	public static void main(String[] args) {
		new Slope().run();
	}

	public void run(){
		double Lat1 = //Enter first matched-point's latitude here;
		double Lon1 = //Enter first matched-point's longitude here;

		double Lat2 = //Enter second matched-point's latitude here;
		double Lon2 = //Enter second matched-point's longitude here;
		
		double alt = Math.abs(alt1-alt2); //alt1 is the altitude of the first map-matched point and alt2 is the same of the second
		
		double distance = (Distance(Lat1, Lon1, Lat2, Lon2, "K"))*1000;
		
		System.out.println("The Slope is "+alt/distance);
		
	}

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

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

}
