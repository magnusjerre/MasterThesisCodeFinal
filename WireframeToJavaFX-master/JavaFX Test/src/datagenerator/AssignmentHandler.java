package datagenerator;

import java.io.File;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import application.Constants;

public class AssignmentHandler {
	
	public static void AssignValues(Parent root, String xmiFileLocation) {
		
		DataUtils utils = DataUtils.getInstance();
		
		URI exisitingInstanceUri = URI.createFileURI(xmiFileLocation);
		Resource instanceResource = utils.dataResource.getResourceSet().getResource(exisitingInstanceUri, true);
		EObject screenDataInstanceRoot = instanceResource.getContents().get(0);
		
		@SuppressWarnings("unchecked")
		EList<EObject> allAssignments = (EList<EObject>) screenDataInstanceRoot.eGet(utils.dAllAssignmentsFeature);
		for (EObject eObject : allAssignments) {
			
			int layoutId = (int) eObject.eGet(utils.aLayoutIDFeature);
			String id = "#" + layoutId;
			
			
			EObject rootContextForAssignment = (EObject) eObject.eGet(utils.aRootContextFeature);
			String locationOfRootXmi = (String) rootContextForAssignment.eGet(utils.cStatementFeature);
			EObject dataInstance = DataUtils.getRootObjectForXmi(locationOfRootXmi);
			
			Object result = (String) OCLHandler.parseOCLStatement(
					utils.dataResource.getResourceSet(), 
					dataInstance, 
					(String) eObject.eGet(utils.aStatementFeature));
			
			handleResultCorrectly(root, id, result);
			
		}
		
	}
	
	private static void handleResultCorrectly(Parent root, String id, Object result) {
		Node node = root.lookup(id);
		
		if (node instanceof Label) {
			((Label) node).setText(result.toString());
		} else if (node instanceof TextInputControl) {
			((TextInputControl) node).setText(result.toString());
		} else if (node instanceof ImageView) {
			String fileName = result.toString().substring(result.toString().lastIndexOf("/") + 1, result.toString().length());
			File imageFile = fileLocation(fileName);
			String uri = imageFile.toURI().toString();
			((ImageView) node).setImage(new Image(uri));
		}
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

}
