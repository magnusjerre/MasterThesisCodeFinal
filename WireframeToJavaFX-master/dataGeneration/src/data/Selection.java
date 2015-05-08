/**
 */
package data;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Selection</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link data.Selection#getName <em>Name</em>}</li>
 *   <li>{@link data.Selection#getExpectedType <em>Expected Type</em>}</li>
 *   <li>{@link data.Selection#getLayoutId <em>Layout Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see data.DataPackage#getSelection()
 * @model
 * @generated
 */
public interface Selection extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see data.DataPackage#getSelection_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link data.Selection#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Expected Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Expected Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expected Type</em>' attribute.
	 * @see #setExpectedType(String)
	 * @see data.DataPackage#getSelection_ExpectedType()
	 * @model
	 * @generated
	 */
	String getExpectedType();

	/**
	 * Sets the value of the '{@link data.Selection#getExpectedType <em>Expected Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expected Type</em>' attribute.
	 * @see #getExpectedType()
	 * @generated
	 */
	void setExpectedType(String value);

	/**
	 * Returns the value of the '<em><b>Layout Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Layout Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Layout Id</em>' attribute.
	 * @see #setLayoutId(String)
	 * @see data.DataPackage#getSelection_LayoutId()
	 * @model
	 * @generated
	 */
	String getLayoutId();

	/**
	 * Sets the value of the '{@link data.Selection#getLayoutId <em>Layout Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Layout Id</em>' attribute.
	 * @see #getLayoutId()
	 * @generated
	 */
	void setLayoutId(String value);

} // Selection
