package datagenerator;

import java.io.IOException;

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

import application.Constants;

import com.wireframesketcher.model.Widget;

public class ScreenEcoreGenerator {
	
	private DataUtils utils;
	private static final String ANNOTATION_SOURCE = "wireframe";
	private int counter;
	private String ASSIGNMENT_PREFIX = "a";
	private String TYPE_PREFIX = "Type";
	
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
		
		addRootContextsTo(screenClass);
		
		addContextsTo(screenClass);
		
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
		
		
		EPackage screenPackage = createScreenPackage(screenName, screenClass);
		
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

	private void addContextsTo(EClass screenClass) {
		for (EObject context : ContextGenerator.getInstance().contextsInOrder) {
			
			if (ContextGenerator.getInstance().contexts.contains(context)) {
				EReference contextRef = EcoreFactory.eINSTANCE.createEReference();
				String refName = (String) context.eGet(utils.c2NameFeature);
				contextRef.setName(refName);
				
				String statement = (String) context.eGet(utils.c2StatementFeature);
				EAnnotation contextAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
				contextAnnotation.setSource(ANNOTATION_SOURCE);
				contextAnnotation.getDetails().put("ocl", statement);
				contextRef.getEAnnotations().add(contextAnnotation);
				
				
				@SuppressWarnings("unchecked")
				EList<EObject> dataList = (EList<EObject>) context.eGet(utils.c2DataFeature);
				EClass contextClass = dataList.get(0).eClass();
				contextRef.setEType(contextClass);
				
				screenClass.getEStructuralFeatures().add(contextRef);
			}
		}
	}

	private void addRootContextsTo(EClass screenClass) {
		for (EObject root : ContextGenerator.getInstance().contextsInOrder) {
			
			if (ContextGenerator.getInstance().rootContexts.contains(root)) {
				EReference rootRef = EcoreFactory.eINSTANCE.createEReference();
				String refName = (String) root.eGet(utils.c2NameFeature);	//left side of equals statement
				rootRef.setName(refName);
				
				String xmiLocation = (String) root.eGet(utils.c2StatementFeature);
				EAnnotation rootAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
				rootAnnotation.setSource(ANNOTATION_SOURCE);
				rootAnnotation.getDetails().put("xmiLocation", xmiLocation);
				rootRef.getEAnnotations().add(rootAnnotation);
				
				@SuppressWarnings("unchecked")
				EList<EObject> dataList = (EList<EObject>) root.eGet(utils.c2DataFeature);	//Requires the model to have already been read...
				EClass rootClass = dataList.get(0).eClass();
				rootRef.setEType(rootClass);
				
				screenClass.getEStructuralFeatures().add(rootRef);
			}
			
		}
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
