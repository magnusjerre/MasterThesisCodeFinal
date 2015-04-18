package datagenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.emf.common.util.EList;
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
		
		EObject assignmentObject = utils.dataFactory.create(utils.assignment2Class);
		
		String statement = strings[0].trim();
		assignmentObject.eSet(utils.a2StatementFeature, statement);
		
		if (strings.length == 2) {
			assignmentObject.eSet(utils.a2UseComponentNamedFeature, strings[1].trim());
		}
		
		assignmentObject.eSet(utils.a2WidgetFeature, getCorrectWidget(assignmentObject, masterMap.get(master)));
		
		//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
		
		assignments.add(assignmentObject, master);
		
	}
	
	public void doSetup() {
		
		doSetupNormalAssignmentsOnly();
		doSetupNormalAssignmentsUsingComponent();
		doSetupAssignmentsPartOfComponent();
		
	}
	
	
	
	private void doSetupNormalAssignmentsOnly() {
		
		for (EObject assignment : assignments.getElementsIterable()) {
			
			if (!isAssignmentUsingComponent(assignment) && !isAssignmentPartOfComponent(assignment)) {
				attachData(assignment);
			}
			
		}
		
	}
	
	private void attachData(EObject assignment) {
		
		String statement = (String) assignment.eGet(utils.a2StatementFeature);
		String contextName = DataUtils.getParentName(statement);
		EObject context = DataUtils.getInstance().getContextNamed(contextName, contexts);
		statement = statement.replaceFirst(contextName, "self");
		Object data = ContextGenerator.getResult(statement, context);
		
		EList<Object> dataList = (EList<Object>) assignment.eGet(utils.a2DataFeature);
		if (data instanceof Collection<?>) {
			ContextGenerator.fillListWithCorrectFormat((Collection<Object>) data, dataList);
		} else {
			Object result = data;
			if (result instanceof EObject) {
				dataList.add(result);
			} else {
				EObject container = utils.dataFactory.create(utils.javaObjectContainerClass);
				String objectType = result.getClass().getSimpleName();
				container.eSet(utils.jocTypeFeature, objectType);
				EList<Object> containerList = (EList<Object>) container.eGet(utils.jocObjectFeature);
				containerList.add(result);
				dataList.add(container);
			}
		}
		
	}

	private void doSetupNormalAssignmentsUsingComponent() {
		
		for (EObject assignment : assignments.getElementsIterable()) {
			
			if (isAssignmentUsingComponent(assignment) 
					&& !isAssignmentPartOfComponent(assignment)) {
				attachComponent(assignment);
				attachData(assignment);
			}
			
		}
		
	}

	private void doSetupAssignmentsPartOfComponent() {
		
		for (EObject assignment : assignments.getElementsIterable()) {
			
			if (isAssignmentPartOfComponent(assignment) 
					&& isAssignmentUsingComponent(assignment)) {
				attachComponent(assignment);
			}
			
		}
		
	}
	
	private void attachComponent(EObject assignment) {
		String componentName = (String) assignment.eGet(utils.a2UseComponentNamedFeature);
		EObject component = TypeGenerator.getInstance().findTypeNamed(componentName);
		assignment.eSet(utils.a2UseComponentFeature, component);
	}
	
	private Widget getCorrectWidget(EObject assignmentObject, Pair<Arrow, Widget> pair) {
		
		if (isAssignmentUsingComponent(assignmentObject)) {
			return pair.getValue();
		} else {
			return WidgetUtils.getDeepestWidget(pair.getKey(), pair.getValue());
		}
		
	}
	
	private static boolean isAssignmentUsingComponent(EObject eObject) {
		
		String usesComponent = (String) eObject.eGet(DataUtils.getInstance().a2UseComponentNamedFeature);
		return usesComponent != null;
		
	}

	/**
	 * Only useable after all types for the screen file have been processed.
	 * @param eObject
	 * @return
	 */
	protected static boolean isAssignmentPartOfComponent(EObject eObject) {
		
		EObject partOfType = (EObject) eObject.eGet(DataUtils.getInstance().a2PartOfComponentFeature);
		return partOfType != null;
		
	}

}
