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
		
		
		String[] nameAndDataType = DataUtils.extractNameAndType(strings[0]);
		selection.setName(nameAndDataType[0]);
		selection.setExpectedType(nameAndDataType[1]);
		selection.setLayoutId(map.get(master).getValue().getId().toString());
		
		selections.add(selection);
		
	}

}
