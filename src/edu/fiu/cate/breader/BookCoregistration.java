package edu.fiu.cate.breader;

import image.tools.ITools;
import image.tools.IViewer;
import img.ImageManipulation;

public class BookCoregistration {
	
	String drive = "";
	
	private BookCoregistration(){
		checkOS();
		byte[][] temp = ITools.toGrayscale(ImageManipulation.loadImage(drive + "School/Research/BookReader/3-17-17-trials/IMG_1769.JPG"));
		byte[][] img  = xMirror(ImageManipulation.loadImage(drive + "School/Research/BookReader/3-17-17-trials/amp631.tiff")[0]);
		
		float hScale = (float)temp.length/(float)img.length;
		float wScale = (float)temp[0].length/(float)img[0].length;
		float scale = hScale;
		if(wScale<scale) scale = wScale;
//		byte[][] imgRez = copyToSize(resize(img, scale), temp.length, temp[0].length);
		byte[][] tempRez = resize(temp, 1f/scale);
		
//		System.out.println(normCrossCorr(imageToDoubleArray(temp), imageToDoubleArray(imgRez)));
		
//		new IViewer("Template", ImageManipulation.getGrayBufferedImage(temp));
		new IViewer("TemplateRez", ImageManipulation.getGrayBufferedImage(tempRez));
//		new IViewer("Template", ImageManipulation.getGrayBufferedImage(resize(temp, 1f/25.0f)));
		new IViewer("Corr", ImageManipulation.getGrayBufferedImage(img));
//		new IViewer("Copied2Size", ImageManipulation.getGrayBufferedImage(imgRez));
//		new IViewer("CorrRez", ImageManipulation.getGrayBufferedImage(resize(img, 2.0f)));

//		coregister(temp, imgRez);
		coregister(tempRez, img);
//		new IViewer("Correlation", ImageManipulation.getGrayBufferedImage(ITools.normalize(coregister(temp, imgRez))));
		
	}
	
	public void checkOS(){
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("win")){
			drive = "M:/";
		}else if(os.contains("nix")||os.contains("nux")||os.contains("aix")){
			drive = "/media/DATA/";
		}
	}
	
	public float[][] coregister(byte[][] template, byte[][] img){
		int[] tempDim = new int[]{template.length, template[0].length};
		int[] imgDim  = new int[]{img.length, img[0].length};
		int[] imgCent = new int[]{imgDim[0]/2, imgDim[1]/2};
		float[][] out = new float[tempDim[0]][tempDim[1]];
//		double[] tempArray = imageToDoubleArray(template);
		float maxCorr = 0;
		int maxCorrShift = 0;
		for(int y=0; y<imgDim[0]; y++){
			int yShift = imgCent[0]-y;
//			for(int x=1300; x<1700; x++){
				int x=imgCent[1];
				int xShift = x-imgCent[1];
//				out[y][x] = (float) normCrossCorr( tempArray, imageToDoubleArray(shiftImage(img, xShift, yShift)));
				out[y][x] = (float) normCrossCorr( template, shiftImage(img, xShift, yShift));
				if(out[y][x]>maxCorr){
					maxCorr = out[y][x];
					maxCorrShift = yShift;
				}
				System.out.print(out[y][x]+":\t"+yShift);
//			}
			System.out.print("\n");
		}
		System.out.println("Max corr: "+maxCorr+", "+maxCorrShift);
		new IViewer("MaxCorr", ImageManipulation.getGrayBufferedImage(shiftImage(img, 0, maxCorrShift)));
		img = shiftImage(img, 0, maxCorrShift);
		float maxRFactor = 0;
		for(float rFactor=0.0f; rFactor<1.0f; rFactor+=0.025){
//			float corr = (float) normCrossCorr( tempArray, imageToDoubleArray(resizeAndCrop(img, 1+rFactor, template.length, template[0].length)));
			float corr = (float) normCrossCorr( template, resizeAndCrop(img, 1+rFactor, template.length, template[0].length));
			if(corr>maxCorr){
				maxCorr = corr;
				maxRFactor = rFactor;
			}
			System.out.print(corr+":\t"+rFactor);
			System.out.print("\n");
		}
		new IViewer("MaxCorrRez", ImageManipulation.getGrayBufferedImage(resizeAndCrop(img, 1+maxRFactor, template.length, template[0].length)));
		System.out.println("Max zoom: "+maxCorr+", "+maxRFactor);
		return out;
	}
	
	public byte[][] xMirror(byte[][] img){
		int[] imgDim  = new int[]{img.length, img[0].length};
		byte[][] out = new byte[imgDim[0]][imgDim[1]];
		for(int y=0; y<imgDim[0]; y++){
			for(int x=0; x<imgDim[1]; x++){
				out[y][x]=img[y][imgDim[1]-1-x];
			}
		}
		return out;
	}
	
	public double normCrossCorr(double[] f, double[] t){
		double out = 0;
		double meanF=0, meanT=0, stdF=0, stdT=0;
		double mean2F=0, mean2T=0;
		for(int i=0; i<f.length; i++){
			meanF  += f[i];
			mean2F += Math.pow(f[i], 2);
			meanT  += t[i];
			mean2T += Math.pow(t[i], 2);
		}
		meanF*=1.0/(double)f.length;
		meanT*=1.0/(double)t.length;
		mean2F*=1.0/(double)f.length;
		mean2T*=1.0/(double)t.length;
		stdF=mean2F-Math.pow(meanF, 2);
		stdT=mean2T-Math.pow(meanT, 2);
		for(int i=0; i<f.length; i++){
			out+=(f[i]-meanF)*(t[i]-meanT);
		}
		return out/(stdT*stdF*(double)f.length);
	}
	
	public double normCrossCorr(byte[][] f, byte[][] t){
		double out = 0;
		double meanF=0, meanT=0, stdF=0, stdT=0;
		double mean2F=0, mean2T=0;
		int h = f.length, w = f[0].length;  
		double s = h*w;
		for(int y=0; y<h; y++){
			for(int x=0; x<w; x++){
				meanF  += f[y][x]&0x0FF;
				mean2F += Math.pow(f[y][x]&0x0FF, 2);
				meanT  += t[y][x]&0x0FF;
				mean2T += Math.pow(t[y][x]&0x0FF, 2);
			}
		}
		meanF*=1.0/s;
		meanT*=1.0/s;
		mean2F*=1.0/s;
		mean2T*=1.0/s;
		stdF=mean2F-Math.pow(meanF, 2);
		stdT=mean2T-Math.pow(meanT, 2);
		for(int y=0; y<h; y++){
			for(int x=0; x<w; x++){
				out+=((double)(f[y][x]&0x0FF)-meanF)*((double)(t[y][x]&0x0FF)-meanT);
			}
		}
		return out/(stdT*stdF*s);
	}
	
	public byte[][] resize(byte[][] img, float scale){
		int[] imgDim  = new int[]{img.length, img[0].length};
		int[] outDim  = new int[]{(int) (imgDim[0]*scale), (int) (imgDim[1]*scale)};
		int[] imgCent = new int[]{imgDim[0]/2, imgDim[1]/2};
		int[] outCent = new int[]{outDim[0]/2, outDim[1]/2};
		byte[][] out = new byte[outDim[0]][outDim[1]];
		float oScale = 1.0f/scale;		
		if(scale>1.0){ //Expand
			// linear interpolation
			for (int h = 0; h < outDim[0]; h++) {
				for (int w = 0; w < outDim[1]; w++) {
					// retrieving from copy
					float oX = (float) (imgCent[1] - ((outCent[1] - w) * oScale));
					float oY = (float) (imgCent[0] - ((outCent[0] - h) * oScale));
					int x = (int) oX;
					int y = (int) oY;
					float xRem = oX % (float) x;
					float yRem = oY % (float) y;
					if (xRem != 0.00 || yRem != 0.00) {
						float d11 = (float) (1f / Math.pow(Math.pow(xRem, 2) + Math.pow(yRem, 2), 0.5f));
						float d12 = (float) (1f / Math.pow(Math.pow(1 - xRem, 2) + Math.pow(yRem, 2), 0.5f));
						float d21 = (float) (1f / Math.pow(Math.pow(xRem, 2) + Math.pow(1 - yRem, 2), 0.5f));
						float d22 = (float) (1f / Math.pow(Math.pow(1 - xRem, 2) + Math.pow(1 - yRem, 2), 0.5f));
						float dt = d11 + d12 + d21 + d22;
						if (y > 0 && y + 1 < imgDim[0] && x > 0 && x + 1 < imgDim[1]) {
							out[h][w] = (byte) ((d11 * (float) (img[y][x] & 0x0FF)
									+ d12 * (float) (img[y][x + 1] & 0x0FF) + d21 * (float) (img[y + 1][x] & 0x0FF)
									+ d22 * (float) (img[y + 1][x + 1] & 0x0FF)) / dt);
						}
					} else {
						if ((y > 0) && (y < imgDim[0]) && (x > 0) && (x < imgDim[1])) {
							out[h][w] = img[y][x];
						}
					}
				}
			}
		}else{//Shrink
			int[][] counts 	= new int[outDim[0]][outDim[1]];
			int[][] sum 	= new int[outDim[0]][outDim[1]];
			for (int h = 0; h < imgDim[0]; h++) {
				for (int w = 0; w < imgDim[1]; w++) {
					int x = (int) (outCent[1] - ((imgCent[1] - w) * scale));
					int y = (int) (outCent[0] - ((imgCent[0] - h) * scale));
					if(x>0 && x<outDim[1] && y>0 && y<outDim[0]){
						counts[y][x]++;
						sum[y][x]+=img[h][w]&0x0FF;
					}
				}
			}
			for (int h = 0; h < outDim[0]; h++) {
				for (int w = 0; w < outDim[1]; w++) {
					if(counts[h][w]!=0)
						out[h][w] = (byte) (sum[h][w]/counts[h][w]);
				}
			}
		}
		return out;
	}

	public byte[][] resizeAndCrop(byte[][] img, float scale, int height, int width){
		int[] imgDim  = new int[]{img.length, img[0].length};
		int[] outDim  = new int[]{height, width};
		int[] imgCent = new int[]{imgDim[0]/2, imgDim[1]/2};
		int[] outCent = new int[]{outDim[0]/2, outDim[1]/2};
		byte[][] out = new byte[outDim[0]][outDim[1]];
		float oScale = 1.0f/scale;		
		//linear interpolation
		 for(int h = 0; h < outDim[0]; h++){
		    	for(int w = 0; w < outDim[1]; w++){
		    		//retrieving from copy
		    		float oX = (float) (imgCent[1] - ((outCent[1]-w)*oScale));
		    		float oY = (float) (imgCent[0] - ((outCent[0]-h)*oScale));
		    		int x = (int) oX;
		    		int y = (int) oY;
		    		float xRem = oX%(float)x;
		    		float yRem = oY%(float)y;
		    		if(xRem != 0.00 || yRem != 0.00 ){
		    			float d11 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(yRem, 2), 0.5f));
		    			float d12 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(yRem, 2), 0.5f));
		    			float d21 = (float) (1f/Math.pow(Math.pow(xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
		    			float d22 = (float) (1f/Math.pow(Math.pow(1-xRem, 2)+Math.pow(1-yRem, 2), 0.5f));
		    			float dt = d11 + d12 + d21 + d22;
		    			if(y>0 && y+1<imgDim[0] && x>0 && x+1<imgDim[1]){
		    				out[h][w] = (byte) ((d11*(float)(img[y][x] & 0x0FF)+d12*(float)(img[y][x+1] & 0x0FF)+d21*(float)(img[y+1][x] & 0x0FF)+d22*(float)(img[y+1][x+1] & 0x0FF))/dt);	
		    			}
		    		}else{
			    		if((y>0) && (y<imgDim[0]) && (x>0) && (x<imgDim[1])){
			    			out[h][w] = img[y][x];
			    		}
		    		}
		    	}
		    }
		return out;
	}
	
	public byte[][] copyToSize(byte[][] img, int h, int w){
		int[] imgDim  = new int[]{img.length, img[0].length};
		int[] imgCent = new int[]{imgDim[0]/2, imgDim[1]/2};
		int[] outCent = new int[]{h/2, w/2};
		byte[][] out = new byte[h][w];
		for(int y=0; y<h; y++){
    		int oY = (imgCent[0] - ((outCent[0]-y)));
			for(int x=0; x<w; x++){
				int oX = (imgCent[1] - ((outCent[1]-x)));
				if(oY>0 && oY<imgDim[0] && oX>0 && oX<imgDim[1]){
	    			out[y][x] = img[oY][oX];
	    		}
			}
		}
		return out;
	}
	
	public byte[][] copyToSizeAndShift(byte[][] img, int h, int w, int xShift, int yShift){
		int[] imgDim  = new int[]{img.length, img[0].length};
		int[] imgCent = new int[]{imgDim[0]/2, imgDim[1]/2};
		int[] outCent = new int[]{h/2+yShift, w/2+xShift};
		byte[][] out = new byte[h][w];
		for(int y=0; y<h; y++){
    		int oY = (imgCent[0] - ((outCent[0]-y)));
			for(int x=0; x<w; x++){
				int oX = (imgCent[1] - ((outCent[1]-x)));
				if(oY>0 && oY<imgDim[0] && oX>0 && oX<imgDim[1]){
	    			out[y][x] = img[oY][oX];
	    		}
			}
		}
		return out;
	}
	
	public byte[][] shiftImage(byte[][] img, int xShift, int yShift){
		int[] imgDim  = new int[]{img.length, img[0].length};
		int[] imgCent = new int[]{imgDim[0]/2, imgDim[1]/2};
		int[] outCent = new int[]{imgCent[0]+yShift, imgCent[1]+xShift};
		byte[][] out = new byte[imgDim[0]][imgDim[1]];
		for(int y=0; y<imgDim[0]; y++){
    		int oY = (imgCent[0] - ((outCent[0]-y)));
			for(int x=0; x<imgDim[1]; x++){
				int oX = (imgCent[1] - ((outCent[1]-x)));
				if(oY>0 && oY<imgDim[0] && oX>0 && oX<imgDim[1]){
	    			out[y][x] = img[oY][oX];
	    		}
			}
		}
		return out;
	}

	public <T> T[][] shiftImage(T[][] img, int xShift, int yShift){
		int[] imgDim  = new int[]{img.length, img[0].length};
		int[] imgCent = new int[]{imgDim[0]/2, imgDim[1]/2};
		int[] outCent = new int[]{imgCent[0]+yShift, imgCent[1]+xShift};
		@SuppressWarnings("unchecked")
		T[][] out = (T[][])java.lang.reflect.Array.newInstance(img.getClass(), imgDim[0], imgDim[1]);
		for(int y=0; y<imgDim[0]; y++){
    		int oY = (imgCent[0] - ((outCent[0]-y)));
			for(int x=0; x<imgDim[1]; x++){
				int oX = (imgCent[1] - ((outCent[1]-x)));
				if(oY>0 && oY<imgDim[0] && oX>0 && oX<imgDim[1]){
	    			out[y][x] = img[oY][oX];
	    		}
			}
		}
		return out;
	}
	
	public double[] imageToDoubleArray(byte[][] img){
		int[] s = new int[]{img.length, img[0].length};
		double[] out = new double[s[0]*s[1]];
		int c = 0;
		for (int y = 0; y < s[0]; y++) {
			for (int x = 0; x < s[1]; x++) {
				out[c++] = img[y][x] & 0x0FF;
			}
		}
		return out;
	}
	
	public static void main(String[] args) {
		new BookCoregistration();
	}

}
