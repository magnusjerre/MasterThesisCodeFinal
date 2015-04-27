package datagenerator;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.ETypedElementImpl;
import org.eclipse.emf.ecore.impl.EcoreFactoryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import application.Constants;

import com.wireframesketcher.model.Widget;
import com.wireframesketcher.model.WidgetGroup;

public class ScreenEcoreGenerator {
	
	private DataUtils utils;
	private static final String ANNOTATION_SOURCE = "wireframe";
	private int counter;
	private String screenName = "nothing";
	
	public ScreenEcoreGenerator() {
		
		utils = DataUtils.getInstance();
		counter = 1;
		
	}
	
	public void generateEcoreForScreen(String screenName) {
		
		if (screenName.endsWith(".screen")) {
			screenName = screenName.replace(".screen", "");
		}
		this.screenName = screenName;
		
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
					//TODO: implement type
				}
			}
			
		}
		
		
		EPackage screenPackage = createScreenPackage(screenName, screenClass);
		
		addTypesToPackage(screenPackage);
		
		saveAsResource(screenPackage);
		
	}
	
	private void addTypesToPackage(EPackage ePackage) {
		
		HashMap<String, EClass> typesMap = new HashMap<String, EClass>();
		

		for (EObject type : TypeGenerator.getInstance().list.getElementsIterable()) {
			
			String typeName = (String) type.eGet(utils.tNameFeature);
			EClass typeClass = EcoreFactory.eINSTANCE.createEClass();
			typeClass.setName(typeName);
			
			EAttribute typeAttr = EcoreFactory.eINSTANCE.createEAttribute();
			typeAttr.setName("fxmlLocation");
			typeAttr.setEType(EcoreFactory.eINSTANCE.getEcorePackage().getEString());
			typeAttr.setDefaultValue(String.format("%s%s-%s.fxml", Constants.DATAGENERATOR_DIRECTORY, screenName, typeName));
			
			typeClass.getEStructuralFeatures().add(typeAttr);
			ePackage.getEClassifiers().add(typeClass);
			
		}
		
		
	}

	private void addAssignmentAttributeTo(EClass screenClass, EObject assignment) {
		String statement = (String) assignment.eGet(utils.a2StatementFeature);
		String attrName = createReferenceName(statement);
		EAttribute assignmentAttr = EcoreFactory.eINSTANCE.createEAttribute();
		assignmentAttr.setName(attrName);
		
		EAnnotation assignmentAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
		assignmentAnnotation.setSource(ANNOTATION_SOURCE);
		assignmentAnnotation.getDetails().put("ocl", statement);
		assignmentAnnotation.getDetails().put("layoutId", "#" + ((Widget) assignment.eGet(utils.a2WidgetFeature)).getId().intValue());
		assignmentAnnotation.getDetails().put("useComponent", (String) assignment.eGet(utils.a2UseComponentNamedFeature));
		assignmentAttr.getEAnnotations().add(assignmentAnnotation);

		@SuppressWarnings("unchecked")
		EList<EObject> dataList = (EList<EObject>) assignment.eGet(utils.a2DataFeature);
		assignmentAttr.setEType(dataList.get(0).eClass().getEStructuralFeature("object").getEType());
		
		screenClass.getEStructuralFeatures().add(assignmentAttr);
	}

	private String createReferenceName(String statement) {

		String[] split = statement.split("\\.");
		if (split.length == 1) {
			return split[0];
		}
		
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
		for (EObject context : ContextGenerator.getInstance().contexts) {
			
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

	private void addRootContextsTo(EClass screenClass) {
		for (EObject root : ContextGenerator.getInstance().rootContexts) {
			
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
	
	private boolean isJavaObjectContainerClass(EObject eObject) {
		
		return eObject.eClass().equals(utils.javaObjectContainerClass);
		
	}

}
