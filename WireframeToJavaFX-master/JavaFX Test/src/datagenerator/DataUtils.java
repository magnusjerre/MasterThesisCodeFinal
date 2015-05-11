package datagenerator;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.ocl.ecore.impl.CollectionTypeImpl;
import org.eclipse.ocl.ecore.impl.PrimitiveTypeImpl;

public class DataUtils {
	
	/**
	 * Creates an EStructuralFeature of either EReference or EAttribute from the given classifier. The classifier can be of three different types:
	 * CollectionTypeImpl, PrimitiveTypeImpl or EClassImpl. In case of a collection, the data type of the elements will be used as the data type for the
	 * structural feature and the upperbound will be set to unbound.
	 * 
	 * The name for the created structural feature is not set and must therefore be set later.
	 * @param classifier
	 * @return EAttribute or EReference
	 */
	public static EStructuralFeature createFeatureFromClassifier(EClassifier classifier) {
		
		EStructuralFeature feature = null;
		
		if (classifier instanceof CollectionTypeImpl) {
			EClassifier elementType = ((CollectionTypeImpl) classifier).getElementType();
			
			if (isEDataType(elementType)) {
				feature = EcoreFactory.eINSTANCE.createEAttribute();
				EDataType eDataType = getEDataTypeFromClassifier(elementType);
				feature.setEType(eDataType);
			} else {
				feature = EcoreFactory.eINSTANCE.createEReference();
				feature.setEType(elementType);
			}
			
			feature.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
			
		} else if (isEDataType(classifier)) {
			feature = EcoreFactory.eINSTANCE.createEAttribute();
			EDataType eDataType = getEDataTypeFromClassifier(classifier);
			feature.setEType(eDataType);
		} else {
			feature = EcoreFactory.eINSTANCE.createEReference();
			feature.setEType(classifier);
		}
		
		return feature;
		
	}
	
	public static boolean isEDataType(EClassifier eClassifier) {
		
		if (eClassifier instanceof PrimitiveTypeImpl) {
			return true;
		}
		
		if ("EDate".equals(eClassifier.getName())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the corresponding EDataType for the primitive.
	 * 
	 * Using this method since the oclstdlib.ecore cannot be found.
	 * @param classifier
	 * @return
	 */
	public static EDataType getEDataTypeFromClassifier(EClassifier classifier) {
		
		switch (classifier.getName()) {
		
			case "String":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEString();
			case "EDate":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEDate();
			case "Integer":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEIntegerObject();
			case "Double":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEDoubleObject();
			case "Long":
				return EcoreFactory.eINSTANCE.getEcorePackage().getELongObject();
			case "Byte":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEByteObject();
			case "Boolean":
				return EcoreFactory.eINSTANCE.getEcorePackage().getEBooleanObject();
			case "Character":
				return EcoreFactory.eINSTANCE.getEcorePackage().getECharacterObject();
			default:
				return null;
				
		}
		
	}

	/**
	 * Creates a new string where the first letter is capitalized.
	 * @param string
	 * @return
	 */
	public static String capitalizeFirst(String string) {
		
		if (string.length() < 2) {
			return string.toUpperCase();
		}
		
		String capitalized = string.toUpperCase();
		String output = capitalized.charAt(0) + string.substring(1);
		return output;
		
	}
	
	/**
	 * Creates a new string where only the substring up to the sequence is returned. i.e clipping the statement
	 * allActors->select(name = 'Lena Headey')
	 * at "->" returns the string 
	 * allActors
	 * @param sequence
	 * @param string
	 * @return
	 */
	public static String clipAtSequence(String sequence, String string) {
		
		int indexOfSequence = string.indexOf(sequence);
		
		if (indexOfSequence == -1) {
			return string;
		}
		
		return string.substring(0, indexOfSequence);
	}
	
	/**
	 * Based on the string, a variable name and data type name is extracted from it.
	 * The string, Actor theActor, will result in the variable name theActor and the data type name Actor
	 * The string, theActor : Actor, will result in the variable name theActor and the data type name Actor 
	 * @param string
	 * @return First element is the variable name, the second element is the type name
	 */
	public static String[] extractNameAndType(String string) {
		
		string = string.trim();
		
		String[] split = new String[]{string};
		String name = null, dataType = null;
		if (string.contains(":")) {	//Type declaration after colon
			split = string.split(":");
			name = split[0];
			dataType = split[1];
		} else if (string.contains(" ")) {	//Data type declaration before blank space, like normal java programming
			split = string.split(" ");
			name = split[1];
			dataType = split[0];
		} 

		if (split.length != 2) {
			throw new RuntimeException(String.format("Error! The component \"%s\" is either malformed or it doesn't declare a data type.", string));
		}
		
		return new String[] {name.trim(), dataType.trim()};
		
	}
	
}
