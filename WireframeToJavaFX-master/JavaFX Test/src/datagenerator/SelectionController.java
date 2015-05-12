package datagenerator;

import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.control.ListView;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import application.Constants;
import application.LocationUtils;

/**
 * The SelectionController is responsible for assigning the correct value to the selection
 * selected during runtime.
 * @author Magnus Jerre
 *
 */
public class SelectionController {
	
	private static SelectionController instance;
	
	public ResourceSet resSet;
	public EPackage selectionPacakge;
	public EFactory selectionFactory;
	public EClass selectionClass;
	public EObject selectionInstance;
	
	public static final String SELECTION_MODEL_SIMPLE_FILE_NAME = "selectionModel.ecore";
	
	private SelectionController() {
		
		
		resSet = ScreenEcoreController.resourceSet;

//		URI uri = URI.createFileURI(Constants.GENERATED_DIRECTORY + SELECTION_MODEL_SIMPLE_FILE_NAME);
		
		
		
		
		
		URI uri = URI.createFileURI(LocationUtils.getFilePathSrc(LocationUtils.GENERATED_PACKAGE, SELECTION_MODEL_SIMPLE_FILE_NAME));
		Resource firstScreen = resSet.getResource(uri, true);
		selectionPacakge = (EPackage) firstScreen.getContents().get(0);
		
		selectionFactory = selectionPacakge.getEFactoryInstance();
		selectionClass = (EClass) selectionPacakge.getEClassifiers().get(0);
		selectionInstance = selectionFactory.create(selectionClass);
		
	}
	
	public static SelectionController getInstance() {
		
		if (instance == null) {
			instance = new SelectionController();
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
						selectedItem = ((ListView<?>) event.getSource()).getSelectionModel().getSelectedItem();
					} else if (event.getSource() instanceof Group) {
						selectedItem = getEObjectForGroup(id);
					}
					selectionInstance.eSet(feature, selectedItem);
				}
				
			}
			
		}
		
	}
	
	/**
	 * Returns the EObject used to populate the element with the given id. 
	 * @param id
	 * @return
	 */
	private EObject getEObjectForGroup(String id) {
		
		EObject eInstance = ScreenEcoreController.instance;
		
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
