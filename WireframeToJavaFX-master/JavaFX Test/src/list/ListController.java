package list;

import org.eclipse.emf.ecore.EClass;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ListController {

    public ObservableList obsList;
    
    public ListController(ListView listView, final String fxmlFileName, final EClass eClass) {
        
        obsList = FXCollections.observableArrayList();
        
        listView.setItems(obsList);
        listView.setCellFactory(new Callback<ListView<Object>, ListCellImpl>() {

			@Override
			public ListCellImpl call(ListView<Object> param) {
				return new ListCellImpl(fxmlFileName, eClass);
			}
		});
       
    }   
    
}