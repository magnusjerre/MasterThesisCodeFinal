package datagenerator;

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
	
	public static AssignmentGenerator getInstance() {
		
		if (instance == null)
			instance = new AssignmentGenerator();
		
		return instance;
		
	}
	
	private AssignmentGenerator() {
		
		utils = DataUtils.getInstance();
		assignments = new DoubleList<EObject, Master>();
		
	}
	
	public void clear() {
		
		utils = DataUtils.getInstance();
		
		assignments.clear();
		
	}
	
	public void generateDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		
		if (strings.length < 1 || strings.length > 2) {
			throw new RuntimeException("The decorator is not well formed, it contains either too many or too few lines.\n"
					+ "The Assignment decorator allows either one or two lines.");
		}
		
		EObject assignmentObject = utils.dataFactory.create(utils.assignment2Class);
		
		String statement = strings[0].trim();
		assignmentObject.eSet(utils.a2StatementFeature, statement);
		
		if (strings.length == 2) {
			assignmentObject.eSet(utils.a2UseComponentNamedFeature, strings[1].trim());
		}
		
		assignmentObject.eSet(utils.a2WidgetFeature, getCorrectWidget(assignmentObject, map.get(master)));
		
		//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
		
		assignments.add(assignmentObject, master);
		
	}
	
	private Widget getCorrectWidget(EObject assignmentObject, Pair<Arrow, Widget> pair) {
		
		if (isAssignmentUsingComponent(assignmentObject)) {
			return pair.getValue();
		} else {
			return WidgetUtils.getDeepestWidget(pair.getKey(), pair.getValue());
		}
		
	}
	
	private boolean isAssignmentUsingComponent(EObject eObject) {
		
		String usesComponent = (String) eObject.eGet(utils.a2UseComponentNamedFeature);
		return usesComponent != null;
		
	}

	/**
	 * Only useable after all types for the screen file have been processed.
	 * @param eObject
	 * @return
	 */
	protected boolean isAssignmentPartOfComponent(EObject eObject) {
		
		EObject partOfType = (EObject) eObject.eGet(utils.a2PartOfComponentFeature);
		return partOfType != null;
		
	}

}
