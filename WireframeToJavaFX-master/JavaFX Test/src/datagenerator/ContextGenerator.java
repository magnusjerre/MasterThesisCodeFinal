package datagenerator;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import javafx.util.Pair;

import org.eclipse.emf.ecore.EObject;

public class ContextGenerator {
	
	private static ContextGenerator instance = null;
	private DataUtils utils;
	
	public ArrayList<EObject> contexts;
	public ArrayList<EObject> rootContexts;
	
	public static ContextGenerator getInstance() {
		
		if (instance == null)
			instance = new ContextGenerator();
		
		return instance;
		
	}
	
	private ContextGenerator() {
		
		utils = DataUtils.getInstance();
		contexts = new ArrayList<>();
		rootContexts = new ArrayList<>();
		
	}

	public void clear() {
		
		contexts.clear();
		rootContexts.clear();
		
	}
	
	public ArrayList<EObject> getAllContexts() {
		
		ArrayList<EObject> allContexts = new ArrayList<EObject>();
		allContexts.addAll(rootContexts);
		allContexts.addAll(contexts);
		return allContexts;
		
	}
	
	public void generateDecorator(String[] strings) {
		
		for (int i = 0; i < strings.length; i++) {
			
			String line = strings[i];

			String[] splitted = line.split(" = ", 2);
			String name = splitted[0].trim();
			String specificStatement = splitted[1].trim();

			EObject contextObject = utils.dataFactory.create(utils.contextClass);
			contextObject.eSet(utils.cNameFeature, name);
			contextObject.eSet(utils.cSpecificStatementFeature, specificStatement);
			contextObject.eSet(utils.cIsRootFeature, false);

			if (isContextRoot(line)) {	
				EObject rootContext = DataUtils.getRootObjectForXmi(specificStatement);
				contextObject.eSet(utils.cIsRootFeature, true);
				contextObject.eSet(utils.cContextFeature, rootContext);
				contextObject.eSet(utils.cStatementFeature, specificStatement);
				rootContexts.add(contextObject);
			} else {
				contexts.add(contextObject);
			}
			
		}
		
	}
	
	private boolean isContextRoot(String string) { 
		
		return string.contains("= /"); 
		
	}	
	
	public void generatePaths() {
		
		for (EObject eObject : contexts) {
			Pair<String, EObject> pair = generateContextPath((String) eObject.eGet(utils.cSpecificStatementFeature));
			eObject.eSet(utils.cStatementFeature, pair.getKey());
			eObject.eSet(utils.cContextFeature, pair.getValue());
		}
		
	}
	
	private Pair<String, EObject> generateContextPath(String statement) {
		
		statement = statement.trim();
		String parentName = DataUtils.getParentName(statement);
		EObject root = null;
		
		while (parentName != null) {
			
			statement = statement.replace(parentName, "");
			
			if (isRootContextName(parentName)) {
				statement = "self" + statement;
				root = utils.getContextNamed(parentName, rootContexts);
				parentName = null;
			} else {
				//Statement should now not show any data regarding the parent
				EObject parent = utils.getContextNamed(parentName, contexts);
				statement = ((String) parent.eGet(utils.cSpecificStatementFeature)) + statement;
				parentName = DataUtils.getParentName(statement);
			}
			
		}
		
		return new Pair<String, EObject>(statement, root);
		
	}
	
	private boolean isRootContextName(String name) {
		
		try {
			utils.getContextNamed(name, rootContexts);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
		
	}

}
