package datagenerator;

import java.util.ArrayList;
import java.util.List;

import data.Context;
import data.DataFactory;

public class ContextGenerator {
	
	private static ContextGenerator instance = null;

	public List<Context> contexts;
	
	public static ContextGenerator getInstance() {
		
		if (instance == null)
			instance = new ContextGenerator();
		
		return instance;
		
	}
	
	private ContextGenerator() {
		
		contexts = new ArrayList<Context>();
		
	}

	public void clear() {
		
		contexts.clear();
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

}
