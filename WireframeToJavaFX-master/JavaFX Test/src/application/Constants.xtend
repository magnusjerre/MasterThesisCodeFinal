package application

import generator.LayoutStyle

/** Project and directory path constants */
abstract class Constants {
		
	/**  Allows several subproject/apps per projectDir. Will probably be removed. */
	public static final val SUB_PROJECT_NAME = "masterexample" //"toduka" // "muniapp"//  
	/** If enabled adds visual grid lines if LayoutStyle is GridPane */
	public static final val enableDebugLines = false
	/** Dev only. Dictates the javafx layout style. This should be handled by the generator. */
	public static final val layoutStyle = LayoutStyle.AnchorPane 
	
}