package datagenerator;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import application.Constants;

import com.wireframesketcher.model.Widget;

public class ScreenEcoreGenerator {
	
	private DataUtils utils;
	private static final String ANNOTATION_SOURCE = "wireframe";
	private int counter;
	private String ASSIGNMENT_PREFIX = "a";
	private String TYPE_PREFIX = "Type";
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
		
		addAssignmentsTo(screenClass);
		
		addComponentsToPackage(screenPackage);
		
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
		
		EObject instance = screenPackage.getEFactoryInstance().create(screenClass);
		populateInstance(instance);
		
		Object result = OCLHandler.parseOCLStatement(resSet, instance, statement);
		if (result == null) {
			throw new RuntimeException("Illegal statement, aborting...: " + statement);
		}
		
		EStructuralFeature eFeature = null;
		if (result instanceof EObject) {
			eFeature = EcoreFactory.eINSTANCE.createEReference();
			eFeature.setEType(((EObject) result).eClass());
		} else {
			eFeature = EcoreFactory.eINSTANCE.createEAttribute();
			eFeature.setEType(EcoreFactory.eINSTANCE.getEcorePackage().getEJavaObject());
		}
		
		String eFeatureName = createAssignmentNameFromStatement(statement);
		eFeature.setName(eFeatureName);
		
		EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
		eAnnotation.setSource(ANNOTATION_SOURCE);
		eAnnotation.getDetails().put("ocl", statement);
		eAnnotation.getDetails().put("layoutId", getAssignmentId(assignment));
		if (assignment.eGet(utils.a2UseComponentNamedFeature) != null) {
			String compName = (String) assignment.eGet(utils.a2UseComponentNamedFeature);
			eAnnotation.getDetails().put("useComponent", createPrefixedComponentNameFromName(compName));
		}
		
		eFeature.getEAnnotations().add(eAnnotation);
		screenClass.getEStructuralFeatures().add(eFeature);
		
	}

	private void addComponentsToPackage(EPackage ePackage) {
		
		for (EObject component : TypeGenerator.getInstance().list.getElementsIterable()) {
			
			String typeName = createPrefixedComponentNameFromName((String) component.eGet(utils.tNameFeature));
			EClass componentClass = EcoreFactory.eINSTANCE.createEClass();
			componentClass.setName(typeName);
			
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
						
						EObject instance = screenPackage.getEFactoryInstance().create(screenClass);
						populateInstance(instance);
						
						Object result = OCLHandler.parseOCLStatement(resSet, instance, statement);
						if (result != null) {
							EStructuralFeature eFeature;
							if (result instanceof EObject) {
								eFeature = EcoreFactory.eINSTANCE.createEReference();
								eFeature.setEType(((EObject) result).eClass());
							} else {
								eFeature = EcoreFactory.eINSTANCE.createEAttribute();
								eFeature.setEType(EcoreFactory.eINSTANCE.getEcorePackage().getEJavaObject());
							}
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

	private void populateInstance(EObject instance) {
		for (EStructuralFeature f : instance.eClass().getEStructuralFeatures()) {
			
			if (f.getEAnnotation(ANNOTATION_SOURCE).getDetails().get("xmiLocation") != null) {
				String location = f.getEAnnotation(ANNOTATION_SOURCE).getDetails().get("xmiLocation");
				
				Resource resource = utils.resourceSet.getResource(URI.createFileURI(location), true);
				EObject value = resource.getContents().get(0);
				instance.eSet(f, value);
			} else {
				String ocl = f.getEAnnotation(ANNOTATION_SOURCE).getDetails().get("ocl");
				Object result = OCLHandler.parseOCLStatement(resSet, instance, ocl);
				instance.eSet(f, result);
			}
			
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
		
		return TYPE_PREFIX + string;
		
	}
	
	private String getAssignmentId(EObject assignment) {
		
		Widget widget = (Widget) assignment.eGet(utils.a2WidgetFeature);
		return "#" + widget.getId();
		
	}
	
}
