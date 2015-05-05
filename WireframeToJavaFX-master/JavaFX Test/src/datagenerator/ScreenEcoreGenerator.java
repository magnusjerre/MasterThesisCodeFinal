package datagenerator;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.EList;
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

public class ScreenEcoreGenerator {
	
	private DataUtils utils;
	private static final String ANNOTATION_SOURCE = "wireframe";
	private int counter;
	private String ASSIGNMENT_PREFIX = "a";
	private EPackage screenPackage;
	private ResourceSet resSet;
	
	public ScreenEcoreGenerator() {
		
		utils = DataUtils.getInstance();
		counter = 1;
		
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
		
		addComponentsToPackage(screenPackage);
		
		addAssignmentsTo(screenClass);
		
		saveAsResource(screenPackage);
		
	}

	private void addAssignmentsTo(EClass screenClass) {
		for (EObject assignment : AssignmentGenerator.getInstance().assignments.getElementsIterable()) {
			addAssignmentTo(screenClass, assignment);
		}
	}
	
	private void addAssignmentTo(EClass screenClass, EObject assignment) {
		
		if (assignment.eGet(utils.a2PartOfComponentFeature) != null) {
			return;
		}
		
		String statement = (String) assignment.eGet(utils.a2StatementFeature);
		
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
		if (assignmentUsesComponent(assignment)) {
			String compName = (String) assignment.eGet(utils.a2UseComponentNamedFeature);
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
		return !eClassifier.getName().equals(expectedTypeForComponent);
	}

	private boolean assignmentUsesComponent(EObject assignment) {
		return assignment.eGet(utils.a2UseComponentNamedFeature) != null;
	}

	private void addComponentsToPackage(EPackage ePackage) {
		
		for (EObject component : TypeGenerator.getInstance().list.getElementsIterable()) {
			
			String typeName = createPrefixedComponentNameFromName((String) component.eGet(utils.tNameFeature));
			EClass componentClass = EcoreFactory.eINSTANCE.createEClass();
			componentClass.setName(typeName);

			String typeType = (String) component.eGet(utils.tType);
			EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(ANNOTATION_SOURCE);
			annotation.getDetails().put("expectedType", typeType);
			componentClass.getEAnnotations().add(annotation);
			
			ePackage.getEClassifiers().add(componentClass);
			
			@SuppressWarnings("unchecked")
			EList<EObject> assignmentsForType = (EList<EObject>) component.eGet(utils.tAssignmentsFeature);
			for (EObject assignment : assignmentsForType) {
				addAssignmentToComponent(componentClass, assignment);
			}
			
		}
		
	}
	
	private void addAssignmentToComponent(EClass componentClass, EObject assignment) {
		
		EStructuralFeature feature;
		String assignmentName = createAssignmentNameFromStatement((String) assignment.eGet(utils.a2StatementFeature));
		
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(ANNOTATION_SOURCE);
		annotation.getDetails().put("ocl", (String) assignment.eGet(utils.a2StatementFeature));
		Widget aWidget = (Widget) assignment.eGet(utils.a2WidgetFeature);
		annotation.getDetails().put("layoutId", "#" + aWidget.getId());
		
		String useComponent = (String) assignment.eGet(utils.a2UseComponentNamedFeature); 
		if (useComponent == null) {	//No type is used
			feature = EcoreFactory.eINSTANCE.createEAttribute();
			feature.setEType(EcoreFactory.eINSTANCE.getEcorePackage().getEJavaObject());
		} else {
			feature = EcoreFactory.eINSTANCE.createEReference();
			feature.setEType(EcoreFactory.eINSTANCE.getEcorePackage().getEObject());
			annotation.getDetails().put("useComponent", createPrefixedComponentNameFromName(useComponent));
		}
		
		feature.setName(assignmentName);
		
		feature.getEAnnotations().add(annotation);
		componentClass.getEStructuralFeatures().add(feature);
		
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
		
		List<EObject> allContexts = (List<EObject>) ContextGenerator.getInstance().getAllContexts();
		
		while (screenClass.getEStructuralFeatures().size() != allContexts.size()) {
			
			for (EObject eObject : allContexts) {
				String name = (String) eObject.eGet(utils.c2NameFeature);
				if (!containsEStructuralFeature(name, screenClass)) {
					String statement = (String) eObject.eGet(utils.c2StatementFeature);
					
					EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
					eAnnotation.setSource(ANNOTATION_SOURCE);
					
					if (statement.startsWith("/")) {
						Resource resource = utils.resourceSet.getResource(URI.createFileURI(statement), true);
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
	
	/**
	 * Creates an EStructuralFeature of either EReference or EAttribute from the given classifier. The classifier can be of three different types:
	 * CollectionTypeImpl, PrimitiveTypeImpl or EClassImpl. In case of a collection, the type of the elements will be used as the type for the
	 * structural feature and the upperbound will be set to unbound.
	 * 
	 * The name for the created structural feature is not set and must therefore be set later.
	 * @param classifier
	 * @return EAttribute or EReference
	 */
	private EStructuralFeature createFeatureFromClassifier(EClassifier classifier) {
		
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
	
	private String getAssignmentId(EObject assignment) {
		
		Widget widget = (Widget) assignment.eGet(utils.a2WidgetFeature);
		return "#" + widget.getId();
		
	}
	
}
