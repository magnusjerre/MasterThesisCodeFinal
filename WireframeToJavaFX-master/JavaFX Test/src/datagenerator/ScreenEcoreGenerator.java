package datagenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.ocl.ecore.impl.CollectionTypeImpl;
import org.eclipse.ocl.ecore.impl.PrimitiveTypeImpl;

import application.Constants;

import com.wireframesketcher.model.Widget;

import data.Assignment;
import data.Context;
import data.ViewComponent;

public class ScreenEcoreGenerator {
	
	private static final String ANNOTATION_SOURCE = "wireframe";
	private int counter;
	private String ASSIGNMENT_PREFIX = "a";
	
	List<Context> contexts;
	List<Assignment> assignments;
	List<ViewComponent> types;
	
	protected EPackage screenPackage;
	private ResourceSet resSet;
	
	private Map<String, Map<String, EClassifier>> classesForXmis;
	
	public ScreenEcoreGenerator(List<Context> contexts, List<Assignment> assignments, List<ViewComponent> types) {
		
		this.contexts = contexts;
		this.assignments = assignments;
		this.types = types;
		
		counter = 1;
		classesForXmis = new HashMap<String, Map<String,EClassifier>>();
		
	}
	
	public void generateEcoreForScreen(String screenName) {
		
		if (screenName.endsWith(".screen")) {
			screenName = screenName.replace(".screen", "");
		}
		
		
		EClass screenClass = EcoreFactory.eINSTANCE.createEClass();
		screenClass.setName(screenName);
		
		screenPackage = createScreenPackage(screenName, screenClass);
		
		resSet = new ResourceSetImpl();
		resSet.getPackageRegistry().put(screenPackage.getNsURI(), screenPackage);
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		addContextsTo(screenClass);
		
		addViewComponentsToPackage(screenPackage);
		
		addAssignmentsTo(screenClass);
		
		saveAsResource(screenPackage);
		
	}

	private void addAssignmentsTo(EClass screenClass) {
		
		for (Assignment assignment : assignments) {
			addAssignmentTo(screenClass, assignment);
		}
	}
	
	private void addAssignmentTo(EClass screenClass, Assignment assignment) {
		
		if (assignment.isPartOfViewComponent()) {
			return;
		}

		String statement = assignment.getStatement();
		
		EClassifier eClassifier = OCLHandler.getClassifierForStatement2(resSet, screenClass, statement);
		if (eClassifier == null) {
			throw new RuntimeException("Illegal statement, aborting...: " + statement);
		}
		
		EStructuralFeature eFeature = createFeatureFromClassifier(eClassifier);
		
		String eFeatureName = createAssignmentNameFromStatement(statement);
		eFeature.setName(eFeatureName);
		
		EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
		eAnnotation.setSource(ANNOTATION_SOURCE);
		eAnnotation.getDetails().put("ocl", statement);
		eAnnotation.getDetails().put("layoutId", getAssignmentId(assignment));
		if (assignment.isUsingViewComponent()) {
			String compName = assignment.getUsingViewComponentNamed();
			EClassifier compForAssignment = screenPackage.getEClassifier(compName);
			
			if (compForAssignment == null) {
				throw new RuntimeException(String.format("Error! Assignment \"%s\" tries to use component \"%s\", but the component doesn't exist.", statement, compName));
			}
			
			String expectedTypeForComponent = compForAssignment.getEAnnotations().get(0).getDetails().get("expectedType");
			if (assignmentProducesWrongTypeForComponent(eClassifier, expectedTypeForComponent)) {
				throw new RuntimeException(String.format("Error! Component \"%s\" expects input of type \"%s\". Assignment \"%s\" produces type \"%s\".", compName, expectedTypeForComponent, statement, eClassifier.getName()));
			}
			eAnnotation.getDetails().put("useComponent", createPrefixedComponentNameFromName(compName));
		}
		
		eFeature.getEAnnotations().add(eAnnotation);
		screenClass.getEStructuralFeatures().add(eFeature);
		
	}
	
	private boolean assignmentProducesWrongTypeForComponent(EClassifier eClassifier, String expectedTypeForComponent) {
		
		if (eClassifier instanceof CollectionTypeImpl) {
			CollectionTypeImpl cti = (CollectionTypeImpl) eClassifier;
			return !cti.getElementType().getName().equals(expectedTypeForComponent);
		}
		return !eClassifier.getName().equals(expectedTypeForComponent);
	}

	private void addViewComponentsToPackage(EPackage ePackage) {
		
		for (ViewComponent component : types) {
			
			String typeName = createPrefixedComponentNameFromName(component.getName());
			EClass componentClass = EcoreFactory.eINSTANCE.createEClass();
			componentClass.setName(typeName);

			String typeType = component.getExpectedType();
			EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(ANNOTATION_SOURCE);
			annotation.getDetails().put("expectedType", typeType);
			componentClass.getEAnnotations().add(annotation);
			
			ePackage.getEClassifiers().add(componentClass);
			
			for (Assignment assignment : component.getAssignments()) {
				
				addAssignmentToComponent(componentClass, assignment);
				
			}
			
		}
		
	}

	private void addAssignmentToComponent(EClass componentClass, Assignment assignment) {
		
		String assignmentName = createAssignmentNameFromStatement(assignment.getStatement());
		
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(ANNOTATION_SOURCE);
		annotation.getDetails().put("ocl", assignment.getStatement());
		Widget aWidget = assignment.getWidget();
		annotation.getDetails().put("layoutId", "#" + aWidget.getId());
		
		String statement = assignment.getStatement();
		String compType = componentClass.getEAnnotations().get(0).getDetails().get("expectedType");
		
		EClassifier expectedComponentClassifier = getClassifierNamed(compType);
		EClassifier assignmentClassifier = OCLHandler.getClassifierForStatement2(resSet, expectedComponentClassifier, statement);
		EStructuralFeature feature = createFeatureFromClassifier(assignmentClassifier);
		
		String useComponent = assignment.getUsingViewComponentNamed();
		if (useComponent != null) {
			annotation.getDetails().put("useComponent", createPrefixedComponentNameFromName(useComponent));
		}
		
		feature.setName(assignmentName);
		
		feature.getEAnnotations().add(annotation);
		componentClass.getEStructuralFeatures().add(feature);
		
	}
	
	protected EClassifier getClassifierNamed(String name) {
		
		for (String key : classesForXmis.keySet()) {
			for (Entry<String, EClassifier> entry : classesForXmis.get(key).entrySet()) {
				if (entry.getKey().equals(name)) {
					return entry.getValue();
				}
			}
		}
		return null;
		
	}

	private void saveAsResource(EPackage screenPackage) {
		URI uri = URI.createFileURI(String.format("%s%s.ecore", Constants.GENERATED_DIRECTORY, screenPackage.getName()));
		ResourceSet resSet = new ResourceSetImpl();
		Resource res = resSet.createResource(uri);
		res.getContents().add(screenPackage);
		
		try {
			res.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private EPackage createScreenPackage(String screenName, EClass screenClass) {
		EPackage screenPackage = EcoreFactory.eINSTANCE.createEPackage();
		screenPackage.setName(screenName);
		screenPackage.setNsPrefix(String.format("%s.%s", ANNOTATION_SOURCE, screenName));
		screenPackage.setNsURI(String.format("http://%s.%s", ANNOTATION_SOURCE, screenName));
		screenPackage.getEClassifiers().add(screenClass);
		return screenPackage;
	}
	
	private void addContextsTo(EClass screenClass) {
		
		List<Context> allContexts = contexts;
		
		while (screenClass.getEStructuralFeatures().size() != allContexts.size()) {
			
			for (Context context : allContexts) {
				
				String name = context.getName();
				if (!containsEStructuralFeature(name, screenClass)) {
					String statement = context.getStatement();
					
					EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
					eAnnotation.setSource(ANNOTATION_SOURCE);
					
					if (statement.startsWith("#")) {
						Resource resource = resSet.getResource(URI.createFileURI(Constants.GENERATED_DIRECTORY + "selectionModel.ecore"), true);
						EStructuralFeature feature = EcoreFactory.eINSTANCE.createEReference();
						feature.setName(name);
						feature.setEType(((EPackage) resource.getContents().get(0)).getEClassifier("Selections"));
						
						feature.getEAnnotations().add(eAnnotation);
						eAnnotation.getDetails().put("xmiLocation", Constants.GENERATED_DIRECTORY + "selectionModel.ecore");
						screenClass.getEStructuralFeatures().add(feature);
						
					} else if (statement.startsWith("/")) {
						Resource resource = resSet.getResource(URI.createFileURI(statement), true);
						addClassesFromResource(statement, resource);
						EObject value = resource.getContents().get(0);
						EClass type = value.eClass();
						
						EStructuralFeature eFeature = EcoreFactory.eINSTANCE.createEReference();
						eFeature.setName(name);
						eFeature.setEType(type);
						eFeature.getEAnnotations().add(eAnnotation);
						eAnnotation.getDetails().put("xmiLocation", statement);
						screenClass.getEStructuralFeatures().add(eFeature);
					} else {
						
						EClassifier newType = OCLHandler.getClassifierForStatement2(resSet, screenClass, statement);
						if (newType != null) {
							EStructuralFeature eFeature = createFeatureFromClassifier(newType);
							eFeature.setName(name);
							eFeature.getEAnnotations().add(eAnnotation);
							eAnnotation.getDetails().put("ocl", statement);
							screenClass.getEStructuralFeatures().add(eFeature);
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	private void addClassesFromResource(String resourceName, Resource resource) {
		
		Map<String, EClassifier> entry = classesForXmis.get(resourceName);
		if (entry == null) {
			entry = new HashMap<String, EClassifier>();
		
			for (EObject content : resource.getContents()) {
				
				EPackage thePackage = content.eClass().getEPackage();
				for (EClassifier classifier : thePackage.getEClassifiers()) {
					entry.put(classifier.getName(), classifier);
				}
				
			}
			
			classesForXmis.put(resourceName, entry);
			
		}
		
	}

	/**
	 * Creates an EStructuralFeature of either EReference or EAttribute from the given classifier. The classifier can be of three different types:
	 * CollectionTypeImpl, PrimitiveTypeImpl or EClassImpl. In case of a collection, the type of the elements will be used as the type for the
	 * structural feature and the upperbound will be set to unbound.
	 * 
	 * The name for the created structural feature is not set and must therefore be set later.
	 * @param classifier
	 * @return EAttribute or EReference
	 */
	protected EStructuralFeature createFeatureFromClassifier(EClassifier classifier) {
		
		EStructuralFeature feature = null;
		
		if (classifier instanceof CollectionTypeImpl) {
			EClassifier elementType = ((CollectionTypeImpl) classifier).getElementType();
			
			if (isEDataType(elementType)) {
				feature = EcoreFactory.eINSTANCE.createEAttribute();
				EDataType eDataType = getEDataTypeFromClassifier(elementType);
				feature.setEType(eDataType);
			} else {
				feature = EcoreFactory.eINSTANCE.createEReference();
				feature.setEType(elementType);
			}
			
			feature.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
			
		} else if (isEDataType(classifier)) {
			feature = EcoreFactory.eINSTANCE.createEAttribute();
			EDataType eDataType = getEDataTypeFromClassifier(classifier);
			feature.setEType(eDataType);
		} else {
			feature = EcoreFactory.eINSTANCE.createEReference();
			feature.setEType(classifier);
		}
		
		return feature;
		
	}
	
	private boolean isEDataType(EClassifier eClassifier) {
		
		if (eClassifier instanceof PrimitiveTypeImpl) {
			return true;
		}
		
		if (eClassifier.getName().equals("EDate")) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the corresponding EDataType for the primitive.
	 * 
	 * Using this method since the oclstdlib.ecore cannot be found.
	 * @param classifier
	 * @return
	 */
	private EDataType getEDataTypeFromClassifier(EClassifier classifier) {
		
		switch (classifier.getName()) {
		
			case "String":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEString();
			case "EDate":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEDate();
			case "Integer":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEIntegerObject();
			case "Double":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEDoubleObject();
			case "Long":
				return EcoreFactory.eINSTANCE.getEcorePackage().getELongObject();
			case "Byte":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEByteObject();
			case "Boolean":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEBooleanObject();
			case "Character":
				return EcoreFactory.eINSTANCE.getEcorePackage().getECharacterObject();
			default:
				return null;
				
		}
		
	}
	
	private boolean containsEStructuralFeature(String name, EClass screenClass) {

		for (EStructuralFeature eStructuralFeature : screenClass.getEStructuralFeatures()) {
			
			if (eStructuralFeature.getName().equals(name)) {
				return true;
			}
			
		}
		
		return false;
		
	}
	
	private String clipAtSequence(String sequence, String string) {
		
		int indexOfSequence = string.indexOf(sequence);
		
		if (indexOfSequence == -1) {
			return string;
		}
		
		return string.substring(0, indexOfSequence);
	}
	
	private String createReferenceName(String statement) {
		
		statement = clipAtSequence("->", statement);
		statement = clipAtSequence("(", statement);
		
		String[] split = statement.split("\\.");
		
		String out = split[0];
		for (int i = 1; i < split.length; i++) {
			String upperCase = split[i].toUpperCase();
			split[i] = upperCase.charAt(0) + split[i].substring(1);
			out += split[i];
		}
		out += counter;
		counter++;
		return out;
		
	}
	
	private String createAssignmentNameFromStatement(String statement) {
		
		String res = createReferenceName(statement);
		return ASSIGNMENT_PREFIX + capitalizeFirst(res);
		
	}
	
	private String capitalizeFirst(String string) {
		
		if (string.length() < 2) {
			return string.toUpperCase();
		}
		
		String capitalized = string.toUpperCase();
		String output = capitalized.charAt(0) + string.substring(1);
		return output;
		
	}

	private String createPrefixedComponentNameFromName(String string) {
		
		return string;
		
	}
	
	private String getAssignmentId(Assignment assignment) {
		
		return "#" + assignment.getWidget().getId();
		
	}
	
}
