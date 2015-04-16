package datagenerator;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;

import application.Constants;

public class RunDataGenerator {

	public static void main(String[] args) {
		
		ContextGenerator cg = ContextGenerator.getInstance();
		String location = Constants.DATAGENERATOR_DIRECTORY + "moviedb.xmi";
		String[] context = {"rContext = " + location};
		cg.generateDecorator(context);
		cg.generateDecorator(new String[]{"actor = rContext.allActors->select(name = 'Lena Headey')->asSequence()->at(1)"});
		cg.generateDecorator(new String[]{"movies = actor.movies"});
		cg.generateDecorator(new String[]{"movies = rContext.allMovies"});
		cg.generatePaths();
		
		ArrayList<EObject> allContexts = new ArrayList<>();
		allContexts.addAll(cg.rootContexts);
		allContexts.addAll(cg.contexts);
		AssignmentGenerator ag = AssignmentGenerator.getInstance();
		ag.setContext(cg.getAllContexts());
//		ag.generateDecorator(new String[]{"movies->at(1)"}, 1);
//		ag.generateDecorator(new String[]{"actor.name", "@Cake"}, 2);
		ag.generatePaths();
		
//		XMIExporter exporter = new XMIExporter();
//		exporter.setGenerators(ag, cg, TypeGenerator.getInstance());
//		exporter.exportXMI("cake.xmi", Constants.DATAGENERATOR_DIRECTORY);
//		
//		System.out.println("cake");
		
	}

}
