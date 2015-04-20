package datagenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ListGenerator {
	
	public static void main(String[] args) {
		
		System.out.println(generateCellFxmlForSimpleListElements());
	}
	
	protected static String generateFxmlForSimpleListelements() {
		
		String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"\n" + 
				"<?import java.lang.*?>\n" + 
				"<?import java.util.*?>\n" + 
				"<?import javafx.scene.*?>\n" + 
				"<?import javafx.scene.control.*?>\n" + 
				"<?import javafx.scene.layout.*?>\n" + 
				"\n" + 
				"<AnchorPane fx:id=\"pane\" xmlns:fx=\"http://javafx.com/fxml/1\" >\n" + 
				"    <Label fx:id=\"element1\" />\n" + 
				"</AnchorPane>";
		
		return text;
		
	}
	
	protected static String generateCellFxmlForSimpleListElements() {
		
		String text =  "package simplelist;\n" + 
				"\n" + 
				"import java.io.IOException;\n" + 
				"import javafx.fxml.FXML;\n" + 
				"import javafx.fxml.FXMLLoader;\n" + 
				"import javafx.scene.control.Label;\n" + 
				"import javafx.scene.layout.Pane;\n" + 
				"\n" + 
				"public class CellFXML {\n" + 
				"    \n" + 
				"    @FXML\n" + 
				"    Label element1;\n" + 
				"    \n" + 
				"    @FXML\n" + 
				"    Pane pane;\n" + 
				"    \n" + 
				"    public CellFXML() {\n" + 
				"        \n" + 
				"        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(\"simple_cell.fxml\"));\n" + 
				"        fxmlLoader.setController(this);\n" + 
				"        try {\n" + 
				"            fxmlLoader.load();\n" + 
				"        } catch (IOException ex) {\n" + 
				"            throw new RuntimeException(ex);\n" + 
				"        }\n" + 
				"        \n" + 
				"    }\n" + 
				"    \n" + 
				"    public void setInfo(Cell cell) {\n" + 
				"        \n" + 
				"        element1.setText(cell.element1);\n" + 
				"        \n" + 
				"    }\n" + 
				"    \n" + 
				"    public Pane getPane() {\n" + 
				"        \n" + 
				"        return pane;\n" + 
				"        \n" + 
				"    }\n" + 
				"    \n" + 
				"    \n" + 
				"}";
		
		return text;
		
	}
	
	protected static String generateSimpleCellJava() {
		String text = "package javafxmlsimplelistexample;\n" + 
				"\n" + 
				"public class Cell {\n" + 
				"    \n" + 
				"    String element1;\n" + 
				"    \n" + 
				"    public Cell(String element1) {\n" + 
				"        this.element1 = element1;\n" + 
				"    }\n" + 
				"    \n" + 
				"}";
		
		return text;
	}
	
	protected static String generateListCellImpl() {
		
		String text = "package simplelist;\n" + 
				"\n" + 
				"import javafx.scene.control.ListCell;\n" + 
				"\n" + 
				"public class ListCellImpl extends ListCell<Cell> {\n" + 
				"    \n" + 
				"    @Override\n" + 
				"    public void updateItem(Cell cell, boolean empty) {\n" + 
				"        super.updateItem(cell, empty);\n" + 
				"        if (cell != null) {\n" + 
				"            CellFXML cFXML = new CellFXML();\n" + 
				"            cFXML.setInfo(cell);\n" + 
				"            setGraphic(cFXML.getPane());\n" + 
				"        }\n" + 
				"    }\n" + 
				"    \n" + 
				"}";
		
		return text;
		
	}
	
	protected static String generateListController() {
		String text = "package simplelist;\n" + 
				"\n" + 
				"import javafx.collections.FXCollections;\n" + 
				"import javafx.collections.ObservableList;\n" + 
				"import javafx.scene.control.ListView;\n" + 
				"import javafx.util.Callback;\n" + 
				"\n" + 
				"public class ListController {\n" + 
				"\n" + 
				"    ObservableList obsList;\n" + 
				"    \n" + 
				"    public ListController(ListView listView) {\n" + 
				"        \n" + 
				"        obsList = FXCollections.observableArrayList();\n" + 
				"        \n" + 
				"        listView.setItems(obsList);\n" + 
				"        listView.setCellFactory(new Callback<ListView<Cell>, ListCellImpl>() {\n" + 
				"\n" + 
				"            @Override\n" + 
				"            public ListCellImpl call(ListView<Cell> param) {\n" + 
				"                return new ListCellImpl();\n" + 
				"            }\n" + 
				"            \n" + 
				"        });\n" + 
				"        \n" + 
				"    }   \n" + 
				"    \n" + 
				"}";
		
		return text;
	}
	
	public String getModifiedFileContents(File fileToModify, String className, String fileWithData) {
		System.out.println("trying to modify");
		try {
			FileReader fr = new FileReader(fileToModify);
			BufferedReader br = new BufferedReader(fr);
			StringBuilder builder = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				if (line.contains("import javafx.fxml.FXML")) {
					line += "\n" +
							"import javafx.fxml.Initializable\n" +
							"import javafx.scene.control.ListCell\n" +
							"import javafx.scene.control.ListView\n" +
							"import javafx.util.Callback\n" +
							"import javafx.collections.FXCollections\n" +
							"import list.ListFileParser\n" + 
							"import java.net.URL\n" + 
							"import java.util.ResourceBundle\n";
				}
				
				if (line.contains("class ")) {
					line = line.replace("{", "");
					line += 
							"implements Initializable {\n" +
							"\t@FXML\n" +
							"\tListView listView\n";
					
				}
				
				builder.append(line + "\n");
				line = br.readLine();
			}
			
			br.close();
			
			String builderString = builder.toString();
			String factoryClassName = className + "Factory";
			String modified = builderString.substring(0, builderString.length() - 2) +
					"\n" +
					"\toverride initialize(URL location, ResourceBundle resources) {\n" + 
						"\t\tval obsList = FXCollections.observableArrayList();\n" +
						"\t\tobsList.addAll(" + factoryClassName + ".gettAllListData(ListFileParser.parseListFile(\"" + fileWithData + "\")));\n" +
						
						"\t\tlistView.setItems(obsList);\n" +
						"\t\tlistView.setCellFactory(new Callback<ListView<" + className + ">, ListCell<" + className + ">>() {\n\n" +
							
							"\t\t\toverride call(ListView<" + className +"> param) {\n" +
								"\t\t\t\treturn new " + className + "Cell\n" +
							"\t\t\t}\n\n" +
								
						"\t\t});\n\n" +
					
					"\t}\n\n" +
					
				"}";
			return modified;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	
	protected static String generateListPopulator() {
		
		return "generateListPopulator()";
		
		
		
	}
}

