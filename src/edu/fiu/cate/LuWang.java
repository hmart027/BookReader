package edu.fiu.cate;

public class LuWang {
	
	public static double[] P = new double[]{0.016, -0.042, 0, 1.026};
	
	public static double f 	= 0.0058;
	public static double Hs = 0.00532;
	public static double Hh = 0.62;
	
	public static double ppm = 2668/0.58;	// pixels per meter

	public static byte[][][] barrelCorrection(byte[][][] img){
		int height = img.length;
		int width = img[0].length;
		byte[][][] nImg = new byte[height][width][3];
		int hc = height/2;
		int wc = width/2;
		double maxR = Math.pow(Math.pow(hc, 2)+Math.pow(wc, 2), 0.5);
		
		double rDist = 0;
		double rCorr = 0;
		double ratio = 0;
		int dH = 0;
		int dW = 0;
		int nH = 0;
		int nW = 0;
				
		for(int h = 0; h < height; h++){
	    	for(int w = 0; w < width; w++){
	    		dH = h-hc;
	    		dW = w-wc;
	    		rCorr = Math.pow(Math.pow(dH, 2)+Math.pow(dW, 2), 0.5)/maxR;
	    		rDist = P[0]*Math.pow(rCorr, 4) + P[1]*Math.pow(rCorr, 3) + P[2]*Math.pow(rCorr, 2) + P[3]*rCorr; 
	    		ratio = rDist/rCorr;
	    		nH = (int) (dH*ratio + hc);
	    		nW = (int) (dW*ratio + wc);
	    		if(nH>=0 && nH<height && nW>=0 && nW<width){
		    		nImg[h][w][0] = img[nH][nW][0];
		    		nImg[h][w][1] = img[nH][nW][1];
		    		nImg[h][w][2] = img[nH][nW][2];
	    		}
	    	}
		}	
		return nImg;
	}

	public static byte[][][] barrelCorrectionWithLinearInterpolation(byte[][][] img){
		int height = img.length;
		int width = img[0].length;
		byte[][][] nImg = new byte[height][width][3];
		int hc = height/2;
		int wc = width/2;
		double maxR = Math.pow(Math.pow(hc, 2)+Math.pow(wc, 2), 0.5);	
		double rDist = 0;
		double rCorr = 0;
		double ratio = 0;
		int dH 	= 0;
		int dW 	= 0;
		float nH 	= 0;
		float nW 	= 0;
		int x 	= 0;
		int y 	= 0;
		float xRem 	= 0;
		float yRem  = 0;
		float d11  	= 0;
		float d12 	= 0;
		float d21 	= 0;
		float d22 	= 0;
		float dt 	= 0;
		for(int h = 0; h < height; h++){
	    	for(int w = 0; w < width; w++){
	    		dH = h-hc;
	    		dW = w-wc;
	    		rCorr = Math.pow(Math.pow(dH, 2)+Math.pow(dW, 2), 0.5)/maxR;
	    		rDist = P[0]*Math.pow(rCorr, 4) + P[1]*Math.pow(rCorr, 3) + P[2]*Math.pow(rCorr, 2) + P[3]*rCorr; 
	    		ratio = rDist/rCorr;
	    		nH = (float) (dH*ratio + hc);
	    		nW = (float) (dW*ratio + wc);
	    		x = (int) nW;
	    		y = (int) nH;
	    		xRem = nW%(float)x;
	    		yRem = nH%(float)y;
	    		if(xRem != 0.00 || yRem != 0.00 ){
	    			d11 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(yRem, 2), 0.5f));
	    			d12 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(yRem, 2), 0.5f));
	    			d21 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
	    			d22 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
	    			dt = d11 + d12 + d21 + d22;
	    			if(y>0 && y+1<height && x>0 && x+1<width){
	    				nImg[h][w][0] = (byte) ((d11*(float)(img[y][x][0] & 0x0FF)+d12*(float)(img[y][x+1][0] & 0x0FF)+d21*(float)(img[y+1][x][0] & 0x0FF)+d22*(float)(img[y+1][x+1][0] & 0x0FF))/dt);
	    				nImg[h][w][1] = (byte) ((d11*(float)(img[y][x][1] & 0x0FF)+d12*(float)(img[y][x+1][1] & 0x0FF)+d21*(float)(img[y+1][x][1] & 0x0FF)+d22*(float)(img[y+1][x+1][1] & 0x0FF))/dt);
	    				nImg[h][w][2] = (byte) ((d11*(float)(img[y][x][2] & 0x0FF)+d12*(float)(img[y][x+1][2] & 0x0FF)+d21*(float)(img[y+1][x][2] & 0x0FF)+d22*(float)(img[y+1][x+1][2] & 0x0FF))/dt);
	    			}
	    		}else{
		    		if(y>0 && y<height && x>0 && x<width){
		    			nImg[h][w][0] = img[y][x][0];
		    			nImg[h][w][1] = img[y][x][1];
		    			nImg[h][w][2] = img[y][x][2];
		    		}
	    		}
	    	}
		}	
		return nImg;
	}

	public static byte[][][] push(byte[][][] img, float[] heights){
		int height 	= img.length;
		int width 	= img[0].length;
		byte[][][] nImg = new byte[height][width][3];
		float[] W = new float[heights.length];
		for(int i = 0; i<W.length; i++){
			W[i] = (float) ((Hh-heights[i]/100f-f)/(Hh-f));
		}
		int nY = 0;
		int cY = height/2;
		for(int h = 0; h < height; h++){
	    	for(int w = 0; w < width; w++){
	    		nY = (int) ((h-cY)/W[w]+cY);
	    		if(nY>0 && nY<height){
	    			nImg[h][w][0] = img[nY][w][0];
	    			nImg[h][w][1] = img[nY][w][1];
	    			nImg[h][w][2] = img[nY][w][2];
	    		}
	    	}
		}
		return nImg;
	}
	
	public static byte[][][] pushWithLinearInterpolation(byte[][][] img, float[] heights){
		int height 	= img.length;
		int width 	= img[0].length;
		byte[][][] nImg = new byte[height][width][3];
		float[] W = new float[heights.length];
		for(int i = 0; i<W.length; i++){
			W[i] = (float) ((Hh-heights[i]/100f-f)/(Hh-f));
		}
		float nH = 0;
		int cH = height/2;
		int x 	= 0;
		int y 	= 0;
		float xRem 	= 0;
		float yRem  = 0;
		float d11  	= 0;
		float d12 	= 0;
		float d21 	= 0;
		float d22 	= 0;
		float dt 	= 0;
		for(int h = 0; h < height; h++){
	    	for(int w = 0; w < width; w++){
	    		nH = ((h-cH)/W[w]+cH);
	    		y = (int) nH;
	    		x = w;
	    		yRem = nH%(float)y;
	    		if(xRem != 0.00 || yRem != 0.00 ){
	    			d11 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(yRem, 2), 0.5f));
	    			d12 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(yRem, 2), 0.5f));
	    			d21 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
	    			d22 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
	    			dt = d11 + d12 + d21 + d22;
	    			if(y>0 && y+1<height && x>0 && x+1<width){
	    				nImg[h][w][0] = (byte) ((d11*(float)(img[y][x][0] & 0x0FF)+d12*(float)(img[y][x+1][0] & 0x0FF)+d21*(float)(img[y+1][x][0] & 0x0FF)+d22*(float)(img[y+1][x+1][0] & 0x0FF))/dt);
	    				nImg[h][w][1] = (byte) ((d11*(float)(img[y][x][1] & 0x0FF)+d12*(float)(img[y][x+1][1] & 0x0FF)+d21*(float)(img[y+1][x][1] & 0x0FF)+d22*(float)(img[y+1][x+1][1] & 0x0FF))/dt);
	    				nImg[h][w][2] = (byte) ((d11*(float)(img[y][x][2] & 0x0FF)+d12*(float)(img[y][x+1][2] & 0x0FF)+d21*(float)(img[y+1][x][2] & 0x0FF)+d22*(float)(img[y+1][x+1][2] & 0x0FF))/dt);
	    			}
	    		}else{
		    		if(y>0 && y<height && x>0 && x<width){
		    			nImg[h][w][0] = img[y][x][0];
		    			nImg[h][w][1] = img[y][x][1];
		    			nImg[h][w][2] = img[y][x][2];
		    		}
	    		}
	    	}
		}
		return nImg;
	}
	
	public static byte[][][] extention(byte[][][] img, float[] heights){
		double[] radii = new double[heights.length];
		double newWidth = 1;
//		double dX = f/(Hh-f); 
		for(int i = 1; i<radii.length; i++){
			radii[i] = (double) (Math.pow(1 + Math.pow((heights[i]-heights[i-1])/(100f)*ppm,2), 0.5));
			newWidth += radii[i];
		}
		int height 	= img.length;
		int width 	= img[0].length;
		byte[][][] nImg = new byte[height][(int)newWidth][3];
		float lX = 0;
		int cPixel = 0;
		float cRad = 0;
		for(int h = 0; h < height; h++){
			cPixel = 1;
			cRad = (float) radii[cPixel];
			lX = cRad;
    		nImg[h][0][0] = img[h][0][0];
    		nImg[h][0][1] = img[h][0][1];
    		nImg[h][0][2] = img[h][0][2];
	    	for(int w = 1; w < (int)newWidth; w++){
	    		if(w > lX){
	    			cPixel++;
	    			cRad = (float) radii[cPixel];
		    		lX += cRad;
	    		}
	    		if(cPixel<width){
		    		nImg[h][w][0] = img[h][cPixel][0];
		    		nImg[h][w][1] = img[h][cPixel][1];
		    		nImg[h][w][2] = img[h][cPixel][2];
	    		}
	    	}
		}
		return nImg;
	}
	
	public static byte[][][] extentionWithLinearInterpolation(byte[][][] img, float[] heights){
		double[] radii = new double[heights.length];
		double newWidth = 1;
//		double dX = f/(Hh-f); 
		for(int i = 1; i<radii.length; i++){
			radii[i] = (double) (Math.pow(1 + Math.pow((heights[i]-heights[i-1])/(100f)*ppm,2), 0.5));
			newWidth += radii[i];
		}
		int height 	= img.length;
		int width 	= img[0].length;
		byte[][][] nImg = new byte[height][(int)newWidth][3];
		float nX = 0;
		float cRad = 0;
		int x 	= 0;
		int y 	= 0;
		float xRem 	= 0;
		float yRem  = 0;
		float d11  	= 0;
		float d12 	= 0;
		float d21 	= 0;
		float d22 	= 0;
		float dt 	= 0;
		for(int h = 0; h < height; h++){
			x = 0;
			cRad = (float) radii[x];
			nX = cRad;
			y = h;
	    	for(int w = 0; w < (int)newWidth; w++){
	    		xRem = nX%(float)w;
	    		if(xRem != 0.00 || yRem != 0.00 ){
	    			d11 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(yRem, 2), 0.5f));
	    			d12 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(yRem, 2), 0.5f));
	    			d21 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
	    			d22 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
	    			dt = d11 + d12 + d21 + d22;
	    			if(y>0 && y+1<height && x>0 && x+1<width){
	    				nImg[h][w][0] = (byte) ((d11*(float)(img[y][x][0] & 0x0FF)+d12*(float)(img[y][x+1][0] & 0x0FF)+d21*(float)(img[y+1][x][0] & 0x0FF)+d22*(float)(img[y+1][x+1][0] & 0x0FF))/dt);
	    				nImg[h][w][1] = (byte) ((d11*(float)(img[y][x][1] & 0x0FF)+d12*(float)(img[y][x+1][1] & 0x0FF)+d21*(float)(img[y+1][x][1] & 0x0FF)+d22*(float)(img[y+1][x+1][1] & 0x0FF))/dt);
	    				nImg[h][w][2] = (byte) ((d11*(float)(img[y][x][2] & 0x0FF)+d12*(float)(img[y][x+1][2] & 0x0FF)+d21*(float)(img[y+1][x][2] & 0x0FF)+d22*(float)(img[y+1][x+1][2] & 0x0FF))/dt);
	    			}
	    		}else{
		    		if(y>0 && y<height && x>0 && x<width){
		    			nImg[h][w][0] = img[y][x][0];
		    			nImg[h][w][1] = img[y][x][1];
		    			nImg[h][w][2] = img[y][x][2];
		    		}
	    		}
	    		if(w > nX){
	    			x++;
	    			cRad = (float) radii[x];
		    		nX += cRad;
	    		}
	    	}
		}
		return nImg;
	}
	
	public static byte[][][] extentionWithLinearInterpolation(byte[][][] img, float[][] heights){
		double[][] radii = new double[heights.length][heights[0].length];
		double newWidth = 1, dW = 0;
//		double dX = f/(Hh-f); 
		for(int y = 0; y<radii.length; y++){
			dW = 1;
			for(int x = 1; x<radii[0].length; x++){
				radii[y][x] = (double) (Math.pow(1 + Math.pow((heights[y][x]-heights[y][x-1])/(100f)*ppm,2), 0.5));
				dW += radii[y][x];
			}
			if(dW>newWidth) newWidth = dW;
		}
		int height 	= img.length;
		int width 	= img[0].length;
		byte[][][] nImg = new byte[height][(int)newWidth][3];
		float nX = 0;
		float cRad = 0;
		int x 	= 0;
		int y 	= 0;
		float xRem 	= 0;
		float yRem  = 0;
		float d11  	= 0;
		float d12 	= 0;
		float d21 	= 0;
		float d22 	= 0;
		float dt 	= 0;
		for(int h = 0; h < height; h++){
			x = 0;
			cRad = (float) radii[h][x];
			nX = cRad;
			y = h;
	    	for(int w = 0; w < (int)newWidth; w++){
    			if(x>=width) continue;
	    		xRem = nX%(float)w;
	    		if(xRem != 0.00 || yRem != 0.00 ){
	    			d11 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(yRem, 2), 0.5f));
	    			d12 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(yRem, 2), 0.5f));
	    			d21 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
	    			d22 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
	    			dt = d11 + d12 + d21 + d22;
	    			if(y>0 && y+1<height && x>0 && x+1<width){
	    				nImg[h][w][0] = (byte) ((d11*(float)(img[y][x][0] & 0x0FF)+d12*(float)(img[y][x+1][0] & 0x0FF)+d21*(float)(img[y+1][x][0] & 0x0FF)+d22*(float)(img[y+1][x+1][0] & 0x0FF))/dt);
	    				nImg[h][w][1] = (byte) ((d11*(float)(img[y][x][1] & 0x0FF)+d12*(float)(img[y][x+1][1] & 0x0FF)+d21*(float)(img[y+1][x][1] & 0x0FF)+d22*(float)(img[y+1][x+1][1] & 0x0FF))/dt);
	    				nImg[h][w][2] = (byte) ((d11*(float)(img[y][x][2] & 0x0FF)+d12*(float)(img[y][x+1][2] & 0x0FF)+d21*(float)(img[y+1][x][2] & 0x0FF)+d22*(float)(img[y+1][x+1][2] & 0x0FF))/dt);
	    			}
	    		}else{
		    		if(y>0 && y<height && x>0 && x<width){
		    			nImg[h][w][0] = img[y][x][0];
		    			nImg[h][w][1] = img[y][x][1];
		    			nImg[h][w][2] = img[y][x][2];
		    		}
	    		}
	    		if(w > nX){
	    			x++;
	    			if(x>=width) continue;
	    			cRad = (float) radii[y][x];
	    			nX += cRad;
	    		}
	    	}
		}
		return nImg;
	}
}
