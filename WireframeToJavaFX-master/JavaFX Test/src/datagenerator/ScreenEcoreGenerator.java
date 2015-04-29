package datagenerator;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
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
		addAllContextsTo(screenClass);
		
		for (EObject assignment : AssignmentGenerator.getInstance().assignments.getElementsIterable()) {
			
			if (assignment.eGet(utils.a2PartOfComponentFeature) == null) {
				@SuppressWarnings("unchecked")
				EList<EObject> dataList = (EList<EObject>) assignment.eGet(utils.a2DataFeature);
				if (isJavaObjectContainerClass(dataList.get(0))) {
					addAssignmentAttributeTo(screenClass, assignment);
				} else {
					addAssignmentReferenceTo(screenClass, assignment);
				}
			}
			
		}
		
		addTypesToPackage(screenPackage);
		
		saveAsResource(screenPackage);
		
	}
	
	private void addAssignmentReferenceTo(EClass screenClass, EObject assignment) {
	
		String aName = createAssignmentNameFromStatement((String) assignment.eGet(utils.a2StatementFeature));
		
		EReference aRef = EcoreFactory.eINSTANCE.createEReference();
		aRef.setName(aName);
		aRef.setEType(EcoreFactory.eINSTANCE.getEcorePackage().getEObject());
		
		EAnnotation aAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
		aRef.getEAnnotations().add(aAnnotation);
		aAnnotation.setSource(ANNOTATION_SOURCE);
		aAnnotation.getDetails().put("ocl", (String) assignment.eGet(utils.a2StatementFeature));
		Widget aWidget = (Widget) assignment.eGet(utils.a2WidgetFeature);
		aAnnotation.getDetails().put("layoutId", "#" + aWidget.getId());
		aAnnotation.getDetails().put("useComponent", createComponentName((String) assignment.eGet(utils.a2UseComponentNamedFeature)));
		
		screenClass.getEStructuralFeatures().add(aRef);
		
	}

	private void addTypesToPackage(EPackage ePackage) {
		
		for (EObject type : TypeGenerator.getInstance().list.getElementsIterable()) {
			
			String typeName = createComponentName((String) type.eGet(utils.tNameFeature));
			EClass typeClass = EcoreFactory.eINSTANCE.createEClass();
			typeClass.setName(typeName);
			
			ePackage.getEClassifiers().add(typeClass);
			
			@SuppressWarnings("unchecked")
			EList<EObject> assignmentsForType = (EList<EObject>) type.eGet(utils.tAssignmentsFeature);
			for (EObject assignment : assignmentsForType) {
				
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
					annotation.getDetails().put("useComponent", createComponentName(useComponent));
				}
				
				feature.setName(assignmentName);
					
				feature.getEAnnotations().add(annotation);
				typeClass.getEStructuralFeatures().add(feature);
				
			}
			
		}
		
		
	}

	private void addAssignmentAttributeTo(EClass screenClass, EObject assignment) {
		String statement = (String) assignment.eGet(utils.a2StatementFeature);
		String attrName = createAssignmentNameFromStatement(statement);
		EAttribute assignmentAttr = EcoreFactory.eINSTANCE.createEAttribute();
		assignmentAttr.setName(attrName);
		
		EAnnotation assignmentAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
		assignmentAnnotation.setSource(ANNOTATION_SOURCE);
		assignmentAnnotation.getDetails().put("ocl", statement);
		assignmentAnnotation.getDetails().put("layoutId", "#" + ((Widget) assignment.eGet(utils.a2WidgetFeature)).getId().intValue());
		assignmentAttr.getEAnnotations().add(assignmentAnnotation);

		@SuppressWarnings("unchecked")
		EList<EObject> dataList = (EList<EObject>) assignment.eGet(utils.a2DataFeature);
		assignmentAttr.setEType(dataList.get(0).eClass().getEStructuralFeature("object").getEType());
		
		screenClass.getEStructuralFeatures().add(assignmentAttr);
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
	
	private void addAllContextsTo(EClass screenClass) {
		
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
	
	private boolean isJavaObjectContainerClass(EObject eObject) {
		
		return eObject.eClass().equals(utils.javaObjectContainerClass);
		
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

	private String createComponentName(String string) {
		
		return TYPE_PREFIX + string;
		
	}
	
}
