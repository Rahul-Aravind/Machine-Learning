/*** Author :Vibhav Gogate
The University of Texas at Dallas
*****/


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
 

public class KMeans {
    public static void main(String [] args){
	if (args.length < 3){
	    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
	    return;
	}
	try{
	    BufferedImage originalImage = ImageIO.read(new File(args[0]));
	    int k=Integer.parseInt(args[1]);
	    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
	    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
	    
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}	
    }
    
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
	int w=originalImage.getWidth();
	int h=originalImage.getHeight();
	BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
	Graphics2D g = kmeansImage.createGraphics();
	g.drawImage(originalImage, 0, 0, w,h , null);
	// Read rgb values from the image
	int[] rgb=new int[w*h];
	int count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		rgb[count++]=kmeansImage.getRGB(i,j);
	    }
	}
	// Call kmeans algorithm: update the rgb values
	kmeans(rgb,k);

	// Write the new rgb values to the image
	count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		kmeansImage.setRGB(i,j,rgb[count++]);
	    }
	}
	return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k){
    	int means[];
    	means = new int[k];
    	
    	boolean hash[];
    	hash = new boolean[rgb.length];
    	
    	Random random = new Random();
    	
    	for(int i = 0; i < k;) {
    		int val = random.nextInt(rgb.length);
    		if(hash[val]) continue;
    		
    		means[i] = rgb[val];
    		hash[val] = true;
    		i++;
    	}
    	
    	int centroid[];
    	centroid = new int[rgb.length];
    	
    	for(int iter = 0; iter < 100; iter++) {
    		
    		// go through each pixel in the image and calculate its nearest mean
    		for(int i = 0; i < rgb.length; i++) {
    			int cluster = -1;
    			int mindistance = Integer.MAX_VALUE;
    			
    			for(int j = 0; j < k; j++) {
    				Color pixel = new Color(rgb[i]);
    				Color clusterregion = new Color(means[j]);
    				
    				double r = Math.pow(pixel.getRed() - clusterregion.getRed(), 2);
    				double g = Math.pow(pixel.getGreen() - clusterregion.getGreen(), 2);
    				double b = Math.pow(pixel.getBlue() - clusterregion.getBlue(), 2);
    				
    				int distance = (int) Math.sqrt(r + g + b);
    				
    				if(distance < mindistance) {
    					mindistance = distance;
    					cluster = j;
    				}
    			}
    			centroid[i] = cluster;
    		}
    		
    		// update the values of the means arr based on the pixels assigned to them.
    		updateMeans(means, centroid, rgb, k);
    	}
    	
    	// update the pixels in the image based on the calculated means
    	for(int i = 0; i < rgb.length; i++) {
    		rgb[i] = means[centroid[i]];
    	}
    }
    
    private static void updateMeans(int means[], int centroid[], int rgb[], int k) {
    	
    	for(int i = 0; i < k; i++) {
    		int pointsInRegion = 0;
    		int r = 0;
    		int g = 0;
    		int b = 0;
    		
    		for(int j = 0; j < rgb.length; j++) {
    			if(centroid[j] == i) {
    				Color pixel = new Color(rgb[j]);
    				r += pixel.getRed();
    				g += pixel.getGreen();
    				b += pixel.getBlue();
    				
    				pointsInRegion++;
    			}
    		}
    		
    		if(pointsInRegion > 0) {
    			Color updatedPixel = new Color(r / pointsInRegion, g / pointsInRegion, b / pointsInRegion);
    			means[i] = updatedPixel.getRGB();
    		}
    	}
    }

}