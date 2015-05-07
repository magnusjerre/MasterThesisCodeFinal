package list;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import datagenerator.OCLHandler;
import datagenerator.ScreenEcoreHandler;

public class CellFXMLGeneric {
	
	
	FXMLLoader fxmlLoader;
	EClass viewComponentEClass;
	
	public CellFXMLGeneric(String resourceName, EClass viewComponentEClass) {
		
		this.viewComponentEClass = viewComponentEClass;
		
		fxmlLoader = new FXMLLoader(getClass().getResource("/generated/" + resourceName));
		fxmlLoader.setController(this);
		
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setInfo(Object object) {
		
		for (Node child : ((Pane) fxmlLoader.getRoot()).getChildren()) {
			
			if ((viewComponentEClass != null) && (object instanceof EObject)) {
				String ocl = getOCLStatementForId(child.getId());
				EObject listElement = (EObject) object;
				Object oclResult = OCLHandler.parseOCLStatement(listElement.eResource().getResourceSet(), listElement, ocl);
				//start with simple case, handle advanced view comp case later if I have time
				EStructuralFeature featureInViewComponent = getFeatureForId(child.getId());
				EObject newInstance = ScreenEcoreHandler.ePackage.getEFactoryInstance().create(viewComponentEClass);
				populateInstance(listElement, newInstance);
				
				EStructuralFeature aFeature = getFeatureForId(child.getId());
				ScreenEcoreHandler.assignComponents(child, newInstance, aFeature);
			}
			
		}
		
	}

	private void populateInstance(EObject elementEObject, EObject newInstance) {
		for (EStructuralFeature iuccFeature : newInstance.eClass().getEStructuralFeatures()) {
			
			EAnnotation iuccAnnotation = iuccFeature.getEAnnotation("wireframe");
			String iuccOcl = iuccAnnotation.getDetails().get("ocl");
			Object result = OCLHandler.parseOCLStatement(ScreenEcoreHandler.resourceSet, elementEObject, iuccOcl);
			if (iuccFeature.isMany())
			{
				List<Object> lst = (List<Object>) newInstance.eGet(iuccFeature);
				lst.addAll((Collection<Object>) result);
			} else {
				newInstance.eSet(iuccFeature, result);
			}
			
		}
	}
	
	public Pane getPane() {
		
		return (Pane) fxmlLoader.getRoot();
		
	}
	
	private String getOCLStatementForId(String id) {
		
		for (EStructuralFeature feature : viewComponentEClass.getEStructuralFeatures()) {
			
			EAnnotation eAnnotation = feature.getEAnnotations().get(0);
			String featureId = eAnnotation.getDetails().get("layoutId");
			if (("#" + id).equals(featureId)) {
				return eAnnotation.getDetails().get("ocl");
			}
			
		}

		return null;
		
	}
	
	private EStructuralFeature getFeatureForId(String id) {

		for (EStructuralFeature feature : viewComponentEClass.getEStructuralFeatures()) {
			
			EAnnotation eAnnotation = feature.getEAnnotations().get(0);
			String featureId = eAnnotation.getDetails().get("layoutId");
			if (("#" + id).equals(featureId)) {
				return feature;
			}
			
		}

		return null;
		
	}
	

}
