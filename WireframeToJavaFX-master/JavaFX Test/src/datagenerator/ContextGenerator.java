package datagenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import application.LocationUtils;
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
		String specificStatement = getLocationForStatement(splitted[1].trim());
		
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
	
	private String getPackageForStatement(String statement) {
		
		int indexOfLastSlash = statement.lastIndexOf("/");
		String packageLocation = statement.substring(0, indexOfLastSlash);
		indexOfLastSlash = packageLocation.lastIndexOf("/");
		if (packageLocation.length() > 1) {
			String packageName = packageLocation.substring(indexOfLastSlash + 1);
			
			return packageName;
		}
		
		return null;
	}
	
	private String getFileNameForStatement(String statement) {
		
		int indexOfLastSlash = statement.lastIndexOf("/");
		if (statement.length() > indexOfLastSlash + 1) {
			String fileName = statement.substring(indexOfLastSlash + 1);
			return fileName;
		}
		
		return null;
		
	}
	
	private boolean fileExists(String fileName, String packageName) {
		
		
		String location = LocationUtils.getFilePathSrc(packageName, fileName);
		File f = new File(location);
		if (f.exists()) {
			return true;
		}
		
		return false;
		
		
	}
	
	private boolean statementReferencesPackage(String statement) {
		
		String fileName = getFileNameForStatement(statement);
		String packageName = getPackageForStatement(statement);
		
		return fileExists(fileName, packageName);
		
	}
	
	/**
	 * Returns the location for the statement. If the statement references a package and filename only, the
	 * absolute path for it will be generated. Otherwise, the statement is returned.
	 * @param statement
	 * @return
	 */
	private String getLocationForStatement(String statement) {
		
		
		if (statement.startsWith("/") && statementReferencesPackage(statement)) {
				
			String fileName = getFileNameForStatement(statement);
			String packageName = getPackageForStatement(statement);
			String location = LocationUtils.getFilePathSrc(packageName, fileName);
			
			return location;
		}
		
		return statement;
		
	}

}
