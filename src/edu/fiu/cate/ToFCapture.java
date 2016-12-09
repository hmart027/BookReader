package edu.fiu.cate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ToFCapture {
	
	public static float cameraHeight = 0.5805f;
	
	public static final double FIELD_OF_VIEW_X = 90.0;
	public static final double FIELD_OF_VIEW_Y = 67.5;
	
	public static final double DEGREE_PER_PIXEL = 0.5625;
	public static final double RADIAN_PER_PIXEL = Math.toRadians(DEGREE_PER_PIXEL);
	
	private static volatile boolean writtingFile = false;
	private static volatile float fileProgress = 0;
	
/*	public static float[][] correctPerspective(float[][] in){
		int height = in.length;
		int width = in[0].length;
		float[][] out = new float[height*width][3];
		float y0 = height/2;
		float x0 = width/2;
				
		double dy = 0;
		double y2 = 0;
		double dx = 0;
		double r = 0;
		double theta = 0;
		float h = 0;
		
		double nY = 0;
		double nX = 0;
				
		int index = 0;
		for(int y = 0; y<height; y++){
			dy = y0-y;
			y2 = Math.pow(dy,2);
			for(int x = 0; x<width; x++){
				dx = x-x0;
				r = Math.pow(y2+Math.pow(dx, 2), 0.5);
				theta = r*RADIAN_PER_PIXEL;
				h = (float) (Math.cos(theta)*in[y][x]);
				nX = h*Math.tan(dx*RADIAN_PER_PIXEL);
				nY = h*Math.tan(dy*RADIAN_PER_PIXEL);
				out[index][0] = (float)nX;
				out[index][1] = (float)nY;
				out[index][2] = (float)h;
				
//				System.out.println(out[index][0]+","+out[index][1]+","+out[index][2]);
//				if(0==0) System.exit(0);
				index++;
			}
		}
		
		System.out.println(index);
		if(0==0) System.exit(0);
		
		return out;
	}*/
	
	public static float[][] correctPerspective(float[][] in){
		int height = in.length;
		int width = in[0].length;
		float[][] out = new float[height*width][3];
		float y0 = height/2;
		float x0 = width/2;
				
		double dy = 0;
		double y2 = 0;
		double dx = 0;
		double r = 0;
		double theta = 0;
		float h = 0;
		
		double nY = 0;
		double nX = 0;
		int i = 0;
		
		for(int y = 0; y<height; y++){
			dy = y0-y;
			y2 = Math.pow(dy,2);
			for(int x = 0; x<width; x++){
				dx = x-x0;
				r = Math.pow(y2+Math.pow(dx, 2), 0.5);
				theta = r*RADIAN_PER_PIXEL;
				h = (float) (Math.cos(theta)*in[y][x]);
				nX = h*Math.tan(dx*RADIAN_PER_PIXEL);
				nY = h*Math.tan(dy*RADIAN_PER_PIXEL);
				out[i][0] = x;
				out[i][1] = y;
				out[i][2] = (float)h;
				i++;
				
//				System.out.println(h);
				System.out.println(x+"\t"+y+"\t"+in[y][x]);
				System.out.println(nX+"\t"+nY+"\t"+h+"\n");
			}
		}
		System.out.println(out[300][1]);
//		if(0==0) System.exit(0);
		return out;
	}
	
	public static float[][][] getPerspectiveFiles(float[][] in){
		int height = in.length;
		int width = in[0].length;
		float[][][] out = new float[3][height][width];
		float y0 = height/2;
		float x0 = width/2;
				
		double dy = 0;
		double y2 = 0;
		double dx = 0;
		double r = 0;
		double theta = 0;
		float h = 0;
		
		double nY = 0;
		double nX = 0;
		
//		int c = 0;
		
		for(int y = 0; y<height; y++){
			dy = y0-y;
			y2 = Math.pow(dy,2);
			for(int x = 0; x<width; x++){
				dx = x-x0;
				r = Math.pow(y2+Math.pow(dx, 2), 0.5);
				theta = r*RADIAN_PER_PIXEL;
				h = (float) (Math.cos(theta)*in[y][x]);
				nX = h*Math.tan(dx*RADIAN_PER_PIXEL);
				nY = h*Math.tan(dy*RADIAN_PER_PIXEL);
				out[0][y][x] = (float)nX;
				out[1][y][x] = (float)nY;
				out[2][y][x] = (float)h;
//				out[2][y][x] = (float)r;
				
//				c++;
//				if(c==2)
//					System.out.println(r);
				
//				System.out.println(x+"\t"+y+"\t"+in[y][x]);
//				System.out.println(nX+"\t"+nY+"\t"+h+"\n");
			}
		}
//		if(0==0)
//			System.exit(0);
		return out;
	}
	
	public static boolean writeBinaryFile(String p, float[][] vals){
		try {
			writtingFile = true;
			new WritteStatus().start();
			float size = vals.length*vals[0].length;
			int count = 0;
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(p)));
			for(float[] val: vals){
				for(float f: val){
					out.writeFloat(f);
					count ++;
					if(count==120)
						System.out.println(f);
					fileProgress = count/size*100f;
				}
			}
			out.flush();
			out.close();
			writtingFile = false;
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writtingFile = false;
		return false;
	}
	
	public static float[][] readBinaryFileAsFloat(String path, int height, int width){
		float[][] out = new float[height][width];
		File f = new File(path);
		try {
//			FileInputStream in = new FileInputStream(f);
			DataInputStream in = new DataInputStream(new FileInputStream(f));
			
//			byte[] n = new byte[4];
			int h = 0;
			int w = 0;
			while(in.available()>0){
//				int o = 3;
//				while(o>=0){
//					in.read(n,o,1);
//					o--;
//				}
//				out[h++][w] = ByteBuffer.wrap(n).getFloat()*100f; // from m to cm
				out[h][w++] = cameraHeight-in.readFloat();
//				if(h==height){
//					h = 0;
//					w++;
//				}
				if(w==width){
					w = 0;
					h++;
				}
			}
			in.close();
			return out;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static float[][] readDepthFile(String path, int height, int width){
		float[][] out = new float[height][width];
		File f = new File(path);
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(f));
			
			int h = 0;
			int w = 0;
			int c = 0;
			while(in.available()>0){
				out[h][w++] = in.readFloat();

//				c++;
//				if(c==85){
//					System.out.println(0.5805-out[h][w-1]);
//				}
					
				if(w==width){
					w = 0;
					h++;
				}
			}
			in.close();
			return out;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static float[][] flatSurface(int height, int width, double distance){
		float[][] out = new float[height][width];
		float y0 = height/2;
		float x0 = width/2;
				
		double dy = 0;
		double y2 = 0;
		double dx = 0;
		double r = 0;
		double theta = 0;
		
		for(int y = 0; y<height; y++){
			dy = y0-y;
			y2 = Math.pow(dy,2);
			for(int x = 0; x<width; x++){
				dx = x-x0;
				r = Math.pow(y2+Math.pow(dx, 2), 0.5);
				theta = r*RADIAN_PER_PIXEL;
				out[y][x]=(float) (distance/Math.cos(theta));
			}
		}
		return out;
	}
	
	private static class WritteStatus extends Thread{
		public void run(){
			while(writtingFile){
				System.out.println("File Writting: "+fileProgress);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
