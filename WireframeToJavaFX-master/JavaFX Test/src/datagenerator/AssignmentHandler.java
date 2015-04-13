package datagenerator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import application.Constants;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class AssignmentHandler {
	
	//EMF
	ResourceSet resourceSet;
	Resource ecoreResource;
	EPackageImpl dataPackage;
	EFactory dataFactory;
	EClass contextClass, assignmentClass, dataForScreenClass;
	//Context class features are prefixed with a c
	EStructuralFeature cNameFeature, cContextFeature, cIsRootFeature, cStatementFeature, cSpecificStatementFeature;
	//Assignment class features are prefixed with an s
	EStructuralFeature aStatementFeature, aSpecificStatementFeature, aRootContextFeature, aLayoutIDFeature;
	//DataForScreen class features are prefixed with a d
	EStructuralFeature dAllContextsFeature, dAllAssignmentsFeature;
	
	public AssignmentHandler() {
		
		loadDataEcoreDefinition();
		
	}
	
	public void AssignValues(Parent root, String xmiFileLocation) {
		
		URI exisitingInstanceUri = URI.createFileURI(xmiFileLocation);
		Resource instanceResource = resourceSet.getResource(exisitingInstanceUri, true);
		EObject screenDataInstanceRoot = instanceResource.getContents().get(0);
		
		@SuppressWarnings("unchecked")
		EList<EObject> allAssignments = (EList<EObject>) screenDataInstanceRoot.eGet(dAllAssignmentsFeature);
		for (EObject eObject : allAssignments) {
			
			int layoutId = (int) eObject.eGet(aLayoutIDFeature);
			String query = "#" + layoutId;
			
			
			EObject rootContextForAssignment = (EObject) eObject.eGet(aRootContextFeature);
			String locationOfRootXmi = (String) rootContextForAssignment.eGet(cStatementFeature);
			EObject dataInstance = loadRootObject(locationOfRootXmi);
			
			String result = (String) OCLHandler.parseOCLStatement(
					resourceSet, 
					dataInstance, 
					(String) eObject.eGet(aStatementFeature));
			
			Label fxmlNode = (Label) root.lookup(query);
			fxmlNode.setText(result);
			
		}
		
	}
	
	private void loadDataEcoreDefinition() {
		
		String pathToEcore = Constants.DATAGENERATOR_DIRECTORY + "Data.ecore";
		
		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		URI existingInstanceUri = URI.createFileURI(pathToEcore);
		
		ecoreResource = resourceSet.getResource(existingInstanceUri, true);
		dataPackage = (EPackageImpl)ecoreResource.getContents().get(0);
		dataFactory = dataPackage.getEFactoryInstance();

		contextClass = (EClass)dataPackage.getEClassifier("Context");
		cNameFeature = contextClass.getEStructuralFeature("name");
		cContextFeature = contextClass.getEStructuralFeature("rootContext");
		cIsRootFeature = contextClass.getEStructuralFeature("isRoot");
		cStatementFeature = contextClass.getEStructuralFeature("statement");
		cSpecificStatementFeature = contextClass.getEStructuralFeature("specificStatement");
		
		assignmentClass = (EClass) dataPackage.getEClassifier("Assignment");
		aStatementFeature = assignmentClass.getEStructuralFeature("statement");
		aSpecificStatementFeature = assignmentClass.getEStructuralFeature("specificStatement");
		aRootContextFeature = assignmentClass.getEStructuralFeature("rootContext");
		aLayoutIDFeature = assignmentClass.getEStructuralFeature("layoutID");
		
		dataForScreenClass = (EClass) dataPackage.getEClassifier("DataForScreen");
		dAllContextsFeature = dataForScreenClass.getEStructuralFeature("allContexts");
		dAllAssignmentsFeature = dataForScreenClass.getEStructuralFeature("allAssignments");
		
	}


	private EObject loadRootObject(String xmiFileLocation) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		URI existingInstanceUri = URI.createFileURI(xmiFileLocation);
		Resource instanceResource = resourceSet.getResource(existingInstanceUri, true);	
		
		return instanceResource.getContents().get(0);
	}
	
}
