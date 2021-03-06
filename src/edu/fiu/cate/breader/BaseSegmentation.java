package edu.fiu.cate.breader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import edu.fiu.cate.LuWang;
import edu.fiu.cate.breader.abbyy.ABBYY;
import edu.fiu.cate.breader.tools.BReaderTools;
import edu.fiu.cate.breader.tools.RollingImageFilter;
import freetts.TTS;
import image.tools.ITools;
import image.tools.IViewer;
import img.ImageManipulation;
import math2.Vector;

public class BaseSegmentation{
	
	static{ 
		// Load OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 	
	}

	private static String saveDir = System.getProperty("user.home") + "/BookReaderDB";
	
	private float[][] rawrawDist, rawDist, normImg, amplitudes, normImgCropped;
	
	private ABBYY abbyy = null;
	private TTS tts = new TTS();
	CaptureSettings disp = null;

	public BaseSegmentation(){
		
		abbyy = ABBYY.getABBYY();
			
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
		disp = new CaptureSettings();
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
		// capture a base image
		float[] base = readBase(saveDir+"/lastBase.bin");
		if(base==null) {
			base = new float[(120*160)];
			for(int i = 0; i<120; i++){
				dist = filter.filter(argos.getDistances());
				base = Vector.add(base, dist);
			}
			base = Vector.scalarMult(base, 1.0f/120.0f);
	        saveBase(saveDir+"/lastBase.bin", base);
		}else {
			System.out.println("Base loaded from file at: ");
			System.out.println("\t"+saveDir+"/lastBase.bin");
		}
		
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
		cf2.clean();
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
		
		Mat color = new Mat();
		Imgproc.cvtColor(src, color,Imgproc.COLOR_GRAY2BGR);
		for (int k = 0; k < contours.size(); k++){
			byte[] vals =  ITools.getHeatMapColor((float)k/(float)contours.size());
			Imgproc.drawContours(color, contours, k, new Scalar(vals[0],vals[1],vals[2]), 8);
		}
		new IViewer("HighRes Contours ",BReaderTools.bufferedImageFromMat(color));
		
		Point center = new Point(src.cols()/2, src.rows()/2);
		//Check hierarchy tree
		int[] res = polySearch(center, hierarchy, contours, 0);
		while(res[0]!=1 && res[2]!=-1){
			res = polySearch(center, hierarchy, contours, res[2]);
			if(res[0]==1) break;
		}
		
		MatOfInt tHull = new MatOfInt();
		int index = 0;
		if(res[1]!=-1) {
			index = res[1];
		}
		Imgproc.convexHull(contours.get(index), tHull);

		//get bounding box
		MatOfPoint cont = contours.get(index);
		Point[] points = new Point[tHull.rows()];
		for(int i=0; i<tHull.rows(); i++){
			int pIndex = (int) tHull.get(i, 0)[0];
			points[i] = new Point(cont.get(pIndex, 0)); 
		}
		Rect out = Imgproc.boundingRect(new MatOfPoint(points));
		return out;
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
//		Imgproc.threshold(dst, dst, 0,255,Imgproc.THRESH_BINARY_INV+Imgproc.THRESH_OTSU);
		Imgproc.Canny(dst, dst, 50, 200, 3, false);
//		Canny(src, dst, 20, 60, 3);

		List<MatOfPoint> contours = new LinkedList<>();
		Mat hierarchy = new Mat();
		/// Find contours
		Imgproc.findContours( dst, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

		Mat color = new Mat();
		Imgproc.cvtColor(src, color,Imgproc.COLOR_GRAY2BGR);
		for (int k = 0; k < contours.size(); k++){
			byte[] vals =  ITools.getHeatMapColor((float)k/(float)contours.size());
			Imgproc.drawContours(color, contours, k, new Scalar(vals[0],vals[1],vals[2]), 1);
		}
		new IViewer("LowRes Contours ",BReaderTools.bufferedImageFromMat(color));
		
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
		Rect out = Imgproc.boundingRect(new MatOfPoint(tHullPoints));
		return out;
	}
	
	/**
	 * Capture button has been press. Obtain the high resolution image and the low resolution 
	 * data. Once captured, the images are corrected. 
	 */
	public void captureEvent(){
		long t0, t1;
		t0 = System.currentTimeMillis();
		t1 = t0;
		byte[][][] img = getHidefImage();
		System.out.println("HiRez Capture: "+(System.currentTimeMillis()-t0)/1000.0);
		new IViewer("HiRez",ImageManipulation.getBufferedImage(img));
		
		t0 = System.currentTimeMillis();
		Rect bound = null;
		try {
			bound = highRes(BReaderTools.byteArrayToMat(ITools.toGrayscale(img)));
		}catch(java.lang.Exception e) {}
		System.out.println("First bounding box: "+(System.currentTimeMillis()-t0)/1000.0);
		
//		Mat imgMat = BReaderTools.byteArrayToMat(img);
//		Imgproc.rectangle(imgMat, bound.tl(), bound.br(), new Scalar(255,255,0), 8);
				
		byte[][] low = ITools.normalize(normImgCropped);
		t0 = System.currentTimeMillis();
		Rect boundLow = null;
		try {
			boundLow = lowResDist(BReaderTools.byteArrayToMat(low));
		}catch(java.lang.Exception e) {}
		System.out.println("second bounding box: "+(System.currentTimeMillis()-t0)/1000.0);
		
		if(bound == null || boundLow == null) {
			tts.doTTS("Document outside field of view. Please realign and press capture again.");
			return;
		}
		
		if((bound.x+bound.width+100) >= img[0][0].length || (bound.y+bound.height+100) >= img[0].length) {
			tts.doTTS("Document outside field of view. Please realign and press capture again.");
			return;
		}
		
		
		//Show the cropped height map with the bounding box
		Mat color = new Mat();
		Imgproc.cvtColor(BReaderTools.byteArrayToMat(low), color,Imgproc.COLOR_GRAY2BGR);
		Imgproc.rectangle(color, boundLow.tl(), boundLow.br(), new Scalar(255,255,0), 1);
		new IViewer("LowRes Bounding Box",BReaderTools.bufferedImageFromMat(color));
		
		Imgproc.cvtColor(BReaderTools.byteArrayToMat(ITools.toGrayscale(img)), color,Imgproc.COLOR_GRAY2BGR);
		Imgproc.rectangle(color, bound.tl(), bound.br(), new Scalar(255,255,0), 8);
		new IViewer("HighRes Bounding Box", BReaderTools.bufferedImageFromMat(color));
		
//		System.out.println(bound.height+", "+bound.width+": "+(double)bound.width/(double)bound.height);
//		System.out.println(boundLow.height+", "+boundLow.width+": "+(double)boundLow.width/(double)boundLow.height);
		
		double rW = (double)bound.width/(double)boundLow.width;
		double rH = (double)bound.height/(double)boundLow.height;
		int h = 0,w = 0, yO = 0, xO = 0;
		double s = 0;
		
		if(rH<rW) {
			s = rH;
			h = boundLow.height;
			w = (int) (bound.width/rH);
			if((w-boundLow.width)%2==0) {
				xO = (boundLow.width-w)/2;
			} 
		}else {
			s = rW;
			h = (int) (bound.height/rW);
			w = boundLow.width;
			if((h-boundLow.height)%2==0) {
				yO = (boundLow.height-h)/2;
			}
		}

		//show the high resolution image cropped
		byte[][][] hiRez = new byte[img.length][][];
		t0 = System.currentTimeMillis();
		for(int i = 0 ; i< img.length; i++) {
				hiRez[i] = ITools.crop(bound.x, bound.y, bound.x+bound.width, bound.y+bound.height,img[i]);
		}
		System.out.println("Cropping HiRez: "+(System.currentTimeMillis()-t0)/1000.0);
		
		//Show the IR amplitude image cropped
//		byte[][] amp = ITools.normalize(amplitudes);
//		byte[][] ampRez = resize(amp, (float)s);
//		int x0 = (int) ((boundLow.x+xO+40)*s), y0 = (int) ((boundLow.y+yO+25)*s);
//		ampRez = ITools.crop(x0, y0, x0+bound.width, y0+bound.height, ampRez);
//		new IViewer(ImageManipulation.getGrayBufferedImage(ampRez));
		
		//Show the Amplitude image in bounding box
//		Rect nBound = new Rect(boundLow.x+xO+40, boundLow.y+yO+25, w, h);
//		Mat gray = new Mat();
//		Imgproc.cvtColor(BReaderTools.byteArrayToMat(ITools.normalize(amplitudes)), gray,Imgproc.COLOR_GRAY2BGR);
//		Imgproc.rectangle(gray, nBound.tl(), nBound.br(), new Scalar(255,255,0), 1);
//		new IViewer(BReaderTools.bufferedImageFromMat(gray));
				
		//Crop the distance image and prepare for correction
		float[][] distRez;
		Mat destRezM = new Mat();
		switch(disp.getInterpolationMethod()){
		case 1:
			Imgproc.resize(BReaderTools.floatArrayToMat(normImg),
					destRezM, new Size(0, 0),
					s,s,
					Imgproc.INTER_LINEAR);//resize image
			break;
		case 2:
			Imgproc.resize(BReaderTools.floatArrayToMat(normImg),
					destRezM, new Size(0, 0),
					s,s,
					Imgproc.INTER_CUBIC);//resize image
			break;
		case 3:
			Imgproc.resize(BReaderTools.floatArrayToMat(normImg),
					destRezM, new Size(0, 0),
					s,s,
					Imgproc.INTER_LANCZOS4);//resize image
			break;
		}
		distRez = BReaderTools.matToFloatArray(destRezM);
		int xCentOff = (img[0][0].length - bound.width)/2 - bound.x;
		int yCentOff = (img[0].length - bound.height)/2 - bound.y;
		int x0 = (int) ((boundLow.x+xO+40)*s), y0 = (int) ((boundLow.y+yO+25)*s);
		distRez = ITools.crop(x0, y0, x0+bound.width, y0+bound.height, distRez);
		distRez = multiply(distRez, -100);
		
		byte[][][] foldCorrected = new byte[hiRez.length][][];
		t0 = System.currentTimeMillis();
		for(int i=0; i<hiRez.length; i++) {
			foldCorrected[i] = BReaderTools.foldCorrection(hiRez[i], distRez, xCentOff, yCentOff);
		}
		System.out.println("Fold Correction: "+(System.currentTimeMillis()-t0)/1000.0);
		
		float[][] distRezPushed = BReaderTools.foldCorrection(distRez, 
				(distRez[0].length - boundLow.width)/2 - boundLow.x, 
				(distRez.length - boundLow.height)/2 - boundLow.y);
		
		byte[][][] extensionCorrected = new byte[hiRez.length][][];
		t0 = System.currentTimeMillis();
		for(int i=0; i<hiRez.length; i++) {
			extensionCorrected[i] = LuWang.extentionWithLinearInterpolation(foldCorrected[i], distRez);
		}
		System.out.println("Extension Correction: "+(System.currentTimeMillis()-t0)/1000.0);
		
		new IViewer("Heigths",ImageManipulation.getGrayBufferedImage(ITools.normalize(distRez)));
		new IViewer("HiRez",ImageManipulation.getBufferedImage(hiRez));
//		new IViewer("Corrected",ImageManipulation.getBufferedImage(foldCorrected));
//		new IViewer("Heigths",ImageManipulation.getGrayBufferedImage(ITools.normalize(distRezPushed)));
//		new IViewer("Flat",ImageManipulation.getBufferedImage(foldCorrected));
//		new IViewer("Extension",ImageManipulation.getBufferedImage(extensionCorrected));
		System.out.println("Overall time: "+(System.currentTimeMillis()-t1)/1000.0);
		
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd-hh-mm-ss");
		String time = format.format(new Date(System.currentTimeMillis()));
		
		// Save Corrected High Rez.
		String imgPath = saveDir+"/correctedImage-"+time+".tiff";
		switch(disp.getCorrectionMethod()){
		case 1:{
			ImageManipulation.writeImage(hiRez, imgPath);
			new IViewer("Correction Results: None",ImageManipulation.getBufferedImage(hiRez));
		}break;
		case 2:{
			ImageManipulation.writeImage(foldCorrected, imgPath);
			new IViewer("Correction Results: Flattening",ImageManipulation.getBufferedImage(foldCorrected));
		}break;
		case 3:{
			ImageManipulation.writeImage(extensionCorrected, imgPath);
			new IViewer("Correction Results: Flattening + Extension",ImageManipulation.getBufferedImage(extensionCorrected));
		}break;
		}

		try{
			String text = abbyy.processImage(imgPath, saveDir+"/text-"+time+".txt");
			System.out.println("Done!!!!");
			tts.doTTS(text);
		}catch (java.lang.NullPointerException e){
			tts.doTTS("ABBYY License expired.");
		}
		saveData(time, img, hiRez, distRez, boundLow, bound);
		
	}
	
	public void saveData(String time, byte[][][] img, byte[][][] hiRez, float[][] distRez, Rect lr, Rect hr ) {
		// Save High Resolution Image
		ImageManipulation.writeImage(img, saveDir+"/highResolution-"+time+".tiff");
		// Save ToF Amplitude Image
		ImageManipulation.writeImage(ITools.normalize(amplitudes), saveDir+"/amplitude-"+time+".tiff");
		// Save ToF height map
		BReaderTools.saveFloatArrayToFile(saveDir+"/height-"+time+".bin", normImg);
		// Save Cropped High Rez
		ImageManipulation.writeImage(hiRez, saveDir+"/highResolutionCropped-"+time+".tiff");
		// Save Cropped Height Map
		BReaderTools.saveFloatArrayToFile(saveDir+"/heightCropped-"+time+".bin", distRez);
		
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveDir+"/data"+time+".txt")));
			out.write("#Raw Image Information#\n");
			out.write("LC:0\n");
			out.write("LW:"+amplitudes.length+"\n");
			out.write("LH:"+amplitudes[0].length+"\n");
			out.write("LName:"+"amplitude-"+time+".tiff"+"\n");
			out.write("HC:"+img.length+"\n");
			out.write("HW:"+img[0].length+"\n");
			out.write("HH:"+img[0][0].length+"\n");
			out.write("HName:"+"highResolution-"+time+".tiff"+"\n");
			out.write("#Cropping Information#\n");
			out.write("LCx:"+lr.x+"\n");
			out.write("LCy:"+lr.y+"\n");
			out.write("LCw:"+lr.width+"\n");
			out.write("LCh:"+lr.height+"\n");
			out.write("HCx:"+hr.x+"\n");
			out.write("HCy:"+hr.y+"\n");
			out.write("HCw:"+hr.width+"\n");
			out.write("HCh:"+hr.height+"\n");
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static boolean saveBase(String filename, float[]x){
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));
			out.writeInt(x.length);
			for(float i: x) {
				out.writeFloat(i);
				out.flush();
			}
			out.close();
			return true;
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static float[] readBase(String filename){
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(filename));
			int l = in.readInt();
			float[] out = new float[l];
			for(int i=0; i<l; i++) {
				out[i]=in.readFloat();
			}
			in.close();
			return out;
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static float[][] multiply(float[][] m, float a){
		int[] l = new int[]{m.length,m[0].length};
		float out[][] = new float[l[0]][l[1]];
		for(int y=0; y<l[0]; y++){
			for(int x=0; x<l[1]; x++){
				out[y][x] = m[y][x]*a;
			}
		}
		return out;
	}
	
	public static byte[][] resize(byte[][] img, float scale){
	    
		int height = img.length;
	    int width = img[0].length;
	    int heightN = (int) (img.length*scale);
	    int widthN = (int) (img[0].length*scale);
	    	    
	    byte[][] nImg = new byte[heightN][widthN];
	    float c = 1/scale;
		float ny = 0, yRem = 0, yRem2=0;
		int y = 0;
		float nx = 0, xRem = 0, xRem2 = 0;
		int x = 0;
		float d11, d12, d21, d22, dt;
	    
	    for(int h = 0; h < heightN; h++){
    		nx=0;
    		x = 0;
    		xRem = 0;
    		xRem2 = 0;
	    	for(int w = 0; w < widthN; w++){
	    		
	    		if(xRem != 0.00 || yRem != 0.00 ){
	    			if(y>0 && y+1<height && x>0 && x+1<width){
		    			d11 = (float) (1f/Math.sqrt(xRem2+yRem2));
		    			d12 = (float) (1f/Math.sqrt((1-xRem)*(1-xRem)+yRem2));
		    			d21 = (float) (1f/Math.sqrt(xRem2+(1-yRem)*(1-yRem)));
		    			d22 = (float) (1f/Math.sqrt((1-xRem)*(1-xRem)+(1-yRem)*(1-yRem)));
		    			dt = d11 + d12 + d21 + d22;
	    				nImg[h][w] = (byte) ((d11*(float)(img[y][x] & 0x0FF)+d12*(float)(img[y][x+1] & 0x0FF)+d21*(float)(img[y+1][x] & 0x0FF)+d22*(float)(img[y+1][x+1] & 0x0FF))/dt);
	    			}
	    		}else{
		    		if(y>0 && y<height && x>0 && x<width){
		    			nImg[h][w] = img[y][x];
		    		}
	    		}
	    		nx+=c;
	    		x = (int) nx;
	    		xRem = nx-x;
	    		xRem2 = xRem*xRem;	
	    	}
    		ny += c;
    		y = (int) ny;
    		yRem = ny-y;
    		yRem2 = yRem*yRem;
	    }
		return nImg;
	}
	
	public static float[][] resize(float[][] img, float scale){
	    
		int height = img.length;
	    int width = img[0].length;
	    int heightN = (int) (img.length*scale);
	    int widthN = (int) (img[0].length*scale);
	    	    
	    float[][] nImg = new float[heightN][widthN];
	    float c = 1/scale;
		double ny = 0, yRem = 0, yRem2 = 0, yRem12 = 0;
		int y = 0;
		double nx = 0, xRem = 0, xRem2 = 0, xRem12 = 0;
		int x = 0;
		double d11, d12, d21, d22, dt;
		
	    for(int h = 0; h < heightN; h++){
    		y = (int) ny;
    		if(y>0 && y<height){
        		yRem = ny-y;
        		yRem2 = yRem*yRem;
        		yRem12 = (1-yRem)*(1-yRem);
        		nx=0;
        		x = 0;
		    	for(int w = 0; w < widthN; w++){
		    		x = (int) nx;
		    		if(x>0 && x<width){
			    		xRem = nx-x;
			    		if(xRem != 0.00 || yRem != 0.00 ){
			    			if(y<height-1 && x<width-1){
					    		xRem2 = xRem*xRem;
					    		xRem12 = (1-xRem)*(1-xRem);
				    			d11 = (1d/Math.sqrt(xRem2+yRem2));
				    			d12 = (1d/Math.sqrt(xRem12+yRem2));
				    			d21 = (1d/Math.sqrt(xRem2+yRem12));
				    			d22 = (1d/Math.sqrt(xRem12+yRem12));
				    			dt = d11 + d12 + d21 + d22;
			    				nImg[h][w] = (float) ((d11*img[y][x]+d12*img[y][x+1]+d21*img[y+1][x]+d22*img[y+1][x+1])/dt);
			    			}
			    		}else{
				    		nImg[h][w] = img[y][x];
			    		}
		    		}
		    		nx+=c;
		    	}
	    	}
    		ny += c;
	    }
		return nImg;
	}
	
	public static void main(String[] args){
		new BaseSegmentation();
//		byte[][][] img = ImageManipulation.loadImage(System.getProperty("user.home")+"/Pictures/i1.png");
//		byte[][][] img2 = new byte[img.length][][];
//		long t0 = System.currentTimeMillis();
//		
////		float dt = 0;
////		for(int i =0 ; i<100; i++){
////			t0 = System.currentTimeMillis();
////			for(int c=0; c<img.length; c++){
////				img2[c]=resize(img[c], 2f);
////			}
////			dt += (System.currentTimeMillis()-t0);
////		}
////		dt/=100.0;
////		System.out.println(dt);
////		new IViewer("img1", ImageManipulation.getBufferedImage(img2));
//		
//
//		Mat destRezM = new Mat();
//		Imgproc.resize(BReaderTools.byteArrayToMat(img),
//				destRezM, new Size(0, 0),
//				10,10,
//				Imgproc.INTER_CUBIC);//resize image
//		new IViewer("OpenCV-INTER_CUBIC",BReaderTools.bufferedImageFromMat(destRezM));
//		
////		new IViewer(ImageManipulation.getGrayBufferedImage(img[0]));
//		
////		float[][][] imgF = ITools.byte2Float(img);
////		float[][] imgF2 = BReaderTools.getBicubicScaling(imgF[0], 5);
////		System.out.println((System.currentTimeMillis()-t0)/1000.0);
////		new IViewer(ImageManipulation.getGrayBufferedImage(ITools.normalize(imgF2)));
////		ImageManipulation.writeImage(ITools.normalize(imgF2), System.getProperty("user.home")+"/Pictures/brain2.jpg");
		
	}
}
