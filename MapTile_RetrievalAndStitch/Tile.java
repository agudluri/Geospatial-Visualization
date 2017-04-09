import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;

public class Tile {

	BufferedImage errorimage; // To store the error image.

	public static void main(String[] args) throws IOException {
		new Tile().run();
	}

	public void run() throws IOException {

		Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

		// Reading the bounding latitudes and longitudes.
		System.out.println("Enter the first latitude and longitude(seperated by a comma).");
		String[] line1 = sc.nextLine().split(",");
		System.out.println("Enter the second latitude and longitude.");
		String[] line2 = sc.nextLine().split(",");

		// Making sure the entered points are within limits.
		double lat1 = TileSystem.Clip(Double.parseDouble(line1[0]), TileSystem.MinLatitude, TileSystem.MaxLatitude);
		double lon1 = TileSystem.Clip(Double.parseDouble(line1[1]), TileSystem.MinLongitude, TileSystem.MaxLongitude);
		double lat2 = TileSystem.Clip(Double.parseDouble(line2[0]), TileSystem.MinLatitude, TileSystem.MaxLatitude);
		double lon2 = TileSystem.Clip(Double.parseDouble(line2[1]), TileSystem.MinLongitude, TileSystem.MaxLongitude);

		// Reading the error image from the disk.
		File file = new File("Error.png");
		errorimage = ImageIO.read(file);

		int level = 23;
		int tile1[] = null;
		int tile2[] = null;
		int rows = 0;
		int columns = 0;
		boolean flag = true;

		// Looping till we find an optimal zoom level without error images.
		while (flag) {

			// Calculating the pixel coordinates of the points.
			int pixel[] = TileSystem.LatLongToPixelXY(lat1, lon1, level);
			int pixel2[] = TileSystem.LatLongToPixelXY(lat2, lon2, level);

			// Calculating the tile coordinates of the points.
			tile1 = TileSystem.PixelXYToTileXY(pixel);
			tile2 = TileSystem.PixelXYToTileXY(pixel2);

			// Calculating the number of tile rows and columns.
			rows = Math.abs(tile1[1] - tile2[1]) + 1;
			columns = Math.abs(tile1[0] - tile2[0]) + 1;

			// Reducing a level of detail if there's an error image in the lot.
			if (Error(tile1, tile2, level))
				level--;
			else
				flag = false;
		}

		// Downloading images at the right level of detail and stitching them together.
		Stitch(tile1, tile2, level, rows, columns);

		sc.close();
	}

	/**
	 * A method to check if the images (at a particular level of detail) are
	 * error-free or not.
	 * 
	 * @param tile1, an integer array containing the tile coordinates of the first point 
	 * at the given level of detail.
	 * @param tile2, an integer array containing the tile coordinates of the second point 
	 * at the given level of detail. 
	 * @param level, the level of detail.
	 * @return a boolean denoting the presence of an error image.
	 * @throws IOException
	 */
	public boolean Error(int[] tile1, int[] tile2, int level) throws IOException {
		long start = System.currentTimeMillis();
		long end = start + 150 * 1000; 
		boolean error = true;
		for (int i = Math.min(tile1[0], tile2[0]); i <= Math.max(tile1[0], tile2[0]); i++) {
			for (int j = Math.min(tile1[1], tile2[1]); j <= Math.max(tile1[1], tile2[1]); j++) {
				
				String key = TileSystem.TileXYToQuadKey(i, j, level); //Calculating the Quadkey.
				
				URL url = new URL("http://h0.ortho.tiles.virtualearth.net/tiles/h" + key + ".jpeg?g=131");
				BufferedImage img = ImageIO.read(url); //Reading image from the URL.

				error = Compare(img, errorimage); //Comparing the image with the standard error image.

				if (error == true || System.currentTimeMillis() > end)
					break;
			} // Inner 'for' loop.
			if (error == true || System.currentTimeMillis() > end)
				break;
		} // Outer 'for' loop.
		return error;
	}

	/**
	 * A support method for comparing downloaded images with the error image.
	 * 
	 * @param, one the first image.
	 * @param, two the second image.
	 * @return a boolean denoting the equality of the input images.
	 */
	public boolean Compare(BufferedImage one, BufferedImage two) {
		boolean equal = true;
		int x = 0;
		for (int y = 0; y < 256; y++) {
			if (one.getRGB(x, y) != two.getRGB(x, y)) { // Comparing the pixels of the input images.
				equal = false;
				break;
			}
		}
		return equal;
	}

	/**
	 * A method to download images at a particular level of detail and 
	 * stitch them together.
	 * 
	 * @param tile1, an integer array containing the tile coordinates of the first point 
	 * at the given level of detail.
	 * @param tile2, an integer array containing the tile coordinates of the second point 
	 * at the given level of detail.
	 * @param level, the level of detail.
	 * @param rows, the number of rows in the stitched image.
	 * @param columns, the number of columns in the stitched image.
	 * @throws IOException
	 */
	public void Stitch(int[] tile1, int[] tile2, int level, int rows, int columns) throws IOException {
	
		// Initializing a BufferedImage instance to hold the final image.
		BufferedImage finalImg = new BufferedImage(256 * columns, 256 * rows, 5);
		for (int i = Math.min(tile1[0], tile2[0]), k = 0; i <= Math.max(tile1[0], tile2[0]); i++, k++) {
			for (int j = Math.min(tile1[1], tile2[1]), l = 0; j <= Math.max(tile1[1], tile2[1]); j++, l++) {
				
				String key = TileSystem.TileXYToQuadKey(i, j, level); // Generating a Quadkey.
				
				URL url = new URL("http://h0.ortho.tiles.virtualearth.net/tiles/h" + key + ".jpeg?g=131");
				BufferedImage img = ImageIO.read(url); // Reading the image from the URL.
				
				// Writing the downloaded image to the respective tile of the final image.
				finalImg.createGraphics().drawImage(img, 256 * k, 256 * l, null);
			}
		}
		// Writing the final output image to the disk.
		System.out.println("\nImage Created.");
		ImageIO.write(finalImg, "jpg", new File("FinalImage.jpg"));
	}

}
