package datagenerator;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import javafx.util.Pair;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class AssignmentGenerator {
	
	private static AssignmentGenerator instance = null;
	
	public ArrayList<EObject> assignments;
	public ArrayList<EObject> contexts;
	
	//EMF
	ResourceSet resourceSet;
	Resource ecoreResource;
	EPackageImpl dataPackage;
	EFactory dataFactory;
	EClass assignmentClass;
	EStructuralFeature statementFeature, specificStatementFeature, rootContextFeature, layoutIDFeature;
	
	public static AssignmentGenerator getInstance() {
		
		if (instance == null)
			instance = new AssignmentGenerator();
		
		return instance;
		
	}
	
	private AssignmentGenerator() {
		
		assignments = new ArrayList<EObject>();
		contexts = new ArrayList<EObject>();
		
		loadAssignmentDefinition();
		
	}
	
	public void setContext(ArrayList<EObject> contexts) {
		
		this.contexts = contexts;
		
	}
	
	public void clear() {
		
		contexts = new ArrayList<EObject>();
		
		assignments.clear();
		
	}
	
	private void loadAssignmentDefinition() {
		
		String pathToEcore = "/Users/Magnus/Master/Workspace_final/MasterThesisCodeFinal/WireframeToJavaFX-master/JavaFX Test/src/datagenerator/Data.ecore";
		
		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		
		URI existingInstanceUri = URI.createFileURI(pathToEcore);
		
		ecoreResource = resourceSet.getResource(existingInstanceUri, true);
		dataPackage = (EPackageImpl)ecoreResource.getContents().get(0);
		dataFactory = dataPackage.getEFactoryInstance();
		assignmentClass = (EClass) dataPackage.getEClassifier("Assignment");
		
		statementFeature = assignmentClass.getEStructuralFeature("statement");
		specificStatementFeature = assignmentClass.getEStructuralFeature("specificStatement");
		rootContextFeature = assignmentClass.getEStructuralFeature("rootContext");
		layoutIDFeature = assignmentClass.getEStructuralFeature("layoutID");
		
	}
	
	public void generateDecorator(String[] strings, int id) {
		
		for (int i = 0; i < strings.length; i++) {
			
			if (i > 0) break;	//temporary statement until more functionality (types) is implemented
			
			String specificStatement = strings[i].trim();

			EObject assignmentObject = dataFactory.create(assignmentClass);
			assignmentObject.eSet(specificStatementFeature, specificStatement);
			assignmentObject.eSet(layoutIDFeature, id);
			//Leave the rest of the properties unassigned for now. Will be assigned later in the program flow.
			
			assignments.add(assignmentObject);

		}
		
	}
	
	public void generatePaths() {
		
		for (EObject assignment : assignments) {
			
			Pair<String, EObject> pair = generateContextPath((String) assignment.eGet(specificStatementFeature));
			assignment.eSet(statementFeature, pair.getKey());
			assignment.eGet(rootContextFeature);
			assignment.eSet(rootContextFeature, pair.getValue());
			
		}
		
	}

	private Pair<String, EObject> generateContextPath(String statement) {
		
		statement = statement.trim();
		String parentName = getParentName(statement);
		statement = statement.replace(parentName, "");
		
		EObject parent = getContextNamed(parentName, contexts);
		EObject root = (EObject) parent.eGet(parent.eClass().getEStructuralFeature("rootContext"));
		statement = ((String) parent.eGet(parent.eClass().getEStructuralFeature("statement"))) + statement;
		
		return new Pair<String, EObject>(statement, root);
		
	}
	
	private String getParentName(String statement) {
		
		statement = statement.trim();
		
		int posOfDot = statement.indexOf('.');
		int posOfArrow = statement.indexOf("->");
		int minPos = min(posOfDot, posOfArrow);
		
		if (minPos == -1) {	//The entire word is the parent
			return statement;
		} else {
			return statement.substring(0, minPos);
		}
		
	}
	
	private int min(int a, int b) {
		
		if (a == -1 && b == -1)	//Redundant, but makes it easy to understand that this case is handled. This case is actually handled in the return statement
			return -1;
		
		if (a == -1)
			return b;
		
		if (b == -1)
			return a;
		
		return a < b ? a : b;
		
	}
	
	private EObject getContextNamed(String name, ArrayList<EObject> list) {
		
		EStructuralFeature nameFeature = list.get(0).eClass().getEStructuralFeature("name");
		
		for (EObject eObject : list) {
			String eName = (String) eObject.eGet(nameFeature);
			if (name.equals(eName)) {				
				return eObject;
			}
		}
		
		throw new NoSuchElementException(String.format("Context named \"%s\" doesn't exist. \"%s\" might be misspelled.", name, name));
		
	}

}
