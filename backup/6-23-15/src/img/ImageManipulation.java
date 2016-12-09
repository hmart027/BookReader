package img;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.FileImageOutputStream;

public class ImageManipulation {
	
	
	public static byte[][][] getRGBAArray(BufferedImage image){
		  final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	      final int width = image.getWidth();
	      final int height = image.getHeight();
	      final boolean hasAlphaChannel = image.getAlphaRaster() != null;

	      byte[][][] result = new byte[height][width][4];
	      if (hasAlphaChannel) {
	         final int pixelLength = 4;
	         for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
	            result[row][col][3] = pixels[pixel]; // alpha
	            result[row][col][2] = pixels[pixel + 1]; // blue
	            result[row][col][1] = pixels[pixel + 2]; // green
	            result[row][col][0] = pixels[pixel + 3]; // red
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      } else {
	         final int pixelLength = 3;
	         for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
	            result[row][col][3] = (byte) 255; // alpha
	            result[row][col][2] = pixels[pixel]; // blue
	            result[row][col][1] = pixels[pixel + 1]; // green
	            result[row][col][0] = pixels[pixel + 2]; // red
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      }

	      return result;
	}
	
	// red- band = 1; green- band = 2; blue- band = 3;
	public static byte[][] getRGBAArrayBand(BufferedImage image, int band){
		  final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	      final int width = image.getWidth();
	      final int height = image.getHeight();
	      final boolean hasAlphaChannel = image.getAlphaRaster() != null;
	      final int pixelLength = image.getSampleModel().getNumBands();

	      byte[][] result = new byte[height][width];
	      if (hasAlphaChannel) {
	         for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
	            result[row][col] = pixels[pixel + band];
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      } else {
	         for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
	            result[row][col] = pixels[pixel + band - 1];
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      }

	      return result;
	}
	
	public static void saveRGBArrayAsImage(byte[][][] pixels, String path, String imgType){
		int height = pixels.length;
		int width = pixels[0].length;
		byte[] imgData = new byte[height*width*3];
		int index = 0;
		for(int h = 0; h < height; h++){
			for(int w = 0; w < width; w++){
				imgData[index++] = pixels[h][w][2];
				imgData[index++] = pixels[h][w][1];
				imgData[index++] = pixels[h][w][0];
			}
		}
	    BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	    img2.setData(Raster.createRaster(
	    		new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,width,height, 3, 3 * width, new int[]{0,1,2})
	    		, new DataBufferByte(imgData, imgData.length)
	    		, null ) );
	    
	    try {
			ImageIO.write(img2, imgType, new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
	
	public static void saveGrayArrayAsImage(byte[][] pixels, String path, String imgType){
		int height = pixels.length;
		int width = pixels[0].length;
		byte[] imgData = new byte[height*width];
		int index = 0;
		for(int h = 0; h < height; h++){
			for(int w = 0; w < width; w++){
				imgData[index++] = pixels[h][w];
			}
		}
	    BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
	    img2.setData(Raster.createRaster(
	    		new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,width,height, 1, 1 * width, new int[]{0})
	    		, new DataBufferByte(imgData, imgData.length)
	    		, null ) );
	    
	    try {
	    //Beging new Code
	    	JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
	    	jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	    	jpegParams.setCompressionQuality(1f);
	    	// use IIORegistry to get the available services
	    	IIORegistry registry = IIORegistry.getDefaultInstance();
	    	// return an iterator for the available ImageWriterSpi for jpeg images
	    	Iterator<ImageWriterSpi> services = registry.getServiceProviders(ImageWriterSpi.class,
	    	                                                 new ServiceRegistry.Filter() {   
	    	        @Override
	    	        public boolean filter(Object provider) {
	    	            if (!(provider instanceof ImageWriterSpi)) return false;

	    	            ImageWriterSpi writerSPI = (ImageWriterSpi) provider;
	    	            String[] formatNames = writerSPI.getFormatNames();
	    	            for (int i = 0; i < formatNames.length; i++) {
	    	                if (formatNames[i].equalsIgnoreCase("JPEG")) {
	    	                    return true;
	    	                }
	    	            }

	    	            return false;
	    	        }
	    	    },
	    	   true);
	    	//...assuming that servies.hasNext() == true, I get the first available service.
	    	ImageWriterSpi writerSpi = services.next();
	    	ImageWriter writer = writerSpi.createWriterInstance();

	    	// specifies where the jpg image has to be written
	    	writer.setOutput(new FileImageOutputStream(
	    	  new File(path)));

	    	// writes the file with given compression level 
	    	// from your JPEGImageWriteParam instance
	    	writer.write(null, new IIOImage(img2, null, null), jpegParams);
	    //End New Code
	    	
//			ImageIO.write(img2, imgType, new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
	
	// [x][y]
	public static byte[][] getGrayImg(BufferedImage image){
		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	    final int width = image.getWidth();
	    final int height = image.getHeight();
	    byte[][] result = new byte[width][height];
	    	    
	    final int pixelLength = 1;
        for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
           result[col][row] = pixels[pixel]; // gray
           col++;
           if (col == width) {
              col = 0;
              row++;
           }
        }
        return result;
	}

}
