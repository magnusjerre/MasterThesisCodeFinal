package datagenerator;

import java.util.HashMap;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;

/**
 * The DataGenerationContainer class is a container for storing the Context-, Assignment-, ViewComponent- and SelectionGenerator for a specific screen file.
 * It also provides a method for generating an ecore file for the specific screen file.
 * @author Magnus Jerre
 *
 */
public class DataGenerationContainer {
	
	private String screenName;
	
	ContextGenerator contextGenerator;
	AssignmentGenerator assignmentGenerator;
	ViewComponentGenerator viewComponentGenerator;
	SelectionGenerator selectionGenerator;
	
	public DataGenerationContainer(String screenName, ResourceSet resSet) {
		
		this.screenName = screenName;
		
		contextGenerator = new ContextGenerator();
		assignmentGenerator = new AssignmentGenerator();
		viewComponentGenerator = new ViewComponentGenerator(assignmentGenerator.assignments);
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
	
	/**
	 * The setup method is responsible for setting up the references between Assignments and ViewComponents for the screen.
	 */
	public void setup() {
		
		viewComponentGenerator.setupAssignmentReferences();
		viewComponentGenerator.generateFxmlForViewComponents(screenName);
	}

	/**
	 * Dynamically creates an ecore file for the screen file and saves it.
	 */
	public void createEcore() {
		ScreenEcoreGenerator seg = new ScreenEcoreGenerator(contextGenerator.getAllContexts(), assignmentGenerator.assignments.elements, viewComponentGenerator.viewComponents.elements);
		seg.generateEcoreForScreen(screenName);
	}
	
}
