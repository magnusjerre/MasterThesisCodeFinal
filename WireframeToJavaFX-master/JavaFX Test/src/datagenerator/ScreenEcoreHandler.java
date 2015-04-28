package datagenerator;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
	public EObject instance;
	
	public ScreenEcoreHandler(String fileName) {
		
		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		URI uri = URI.createFileURI(fileName);
		Resource firstScreen = resourceSet.getResource(uri, true);
		ePackage = (EPackage) firstScreen.getContents().get(0);
		
		EFactory factory = ePackage.getEFactoryInstance();
		EClass firstScreenClass = (EClass) ePackage.getEClassifier("firstscreen");
		instance = factory.create(firstScreenClass);
		
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
				instance.eSet(feature, result);
			}
			
		}
		
	}
	
	public void assignValues(Parent root) {
		
		for (EStructuralFeature feature : instance.eClass().getEStructuralFeatures()) {
			
			EAnnotation annotation = feature.getEAnnotation("wireframe");
			
			String id = annotation.getDetails().get("layoutId");
			if (id != null) {
				handleResultCorrectly(root, id, instance.eGet(feature));
			}
			
		}
		
	}
	
	private void handleResultCorrectly(Parent root, String id, Object result) {
		Node node = root.lookup(id);
		
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
