package datagenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Pair;

import application.Constants;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Image;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;
import com.wireframesketcher.model.WidgetGroup;

public class TypeGenerator {
	
	private static TypeGenerator instance = null;
	private DataUtils utils;
	
	public DoubleList<EObject, Master> list;
	public HashMap<Master, Pair<Arrow, Widget>> masterMap;
	
	
	private TypeGenerator() {
		
		utils = DataUtils.getInstance();
		list = new DoubleList<EObject, Master>();
		
	}
	
	public static TypeGenerator getInstance() {
		
		if (instance == null)
			instance = new TypeGenerator();
		
		return instance;
		
	}
	
	public void clear() {
		
		masterMap = null;
		list.clear();
		
	}
	
	public void generateDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		
		if (masterMap == null) {
			masterMap = map;
		}
		
		if (strings.length > 1 || strings.length < 1) {
			throw new RuntimeException(String.format("Illegal number of lines in Type decorator. First line states: %s", strings[0]));
		}
		
		String name = strings[0].trim();

		EObject typeObject = utils.dataFactory.create(utils.typeClass);
		typeObject.eSet(utils.tNameFeature, name);
		typeObject.eSet(utils.tWidgetFeature, masterMap.get(master).getValue());
		//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
		
		list.add(typeObject, master);

	}
	
	public void setupAssignmentReferences() {
		
		if (list.size() == 0) {
			return;
		}
		
		for (EObject assignment : AssignmentGenerator.getInstance().assignments.getElementsIterable()) {
			
			if (isPartOfType(assignment)) {
				EObject type = getTypeForAssignment(assignment);
				setupConnection(assignment, type);
			}
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void setupConnection(EObject assignment, EObject type) {

		((EList<EObject>)type.eGet(utils.tAssignmentsFeature)).add(assignment);
		assignment.eSet(utils.a2PartOfComponentFeature, type);
		assignment.eSet(utils.a2WidgetFeature, getCorrectWidget(assignment));
		
		String assignmentUsesTypeNamed = (String) assignment.eGet(utils.a2UseComponentNamedFeature);
		if (assignmentUsesTypeNamed != null) {
			EObject typeToUse = findTypeNamed(assignmentUsesTypeNamed);
			((EList<EObject>)type.eGet(utils.tTypesFeature)).add(typeToUse);
		}
		
	}
	
	private Widget getCorrectWidget(EObject assignment) {
		
		Pair<Arrow, Widget> assignmentPair = getPair(assignment, AssignmentGenerator.getInstance().assignments);
		Arrow arrow = assignmentPair.getKey();
		Widget widget = assignmentPair.getValue();
		return WidgetUtils.getSecondShallowestWidget(arrow, widget);
		
	}
	
	private Pair<Arrow, Widget> getPair(EObject eObject, DoubleList<EObject, Master> doubleList) {
		
		return masterMap.get(doubleList.getMaster(eObject));
		
	}


	private EObject getTypeForAssignment(EObject assignment) {
		
		if (masterMap == null) {
			return null;
		}
		
		Master assignmentMaster = AssignmentGenerator.getInstance().assignments.getMaster(assignment);
		Pair<Arrow, Widget> pairForAssignment = masterMap.get(assignmentMaster);
		Master correctMaster = null;
		
		for (Master type : list.getMasterIterable()) {
			
			Widget widget = masterMap.get(type).getValue();
			if (widget.equals(pairForAssignment.getValue())) {
				correctMaster = type;
				break;
			}
			
		}
		
		if (correctMaster == null) {
			return null;
		}
		
		return list.getElement(correctMaster);
		
	}
	
	protected EObject findTypeNamed(String name) {
		
		for (EObject type : list.getElementsIterable()) {
			
			String typeName = (String) type.eGet(utils.tNameFeature);
			if (typeName.equals(name)) {
				return type;
			}
			
		}
		
		throw new RuntimeException(String.format("Couldn't find Type with name %s", name));
		
	}
	
	
	protected boolean isPartOfType(EObject assignment) {
		
		if (getTypeForAssignment(assignment) == null) {
			return false;
		}

		return true;
		
	}
	
	/**
	 * This will currently only generate the base cases, i.e not types containing types
	 * @param screenName
	 */
	public void generateFxmlForTypes(String screenName) {
		
		for (EObject type : list.getElementsIterable()) {
			
			String fileName = String.format("%s-Type%s.fxml", screenName, (String) type.eGet(utils.tNameFeature));
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
			
			WidgetGroup widgetGroup = (WidgetGroup) type.eGet(utils.tWidgetFeature);
			fxmlBuidler.append(String.format(
					"<AnchorPane xmlns:fx=\"http://javafx.com/fxml/1\" prefHeight=\"%d\" prefWidth=\"%d\" >\n" + 
							"    <children>\n", 
					widgetGroup.getMeasuredWidth(), widgetGroup.getMeasuredHeight()));
			
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
					element = String.format(
							"        <Group layoutX=\"%d\" layoutY=\"%d\" fx:id=\"%d\" />" +
									"\n",
									widget.getX(), widget.getY(), widget.getId().intValue());
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}
		
	}
	
}
