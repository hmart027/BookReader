package edu.fiu.cate.paper2;

import java.io.File;

public class CreateFolders {

	public static void main(String[] args) {
		String path = "/mnt/8ACC1B44CC1B2A49/School/Research/BookReader/Joint Paper/J2 test images/ABBYY/";
		for(int i = 13; i<76; i++){
			new File(path+i).mkdirs();
			new File(path+i+"f").mkdirs();
			new File(path+i+"fe").mkdirs();
		}
	}

}
