package datagenerator;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Position;
import com.wireframesketcher.model.Widget;
import com.wireframesketcher.model.WidgetGroup;

public class TypeGenerator {
	
	private static TypeGenerator instance = null;
	private DataUtils utils;
	
	//types and typesAsMaster are tightly coupled 
	public ArrayList<EObject> types;
	public ArrayList<Master> typesAsMaster;
	public HashMap<Master, Pair<Arrow, Widget>> masterMap;

	public ArrayList<EObject> assignments;
	public ArrayList<Master> assignmentsAsMaster;
	
	
	private TypeGenerator() {
		
		utils = DataUtils.getInstance();
		types = new ArrayList<EObject>();
		typesAsMaster = new ArrayList<Master>();
		assignments = AssignmentGenerator.getInstance().assignments;
		assignmentsAsMaster = AssignmentGenerator.getInstance().assignmentsAsMaster;
		
	}
	
	public static TypeGenerator getInstance() {
		
		if (instance == null)
			instance = new TypeGenerator();
		
		return instance;
		
	}
	
	public void clear() {
		
		masterMap = null;
		types.clear();
		typesAsMaster.clear();
		
	}
	
	public void generateDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		
		if (masterMap == null) {
			masterMap = map;
		}
		
		if (strings.length > 1 || strings.length < 1) {
			throw new RuntimeException(String.format("Illegal number of lines in Type decorator. First line states: %s", strings[0]));
		}
		
		String name = strings[0].trim();

		EObject assignmentObject = utils.dataFactory.create(utils.typeClass);
		assignmentObject.eSet(utils.tNameFeature, name);
		//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
		
		types.add(assignmentObject);
		typesAsMaster.add(master);
		
	}
	
	public void setupAssignmentReferences() {
		
		for (EObject assignment : assignments) {
			
			if (AssignmentGenerator.getInstance().isTypeAssignment(assignment)) {
				EObject type = getTypeAssignmentBelongsTo(assignment);
				setupConnection(assignment, type);
			}
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void setupConnection(EObject assignment, EObject type) {

		((EList<EObject>)type.eGet(utils.tAssignmentsFeature)).add(assignment);
		assignment.eSet(utils.aPartOf, type);
		assignment.eSet(utils.aLayoutIDFeature, getElementAssigned(assignment));
		
	}
	
	private int getElementAssigned(EObject assignment) {
		
		Pair<Arrow, Widget> assignmentPair = getPairForAssignment(assignment);
		Arrow arrow = assignmentPair.getKey();
		WidgetGroup widgetGroup = (WidgetGroup) assignmentPair.getValue();
		Widget finalWidget = getWidgetPointedToByArrow(arrow, widgetGroup);
		
		return finalWidget.getId().intValue();
		
	}
	
	private Pair<Arrow, Widget> getPairForAssignment(EObject assignment) {
		return getPairForEObject(assignment, assignments, assignmentsAsMaster);
	}
	
	private Pair<Arrow, Widget> getPairForEObject(EObject eObject, ArrayList<EObject> eList, ArrayList<Master> masterList) {
		
		int pos = eList.indexOf(eObject);
		Master master = masterList.get(pos);
		return masterMap.get(master);
		
	}

	private Widget getWidgetPointedToByArrow(Arrow arrow, WidgetGroup widgetGroup) {
		
		Point arrowHead = getArrowHeadPosition(arrow);
		return getWigetForArrowHead(arrowHead, widgetGroup, new Point(widgetGroup.getX(),widgetGroup.getY()));
			
	}
	
	private Point getArrowHeadPosition(Arrow arrow) {
		
		if (arrow.isLeft() == arrow.isRight()) {
			throw new RuntimeException("There is an error with the arrow. It's either not pointing, or it's pointing both ways.");
		}
		
		//Topmost and Leftmost point of line
		int x = arrow.getX();
		int y = arrow.getY();
		
		if (arrow.isRight() && arrow.getDirection() == Position.BOTTOM){
			x += arrow.getMeasuredWidth();
		} else if (arrow.isRight()){
			x += arrow.getMeasuredWidth();
			y += arrow.getMeasuredHeight();
		} else if (arrow.getDirection() == Position.BOTTOM){
			y += arrow.getMeasuredHeight();
		}
		
		return new Point(x,y);
		
	}
	
	private Widget getWigetForArrowHead(Point arrowHead, WidgetGroup widgetGroup, Point offset) {
		
		for (Widget widget : widgetGroup.getWidgets()) {
			
			if (pointIsInsideRectangle(arrowHead, widget.getX() + offset.x, widget.getY() + offset.y, widget.getMeasuredWidth(), widget.getMeasuredHeight())) {
				if (widget instanceof WidgetGroup) {
					return getWigetForArrowHead(arrowHead, (WidgetGroup) widget, new Point(widget.getX(), widget.getY()));
				} else {
					return widget;
				}
			}
			
		}
		
		throw new RuntimeException("No widget found for arrow head position.");
		
	}
	
	private boolean pointIsInsideRectangle(Point point, int left, int top, int width, int height) {
		
		int right = left + width;
		int bottom = top + height;
		
		if (point.x < left) {
			return false;
		}
		
		if (point.x > right) {
			return false;
		}
		
		if (point.y < top) {
			return false;
		}
		
		if (point.y > bottom) {
			return false;
		}
		
		return true;
		
	}

	private EObject getTypeAssignmentBelongsTo(EObject assignment) {

		Master typeMaster = getTypeMasterForAssignment(assignment);
		int posOfType = typesAsMaster.indexOf(typeMaster);
		return types.get(posOfType);
		
	}
	
	private Master getTypeMasterForAssignment(EObject assignment) {
		
		int pos = assignments.indexOf(assignment);
		Master assignmentMaster = assignmentsAsMaster.get(pos);
		
		Pair<Arrow, Widget> assignmentPair = masterMap.get(assignmentMaster);
		Master correctMaster = null;
		
		for (Master typeMaster : typesAsMaster) {
			
			if (masterMap.get(typeMaster).getValue().equals(assignmentPair.getValue())) {
				correctMaster = typeMaster;
			}
			
		}
		
		return correctMaster;
		
	}
	
	private class Point {
		
		public final int x, y;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString() {
			return String.format("{x: , y: }", x, y);
		}
	}
	
	protected EObject findTypeNamed(String name) {
		
		for (EObject type : types) {
			
			String typeName = (String) type.eGet(utils.tNameFeature);
			if (typeName.equals(name)) {
				return type;
			}
			
		}
		
		throw new RuntimeException(String.format("Couldn't find Type with name %s", name));
		
	}
	
}
