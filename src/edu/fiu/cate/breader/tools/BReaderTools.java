package edu.fiu.cate.breader.tools;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class BReaderTools {
	
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
	
	public static Mat byteArrayToMat(byte[][][] bi) {
		Mat mat = new Mat(bi[0].length, bi[0][0].length, CvType.CV_8UC3); //The indeces are BRG 
		byte[] data = new byte[bi[0].length * bi[0][0].length * 3];
		int i = 0;
		for(int y=0; y<bi[0].length; y++) {
			for(int x=0; x<bi[0][0].length; x++) {
				data[i++] = bi[2][y][x]; 
				data[i++] = bi[1][y][x]; 
				data[i++] = bi[0][y][x]; 
			}
		}
		mat.put(0, 0, data);
		return mat;
	}
	
	public static BufferedImage bufferedImageFromMat(Mat bi) {
		// Create an empty image in matching format
		BufferedImage img;
		if(bi.type()==CvType.CV_8UC1){
			img = new BufferedImage(bi.width(), bi.height(), BufferedImage.TYPE_BYTE_GRAY);
			// Get the BufferedImage's backing array and copy the pixels directly into it
			byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
			bi.get(0, 0, data);
			return img;
		}else if(bi.type()==CvType.CV_8UC3){
			img = new BufferedImage(bi.width(), bi.height(), BufferedImage.TYPE_3BYTE_BGR);
			// Get the BufferedImage's backing array and copy the pixels directly into it
			byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
			bi.get(0, 0, data);
			return img;
		}
		return null;
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
	
	public static float cameraHeight = 53f;//58.05f;
	/**
	 * Best working version
	 * @param img
	 * @param heights
	 * @param xO
	 * @param yO
	 * @return
	 */
	public static byte[][] foldCorrection(byte[][] img, float[][] heights, int xO, int yO){
	    
	    int height = img.length;
	    int width = img[0].length;
	    
	    int xc = width/2+xO;
	    int yc = height/2+yO;
	    
	    byte[][] nImg = new byte[height][width];
	    int x = 0;
	    double nx = 0, xRem = 0;
	    int y = 0;
	    double ny = 0, yRem = 0;
	    
	    double d11, d12, d21, d22, dt;
	    
	    for(int h = 0; h < height; h++){
	    	for(int w = 0; w < width; w++){
	       		
	    		nx = (xc - ((xc-w)*cameraHeight/(cameraHeight-heights[h][w])));
	    		ny = (yc - ((yc-h)*cameraHeight/(cameraHeight-heights[h][w])));
	    		x = (int) nx;
	    		y = (int) ny;
	    		xRem = nx-x;
	    		yRem = ny-y;
	    		
	    		if(xRem != 0.00 || yRem != 0.00 ){
	    			if(y>0 && y+1<height && x>0 && x+1<width){
		    			d11 = (1f/Math.sqrt(xRem*xRem+yRem*yRem));
		    			d12 = (1f/Math.sqrt((1-xRem)*(1-xRem)+yRem*yRem));
		    			d21 = (1f/Math.sqrt(xRem*xRem+(1-yRem)*(1-yRem)));
		    			d22 = (1f/Math.sqrt((1-xRem)*(1-xRem)+(1-yRem)*(1-yRem)));
		    			dt = d11 + d12 + d21 + d22;
	    				nImg[h][w] = (byte) ((d11*(img[y][x] & 0x0FF)+d12*(img[y][x+1] & 0x0FF)+d21*(img[y+1][x] & 0x0FF)+d22*(img[y+1][x+1] & 0x0FF))/dt);
	    			}
	    		}else{
		    		if(y>0 && y<height && x>0 && x<width){
		    			nImg[h][w] = img[y][x];
		    		}
	    		}
	    	}
	    }
		return nImg;
	}
	
	/**
	 * Correct heights. Best working version
	 * @param heights
	 * @param xO
	 * @param yO
	 * @return
	 */
	public static float[][] foldCorrection(float[][] heights, int xO, int yO){
	    
	    int height = heights.length;
	    int width = heights[0].length;
	    
	    int xc = width/2+xO;
	    int yc = height/2+yO;
	    
	    float[][] nImg = new float[height][width];
	    int x = 0;
	    double nx = 0, xRem = 0;
	    int y = 0;
	    double ny = 0, yRem = 0;
	    
	    double d11, d12, d21, d22, dt;
	    
	    for(int h = 0; h < height; h++){
	    	for(int w = 0; w < width; w++){
	       		
	    		nx = (xc - ((xc-w)*cameraHeight/(cameraHeight-heights[h][w])));
	    		ny = (yc - ((yc-h)*cameraHeight/(cameraHeight-heights[h][w])));
	    		x = (int) nx;
	    		y = (int) ny;
	    		xRem = nx-x;
	    		yRem = ny-y;
	    		
	    		if(xRem != 0.00 || yRem != 0.00 ){
	    			if(y>0 && y+1<height && x>0 && x+1<width){
		    			d11 = (1f/Math.sqrt(xRem*xRem+yRem*yRem));
		    			d12 = (1f/Math.sqrt((1-xRem)*(1-xRem)+yRem*yRem));
		    			d21 = (1f/Math.sqrt(xRem*xRem+(1-yRem)*(1-yRem)));
		    			d22 = (1f/Math.sqrt((1-xRem)*(1-xRem)+(1-yRem)*(1-yRem)));
		    			dt = d11 + d12 + d21 + d22;
	    				nImg[h][w] = (float) ((d11*heights[y][x]+d12*heights[y][x+1]+d21*heights[y+1][x]+d22*heights[y+1][x+1])/dt);
	    			}
	    		}else{
		    		if(y>0 && y<height && x>0 && x<width){
		    			nImg[h][w] = heights[y][x];
		    		}
	    		}
	    	}
	    }
		return nImg;
	}
	
	public static float[][] getLanczozScaling(float[][] img, float scale){
		return getLanczozScaling(img, scale, 3);
	}
	
	public static float[][] getLanczozScaling(float[][] img, float scale, int kSize){
		int[] l = {img.length, img[0].length};
		int[] nl = {(int) (l[0]*scale), (int) (l[1]*scale)};
		float[][] out = new float[nl[0]][nl[1]];
		
		float c = 1f/scale;
		float oY, oX; // original index; from 0 to l
		float dY, dX; // fractional index; from 0 to 1
		float Lxy; 	  // Lanczos xy
		float s; 	  // sum
		float pY,pX, pY2, pX2;
		float pi2 = (float) (Math.PI*Math.PI), pi4 = (float) (Math.PI*Math.PI*Math.PI*Math.PI);
		java.util.TreeSet<Float> pys = new java.util.TreeSet<>();
		
		for(int y=kSize; y<nl[0]-kSize; y++) {
			oY = y*c;
			dY = oY-(int)oY;
			for(int x=kSize; x<nl[1]-kSize; x++) {
				oX = x*c;
				dX = oX-(int)oX;
				s = 0;
				Lxy = 0;
				for(int aY=1-kSize; aY<=kSize; aY++) {
					pY = dY-aY;
					pY2=pY*pY;
					pys.add(pY);
					for(int aX=1-kSize; aX<=kSize; aX++) {
						pX = dX-aX;
						pX2=pX*pX;
						if(pX==0 && pY==0) {
							Lxy = 1;
						}else if(pX==0 && Math.abs(pY)<kSize){
							Lxy = (float) ((float)kSize*Math.sin(Math.PI*pY)*Math.sin(Math.PI*pY/(float)kSize)/(pi2*pY2));
						}else if(pY==0 && Math.abs(pX)<kSize){
							Lxy = (float) ((float)kSize*Math.sin(Math.PI*pX)*Math.sin(Math.PI*pX/(float)kSize)/(pi2*pX2));
						}else if (Math.abs(pX)<kSize && Math.abs(pY)<kSize){
							Lxy = (float) ((float)(kSize*kSize)*Math.sin(Math.PI*pY)*Math.sin(Math.PI*pX)*Math.sin(Math.PI*pY/(float)kSize)*Math.sin(Math.PI*pX/(float)kSize)/
									(pi4*pX2*pY2));
						}
						if((int) (oY+aY)>=0 && (int) (oY+aY)<l[0] && (int) (oX+aX)>=0 && (int) (oX+aX)< l[1])
							s+=img[(int) (oY+aY)][(int) (oX+aX)]*Lxy;
					}
				}
				out[y][x]=s;
				
			}
		}
		System.out.println("pYs: "+pys.size());
		return out;
	}
	
	public static float[][] getBicubicScaling(float[][] img, float scale){
		int[] l = {img.length, img[0].length};
		int[] nl = {(int) (l[0]*scale), (int) (l[1]*scale)};
		float[][] out = new float[nl[0]][nl[1]];
		
		float c = 1f/scale;
		
		
		return out;
	}

}
