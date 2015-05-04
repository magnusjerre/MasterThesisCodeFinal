package datagenerator;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.ecore.OCL.Query;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;

public class OCLHandler {
	
	public static Object parseOCLStatement(ResourceSet resourceSet, EObject instanceRoot, String queryExpr) {
		
		OCL.initialize(resourceSet);
		OCL ocl = OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
		OCLHelper<EClassifier, ?, ?, Constraint> oclHelper = ocl.createOCLHelper();
		
		try {
			oclHelper.setContext(instanceRoot.eClass());
			OCLExpression<EClassifier> queryExpression = oclHelper.createQuery(queryExpr);
			Query query = ocl.createQuery(queryExpression);
			return query.evaluate(instanceRoot);
		} catch (ParserException e) {
			System.out.println(String.format("Error in parsing ocl on instance. The statement doesn't result in any value. %s", e.getLocalizedMessage()));
		}
		
		return null;
	}
	
	public static EClassifier getClassifierForStatement2(ResourceSet resourceSet, EClassifier instanceRoot, String query) {
		
		OCL.initialize(resourceSet);
		OCL ocl = OCL.newInstance(EcoreEnvironmentFactory.INSTANCE);
		OCLHelper<EClassifier, ?, ?, Constraint> oclHelper = ocl.createOCLHelper();
		
		try {
			oclHelper.setContext(instanceRoot);
			OCLExpression<EClassifier> queryExpression = oclHelper.createQuery(query);
			return queryExpression.getType();
		} catch (ParserException e) {
			System.out.println(String.format("Error in retrieving the classifier for the ocl on instance. The screenfile might not have added it, or the statement might not be legal. %s", e.getLocalizedMessage()));
		}
		
		return null;
		
	}

}
