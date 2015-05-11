package datagenerator;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
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

import application.Constants;

import com.wireframesketcher.model.Widget;

import data.Assignment;
import data.Context;
import data.ViewComponent;

/**
 * The ScreenEcoreGenerator is responsible for generating the ecore file for a specific screen file.
 * 
 * The ecore file is structured such that the screen filename is used as the name for the screen class.
 * The fields of the screen class are all the contexts as well as the top-level assignments for the
 * screen. 
 * 
 * In addition to the screen class, classes for each of the different ViewComponents are created. The
 * fields of each ViewComponent class are the assignments that are part of it.
 * 
 * Each field, or EStructuralFeature, is annotated with extra information, such as the statement, 
 * layoutId and useViewComponent when necessary. These annotations are used during runtime to load
 * the data for the prototype.
 * 
 * First, the screen EClass is created, then all the contexts are added as fields for the screen EClass. 
 * The contexts are added in a way such that their order is an allowable order of execution when 
 * traversing through all the fields of the screen EClass. 
 * 
 * After all contexts have been added, the different ViewComponents will be added as separate EClasses. 
 * Each assignment that is part of the ViewComponent will be added as fields to the EClass.
 * 
 * After the ViewComponents have been handled, the top-level assignments will be added as fields to 
 * the screen EClass.
 * 
 * Finally the EPackage will be saved as an ecore file.
 * @author Magnus Jerre
 *
 */
public class ScreenEcoreGenerator {
	
	private static final String ANNOTATION_SOURCE = "wireframe";
	private int counter;
	private String ASSIGNMENT_PREFIX = "a";
	
	List<Context> contexts;
	List<Assignment> assignments;
	List<ViewComponent> viewComponents;
	
	protected EPackage screenPackage;
	private ResourceSet resSet;
	
	public ScreenEcoreGenerator(List<Context> contexts, List<Assignment> assignments, List<ViewComponent> viewComponents) {
		
		this.contexts = contexts;
		this.assignments = assignments;
		this.viewComponents = viewComponents;
		
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
		
		EStructuralFeature eFeature = DataUtils.createFeatureFromClassifier(eClassifier);
		
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
			if (assignmentProducesWrongDataTypeForComponent(eClassifier, expectedTypeForComponent)) {
				throw new RuntimeException(String.format("Error! Component \"%s\" expects input of data type \"%s\". Assignment \"%s\" produces data type \"%s\".", compName, expectedTypeForComponent, statement, eClassifier.getName()));
			}
			eAnnotation.getDetails().put("useComponent", compName);
		}
		
		eFeature.getEAnnotations().add(eAnnotation);
		screenClass.getEStructuralFeatures().add(eFeature);
		
	}
	
	private boolean assignmentProducesWrongDataTypeForComponent(EClassifier eClassifier, String expectedTypeForComponent) {
		
		if (eClassifier instanceof CollectionTypeImpl) {
			CollectionTypeImpl cti = (CollectionTypeImpl) eClassifier;
			return !cti.getElementType().getName().equals(expectedTypeForComponent);
		}
		return !eClassifier.getName().equals(expectedTypeForComponent);
	}

	private void addViewComponentsToPackage(EPackage ePackage) {
		
		for (ViewComponent component : viewComponents) {
			
			String viewComponentName = component.getName();
			EClass componentClass = EcoreFactory.eINSTANCE.createEClass();
			componentClass.setName(viewComponentName);

			String viewComponentDataType = component.getExpectedType();
			EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(ANNOTATION_SOURCE);
			annotation.getDetails().put("expectedType", viewComponentDataType);
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
		String viewComponentDataType = componentClass.getEAnnotations().get(0).getDetails().get("expectedType");
		
		EClassifier expectedComponentClassifier = DataGenerator.getInstance().getClassifierForName(viewComponentDataType);
		EClassifier assignmentClassifier = OCLHandler.getClassifierForStatement2(resSet, expectedComponentClassifier, statement);
		EStructuralFeature feature = DataUtils.createFeatureFromClassifier(assignmentClassifier);
		
		String useComponent = assignment.getUsingViewComponentNamed();
		if (useComponent != null) {
			annotation.getDetails().put("useComponent", useComponent);
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
							EStructuralFeature eFeature = DataUtils.createFeatureFromClassifier(newType);
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

	private boolean containsEStructuralFeature(String name, EClass screenClass) {

		for (EStructuralFeature eStructuralFeature : screenClass.getEStructuralFeatures()) {
			
			if (eStructuralFeature.getName().equals(name)) {
				return true;
			}
			
		}
		
		return false;
		
	}
	
	private String createReferenceName(String statement) {
		
		statement = DataUtils.clipAtSequence("->", statement);
		statement = DataUtils.clipAtSequence("(", statement);
		
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
		return ASSIGNMENT_PREFIX + DataUtils.capitalizeFirst(res);
		
	}
	
	private String getAssignmentId(Assignment assignment) {
		
		return "#" + assignment.getWidget().getId();
		
	}
	
}
