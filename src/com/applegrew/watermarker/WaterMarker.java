package com.applegrew.watermarker;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;

public class WaterMarker {
	
	private String outputDirectory = "processed_images";
	private String imageName = "watermarkedImage";
	
	private ImageManipulator imageManipulator;
	private Coordinates padding;
	private String position;
	private BufferedImage watermark;
	private File[] targetImagePaths;
	
	private int outputCounter = 0;
	
	public WaterMarker() {
		imageManipulator = new ImageManipulator();
	}
	
	public void parseCommandLineAndLoadAssets(String[] args) throws InvalidArgumentException, IOException{
		ArgumentParser parser = new ArgumentParser();
		parser.parseCommandLineArguments(args);
		loadAssets(parser);
	}
	
	public void loadAssets(ArgumentParser parsedContent) throws IOException {
		this.padding = parsedContent.getPadding();
		this.position = parsedContent.getPosition();
		this.watermark = loadWatermark(parsedContent.getWatermarkPath());
		this.targetImagePaths = imageManipulator.buildImagePaths(parsedContent.getTargetImageFilePaths());
	}
	
	public BufferedImage loadWatermark(String filePath) throws IOException{
		try{
			File file = new File(filePath);
			return imageManipulator.loadImage(file);
		} catch (IOException e){
			throw new IOException("Could not load watermark.");
		}
	}
	
	public void createOutputDirectory(){
		File directoryMaker = new File(outputDirectory);
		directoryMaker.mkdir();
	}
	
	public void addWatermarksAndExportFiles() {
		for (int i=0; i<targetImagePaths.length; i++) {
			try {
				BufferedImage nonWatermarkedImage = imageManipulator.loadImage(targetImagePaths[i]);
				BufferedImage watermarkedImage = imageManipulator.addWatermark(watermark, nonWatermarkedImage, position, padding);
				writeFile(watermarkedImage, targetImagePaths[i]);
				System.out.println("Processing image: " + targetImagePaths[i]);
			} catch (IOException e) {
				System.err.print("Couldn't load file at " + targetImagePaths[i] + " - skipping...");
			}
		}
	}
	
	public void writeFile(BufferedImage image, File srcImageFile) {
		String savePath = outputDirectory + "/" + imageName + outputCounter +".jpg";
		File imageFile = new File(savePath);
		
		try {
			ImageIO.write(image, "jpg", imageFile);
			imageManipulator.copyImageMetaData(srcImageFile, imageFile);
		} catch (IOException e) {
			System.err.print("Couldn't save file number " + outputCounter	 + " - skipping...");
		} catch (ImageReadException e) {
			System.err.print("Couldn't read file number " + outputCounter	 + "'s metadata - skipping...");
		} catch (ImageWriteException e) {
			System.err.print("Couldn't save file number " + outputCounter	 + " - skipping...");
		}
		outputCounter++;
	}
	
	public static void main(String[] args) {
		WaterMarker instance = new WaterMarker();
		
		try {
			instance.parseCommandLineAndLoadAssets(args);
			instance.createOutputDirectory();
			instance.addWatermarksAndExportFiles();
			System.out.println("Done!");
		} catch (InvalidArgumentException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} 
	}
}
