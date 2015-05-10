package datagenerator;

import java.util.ArrayList;
import java.util.List;

import data.Context;
import data.DataFactory;

public class ContextGenerator {
	
	public List<Context> contexts;
	
	public ContextGenerator() {
		
		contexts = new ArrayList<Context>();
		
	}

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
	
	public List<Context> getAllContexts() {
		
		return contexts;
		
	}
	
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
