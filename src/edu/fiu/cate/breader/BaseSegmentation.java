package edu.fiu.cate.breader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import org.gphoto2.Camera;
import org.gphoto2.CameraFile;
import org.gphoto2.CameraUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import com.bluetechnix.argos.Argos3D;
import edu.fiu.cate.breader.tools.BReaderTools;
import edu.fiu.cate.breader.tools.RollingImageFilter;
import image.tools.ITools;
import image.tools.IViewer;
import img.ImageManipulation;
import math2.Vector;

public class BaseSegmentation{
	
	static{ 
		// Load OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 	
	}
	
	private float[][] rawrawDist, rawDist, normImg, amplitudes, normImgCropped;
	
	public BaseSegmentation(){
			
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
			base = Vector.add(base, dist);
		}
		base = Vector.scalarMult(base, 1.0f/120.0f);
        
        BufferedImage finalDisplay = null;
        
        disp.enableCapture(true);
        disp.addCaptureActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				captureEvent();
			}
		});
		
        float[] filtDist, averageRes;
		while((dist = argos.getDistances())!=null){
			
			filtDist = filter.filter(dist);
			averageRes = Vector.substract(filtDist, base);
			// 2D Raw distance as received from Argos
			rawrawDist = BReaderTools.getHorizontalFlip(BReaderTools.getImageFromArray(dist, w, h));
			// 2D Filtered Distances
			rawDist = BReaderTools.getHorizontalFlip(BReaderTools.getImageFromArray(filtDist, w, h));
			// 2D Difference between the base readings and the filtered readings
			normImg = BReaderTools.getHorizontalFlip(BReaderTools.getImageFromArray(averageRes, w, h));
			// 2D IR Amplitude (grayscale) image from Argos
			amplitudes = BReaderTools.getHorizontalFlip(BReaderTools.getImageFromArray(argos.getAmplitudes(), w, h));
			// Cropped book surface
			normImgCropped = ITools.crop( 160-120, 25,160-30, 85, normImg);
			
			image = BReaderTools.byteArrayToMat(ITools.normalize(normImgCropped));
			finalDisplay=BReaderTools.bufferedImageFromGrayMat(image);

//			disp.setImage(ImageManipulation.getGrayBufferedImage(img));
			disp.setImage(ImageManipulation.getGrayBufferedImage(ITools.normalize(normImgCropped)));
			
//        	ImageManipulation.writeImage(ITools.normalize(normImg), pathToImg+"dist"+(i++)+".tiff"); 
//        	ImageManipulation.writeImage(ITools.normalize(normImgCropped), pathToImg+"distC"+(i++)+".tiff"); 
//        	ImageManipulation.writeImage(ITools.normalize(amplitudes), pathToImg+"amp"+(i++)+".tiff"); 
//        	BReaderTools.saveFloatArrayToFile(pathToImg+"raw"+(i++)+".bin", rawDist);
			
	        if(finalDisplay!=null){
	        	disp.setImage(finalDisplay);     	
	        }
		}
		argos.close();
		//Saving images end
		
	}
	
	/**
	 * Gets the High definition image from the Canon camera using GPhoto2
	 * @return
	 */
	public byte[][][] getHidefImage(){
//		final CameraList cl = new CameraList();
//		CameraUtils.closeQuietly(cl);
		
		final Camera c = new Camera();
		c.initialize();
		final CameraFile cf2 = c.captureImage();
		java.io.File imageFile = new java.io.File("captured.jpg"); 
		cf2.save(imageFile.getAbsolutePath());
		CameraUtils.closeQuietly(cf2);
		CameraUtils.closeQuietly(c);
		
		byte[][][] img = ImageManipulation.loadImage("captured.jpg");
		imageFile.delete();
		return img;
	}
	
	/**
	 * Finds the bounding box for the book on the stand using 
	 * the high resolution image.
	 * @param src- High Resolution image of the book
	 * @return Rectangle delineating the book
	 */
	public Rect highRes(Mat src){
		Mat dst = src.clone();
		Imgproc.blur(src, dst, new Size(100.0, 100.0), new Point(-1,-1), Core.BORDER_REPLICATE);
		Imgproc.threshold(dst, dst,  0,255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
		Imgproc.Canny(dst, dst, 50, 200, 3, false);

		List<MatOfPoint> contours = new LinkedList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours( dst, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0) );
				
		Point center = new Point(src.cols()/2, src.rows()/2);
		//Check hierarchy tree
		int[] res = polySearch(center, hierarchy, contours, 0);
		while((res[0]==-1) && (res[2]!=-1)){
			res = polySearch(center, hierarchy, contours, res[2]);
		}
		
		MatOfInt tHull = new MatOfInt();
		Imgproc.convexHull(contours.get(res[1]), tHull);

		//get bounding box
		MatOfPoint cont = contours.get(res[1]);
		Point[] points = new Point[tHull.rows()];
		for(int i=0; i<tHull.rows(); i++){
			int pIndex = (int) tHull.get(i, 0)[0];
			points[i] = new Point(cont.get(pIndex, 0)); 
		}
		return Imgproc.boundingRect(new MatOfPoint(points));
	}
	
	public int[] polySearch(Point point, Mat hierarchy, List<MatOfPoint> contours, int current){
		// first index is whether the point is contained and second is which children has it.
		// Third is who is next
		int[] out = new int[3];
		int[] hEntry = new int[4];
		hierarchy.get(0,current, hEntry);
		int nextChild = hEntry[2], nextSibling = hEntry[0];
		// If point is not within current contour return -1
		if(Imgproc.pointPolygonTest(new MatOfPoint2f(contours.get(current).toArray()), point, false)<0){
			out[0]=-1;
			out[1]=-1;
		}else{
			//Otherwise check if contours has children containing the point
			out[0]=1;
			//Depth first search
			int[] ret = new int[3];
			int childrenCount = 0;
			while(nextChild!=-1){
				ret = polySearch(point, hierarchy, contours, nextChild);
				childrenCount++;
				nextChild = ret[2];
			}
			//If there is only one children return it.
			if(childrenCount==1){
				out[1]=ret[1];
			}else{//If more than one children are contained returned the parent
				out[1]=current;
			}
		}
		out[2]=nextSibling;

		return out;
	}
	
	/**
	 * Finds the bounding box for the book on the stand using 
	 * the depth average image.
	 * @param src- The Depth average image
	 * @return Rectangle delineating the book
	 */
	public Rect lowResDist(Mat src){
		Mat dst = src.clone();

		Imgproc.blur(src, dst, new Size(5,5), new Point(-1,-1), Core.BORDER_REPLICATE);
//		threshold(src, src2, 0,255,THRESH_BINARY_INV+THRESH_OTSU);
		Imgproc.Canny(dst, dst, 50, 200, 3, false);
//		Canny(src, dst, 20, 60, 3);

		List<MatOfPoint> contours = new LinkedList<>();
		Mat hierarchy = new Mat();
		/// Find contours
		Imgproc.findContours( dst, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

		for (int k = 0; k < contours.size(); k++){
			MatOfPoint2f tMat = new MatOfPoint2f();
			Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(k).toArray()), tMat, 5, true);
			contours.set(k, new MatOfPoint(tMat.toArray()));
		}

		List<Point> points = new LinkedList<Point>();
		for (int i = 0; i < contours.size(); i++) {
			points.addAll(contours.get(i).toList());
		}
		
		MatOfInt tHull = new MatOfInt();
		Imgproc.convexHull(new MatOfPoint(points.toArray(new Point[points.size()])), tHull);

		//get bounding box
		Point[] tHullPoints = new Point[tHull.rows()];
		for(int i=0; i<tHull.rows(); i++){
			int pIndex = (int) tHull.get(i, 0)[0];
			tHullPoints[i] = points.get(pIndex); 
		}
		return Imgproc.boundingRect(new MatOfPoint(tHullPoints));
	}
	
	/**
	 * Capture button has been press. Obtain the high resolution image and the low resolution 
	 * data. Once captured, the images are corrected. 
	 */
	public void captureEvent(){
		byte[][][] img = getHidefImage();
		Rect bound = highRes(BReaderTools.byteArrayToMat(ITools.toGrayscale(img)));
		
		Mat imgMat = BReaderTools.byteArrayToMat(img);
		Imgproc.rectangle(imgMat, bound.tl(), bound.br(), new Scalar(255,255,0), 8);
		
		new IViewer(BReaderTools.bufferedImageFromMat(imgMat));
		
		byte[][] low = ITools.normalize(normImgCropped);
		Rect boundLow = lowResDist(BReaderTools.byteArrayToMat(low));
		
		Mat color = new Mat();
		Imgproc.cvtColor(BReaderTools.byteArrayToMat(low), color,Imgproc.COLOR_GRAY2BGR);
		Imgproc.rectangle(color, boundLow.tl(), boundLow.br(), new Scalar(255,255,0), 1);
		
		new IViewer(BReaderTools.bufferedImageFromMat(color));
	}
	
	public static void main(String[] args){
		new BaseSegmentation();
	}
}
