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
		
		masterMap = null;
		assignments.clear();
		assignmentsAsMaster.clear();
		
	}
	
	public void generateDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		
		if (masterMap == null) {
			masterMap = map;
		}
		
		if (strings.length < 1 || strings.length > 2) {
			throw new RuntimeException("The decorator is not well formed, it contains either too many or too few lines.\n"
					+ "The Assignment decorator allows either one or two lines.");
		}
		
		EObject assignmentObject = utils.dataFactory.create(utils.assignmentClass);
		String specificStatement = strings[0].trim();
		assignmentObject.eSet(utils.aSpecificStatementFeature, specificStatement);
		assignmentObject.eSet(utils.aLayoutIDFeature, map.get(master).getValue().getId().intValue());
		if (strings.length == 2) {
			assignmentObject.eSet(utils.aUseTypeName, strings[1].trim());
		}
		
		//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
		
		assignments.add(assignmentObject);
		assignmentsAsMaster.add(master);
		
	}
	
	
	
	public void generatePaths() {
		
		generateNormalPaths();
		generateUsingTypePaths();
		generatePartOfTypePaths();
		
	}

	private void generateNormalPaths() {
		
		for (EObject assignment : assignments) {
			
			if (!isTypeAssignment(assignment)) {
				Pair<String, EObject> pair = generateContextPath((String) assignment.eGet(utils.aSpecificStatementFeature));
				assignment.eSet(utils.aStatementFeature, pair.getKey());
				assignment.eSet(utils.aRootContextFeature, pair.getValue());
			}
			
		}
		
	}
	
	private void generateUsingTypePaths() {
		
		for (EObject assignment : assignments) {
			
			if (usesType(assignment)) {
				String useTypeName = (String) assignment.eGet(utils.aUseTypeName);
				EObject useType = TypeGenerator.getInstance().findTypeNamed(useTypeName);
				assignment.eSet(utils.aUseType, useType);
			}
			
		}
		
	}
	
	private void generatePartOfTypePaths() {
		
		for (EObject assignment : assignments) {
			
			if (isTypeAssignment(assignment)) {
				EObject type = (EObject) assignment.eGet(utils.aPartOf);
				EObject typeUser = findAssignmentUsingType(type);
				String typeUserStatement = (String) typeUser.eGet(utils.aStatementFeature);
				String specificStatement = (String) assignment.eGet(utils.aSpecificStatementFeature);
				assignment.eSet(utils.aStatementFeature,  typeUserStatement + specificStatement);
				assignment.eSet(utils.aRootContextFeature, typeUser.eGet(utils.aRootContextFeature));
			}
			
		}
		
	}

	private boolean usesType(EObject assignment) {
		
		return assignment.eGet(utils.aUseTypeName) != null;
		
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
	
	protected boolean isTypeAssignment(EObject eObject) {
		
		Pair<Arrow, Widget> pair = getPairForAssignment(eObject);
		if (pair.getValue() instanceof WidgetGroup) {
			if (eObject.eGet(utils.aUseTypeName) == null) {
				return true;
			}
		}
		
		return false;
		
	}

	private EObject findAssignmentUsingType(EObject type) {
		
		for (EObject assignment : assignments) {
			
			EObject assignmentUsingType = (EObject) assignment.eGet(utils.aUseType);
			if (type == assignmentUsingType) {
				return assignment;
			}
			
		}
		
		throw new RuntimeException(String.format("Couldn't find the assignment using type: %s", type.eGet(utils.tNameFeature)));
		
	}
	
}
