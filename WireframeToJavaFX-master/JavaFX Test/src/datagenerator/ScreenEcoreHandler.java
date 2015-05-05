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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import org.eclipse.emf.common.util.EMap;
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

public class ScreenEcoreHandler {
	
	public ResourceSet resourceSet;
	public EPackage ePackage;
	public EFactory factory;
	public EObject instance;
	public String fileName;
	
	public static final String ANNOTATION_SOURCE = "wireframe";
	
	public ScreenEcoreHandler(String fileName) {
		
		this.fileName = fileName;
		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		URI uri = URI.createFileURI(fileName);
		Resource firstScreen = resourceSet.getResource(uri, true);
		ePackage = (EPackage) firstScreen.getContents().get(0);
		
		factory = ePackage.getEFactoryInstance();
		EClass screenClass = (EClass) ePackage.getEClassifiers().get(0);
		instance = factory.create(screenClass);

		populateInstance();
		
	}

	private void populateInstance() {
		for (EStructuralFeature feature : instance.eClass().getEStructuralFeatures()) {
			
			EAnnotation annotation = feature.getEAnnotation("wireframe");
			
			String location = annotation.getDetails().get("xmiLocation");
			if (location != null) {
				Resource res = resourceSet.getResource(URI.createFileURI("/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/datagenerator/moviedb.xmi"), true);
				EObject database = res.getContents().get(0);
				instance.eSet(feature, database);
			} 
		
			String ocl = annotation.getDetails().get("ocl");
			if (ocl != null) {
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
	
	public void assignComponents(Node node, EObject instance, EStructuralFeature feature) {
		
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
			
			EObject instanceFromFeature = (EObject) instance.eGet(feature);

			if (!instanceTypeMatchesComponentExpectedType(instanceFromFeature.eClass(), iUseComponent)) {
				throw new RuntimeException(String.format("Error! The type from the assignment doesn't match the expected type for the component %s", iUseComponent));
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
				String componentFxmlLocation = getFxmlLocationForComponent(iUseComponent);
				URL url = new File(componentFxmlLocation).toURI().toURL();
				Node componentNode = FXMLLoader.load(url);
				
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
	
	private boolean instanceTypeMatchesComponentExpectedType(EClass instanceClass, String comopnentNamed) {
		
		EClass compClass = (EClass) ePackage.getEClassifier(comopnentNamed);
		String expectedTypeName = compClass.getEAnnotations().get(0).getDetails().get("expectedType");
		
		if (expectedTypeName == null) {
			return true;
		}
		
		if (instanceClass.getName().equals(expectedTypeName)) {
			return true;
		}
		
		return false;
		
	}

	private String getFxmlLocationForComponent(String iUseComponent) {
		File componentFxmlFile = new File(fileName);
		String screenName = componentFxmlFile.getName().replace(".ecore", "");
		String componentFxmlLocation = String.format("%s%s-%s.fxml", Constants.GENERATED_DIRECTORY, screenName, iUseComponent);
		return componentFxmlLocation;
	}
	
	private void handleResultCorrectly(Node node, Object result) {
		
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
		}
		
	}
	
	private String getStringRepresentation(Object object) {
		
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
	
	private File fileLocation(String fileName) {
		String[] possibleLocations = new String[] {
				 Constants.PROJECT_DIR + Constants.SUB_PROJECT_NAME + "/images/" + fileName,
				 Constants.PROJECT_DIR + Constants.SUB_PROJECT_NAME + "/" + fileName,
				 Constants.GENERATED_DIRECTORY + fileName
		};
		
		for (int i = 0; i < possibleLocations.length; i++) {
			File imageFile = new File(possibleLocations[i]);
			if (imageFile.exists())
				return imageFile;
		}
		
		throw new RuntimeException(String.format("File %s not found", fileName));
		
		
	}

}
