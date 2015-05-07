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
				EObject eObject = (EObject) object;
				Object oclResult = OCLHandler.parseOCLStatement(eObject.eResource().getResourceSet(), eObject, ocl);
				//start with simple case, handle advanced view comp case later if I have time
				EStructuralFeature featureInViewComponent = getFeatureForId(child.getId());
				EObject newInstance = ScreenEcoreHandler.ePackage.getEFactoryInstance().create(viewComponentEClass);
				
				for (EStructuralFeature iuccFeature : newInstance.eClass().getEStructuralFeatures()) {
					
					EAnnotation iuccAnnotation = iuccFeature.getEAnnotation("wireframe");
					String iuccOcl = iuccAnnotation.getDetails().get("ocl");
					Object result = OCLHandler.parseOCLStatement(ScreenEcoreHandler.resourceSet, eObject, iuccOcl);
					if (iuccFeature.isMany())
					{
						List<Object> lst = (List<Object>) newInstance.eGet(iuccFeature);
						lst.addAll((Collection<Object>) result);
					} else {
						newInstance.eSet(iuccFeature, result);
					}
					
				}
				
				EStructuralFeature aFeature = getFeatureForId(child.getId());
				ScreenEcoreHandler.assignComponents(child, newInstance, aFeature );
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
