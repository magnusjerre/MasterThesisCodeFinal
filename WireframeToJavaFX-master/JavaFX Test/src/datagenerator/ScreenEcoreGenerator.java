package datagenerator;

import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import application.Constants;

public class ScreenEcoreGenerator {
	
	private DataUtils utils;
	private static final String ANNOTATION_SOURCE = "wireframe";
	
	public ScreenEcoreGenerator() {
		
		utils = DataUtils.getInstance();
		
	}
	
	public void generateEcoreForScreen(String screenName) {
		
		if (screenName.endsWith(".screen")) {
			screenName = screenName.replace(".screen", "");
		}
		
		EClass screenClass = EcoreFactory.eINSTANCE.createEClass();
		screenClass.setName(screenName);
		
		addRootContextsTo(screenClass);
		
		addContextsTo(screenClass);
		
		EPackage screenPackage = createScreenPackage(screenName, screenClass);
		
		saveAsResource(screenPackage);
		
	}

	private void saveAsResource(EPackage screenPackage) {
		URI uri = URI.createFileURI(String.format("%s%s.ecore", Constants.GENERATED_DIRECTORY, screenPackage.getName()));
		ResourceSet resSet = new ResourceSetImpl();
		Resource res = resSet.createResource(uri);
		res.getContents().add(screenPackage);
		
		try {
			res.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private EPackage createScreenPackage(String screenName, EClass screenClass) {
		EPackage screenPackage = EcoreFactory.eINSTANCE.createEPackage();
		screenPackage.setName(screenName);
		screenPackage.setNsPrefix(String.format("%s.%s", ANNOTATION_SOURCE, screenName));
		screenPackage.setNsURI(String.format("http://%s.%s", ANNOTATION_SOURCE, screenName));
		screenPackage.getEClassifiers().add(screenClass);
		return screenPackage;
	}

	private void addContextsTo(EClass screenClass) {
		for (EObject context : ContextGenerator.getInstance().contexts) {
			
			EReference contextRef = EcoreFactory.eINSTANCE.createEReference();
			String refName = (String) context.eGet(utils.c2NameFeature);
			contextRef.setName(refName);
			
			String statement = (String) context.eGet(utils.c2StatementFeature);
			EAnnotation contextAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
			contextAnnotation.setSource(ANNOTATION_SOURCE);
			contextAnnotation.getDetails().put("ocl", statement);
			contextRef.getEAnnotations().add(contextAnnotation);
			
			EList<EObject> dataList = (EList<EObject>) context.eGet(utils.c2DataFeature);
			EClass contextClass = dataList.get(0).eClass();
			contextRef.setEType(contextClass);
			
			screenClass.getEStructuralFeatures().add(contextRef);
			
		}
	}

	private void addRootContextsTo(EClass screenClass) {
		for (EObject root : ContextGenerator.getInstance().rootContexts) {
			
			EReference rootRef = EcoreFactory.eINSTANCE.createEReference();
			String refName = (String) root.eGet(utils.c2NameFeature);	//left side of equals statement
			rootRef.setName(refName);
			
			String xmiLocation = (String) root.eGet(utils.c2StatementFeature);
			EAnnotation rootAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
			rootAnnotation.setSource(ANNOTATION_SOURCE);
			rootAnnotation.getDetails().put("xmiLocation", xmiLocation);
			rootRef.getEAnnotations().add(rootAnnotation);
			
			@SuppressWarnings("unchecked")
			EList<EObject> dataList = (EList<EObject>) root.eGet(utils.c2DataFeature);	//Requires the model to have already been read...
			EClass rootClass = dataList.get(0).eClass();
			rootRef.setEType(rootClass);
			
			screenClass.getEStructuralFeatures().add(rootRef);
			
		}
	}
	

}
