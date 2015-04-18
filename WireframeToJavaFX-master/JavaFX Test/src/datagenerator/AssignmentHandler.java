package datagenerator;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

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

import application.Constants;

import com.wireframesketcher.model.Widget;

public class AssignmentHandler {
	
	private static DataUtils utils = DataUtils.getInstance();
	
	public static void assignValues(Parent root, String xmiFileLocation) {
		
		URI exisitingInstanceUri = URI.createFileURI(xmiFileLocation);
		Resource instanceResource = utils.dataResource.getResourceSet().getResource(exisitingInstanceUri, true);
		EObject screenDataInstanceRoot = instanceResource.getContents().get(0);
		
		@SuppressWarnings("unchecked")
		EList<EObject> allAssignments = (EList<EObject>) screenDataInstanceRoot.eGet(utils.dAllAssignment2sFeature);
		for (EObject assignment : allAssignments) {
			
			if (isAssignmentPlain(assignment)) {
				handlePlainAssignment(root, assignment);
			} else if (isAssignmentUsingType(assignment) && !isAssignmentPartOfType(assignment)) {
				handleAssignmentUsingComponent(root, assignment);
			}
			
		}
		
	}
	
	private static void handlePlainAssignment(Parent root, EObject assignment) {
		
		@SuppressWarnings("unchecked")
		EList<EObject> dataInAssignment = (EList<EObject>) assignment.eGet(utils.a2DataFeature);
		
		if (isMany((EList<EObject>) dataInAssignment)) {
			throw new RuntimeException("Something is wrong here, no list implementation for lists exists yet.");
		}
		
		EObject theElement = (EObject) getFirstObjectInCollection(dataInAssignment);
		
		if (isJavaObjectContainerClass(theElement)) {
			Object value = theElement.eGet(utils.jocObjectFeature);
			value = getFirstObjectInCollection((Collection<?>) value);
			String id = getJavaFXIdFromWidgetId(assignment);
			handleResultCorrectly(root, id, value);
		} else {
			throw new RuntimeException("Something is wrong here. An EObject cannot be directly assigned without using some sort of component");
		}
		
	}

	private static String getJavaFXIdFromWidgetId(EObject assignment) {
		
		Widget widget = (Widget) assignment.eGet(utils.a2WidgetFeature);
		int layoutId = widget.getId().intValue();
		String id = "#" + layoutId;
		return id;
		
	}
	
	private static Object getFirstObjectInCollection(Collection<?> objects) {
		
		Iterator<?> iterator = objects.iterator();
		if (iterator.hasNext()) {
			return iterator.next();
		}
		
		return null;
		
	}
	
	private static boolean isJavaObjectContainerClass(EObject eObject) {
		
		return eObject.eClass().equals(utils.javaObjectContainerClass);
		
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
		
		EObject usesType = (EObject) eObject.eGet(utils.a2UseComponentFeature);
		return usesType != null;
		
	}
	
	private static boolean isAssignmentPartOfType(EObject eObject) {
		
		EObject partOfType = (EObject) eObject.eGet(utils.a2PartOfComponentFeature);
		return partOfType != null;
		
	}
	
	private static void handleAssignmentUsingComponent(Parent root, EObject mainAssignment) {
		
		Widget mainAssignmentWidget = (Widget) mainAssignment.eGet(utils.a2WidgetFeature);
		handleRecursively(root, mainAssignment, null, mainAssignmentWidget);
		
	}
	
	private static String modifyCompstatementToFitList(String compStatement) {
		
		return "data->" + compStatement;
		
	}

	private static void handleRecursively(Parent root, EObject parentAssignment, Object prevResult, Widget corrWidgetInMainAssignment) {
		
		if (isAssignmentUsingType(parentAssignment)) {
			
			Widget mainAssignmentWidget = (Widget) parentAssignment.eGet(utils.a2WidgetFeature);
			@SuppressWarnings("unchecked")
			EList<EObject> data = (EList<EObject>) parentAssignment.eGet(utils.a2DataFeature);
			if (isMany(data)) {	//Lists are not supported yet
				EObject componentForMainAssignment = (EObject) parentAssignment.eGet(utils.a2UseComponentFeature);
				Widget componentWidget = (Widget) componentForMainAssignment.eGet(utils.tWidgetFeature);
				
				@SuppressWarnings("unchecked")
				EList<EObject> assignmentsForType = (EList<EObject>) componentForMainAssignment.eGet(utils.tAssignmentsFeature);
				for (EObject compAssignment : assignmentsForType) {
					
					Widget saWidget = (Widget) compAssignment.eGet(utils.a2WidgetFeature);
					Widget corrWidget = WidgetUtils.getCorrespondingWidget(saWidget, componentWidget, mainAssignmentWidget);
					
					String compStatement = (String) compAssignment.eGet(utils.a2StatementFeature);
					compStatement = modifyCompstatementToFitList(compStatement);
					Object result = OCLHandler.parseOCLStatement(parentAssignment.eResource().getResourceSet(), parentAssignment, compStatement);
					
					handleRecursively(root, compAssignment, result, corrWidget);
					
				}
			} else {
				
				EObject componentForMainAssignment = (EObject) parentAssignment.eGet(utils.a2UseComponentFeature);
				Widget componentWidget = (Widget) componentForMainAssignment.eGet(utils.tWidgetFeature);
				
				@SuppressWarnings("unchecked")
				EList<EObject> assignmentsForType = (EList<EObject>) componentForMainAssignment.eGet(utils.tAssignmentsFeature);
				for (EObject compAssignment : assignmentsForType) {
					
					Widget saWidget = (Widget) compAssignment.eGet(utils.a2WidgetFeature);
					Widget corrWidget  = WidgetUtils.getCorrespondingWidget(saWidget, componentWidget, corrWidgetInMainAssignment);
					
					String compStatement = (String) compAssignment.eGet(utils.a2StatementFeature);
					EObject dataValue = (EObject) getFirstObjectInCollection(data);
					
					Object result = null;
					if (prevResult == null) {
						result = OCLHandler.parseOCLStatement(dataValue.eResource().getResourceSet(), dataValue, compStatement);
					} else {
						EObject temp = (EObject) prevResult;
						result = OCLHandler.parseOCLStatement(temp.eResource().getResourceSet(), temp, compStatement);
					}
					
					handleRecursively(root, compAssignment, result, corrWidget);
					
				}
			}
			
			
		} else {	//Base case
			
			String layoutId = "#" + corrWidgetInMainAssignment.getId().intValue();
			handleResultCorrectly(root, layoutId, prevResult);
			
		}
		
	}
	
	private static boolean isMany(EList<EObject> list) {
		
		if (list.size() > 1) {
			return true;
		}
		
		return false;
		
	}
	
}
