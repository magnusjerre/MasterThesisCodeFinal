package list;

import org.eclipse.emf.ecore.EClass;

import javafx.scene.control.ListCell;
import list.CellFXMLGeneric;

public class ListCellImpl<T> extends ListCell<T> {
	
	public String location;
	public EClass eClass;
	
	public ListCellImpl(String location, EClass eClass) {
		
		super();
		this.location = location;
		this.eClass = eClass;
		
	}
    
    @Override
    public void updateItem(T t, boolean empty) {
    	
        super.updateItem(t, empty);
        if (t != null) {
            CellFXMLGeneric cFXML = new CellFXMLGeneric(location, eClass);
            cFXML.setInfo(t);
            setGraphic(cFXML.getPane());
        }
        
    }
    
}