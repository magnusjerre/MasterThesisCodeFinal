package datagenerator;

import java.util.ArrayList;
import java.util.NoSuchElementException;

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

/**
 * Contains the blueprint for the Data model in Data.ecore. Also contains some helper methods.
 * @author Magnus
 */
public class DataUtils {
	
	private static DataUtils instance = null;
	
	//EMF
	Resource dataResource;
	EPackageImpl dataPackage;
	EFactory dataFactory;
	EClass contextClass, assignmentClass, dataForScreenClass;
	//Context class features are prefixed with a c
	EStructuralFeature cNameFeature, cContextFeature, cIsRootFeature, cStatementFeature, cSpecificStatementFeature;
	//Assignment class features are prefixed with an s
	EStructuralFeature aStatementFeature, aSpecificStatementFeature, aRootContextFeature, aLayoutIDFeature;
	//DataForScreen class features are prefixed with a d
	EStructuralFeature dAllContextsFeature, dAllAssignmentsFeature;
	
	private DataUtils() {
		
		dataResource = loadResource(Constants.DATAGENERATOR_DIRECTORY + "Data.ecore");
		loadDefinitions();
		
	}
	
	private void loadDefinitions() {
		
		//Package
		dataPackage = (EPackageImpl)dataResource.getContents().get(0);
		
		//Factory
		dataFactory = dataPackage.getEFactoryInstance();
		
		//Context
		contextClass = (EClass)dataPackage.getEClassifier("Context");
		cNameFeature = contextClass.getEStructuralFeature("name");
		cContextFeature = contextClass.getEStructuralFeature("rootContext");
		cIsRootFeature = contextClass.getEStructuralFeature("isRoot");
		cStatementFeature = contextClass.getEStructuralFeature("statement");
		cSpecificStatementFeature = contextClass.getEStructuralFeature("specificStatement");
		
		//Assignment
		assignmentClass = (EClass) dataPackage.getEClassifier("Assignment");
		aStatementFeature = assignmentClass.getEStructuralFeature("statement");
		aSpecificStatementFeature = assignmentClass.getEStructuralFeature("specificStatement");
		aRootContextFeature = assignmentClass.getEStructuralFeature("rootContext");
		aLayoutIDFeature = assignmentClass.getEStructuralFeature("layoutID");
		
		//DataForContext
		dataForScreenClass = (EClass) dataPackage.getEClassifier("DataForScreen");
		dAllContextsFeature = dataForScreenClass.getEStructuralFeature("allContexts");
		dAllAssignmentsFeature = dataForScreenClass.getEStructuralFeature("allAssignments");
		
	}
	
	public static DataUtils getInstance() {
		
		if (instance == null)
			instance = new DataUtils();
		
		return instance;
		
	}

	protected static EObject getRootObjectForXmi(String xmiLocation) {
		
		return loadResource(xmiLocation).getContents().get(0);
		
	}
	
	protected EObject getContextNamed(String name, ArrayList<EObject> list) {
		
		for (EObject eObject : list) {
			String eName = (String) eObject.eGet(cNameFeature);
			if (name.equals(eName)) {				
				return eObject;
			}
		}
		
		throw new NoSuchElementException(String.format("Context named \"%s\" doesn't exist. \"%s\" might be misspelled.", name, name));
		
	}
	
	protected static Resource loadResource(String resourceLocation) {
		
		String pathToEcore = resourceLocation;
		
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		URI existingInstanceUri = URI.createFileURI(pathToEcore);
		
		return resourceSet.getResource(existingInstanceUri, true);
		
	}
	
	protected static String getParentName(String statement) {
		
		statement = statement.trim();
		
		int posOfDot = statement.indexOf('.');
		int posOfArrow = statement.indexOf("->");
		int minPos = min(posOfDot, posOfArrow);
		
		if (minPos == -1) {	//The entire word is the parent
			return statement;
		} else {
			return statement.substring(0, minPos);
		}
		
	}
	
	protected static int min(int a, int b) {
		
		if (a == -1 && b == -1)	//Redundant, but makes it easy to understand that this case is handled. This case is actually handled in the return statement
			return -1;
		
		if (a == -1)
			return b;
		
		if (b == -1)
			return a;
		
		return a < b ? a : b;
		
	}
	
}
