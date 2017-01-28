package edu.fiu.cate.breader;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import image.tools.IViewer;
import img.ImageManipulation;

public class BaseSegmentation{
	
	static{ 
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 	
	}
	
	public BaseSegmentation(){

//		byte[][][] img = ImageManipulation.loadImage("/mnt/Research/Harold/BookReader/Images/J2 test images/SHM/testSHM001.jpg");
//		new IViewer(ImageManipulation.getBufferedImage(img));
//		new IViewer("R",ImageManipulation.getGrayBufferedImage(img[0]));
//		new IViewer("G",ImageManipulation.getGrayBufferedImage(img[1]));
//		new IViewer("B",ImageManipulation.getGrayBufferedImage(img[2]));
	
		String imgType = "test"; //ampdata
		String pathToImg = "/mnt/Research/Harold/BookReader/Images/J2 test images/SHM/";
		String extension = "jpg"; //tiff
		
		Mat image, fgMask, output;
		BackgroundSubtractorMOG2 backgroundSubtractorMOG = Video.createBackgroundSubtractorMOG2();
		backgroundSubtractorMOG.setDetectShadows(false);
		
		for(int i=1; i<=60; i++){
			String imgNumber = "";
			if(i>=100) imgNumber+= i/100; else imgNumber+= "0";
			if(i>=10) imgNumber+= (i-(i/100)*100)/10+""; else imgNumber+= "0";
			imgNumber+= (i-(i/10)*10) +"";
			System.out.println("Loading: "+imgType+"SHM"+imgNumber+"."+extension);
			image = Imgcodecs.imread(pathToImg+imgType+"SHM"+imgNumber+"."+extension);
			fgMask=new Mat();
	        backgroundSubtractorMOG.apply(image, fgMask,0.1);
		}
		
		image = Imgcodecs.imread(pathToImg+imgType+"SHM001."+extension);
		new IViewer("Original",bufferedImageFromMat(image));
		
		fgMask=new Mat();
        backgroundSubtractorMOG.apply(image, fgMask);
		
        output=new Mat();
        image.copyTo(output,fgMask);
        
		new IViewer("No Background",bufferedImageFromMat(output));
	}
	
	public static Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}
	
	public static BufferedImage bufferedImageFromMat(Mat bi) {
		// Create an empty image in matching format
		BufferedImage img = new BufferedImage(bi.width(), bi.height(), BufferedImage.TYPE_3BYTE_BGR);

		// Get the BufferedImage's backing array and copy the pixels directly into it
		byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		bi.get(0, 0, data);
		
		return img;
	}
	
	public static void main(String[] args){
		new BaseSegmentation();
	}

}
