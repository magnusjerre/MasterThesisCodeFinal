package datagenerator;

import java.util.LinkedHashSet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl.EObjectInputStream;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class RunDataGenerator {
	
	//EMF
	public ResourceSet resourceSet;
	public Resource ecoreResource;
	public EObject instanceRoot;
	
	public RunDataGenerator() {
		loadContextEcoreDefinition();
	}

	public static void main(String[] args) {
		
	/*
		RunDataGenerator rdg = new RunDataGenerator();
		String query1 = "allActors->select(name = 'Lena Headey')->asSequence()->at(1)";
//		String query1 = "allMovies->select(title = '300')->asSequence()->at(1)";
//		Object result = OCLHandler.parseOCLStatement(rdg.resourceSet, rdg.instanceRoot, query);
		EObject tempResult = (EObject) OCLHandler.parseOCLStatement(rdg.resourceSet, rdg.instanceRoot, query1);
		String query2 = "birthdate";
		Object out = OCLHandler.parseOCLStatement(rdg.resourceSet, tempResult, query2);
		int a = 1;
		String s = "cqk";
		System.out.println("cake");
		
	*/
		
		ContextGenerator cg = new ContextGenerator();
		String location = "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/datagenerator/moviedb.xmi";
		String[] context = {"rContext = " + location};
		cg.generateDecoratorFile(context);
		cg.generateDecoratorFile(new String[]{"actor = rContext.allActors->select(name = 'Lena Headey')->asSequence()->at(1)"});
		cg.generateDecoratorFile(new String[]{"movies = actor.movies"});
		cg.generateDecoratorFile(new String[]{"movies = rContext.allMovies"});
		cg.generatePaths();
		
		System.out.println("cake");
	}
	
	
	private void loadContextEcoreDefinition() {
		String pathToEcore = "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/datagenerator/moviedb.xmi";
		
		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		URI existingInstanceUri = URI.createFileURI(pathToEcore);
		
		ecoreResource = resourceSet.getResource(existingInstanceUri, true);
		instanceRoot = ecoreResource.getContents().get(0);
	}

}
