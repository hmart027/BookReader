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
		for(int y=0; y<bi[0].length; y++)
			for(int x=0; x<bi[0][0].length; x++)
				mat.put(y, x, new byte[]{bi[2][y][x], bi[1][y][x], bi[0][y][x]});
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
	
}
