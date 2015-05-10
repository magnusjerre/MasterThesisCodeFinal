package datagenerator;

import java.util.HashMap;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;

public class DataGenerationContainer {
	
	private String screenName;
	
	ContextGenerator contextGenerator;
	AssignmentGenerator assignmentGenerator;
	TypeGenerator viewComponentGenerator;
	SelectionGenerator selectionGenerator;
	
	ResourceSet resSet;
	
	public DataGenerationContainer(String screenName, ResourceSet resSet) {
		
		this.screenName = screenName;
		this.resSet = resSet;
		
		contextGenerator = new ContextGenerator();
		assignmentGenerator = new AssignmentGenerator();
		viewComponentGenerator = new TypeGenerator(assignmentGenerator.assignments);
		selectionGenerator = new SelectionGenerator();
		
	}
	
	public void generateContextDecorator(String[] strings) {
		contextGenerator.generateDecorator(strings);
	}
	
	public void generateAssignmentDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		assignmentGenerator.generateDecorator(strings, master, map);
	}
	
	public void generateViewComponentDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		viewComponentGenerator.generateDecorator(strings, master, map);
	}
	
	public void generateSelectionComponentDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		selectionGenerator.generateDecorator(strings, master, map);
	}
	
	public void setup() {
		
		viewComponentGenerator.setupAssignmentReferences();
		viewComponentGenerator.generateFxmlForTypes(screenName);
	}

	public void createEcore() {
		ScreenEcoreGenerator seg = new ScreenEcoreGenerator(contextGenerator.getAllContexts(), assignmentGenerator.assignments.elements, viewComponentGenerator.theList.elements);
		seg.generateEcoreForScreen(screenName);
	}
	
}
