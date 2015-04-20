package datagenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;

public class MappingGenerator {
	
	private static MappingGenerator instance = null;
	private DataUtils utils = DataUtils.getInstance();
	
	
	public ArrayList<EObject> mappings;
	
	public HashMap<Master, Pair<Arrow, Widget>> masterMap = null;
	
	public static MappingGenerator getInstance() {
		
		if (instance == null)
			instance = new MappingGenerator();
		
		return instance;
		
	}
	
	private MappingGenerator() {
		
		utils = DataUtils.getInstance();
		mappings = new ArrayList<EObject>();
		
	}
	
	public void setContext(ArrayList<EObject> contexts) {
		
		this.mappings = contexts;
		
	}
	
	public void clear() {
		
		masterMap = null;
		mappings.clear();
		
	}
	
	public void assignValues(String xmiFileLocation) {
		
		URI exisitingInstanceUri = URI.createFileURI(xmiFileLocation);
		Resource instanceResource = DataUtils.getInstance().dataResource.getResourceSet().getResource(exisitingInstanceUri, true);
		EObject screenDataInstanceRoot = instanceResource.getContents().get(0);
		
		@SuppressWarnings("unchecked")
		EList<EObject> allAssignments = (EList<EObject>) screenDataInstanceRoot.eGet(DataUtils.getInstance().dAllAssignment2sFeature);
		
		for (EObject assignment : allAssignments) {
			
			if (isAssignmentPlain(assignment)) {
				handlePlainAssignment(assignment);
			} else if (isAssignmentUsingType(assignment) && !isAssignmentPartOfType(assignment)) {
				handleAssignmentUsingComponent(assignment);
			}
			
		}
		
	}
	
	private void handlePlainAssignment(EObject assignment) {
		
		@SuppressWarnings("unchecked")
		EList<EObject> dataInAssignment = (EList<EObject>) assignment.eGet(DataUtils.getInstance().a2DataFeature);
		
		if (isMany((EList<EObject>) dataInAssignment)) {
			throw new RuntimeException("Something is wrong here, no list implementation for lists exists yet.");
		}
		
		EObject theElement = (EObject) getFirstObjectInCollection(dataInAssignment);
		
		if (isJavaObjectContainerClass(theElement)) {
			Object value = theElement.eGet(DataUtils.getInstance().jocObjectFeature);
			value = getFirstObjectInCollection((Collection<?>) value);
			String id = getJavaFXIdFromWidgetId(assignment);
			List<EObject> assignmentPath = new ArrayList<EObject>();
			assignmentPath.add(assignment);
			EObject mapping = DataUtils.getInstance().dataFactory.create(DataUtils.getInstance().mappingClass);
			handleResultCorrectly(id, value, assignmentPath, mapping, mapping);
		} else {
			throw new RuntimeException("Something is wrong here. An EObject cannot be directly assigned without using some sort of component");
		}
	}
	
	private void handleResultCorrectly(String id, Object value, List<EObject> assignmentPath, EObject rootMapping, EObject mapping) {
		
		mapping.eSet(DataUtils.getInstance().mLayoutIdFeature, id);
		
		mapping.eSet(utils.mValueFeature, value);
		mapping.eSet(utils.mStringRepresentationFeature, value.toString());
		
		for (EObject eObject : assignmentPath) {
			((EList<EObject>) mapping.eGet(DataUtils.getInstance().mAssignmentPathFeature)).add(eObject);
		}
		
		mappings.add(rootMapping);
	}

	private void handleAssignmentUsingComponent(EObject mainAssignment) {
		
		Widget mainAssignmentWidget = (Widget) mainAssignment.eGet(DataUtils.getInstance().a2WidgetFeature);
		List<EObject> assignmentPath = new ArrayList<>();
		EObject mapping = DataUtils.getInstance().dataFactory.create(DataUtils.getInstance().mappingClass);
		handleRecursively(mainAssignment, null, mainAssignmentWidget, assignmentPath, mapping, mapping);
		
	}

	private void handleRecursively(EObject parentAssignment, Object prevResult, Widget corrWidgetInMainAssignment, 
			List<EObject> assignmentPath, EObject rootMapping, EObject mapping) {
		
		
		List<EObject> newAssignmentPath = new ArrayList<EObject>();
		newAssignmentPath.addAll(assignmentPath);
		newAssignmentPath.add(parentAssignment);
		for (EObject eObject : newAssignmentPath) {
			((EList<EObject>) mapping.eGet(DataUtils.getInstance().mAssignmentPathFeature)).add(eObject);
		}
		if (isAssignmentUsingType(parentAssignment)) {
			
			EObject component = (EObject) parentAssignment.eGet(utils.a2UseComponentFeature);
			mapping.eSet(utils.mUsingComponentFeature, component);
			
			Widget mainAssignmentWidget = (Widget) parentAssignment.eGet(DataUtils.getInstance().a2WidgetFeature);
			@SuppressWarnings("unchecked")
			EList<EObject> data = (EList<EObject>) parentAssignment.eGet(DataUtils.getInstance().a2DataFeature);
			if (isMany(data)) {	//Lists are not supported yet
				EObject componentForMainAssignment = (EObject) parentAssignment.eGet(DataUtils.getInstance().a2UseComponentFeature);
				Widget componentWidget = (Widget) componentForMainAssignment.eGet(DataUtils.getInstance().tWidgetFeature);
				
				@SuppressWarnings("unchecked")
				EList<EObject> assignmentsForType = (EList<EObject>) componentForMainAssignment.eGet(DataUtils.getInstance().tAssignmentsFeature);
				for (EObject compAssignment : assignmentsForType) {
					
					Widget saWidget = (Widget) compAssignment.eGet(DataUtils.getInstance().a2WidgetFeature);
					Widget corrWidget = WidgetUtils.getCorrespondingWidget(saWidget, componentWidget, mainAssignmentWidget);
					
					String compStatement = (String) compAssignment.eGet(DataUtils.getInstance().a2StatementFeature);
					compStatement = modifyCompstatementToFitList(compStatement);
					Object result = OCLHandler.parseOCLStatement(parentAssignment.eResource().getResourceSet(), parentAssignment, compStatement);
					
					EObject newMapping = DataUtils.getInstance().dataFactory.create(DataUtils.getInstance().mappingClass);
					@SuppressWarnings("unchecked")
					EList<EObject> mappingsInMapping = (EList<EObject>) mapping.eGet(DataUtils.getInstance().mMappingsFeature);
					mappingsInMapping.add(newMapping);
					
					handleRecursively(compAssignment, result, corrWidget, newAssignmentPath, rootMapping, newMapping);
					
				}
			} else {
				
				EObject componentForMainAssignment = (EObject) parentAssignment.eGet(DataUtils.getInstance().a2UseComponentFeature);
				Widget componentWidget = (Widget) componentForMainAssignment.eGet(DataUtils.getInstance().tWidgetFeature);
				
				@SuppressWarnings("unchecked")
				EList<EObject> assignmentsForType = (EList<EObject>) componentForMainAssignment.eGet(DataUtils.getInstance().tAssignmentsFeature);
				for (EObject compAssignment : assignmentsForType) {
					
					Widget saWidget = (Widget) compAssignment.eGet(DataUtils.getInstance().a2WidgetFeature);
					Widget corrWidget  = WidgetUtils.getCorrespondingWidget(saWidget, componentWidget, corrWidgetInMainAssignment);
					
					String compStatement = (String) compAssignment.eGet(DataUtils.getInstance().a2StatementFeature);
					EObject dataValue = (EObject) getFirstObjectInCollection(data);
					
					Object result = null;
					if (prevResult == null) {
						result = OCLHandler.parseOCLStatement(dataValue.eResource().getResourceSet(), dataValue, compStatement);
					} else {
						EObject temp = (EObject) prevResult;
						result = OCLHandler.parseOCLStatement(temp.eResource().getResourceSet(), temp, compStatement);
					}
					
					EObject newMapping = DataUtils.getInstance().dataFactory.create(DataUtils.getInstance().mappingClass);
					@SuppressWarnings("unchecked")
					EList<EObject> mappingsInMapping = (EList<EObject>) mapping.eGet(DataUtils.getInstance().mMappingsFeature);
					mappingsInMapping.add(newMapping);
					
					handleRecursively(compAssignment, result, corrWidget, newAssignmentPath, rootMapping, newMapping);
					
				}
			}
			
			
		} else {	//Base case
			
			String layoutId = "#" + corrWidgetInMainAssignment.getId().intValue();
			handleResultCorrectly(layoutId, prevResult, newAssignmentPath, rootMapping, mapping);
			
		}
		
	}
	
	private Object getFirstObjectInCollection(Collection<?> objects) {
		
		Iterator<?> iterator = objects.iterator();
		if (iterator.hasNext()) {
			return iterator.next();
		}
		
		return null;
		
	}
		
	private boolean isAssignmentPlain(EObject eObject) {
		
		if (isAssignmentPartOfType(eObject)) {
			return false;
		}
		
		if (isAssignmentUsingType(eObject)) {
			return false;
		}
		
		return true;
		
	}
	
	private boolean isAssignmentUsingType(EObject eObject) {
		
		EObject usesType = (EObject) eObject.eGet(DataUtils.getInstance().a2UseComponentFeature);
		return usesType != null;
		
	}
	
	private boolean isAssignmentPartOfType(EObject eObject) {
		
		EObject partOfType = (EObject) eObject.eGet(DataUtils.getInstance().a2PartOfComponentFeature);
		return partOfType != null;
		
	}
	
	private boolean isMany(EList<EObject> list) {
		
		if (list.size() > 1) {
			return true;
		}
		
		return false;
		
	}
	
	private boolean isJavaObjectContainerClass(EObject eObject) {
		
		return eObject.eClass().equals(DataUtils.getInstance().javaObjectContainerClass);
		
	}
	
	private String getJavaFXIdFromWidgetId(EObject assignment) {
		
		Widget widget = (Widget) assignment.eGet(DataUtils.getInstance().a2WidgetFeature);
		int layoutId = widget.getId().intValue();
		String id = "#" + layoutId;
		return id;
		
	}
	
	private String modifyCompstatementToFitList(String compStatement) {
		
		return "data->" + compStatement;
		
	}
		
}
