/**
 */
package data;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see data.DataFactory
 * @model kind="package"
 * @generated
 */
public interface DataPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "data";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://no.jerre.data";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "no.jerre.data";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	DataPackage eINSTANCE = data.impl.DataPackageImpl.init();

	/**
	 * The meta object id for the '{@link data.impl.ViewComponentImpl <em>View Component</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see data.impl.ViewComponentImpl
	 * @see data.impl.DataPackageImpl#getViewComponent()
	 * @generated
	 */
	int VIEW_COMPONENT = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_COMPONENT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Assignments</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_COMPONENT__ASSIGNMENTS = 1;

	/**
	 * The feature id for the '<em><b>Widget</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_COMPONENT__WIDGET = 2;

	/**
	 * The feature id for the '<em><b>Expected Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_COMPONENT__EXPECTED_TYPE = 3;

	/**
	 * The number of structural features of the '<em>View Component</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_COMPONENT_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>View Component</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VIEW_COMPONENT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link data.impl.ContextImpl <em>Context</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see data.impl.ContextImpl
	 * @see data.impl.DataPackageImpl#getContext()
	 * @generated
	 */
	int CONTEXT = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Statement</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT__STATEMENT = 1;

	/**
	 * The number of structural features of the '<em>Context</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Context</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTEXT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link data.impl.AssignmentImpl <em>Assignment</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see data.impl.AssignmentImpl
	 * @see data.impl.DataPackageImpl#getAssignment()
	 * @generated
	 */
	int ASSIGNMENT = 2;

	/**
	 * The feature id for the '<em><b>Statement</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSIGNMENT__STATEMENT = 0;

	/**
	 * The feature id for the '<em><b>Widget</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSIGNMENT__WIDGET = 1;

	/**
	 * The feature id for the '<em><b>Using View Component Named</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSIGNMENT__USING_VIEW_COMPONENT_NAMED = 2;

	/**
	 * The feature id for the '<em><b>Part Of Component</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSIGNMENT__PART_OF_COMPONENT = 3;

	/**
	 * The number of structural features of the '<em>Assignment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSIGNMENT_FEATURE_COUNT = 4;

	/**
	 * The operation id for the '<em>Is Part Of View Component</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSIGNMENT___IS_PART_OF_VIEW_COMPONENT = 0;

	/**
	 * The operation id for the '<em>Is Using View Component</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSIGNMENT___IS_USING_VIEW_COMPONENT = 1;

	/**
	 * The number of operations of the '<em>Assignment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSIGNMENT_OPERATION_COUNT = 2;


	/**
	 * The meta object id for the '{@link data.impl.SelectionImpl <em>Selection</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see data.impl.SelectionImpl
	 * @see data.impl.DataPackageImpl#getSelection()
	 * @generated
	 */
	int SELECTION = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELECTION__NAME = 0;

	/**
	 * The feature id for the '<em><b>Expected Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELECTION__EXPECTED_TYPE = 1;

	/**
	 * The feature id for the '<em><b>Layout Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELECTION__LAYOUT_ID = 2;

	/**
	 * The number of structural features of the '<em>Selection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELECTION_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Selection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SELECTION_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link data.ViewComponent <em>View Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>View Component</em>'.
	 * @see data.ViewComponent
	 * @generated
	 */
	EClass getViewComponent();

	/**
	 * Returns the meta object for the attribute '{@link data.ViewComponent#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see data.ViewComponent#getName()
	 * @see #getViewComponent()
	 * @generated
	 */
	EAttribute getViewComponent_Name();

	/**
	 * Returns the meta object for the reference list '{@link data.ViewComponent#getAssignments <em>Assignments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Assignments</em>'.
	 * @see data.ViewComponent#getAssignments()
	 * @see #getViewComponent()
	 * @generated
	 */
	EReference getViewComponent_Assignments();

	/**
	 * Returns the meta object for the reference '{@link data.ViewComponent#getWidget <em>Widget</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Widget</em>'.
	 * @see data.ViewComponent#getWidget()
	 * @see #getViewComponent()
	 * @generated
	 */
	EReference getViewComponent_Widget();

	/**
	 * Returns the meta object for the attribute '{@link data.ViewComponent#getExpectedType <em>Expected Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expected Type</em>'.
	 * @see data.ViewComponent#getExpectedType()
	 * @see #getViewComponent()
	 * @generated
	 */
	EAttribute getViewComponent_ExpectedType();

	/**
	 * Returns the meta object for class '{@link data.Context <em>Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Context</em>'.
	 * @see data.Context
	 * @generated
	 */
	EClass getContext();

	/**
	 * Returns the meta object for the attribute '{@link data.Context#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see data.Context#getName()
	 * @see #getContext()
	 * @generated
	 */
	EAttribute getContext_Name();

	/**
	 * Returns the meta object for the attribute '{@link data.Context#getStatement <em>Statement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Statement</em>'.
	 * @see data.Context#getStatement()
	 * @see #getContext()
	 * @generated
	 */
	EAttribute getContext_Statement();

	/**
	 * Returns the meta object for class '{@link data.Assignment <em>Assignment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Assignment</em>'.
	 * @see data.Assignment
	 * @generated
	 */
	EClass getAssignment();

	/**
	 * Returns the meta object for the attribute '{@link data.Assignment#getStatement <em>Statement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Statement</em>'.
	 * @see data.Assignment#getStatement()
	 * @see #getAssignment()
	 * @generated
	 */
	EAttribute getAssignment_Statement();

	/**
	 * Returns the meta object for the reference '{@link data.Assignment#getWidget <em>Widget</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Widget</em>'.
	 * @see data.Assignment#getWidget()
	 * @see #getAssignment()
	 * @generated
	 */
	EReference getAssignment_Widget();

	/**
	 * Returns the meta object for the attribute '{@link data.Assignment#getUsingViewComponentNamed <em>Using View Component Named</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Using View Component Named</em>'.
	 * @see data.Assignment#getUsingViewComponentNamed()
	 * @see #getAssignment()
	 * @generated
	 */
	EAttribute getAssignment_UsingViewComponentNamed();

	/**
	 * Returns the meta object for the reference '{@link data.Assignment#getPartOfComponent <em>Part Of Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Part Of Component</em>'.
	 * @see data.Assignment#getPartOfComponent()
	 * @see #getAssignment()
	 * @generated
	 */
	EReference getAssignment_PartOfComponent();

	/**
	 * Returns the meta object for the '{@link data.Assignment#isPartOfViewComponent() <em>Is Part Of View Component</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Is Part Of View Component</em>' operation.
	 * @see data.Assignment#isPartOfViewComponent()
	 * @generated
	 */
	EOperation getAssignment__IsPartOfViewComponent();

	/**
	 * Returns the meta object for the '{@link data.Assignment#isUsingViewComponent() <em>Is Using View Component</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Is Using View Component</em>' operation.
	 * @see data.Assignment#isUsingViewComponent()
	 * @generated
	 */
	EOperation getAssignment__IsUsingViewComponent();

	/**
	 * Returns the meta object for class '{@link data.Selection <em>Selection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Selection</em>'.
	 * @see data.Selection
	 * @generated
	 */
	EClass getSelection();

	/**
	 * Returns the meta object for the attribute '{@link data.Selection#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see data.Selection#getName()
	 * @see #getSelection()
	 * @generated
	 */
	EAttribute getSelection_Name();

	/**
	 * Returns the meta object for the attribute '{@link data.Selection#getExpectedType <em>Expected Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expected Type</em>'.
	 * @see data.Selection#getExpectedType()
	 * @see #getSelection()
	 * @generated
	 */
	EAttribute getSelection_ExpectedType();

	/**
	 * Returns the meta object for the attribute '{@link data.Selection#getLayoutId <em>Layout Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Layout Id</em>'.
	 * @see data.Selection#getLayoutId()
	 * @see #getSelection()
	 * @generated
	 */
	EAttribute getSelection_LayoutId();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DataFactory getDataFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link data.impl.ViewComponentImpl <em>View Component</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see data.impl.ViewComponentImpl
		 * @see data.impl.DataPackageImpl#getViewComponent()
		 * @generated
		 */
		EClass VIEW_COMPONENT = eINSTANCE.getViewComponent();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VIEW_COMPONENT__NAME = eINSTANCE.getViewComponent_Name();

		/**
		 * The meta object literal for the '<em><b>Assignments</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VIEW_COMPONENT__ASSIGNMENTS = eINSTANCE.getViewComponent_Assignments();

		/**
		 * The meta object literal for the '<em><b>Widget</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VIEW_COMPONENT__WIDGET = eINSTANCE.getViewComponent_Widget();

		/**
		 * The meta object literal for the '<em><b>Expected Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VIEW_COMPONENT__EXPECTED_TYPE = eINSTANCE.getViewComponent_ExpectedType();

		/**
		 * The meta object literal for the '{@link data.impl.ContextImpl <em>Context</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see data.impl.ContextImpl
		 * @see data.impl.DataPackageImpl#getContext()
		 * @generated
		 */
		EClass CONTEXT = eINSTANCE.getContext();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT__NAME = eINSTANCE.getContext_Name();

		/**
		 * The meta object literal for the '<em><b>Statement</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTEXT__STATEMENT = eINSTANCE.getContext_Statement();

		/**
		 * The meta object literal for the '{@link data.impl.AssignmentImpl <em>Assignment</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see data.impl.AssignmentImpl
		 * @see data.impl.DataPackageImpl#getAssignment()
		 * @generated
		 */
		EClass ASSIGNMENT = eINSTANCE.getAssignment();

		/**
		 * The meta object literal for the '<em><b>Statement</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSIGNMENT__STATEMENT = eINSTANCE.getAssignment_Statement();

		/**
		 * The meta object literal for the '<em><b>Widget</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ASSIGNMENT__WIDGET = eINSTANCE.getAssignment_Widget();

		/**
		 * The meta object literal for the '<em><b>Using View Component Named</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSIGNMENT__USING_VIEW_COMPONENT_NAMED = eINSTANCE.getAssignment_UsingViewComponentNamed();

		/**
		 * The meta object literal for the '<em><b>Part Of Component</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ASSIGNMENT__PART_OF_COMPONENT = eINSTANCE.getAssignment_PartOfComponent();

		/**
		 * The meta object literal for the '<em><b>Is Part Of View Component</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ASSIGNMENT___IS_PART_OF_VIEW_COMPONENT = eINSTANCE.getAssignment__IsPartOfViewComponent();

		/**
		 * The meta object literal for the '<em><b>Is Using View Component</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ASSIGNMENT___IS_USING_VIEW_COMPONENT = eINSTANCE.getAssignment__IsUsingViewComponent();

		/**
		 * The meta object literal for the '{@link data.impl.SelectionImpl <em>Selection</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see data.impl.SelectionImpl
		 * @see data.impl.DataPackageImpl#getSelection()
		 * @generated
		 */
		EClass SELECTION = eINSTANCE.getSelection();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SELECTION__NAME = eINSTANCE.getSelection_Name();

		/**
		 * The meta object literal for the '<em><b>Expected Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SELECTION__EXPECTED_TYPE = eINSTANCE.getSelection_ExpectedType();

		/**
		 * The meta object literal for the '<em><b>Layout Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SELECTION__LAYOUT_ID = eINSTANCE.getSelection_LayoutId();

	}

} //DataPackage
