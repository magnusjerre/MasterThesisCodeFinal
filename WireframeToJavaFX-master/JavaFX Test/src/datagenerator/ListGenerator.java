package datagenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import application.Constants;

public class ListGenerator {
	
	private static ListGenerator instance;
	public ArrayList<EObject> mappings;
	private DataUtils utils;
	
	public ArrayList<String> listViewNames;
	
	private ListGenerator() {
		utils = DataUtils.getInstance();
		listViewNames = new ArrayList<String>();
	}
	
	public static ListGenerator getInstance() {
		
		if (instance == null) {
			instance = new ListGenerator();
		}
		
		return instance;
		
	}
	
	public void setMappings(ArrayList<EObject> mappings) {
		
		this.mappings = mappings;
		
	}
	
	public void clear() {
		
		listViewNames.clear();
		
	}
	
	public void createLists() {
		
		int PosOfListViewName = 0;
		for (EObject mapping : mappings) {
			
			boolean isList = (boolean) mapping.eGet(utils.mIsListFeature);
			if (isList) {
				
				String name = listViewNames.get(PosOfListViewName);
				
				if (isSimpleList(mapping)) {
					handleSimpleList(mapping, name);
				} else {
					//handle advanced list
				}
				PosOfListViewName++;
				
			}
			
		}
		
		File f = new File(Constants.FXML_DIRECTORY + "ScreenNavigatorControllerfifthscreen.xtend");
		if (f.exists()) {
			writeFile(Constants.FXML_DIRECTORY + "ScreenNavigatorControllerfifthscreen.xtend", getModifiedFileContents(f));
		}
		
	}
	
	private void handleSimpleList(EObject listMapping, String listViewName) {
		
		writeFile(Constants.FXML_DIRECTORY + "simple_cell.fxml", generateFxmlForSimpleListelements());
		writeFile(Constants.FXML_DIRECTORY + "Cell.java", generateCellJava());
		writeFile(Constants.FXML_DIRECTORY + "CellFXML.java", generateCellFxmlForSimpleListElements());
		writeFile(Constants.FXML_DIRECTORY + "ListCellImpl.java", generateListCellImpl());
		writeFile(Constants.FXML_DIRECTORY + "ListController.java", generateListController());
		
	}
	
	private void writeFile(String fileName, String content) {
		
		BufferedWriter bw = null;
		
		try {
			FileWriter fw = new FileWriter(new File(fileName));
			bw = new BufferedWriter(fw);
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	/**
	 * Assumes all child-mappings of listMapping are the same with respect to 
	 * the number of children each mapping has.
	 * @param listMapping
	 * @return
	 */
	private boolean isSimpleList(EObject listMapping) {
		
		EList<EObject> childMappings = (EList<EObject>) listMapping.eGet(utils.mMappingsFeature);
		EObject firstMapping = childMappings.get(0);
		if (firstMapping.eGet(utils.mValueFeature) != null) {
			return true;
		}
		
		return false;
		
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
		
		String text =  "package application;\n" + 
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
	
	protected static String generateCellJava() {
		String text = "package application;\n" + 
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
		
		String text = "package application;\n" + 
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
		String text = "package application;\n" + 
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
	
	public String getModifiedFileContents(File fileToModify) {
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
							"import javafx.scene.control.ListView\n" +
							"import java.net.URL\n" + 
							"import java.util.ResourceBundle\n";
				}
				
				if (line.contains("class ")) {
					line = line.replace("{", "");
					line += 
							"implements Initializable {\n" +
							createListFields(listViewNames);
					
				}
				
				builder.append(line + "\n");
				line = br.readLine();
			}
			
			br.close();
			
			String initialize = "	override initialize(URL location, ResourceBundle resources) {\n" + 
					"		\n" + 
					createLists(listViewNames) + 
					"		lc.obsList.add(new Cell(\"Anniken\"))\n" + 
					"		\n" + 
					"	}\n" +
					"\n" +
					"}\n";
			
			String modified = builder.toString().substring(0,  builder.length() - 2) + "\n" + initialize;
			return modified;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String createListFields(ArrayList<String> listViewNames) {
		
		StringBuilder builder = new StringBuilder();
		for (String name : listViewNames) {
			builder.append("\n" +
					"\t@FXML\n" +
					"\tListView " + name + "\n");
		}
		return builder.toString();
		
	}
	
	private String createLists(ArrayList<String> listViewNames) {
		
		StringBuilder builder = new StringBuilder();
		for (String name : listViewNames) {
			builder.append("		val lc" + name + " = new ListController(" + name + ")\n");
		}
		
		return builder.toString();
		
	}
	
	protected static String generateListPopulator() {
		
		return "generateListPopulator()";
		
		
		
	}
}

