package edu.fiu.cate;

import img.ImageManipulation;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class BookReaderMain {
	
	public static float cameraHeight = 50;
	public static int defaultBookSupportHeight = 350;
	public static int pixelActivationTh = 127;
	public static File folder;

	public static void main(String[] args){
	    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd"); 		  
		folder = new File("C:\\School\\Research\\BookReader\\image trials\\"+date.format(new Date(System.currentTimeMillis())));
		if(!folder.exists()){
			if(!folder.mkdir()){
				System.out.println("Couldn't create folder. Exiting program.");
				System.exit(-1);
			}
		}else{
			if(!folder.isDirectory()){
				if(!folder.mkdir()){
					System.out.println("Couldn't create folder. Exiting program.");
					System.exit(-1);
				}
			}
		}
		
		try {
			byte[][] img = ImageManipulation.getRGBAArrayBand(ImageIO.read(new File("C:\\School\\Research\\BookReader\\image trials\\outline1.jpg")),1);
			//Book 1
//			byte[][] img = ImageManipulation.getRGBAArrayBand(ImageIO.read(new File("C:\\School\\Research\\BookReader\\image trials\\afterSegText.jpg")),1);
		    float[] heights = processEdgeImg(ImageIO.read(new File("C:\\School\\Research\\BookReader\\image trials\\afterEdgeDetect.jpg")));
			//Book 2
//			byte[][] img = ImageManipulation.getRGBAArrayBand(ImageIO.read(new File("C:\\School\\Research\\BookReader\\image trials\\15-06-22\\afterSegText.jpg")),1);
//		    float[] heights = processEdgeImg(ImageIO.read(new File("C:\\School\\Research\\BookReader\\image trials\\15-06-22\\afterEdgeDetect.jpg")));
		    	
//		    double[][] kernel3 = getGaussianKernel(5, 5, 1.0);	//0.001
		    
//			for(int y = 0; y < kernel3.length; y++){
//				for(int x = 0; x < kernel3[0].length; x++){
//					System.out.print(kernel3[y][x]+" ");
//				}
//				System.out.println();
//			}
		    
//		    int[][] kernel4 = getLaplacianOfGaussianKernel(15,15,2.5);	//15, 15, 2.0
//		    int[][] kernel4 = getLaplacianOfGaussianKernel(21,21,3.0);	//15, 15, 1.8
		    int[][] kernel4 = getLaplacianOfGaussianKernel(15,15,2.0);	//15, 15, 1.8
		    
//		    for(int y = 0; y < kernel4.length; y++){
//				for(int x = 0; x < kernel4[0].length; x++){
//					System.out.print(kernel4[y][x]+" ");
//				}
//				System.out.print("; ");
//			}
//		    if(1==1)System.exit(0);
		    		    		    		    		    		    
//		    for(float h: heights)
//		    	System.out.println(h);
		    
		    int height = img.length;
		    int width = img[0].length;
		    
		    int xc = width/2;
		    int yc = height/2;
		    
		    heights = stretchCurve(heights, width);
		    
//		    for(float f: heights){
//		    	System.out.println(f);
//		    }
		    		    
		    byte[][] nImg = new byte[height][width];
		    
		    long t0 = System.currentTimeMillis();
		    
//		    for(int h = 100; h < height-100; h++){
//		    	for(int w = 50; w < width-50; w++){
//		       		
//		    		//retrieving from copy
//		    		float nx = xc - ((xc-w)*(cameraHeight/heights[w]));
//		    		float ny = yc - ((yc-h)*(cameraHeight/heights[w]));
//		    		int x = (int) nx;
//		    		int y = (int) ny;
//		    		
////		    		float xRem = nx%(float)x;
////		    		float yRem = ny%(float)y;
////		    		
////		    		if(xRem != 0.00 || yRem != 0.00 ){
////		    			float d11 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(yRem, 2), 0.5f));
////		    			float d12 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(yRem, 2), 0.5f));
////		    			float d21 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
////		    			float d22 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
////		    			float dt = d11 + d12 + d21 + d22;
////		    			if(y>0 && y+1<height && x>0 && x+1<width)
////		    				nImg[h][w] = (byte) ((d11*(float)(img[y][x] & 0x0FF)+d12*(float)(img[y][x+1] & 0x0FF)+d21*(float)(img[y+1][x] & 0x0FF)+d22*(float)(img[y+1][x+1] & 0x0FF))/dt);
////		    		}else{
//			    		if(y>0 && y<height && x>0 && x<width)
//			    			nImg[h][w] = img[y][x];
////		    		}
//		    	}
//		    }
		    
//		    nImg = img;
		    		    
//		    nImg = changeContrast(invertGrayScale(convolution(nImg, kernel3)), 0.2f);
//		    nImg = applyLoG(img, kernel4);
		    
		    nImg = convolution(img, kernel4);
		    nImg = getBinary(nImg, 110);	//110
		    
		    
//		    byte[][][] i2 = findRectangle(nImg, 0.98);
//		    if(1==1)System.exit(0);
		    
//		    nImg = convolution(nImg, v);
//		    nImg = getBinary(nImg, (byte)5);
		    
		    System.out.println(System.currentTimeMillis()-t0);
		    date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"); 	
		    String dst = folder.getAbsolutePath()+"\\javaManipulation"+date.format(new Date(System.currentTimeMillis()))+".jpg";
		    ImageManipulation.saveGrayArrayAsImage(nImg, dst, "jpeg");
//		    ImageManipulation.saveRGBArrayAsImage(i2, dst, "jpeg");
		    
		    Desktop.getDesktop().open(new File(dst));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static byte[][][] findRectangle(byte[][] img, double th){
		byte[][][] o = new byte[img.length][img[0].length][3];
		int yc = img.length/2;
		int xc = img[0].length/2;
		int xu = 20;
		int xl = 20;
		int yu = 20;
		int yl = 20;
		double sum = 100;
		int count = 0;
		System.out.println((yc)+"; "+(xc));
		//Increase X
		do{
			count = 0;
			sum = 0;
			for(int y = yc-yl; y<=yc+yu; y++){
				for(int x = xc-xl; x<=xc+xu; x++){
					sum += img[y][x] & 0x0FF;
					count++;
				}	
			}
			xu++;
			xl=xu;
			sum = sum/(count*255d);
		}while(sum>=th);
		sum = 0;
		System.out.println((yc-yl)+"; "+(xc-xl));
		// Increase Y
		double lastSum = 0;
		boolean done = false;
		int lastUsed = 0; // 0-nune, 1-y, 2-x
		do{
			count = 0;
			sum = 0;
			for(int y = yc-yl; y<=yc+yu; y++){
				for(int x = xc-xl; x<=xc+xu; x++){
					sum += img[y][x] & 0x0FF;
					count++;
				}	
			}
			sum = sum/(count*255d);
			if(sum<lastSum){	//Running into more black pixels
				switch (lastUsed) {
				case 0:
					xu--;
					xl=xu;
					lastUsed = 2;
					break;
				case 1:
					xu--;
					xl=xu;
					lastUsed = 2;
					break;
				case 2:	// x used twice in a row
					done = true;
					break;
				}
			}else{
				yu++;
				yl=yu;
				lastUsed = 1;
			}

			lastSum = sum;
//			System.out.println((yc-yl)+"; "+(xc-xl)+"; "+lastUsed);
			
		}while(!done);
		//Combine images
		for(int y = 0; y<img.length; y++){
			for(int x = 0; x<img[0].length; x++){
				o[y][x][0] = img[y][x];
				double dx = Math.abs(xc-x);
				double dy = Math.abs(yc-y);
				//Vertical line
				if(dx<(xu+3) && dx>(xu-3) && dy<yu)
					o[y][x][1] = (byte)255;
				//Horizontal line
				if(dy<(yu+3) && dy>(yu-3) && dx<xu)
					o[y][x][1] = (byte)255;
			}	
		}
		System.out.println((yc-yl)+"; "+(xc-xl));
		return o;
	}

	public static byte[][] applyLoG(byte[][] img, double[][] ker){
		if(img == null || ker == null) return null;
		int M = img.length; 	// height
		int N = img[0].length; 	// width;
		int m = ker.length; 	// height
		int n = ker[0].length; 	// width;
		int oM = M - m + 1;
		int oN = N - n + 1;
		byte[][] o = new byte[oM][oN];
		double[][] t = new double[oM][oN];
		double maxT = 0;
		double minT = 0;
		
		// Going through every pixel in the temporaty image.
		for(int i = 0; i < oM; i++){
			for(int j = 0; j < oN; j++){
				// Going through every pixel in the kernel.
				for(int k = 0; k < m; k++){
					for(int l = 0; l < n; l++){
						t[i][j] += ((img[i+k][j+l] & 0x0FF)*ker[k][l]);
						if(t[i][j] > maxT) maxT = t[i][j];
						if(t[i][j] < minT) minT = t[i][j];
					}
				}
			}	
		}
		double slope = 255f/(maxT-minT);
		//Normalizing the image
		for(int i = 0; i < oM; i++){
			for(int j = 0; j < oN; j++){
				// Going through every pixel in the kernel.
				for(int k = 0; k < m; k++){
					for(int l = 0; l < n; l++){
						o[i][j] += ((t[i][j]-minT)*slope);
					}
				}
			}	
		}
		return o;
	}
	
	public static float[] processEdgeImg(BufferedImage image){
		byte [][] img = ImageManipulation.getGrayImg(image);
		float out[] = new float[img.length];	//Array with size equal to img width
				
		for(int w = 0; w < img.length; w++){
			for (int h = 0; h < img[0].length; h++){
				int p = img[w][h] & 0x0FF;
				if(out[w] == 0 && p >= pixelActivationTh){
					out[w] = h;
					continue;
				}
			}
			if(out[w]==0) out[w] = defaultBookSupportHeight;
		}
		for(int i = 0; i< out.length; i++)
			out[i] = out[i]*0.0207f + 42.69f;
		return out;
	}
	
	public static float[] stretchCurve(float[] old, int nWidth){
		float[] out = new float[nWidth];
		float ratio = (float)old.length / (float)nWidth;
		for(int i = 0; i<nWidth; i++){
			int p = (int) (i * ratio);
			float rem = (i*ratio)%p;
			if(rem > 0.01 && p < (old.length-1))
				out[i] = (old[p+1]-old[p])*rem + old[p];
			else
				out[i] = old[p];
		}
		return out;
	}
	
	public static byte[][] convolution(byte[][] img, byte[][] ker){
		if(img == null || ker == null) return null;
		int M = img.length; 	// height
		int N = img[0].length; 	// width;
		int m = ker.length; 	// height
		int n = ker[0].length; 	// width;
		int oM = M - m + 1;
		int oN = N - n + 1;
		byte[][] o = new byte[oM][oN];
		// Going through every pixel in the output image.
		for(int i = 0; i < oM; i++){
			for(int j = 0; j < oN; j++){
				// Going through every pixel in the kernel.
				for(int k = 0; k < m; k++){
					for(int l = 0; l < n; l++){
						o[i][j] += (byte) (img[i+k][j+l]*ker[k][l]);
					}
				}
			}	
		}
		return o;
	}
	
	public static byte[][] convolution(byte[][] img, int[][] ker){
		if(img == null || ker == null) return null;
		int M = img.length; 	// height
		int N = img[0].length; 	// width;
		int m = ker.length; 	// height
		int n = ker[0].length; 	// width;
		int oM = M - m + 1;
		int oN = N - n + 1;
		byte[][] o = new byte[oM][oN];
		double[][] temp = new double[oM][oN];
		double max = 0;
		double min = 0;
		// Getting sum of all cells in kernel
		double sum = 0;
		for(int k = 0; k < m; k++){
			for(int l = 0; l < n; l++){
				sum += ker[k][l];
			}
		}
		sum = 1/sum;
		// Going through every pixel in the output image.
		for(int i = 0; i < oM; i++){
			for(int j = 0; j < oN; j++){
				// Going through every pixel in the kernel.
				for(int k = 0; k < m; k++){
					for(int l = 0; l < n; l++){
						temp[i][j] += (sum*(img[i+k][j+l] & 0x0FF)*ker[k][l]);
					}
				}
				if(temp[i][j]>max) max = temp[i][j];
				if(temp[i][j]<min) min = temp[i][j];
			}	
		}
		double slope = 255d/(max-min);
		// Going through every pixel in the output image and normalizing.
		for(int i = 0; i < oM; i++){
			for(int j = 0; j < oN; j++){
				o[i][j] = (byte) ((temp[i][j]-min)*slope);
			}	
		}
		return o;
	}

	public static byte[][] convolution(byte[][] img, double[][] ker){
		if(img == null || ker == null) return null;
		int M = img.length; 	// height
		int N = img[0].length; 	// width;
		int m = ker.length; 	// height
		int n = ker[0].length; 	// width;
		int oM = M - m + 1;
		int oN = N - n + 1;
		byte[][] o = new byte[oM][oN];
		// Getting sum of all cells
		float sum = 0;
		for(int k = 0; k < m; k++){
			for(int l = 0; l < n; l++){
				sum += ker[k][l];
			}
		}
		sum = 1/sum;
		// Going through every pixel in the output image.
		for(int i = 0; i < oM; i++){
			for(int j = 0; j < oN; j++){
				// Going through every pixel in the kernel.
				for(int k = 0; k < m; k++){
					for(int l = 0; l < n; l++){
						o[i][j] += ((img[i+k][j+l] & 0x0FF)*ker[k][l]*sum);
					}
				}
			}	
		}
		return o;
	}
	
	public static double[][] getMeanKernel(int h, int w){
		double[][] d = new double[h][w];
		double p = 1/(double)(h*w);
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				d[y][x] = p;
			}
		}
		return d;
	}
	
	public static double[][] getGaussianKernel(int h, int w, double s){
		double[][] d = new double[h][w];
		double cx = w/2+1;
		double cy = h/2+1;
		double dividend = 0;
		double p0 = 2d*Math.pow(s, 2);
		double p1 = p0*Math.PI;
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				d[y][x] = Math.pow(Math.E, -(Math.pow(x-cx, 2)+Math.pow(y-cy, 2))/p0)/p1;
			}
		}
		double scaling = 1/(d[0][0]);
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				d[y][x] = d[y][x]*scaling;
				dividend += d[y][x];
			}
		}
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				d[y][x] = d[y][x]/dividend;
			}
		}
		return d;
	}

	public static int[][] getLaplacianOfGaussianKernel(int h, int w, double s){
		int[][] o = new int[h][w];
		double[][] d = new double[h][w];
		double cx = (int)(w/2);
		double cy = (int)(h/2);
		double t1 = -1d/(Math.PI*Math.pow(s, 4));
		double t2 = 2d*Math.pow(s,2);
		double t3 = 0;
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				t3 = (Math.pow(x-cx, 2) + Math.pow(y-cy, 2))/t2;
				d[y][x] = t1*(1d-t3)*Math.pow(Math.E, -t3);
			}
		}
		double scaling = 1/(d[0][0]);
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				o[y][x] = (int) (d[y][x]*scaling);
			}
		}		
		return o;
	}
	
	public static byte[][] invertGrayScale(byte[][] img){
		int h = img.length;
		int w = img[0].length;
		byte[][] o = new byte[h][w];
		for(int y = 0; y<h; y++)
			for(int x = 0; x<w; x++)
				o[y][x] = (byte) (255 - img[y][x]); 
		return o;
	}
	
	public static byte[][] changeBrightness(byte[][] img, float b){
		int h = img.length;
		int w = img[0].length;
		byte[][] o = new byte[h][w];
		for(int y = 0; y<h; y++)
			for(int x = 0; x<w; x++){
				float p = (img[y][x] & 0x0FF)*(1+b);
				if(p>255) p = 255;
				if(p<0) p = 0;
				o[y][x] = (byte) (p); 
			}
		return o;
	}
	
	public static byte[][] changeContrast(byte[][] img, float b){
		int h = img.length;
		int w = img[0].length;
		byte[][] o = new byte[h][w];
		for(int y = 0; y<h; y++)
			for(int x = 0; x<w; x++){
				float p = (img[y][x] & 0x0FF);
				if(p>127) p *= (1+b);
				else p *= (1-b);
				if(p>255) p = 255;
				if(p<0) p = 0;
				o[y][x] = (byte) (p); 
			}
		return o;
	}
	
	public static byte[][] getBinary(byte[][] img, int th){
		int h = img.length;
		int w = img[0].length;
		byte[][] o = new byte[h][w];
		for(int y = 0; y<h; y++)
			for(int x = 0; x<w; x++){
				if((img[y][x] & 0x0FF)>=th)
					o[y][x] = 0;
				else
					o[y][x] = (byte)255; 
			}
		return o;
	}


    int[][] h = new int[][]{
    		new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
    		new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
    		new int[]{ 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
    		new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
    		new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}
    		};
    int[][] v = new int[][]{
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1},
    		new int[]{-1,-1, 5,-1,-1}
    		};
}
