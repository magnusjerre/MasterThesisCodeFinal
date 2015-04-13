package datagenerator;

import javafx.scene.Parent;
import javafx.scene.control.Label;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

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
			String query = "#" + layoutId;
			
			
			EObject rootContextForAssignment = (EObject) eObject.eGet(utils.aRootContextFeature);
			String locationOfRootXmi = (String) rootContextForAssignment.eGet(utils.cStatementFeature);
			EObject dataInstance = DataUtils.getRootObjectForXmi(locationOfRootXmi);
			
			String result = (String) OCLHandler.parseOCLStatement(
					utils.dataResource.getResourceSet(), 
					dataInstance, 
					(String) eObject.eGet(utils.aStatementFeature));
			
			Label fxmlNode = (Label) root.lookup(query);
			fxmlNode.setText(result);
			
		}
		
	}

}
