package datagenerator;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Position;
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
		if (strings.length == 2) {
			assignmentObject.eSet(utils.aUseTypeName, strings[1].trim());
		}
		assignmentObject.eSet(utils.aLayoutIDFeature, getId(assignmentObject, map.get(master)));
		
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
				String specificStatement = (String) assignment.eGet(utils.aSpecificStatementFeature);
				assignment.eSet(utils.aStatementFeature,  specificStatement);
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
		
		return TypeGenerator.getInstance().isPartOfType(eObject);
		
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
	
	private int getId(EObject assignmentObject, Pair<Arrow, Widget> pair) {
		
		if (isAssignmentPlain(assignmentObject)) {
			return getDeepestWidget(pair.getKey(), pair.getValue()).getId().intValue();
		} else if (isAssignmentUsingType(assignmentObject)){
			return pair.getValue().getId().intValue();
		} else {
			return getShallowestWidget(pair.getKey(), pair.getValue()).getId().intValue();
		}
		
	}
	
	private Widget getShallowestWidget(Arrow arrow, Widget widget) {
		
		if (isNotWidgetGroup(widget)) {
			return widget;
		}
		
		Point arrowHead = getArrowHeadPosition(arrow);
		WidgetGroup widgetGroup = (WidgetGroup) widget;
		Point offset = new Point(widgetGroup.getX(), widgetGroup.getY());
		for (Widget w : widgetGroup.getWidgets()) {
			
			int x = w.getX() + offset.x;
			int y = w.getY() + offset.y;
			if (pointIsInsideRectangle(arrowHead, x, y, w.getMeasuredWidth(), w.getMeasuredHeight())) {
				return w;
			}
		}
		
		throw new RuntimeException("Didn't find a match for the arrow location");
		
	}
	
	private Widget getDeepestWidget(Arrow arrow, Widget widget) {
		
		Point arrowHead = getArrowHeadPosition(arrow);
		return getDeepestWidget(arrowHead, widget, new Point(widget.getX(), widget.getY()));
		
	}
	
	private Widget getDeepestWidget(Point arrowHead, Widget widget, Point offset) {
		
		if (isNotWidgetGroup(widget)) {
			return widget;
		}
		
		WidgetGroup widgetGroup = (WidgetGroup) widget;
		Widget output = null;
		for (Widget w : widgetGroup.getWidgets()) {
			
			int x = w.getX() + offset.x;
			int y = w.getY() + offset.y;
			if (pointIsInsideRectangle(arrowHead, x, y, w.getMeasuredWidth(), w.getMeasuredHeight())) {
				output = w;
				break;
			}
			
		}
		
		if (isNotWidgetGroup(output)) {
			return output;
		}
		
		offset = new Point(offset.x + output.getX(), offset.y + output.getY());
		return getDeepestWidget(arrowHead, output, offset);
		
		
	}
	
	private boolean isNotWidgetGroup(Widget widget) {
		return !(widget instanceof WidgetGroup);
	}
	
	private Widget getWidgetPointedToByArrow(Arrow arrow, WidgetGroup widgetGroup) {
		
		Point arrowHead = getArrowHeadPosition(arrow);
		return getWidgetForArrowHead(arrowHead, widgetGroup, new Point(widgetGroup.getX(),widgetGroup.getY()));
			
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
	
	private Widget getWidgetForArrowHead(Point arrowHead, WidgetGroup widgetGroup, Point offset) {
		
		for (Widget widget : widgetGroup.getWidgets()) {
			
			int x = widget.getX() + offset.x;
			int y = widget.getY() + offset.y;
			if (pointIsInsideRectangle(arrowHead, x, y, widget.getMeasuredWidth(), widget.getMeasuredHeight())) {
				return widget;
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
	
	private class Point {
		
		public final int x, y;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString() {
			return String.format("{x: %d , y: %d }", x, y);
		}
	}
	
	
	private static boolean isAssignmentPlain(EObject eObject) {
		
		if (isAssignmentPartOfType(eObject)) {
			return false;
		}
		
		if (isAssignmentUsingType(eObject)) {
			return false;
		}
		
		return true;
		
	}
	
	private static boolean isAssignmentUsingType(EObject eObject) {
		
		String usesType = (String) eObject.eGet(DataUtils.getInstance().aUseTypeName);
		return usesType != null;
		
	}
	
	private static boolean isAssignmentPartOfType(EObject eObject) {
		
		EObject partOfType = (EObject) eObject.eGet(DataUtils.getInstance().aPartOf);
		return partOfType != null;
		
	}
}
