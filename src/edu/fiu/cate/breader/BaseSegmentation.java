package edu.fiu.cate.breader;

import java.awt.List;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.KeyPoint;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import com.bluetechnix.argos.Argos3D;
import com.sun.javafx.iio.common.ImageTools;

import image.tools.ITools;
import image.tools.IViewer;
import img.ImageManipulation;

public class BaseSegmentation{
	
	static{ 
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 	
	}
	
	public BaseSegmentation(){
		
//		CaptureSettings disp = new CaptureSettings();
//		VideoCapture camera = new VideoCapture(0);
//		Mat frame = new Mat();
//		byte[] img = null;
//		camera.read(frame);
//		img=new byte[frame.width()*frame.height()]; 
//		while(camera.read(frame)){
//			Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
//			frame.get(0, 0, img);
//			float normVal = disp.getNormVal();
//			for(int i=0; i<img.length; i++){
//				img[i] = (byte) ((255f/normVal)*img[i]);
//			}
//			disp.setImage(bufferedImageFromArray(img, frame.width(), frame.height(), BufferedImage.TYPE_BYTE_GRAY));
//		}
//		byte[][][] img = ImageManipulation.loadImage("/mnt/Research/Harold/BookReader/Images/J2 test images/SHM/testSHM001.jpg");
//		new IViewer(ImageManipulation.getBufferedImage(img));
//		new IViewer("R",ImageManipulation.getGrayBufferedImage(img[0]));
//		new IViewer("G",ImageManipulation.getGrayBufferedImage(img[1]));
//		new IViewer("B",ImageManipulation.getGrayBufferedImage(img[2]));

		
		String imgType = "test"; //ampdata
		String pathToImg = "/mnt/Research/Harold/BookReader/Images/J2 test images/SHM/";
		pathToImg = "/media/harold/DataLPC/BReader/Background Removal Test/cardboardNN/";
		pathToImg = "/media/harold/DataLPC/BReader/7-16-17/";
		String extension = "tiff"; //tiff
		
		Mat image = null, fgMask = null, output = null;
		BackgroundSubtractorMOG2 backgroundSubtractorMOG = Video.createBackgroundSubtractorMOG2();
		backgroundSubtractorMOG.setDetectShadows(false);
		
		Argos3D argos = new Argos3D();
		argos.update();
		CaptureSettings disp = new CaptureSettings();
		disp.addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                argos.close();
                e.getWindow().dispose();
            }
        });
		int w = argos.getImageData().numColumns;
		int h = argos.getImageData().numRows;
		float[] amp = argos.getAmplitudes();
		float[] dist = argos.getAmplitudes();
		byte[] img = new byte[dist.length];
//		image = new Mat(h, w, CvType.CV_8UC1);
//		for (int i=0; i<120; i++){
//			if((amp = argos.getAmplitudes())!=null){
//				for(int x=0; x<img.length; x++){
//					img[i] = (byte) ((255f/6500)*amp[i]);
//				}
//				fgMask=new Mat();
//				image.put(0, 0, img);
//		        backgroundSubtractorMOG.apply(image, fgMask);
//			}
//		}
//		while((amp = argos.getAmplitudes())!=null){
//			float normVal = disp.getNormVal();
//			normVal=6500;
//			for(int i=0; i<img.length; i++){
//				img[i] = (byte) ((255f/normVal)*amp[i]);
//			}
//			fgMask=new Mat();
//			image.put(0, 0, img);
//	        backgroundSubtractorMOG.apply(image, fgMask);
//	        output=new Mat();
//	        image.copyTo(output,fgMask);
//			disp.setImage(bufferedImageFromGrayMat(output));
//		}
		
//		while((amp = argos.getDistances())!=null){
//			float normVal = disp.getNormVal();
//			for(int i=0; i<img.length; i++){
//				img[i] = (byte) ((255f/normVal)*amp[i]);
//			}
//			disp.setImage(bufferedImageFromArray(img, w, h, BufferedImage.TYPE_BYTE_GRAY));
//		}
//		argos.close();

		//Saving images
		argos.setBilateralFilter(true);
		RollingImageFilter filter = new RollingImageFilter(60, 120*160);
		for(int i = 0; i<60; i++){
			dist = argos.getDistances();
			filter.filter(dist);
		}
		float[] base = new float[(120*160)];
		for(int i = 0; i<120; i++){
			dist = filter.filter(argos.getDistances());
			base = addArray(base, dist);
		}
		base = scalarMultArray(base, 1.0f/120.0f);

		MatOfKeyPoint matOfKeyPoints = new MatOfKeyPoint();
        FeatureDetector blobDetector = FeatureDetector.create(FeatureDetector.DYNAMIC_GFTT);
        
        KeyPoint[] keyPoints;
        BufferedImage finalDisplay = null;
        int i = 0;
		
        float[] filtDist, averageRes;
		while((dist = argos.getDistances())!=null){
//			amp = filter.filter(argos.getDistances());
//			float normVal = disp.getNormVal();
//			float max = amp[0];
//			for(int x=0; x<img.length; x++){
//				if(amp[x]>max) max = amp[x];
//				img[x] = (byte) ((255f/normVal)*amp[x]);
//			}
//			disp.setActualMax((int)max);
			
			filtDist = filter.filter(dist);
			averageRes = subArray(filtDist, base);
			float[][] rawrawDist = getHorizontalFlip(getImageFromArray(dist, w, h));
			float[][] rawDist = getHorizontalFlip(getImageFromArray(filtDist, w, h));
			float[][] normImg = getHorizontalFlip(getImageFromArray(averageRes, w, h));
			float[][] amplitudes = getHorizontalFlip(getImageFromArray(argos.getAmplitudes(), w, h));
			float[][] normImgCropped = ITools.crop( 160-120, 25,160-30, 85, normImg);
			
//			float[][] normImg = getImageFromArray(amp, w, h);
			
			//croping image to reduce noise. needs to be readjusted when cameras are relocated
			//normImg = ITools.crop(30, 40, 120, 100, normImg);	
			
//			image = byteArrayToMat(ITools.normalize(normImg));
//			finalDisplay=bufferedImageFromGrayMat(image);
//	        blobDetector.detect(image, matOfKeyPoints);
//
//	        System.out.println("Detected " + matOfKeyPoints.size()+ " blobs in the image");
//	        keyPoints = matOfKeyPoints.toArray();
//	        finalDisplay.getGraphics().setColor(java.awt.Color.RED);
//			for(i=0;i<5;i++){
//				KeyPoint k = keyPoints[i];
//				finalDisplay.getGraphics().drawOval((int)k.pt.x, (int)k.pt.y, (int)k.size, (int)k.size);
//			}

//			disp.setImage(ImageManipulation.getGrayBufferedImage(img));
			disp.setImage(ImageManipulation.getGrayBufferedImage(ITools.normalize(normImgCropped)));
			
        	ImageManipulation.writeImage(ITools.normalize(normImg), pathToImg+"dist"+(i++)+".tiff"); 
        	ImageManipulation.writeImage(ITools.normalize(normImgCropped), pathToImg+"distC"+(i++)+".tiff"); 
        	ImageManipulation.writeImage(ITools.normalize(amplitudes), pathToImg+"amp"+(i++)+".tiff"); 
        	saveFloatArrayToFile(pathToImg+"raw"+(i++)+".bin", rawDist);
			
//	        if(finalDisplay!=null){
//	        	disp.setImage(finalDisplay);     	
//	        }
		}
		argos.close();
		//Saving images end
		
////		Mat bg = Imgcodecs.imread(pathToImg+"img0.tiff");
////		new IViewer("Background",bufferedImageFromMat(bg));
////		for(int i=1; i<=100; i++){
////			img2 = image.clone();
////	        backgroundSubtractorMOG.apply(image, fgMask,0.1);
////		}
//		
//		for (int x=0; x<2; x++)
//		for(int i=0; i<60; i++){
////			String imgNumber = "";
////			if(i>=100) imgNumber+= i/100; else imgNumber+= "0";
////			if(i>=10) imgNumber+= (i-(i/100)*100)/10+""; else imgNumber+= "0";
////			imgNumber+= (i-(i/10)*10) +"";
////			System.out.println("Loading: "+imgType+"SHM"+imgNumber+"."+extension);
////			image = Imgcodecs.imread(pathToImg+imgType+"SHM"+imgNumber+"."+extension);
//			System.out.println("Loading: "+"imgA"+i+"Bck."+extension);
//			image = Imgcodecs.imread(pathToImg+"imgA"+i+"Bck."+extension);
//			fgMask=new Mat();
//	        backgroundSubtractorMOG.apply(image, fgMask, 0.50);
//		}
//		
////		image = Imgcodecs.imread(pathToImg+imgType+"SHM001."+extension);
//		image = Imgcodecs.imread(pathToImg+"imgA9Tb."+extension);
//		new IViewer("Original",bufferedImageFromMat(image));
//		
////		Mat img2 = image.clone();
////		Core.subtract(image, bg, img2);
////		new IViewer("No Background",bufferedImageFromMat(img2));
//		
//		
//		fgMask=new Mat();
//        backgroundSubtractorMOG.apply(image, fgMask);
//		
//        output=new Mat();
//        image.copyTo(output,fgMask);
//        
//		new IViewer("No Background 1.0 x2",bufferedImageFromMat(output));
		
		
////		Argos3D argos = new Argos3D();
//		argos.update();
//		int rows = argos.getImageData().numRows;
//		int cols = argos.getImageData().numColumns;
//		System.out.println("Rows: "+rows);
//		System.out.println("Cols: "+cols);
//		
////		float[] amp, dist;
//		float maxA, maxD, maxT;
//		byte[][] imgA = new byte[rows][cols];
//		byte[][] imgD = new byte[rows][cols];
//		int c = 0;
//		IViewer view = new IViewer(ImageManipulation.getGrayBufferedImage(imgG));
//		
//		String path = "/media/harold/DataLPC/BReader/Background Removal Test/cardboardNN/";
//		
//		for(int t=0; t<60; t++){
////			System.out.println("Writting image "+t);
//			c=0;
//			amp = argos.getAmplitudes();
//			dist = argos.getDistances();
//			maxA = amp[0];
//			maxD = dist[0];
//			for(int i=0; i<amp.length; i++){
//				if(amp[i]>maxA) maxA = amp[i];
//				if(dist[i]>maxD) maxD = dist[i];
//			}
//			for(int y = 0;y<rows; y++){
//				for(int x = 0;x<cols; x++){
//					imgA[y][x] = (byte) (amp[c]/6000.0*255);
//					imgD[y][x] = (byte) (dist[c]/2.0*255);
//					c++;
//				}
//			}
//			
//			System.out.println(maxA+"\t"+maxD);
//			
////			ImageManipulation.writeImage(imgA, path+"imgA"+t+"Bck.tiff");
////			ImageManipulation.writeImage(imgD, path+"imgD"+t+"Bck.tiff");
//
////			ImageManipulation.writeImage(imgA, path+"imgA"+t+"Tb.tiff");
////			ImageManipulation.writeImage(imgD, path+"imgD"+t+"Tb.tiff");
//			
//			view.setImage(ImageManipulation.getGrayBufferedImage(amp));
//		}
//				
//		argos.close();
		
	}
	
	public static void saveFloatArrayToFile(String file, float[] array){
		try {
			java.io.OutputStream out = new java.io.FileOutputStream(file);
			for(float f: array){
				out.write(Float.floatToIntBits(f));
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveFloatArrayToFile(String file, float[][] array){
		try {
			java.io.OutputStream out = new java.io.FileOutputStream(file);
			for(int y=0; y<array.length; y++){
				for(int x=0; x<array[0].length; x++){
					out.write(Float.floatToIntBits(array[y][x]));
				}
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}
	
	public static Mat byteArrayToMat(byte[][] bi) {
		Mat mat = new Mat(bi.length, bi[0].length, CvType.CV_8UC1);
		for(int i=0; i<bi.length; i++)
			mat.put(i, 0, bi[i]);
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
	
	public static BufferedImage bufferedImageFromGrayMat(Mat bi) {
		// Create an empty image in matching format
		BufferedImage img = new BufferedImage(bi.width(), bi.height(), BufferedImage.TYPE_BYTE_GRAY);

		// Get the BufferedImage's backing array and copy the pixels directly into it
		byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		bi.get(0, 0, data);
		
		return img;
	}
	
	public static BufferedImage bufferedImageFromArray(byte[] bi, int w, int h, int imgType) {
		// Create an empty image in matching format
		BufferedImage img = new BufferedImage(w, h, imgType);

		// Get the BufferedImage's backing array and copy the pixels directly into it
		byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		for(int i=0; i<data.length; i++){
			data[i]=bi[i];
		}
		
		return img;
	}
	
	public static byte[][] getImageFromArray(byte[] bi, int w, int h) {
		byte[][] img = new byte[h][w];
		int c = 0;
		for(int y=0; y<h; y++){
			for(int x=0; x<w; x++){
				img[y][x]=bi[c++];
			}
		}
		return img;
	}
	
	public static float[][] getImageFromArray(float[] bi, int w, int h) {
		float[][] img = new float[h][w];
		int c = 0;
		for(int y=0; y<h; y++){
			for(int x=0; x<w; x++){
				img[y][x]=bi[c++];
			}
		}
		return img;
	}
	
	public static float[][] getHorizontalFlip(float[][] src){
		int[] s = new int[]{src.length, src[0].length};
		float[][] out = new float[s[0]][s[1]];
		for(int y=0; y<s[0]; y++){
			for(int x=0; x<s[1]; x++){
				out[y][s[1]-1-x]=src[y][x];
			}
		}
		return out;
	}
	
	public static byte[][] getHorizontalFlip(byte[][] src){
		int[] s = new int[]{src.length, src[0].length};
		byte[][] out = new byte[s[0]][s[1]];
		for(int y=0; y<s[0]; y++){
			for(int x=0; x<s[1]; x++){
				out[y][s[1]-1-x]=src[y][x];
			}
		}
		return out;
	}
	
	public static float[] addArray(float[] a1, float[] a2){
		if(a1.length!=a2.length) return null;
		float[] out = new float[a1.length];
		for(int i=0; i<a1.length; i++)
			out[i] = a1[i]+a2[i];
		return out;
	}
	
	public static float[] subArray(float[] a1, float[] a2){
		if(a1.length!=a2.length) return null;
		float[] out = new float[a1.length];
		for(int i=0; i<a1.length; i++)
			out[i] = a1[i]-a2[i];
		return out;
	}
	
	public static float[] scalarMultArray(float[] array, float scalar){
		if(array==null || array.length==0) return null;
		float[] out = new float[array.length];
		for(int i=0; i<array.length; i++)
			out[i] = array[i]*scalar;
		return out;
	}
	
	public static void main(String[] args){
		new BaseSegmentation();
	}

	public static class RollingImageFilter{
		private int depth = 1;
		private int imgLenght = 0;
		private java.util.ArrayList<float[]> images;
		
		private RollingImageFilter(int depth, int imgLenght){
			this.depth = depth;
			this.imgLenght = imgLenght;
			this.images = new java.util.ArrayList<float[]>(depth-1);
			for(int i=0; i<depth-1; i++){
				this.images.add(new float[this.imgLenght]);
			}
		}
		
		public static RollingImageFilter getRollingImageFilter(int depth, int imgLenght){
			return new RollingImageFilter(depth, imgLenght);
		}
		
		public float[] filter(float[] img){
			float[] out = new float[this.imgLenght];
			out = addArray(out, img);
			for(int i=0; i<depth-1; i++){
				out = addArray(out, images.get(i));
			}
			this.images.remove(0);
			this.images.add(img);
			out = scalarMultArray(out, 1f/(float)depth);
			return out;
		}
		
	}
}
