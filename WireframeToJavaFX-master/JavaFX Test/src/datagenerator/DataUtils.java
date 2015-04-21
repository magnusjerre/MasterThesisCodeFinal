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
 * Contains the blueprint for the Data model in Data.ecore, as well as any loaded resources
 * the prototype will use. Also contains some helper methods.
 * @author Magnus
 */
public class DataUtils {
	
	private static DataUtils instance = null;
	
	//EMF
	protected ResourceSet resourceSet;
	protected Resource dataResource;
	EPackageImpl dataPackage;
	EFactory dataFactory;
	EClass context2Class, assignment2Class, dataForScreenClass, typeClass, javaObjectContainerClass, mappingClass;
	//DataForScreen class features are prefixed with a d
	EStructuralFeature dAllContextsFeature, dAllTypesFeature, dAllAssignment2sFeature, dAllMappings;
	//Type class features are prefixed with a t
	EStructuralFeature tNameFeature, tAssignmentsFeature, tTypesFeature, tWidgetFeature;
	//Context2 class features are prefixed with a c
	EStructuralFeature c2NameFeature, c2StatementFeature, c2DataFeature;
	//Assignment2 class features are prefixed with a2
	EStructuralFeature a2StatementFeature, a2DataFeature, a2WidgetFeature, a2UseComponentNamedFeature, a2UseComponentFeature, a2PartOfComponentFeature;
	//JavaObjectContainer class
	EStructuralFeature jocObjectFeature, jocTypeFeature, jocStringRepresentationFeature;
	//Mapping class features are prefixed with m
	EStructuralFeature mLayoutIdFeature, mValueFeature, mAssignmentPathFeature, mStringRepresentationFeature, mMappingsFeature, mUsingComponentFeature, mIsListFeature;

	private DataUtils() {
		
		clear();
		
	}
	
	public void clear() {
		
		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

		loadDefinitions();
		
	}
	
	private void loadDefinitions() {
		
		//Resource
		dataResource = loadResource(Constants.DATAGENERATOR_DIRECTORY + "Data.ecore");
		
		//Package
		dataPackage = (EPackageImpl)dataResource.getContents().get(0);
		
		//Factory
		dataFactory = dataPackage.getEFactoryInstance();
		
		//DataForContext
		dataForScreenClass = (EClass) dataPackage.getEClassifier("DataForScreen");
		dAllContextsFeature = dataForScreenClass.getEStructuralFeature("allContexts");
		dAllTypesFeature = dataForScreenClass.getEStructuralFeature("allTypes");
		dAllAssignment2sFeature = dataForScreenClass.getEStructuralFeature("allAssignment2s");
		dAllMappings = dataForScreenClass.getEStructuralFeature("allMappings");
		
		//Type
		typeClass = (EClass) dataPackage.getEClassifier("Type");
		tNameFeature = typeClass.getEStructuralFeature("name");
		tAssignmentsFeature = typeClass.getEStructuralFeature("assignments");
		tTypesFeature = typeClass.getEStructuralFeature("types");
		tWidgetFeature = typeClass.getEStructuralFeature("widget");
		
		//Context2
		context2Class = (EClass) dataPackage.getEClassifier("Context");
		c2NameFeature = context2Class.getEStructuralFeature("name");
		c2StatementFeature = context2Class.getEStructuralFeature("statement");
		c2DataFeature = context2Class.getEStructuralFeature("data");
		
		//Assignment2
		assignment2Class = (EClass) dataPackage.getEClassifier("Assignment"); 
		a2StatementFeature = assignment2Class.getEStructuralFeature("statement");
		a2DataFeature = assignment2Class.getEStructuralFeature("data");
		a2WidgetFeature = assignment2Class.getEStructuralFeature("widget");
		a2UseComponentNamedFeature = assignment2Class.getEStructuralFeature("useComponentNamed");
		a2UseComponentFeature = assignment2Class.getEStructuralFeature("useComponent");
		a2PartOfComponentFeature = assignment2Class.getEStructuralFeature("partOfComponent");
		
		//JavaObjectContainer
		javaObjectContainerClass = (EClass) dataPackage.getEClassifier("JavaObjectContainer");
		jocObjectFeature = javaObjectContainerClass.getEStructuralFeature("object");
		jocTypeFeature = javaObjectContainerClass.getEStructuralFeature("type");
		jocStringRepresentationFeature = javaObjectContainerClass.getEStructuralFeature("stringRepresentation");
		
		//Mapping
		mappingClass = (EClass) dataPackage.getEClassifier("Mapping");
		mLayoutIdFeature = mappingClass.getEStructuralFeature("layoutId");
		mValueFeature = mappingClass.getEStructuralFeature("value");
		mAssignmentPathFeature = mappingClass.getEStructuralFeature("assignmentPath");
		mStringRepresentationFeature = mappingClass.getEStructuralFeature("stringRepresentation");
		mMappingsFeature = mappingClass.getEStructuralFeature("mappings");
		mUsingComponentFeature = mappingClass.getEStructuralFeature("usingComponent");
		mIsListFeature = mappingClass.getEStructuralFeature("isList");
		
	}
	
	public static DataUtils getInstance() {
		
		if (instance == null)
			instance = new DataUtils();
		
		return instance;
		
	}

	protected EObject getRootObjectForXmi(String xmiLocation) {
		
		return loadResource(xmiLocation).getContents().get(0);
		
	}
	
	protected EObject getContextNamed(String name, ArrayList<EObject> list) {
		
		for (EObject eObject : list) {
			String eName = (String) eObject.eGet(c2NameFeature);
			if (name.equals(eName)) {				
				return eObject;
			}
		}
		
		throw new NoSuchElementException(String.format("Context named \"%s\" doesn't exist. \"%s\" might be misspelled.", name, name));
		
	}
	
	protected Resource loadResource(String resourceLocation) {
		
		URI existingInstanceUri = URI.createFileURI(resourceLocation);
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
