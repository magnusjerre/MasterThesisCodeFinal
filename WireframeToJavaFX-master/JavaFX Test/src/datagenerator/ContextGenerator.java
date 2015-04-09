package datagenerator;

import java.util.ArrayList;

import javafx.util.Pair;

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

import com.sun.xml.internal.ws.encoding.RootOnlyCodec;

public class ContextGenerator {
	
	public ArrayList<EObject> contexts;
	public ArrayList<EObject> rootContexts;
	
	//EMF
	ResourceSet resourceSet;
	Resource ecoreResource;
	EPackageImpl contextPackage;
	EFactory contextFactory;
	EClass contextClass;
	EStructuralFeature contextNameFeature, rootContextFeature, isRootFeature, contextStatementFeature, contextSpecificStatementFeature;
	
	public ContextGenerator() {
		
		contexts = new ArrayList<>();
		rootContexts = new ArrayList<>();
		
		loadContextEcoreDefinition();
	}

	private void loadContextEcoreDefinition() {
		String pathToEcore = "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/datagenerator/Context.ecore";
		
		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		URI existingInstanceUri = URI.createFileURI(pathToEcore);
		
		ecoreResource = resourceSet.getResource(existingInstanceUri, true);
		contextPackage = (EPackageImpl)ecoreResource.getContents().get(0);
		contextFactory = contextPackage.getEFactoryInstance();
		contextClass = (EClass)contextPackage.getEClassifier("Context");
		contextNameFeature = contextClass.getEStructuralFeature("name");
		rootContextFeature = contextClass.getEStructuralFeature("rootContext");
		isRootFeature = contextClass.getEStructuralFeature("isRoot");
		contextStatementFeature = contextClass.getEStructuralFeature("statement");
		contextSpecificStatementFeature = contextClass.getEStructuralFeature("specificStatement");
	}
	
	public void generateDecoratorFile(String[] strings) {
		for (int i = 0; i < strings.length; i++) {
			String line = strings[i];

			String[] splitted = line.split(" = ", 2);
			String name = splitted[0].trim();
			String specificStatement = splitted[1].trim();

			EObject contextObject = contextFactory.create(contextClass);
			contextObject.eSet(contextNameFeature, name);
			contextObject.eSet(contextSpecificStatementFeature, specificStatement);
			contextObject.eSet(isRootFeature, false);

			if (isContextRoot(line)) {	
				EObject rootContext = loadRootObjectForXmi(specificStatement);
				contextObject.eSet(isRootFeature, true);
				contextObject.eSet(rootContextFeature, rootContext);
				contextObject.eSet(contextStatementFeature, specificStatement);
				rootContexts.add(contextObject);
			} else {
				contexts.add(contextObject);
			}
		}
	}
	
	public Pair<String, EObject> generateContextPath(String statement) {
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
				statement = ((String) parent.eGet(contextSpecificStatementFeature)) + statement;
				parentName = getParentName(statement);
			}			
		}
		
		return new Pair<String, EObject>(statement, root);
	}
	
	public boolean isRootContextName(String name) {
		
		EObject result = getContextNamed(name, rootContexts);
		return result != null ? true : false;
		
	}
	
	
	public String getParentName(String statement) {
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
			String eName = (String) eObject.eGet(contextNameFeature);
			if (name.equals(eName)) {				
				return eObject;
			}
		}
		
		return null;
		
	}
	
	public void generatePaths() {
		for (EObject eObject : contexts) {
			Pair<String, EObject> pair = generateContextPath((String) eObject.eGet(contextSpecificStatementFeature));
			eObject.eSet(contextStatementFeature, pair.getKey());
			eObject.eSet(rootContextFeature, pair.getValue());
		}
	}
	public void generateNonRootContexts(){
		for (int i = 0; i < contexts.size(); i++) {
			EObject contextObject = contexts.get(i);
			EObject contextValue = (EObject) contextObject.eGet(rootContextFeature);
			if (contextValue == null) {	//the values for the context has not been set
				EObject parentContext = getParentContext("parent");
				//Parse ocl statement
			}
		}
	}
	
	private EObject getParentContext(String string) {
		for (EObject eObject : rootContexts) {
			String name = (String) eObject.eGet(contextNameFeature); 
			if (name.equalsIgnoreCase(string)) {
				return eObject;
			}
		}
		
		for (EObject eObject : contexts) {
			String name = (String) eObject.eGet(contextNameFeature); 
			if (name.equalsIgnoreCase(string)) {
				return eObject;
			}
		}
		
		return null;
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
