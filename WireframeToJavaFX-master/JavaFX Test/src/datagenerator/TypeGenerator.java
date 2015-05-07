package datagenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.xtext.xbase.lib.Pair;

import application.Constants;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Image;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;
import com.wireframesketcher.model.WidgetGroup;

import data.Assignment;
import data.DataFactory;
import data.ViewComponent;

public class TypeGenerator {
	
	private static TypeGenerator instance = null;
	
	public DoubleList<ViewComponent, Master> theList;
	public HashMap<Master, Pair<Arrow, Widget>> masterMap;
	
	
	private TypeGenerator() {
		
		theList = new DoubleList<ViewComponent, Master>();
		
	}
	
	public static TypeGenerator getInstance() {
		
		if (instance == null)
			instance = new TypeGenerator();
		
		return instance;
		
	}
	
	public void clear() {
		
		masterMap = null;
		theList.clear();
		
	}
	
	public void generateDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		
		if (masterMap == null) {
			masterMap = map;
		}
		
		if (strings.length > 1 || strings.length < 1) {
			throw new RuntimeException(String.format("Illegal number of lines in Type decorator. First line states: %s", strings[0]));
		}
		
		String[] split = getNameAndType(strings[0]);
		String name = split[0];
		String type = split[1];

		ViewComponent viewComponent = DataFactory.eINSTANCE.createViewComponent();
		viewComponent.setName(name);
		viewComponent.setExpectedType(type);
		viewComponent.setWidget(masterMap.get(master).getValue());
		theList.add(viewComponent, master);

	}
	
	/**
	 * The first element is the name of the component, the second element is the name of the expected type for the component.
	 * @param string
	 * @return
	 */
	private String[] getNameAndType(String string) {
		
		string = string.trim();
		
		String[] split = new String[]{string};
		String name = null, type = null;
		if (string.contains(":")) {	//Type declaration after colon
			split = string.split(":");
			name = split[0];
			type = split[1];
		} else if (string.contains(" ")) {	//Type declaration before blank space, like normal java programming
			split = string.split(" ");
			name = split[1];
			type = split[0];
		} 

		if (split.length != 2) {
			throw new RuntimeException(String.format("Error! The component \"%s\" is either malformed or it doesn't declare a type.", string));
		}
		
		return new String[] {name.trim(), type.trim()};
		
	}
	
	public void setupAssignmentReferences() {
		
		if (theList.size() == 0) {
			return;
		}
		
		for (Assignment assignment : AssignmentGenerator.getInstance().assignments.getElementsIterable()) {
			
			if (shouldBePartOfViewComponent(assignment)) {
				ViewComponent component = getComponentForAssignment(assignment);
				setupConntection(assignment, component);
			}
			
		}
		
	}
	
	private boolean shouldBePartOfViewComponent(Assignment assignment) {
		
		if (getComponentForAssignment(assignment) == null) {
			return false;
		}
		return true;
	}

	private void setupConntection(Assignment assignment, ViewComponent component) {
		
		component.getAssignments().add(assignment);
		assignment.setPartOfComponent(component);
		assignment.setWidget(getCorrectWidget2(assignment));
		
	}
	
	private Widget getCorrectWidget2(Assignment assignment) {
		
		Pair<Arrow, Widget> assignmentPair = getPair(assignment, AssignmentGenerator.getInstance().assignments);
		Arrow arrow = assignmentPair.getKey();
		Widget widget = assignmentPair.getValue();
		return WidgetUtils.getSecondShallowestWidget(arrow, widget);
		
	}

	private Pair<Arrow, Widget> getPair(Assignment assignment, DoubleList<Assignment, Master> newAssignments) {

		return masterMap.get(newAssignments.getMaster(assignment));
		
	}

	private ViewComponent getComponentForAssignment(Assignment assignment) {

		if (masterMap == null) {
			return null;
		}
		
		Master assignmentMaster = AssignmentGenerator.getInstance().assignments.getMaster(assignment);
		Pair<Arrow, Widget> pairForAssignment = masterMap.get(assignmentMaster);
		Master correctMaster = null;
		
		for (Master type : theList.getMasterIterable()) {
			
			Widget widget = masterMap.get(type).getValue();
			if (widget.equals(pairForAssignment.getValue())) {
				correctMaster = type;
				break;
			}
			
		}
		
		if (correctMaster == null) {
			return null;
		}
		
		return theList.getElement(correctMaster);
		
	}
	
	/**
	 * This will currently only generate the base cases, i.e not types containing types
	 * @param screenName
	 */
	public void generateFxmlForTypes(String screenName) {
		
		for (ViewComponent type : theList.getElementsIterable()) {
			
			String fileName = String.format("%s-%s.fxml", screenName, type.getName());
			StringBuilder fxmlBuidler = new StringBuilder();
			fxmlBuidler.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
					"\n" + 
					"<?import java.lang.*?>\n" + 
					"<?import java.util.*?>\n" + 
					"<?import javafx.scene.*?>\n" + 
					"<?import javafx.scene.control.*?>\n" + 
					"<?import javafx.scene.layout.*?>\n" +
					"<?import javafx.scene.image.*?>" +
					"\n" +
					"\n");
			
			WidgetGroup widgetGroup = (WidgetGroup) type.getWidget();
			fxmlBuidler.append(String.format(
					"<AnchorPane xmlns:fx=\"http://javafx.com/fxml/1\" prefHeight=\"%d\" prefWidth=\"%d\" >\n" + 
							"    <children>\n", 
					widgetGroup.getMeasuredHeight(), widgetGroup.getMeasuredWidth()));
			
			for (Widget widget : widgetGroup.getWidgets()) {
				
				//Assume it is a label for now
				String nodeType = "Label";
				String height = "minHeight", width = "minWidth";
				if (widget instanceof Image) {
					nodeType = "ImageView";
					height = "fitHeight";
					width = "fitWidth";
				} 
				
				String element = String.format(
						"        <%s layoutX=\"%d\" layoutY=\"%d\" %s=\"%d\" %s=\"%d\" fx:id=\"%d\" />" +
								"\n",
								nodeType, widget.getX(), widget.getY(), height, widget.getMeasuredHeight(), width, widget.getMeasuredWidth(), widget.getId().intValue());
				
				if (widget instanceof WidgetGroup) {
					
					if (((WidgetGroup) widget).getName().contains("list")) {
						
						String orientation = "VERTICAL";
						String[] split = ((WidgetGroup) widget).getName().split(" ");
						if (split.length > 1) {
							if (split[1].startsWith("h") || split[1].startsWith("H")) {
								orientation = "HORIZONTAL";
							}
						}
						
						element = String.format(
								"        <ListView layoutX=\"%d\" layoutY=\"%d\" prefHeight=\"%d\" prefWidth=\"%d\" orientation=\"%s\" fx:id=\"%d\" />" +
										"\n",
										widget.getX(), widget.getY(), widget.getMeasuredHeight(), widget.getMeasuredWidth(), orientation, widget.getId().intValue());
					} else {
						element = String.format(
								"        <Group layoutX=\"%d\" layoutY=\"%d\" fx:id=\"%d\" />" +
										"\n",
										widget.getX(), widget.getY(), widget.getId().intValue());
					}
					
				}
				
				fxmlBuidler.append(element);
				
			}
			
			
			String end = "    </children>\n" + 
					"</AnchorPane>";
			
			fxmlBuidler.append(end);
			
			FileWriter fw = null;
			try {
				fw = new FileWriter(new File(Constants.GENERATED_DIRECTORY + fileName));
				fw.write(fxmlBuidler.toString());
				fw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		
	}
	
}
