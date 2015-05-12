package list;

import org.eclipse.emf.ecore.EClass;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ListController<T> {

    public ObservableList<T> obsList;
    
    public ListController(Node node, final String fxmlFileName, final EClass eClass) {
        
    	ListView<T> listView = (ListView<T>) node;
        obsList = FXCollections.observableArrayList();

        listView.setItems(obsList);
        listView.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
        	@Override
			public ListCell<T> call(ListView<T> param) {
				return new ListCellImpl<T>(fxmlFileName, eClass);
			}
        });
        
    }   
    
}