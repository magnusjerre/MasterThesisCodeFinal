package datagenerator;

import java.util.HashMap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;

public class TypeGenerator {
	
	private static TypeGenerator instance = null;
	private DataUtils utils;
	
	public DoubleList<EObject, Master> list;
	public HashMap<Master, Pair<Arrow, Widget>> masterMap;
	
	public DoubleList<EObject, Master> assignments;
	
	private TypeGenerator() {
		
		utils = DataUtils.getInstance();
		assignments = AssignmentGenerator.getInstance().assignments;
		list = new DoubleList<EObject, Master>();
		
	}
	
	public static TypeGenerator getInstance() {
		
		if (instance == null)
			instance = new TypeGenerator();
		
		return instance;
		
	}
	
	public void clear() {
		
		masterMap = null;
		list.clear();
		
	}
	
	public void generateDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		
		if (masterMap == null) {
			masterMap = map;
		}
		
		if (strings.length > 1 || strings.length < 1) {
			throw new RuntimeException(String.format("Illegal number of lines in Type decorator. First line states: %s", strings[0]));
		}
		
		String name = strings[0].trim();

		EObject typeObject = utils.dataFactory.create(utils.typeClass);
		typeObject.eSet(utils.tNameFeature, name);
		typeObject.eSet(utils.tWidgetFeature, masterMap.get(master).getValue());
		//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
		
		list.add(typeObject, master);

	}
	
	public void setupAssignmentReferences() {
		
		if (list.size() == 0) {
			return;
		}
		
		for (EObject assignment : assignments.getElementsIterable()) {
			
			if (isPartOfType(assignment)) {
				EObject type = getTypeForAssignment(assignment);
				setupConnection(assignment, type);
			}
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void setupConnection(EObject assignment, EObject type) {

		((EList<EObject>)type.eGet(utils.tAssignmentsFeature)).add(assignment);
		assignment.eSet(utils.aPartOf, type);
		assignment.eSet(utils.aWidgetFeature, getCorrectWidget(assignment));
		
		String assignmentUsesTypeNamed = (String) assignment.eGet(utils.aUsingTypeNamed);
		if (assignmentUsesTypeNamed != null) {
			EObject typeToUse = findTypeNamed(assignmentUsesTypeNamed);
			((EList<EObject>)type.eGet(utils.tTypesFeature)).add(typeToUse);
		}
		
	}
	
	private Widget getCorrectWidget(EObject assignment) {
		
		Pair<Arrow, Widget> assignmentPair = getPair(assignment, assignments);
		Arrow arrow = assignmentPair.getKey();
		Widget widget = assignmentPair.getValue();
		return WidgetUtils.getSecondShallowestWidget(arrow, widget);
		
	}
	
	private int getLayoutIdForAssignment(EObject assignment) {
		
		Pair<Arrow, Widget> assignmentPair = getPair(assignment, assignments);
		Arrow arrow = assignmentPair.getKey();
		Widget widget = assignmentPair.getValue();
		Widget finalWidget = WidgetUtils.getSecondShallowestWidget(arrow, widget);
		
		return finalWidget.getId().intValue();
		
	}
	
	private Pair<Arrow, Widget> getPair(EObject eObject, DoubleList<EObject, Master> doubleList) {
		
		return masterMap.get(doubleList.getMaster(eObject));
		
	}


	private EObject getTypeForAssignment(EObject assignment) {
		
		if (masterMap == null) {
			return null;
		}
		
		Master assignmentMaster = assignments.getMaster(assignment);
		Pair<Arrow, Widget> pairForAssignment = masterMap.get(assignmentMaster);
		Master correctMaster = null;
		
		for (Master type : list.getMasterIterable()) {
			
			Widget widget = masterMap.get(type).getValue();
			if (widget.equals(pairForAssignment.getValue())) {
				correctMaster = type;
				break;
			}
			
		}
		
		if (correctMaster == null) {
			return null;
		}
		
		return list.getElement(correctMaster);
		
	}
	
	protected EObject findTypeNamed(String name) {
		
		for (EObject type : list.getElementsIterable()) {
			
			String typeName = (String) type.eGet(utils.tNameFeature);
			if (typeName.equals(name)) {
				return type;
			}
			
		}
		
		throw new RuntimeException(String.format("Couldn't find Type with name %s", name));
		
	}
	
	
	protected boolean isPartOfType(EObject assignment) {
		
		if (getTypeForAssignment(assignment) == null) {
			return false;
		}

		return true;
		
	}
	
}
