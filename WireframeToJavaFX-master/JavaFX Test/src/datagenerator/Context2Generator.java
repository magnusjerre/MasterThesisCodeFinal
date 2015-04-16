package datagenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class Context2Generator {
	
	private static Context2Generator instance = null;
	private DataUtils utils;
	
	public ArrayList<EObject> contexts;
	public ArrayList<EObject> rootContexts;
	
	public static Context2Generator getInstance() {
		
		if (instance == null)
			instance = new Context2Generator();
		
		return instance;
		
	}
	
	private Context2Generator() {
		
		utils = DataUtils.getInstance();
		rootContexts = new ArrayList<>();
		contexts = new ArrayList<>();
		
	}

	public void clear() {
		
		contexts.clear();
		rootContexts.clear();
		
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

		if (isContextRoot(line)) {
			EList<EObject> list = (EList<EObject>) contextObject.eGet(utils.c2DataFeature);
			list.add(DataUtils.getRootObjectForXmi(specificStatement));
			rootContexts.add(contextObject);
		} else {
			contexts.add(contextObject);
		}
			
	}
	
	public void generatePaths() {
		
		while (!allContextsSet()) {
			for (EObject eObject : contexts) {
				
				if (!isContextSet(eObject)) {
					
					String parentName = DataUtils.getParentName((String) eObject.eGet(utils.c2StatementFeature));
					EObject parent = getContextNamed(parentName);
					
					if (parent != null) {
						String statement = trimStatementOfParentAndDot((String) eObject.eGet(utils.c2StatementFeature));
						Object dataResult = getResult(statement, parent);
						
						if (dataResult instanceof Collection<?>) {
							fillListWithCorrectFormat((Collection<EObject>) dataResult, (EList<EObject>) eObject.eGet(utils.c2DataFeature));
						} else {
							EObject res = (EObject) dataResult;
							((EList<EObject>) eObject.eGet(utils.c2DataFeature)).add(res);
						}
					}
					
				}
				
			}
		}
		
	}
	
	private void fillListWithCorrectFormat(Collection<EObject> result, EList<EObject> listToFille) {
		
		Iterator it = result.iterator();
		while (it.hasNext()) {
			EObject eObject = (EObject) it.next();
			listToFille.add(eObject);
		}
		
		
	}
	
	private String trimStatementOfParentAndDot(String statement) {
		
		String statement2 = statement.trim();
		String parentName = DataUtils.getParentName(statement2);
		statement2 = statement2.replace(parentName, "");
		
		if (statement2.charAt(0) == '.') {
			statement2 = statement2.substring(1, statement2.length());
		} 
		
		return statement2;
	}
	
	
	private Object getResult(String statement, EObject parent) {
		
		EList<EObject> list = (EList<EObject>) parent.eGet(utils.c2DataFeature);
		if (list.size() == 1) {
			EObject first = list.get(0);
			ResourceSet rs = first.eResource().getResourceSet();
			
			Object result = (Object) OCLHandler.parseOCLStatement(rs, first, statement);
			
			return result;
		} else {
			statement = "data" + statement;
			Object result = OCLHandler.parseOCLStatement(utils.dataResource.getResourceSet(), parent, statement);
			return result;
		}
		
	}
	
	private EObject getContextNamed(String name) {
		
		try {
			
			EObject parent = utils.getContextNamed(name, rootContexts);
			return parent;
			
		} catch (NoSuchElementException e) {
		}
		
		try {

			EObject parent = utils.getContextNamed(name, contexts);
			return parent;
			
		} catch (NoSuchElementException e) {
		}
		
		return null;
		
	}
	
	private boolean isContextRoot(String string) { 
		
		return string.contains("= /"); 
		
	}
	
	private boolean isManyContext(EObject eObject) {
		
		EList<EObject> list = (EList<EObject>) eObject.eGet(utils.c2DataFeature);
		
		if (list.size() > 1) {
			return true;
		}
		
		return false;
		
	}
	
	private boolean allContextsSet() {
		
		for (EObject eObject : contexts) {
			
			if (!isContextSet(eObject)) {
				return false;
			}
			
		}
		
		return true;
		
	}
	
	private boolean isContextSet(EObject context) {
		
		EList<EObject> data = (EList<EObject>) context.eGet(utils.c2DataFeature);
		if (data.size() == 0) {
			return false;
		}
		
		return true;
		
	}

}
