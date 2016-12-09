package edu.fiu.cate;

import java.io.File;

import freetts.TTS;
import tess4j.TesseractInstance;

public class OCRAndTTS {
	static TTS tts;
	
	public static void main(String[] args){
		File f = new File("pics/cell5.png");
		f = new File("/home/harold/Downloads/20150918_154059.jpg_p_12.jpg");
		String text = TesseractInstance.doOCR(f);
		System.out.println(text);
		tts = new TTS();
//		new T2().start();
		tts.doTTS(text);	// Thread blocking
	}
	
	public static class T2 extends Thread{
		public void run(){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tts.pause();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tts.resume();
		}
	}

}
