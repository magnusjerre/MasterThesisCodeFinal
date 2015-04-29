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
		
		DataUtils.getInstance().clear();
		ContextGenerator.getInstance().clear();
		TypeGenerator.getInstance().clear();
		AssignmentGenerator.getInstance().clear();
		ListGenerator.getInstance().clear();
		
	}
	
	public void generate(String name) {
		
		ContextGenerator cg = ContextGenerator.getInstance();
		cg.generatePaths();
		TypeGenerator.getInstance().setupAssignmentReferences();
		TypeGenerator.getInstance().generateFxmlForTypes(name);
		AssignmentGenerator.getInstance().doSetup();
		
		ScreenEcoreGenerator seg = new ScreenEcoreGenerator();
		seg.generateEcoreForScreen(name);
		
	}
	
}
