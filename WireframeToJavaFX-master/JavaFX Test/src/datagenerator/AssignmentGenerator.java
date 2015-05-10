package datagenerator;

import java.util.HashMap;

import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;
import com.wireframesketcher.model.WidgetGroup;

import data.Assignment;
import data.DataFactory;

public class AssignmentGenerator {
	
	public DoubleList<Assignment, Master> assignments;
	
	public AssignmentGenerator() {
		
		assignments = new DoubleList<Assignment, Master>();
		
	}
	
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
		
		assignment.setWidget(getCorrectWidget2(assignment, map.get(master)));
		
		//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
		
		assignments.add(assignment, master);
	}
	
	private Widget getCorrectWidget2(Assignment assignment, Pair<Arrow, Widget> pair) {
		
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
