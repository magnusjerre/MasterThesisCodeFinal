package datagenerator;

import java.util.ArrayList;

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
	EStructuralFeature contextNameFeature, contextFeature, isRootFeature, contextStatementFeature;
	
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
		contextFeature = contextClass.getEStructuralFeature("context");
		isRootFeature = contextClass.getEStructuralFeature("isRoot");
		contextStatementFeature = contextClass.getEStructuralFeature("statement");
	}
	
	public void generateDecoratorFile(String[] strings) {
		for (int i = 0; i < strings.length; i++) {
			String line = strings[i];

			String[] splitted = line.split(" = ", 2);
			String name = splitted[0].trim();
			String statement = splitted[1].trim();

			EObject contextObject = contextFactory.create(contextClass);
			contextObject.eSet(contextNameFeature, name);
			contextObject.eSet(contextStatementFeature, statement);
			contextObject.eSet(isRootFeature, false);

			if (isContextRoot(line)) {	
				EObject rootContext = loadRootObjectForXmi(statement);
				contextObject.eSet(isRootFeature, true);
				contextObject.eSet(contextFeature, rootContext);				
				rootContexts.add(contextObject);
			} else {
				contexts.add(contextObject);
			}
		}
	}
	
	public void generateNonRootContexts(){
		for (int i = 0; i < contexts.size(); i++) {
			EObject contextObject = contexts.get(i);
			EObject contextValue = (EObject) contextObject.eGet(contextFeature);
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
