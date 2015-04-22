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
	private DataUtils utils;
	
	
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
		Resource instanceResource = utils.dataResource.getResourceSet().getResource(exisitingInstanceUri, true);
		EObject screenDataInstanceRoot = instanceResource.getContents().get(0);
		
		@SuppressWarnings("unchecked")
		EList<EObject> allAssignments = (EList<EObject>) screenDataInstanceRoot.eGet(utils.dAllAssignment2sFeature);
		
		for (EObject assignment : allAssignments) {
			
			createMapping(assignment);
			
//			if (isAssignmentPlain(assignment)) {
//				handlePlainAssignment(assignment);
//			} else if (isAssignmentUsingType(assignment) && !isAssignmentPartOfType(assignment)) {
//				handleAssignmentUsingComponent(assignment);
//			}
			
		}
		
		System.out.println("cae");
		
	}
	
	private void createMapping(EObject assignment) {
		
		String aStatement = (String) assignment.eGet(utils.a2StatementFeature);
		Widget aWidget = (Widget) assignment.eGet(utils.a2WidgetFeature);
		EObject aUseComponent = (EObject) assignment.eGet(utils.a2UseComponentFeature);
		String aUseComponentNamed = (String) assignment.eGet(utils.a2UseComponentNamedFeature);
		EObject aPartOfComponent = (EObject) assignment.eGet(utils.a2PartOfComponentFeature);
		EList<EObject> aData = (EList<EObject>) assignment.eGet(utils.a2DataFeature);
		
		if (isAssignmentPlain(assignment)) {
			//This means that it doesn't use or is a part of an interface component
			//Should therefore store a data value or several data values of type java object container.
			
			if (!isJavaObjectContainerClass(aData.get(0))) {
				throw new RuntimeException("Error! Trying to populate either a simple list or a simple element with non-simple value(s).");
			}
			
			createPlainAssignmentMapping(assignment, aData);
			
		} else if (isAssignmentUsingType(assignment) && !isAssignmentPartOfType(assignment)) {
			//These kinds of assignments should contain data since they are directly "linked" to a context.
			
			if (aData.size() == 0) {
				//Not sure whether this is the ideal reaction to no-input. If filtering, the result might be empty, which can be correct, but
				//this error message will still be thrown.
				throw new RuntimeException("Error! The base assignment for the mapping doesn't contain any data.");
			}
			
			if (isMany(aData)) {
				//TODO: This case should be implemented when advanced lists are on the menu.
			} else {
				
				EObject data = aData.get(0);	//Only a single element in the aData list, will therefore use the one at index 0
				
				EObject rootMapping = utils.dataFactory.create(utils.mappingClass);
				rootMapping.eSet(utils.mUsingComponentFeature, aUseComponent);
				((EList<EObject>) rootMapping.eGet(utils.mAssignmentPathFeature)).add(assignment);
				mappings.add(rootMapping);
				
				//This will later be the recursive part
				createCompositeAssignmentMapping2(assignment, aWidget, aUseComponent, data, rootMapping);
				
			}
			
		}
		
		//All other assignments are part of a view component and should not be the basis for creating mappings
		
		
	}
	
	private void createCompositeAssignmentMapping2(EObject assignment, Widget aWidget, EObject aUseComponent, EObject data, EObject parentMapping) {
		
		if (isJavaObjectContainerClass(data)) {
			throw new RuntimeException("Error! Can't have a plain value use type.");
		}
		
		EList<EObject> compAssignments = (EList<EObject>) aUseComponent.eGet(utils.tAssignmentsFeature);
		for (EObject cAssignment : compAssignments) {
			
			String cStatement = (String) cAssignment.eGet(utils.a2StatementFeature);
			Object value = getResultFromStatement(cStatement, data);
			
			if (isAssignmentUsingType(cAssignment)) {
				
				//Act recursively
				if ((value instanceof EObject) && isJavaObjectContainerClass((EObject) value)) {
					throw new RuntimeException("Error! Can't have a plain value use type.");
				}
				
				if (value instanceof Collection<?>) {
					throw new RuntimeException("Error! Handling list and using OCL on them is not yet implemented.");
				} else {
					
					Widget caWidget = (Widget) cAssignment.eGet(utils.a2WidgetFeature);
					Widget aComponentWidget = (Widget) aUseComponent.eGet(utils.tWidgetFeature);
					Widget correctWidget = WidgetUtils.getCorrespondingWidget(caWidget, aComponentWidget, aWidget);
					
					EObject cComponent = (EObject) cAssignment.eGet(utils.a2UseComponentFeature);
					
					EObject cMapping = utils.dataFactory.create(utils.mappingClass);
					EList<EObject> rmAssignmentPath = (EList<EObject>) parentMapping.eGet(utils.mAssignmentPathFeature);
					for (EObject ap : rmAssignmentPath) {
						((EList<EObject>) cMapping.eGet(utils.mAssignmentPathFeature)).add(ap);
					}
					((EList<EObject>) cMapping.eGet(utils.mAssignmentPathFeature)).add(cAssignment);
					cMapping.eSet(utils.mUsingComponentFeature, cComponent);
					((EList<EObject>) parentMapping.eGet(utils.mMappingsFeature)).add(cMapping);
					createCompositeAssignmentMapping2(cAssignment, correctWidget, cComponent, (EObject) value, cMapping);
					
				}
				
			} else { //Base case
				
				EObject cMapping = utils.dataFactory.create(utils.mappingClass);
				EList<EObject> rmAssignmentPath = (EList<EObject>) parentMapping.eGet(utils.mAssignmentPathFeature);
				for (EObject ap : rmAssignmentPath) {
					((EList<EObject>) cMapping.eGet(utils.mAssignmentPathFeature)).add(ap);
				}
				((EList<EObject>) cMapping.eGet(utils.mAssignmentPathFeature)).add(cAssignment);
				
				Widget cWidget = (Widget) cAssignment.eGet(utils.a2WidgetFeature);
				Widget componentWidget = (Widget) aUseComponent.eGet(utils.tWidgetFeature);
				Widget correctWidget = WidgetUtils.getCorrespondingWidget(cWidget, componentWidget, aWidget);
				cMapping.eSet(utils.mLayoutIdFeature, "#" + correctWidget.getId().intValue());
				
				cMapping.eSet(utils.mValueFeature, value);
				cMapping.eSet(utils.mStringRepresentationFeature, value.toString());
				
				((EList<EObject>) parentMapping.eGet(utils.mMappingsFeature)).add(cMapping);
				
			}
			
			
		}
		
	}

	private void createCompositeAssignmentMapping(EObject assignment,
			Widget aWidget, EObject aUseComponent, EObject data,
			EObject rootMapping) {
		
		EList<EObject> compAssignments = (EList<EObject>) aUseComponent.eGet(utils.tAssignmentsFeature);
		for (EObject cAssignment : compAssignments) {
			
			String cStatement = (String) cAssignment.eGet(utils.a2StatementFeature);
			
			if (isJavaObjectContainerClass(data)) {
				throw new RuntimeException("Error! Can't have a plain value use type.");
			}
			
			EObject cMapping = utils.dataFactory.create(utils.mappingClass);
			((EList<EObject>) cMapping.eGet(utils.mAssignmentPathFeature)).add(assignment);
			((EList<EObject>) cMapping.eGet(utils.mAssignmentPathFeature)).add(cAssignment);
			
			Widget cWidget = (Widget) cAssignment.eGet(utils.a2WidgetFeature);
			Widget componentWidget = (Widget) aUseComponent.eGet(utils.tWidgetFeature);
			Widget correctWidget = WidgetUtils.getCorrespondingWidget(cWidget, componentWidget, aWidget);
			cMapping.eSet(utils.mLayoutIdFeature, "#" + correctWidget.getId().intValue());
			
			Object value = getResultFromStatement(cStatement, data);
			cMapping.eSet(utils.mValueFeature, value);
			cMapping.eSet(utils.mStringRepresentationFeature, value.toString());
			
			((EList<EObject>) rootMapping.eGet(utils.mMappingsFeature)).add(cMapping);
			
		}
		
	}

	private Object getResultFromStatement(String cStatement, EObject data) {
		// TODO Auto-generated method stub
		return OCLHandler.parseOCLStatement(utils.resourceSet, data, cStatement);
	}

	private void createPlainAssignmentMapping(EObject assignment,
			EList<EObject> aData) {
		EObject rootMapping = utils.dataFactory.create(utils.mappingClass);
		rootMapping.eSet(utils.mLayoutIdFeature, getJavaFXIdFromWidgetId(assignment));
		((EList<EObject>) rootMapping.eGet(utils.mAssignmentPathFeature)).add(assignment);
		mappings.add(rootMapping);
		
		if (isMany(aData)) {
			//It is assumed that the list is simple, i.e, no view components will be used
			createSimplIsManyMappings(assignment, aData, rootMapping);
			
		} else {
			//It is assumed that the list will contain exactly one element of type java object container
			
			EObject jocData = aData.get(0);
			Object value = jocData.eGet(utils.jocObjectFeature);
			
			rootMapping.eSet(utils.mValueFeature, value);
			rootMapping.eSet(utils.mStringRepresentationFeature, value.toString());
			
		}
	}

	private void createSimplIsManyMappings(EObject assignment,
			EList<EObject> aData, EObject rootMapping) {
		
		rootMapping.eSet(utils.mIsListFeature, true);
		for (EObject data : aData) {
			
			if (!isJavaObjectContainerClass(data)) {
				throw new RuntimeException("Trying to populate a simple list with non-simple values");
			}
			
			Object value = data.eGet(utils.jocObjectFeature);
			
			EObject subMapping = utils.dataFactory.create(utils.mappingClass);
			subMapping.eSet(utils.mValueFeature, value);
			subMapping.eSet(utils.mStringRepresentationFeature, value.toString());
			((EList<EObject>) subMapping.eGet(utils.mAssignmentPathFeature)).add(assignment);
			((EList<EObject>) rootMapping.eGet(utils.mMappingsFeature)).add(subMapping);
			
		}
		
	}
	
	private void handlePlainAssignment(EObject assignment) {
		
		@SuppressWarnings("unchecked")
		EList<EObject> dataInAssignment = (EList<EObject>) assignment.eGet(utils.a2DataFeature);
		
		if (isMany((EList<EObject>) dataInAssignment)) {	//Treat result as a list
			EObject rootMapping = utils.dataFactory.create(utils.mappingClass);
			rootMapping.eSet(utils.mIsListFeature, true);
			mappings.add(rootMapping);
			for (EObject eObject : dataInAssignment) {
				String id = getJavaFXIdFromWidgetId(assignment);
				List<EObject> assignmentPath = new ArrayList<EObject>();
				assignmentPath.add(assignment);
				
				
				EObject mapping = utils.dataFactory.create(utils.mappingClass);
				EList<Object> jocObjects = (EList<Object>) eObject.eGet(utils.jocObjectFeature);
				
				handleListResultCorrectly(id, jocObjects.get(0), assignmentPath, rootMapping, mapping);
				
			}
		} else {			
			EObject theElement = (EObject) getFirstObjectInCollection(dataInAssignment);
			
			if (isJavaObjectContainerClass(theElement)) {
				Object value = theElement.eGet(utils.jocObjectFeature);
				value = getFirstObjectInCollection((Collection<?>) value);
				String id = getJavaFXIdFromWidgetId(assignment);
				List<EObject> assignmentPath = new ArrayList<EObject>();
				assignmentPath.add(assignment);
				EObject mapping = utils.dataFactory.create(utils.mappingClass);
				handleResultCorrectly(id, value, assignmentPath, mapping, mapping);
			} else {
				throw new RuntimeException("Something is wrong here. An EObject cannot be directly assigned without using some sort of component");
			}
		}
		
	}
	
	private void handleListResultCorrectly(String id, Object value, List<EObject> assignmentPath, EObject rootMapping, EObject mapping) {
		rootMapping.eSet(utils.mLayoutIdFeature, id);
		EList<EObject> mappings = (EList<EObject>) rootMapping.eGet(utils.mMappingsFeature);
		mappings.add(mapping);
		
		mapping.eSet(utils.mValueFeature, value);
		mapping.eSet(utils.mStringRepresentationFeature, value.toString());
		
		for (EObject eObject : assignmentPath) {
			((EList<EObject>) mapping.eGet(utils.mAssignmentPathFeature)).add(eObject);
			((EList<EObject>) rootMapping.eGet(utils.mAssignmentPathFeature)).add(eObject);
		}
		
	}
	
	private void handleResultCorrectly(String id, Object value, List<EObject> assignmentPath, EObject rootMapping, EObject mapping) {
		
		mapping.eSet(utils.mLayoutIdFeature, id);
		
		mapping.eSet(utils.mValueFeature, value);
		mapping.eSet(utils.mStringRepresentationFeature, value.toString());
		
		for (EObject eObject : assignmentPath) {
			((EList<EObject>) mapping.eGet(utils.mAssignmentPathFeature)).add(eObject);
		}
		
		mappings.add(rootMapping);
	}

	private void handleAssignmentUsingComponent(EObject mainAssignment) {
		
		Widget mainAssignmentWidget = (Widget) mainAssignment.eGet(utils.a2WidgetFeature);
		List<EObject> assignmentPath = new ArrayList<>();
		EObject mapping = utils.dataFactory.create(utils.mappingClass);
		handleRecursively(mainAssignment, null, mainAssignmentWidget, assignmentPath, mapping, mapping);
		
	}

	private void handleRecursively(EObject parentAssignment, Object prevResult, Widget corrWidgetInMainAssignment, 
			List<EObject> assignmentPath, EObject rootMapping, EObject mapping) {
		
		
		List<EObject> newAssignmentPath = new ArrayList<EObject>();
		newAssignmentPath.addAll(assignmentPath);
		newAssignmentPath.add(parentAssignment);
		for (EObject eObject : newAssignmentPath) {
			((EList<EObject>) mapping.eGet(utils.mAssignmentPathFeature)).add(eObject);
		}
		if (isAssignmentUsingType(parentAssignment)) {
			
			EObject parentAssignmentComponent = (EObject) parentAssignment.eGet(utils.a2UseComponentFeature);
			mapping.eSet(utils.mUsingComponentFeature, parentAssignmentComponent);
			
			Widget parentAssignmentWidget = (Widget) parentAssignment.eGet(utils.a2WidgetFeature);
			@SuppressWarnings("unchecked")
			EList<EObject> parentData = (EList<EObject>) parentAssignment.eGet(utils.a2DataFeature);
			if (isMany(parentData)) {	//Lists are not supported yet
				EObject componentForMainAssignment = (EObject) parentAssignment.eGet(utils.a2UseComponentFeature);
				Widget componentWidget = (Widget) componentForMainAssignment.eGet(utils.tWidgetFeature);
				
				@SuppressWarnings("unchecked")
				EList<EObject> assignmentsForType = (EList<EObject>) componentForMainAssignment.eGet(utils.tAssignmentsFeature);
				for (EObject compAssignment : assignmentsForType) {
					
					Widget saWidget = (Widget) compAssignment.eGet(utils.a2WidgetFeature);
					Widget corrWidget = WidgetUtils.getCorrespondingWidget(saWidget, componentWidget, parentAssignmentWidget);
					
					String compStatement = (String) compAssignment.eGet(utils.a2StatementFeature);
					compStatement = modifyCompstatementToFitList(compStatement);
					Object result = OCLHandler.parseOCLStatement(parentAssignment.eResource().getResourceSet(), parentAssignment, compStatement);
					
					EObject newMapping = utils.dataFactory.create(utils.mappingClass);
					@SuppressWarnings("unchecked")
					EList<EObject> mappingsInMapping = (EList<EObject>) mapping.eGet(utils.mMappingsFeature);
					mappingsInMapping.add(newMapping);
					
					handleRecursively(compAssignment, result, corrWidget, newAssignmentPath, rootMapping, newMapping);
					
				}
			} else {	//Only one element
				Widget parentComponentWidget = (Widget) parentAssignmentComponent.eGet(utils.tWidgetFeature);
				
				@SuppressWarnings("unchecked")
				EList<EObject> componentAssignments = (EList<EObject>) parentAssignmentComponent.eGet(utils.tAssignmentsFeature);
				for (EObject compAssignment : componentAssignments) {
					
					Widget saWidget = (Widget) compAssignment.eGet(utils.a2WidgetFeature);
					Widget corrWidget  = WidgetUtils.getCorrespondingWidget(saWidget, parentComponentWidget, corrWidgetInMainAssignment);
					
					String compStatement = (String) compAssignment.eGet(utils.a2StatementFeature);
					EObject parentDataValue = parentData.get(0);
					
					Object result = null;
					if (prevResult == null) {
						result = OCLHandler.parseOCLStatement(utils.resourceSet, parentDataValue, compStatement);
					} else {
						EObject temp = (EObject) prevResult;
						result = OCLHandler.parseOCLStatement(utils.resourceSet, temp, compStatement);
					}
					
					EObject newMapping = utils.dataFactory.create(utils.mappingClass);
					@SuppressWarnings("unchecked")
					EList<EObject> mappingsInMapping = (EList<EObject>) mapping.eGet(utils.mMappingsFeature);
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
		
		EObject usesType = (EObject) eObject.eGet(utils.a2UseComponentFeature);
		return usesType != null;
		
	}
	
	private boolean isAssignmentPartOfType(EObject eObject) {
		
		EObject partOfType = (EObject) eObject.eGet(utils.a2PartOfComponentFeature);
		return partOfType != null;
		
	}
	
	private boolean isMany(EList<EObject> list) {
		
		if (list.size() > 1) {
			return true;
		}
		
		return false;
		
	}
	
	private boolean isJavaObjectContainerClass(EObject eObject) {
		
		return eObject.eClass().equals(utils.javaObjectContainerClass);
		
	}
	
	private String getJavaFXIdFromWidgetId(EObject assignment) {
		
		Widget widget = (Widget) assignment.eGet(utils.a2WidgetFeature);
		int layoutId = widget.getId().intValue();
		String id = "#" + layoutId;
		return id;
		
	}
	
	private String modifyCompstatementToFitList(String compStatement) {
		
		return "data->" + compStatement;
		
	}
		
}
