package application

import generator.LayoutStyle

/** Project and directory path constants */
abstract class Constants {
		
	/** The directory of the wireframesketcher storyboard/screen files. Must have a trailing slash */
	public static final val PROJECT_DIR = "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/wireframing-tutorial/"
	/**  Allows several subproject/apps per projectDir. Will probably be removed. */
	public static final val SUB_PROJECT_NAME = "movieapp" //"toduka" // "muniapp"//  
	/** JavaFX project directory (dictates where FXMLBuilder saves the fxml files.  Must have a trailing slash */
	public static final val FXML_DIRECTORY = "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/application/" 
//	public static final val GENERATED_DIRECTORY = "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/generated/"
	/** Directory containing the definition of the Data model used for storing the Context, Assignment and Type annotations, as well as containing class for using them*/
	public static final val DATAGENERATOR_DIRECTORY = "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/datagenerator/"
	/** If enabled adds visual grid lines if LayoutStyle is GridPane */
	public static final val enableDebugLines = false
	/** Dev only. Dictates the javafx layout style. This should be handled by the generator. */
	public static final val layoutStyle = LayoutStyle.AnchorPane 
	public static final val LIST_DIRECTORY = "/list/"
	public static final val GEN_DIRECTORY = "/generated/"
	
}