package datagenerator;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;

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
		
		XMIExporter exporter = new XMIExporter();
		exporter.setGenerators(ag, cg);
		exporter.exportXMI("cake.xmi", "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/datagenerator/");
		
		System.out.println("cake");
		
	}

}
