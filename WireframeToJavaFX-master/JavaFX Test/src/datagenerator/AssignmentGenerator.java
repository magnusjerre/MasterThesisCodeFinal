package datagenerator;

import java.util.ArrayList;

import javafx.util.Pair;

import org.eclipse.emf.ecore.EObject;

public class AssignmentGenerator {
	
	private static AssignmentGenerator instance = null;
	private DataUtils utils;
	
	public ArrayList<EObject> assignments;
	public ArrayList<EObject> contexts;
	
	public static AssignmentGenerator getInstance() {
		
		if (instance == null)
			instance = new AssignmentGenerator();
		
		return instance;
		
	}
	
	private AssignmentGenerator() {
		
		utils = DataUtils.getInstance();
		assignments = new ArrayList<EObject>();
		contexts = new ArrayList<EObject>();
		
	}
	
	public void setContext(ArrayList<EObject> contexts) {
		
		this.contexts = contexts;
		
	}
	
	public void clear() {
		
		contexts = new ArrayList<EObject>();
		
		assignments.clear();
		
	}
	
	public void generateDecorator(String[] strings, int id) {
		
		for (int i = 0; i < strings.length; i++) {
			
			if (i > 0) break;	//temporary statement until more functionality (types) is implemented
			
			String specificStatement = strings[i].trim();

			EObject assignmentObject = utils.dataFactory.create(utils.assignmentClass);
			assignmentObject.eSet(utils.aSpecificStatementFeature, specificStatement);
			assignmentObject.eSet(utils.aLayoutIDFeature, id);
			//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
			
			assignments.add(assignmentObject);

		}
		
	}
	
	public void generatePaths() {
		
		for (EObject assignment : assignments) {
			
			Pair<String, EObject> pair = generateContextPath((String) assignment.eGet(utils.aSpecificStatementFeature));
			assignment.eSet(utils.aStatementFeature, pair.getKey());
			assignment.eGet(utils.aRootContextFeature);
			assignment.eSet(utils.aRootContextFeature, pair.getValue());
			
		}
		
	}

	private Pair<String, EObject> generateContextPath(String statement) {
		
		statement = statement.trim();
		String parentName = DataUtils.getParentName(statement);
		if (parentName.equals("")) {
			return new Pair<String, EObject>("", null);
		}
		
		statement = statement.replace(parentName, "");
		
		EObject parent = utils.getContextNamed(parentName, contexts);
		EObject root = (EObject) parent.eGet(utils.cContextFeature);
		statement = ((String) parent.eGet(utils.cStatementFeature)) + statement;
		
		return new Pair<String, EObject>(statement, root);
		
	}

}
