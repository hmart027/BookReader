package edu.fiu.cate.breader.tools;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import math2.Matrix;

public class BReaderTools {
	
	public static void saveFloatArrayToFile(String file, float[] array){
		try {
			java.io.DataOutputStream  out = new java.io.DataOutputStream(new FileOutputStream(file));
			for(float f: array){
				out.writeFloat(f);
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
			java.io.DataOutputStream  out = new java.io.DataOutputStream(new FileOutputStream(file));
			for(int y=0; y<array.length; y++){
				for(int x=0; x<array[0].length; x++){
					out.writeFloat(array[y][x]);
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
	
	public static Mat floatArrayToMat(float[][] bi) {
		Mat mat = new Mat(bi.length, bi[0].length, CvType.CV_32FC1);
		for(int i=0; i<bi.length; i++)
			mat.put(i, 0, bi[i]);
		return mat;
	}
	
	public static float[][] matToFloatArray(Mat mat) {
		float[][] out = new float[mat.rows()][mat.cols()];
		for(int i=0; i<mat.rows(); i++)
			mat.get(i, 0, out[i]);
		return out;
	}
	
	public static byte[][] matToByteArray(Mat mat) {
		byte[][] out = new byte[mat.rows()][mat.cols()];
		for(int i=0; i<mat.rows(); i++)
			mat.get(i, 0, out[i]);
		return out;
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
	
	//Not Working right
	public static float[][] getBicubicScaling(float[][] img, float scale){
		int[] l = {img.length, img[0].length};
		int[] nl = {(int) (l[0]*scale), (int) (l[1]*scale)};
		float[][] out = new float[nl[0]][nl[1]];
		
		double[][] A1= {
				{ 1, 0, 0, 0},
				{ 0, 0, 1, 0},
				{-3, 3,-2,-1},
				{ 2,-2, 1, 1},				
		};
		double[][] A2= {
				{ 1, 0,-3, 2},
				{ 0, 0, 3,-2},
				{ 0, 1,-2, 1},
				{ 0, 0,-1, 1},				
		};	
		double[][] a, f= new double[4][4];
		
		float c = 1f/scale;
		float x, y;
		int xi, yi;
		float dx, dy;
		float x1,x2,x3,y1,y2,y3;
		for(int j=0; j<nl[0]; j++) {
			y=j*c;
			yi=(int) y;
			dy=y-yi;
			y1=dy;
			y2=y1*y1;
			y3=y2*y1;
			
			//Fix this. An approximation is required for boundary pixels
			if(yi<1||yi>=l[0]-2)continue;
			
			for(int i=0; i<nl[1]; i++) {
				x=i*c;
				xi=(int) x;
				dx=x-xi;
				x1=dx;
				x2=x1*x1;
				x3=x2*x1;
				
				//Fix this. An approximation is required for boundary pixels
				if(xi<1||xi>=l[1]-2)continue;
				
				f[0][0]=img[yi][xi];
				f[0][1]=img[yi][xi+1];
				f[0][2]=(img[yi+1][xi]-img[yi-1][xi])/2f;
				f[0][2]=(img[yi+1][xi+1]-img[yi-1][xi+1])/2f;
				
				f[1][0]=img[yi+1][xi];
				f[1][1]=img[yi+1][xi+1];
				f[1][2]=(img[yi+2][xi]-img[yi][xi])/2f;
				f[1][2]=(img[yi+2][xi+1]-img[yi][xi+1])/2f;
				
				f[2][0]=(img[yi][xi+1]-img[yi][xi-1])/2f;
				f[2][1]=(img[yi][xi+2]-img[yi][xi])/2f;
				f[2][2]=(img[yi+1][xi+1]-img[yi+1][xi-1]-
						img[yi-1][xi+1]+img[yi-1][xi-1])/4f;
				f[2][2]=(img[yi+1][xi+2]-img[yi+1][xi]-
						img[yi-1][xi+2]+img[yi-1][xi])/4f;

				f[3][0]=(img[yi+1][xi+1]-img[yi+1][xi-1])/2f;
				f[3][1]=(img[yi+1][xi+2]-img[yi+1][xi])/2f;
				f[3][2]=(img[yi+2][xi+1]-img[yi+2][xi-1]-
						img[yi][xi+1]+img[yi][xi-1])/4f;
				f[3][2]=(img[yi+2][xi+2]-img[yi+2][xi]-
						img[yi][xi+2]+img[yi][xi])/4f;
				
				a = Matrix.multiply(Matrix.multiply(A1, f), A2);
				
				out[j][i] = (float) (
						(a[0][0]+a[1][0]*x1+a[2][0]*x2+a[3][0]*x3)
						+(a[0][1]+a[1][1]*x1+a[2][1]*x2+a[3][1]*x3)*y1
						+(a[0][2]+a[1][2]*x1+a[2][2]*x2+a[3][2]*x3)*y2
						+(a[0][3]+a[1][3]*x1+a[2][3]*x2+a[3][3]*x3)*y3
						);
				
			}
		}
		
		return out;
	}

}
