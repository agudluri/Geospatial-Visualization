import java.io.*;

public class TileSystem {

	protected final static double EarthRadius = 6378137;
	protected final static double MinLatitude = -85.05112878;
	protected final static double MaxLatitude = 85.05112878;
	protected final static double MinLongitude = -180;
	protected final static double MaxLongitude = 180;

	/**
	 * A method to confine values to specified limits.
	 * 
	 * @param n,the value to be limited.
	 * @param minValue,
	 *            the minimum limit.
	 * @param maxValue,
	 *            the maximum limit.
	 * @return a double with the corrected value of the input.
	 */
	public static double Clip(double n, double minValue, double maxValue) {
		return Math.min(Math.max(n, minValue), maxValue);
	}

	/**
	 * Converts a point from latitude/longitude WGS-84 coordinates (in degrees)
	 * into pixel XY coordinates at a specified level of detail.
	 * 
	 * @param latitude,
	 *            the latitude of the geographical position.
	 * @param longitude,
	 *            the longitude of the geographical position.
	 * @param levelOfDetail,
	 *            the level of detail of the map.
	 */
	public static int[] LatLongToPixelXY(double latitude, double longitude, int levelOfDetail) {
		int[] result = new int[2];
		latitude = Clip(latitude, MinLatitude, MaxLatitude);
		longitude = Clip(longitude, MinLongitude, MaxLongitude);

		double x = (longitude + 180) / 360;
		double sinLatitude = Math.sin(latitude * Math.PI / 180);
		double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

		long mapSize = MapSize(levelOfDetail);
		int pixelX = (int) Clip(x * mapSize + 0.5, 0, mapSize - 1);
		int pixelY = (int) Clip(y * mapSize + 0.5, 0, mapSize - 1);
		result[0] = pixelX;
		result[1] = pixelY;
		return result;
	}

	/**
	 * Converts pixel XY coordinates into tile XY coordinates of the tile
	 * containing the specified pixel.
	 * 
	 * @param input,
	 *            an array with the pixel coordinates of the point.
	 * @return an integer array with the XY coordinates of the tile.
	 */
	public static int[] PixelXYToTileXY(int input[]) {
		int[] result = new int[2];
		int tileX = input[0] / 256;
		int tileY = input[1] / 256;
		result[0] = tileX;
		result[1] = tileY;
		return result;
	}

	/**
	 * Converts tile XY coordinates into a QuadKey at a specified level of
	 * detail.
	 * 
	 * @param tileX,
	 *            the X coordinate of the tile.
	 * @param tileY,
	 *            the X coordinate of the tile.
	 * @param levelOfDetail,
	 *            the level of detail of the map.
	 * @return a String with the quadkey of the input tile.
	 */
	public static String TileXYToQuadKey(int tileX, int tileY, int levelOfDetail) {
		StringBuilder quadKey = new StringBuilder();
		for (int i = levelOfDetail; i > 0; i--) {
			char digit = '0';
			int mask = 1 << (i - 1);
			if ((tileX & mask) != 0) {
				digit++;
			}
			if ((tileY & mask) != 0) {
				digit++;
				digit++;
			}
			quadKey.append(digit);
		}
		return quadKey.toString();
	}

	/**
	 * a method to find the maps height and width at a specified level of
	 * detail.
	 * 
	 * @param levelOfDetail,
	 *            the level of detail of the map.
	 * @return a long with the size of the map. (the height and width)
	 */
	public static long MapSize(int levelOfDetail) {
		return (long) 256 << levelOfDetail;
	}

	/**
	 * A method to calculate the ground resolution at a specified latitude and
	 * level of detail.
	 * 
	 * @param latitude,
	 *            the latitude of the location.
	 * @param levelOfDetail,
	 *            the level of detail of the map.
	 * @return a double with the ground resolution of the map.
	 */
	public static double GroundResolution(double latitude, int levelOfDetail) {
		latitude = Clip(latitude, MinLatitude, MaxLatitude);
		return Math.cos(latitude * Math.PI / 180) * 2 * Math.PI * EarthRadius / MapSize(levelOfDetail);
	}

	/**
	 * Determines the map scale at a specified latitude, level of detail and
	 * screen resolution.
	 * 
	 * @param latitude,
	 *            the latitude of the location.
	 * @param levelOfDetail
	 *            the level of detail of the map.
	 * @param screenDpi,
	 *            the dots per inch metric of the map.
	 * @return a double with the scale of the map.
	 */
	public static double MapScale(double latitude, int levelOfDetail, int screenDpi) {
		return GroundResolution(latitude, levelOfDetail) * screenDpi / 0.0254;
	}

	/**
	 * Converts tile XY coordinates into pixel XY coordinates of the upper-left
	 * pixel of the specified tile.
	 * 
	 * @param tileX,
	 *            the X coordinate of the tile.
	 * @param tileY,
	 *            the Y coordinate of the tile.
	 * @return an integer array containing the pixel coordinates.
	 */
	public static int[] TileXYToPixelXY(int tileX, int tileY) {
		int[] result = new int[2];
		int pixelX = tileX * 256;
		int pixelY = tileY * 256;
		result[0] = pixelX;
		result[1] = pixelY;
		return result;
	}

	/**
	 * Converts a QuadKey into tile XY coordinates.
	 * 
	 * @param quadKey,
	 *            a string containing the quadkey
	 * @return a three integer array whose first integer is the Tile's X
	 *         coordinate, second integer is the Tile's Y coordinate and the
	 *         third integer is the level of detail.
	 * @throws IOException
	 */
	public static int[] QuadKeyToTileXY(String quadKey) throws IOException {
		int[] result = new int[3];
		int tileX = 0;
		int tileY = 0;
		int levelOfDetail = quadKey.length();
		result[3] = levelOfDetail;
		for (int i = levelOfDetail; i > 0; i--) {
			int mask = 1 << (i - 1);

			switch (quadKey.charAt(levelOfDetail - i)) {
			case '0':
				break;

			case '1':
				tileX |= mask;
				result[0] = tileX;
				break;

			case '2':
				tileY |= mask;
				result[1] = tileY;
				break;

			case '3':
				tileX |= mask;
				tileY |= mask;
				result[0] = tileX;
				result[1] = tileY;
				break;

			default:
				throw new IOException("Invalid QuadKey digit sequence.");
			}
		}
		return result;
	}

}
