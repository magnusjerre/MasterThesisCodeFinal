package datagenerator;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import javafx.util.Pair;

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

public class ContextGenerator {
	
	private static ContextGenerator instance = null;
	
	public ArrayList<EObject> contexts;
	public ArrayList<EObject> rootContexts;
	
	//EMF
	ResourceSet resourceSet;
	Resource ecoreResource;
	EPackageImpl dataPackage;
	EFactory dataFactory;
	EClass contextClass;
	EStructuralFeature nameFeature, contextFeature, isRootFeature, statementFeature, specificStatementFeature;
	
	public static ContextGenerator getInstance() {
		
		if (instance == null)
			instance = new ContextGenerator();
		
		return instance;
		
	}
	
	private ContextGenerator() {
		
		contexts = new ArrayList<>();
		rootContexts = new ArrayList<>();
		
		loadContextEcoreDefinition();
		
	}

	public void clear() {
		
		contexts.clear();
		rootContexts.clear();
		
	}
	
	public ArrayList<EObject> getAllContexts() {
		
		ArrayList<EObject> allContexts = new ArrayList<EObject>();
		allContexts.addAll(rootContexts);
		allContexts.addAll(contexts);
		return allContexts;
		
	}
	
	private void loadContextEcoreDefinition() {
		
		String pathToEcore = "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/datagenerator/Data.ecore";
		
		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		URI existingInstanceUri = URI.createFileURI(pathToEcore);
		
		ecoreResource = resourceSet.getResource(existingInstanceUri, true);
		dataPackage = (EPackageImpl)ecoreResource.getContents().get(0);
		dataFactory = dataPackage.getEFactoryInstance();
		contextClass = (EClass)dataPackage.getEClassifier("Context");
		nameFeature = contextClass.getEStructuralFeature("name");
		contextFeature = contextClass.getEStructuralFeature("rootContext");
		isRootFeature = contextClass.getEStructuralFeature("isRoot");
		statementFeature = contextClass.getEStructuralFeature("statement");
		specificStatementFeature = contextClass.getEStructuralFeature("specificStatement");
	}
	
	public void generateDecorator(String[] strings) {
		
		for (int i = 0; i < strings.length; i++) {
			
			String line = strings[i];

			String[] splitted = line.split(" = ", 2);
			String name = splitted[0].trim();
			String specificStatement = splitted[1].trim();

			EObject contextObject = dataFactory.create(contextClass);
			contextObject.eSet(nameFeature, name);
			contextObject.eSet(specificStatementFeature, specificStatement);
			contextObject.eSet(isRootFeature, false);

			if (isContextRoot(line)) {	
				EObject rootContext = loadRootObjectForXmi(specificStatement);
				contextObject.eSet(isRootFeature, true);
				contextObject.eSet(contextFeature, rootContext);
				contextObject.eSet(statementFeature, specificStatement);
				rootContexts.add(contextObject);
			} else {
				contexts.add(contextObject);
			}
			
		}
		
	}
	
	private Pair<String, EObject> generateContextPath(String statement) {
		
		statement = statement.trim();
		String parentName = getParentName(statement);
		EObject root = null;
		
		while (parentName != null) {
			
			statement = statement.replace(parentName, "");
			
			if (isRootContextName(parentName)) {
				statement = "self" + statement;
				root = getContextNamed(parentName, rootContexts);
				parentName = null;
			} else {
				//Statement should now not show any data regarding the parent
				EObject parent = getContextNamed(parentName, contexts);
				statement = ((String) parent.eGet(specificStatementFeature)) + statement;
				parentName = getParentName(statement);
			}
			
		}
		
		return new Pair<String, EObject>(statement, root);
		
	}
	
	private boolean isRootContextName(String name) {
		
		try {
			getContextNamed(name, rootContexts);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
		
	}
	
	private String getParentName(String statement) {
		
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
	
	private int min(int a, int b) {
		
		if (a == -1 && b == -1)	//Redundant, but makes it easy to understand that this case is handled. This case is actually handled in the return statement
			return -1;
		
		if (a == -1)
			return b;
		
		if (b == -1)
			return a;
		
		return a < b ? a : b;
		
	}
	
	private EObject getContextNamed(String name, ArrayList<EObject> list) {
		
		for (EObject eObject : list) {
			String eName = (String) eObject.eGet(nameFeature);
			if (name.equals(eName)) {				
				return eObject;
			}
		}
		
		throw new NoSuchElementException(String.format("Context named \"%s\" doesn't exist. \"%s\" might be misspelled.", name, name));
		
	}
	
	public void generatePaths() {
		
		for (EObject eObject : contexts) {
			Pair<String, EObject> pair = generateContextPath((String) eObject.eGet(specificStatementFeature));
			eObject.eSet(statementFeature, pair.getKey());
			eObject.eSet(contextFeature, pair.getValue());
		}
		
	}

	private EObject loadRootObjectForXmi(String xmiLocation) {
		
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		URI existingInstanceUri = URI.createFileURI(xmiLocation);
		Resource instanceResource = resourceSet.getResource(existingInstanceUri, true);	
		
		return instanceResource.getContents().get(0);
	}
	
	private boolean isContextRoot(String string) { 
		
		return string.contains("= /"); 
		
	}

}
