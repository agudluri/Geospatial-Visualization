This is to find the mapmatched point of a probe node to a link node. Go through the sample problem for better clarity.

Steps involved:
1. Find the closest probe to a particular link node.
2. Now find a second link node that is the closest to these two.
3. Form a triangle.
4. Using the principles of finding the area of a triangle, Pythogaras theorem and simple math to find the perpendicular 
	distance first, then the map matched point.

Note: Proceed only if you have a link node and its closest probe node first.

You can do this simply by finding distances between link nodes and probes.



A distance method you can use for finding the above:

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