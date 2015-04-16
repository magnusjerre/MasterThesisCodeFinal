package datagenerator;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;

public class AssignmentGenerator {
	
	private static AssignmentGenerator instance = null;
	private DataUtils utils;
	
	
	public DoubleList<EObject, Master> assignments;
	public ArrayList<EObject> contexts;
	
	public HashMap<Master, Pair<Arrow, Widget>> masterMap = null;
	
	public static AssignmentGenerator getInstance() {
		
		if (instance == null)
			instance = new AssignmentGenerator();
		
		return instance;
		
	}
	
	private AssignmentGenerator() {
		
		utils = DataUtils.getInstance();
		assignments = new DoubleList<EObject, Master>();
		contexts = new ArrayList<EObject>();
		
	}
	
	public void setContext(ArrayList<EObject> contexts) {
		
		this.contexts = contexts;
		
	}
	
	public void clear() {
		
		masterMap = null;
		assignments.clear();
		
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
		
		if (strings.length == 2) {
			assignmentObject.eSet(utils.aUseTypeName, strings[1].trim());
		}
		
		assignmentObject.eSet(utils.aWidgetFeature, getCorrectWidget(assignmentObject, masterMap.get(master)));
		
		//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
		
		assignments.add(assignmentObject, master);
		
	}

	public void generatePaths() {
		
		generateNormalPaths();
		generateUsingTypePaths();
		generatePartOfTypePaths();
		
	}

	private void generateNormalPaths() {
		
		for (EObject assignment : assignments.getElementsIterable()) {
			
			if (!isAssignmentPartOfType(assignment)) {
				Pair<String, EObject> pair = generateContextPath((String) assignment.eGet(utils.aSpecificStatementFeature));
				assignment.eSet(utils.aStatementFeature, pair.getKey());
				assignment.eSet(utils.aRootContextFeature, pair.getValue());
			}
			
		}
		
	}
	
	private void generateUsingTypePaths() {
		
		for (EObject assignment : assignments.getElementsIterable()) {
			
			if (isAssignmentUsingType(assignment)) {
				String useTypeName = (String) assignment.eGet(utils.aUseTypeName);
				EObject useType = TypeGenerator.getInstance().findTypeNamed(useTypeName);
				assignment.eSet(utils.aUsingTypeFeature, useType);
			}
			
		}
		
	}
	
	private void generatePartOfTypePaths() {
		
		for (EObject assignment : assignments.getElementsIterable()) {
			
			if (isAssignmentPartOfType(assignment)) {
				String specificStatement = (String) assignment.eGet(utils.aSpecificStatementFeature);
				assignment.eSet(utils.aStatementFeature,  specificStatement);
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
	
	private int getLayoutId(EObject assignmentObject, Pair<Arrow, Widget> pair) {
		
		if (isAssignmentUsingType(assignmentObject)) {
			return pair.getValue().getId().intValue();
		} else {
			return WidgetUtils.getDeepestWidget(pair.getKey(), pair.getValue()).getId().intValue();
		}
		
	}
	
	private Widget getCorrectWidget(EObject assignmentObject, Pair<Arrow, Widget> pair) {
		
		if (isAssignmentUsingType(assignmentObject)) {
			return pair.getValue();
		} else {
			return WidgetUtils.getDeepestWidget(pair.getKey(), pair.getValue());
		}
		
	}
	
	private static boolean isAssignmentUsingType(EObject eObject) {
		
		String usesType = (String) eObject.eGet(DataUtils.getInstance().aUseTypeName);
		return usesType != null;
		
	}
	
	/**
	 * Only useable after all types for the screen file have been processed.
	 * @param eObject
	 * @return
	 */
	protected static boolean isAssignmentPartOfType(EObject eObject) {
		
		EObject partOfType = (EObject) eObject.eGet(DataUtils.getInstance().aPartOf);
		return partOfType != null;
		
	}
	
}
