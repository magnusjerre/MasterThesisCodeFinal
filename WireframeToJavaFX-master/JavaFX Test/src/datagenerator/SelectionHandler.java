package datagenerator;

import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;

import application.Constants;

public class SelectionHandler {
	
	
	private static SelectionHandler instance;
	
	public ResourceSet resSet;
	public EPackage selectionPacakge;
	public EFactory selectionFactory;
	public EClass selectionClass;
	public EObject selectionInstance;
	
	public static final String SELECTION_MODEL_SIMPLE_FILE_NAME = "selectionModel.ecore";
	
	private SelectionHandler() {
		
		
		resSet = ScreenEcoreHandler.resourceSet;

		URI uri = URI.createFileURI(Constants.GENERATED_DIRECTORY + SELECTION_MODEL_SIMPLE_FILE_NAME);
		Resource firstScreen = resSet.getResource(uri, true);
		selectionPacakge = (EPackage) firstScreen.getContents().get(0);
		
		selectionFactory = selectionPacakge.getEFactoryInstance();
		selectionClass = (EClass) selectionPacakge.getEClassifiers().get(0);
		selectionInstance = selectionFactory.create(selectionClass);
		
	}
	
	public static SelectionHandler getInstance() {
		
		if (instance == null) {
			instance = new SelectionHandler();
		}
		
		return instance;
		
	}
	
	public void handleMouseEvent(String id, Event event) {
		
		if (id == null) {
			return;
		}
		
		for (EStructuralFeature feature : selectionInstance.eClass().getEStructuralFeatures()) {
			
			EMap<String, String> details = feature.getEAnnotation("wireframe").getDetails();
			
			for (java.util.Map.Entry<String, String> entry : details) {
				
				if (id.equals(entry.getValue().replace("#", ""))) {
					Object selectedItem = null;
					if (event.getSource() instanceof ListView) {
						selectedItem = ((ListView) event.getSource()).getSelectionModel().getSelectedItem();
					} else if (event.getSource() instanceof Group) {
						selectedItem = getEObjectForGroup(id);
					}
					selectionInstance.eSet(feature, selectedItem);
					System.out.println("cake");
				}
				
			}
			
		}
		
		
	}
	
	private EObject getEObjectForGroup(String id) {
		
		EObject eInstance = ScreenEcoreHandler.instance;
		
		for (EStructuralFeature feature : eInstance.eClass().getEStructuralFeatures()) {
			
			EMap<String, String> details = feature.getEAnnotation("wireframe").getDetails();
			
			String layoutId = details.get("layoutId");
			if (layoutId != null) {
				String layoutIdWithoutHashTag = details.get("layoutId").replace("#", "");
				if (id.equals(layoutIdWithoutHashTag)) {
					return (EObject) eInstance.eGet(feature);
				}
			}
			
		}
		
		return null;
	}
	
	
	
	
}
