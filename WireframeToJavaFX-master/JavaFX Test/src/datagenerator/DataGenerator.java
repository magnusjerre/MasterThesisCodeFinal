package datagenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;

import data.Context;

/**
 * The DataGenerator class is responsible for managing the different data decorators for all the screens in the story.
 * Implemented as a singleton so that it's easy to store the different decorators in memory when they are encountered
 * in ScreenDecoratorGenerator.
 * 
 * DataGenerator works by first adding all data decorator elements for each screen into separate 
 * DataGenerationContainers without any special processing other than assigning the value to the different data 
 * decorator objects. Note that before starting to read the next screen-file, a call to prepareGeneratorForScreen is 
 * necessary so that a new DataGenerationContainer can be created for the new screen file.
 * 
 * The second step is to load all xmi-files the story is going to use, these are the ones defined as contexts 
 * starting with a slash "/". After all xmi-files have been loaded, each class defined inside the xmi-files' ecore file
 * are added to a map for easy retrieval. Before any other contexts (or Assignments, ViewComponents) are processed, 
 * all selections must be processed and stored in an ecore in order to verify and get the correct type for the 
 * selection. This is necessary for the contexts that are dependant on some selection, otherwise the contexts' type 
 * (and subsequent Assignments) can't be verified.
 * 
 * When the xmi-files have been loaded, classes stored and selections saved as an ecore file, the ecore files for each 
 * of the screens in the story are created and saved.
 * 
 * @author Magnus Jerre
 *
 */
public class DataGenerator {
	
	private static DataGenerator instance = null;
	
	private Map<String, DataGenerationContainer> storyMap;
	private Map<String, Map<String, EClass>> classesForXmis;
	
	private Entry<String, DataGenerationContainer> currentContainer;
	
	ResourceSet resSet;
	
	private DataGenerator() {
		
		storyMap = new HashMap<String, DataGenerationContainer>();
		classesForXmis = new HashMap<String, Map<String,EClass>>();
		
		resSet = new ResourceSetImpl();
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
	}
	
	public static DataGenerator getInstance() {
		
		if (instance == null) {
			instance = new DataGenerator();
		}
		
		return instance;
	}
	
	/**
	 * This method should be called whenever a new screen file will be processed. It's responsible for creating a new 
	 * DataGenerationContainer to use with the new screen.
	 * @param screenName
	 */
	public void prepareGeneratorForScreen(String screenName) {
		
		DataGenerationContainer dgc = new DataGenerationContainer(screenName, resSet);
		storyMap.put(screenName, dgc);
		for (Entry<String, DataGenerationContainer> entry : storyMap.entrySet()) {
			if (entry.getKey().equals(screenName)) {
				currentContainer = entry;
			}
		}
		
	}
	
	public void generateContextDecorator(String[] strings) {
		currentContainer.getValue().generateContextDecorator(strings);
	}
	
	public void generateAssignmentDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		currentContainer.getValue().generateAssignmentDecorator(strings, master, map);
	}
	
	public void generateViewComponentDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		currentContainer.getValue().generateViewComponentDecorator(strings, master, map);
	}
	
	public void generateSelectionDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		currentContainer.getValue().generateSelectionComponentDecorator(strings, master, map);
	}
	
	public void generateAll() {
		
		loadXmis();
		addClassesForXmis();
		generateAndSaveSelectionsEcoreFile();
		generateScreenEcoreFiles();
		
	}
	
	/**
	 * Loads each unique xmi-file used in the story.
	 */
	private void loadXmis() {
		
		for (DataGenerationContainer container : storyMap.values()) {
			for (Context context : container.contextGenerator.contexts) {
				if (context.isStatementXmiLocation()) {
					resSet.getResource(URI.createFileURI(context.getStatement()), true);
				}
			}
		}
		
	}
	
	/**
	 * Adds all the classes for each loaded xmi-file into the map containing the name of the xmi-file and a new map
	 * containing the name of the EClass and the actual EClass.
	 */
	private void addClassesForXmis() {
		
		for (Resource resource : resSet.getResources()) {
			if (resource.getContents().get(0) instanceof EPackage) {
				EPackage resourcePackage = (EPackage) resource.getContents().get(0);
				Map<String, EClass> classesForXmi = new HashMap<String, EClass>();
				
				for (EClassifier classifier : resourcePackage.getEClassifiers()) {
					if (classifier instanceof EClass)
						classesForXmi.put(classifier.getName(), (EClass) classifier);
				}
				
				classesForXmis.put(resourcePackage.getNsURI(), classesForXmi);
			}
			
		}
		
	}
	
	/**
	 * Searches through all DataGenerationContainers for Selections. Each selection is added to the generator. After all
	 * Selections have been found and processed, the ecore file is saved as SelectionModel.ecore
	 */
	private void generateAndSaveSelectionsEcoreFile() {
		
		SelectionEcoreGenerator seg = new SelectionEcoreGenerator(this);
		
		for (DataGenerationContainer dgc : storyMap.values()) {
			seg.addSelections(dgc.selectionGenerator.selections);
		}
		
		seg.saveResource();
		
	}

	private void generateScreenEcoreFiles() {
		
		for (DataGenerationContainer dgc : storyMap.values()) {
			dgc.setup();
			dgc.createEcore();
		}
		
	}
	
	/**
	 * @param expectedType
	 * @return First EClass with the same name as the input parameter. If there exist several different classes with 
	 * the same name, only the first class encountered will be returned. 
	 */
	public EClassifier getClassifierForName(String expectedType) {
		
		for (Entry<String, Map<String, EClass>> classesForXmi : classesForXmis.entrySet()) {
			
			for (String key : classesForXmi.getValue().keySet()) {
				
				if (expectedType.equals(key)) {
					return classesForXmi.getValue().get(key);
				}
			}
			
		}
		
		return null;
	}
	
}
