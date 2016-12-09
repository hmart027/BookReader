package edu.fiu.cate.paper2;

import pdfreader.PDFReader;
import diff.utils.JDTTest;

public class CompareOCRResults {
	
	private String bookPath = "/School/Research/BookReader/Joint Paper/book/SHM_Part1";
	private String bookName = "/SHM_";
	private String pagePath = "/School/Research/BookReader/Joint Paper/J2 test images/ABBYY/";
	private String oName	= "CutRGBImJ2SHM";
	private String fName	= "CutFlatRGBImJ2Int2Dx3CubSHM";
	private String feName	= "CutFlatRGBImInt2Dx3CubwLinextJ2SHM";
	private String feNatName= "CutFlatRGBImInt2Dx3CubwNatextJ2SHM";
	private String feNeaName= "CutFlatRGBImInt2Dx3CubwNeaextJ2SHM";
	private String reff;
	private String page;
	int reff_char_count = 0;
	
	CompareOCRResults(){
		System.out.println("Hi");
		setDrive();
		int refDoc = 1;
		int maxRefDoc = 147;	//147
		int skip = 16;

//		for(int p = 1; p < 148; p++){
//			reff = readTxtFile(bookPath+bookName+getDocString(p)+".txt");
//			System.out.println(reff_char_count);
//		}
		
		int ref = 74;
		System.out.println("\no");
		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/038_o.pdf");
//		System.out.println("\no_f");
//		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/034_o_f.pdf");
//		System.out.println("\no_f_e_luis");
//		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/034_o_f_e_luis.pdf");
//		System.out.println("\no_f_e_u_luis");
//		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/034_o_f_e_u_luis.pdf");
//		System.out.println("\no_f_e_u_lu");
//		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/034_o_f_e_u_lu.pdf");
//		System.out.println("\no_f_e_lu");
//		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/034_o_f_e_lu.pdf");
//
//		System.out.println("\nr");
//		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/034_r.pdf");
//		System.out.println("\nr_f");
//		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/034_r_f.pdf");
//		System.out.println("\nr_f_e_luis");
//		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/074_r_f_e_luis.pdf");
//		System.out.println("\nr_f_e_u_luis");
//		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/074_r_f_e_u_luis.pdf");
//		System.out.println("\nr_f_e_u_lu");
//		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/013_r_f_e_u_lu.pdf");
//		System.out.println("\nr_f_e_lu");
//		doDocument(ref, "/media/harold/DATA_USB/J2 test images/ABBYY/018_r_f_e_lu.pdf");
		
////		doFlatExtendedLu(40, 76);
//		
////		doRedos();
//		
//		for (int img = 1; img <= 75; img++){
//			if(img==skip) continue;
//			if(refDoc>maxRefDoc) continue;
//			int c = 0;
////			System.out.println("\nImg "+img+": ");
////			System.out.println("\nOriginal: "+img);
////			c = doOriginal(img, refDoc);
//				
////			System.out.println("Flat: ");
////			c = doFlat(img, refDoc);
//			
////			System.out.println("\nFlat and Extended: "+img);
//			c = doFlatExtended(img, refDoc);
//			
////			c = doFlatExtendedNat(img, refDoc);
////			c = doFlatExtendedNea(img, refDoc);
//			
////			c = doFlatExtendedLuRaw(img, refDoc);
//			
////			c = doRotated(img, refDoc);
//			
//			if(img != 1){
//				refDoc+= 2;
//				if(c==0) System.out.println("0\n0");
//				if(c==1) System.out.println("0");
//			}else{
//				refDoc+= 1;
//			}
//		}
	}
	
	private void setDrive(){
		String OS = System.getProperty("os.name");
		switch(OS.toLowerCase()){
		case "linux":
//			bookPath = "/mnt/8ACC1B44CC1B2A49" + bookPath;
//			pagePath = "/mnt/8ACC1B44CC1B2A49" + pagePath;
			bookPath = "/media/DATA" + bookPath;
			pagePath = "/media/DATA" + pagePath;
			break;
		case "windows":
			bookPath = "C:" + bookPath;
			pagePath = "C:" + pagePath;
			break;
		}
	}
	
	public int doOriginal(int doc, int refDoc){
		PDFReader reader = new PDFReader(pagePath+doc+"/"+oName+getDocString(doc)+".pdf");
		int pageCount = reader.getPageCount();
		int e_count = 0;
		for(int p = 0; p < pageCount; p++){
			reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
			page = reader.parsePage(p+1);
			e_count = JDTTest.getDeltas(reff, page);
//			System.out.println("Error Count: " + e_count);
//			System.out.println("Original Char Count: "+ reff_char_count);
			System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
		}
		return pageCount;
	}
	
	public int doFlat(int doc, int refDoc){
		PDFReader reader = new PDFReader(pagePath+doc+"f/"+fName+getDocString(doc)+".pdf");
		int pageCount = reader.getPageCount();
		int e_count = 0;
		for(int p = 0; p < pageCount; p++){
			reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
			page = reader.parsePage(p+1);
			e_count = JDTTest.getDeltas(reff, page);
			System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
		}
		return pageCount;
	}
	
	public int doFlatExtended(int doc, int refDoc){
//		PDFReader reader = new PDFReader(pagePath+doc+"fe/"+feName+getDocString(doc)+".pdf");
		PDFReader reader = new PDFReader(pagePath+"luis_"+getDocString(doc)+"_fe_u.pdf");
		int pageCount = reader.getPageCount();
		int e_count = 0;
		for(int p = 0; p < pageCount; p++){
			reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
			page = reader.parsePage(p+1);
			e_count = JDTTest.getDeltas(reff, page);
//			System.out.println("Error Count: " + e_count);
//			System.out.println("Original Char Count: "+ reff_char_count);
			System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
		}
		return pageCount;
	}
	
	public int doFlatExtendedNat(int doc, int refDoc){
		PDFReader reader = new PDFReader(pagePath+doc+"fenat/"+feNatName+getDocString(doc)+".pdf");
		int pageCount = reader.getPageCount();
		int e_count = 0;
		for(int p = 0; p < pageCount; p++){
			reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
			page = reader.parsePage(p+1);
			e_count = JDTTest.getDeltas(reff, page);
//			System.out.println("Error Count: " + e_count);
//			System.out.println("Original Char Count: "+ reff_char_count);
			System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
		}
		return pageCount;
	}
	
	public int doFlatExtendedNea(int doc, int refDoc){
		PDFReader reader = new PDFReader(pagePath+doc+"fenea/"+feNeaName+getDocString(doc)+".pdf");
		int pageCount = reader.getPageCount();
		int e_count = 0;
		for(int p = 0; p < pageCount; p++){
			reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
			page = reader.parsePage(p+1);
			e_count = JDTTest.getDeltas(reff, page);
//			System.out.println("Error Count: " + e_count);
//			System.out.println("Original Char Count: "+ reff_char_count);
			System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
		}
		return pageCount;
	}
	
	public int doFlatExtendedLu(int doc, int refDoc){
		PDFReader reader = new PDFReader(pagePath+doc+"feLu/lu_"+getDocString(doc)+".pdf");
		int pageCount = reader.getPageCount();
		int e_count = 0;
		for(int p = 0; p < pageCount; p++){
			reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
			page = reader.parsePage(p+1);
			e_count = JDTTest.getDeltas(reff, page);
			System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
		}
		return pageCount;
	}
	
	public int doFlatExtendedLuRaw(int doc, int refDoc){
		PDFReader reader = new PDFReader(pagePath+doc+"feLu/lu_"+getDocString(doc)+"_raw.pdf");
		int pageCount = reader.getPageCount();
		int e_count = 0;
		for(int p = 0; p < pageCount; p++){
			reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
			page = reader.parsePage(p+1);
			e_count = JDTTest.getDeltas(reff, page);
			System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
		}
		return pageCount;
	}
	
	public int doRotated(int doc, int refDoc){
		PDFReader reader = new PDFReader(pagePath+getDocString(doc)+"_r_f_e_u_lu.pdf");
		int pageCount = reader.getPageCount();
		int e_count = 0;
		for(int p = 0; p < pageCount; p++){
			reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
			page = reader.parsePage(p+1);
			e_count = JDTTest.getDeltas(reff, page);
			System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
		}
		return pageCount;
	}
	
	int[][] redos = new int[][]{
			new int[]{ 1, 2, 3, 5, 6, 8,10,12,13,14,15,16,17,19,21,23,24,25,26,27,28,29,
					30,32,36,37,38,41,42,43,44,46,47,48,50,54,55,56,57,59,61,62,63,64,66,67,68,69,70,71,72,73},	//original
			new int[]{10,11,13,14,15,16,17,18,19,24,25,26,30,38,40,42,57,65}		//original rotated
	};
	
	public void doRedos(){
		int refDoc = 0;
		
		int doc = 74;
		System.out.println("\t"+doc);
		refDoc = 146;
		if(refDoc==0)refDoc = 1;
		PDFReader reader = new PDFReader(pagePath+"redo/"+getDocString(doc)+"_r_f_e_lu.pdf");
		int pageCount = reader.getPageCount();
		int e_count = 0;
		for(int p = 0; p < pageCount; p++){
			reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
			page = reader.parsePage(p+1);
			e_count = JDTTest.getDeltas(reff, page);
			System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
		}
		
//		for(int doc: redos[0]){
//			System.out.println("\t"+doc);
//			refDoc = 2*(doc-1);
//			if(refDoc==0)refDoc = 1;
//			PDFReader reader = new PDFReader(pagePath+"original/"+getDocString(doc)+".pdf");
//			int pageCount = reader.getPageCount();
//			int e_count = 0;
//			for(int p = 0; p < pageCount; p++){
//				reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
//				page = reader.parsePage(p+1);
//				e_count = JDTTest.getDeltas(reff, page);
//				System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
//			}
//		}
//		System.out.println("Rotated");
//		for(int doc: redos[1]){
//			System.out.println("\t"+doc);
//			refDoc = 2*(doc-1);
//			PDFReader reader = new PDFReader(pagePath+"original/"+getDocString(doc)+"r.pdf");
//			int pageCount = reader.getPageCount();
//			int e_count = 0;
//			for(int p = 0; p < pageCount; p++){
//				reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
//				page = reader.parsePage(p+1);
//				e_count = JDTTest.getDeltas(reff, page);
//				System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
//			}
//		}
	}

	public void doDocument(int refDoc, String docPath){
		if(refDoc==0)refDoc = 1;
		PDFReader reader = new PDFReader(docPath);
		int pageCount = reader.getPageCount();
		int e_count = 0;
		for(int p = 0; p < pageCount; p++){
			reff = readTxtFile(bookPath+bookName+getDocString(refDoc+p)+".txt");
			page = reader.parsePage(p+1);
			e_count = JDTTest.getDeltas(reff, page);
			
//			System.out.println("Ref: ");
//			System.out.println(reff);
//			System.out.println("Page: ");
//			System.out.println(page);
			
//			System.out.println("E count: "+e_count+", REF: "+reff_char_count);
			System.out.println((1-(float)e_count/(float)reff_char_count)*100.0);
		}
	}
	
	public String getDocString(int doc){
		String out = "";
		if(doc<10) out += "00";
		else if(doc<100) out += "0";
		out += doc;
		return out;
	}
	
	public String readTxtFile(String path){
		String out = "";
		java.io.File f = new java.io.File(path);
		reff_char_count = 0;
		try {
			java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(f)));
			while(in.ready()){
				String l = in.readLine();
				reff_char_count += l.length();
				out += l+"\n";
			}
			in.close();
//			reff_char_count = out.length();
			return out;
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return out;
	}

	public static void main(String[] args) {
		new CompareOCRResults();
	}
	
}
