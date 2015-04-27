package datagenerator;

import application.Constants;

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
		MappingGenerator.getInstance().clear();
		ListGenerator.getInstance().clear();
		
	}
	
	public void generate(String name) {
		
		ContextGenerator cg = ContextGenerator.getInstance();
		cg.generatePaths();
		TypeGenerator.getInstance().setupAssignmentReferences();
		TypeGenerator.getInstance().generateFxmlForTypes("cake");
//		AssignmentGenerator.getInstance().setContexts(ContextGenerator.getInstance().getAllContexts());
		AssignmentGenerator.getInstance().doSetup();
		
		ScreenEcoreGenerator seg = new ScreenEcoreGenerator();
		seg.generateEcoreForScreen(name);
//		XMIExporter xmiExpoter = new XMIExporter();
////		xmiExpoter.setGenerators(AssignmentGenerator.getInstance(), ContextGenerator.getInstance(), TypeGenerator.getInstance());
//		xmiExpoter.exportXMI(name, Constants.GENERATED_DIRECTORY);
//		MappingGenerator.getInstance().assignValues(Constants.GENERATED_DIRECTORY + name + ".xmi");
//		ListGenerator.getInstance().mappings = MappingGenerator.getInstance().mappings;
//		ListGenerator.getInstance().createLists();
//				
//		xmiExpoter = new XMIExporter();
////		xmiExpoter.setGenerators(AssignmentGenerator.getInstance(), ContextGenerator.getInstance(), TypeGenerator.getInstance());
//		xmiExpoter.exportXMI(name, Constants.GENERATED_DIRECTORY);
		
		
	}
	
}
