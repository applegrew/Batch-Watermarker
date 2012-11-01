package com.applegrew.watermarker;
import java.util.Vector;

public class ArgumentParser {
	
	private final int defaultPadding = 20;
	private String defaultPosition = "BOTTOM_RIGHT";

	private String[] targetImageFilePaths;
	private String watermarkPath;
	private Coordinates padding;
	private String position;
	
	public ArgumentParser() {
		this.padding = new Coordinates(defaultPadding, defaultPadding);
		this.position = defaultPosition;
	}
	
	public void parseCommandLineArguments(String[] args) throws InvalidArgumentException{
		
		int i = 0;
		
		while (i+1 < args.length) {
			if (args[i].equals("-watermark")){
				if (args[i+1].startsWith("-")){
					throw new InvalidArgumentException("You must specify a watermark to apply.");
				} else {
					this.watermarkPath = args[i+1];
				}
			} else if (args[i].equals("-source")){
				if (args[i+1].startsWith("-")){
					throw new InvalidArgumentException("You must specify at least one image or directory to apply a watermark to.");
				} else {
					Vector <String> targetImageFilePathsTempList = new Vector<String>();
					while (i+1 < args.length && !args[i+1].startsWith("-")) {
						targetImageFilePathsTempList.add(args[++i]);
					}
					this.targetImageFilePaths = new String[targetImageFilePathsTempList.size()];
					for (int j=0; j<targetImageFilePathsTempList.size(); j++){
						targetImageFilePaths[j] = targetImageFilePathsTempList.get(j);
					}
				}
			} else if (args[i].equals("-padding")){
				if (args[i+1].startsWith("-")){
					System.out.println("No padding specified, using default: " + this.defaultPadding + " pixels");
				} else {
					try {
						int paddingArg = Math.abs(Integer.parseInt(args[++i]));
						this.padding = new Coordinates(paddingArg, paddingArg);
					} catch(NumberFormatException e) {
						System.out.println("Invalid padding specified, using default: " + this.defaultPadding + " pixels");
					}
				}
			} else if (args[i].equals("-position")) {
				if (args[i+1].startsWith("-")){
					System.out.println("No position given, using default: " + this.defaultPosition);
				} else {
					if (args[i+1].matches("TOP_LEFT|TOP_RIGHT|BOTTOM_LEFT|BOTTOM_RIGHT")) {
						this.position = args[i+1];
					} else {
						System.out.println("Invalid position given, using default: " + this.defaultPosition);
					}
				}
			} 
			i++;
		}
		
		if (this.minimumRequiredArgumentsNotSet()) {
			throw new InvalidArgumentException("Expected more arguments.");
		}
		
	}
	
	private boolean minimumRequiredArgumentsNotSet() {
		if (this.watermarkPath == null || this.watermarkPath == "" || this.targetImageFilePaths == null || this.targetImageFilePaths.length == 0) {
			return true;
		}
		
		return false;
	}

	public String[] getTargetImageFilePaths() {
		return targetImageFilePaths;
	}

	public String getWatermarkPath() {
		return watermarkPath;
	}

	public Coordinates getPadding() {
		return padding;
	}

	public String getPosition() {
		return position;
	}
	
	public int getDefaultPadding() {
		return defaultPadding;
	}
	
	public String getDefaultPosition() {
		return defaultPosition;
	}
}
