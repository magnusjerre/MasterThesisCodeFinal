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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.wireframesketcher.model.Widget;
import com.wireframesketcher.model.WidgetGroup;

import application.Constants;

public class AssignmentHandler {
	
	public static void assignValues(Parent root, String xmiFileLocation) {
		
		DataUtils utils = DataUtils.getInstance();
		
		URI exisitingInstanceUri = URI.createFileURI(xmiFileLocation);
		Resource instanceResource = utils.dataResource.getResourceSet().getResource(exisitingInstanceUri, true);
		EObject screenDataInstanceRoot = instanceResource.getContents().get(0);
		
		@SuppressWarnings("unchecked")
		EList<EObject> allAssignments = (EList<EObject>) screenDataInstanceRoot.eGet(utils.dAllAssignmentsFeature);
		for (EObject eObject : allAssignments) {
			
			int layoutId = (int) eObject.eGet(utils.aLayoutIDFeature);
			String id = "#" + layoutId;
			
			if (isAssignmentPlain(eObject)) {
				handlePlainAssignment(root, eObject, id);
			} else if (isAssignmentUsingType(eObject) && !isAssignmentPartOfType(eObject)) {
				handleAssignmentUsingType(root, eObject, id);
			}
			
		}
		
	}
	
	private static void handlePlainAssignment(Parent root, EObject eObject, String id) {
		
		DataUtils utils = DataUtils.getInstance();
		
		EObject rootContextForAssignment = (EObject) eObject.eGet(utils.aRootContextFeature);
		if (rootContextForAssignment != null) {	//Temporary solution, means that the assignment is not part of a type
			String locationOfRootXmi = (String) rootContextForAssignment.eGet(utils.cStatementFeature);
			EObject dataInstance = DataUtils.getRootObjectForXmi(locationOfRootXmi);
			
			Object result = OCLHandler.parseOCLStatement(
					utils.dataResource.getResourceSet(), 
					dataInstance, 
					(String) eObject.eGet(utils.aStatementFeature));
			
			handleResultCorrectly(root, id, result);
		}
		
	}
	
	private static void handleResultCorrectly(Parent root, String id, Object result) {
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
			File imageFile = fileLocation(fileName);
			String uri = imageFile.toURI().toString();
			((ImageView) node).setImage(new Image(uri));
		}
	}
	
	private static String getStringRepresentation(Object object) {
		
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
	
	private static File fileLocation(String fileName) {
		String[] possibleLocations = new String[] {
				 Constants.PROJECT_DIR + Constants.SUB_PROJECT_NAME + "/images/" + fileName,
				 Constants.PROJECT_DIR + Constants.SUB_PROJECT_NAME + "/" + fileName,
				 Constants.FXML_DIRECTORY + fileName
		};
		
		for (int i = 0; i < possibleLocations.length; i++) {
			File imageFile = new File(possibleLocations[i]);
			if (imageFile.exists())
				return imageFile;
		}
		
		throw new RuntimeException(String.format("File %s not found", fileName));
		
		
	}

	private static boolean isAssignmentPlain(EObject eObject) {
		
		if (isAssignmentPartOfType(eObject)) {
			return false;
		}
		
		if (isAssignmentUsingType(eObject)) {
			return false;
		}
		
		return true;
		
	}
	
	private static boolean isAssignmentUsingType(EObject eObject) {
		
		EObject usesType = (EObject) eObject.eGet(DataUtils.getInstance().aUseType);
		return usesType != null;
		
	}
	
	private static boolean isAssignmentPartOfType(EObject eObject) {
		
		EObject partOfType = (EObject) eObject.eGet(DataUtils.getInstance().aPartOf);
		return partOfType != null;
		
	}
	
	private static void handleAssignmentUsingType(Parent root, EObject assignment, String id) {
		
		DataUtils utils = DataUtils.getInstance();
		
		Widget assignmentWidget = (Widget) assignment.eGet(utils.aWidgetFeature);
		
		EObject type = (EObject) assignment.eGet(utils.aUseType);
		Widget typeWidget = (Widget) type.eGet(utils.tWidgetFeature);
		
		//Start with the simple case were there is no type in type
		
		EList<EObject> typeAssignments = (EList<EObject>) type.eGet(utils.tAssignmentsFeature);
		for (EObject typeAssignment : typeAssignments) {
			
			Widget tAWidget = (Widget) typeAssignment.eGet(utils.aWidgetFeature);
			
			int fxmlLayoutId = getCorrespondingId(tAWidget, typeWidget, assignmentWidget);

			String statementFromTypeAssignment = (String) typeAssignment.eGet(utils.aStatementFeature);
			String entireStatement = (String) assignment.eGet(utils.aStatementFeature) + statementFromTypeAssignment;
			
			EObject rootContextForAssignment = (EObject) assignment.eGet(utils.aRootContextFeature);
			if (rootContextForAssignment != null) {	//Temporary solution, means that the assignment is not part of a type
				String locationOfRootXmi = (String) rootContextForAssignment.eGet(utils.cStatementFeature);
				EObject dataInstance = DataUtils.getRootObjectForXmi(locationOfRootXmi);
				
				Object result = OCLHandler.parseOCLStatement(
						utils.dataResource.getResourceSet(), 
						dataInstance, 
						entireStatement);
			
				handleResultCorrectly(root, "#" + fxmlLayoutId, result);
			}
			
		}
		
		
		
		
	}
	
	private static int getCorrespondingId(Widget tAWidget, Widget typeWidget, Widget assignmentWidget) {
		
		WidgetGroup typeWidgetGroup = (WidgetGroup) typeWidget;
		
		int counter = 0, numberInLine = -1;
		
		for (Widget w : typeWidgetGroup.getWidgets()) {
			
			if (tAWidget.equals(w)) {
				numberInLine = counter;
			} 
			
			counter++;
			
		}
		
		Widget correctWidget = null;
		WidgetGroup assignmentWidgetGroup = (WidgetGroup) assignmentWidget;
		
		for (int i = 0; i < assignmentWidgetGroup.getWidgets().size(); i++) {
			
			if (i == numberInLine) {
				correctWidget = assignmentWidgetGroup.getWidgets().get(i);
			}
			
		}
		
		if (correctWidget == null) {
			throw new RuntimeException("Whoa! Somehow didn't find the correct widget for the type");
		}
		
		
		return correctWidget.getId().intValue();
		
	}
	
	
}
