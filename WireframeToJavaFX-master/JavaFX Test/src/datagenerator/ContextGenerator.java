package datagenerator;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;

public class ContextGenerator {
	
	private static ContextGenerator instance = null;
	private DataUtils utils;

	public ArrayList<EObject> newContextsList;
	
	public static ContextGenerator getInstance() {
		
		if (instance == null)
			instance = new ContextGenerator();
		
		return instance;
		
	}
	
	private ContextGenerator() {
		
		utils = DataUtils.getInstance();
		newContextsList = new ArrayList<EObject>();
	}

	public void clear() {
		
		newContextsList.clear();
		
	}
	
	public void generateDecorator(String[] strings) {
		
		if (strings.length != 1)
			throw new RuntimeException("Illegal format for context2");
		
		String line = strings[0];

		String[] splitted = line.split(" = ", 2);
		String name = splitted[0].trim();
		String specificStatement = splitted[1].trim();

		EObject contextObject = utils.dataFactory.create(utils.context2Class);
		contextObject.eSet(utils.c2NameFeature, name);
		contextObject.eSet(utils.c2StatementFeature, specificStatement);
		newContextsList.add(contextObject);
			
	}
	
	public ArrayList<EObject> getAllContexts() {
		
		return newContextsList;
		
	}

}
