package datagenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;

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
			list.add(DataUtils.getInstance().getRootObjectForXmi(specificStatement));
			rootContexts.add(contextObject);
		} else {
			contexts.add(contextObject);
		}
			
	}
	
	public ArrayList<EObject> getAllContexts() {
		
		ArrayList<EObject> allContexts = new ArrayList<EObject>();
		allContexts.addAll(rootContexts);
		allContexts.addAll(contexts);
		return allContexts;
		
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
							fillListWithCorrectFormat((Collection<Object>) dataResult, (EList<Object>) eObject.eGet(utils.c2DataFeature));
						} else {
							EObject res = (EObject) dataResult;
							((EList<EObject>) eObject.eGet(utils.c2DataFeature)).add(res);
						}
					}
					
				}
				
			}
		}
		
	}
	
	protected void fillListWithCorrectFormat(Collection<Object> result, EList<Object> listToFille) {
		
		Iterator it = result.iterator();
		while (it.hasNext()) {
			Object object = it.next();
			if (object instanceof EObject) {
				listToFille.add(object);
			} else {
				EObject container = utils.dataFactory.create(utils.javaObjectContainerClass);
				EList<Object> jocs = (EList<Object>) container.eGet(utils.jocObjectFeature);
				jocs.add(object);
				container.eSet(utils.jocStringRepresentationFeature, jocs.toString());
				listToFille.add(container);
			}
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
	
	
	protected Object getResult(String statement, EObject parent) {
		
		EList<EObject> list = (EList<EObject>) parent.eGet(utils.c2DataFeature);
		if (list.size() == 1) {
			EObject first = list.get(0);
//			ResourceSet rs = first.eResource().getResourceSet();
			ResourceSet rs = utils.resourceSet;
			
			Object result = (Object) OCLHandler.parseOCLStatement(rs, first, statement);
			
			return result;
		} else {
			if (statement.startsWith("self")) {
				statement = "self.data" + statement.replace("self", "");
			} else {
				statement = "data" + statement;
			}
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
