package datagenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.xtext.xbase.lib.Pair;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.Master;
import com.wireframesketcher.model.Widget;

import data.DataFactory;
import data.Selection;

public class SelectionGenerator {
	
	public List<Selection> selections; 
	
	public SelectionGenerator() {
		
		selections = new ArrayList<Selection>();
		
	}
	
	public void generateDecorator(String[] strings, Master master, HashMap<Master, Pair<Arrow, Widget>> map) {
		
		if (strings.length != 1) {
			throw new RuntimeException("The decorator is not well formed, it contains either too many or no lines.\n"
					+ "The Selection decorator allows either only one line.");
		}
		
		Selection selection = DataFactory.eINSTANCE.createSelection();
		
		
		String[] nameAndType = getNameAndType(strings[0]);
		selection.setName(nameAndType[0]);
		selection.setExpectedType(nameAndType[1]);
		selection.setLayoutId(map.get(master).getValue().getId().toString());
		
		selections.add(selection);
		
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
	

}
