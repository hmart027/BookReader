package edu.fiu.cate.breader.abbyy;

import com.abbyy.FREngine.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ABBYY {
	
	private final String DllFolder = "/opt/ABBYY/FREngine11/Bin";
	private final String DeveloperSN = "SWAT11011006344135873665";

	private IEngine engine = null;
	
	private ABBYY() {}
	
	public static ABBYY getABBYY() {
		ABBYY abbyy = new ABBYY();
		try {
			// Load ABBYY FineReader Engine
			abbyy.loadEngine();
			abbyy.setupFREngine();
			System.out.println("Engine Loaded");
			
			return abbyy;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void loadEngine() throws Exception {
		System.out.println( "Initializing Engine..." );
		engine = Engine.GetEngineObject( DllFolder, DeveloperSN);
	}

	private void setupFREngine() {
		System.out.println( "Loading predefined profile..." );
		engine.LoadPredefinedProfile( "TextExtraction_Accuracy" );
	}
	
	public String processImage(String imagePath, String txtPath) {
		String txt = processImage(imagePath);
		try {
			PrintWriter out = new PrintWriter(txtPath);
			out.print(txt);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return txt;
	}
	
	public String processImage(String imagePath) {

		String txt = "";
		try {
			// Create document
			IFRDocument document = engine.CreateFRDocument();

			try {
				// Add image file to document
				System.out.println( "Loading image..." );
				document.AddImageFile( imagePath, null, null );

				// Process document
				System.out.println( "Process..." );
				document.Process( null );
				
				IImageModification mod = engine.CreateImageModification();
				
				txt = document.getPlainText().getText();
												
			} finally {
				// Close document
				document.Close();
			}
			return txt;
		} catch( Exception ex ) {
			System.out.println( ex.getMessage() );
		}
		return null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		// Unload ABBYY FineReader Engine
		unloadEngine();
		super.finalize();
	}

	private void unloadEngine() throws Exception {
		System.out.println( "Deinitializing Engine..." );
		engine = null;
		Engine.DeinitializeEngine();
	}

}
