package datagenerator;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ocl.ecore.impl.CollectionTypeImpl;
import org.eclipse.ocl.ecore.impl.PrimitiveTypeImpl;

import application.Constants;
import data.Selection;

public class SelectionEcoreGenerator {
	
	private static final String PACKAGE_NAME = "selectionModel";
	private static final String ANNOTATION_SOURCE = "wireframe";
	private static final String SELECTION_CLASS_NAME = "Selections";
	
	private EPackage selectionPackage;
	private ResourceSet resSet;
	private EClass selectionsClass;
	
	NewGenerator newGenerator;
	
	public SelectionEcoreGenerator(NewGenerator newGenerator) {
		
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

	private void addSelection(Selection selection) {
		
		if (featureNameExists(selection.getName())) {
			
			EStructuralFeature feature = selectionsClass.getEStructuralFeature(selection.getName());
			EAnnotation annotation = feature.getEAnnotation(ANNOTATION_SOURCE);
			annotation.getDetails().put("layoutId" + annotation.getDetails().size() + 1, selection.getLayoutId());
			
			return;
		}
		
		EClassifier classifierForSelection = newGenerator.getClassifierForName(selection.getExpectedType());
		EStructuralFeature feature = createFeatureFromClassifier(classifierForSelection);
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
	
	
	public void saveResource() {
		
		URI uri = URI.createFileURI(String.format("%s%s.ecore", Constants.GENERATED_DIRECTORY, selectionPackage.getName()));
		ResourceSet resSet = new ResourceSetImpl();
		Resource res = resSet.createResource(uri);
		res.getContents().add(selectionPackage);
		
		try {
			res.save(null);
		} catch (IOException e) {
			e.printStackTrace();
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
}
