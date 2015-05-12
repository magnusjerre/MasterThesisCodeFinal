package datagenerator;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import application.Constants;
import application.LocationUtils;
import data.Selection;

/**
 * The SelectionEcoreGenerator class is responsible creating and saving an ecore containing
 * all the selections for the entire story.
 * @author Magnus Jerre
 *
 */
public class SelectionEcoreGenerator {
	
	private static final String PACKAGE_NAME = "selectionModel";
	private static final String ANNOTATION_SOURCE = "wireframe";
	private static final String SELECTION_CLASS_NAME = "Selections";
	
	private EPackage selectionPackage;
	private ResourceSet resSet;
	private EClass selectionsClass;
	
	DataGenerator newGenerator;
	
	public SelectionEcoreGenerator(DataGenerator newGenerator) {
		
		this.newGenerator = newGenerator;
		
		selectionPackage = EcoreFactory.eINSTANCE.createEPackage();
		selectionPackage.setName(PACKAGE_NAME);
		selectionPackage.setNsPrefix(String.format("%s.%s", ANNOTATION_SOURCE, PACKAGE_NAME));
		selectionPackage.setNsURI(String.format("http://%s.%s", ANNOTATION_SOURCE, PACKAGE_NAME));
		
		selectionsClass = EcoreFactory.eINSTANCE.createEClass();
		selectionsClass.setName(SELECTION_CLASS_NAME);
		selectionPackage.getEClassifiers().add(selectionsClass);
		
		resSet = newGenerator.resSet;
		resSet.getPackageRegistry().put(selectionPackage.getNsURI(), selectionPackage);
		
	}
	
	public void addSelections(List<Selection> selections) {
		
		for (Selection selection : selections) {
			addSelection(selection);
		}
		
	}

	/**
	 * Adds the selection to the epackage. Each selection has a variable name and a type, as well a
	 * an EAnnotation associated with it. The annotation includes the layoutId for each element that 
	 * will be able to affect the value stored for the specific selection. 
	 * 
	 * If the selection doesn't already exist, it is added to the selectionsClass. If it already 
	 * exists, the selection's annotation list is updated with another member, the layoutId for the 
	 * element affecting the selection.
	 * @param selection
	 */
	private void addSelection(Selection selection) {
		
		if (featureNameExists(selection.getName())) {
			
			EStructuralFeature feature = selectionsClass.getEStructuralFeature(selection.getName());
			EAnnotation annotation = feature.getEAnnotation(ANNOTATION_SOURCE);
			annotation.getDetails().put("layoutId" + annotation.getDetails().size() + 1, selection.getLayoutId());
			
			return;
		}
		
		EClassifier classifierForSelection = newGenerator.getClassifierForName(selection.getExpectedType());
		EStructuralFeature feature = DataUtils.createFeatureFromClassifier(classifierForSelection);
		feature.setName(selection.getName());
		feature.setEType(classifierForSelection);
		
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(ANNOTATION_SOURCE);
		annotation.getDetails().put("layoutId0", selection.getLayoutId());
		
		feature.getEAnnotations().add(annotation);
		selectionsClass.getEStructuralFeatures().add(feature);
		
	}
	
	private boolean featureNameExists(String featureName) {
		
		for (EStructuralFeature feature : selectionsClass.getEStructuralFeatures()) {
			if (featureName.equals(feature.getName())) {
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * Saves the selection package as an ecore file named selectionModel.ecore
	 */
	public void saveResource() {
		
//		URI uri = URI.createFileURI(String.format("%s%s.ecore", Constants.GENERATED_DIRECTORY, selectionPackage.getName()));
		
		
		
		URI uri = URI.createFileURI(LocationUtils.getFilePathSrc(LocationUtils.GENERATED_PACKAGE, selectionPackage.getName() + ".ecore"));
		ResourceSet resSet = new ResourceSetImpl();
		Resource res = resSet.createResource(uri);
		res.getContents().add(selectionPackage);
		
		try {
			res.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
