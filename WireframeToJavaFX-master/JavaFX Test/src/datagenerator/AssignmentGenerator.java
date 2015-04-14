package datagenerator;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;
import com.wireframesketcher.model.WidgetGroup;

public class AssignmentGenerator {
	
	private static AssignmentGenerator instance = null;
	private DataUtils utils;
	
	public ArrayList<EObject> assignments;
	public ArrayList<Master> assignmentsAsMaster;
	public ArrayList<EObject> contexts;
	
	public HashMap<Master, Pair<Arrow, Widget>> masterMap = null;
	
	public static AssignmentGenerator getInstance() {
		
		if (instance == null)
			instance = new AssignmentGenerator();
		
		return instance;
		
	}
	
	private AssignmentGenerator() {
		
		utils = DataUtils.getInstance();
		assignments = new ArrayList<EObject>();
		assignmentsAsMaster = new ArrayList<Master>();
		contexts = new ArrayList<EObject>();
		
	}
	
	public void setContext(ArrayList<EObject> contexts) {
		
		this.contexts = contexts;
		
	}
	
	public void clear() {
		
		contexts = new ArrayList<EObject>();
		masterMap = null;
		assignments.clear();
		assignmentsAsMaster.clear();
		
	}
	
	public void generateDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		
		if (masterMap == null) {
			masterMap = map;
		}
		
		if (strings.length > 1)
			return;
		
		for (int i = 0; i < strings.length; i++) {
			
			String specificStatement = strings[i].trim();

			EObject assignmentObject = utils.dataFactory.create(utils.assignmentClass);
			assignmentObject.eSet(utils.aSpecificStatementFeature, specificStatement);
			assignmentObject.eSet(utils.aLayoutIDFeature, map.get(master).getValue().getId().intValue());
			//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
			
			assignments.add(assignmentObject);
			assignmentsAsMaster.add(master);

		}
		
	}
	
	public void generatePaths() {
		
		for (EObject assignment : assignments) {
			
			if (isTypeAssignment(assignment)) {
				assignment.eSet(utils.aStatementFeature, assignment.eGet(utils.aSpecificStatementFeature));
				assignment.eSet(utils.aRootContextFeature, null);
			} else {
				Pair<String, EObject> pair = generateContextPath((String) assignment.eGet(utils.aSpecificStatementFeature));
				assignment.eSet(utils.aStatementFeature, pair.getKey());
				assignment.eSet(utils.aRootContextFeature, pair.getValue());
			}
			
			
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
	
	private Pair<Arrow, Widget> getPairForAssignment(EObject assignment) {
		return getPairForEObject(assignment, assignments, assignmentsAsMaster);
	}
	
	private Pair<Arrow, Widget> getPairForEObject(EObject eObject, ArrayList<EObject> eList, ArrayList<Master> masterList) {
		
		int pos = eList.indexOf(eObject);
		Master master = masterList.get(pos);
		return masterMap.get(master);
		
	}
	
	private boolean isTypeAssignment(EObject eObject) {
		
		Pair<Arrow, Widget> pair = getPairForAssignment(eObject);
		if (pair.getValue() instanceof WidgetGroup) {
			return true;	//temporary
		}
		
		return false;
		
	}

}
