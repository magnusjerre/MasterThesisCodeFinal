package datagenerator;

import java.util.ArrayList;
import java.util.List;

import data.Context;
import data.DataFactory;

/**
 * Responsible for creating and storing all the Context decorators for a specific screen file.
 * @author Magnus Jerre
 *
 */
public class ContextGenerator {
	
	public List<Context> contexts = new ArrayList<Context>();

	public void generateDecorator(String[] strings) {
		
		if (strings.length != 1)
			throw new RuntimeException("Illegal format for context2");
		
		String line = strings[0];

		String[] splitted = line.split(" = ", 2);
		String name = splitted[0].trim();
		String specificStatement = splitted[1].trim();
		
		Context context = DataFactory.eINSTANCE.createContext();
		context.setName(name);
		context.setStatement(specificStatement);
		contexts.add(context);

	}
	
	/**
	 * Creates a new list containing only the Contexts that are directly referencing an xmi-file. Their
	 * statements start with a slash "/".
	 * @return
	 */
	public List<Context> getXMIContexts() {
		
		List<Context> objects = new ArrayList<Context>();
		
		for (Context context : contexts) {
			
			if (context.getStatement().startsWith("/")) {
				objects.add(context);
			}
		}
		
		return objects;
		
	}

}
