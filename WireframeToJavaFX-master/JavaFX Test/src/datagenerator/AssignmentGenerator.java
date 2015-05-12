package datagenerator;

import java.util.HashMap;

import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;
import com.wireframesketcher.model.WidgetGroup;

import data.Assignment;
import data.DataFactory;

/**
 * Responsible for creating and storing all the Assignment decorators for a specific screen file.
 * @author Magnus Jerre
 *
 */
public class AssignmentGenerator {
	
	public DoubleList<Assignment, Master> assignments = new DoubleList<>();
	
	public void generateDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		
		if (strings.length < 1 || strings.length > 2) {
			throw new RuntimeException("The decorator is not well formed, it contains either too many or too few lines.\n"
					+ "The Assignment decorator allows either one or two lines.");
		}
		
		Assignment assignment = DataFactory.eINSTANCE.createAssignment();
		
		String statement = strings[0].trim();
		assignment.setStatement(statement);
		
		if (strings.length == 2) {
			assignment.setUsingViewComponentNamed(strings[1].trim());
		}
		
		assignment.setWidget(getCorrectWidget(assignment, map.get(master)));
		
		//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
		
		assignments.add(assignment, master);
	}
	
	/**
	 * Depending on what type of widget the Assignment is pointing at, a widget is returned. 
	 * If the assignment point at a widget group named list, the widget group itself will be returned.
	 * If the assignment is using a ViewComponent, the widget group the assignment is pointing to will be returned.
	 * Otherwise the widget returned will be the deepest widget inside the widget group. If it isn't pointing at
	 * a widget group, the widget pointed to will be returned.
	 * 
	 * @param assignment
	 * @param pair
	 * @return The widget the assignment is pointing to.
	 */
	private Widget getCorrectWidget(Assignment assignment, Pair<Arrow, Widget> pair) {
		
		if (pair.getValue() instanceof WidgetGroup) {
			WidgetGroup widgetGroup = (WidgetGroup) pair.getValue();
			if ("list".equalsIgnoreCase(widgetGroup.getName())) {
				return widgetGroup;
			}
		}
		
		if (assignment.isUsingViewComponent()) {
			return pair.getValue();
		} 
		
		return WidgetUtils.getDeepestWidget(pair.getKey(), pair.getValue());
		
	}

}
