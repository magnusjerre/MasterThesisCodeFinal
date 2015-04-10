package datagenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class XMIExporter {
	
	AssignmentGenerator assignmentGenerator;
	ContextGenerator contextGenerator;
	
	//EMF
	ResourceSet resourceSet;
	Resource ecoreResource;
	EPackageImpl dataPackage;
	EFactory dataFactory;
	EClass dataForScreenClass;
	EStructuralFeature allContextsFeature, allAssignmentsFeature;
	
	public XMIExporter() {
		
		String pathToEcore = "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/datagenerator/Data.ecore";
		
		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		URI existingInstanceUri = URI.createFileURI(pathToEcore);
		
		ecoreResource = resourceSet.getResource(existingInstanceUri, true);
		dataPackage = (EPackageImpl)ecoreResource.getContents().get(0);
		dataFactory = dataPackage.getEFactoryInstance();
		
		dataForScreenClass = (EClass) dataPackage.getEClassifier("DataForScreen");
		allContextsFeature = dataForScreenClass.getEStructuralFeature("allContexts");
		allAssignmentsFeature = dataForScreenClass.getEStructuralFeature("allAssignments");
		
	}
	
	public void setGenerators(AssignmentGenerator ag, ContextGenerator cg) {
		assignmentGenerator = ag;
		contextGenerator = cg;
	}
	
	public void exportXMI(String filename, String absFolderLocation) {
		
		if (contextGenerator == null) throw new NullPointerException("Context generator not set for XMIExporter.");
		if (assignmentGenerator == null) throw new NullPointerException("Assignment generator not set for XMIExporter.");

		//Create and fill the new instance with the contexts
		EObject newContextsForScreen = dataFactory.create(dataForScreenClass);
		
		@SuppressWarnings("unchecked")	//Defined as an EList in Data.ecore by me, therefore safe to suppress warning
		EList<EObject> allContexts = (EList<EObject>) newContextsForScreen.eGet(allContextsFeature);
		for (EObject eObject : contextGenerator.rootContexts) {
			allContexts.add(eObject);
		}
		
		for (EObject eObject : contextGenerator.contexts) {
			allContexts.add(eObject);
		}
		
		@SuppressWarnings("unchecked")	//Defined as an EList in Data.ecore by me, therefore safe to suppress warning
		EList<EObject> allAssignments = (EList<EObject>) newContextsForScreen.eGet(allAssignmentsFeature);
		for (EObject eObject : assignmentGenerator.assignments) {
			allAssignments.add(eObject);
		}
		
		//Create the xmi and fill with the new instance
		URI newInstanceUri = URI.createFileURI(absFolderLocation + filename);
		
		Resource newInstanceResource = resourceSet.createResource(newInstanceUri);
		newInstanceResource.getContents().add(newContextsForScreen);
		
		try {
			
			Map<String, Boolean> options = new HashMap<String, Boolean>();
			options.put(XMIResource.OPTION_SCHEMA_LOCATION, true);
			newInstanceResource.save(options);
			
		} catch (IOException e) {

			e.printStackTrace();
			
		}
		
	}

}
