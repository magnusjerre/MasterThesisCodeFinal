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

import application.Constants;

public class XMIExporter {
	
	AssignmentGenerator assignmentGenerator;
	ContextGenerator contextGenerator;
	TypeGenerator typeGenerator;
	
	//EMF
	ResourceSet resourceSet;
	Resource ecoreResource;
	EPackageImpl dataPackage;
	EFactory dataFactory;
	EClass dataForScreenClass;
	EStructuralFeature allContextsFeature, allAssignmentsFeature, allTypesFeature;
	
	public XMIExporter() {
		
		String pathToEcore = Constants.DATAGENERATOR_DIRECTORY + "Data.ecore";
		
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
		allTypesFeature = dataForScreenClass.getEStructuralFeature("allTypes");
		
	}
	
	public void setGenerators(AssignmentGenerator ag, ContextGenerator cg, TypeGenerator tg) {
		assignmentGenerator = ag;
		contextGenerator = cg;
		typeGenerator = tg;
	}
	
	public void exportXMI(String filename, String absFolderLocation) {
		
		if (contextGenerator == null) throw new NullPointerException("Context generator not set for XMIExporter.");
		if (assignmentGenerator == null) throw new NullPointerException("Assignment generator not set for XMIExporter.");
		if (typeGenerator == null) throw new NullPointerException("Type generator not set for XMIExporter.");

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
		for (EObject eObject : assignmentGenerator.assignments.getElementsIterable()) {
			allAssignments.add(eObject);
		}
		
		@SuppressWarnings("unchecked")
		EList<EObject> allTypes = (EList<EObject>) newContextsForScreen.eGet(allTypesFeature);
		for (EObject eObject : typeGenerator.list.getElementsIterable()) {
			allTypes.add(eObject);
		}
		
		//Create the xmi and fill with the new instance
		filename = filename.trim();
		if (!filename.endsWith(".xmi"))
			filename = filename + ".xmi";
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
