package datagenerator;


public class DataGenerator {

	
	private static DataGenerator instance = null;
	
	private DataGenerator() {
		
	}
	
	public static DataGenerator getInstance() {
		
		if (instance == null) {
			instance = new DataGenerator();
		}
		return instance;
		
	}
	
	public void clear() {
		
		ContextGenerator.getInstance().clear();
		TypeGenerator.getInstance().clear();
		AssignmentGenerator.getInstance().clear();
		ListGenerator.getInstance().clear();
		
	}
	
	public void generate(String name) {
		
		ContextGenerator cg = ContextGenerator.getInstance();
		TypeGenerator.getInstance().setupAssignmentReferences();
		TypeGenerator.getInstance().generateFxmlForTypes(name);
		
		ScreenEcoreGenerator seg = new ScreenEcoreGenerator();
		seg.generateEcoreForScreen(name);
		
		ListGenerator.getInstance().createList(seg.screenPackage);
		
	}
	
}
