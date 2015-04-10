package datagenerator;

import java.util.ArrayList;
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

	public static void main(String[] args) {
		
		ContextGenerator cg = new ContextGenerator();
		String location = "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/datagenerator/moviedb.xmi";
		String[] context = {"rContext = " + location};
		cg.generateDecorator(context);
		cg.generateDecorator(new String[]{"actor = rContext.allActors->select(name = 'Lena Headey')->asSequence()->at(1)"});
		cg.generateDecorator(new String[]{"movies = actor.movies"});
		cg.generateDecorator(new String[]{"movies = rContext.allMovies"});
		cg.generatePaths();
		//cg.saveXMI("cake.xmi", "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/datagenerator/");
		
		ArrayList<EObject> allContexts = new ArrayList<>();
		allContexts.addAll(cg.rootContexts);
		allContexts.addAll(cg.contexts);
		AssignmentGenerator ag = new AssignmentGenerator(allContexts);
		ag.generateDecorator(new String[]{"movies->at(1)"}, 1);
		ag.generateDecorator(new String[]{"actor.name", "@Cake"}, 2);
		ag.generatePaths();
		
		System.out.println("cake");
		
	}

}
