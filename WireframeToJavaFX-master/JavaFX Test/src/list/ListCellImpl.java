package list;

import org.eclipse.emf.ecore.EClass;

import javafx.scene.control.ListCell;
import list.CellFXMLGeneric;

public class ListCellImpl extends ListCell<Object> {
	
	public String location;
	public EClass eClass;
	
	public ListCellImpl(String location, EClass eClass) {
		
		super();
		this.location = location;
		this.eClass = eClass;
		
	}
    
    @Override
    public void updateItem(Object object, boolean empty) {
    	
        super.updateItem(object, empty);
        if (object != null) {
            CellFXMLGeneric cFXML = new CellFXMLGeneric(location, eClass);
            cFXML.setInfo(object);
            setGraphic(cFXML.getPane());
        }
        
    }
    
}