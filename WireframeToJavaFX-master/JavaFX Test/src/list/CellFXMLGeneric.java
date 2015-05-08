package list;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import application.Constants;
import datagenerator.OCLHandler;
import datagenerator.ScreenEcoreHandler;

public class CellFXMLGeneric {
	
	
	FXMLLoader fxmlLoader;
	EClass viewComponentEClass;
	
	public CellFXMLGeneric(String resourceName, EClass viewComponentEClass) {
		
		this.viewComponentEClass = viewComponentEClass;
		
		try {
			
			String path = Constants.GEN_DIRECTORY + resourceName;
			URL pathURl = getClass().getResource(path);
			if (pathURl == null) {
				path = Constants.LIST_DIRECTORY + resourceName;
			}
			
			
			fxmlLoader = new FXMLLoader(getClass().getResource(path));
			fxmlLoader.setController(this);
			
			fxmlLoader.load();

		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	public void setInfo(Object object) {
		
		for (Node child : ((Pane) fxmlLoader.getRoot()).getChildren()) {
			
			if ((viewComponentEClass != null) && (object instanceof EObject)) {
				EObject listElement = (EObject) object;

				EObject newInstance = ScreenEcoreHandler.ePackage.getEFactoryInstance().create(viewComponentEClass);
				populateInstance(listElement, newInstance);
				
				EStructuralFeature aFeature = getFeatureForId(child.getId());
				ScreenEcoreHandler.assignComponents(child, newInstance, aFeature);
			} else {
				
				ScreenEcoreHandler.handleResultCorrectly(child, object);
				
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
