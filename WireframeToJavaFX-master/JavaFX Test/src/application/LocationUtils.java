package application;

import java.net.URL;

public class LocationUtils {
	
	private static LocationUtils instance = null;
	
	private static final String PROJECT_FOLDER_NAME = "JavaFX Test";
	private static final String WIREFRAME_FOLDER_NAME = "wireframing-tutorial";
	public static final String GENERATED_PACKAGE = "generated";
	public static final String DATAGENERATOR_PACKAGE = "generator";
	public static final String LIST_PACKAGE = "list";
	
	private String projectFolderLocation = null;
	private String srcFolderLocation = null;
	private String wireframeFolderLocation = null;
	
	private LocationUtils() {
		
	}
	
	public static LocationUtils getInstance() {
		
		if (instance == null) {
			instance = new LocationUtils();
			setupPaths(instance);
		}
		
		return instance;
		
	}
	
	public static String getPakcageFolder(String thePackage) {
		
		return getInstance().srcFolderLocation + thePackage + "/";
		
	}
	
	public static String getFilePathSrc(String thePackage, String fileName) {

		return getInstance().srcFolderLocation + thePackage + "/" + fileName;

	}

	private static void setupPaths(LocationUtils theInstance) {

		URL url = getInstance().getClass().getResource("");
		String classPath = url.toString().replace("file:", "").replace("%20", " ");
		int indexOfProjectFolder = classPath.indexOf(PROJECT_FOLDER_NAME);
		theInstance.projectFolderLocation = classPath.substring(0, indexOfProjectFolder) + PROJECT_FOLDER_NAME + "/";
		theInstance.srcFolderLocation = getInstance().projectFolderLocation + "src/";
		theInstance.wireframeFolderLocation = classPath.substring(0, indexOfProjectFolder) + WIREFRAME_FOLDER_NAME + "/";

	}

}
