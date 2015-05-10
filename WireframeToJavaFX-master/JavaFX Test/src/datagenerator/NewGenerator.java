package datagenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
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

public class NewGenerator {
	
	private static NewGenerator instance = null;
	
	private Map<String, DataGenerationContainer> storyMap;
	private Map<String, Map<String, EClass>> classesForXmis;
	
	private Entry<String, DataGenerationContainer> currentContainer;
	
	ResourceSet resSet;
	
	private NewGenerator() {
		
		storyMap = new HashMap<String, DataGenerationContainer>();
		classesForXmis = new HashMap<String, Map<String,EClass>>();
		
		resSet = new ResourceSetImpl();
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
	}
	
	public static NewGenerator getInstance() {
		
		if (instance == null) {
			instance = new NewGenerator();
		}
		
		return instance;
	}
	
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
	
	public void addClassForXmi(String xmiFile, EClass newClass) {
		
		Map<String, EClass> classesForXmi = classesForXmis.get(xmiFile);
		if (classesForXmi == null) {
			classesForXmi = new HashMap<String, EClass>();
		}
		
		if (classesForXmi.get(newClass.getName()) == null) {
			classesForXmi.put(newClass.getName(), newClass);
		}
		
	}

	public void generateAll() {
		
		loadXmis();
		addClassesForXmis();
		generateSelectionsEcoreFile();
		generateEcoreFiles();
		
	}
	
	private void loadXmis() {
		
		for (Context xmiContext : currentContainer.getValue().contextGenerator.getXMIContexts()) {
			resSet.getResource(URI.createFileURI(xmiContext.getStatement()), true);
		}
		
	}
	
	private void addClassesForXmis() {
		
		for (Resource resource : resSet.getResources()) {
			if (resource.getContents().get(0) instanceof EPackage) {
				EPackage resourcePackage = (EPackage) resource.getContents().get(0);
				Map<String, EClass> classesForXmi = new HashMap<String, EClass>();
				
				for (EClassifier classifier : resourcePackage.getEClassifiers()) {
					classesForXmi.put(classifier.getName(), (EClass) classifier);
				}
				
				classesForXmis.put(resourcePackage.getNsURI(), classesForXmi);
			}
			
		}
		
	}

	private void generateSelectionsEcoreFile() {
		
		SelectionEcoreGenerator seg = new SelectionEcoreGenerator(this);
		
		for (DataGenerationContainer dgc : storyMap.values()) {
			seg.addSelections(dgc.selectionGenerator.selections);
		}
		
		seg.saveResource();
		
	}

	private void generateEcoreFiles() {
		
		for (DataGenerationContainer dgc : storyMap.values()) {
			dgc.setup();
			dgc.createEcore();
		}
		
	}
	
	/**
	 * @param expectedType
	 * @return First Eclass with the same name as the input parameter
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
