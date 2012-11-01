package com.applegrew.watermarker;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

public class ImageManipulator {
	
	public BufferedImage loadImage(File file) throws IOException{
		BufferedImage image = ImageIO.read(file);
		return image;
	}
	
	public void copyImageMetaData(File srcImageFile, File dstImageFile) throws ImageReadException, IOException, ImageWriteException {
		IImageMetadata metadata = Sanselan.getMetadata(srcImageFile);
		
		TiffOutputSet outputSet = null;
		
		if (metadata != null && metadata instanceof JpegImageMetadata) {
			JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			TiffImageMetadata exif = jpegMetadata.getExif();
			if (exif != null) {
				outputSet = exif.getOutputSet();
			}
		}
		
		if (outputSet != null) {
			InputStream in = new BufferedInputStream(new FileInputStream(dstImageFile));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int b = 0;
			while ((b = in.read()) != -1) {
				bos.write(b);
			}
			in.close();
			
			OutputStream os = new BufferedOutputStream(new FileOutputStream(dstImageFile));
			new ExifRewriter().updateExifMetadataLossy(bos.toByteArray(), os, outputSet); // Copies the image data of destination image as-is.Only modifies the meta portion.
			os.close();
		}
	}

	public File[] buildImagePaths(String[] fileNames) throws IOException{

		Vector<File> targetImagePaths = new Vector<File>();
		
		for (int i=0; i<fileNames.length; i++){
			File filePath = new File(fileNames[i]);
			
			if (filePath.isFile() && fileIsImage(filePath)) {
				targetImagePaths.add(filePath);
			} else if (filePath.isDirectory()){ // Directory
				targetImagePaths.addAll(getImagePathsInsideDirectory(filePath));
			}
		}
		
		if (targetImagePaths.size() > 0) {
			return targetImagePaths.toArray(new File[0]);
		} else {
			throw new IOException("Could not find any valid source files.");
		}
	}
	
	public Vector<File> getImagePathsInsideDirectory(File directory) {
		Vector<File> targetImagePaths = new Vector<File>();
		String[] directoryFilePaths = directory.list();
		
		for (int i=0; i<directoryFilePaths.length; i++){
			String fullPath = directory.getPath() + "/" + directoryFilePaths[i];
			File filePath = new File(fullPath);
			if(filePath.isFile() && fileIsImage(filePath)){
				targetImagePaths.add(filePath);
			}
		}
		return targetImagePaths;
	}
	
	public boolean fileIsImage(File file) {
		if(file.getName().matches("(?i).*(.jpg|.jpeg|.png)")){
			return true;
		}
		return false;
	}
	
	public BufferedImage addWatermark(BufferedImage watermark, BufferedImage nonWatermarkedImage, String position, Coordinates padding) {
		int xStartPixel = position.matches("TOP_LEFT|BOTTOM_LEFT") ? padding.getXCoord() : nonWatermarkedImage.getWidth() - (watermark.getWidth() + padding.getXCoord()) ;
		int yStartPixel = position.matches("TOP_LEFT|TOP_RIGHT") ? padding.getYCoord() : nonWatermarkedImage.getHeight() - (watermark.getHeight() + padding.getYCoord()) ;
		
		Graphics2D g2d = (Graphics2D) nonWatermarkedImage.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setComposite(AlphaComposite.SrcOver);
		if (!g2d.drawImage(watermark, xStartPixel, yStartPixel, null)) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
		
		return nonWatermarkedImage;
	}
	
}
