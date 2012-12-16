# Batch Watermarker version 2.1

A simple shell utility for adding a watermark image to a collection of source images.

## Usage

```
java WaterMarker -watermark logo.png -source My_Pictures/ -padding 38 -position TOP_LEFT
```

Images are output in the directory of the program source files in a folder 'processed_images'.

## Parameters

* `-watermark` The watermark image
* `-source` The source images, can be a single file or directory or a combination of both
* `-position` The location of the watermark, can be TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT or BOTTOM_RIGHT
* `-padding` The space between the edge of the image and the watermark in pixels (default 0)

## Changelog

*2.1*

* Default padding is now 0.
* Shows example usage when all needed arguments are not provided.

*2.0*

* Now `ImageManipulator.java` uses `Graphics2D` to overlay the watermark image, instead of pixel-by-pixel manipulation. This provides better flexibility in coding and provides better quality overlay. (The previous approach created heavily pixelated watermark.)
* It now copies the EXIF metadata from JPEG source images to the watermarked versions.

*1.0*

* The original code from the source fork.