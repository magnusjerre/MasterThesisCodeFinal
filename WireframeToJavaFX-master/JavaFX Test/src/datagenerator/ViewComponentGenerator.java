package datagenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.xtext.xbase.lib.Pair;

import application.Constants;
import application.LocationUtils;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Image;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;
import com.wireframesketcher.model.WidgetGroup;

import data.Assignment;
import data.DataFactory;
import data.ViewComponent;

/**
 * Responsible for creating and storing all the ViewComponent decorators for a specific screen file.
 * 
 * Each ViewComponent will have an fxml file associated with it which describes how the viewcomponent 
 * will be displayed.
 * @author Magnus Jerre
 *
 */
public class ViewComponentGenerator {
	
	public DoubleList<ViewComponent, Master> viewComponents = new DoubleList<ViewComponent, Master>();
	public HashMap<Master, Pair<Arrow, Widget>> masterMap;
	
	private DoubleList<Assignment, Master> assignments;
	
	public ViewComponentGenerator(DoubleList<Assignment, Master> assignments) {
		
		this.assignments = assignments;
		
	}
	
	public void generateDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		
		if (masterMap == null) {
			masterMap = map;
		}
		
		if (strings.length > 1 || strings.length < 1) {
			throw new RuntimeException(String.format("Illegal number of lines in ViewComponent decorator. First line states: %s", strings[0]));
		}
		
		String[] split = DataUtils.extractNameAndType(strings[0]);
		String name = split[0];
		String dataType = split[1];

		ViewComponent viewComponent = DataFactory.eINSTANCE.createViewComponent();
		viewComponent.setName(name);
		viewComponent.setExpectedType(dataType);
		viewComponent.setWidget(masterMap.get(master).getValue());
		viewComponents.add(viewComponent, master);

	}
	
	/**
	 * Creates the necessary references between the assignments that should be part of specific ViewComponents.
	 */
	public void setupAssignmentReferences() {
		
		if (viewComponents.size() == 0) {
			return;
		}
		
		for (Assignment assignment : assignments.getElementsIterable()) {
			
			if (shouldBePartOfViewComponent(assignment)) {
				ViewComponent component = getViewComponentForAssignment(assignment);
				setupConnection(assignment, component);
			}
			
		}
		
	}
	
	private boolean shouldBePartOfViewComponent(Assignment assignment) {
		
		if (getViewComponentForAssignment(assignment) == null) {
			return false;
		}
		return true;
	}

	/**
	 * Adds a reference from the Assignment to the ViewComponent and vice versa. In addition, the assignment's widget
	 * is updated so that it correctly points to the widget for the ViewComponent.
	 * @param assignment
	 * @param component
	 */
	private void setupConnection(Assignment assignment, ViewComponent component) {
		
		component.getAssignments().add(assignment);
		assignment.setPartOfComponent(component);
		assignment.setWidget(getCorrectWidget(assignment));
		
	}
	
	/**
	 * Return the correct widget for the Assignment that is part of a ViewComponent.
	 * @param assignment
	 * @return
	 */
	private Widget getCorrectWidget(Assignment assignment) {
		
		Pair<Arrow, Widget> assignmentPair = getPair(assignment, assignments);
		Arrow arrow = assignmentPair.getKey();
		Widget widget = assignmentPair.getValue();
		return WidgetUtils.getSecondShallowestWidget(arrow, widget);
		
	}

	private Pair<Arrow, Widget> getPair(Assignment assignment, DoubleList<Assignment, Master> newAssignments) {

		return masterMap.get(newAssignments.getMaster(assignment));
		
	}

	private ViewComponent getViewComponentForAssignment(Assignment assignment) {

		if (masterMap == null) {
			return null;
		}
		
		Master assignmentMaster = assignments.getMaster(assignment);
		Pair<Arrow, Widget> pairForAssignment = masterMap.get(assignmentMaster);
		Master correctMaster = null;
		
		for (Master viewComponent : viewComponents.getMasterIterable()) {
			
			Widget widget = masterMap.get(viewComponent).getValue();
			if (widget.equals(pairForAssignment.getValue())) {
				correctMaster = viewComponent;
				break;
			}
			
		}
		
		if (correctMaster == null) {
			return null;
		}
		
		return viewComponents.getElement(correctMaster);
		
	}
	
	/**
	 * Creates and saves a fxml-file for the ViewComponent.
	 * @param screenName
	 */
	public void generateFxmlForViewComponents(String screenName) {
		
		for (ViewComponent viewComponent : viewComponents.getElementsIterable()) {
			
			String fileName = String.format("%s-%s.fxml", screenName, viewComponent.getName());
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
			
			WidgetGroup widgetGroup = (WidgetGroup) viewComponent.getWidget();
			fxmlBuidler.append(String.format(
					"<AnchorPane xmlns:fx=\"http://javafx.com/fxml/1\" prefHeight=\"%d\" prefWidth=\"%d\" >\n" + 
							"    <children>\n", 
					widgetGroup.getMeasuredHeight(), widgetGroup.getMeasuredWidth()));
			
			for (Widget widget : widgetGroup.getWidgets()) {
				
				//Defaults to type Label
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
//				fw = new FileWriter(new File(Constants.GENERATED_DIRECTORY + fileName));
				
				
				fw = new FileWriter(new File(LocationUtils.getFilePathSrc(LocationUtils.GENERATED_PACKAGE, fileName)));
				
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
