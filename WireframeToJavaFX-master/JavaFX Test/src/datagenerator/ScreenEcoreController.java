package datagenerator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import list.ListController;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import application.Constants;
import application.LocationUtils;

/**
 * This class is responsible for populating a specific screen with the data it's supposed to display. First it will
 * go through each field for the screen class and load the data into the fields. After all the fields have been 
 * populated with data, each assignment will assign its value to its view elements in the JavaFX program.
 * 
 * @author Magnus Jerre
 *
 */
public class ScreenEcoreController {
	
	public static ResourceSet resourceSet;
	public static EPackage ePackage;
	public static EFactory factory;
	public static EObject instance;
//	public static String fileName;
	public static String fileNameSimple;
	
	public static final String ANNOTATION_SOURCE = "wireframe";
	
	public ScreenEcoreController(String fileName) {
		
//		this.fileName = fileName;
		fileNameSimple = fileName;
		if (resourceSet == null) {
			resourceSet = new ResourceSetImpl();
			resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
			
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		}
		
//		URI uri = URI.createFileURI(fileName);
		URI uri = URI.createFileURI(LocationUtils.getFilePathSrc(LocationUtils.GENERATED_PACKAGE, fileName + ".ecore"));
		Resource firstScreen = resourceSet.getResource(uri, true);
		ePackage = (EPackage) firstScreen.getContents().get(0);
		
		factory = ePackage.getEFactoryInstance();
		EClass screenClass = (EClass) ePackage.getEClassifiers().get(0);
		instance = factory.create(screenClass);

		populateInstance();
		
	}

	/**
	 * Populates the instance with the data that each field specify in their annotations.
	 * 
	 * If the annotations contain a detail named xmiLocation, the xmi file specified by the detail
	 * must be loaded.
	 * 
	 * If the annotations contain a detail named ocl, the statement specified by the detail will
	 * be executed on the instance.
	 */
	private void populateInstance() {
		for (EStructuralFeature feature : instance.eClass().getEStructuralFeatures()) {
			
			EAnnotation annotation = feature.getEAnnotation("wireframe");
			
			//Load and populate the instance with the xmi if the feature is specified to do so
			String location = annotation.getDetails().get("xmiLocation");
			if (location != null) {
				Resource res = resourceSet.getResource(URI.createFileURI(location), true);
				EObject database = res.getContents().get(0);
				if (database instanceof EPackage) {
					EObject selectionInstance = SelectionController.getInstance().selectionInstance;
					instance.eSet(feature, selectionInstance);
					System.out.println("featureName: " + feature.getName() + ", value: " + instance.eGet(feature));
				} else {
					instance.eSet(feature, database);
				}
			} 
		
			//Populate the instance with the value from the context's or assignment's ocl-statement
			String ocl = annotation.getDetails().get("ocl");
			if (ocl != null) {
				EObject theInstance = instance;
				Object result = OCLHandler.parseOCLStatement(resourceSet, instance, ocl);
				if (feature.isMany()) {
					@SuppressWarnings("unchecked")
					Collection<Object> coll = (Collection<Object>) instance.eGet(feature);
					@SuppressWarnings("unchecked")
					Collection<Object> resColl = (Collection<Object>) result;
					coll.addAll(resColl);
				} else {
					instance.eSet(feature, result);
				}
			}
			
		}
	}
	
	public void assignValues(Parent root) {

		for (EStructuralFeature feature : instance.eClass().getEStructuralFeatures()) {
			assignComponents(root, instance, feature);
		}
		
	}
	
	/**
	 * This method assigns the value for each assignment field to the correct JavaFX view element.
	 * 
	 * It works in a recursive manner. The base case is when the assignment doesn't use a ViewComponent.
	 * A call to handleResultCorrectly is made, and the value is assigned to the node given to 
	 * handleResultCorrectly.
	 * 
	 * If the base case is not yet applicable some more work must be done. The first is to check whether
	 * the assignment is dealing with a list or not. If it is, a ListView controller is assigned to the
	 * ListView and the data is added to the ListView. Otherwise, it is not a list, just an assignment 
	 * that uses a ViewComponent.
	 * 
	 * When the assignment uses a ViewComponent, the ViewComponent's EClass is used to create a ViewComponent
	 * instance containing the data for each of its Assignments. After the ViewComponent has been populated
	 * with data, its fxml file is loaded and added as a child to its parent. A recursive call to 
	 * assignComponents is then made.
	 * 
	 * @param node
	 * @param instance
	 * @param feature
	 */
	public static void assignComponents(Node node, EObject instance, EStructuralFeature feature) {
		
		EAnnotation iAnnotation = feature.getEAnnotation("wireframe");
		
		String iLayoutId = iAnnotation.getDetails().get("layoutId");
		if (iLayoutId == null) {
			return;
		}
		
		String iUseComponent = iAnnotation.getDetails().get("useComponent");
		if (iUseComponent == null) {
			Node finalNode = node.lookup(iLayoutId);
			handleResultCorrectly(finalNode, instance.eGet(feature));
		} else {
			
			if (feature.isMany()) {
				EClass iUseComponentClass = (EClass) ePackage.getEClassifier(iUseComponent);
				
//				String simpleFileName = getSimpleName(fileName);
				String simpleFileName = fileNameSimple;
				String componentName = String.format("%s-%s.fxml", simpleFileName, iUseComponent);
				
				@SuppressWarnings("unchecked")
				List<Object> listValues = (List<Object>) instance.eGet(feature);
				ListController<Object> lc = new ListController<>(node.lookup(iLayoutId), componentName, iUseComponentClass);
				lc.obsList.addAll(listValues);
				
			} else {
				
				EObject instanceFromFeature = (EObject) instance.eGet(feature);
				
				if (!instanceDataTypeMatchesComponentExpectedDataType(instanceFromFeature.eClass(), iUseComponent)) {
					throw new RuntimeException(String.format("Error! The data type from the assignment doesn't match the expected data type for the component %s", iUseComponent));
				}
				
				//Create and populate the component used 
				EClass iUseComponentClass = (EClass) ePackage.getEClassifier(iUseComponent);
				EObject componentInstance = factory.create(iUseComponentClass);
				for (EStructuralFeature iuccFeature : componentInstance.eClass().getEStructuralFeatures()) {
					
					EAnnotation iuccAnnotation = iuccFeature.getEAnnotation("wireframe");
					String iuccOcl = iuccAnnotation.getDetails().get("ocl");
					Object result = OCLHandler.parseOCLStatement(resourceSet, instanceFromFeature, iuccOcl);
					componentInstance.eSet(iuccFeature, result);
					
				}
				
				try {
					//Load fxml for ViewComponent
//					String componentFxmlLocation = getFxmlLocationForComponent(iUseComponent);
					String componentFxmlLocation = LocationUtils.getFilePathSrc(LocationUtils.GENERATED_PACKAGE, fileNameSimple + "-" + iUseComponent + ".fxml");
					//String.format("%s%s-%s.fxml", Constants.GENERATED_DIRECTORY, screenName, iUseComponent);
					URL url = new File(componentFxmlLocation).toURI().toURL();
					Node componentNode = FXMLLoader.load(url);
					
					//Assign values for the ViewComponent to the different JavaFX view elements
					for (EStructuralFeature cFeature : componentInstance.eClass().getEStructuralFeatures()) {
						
						String cLayoutId = cFeature.getEAnnotation("wireframe").getDetails().get("layoutId");
						Node subComponentNode = componentNode.lookup(cLayoutId);
						
						assignComponents(subComponentNode, componentInstance, cFeature);
						
					}
					
					//The node provided in the method is the parent node for the group/anchorpane node we want to add the child to
					Node nodeToAddTo = node.lookup(feature.getEAnnotation("wireframe").getDetails().get("layoutId"));
					
					if (nodeToAddTo instanceof Group) {
						((Group) nodeToAddTo).getChildren().add(componentNode);
					} else if (nodeToAddTo instanceof Pane) {
						((Pane) nodeToAddTo).getChildren().add(componentNode);
					}
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	
//	private static String getSimpleName(String fileLocation) {
//		
//		String removedPath = fileLocation.replace(Constants.GENERATED_DIRECTORY, "");
//		String removedFileEnding = removedPath.replace(".ecore", "");
//		return removedFileEnding;
//				
//	}
	
	private static boolean instanceDataTypeMatchesComponentExpectedDataType(EClass instanceClass, String comopnentNamed) {
		
		EClass compClass = (EClass) ePackage.getEClassifier(comopnentNamed);
		String expectedType = compClass.getEAnnotations().get(0).getDetails().get("expectedType");
		
		if (instanceClass.getName().equals(expectedType)) {
			return true;
		}
		
		return false;
		
	}

//	private static String getFxmlLocationForComponent(String iUseComponent) {
//		File componentFxmlFile = new File(fileName);
//		String screenName = componentFxmlFile.getName().replace(".ecore", "");
//		String componentFxmlLocation = String.format("%s%s-%s.fxml", Constants.GENERATED_DIRECTORY, screenName, iUseComponent);
//		return componentFxmlLocation;
//	}
	
	/**
	 * This method assigns the value from the result parameter to the JavaFX view element in the node parameter.
	 * 
	 * This method will only work when the result should not use a ViewComponent. 
	 * @param node
	 * @param result
	 */
	public static void handleResultCorrectly(Node node, Object result) {
		
		if (node instanceof Label) {
			((Label) node).setText(getStringRepresentation(result));
		} else if (node instanceof TextInputControl) {
			if (node instanceof TextArea) {
				((TextArea) node).setWrapText(true);
			}
			((TextInputControl) node).setText(getStringRepresentation(result));
		} else if (node instanceof ImageView) {
			String fileName = result.toString().substring(result.toString().lastIndexOf("/") + 1, result.toString().length());
			if (fileName.startsWith("[")) {
				fileName = fileName.substring(1);
			}
			if (fileName.endsWith("]")) {
				fileName = fileName.substring(0, fileName.length() - 1);
			}
			File imageFile = fileLocation(fileName);
			String uri = imageFile.toURI().toString();
			((ImageView) node).setImage(new Image(uri));
		} else if (node instanceof ListView) {
			ListController<String> lc = new ListController<>(node, "simple_strings.fxml", null);
			lc.obsList.addAll((Collection<String>) result);
		}
		
	}
	
	private static String getStringRepresentation(Object object) {
		
		if (object instanceof Collection){
			Collection<Object> coll = (Collection<Object>) object;
			int currentPos = 0;
			StringBuilder out = new StringBuilder();
			for (Object o : coll) {
				out.append(o.toString());
				currentPos++;
				if (currentPos < coll.size()) {
					out.append(", ");
				}
			}
			return out.toString();
		}
		
		if (object instanceof String) {
			return object.toString();
		}
		
		if (object instanceof Integer) {
			return Integer.toString((Integer) object);
		}
		
		if (object instanceof Double) {
			return Double.toString((Double) object);
		}
		
		if (object instanceof Date) {
			DateFormat df = new SimpleDateFormat();
			return df.format((Date) object);
		}
		
		return null;
	}
	
	/**
	 * Returns the location for the file named fileName
	 * @param fileName
	 * @return
	 */
	private static File fileLocation(String fileName) {
		String[] possibleLocations = new String[] {
				LocationUtils.getWireframeProjectFolder(Constants.SUB_PROJECT_NAME) + "images/" + fileName,
				LocationUtils.getWireframeProjectFolder(Constants.SUB_PROJECT_NAME) + fileName,
				LocationUtils.getFilePathSrc(LocationUtils.GENERATED_PACKAGE, fileName)
		};
		
		for (int i = 0; i < possibleLocations.length; i++) {
			File imageFile = new File(possibleLocations[i]);
			if (imageFile.exists())
				return imageFile;
		}
		
		throw new RuntimeException(String.format("File %s not found", fileName));
		
		
	}

}
