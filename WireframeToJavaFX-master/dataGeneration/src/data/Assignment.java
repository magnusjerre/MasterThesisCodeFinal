/**
 */
package data;

import com.wireframesketcher.model.Widget;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Assignment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link data.Assignment#getStatement <em>Statement</em>}</li>
 *   <li>{@link data.Assignment#getWidget <em>Widget</em>}</li>
 *   <li>{@link data.Assignment#getUsingViewComponentNamed <em>Using View Component Named</em>}</li>
 *   <li>{@link data.Assignment#getPartOfComponent <em>Part Of Component</em>}</li>
 * </ul>
 * </p>
 *
 * @see data.DataPackage#getAssignment()
 * @model
 * @generated
 */
public interface Assignment extends EObject {
	/**
	 * Returns the value of the '<em><b>Statement</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Statement</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Statement</em>' attribute.
	 * @see #setStatement(String)
	 * @see data.DataPackage#getAssignment_Statement()
	 * @model
	 * @generated
	 */
	String getStatement();

	/**
	 * Sets the value of the '{@link data.Assignment#getStatement <em>Statement</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Statement</em>' attribute.
	 * @see #getStatement()
	 * @generated
	 */
	void setStatement(String value);

	/**
	 * Returns the value of the '<em><b>Widget</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Widget</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Widget</em>' reference.
	 * @see #setWidget(Widget)
	 * @see data.DataPackage#getAssignment_Widget()
	 * @model
	 * @generated
	 */
	Widget getWidget();

	/**
	 * Sets the value of the '{@link data.Assignment#getWidget <em>Widget</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Widget</em>' reference.
	 * @see #getWidget()
	 * @generated
	 */
	void setWidget(Widget value);

	/**
	 * Returns the value of the '<em><b>Using View Component Named</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Using View Component Named</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Using View Component Named</em>' attribute.
	 * @see #setUsingViewComponentNamed(String)
	 * @see data.DataPackage#getAssignment_UsingViewComponentNamed()
	 * @model
	 * @generated
	 */
	String getUsingViewComponentNamed();

	/**
	 * Sets the value of the '{@link data.Assignment#getUsingViewComponentNamed <em>Using View Component Named</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Using View Component Named</em>' attribute.
	 * @see #getUsingViewComponentNamed()
	 * @generated
	 */
	void setUsingViewComponentNamed(String value);

	/**
	 * Returns the value of the '<em><b>Part Of Component</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Part Of Component</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Part Of Component</em>' reference.
	 * @see #setPartOfComponent(ViewComponent)
	 * @see data.DataPackage#getAssignment_PartOfComponent()
	 * @model
	 * @generated
	 */
	ViewComponent getPartOfComponent();

	/**
	 * Sets the value of the '{@link data.Assignment#getPartOfComponent <em>Part Of Component</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Part Of Component</em>' reference.
	 * @see #getPartOfComponent()
	 * @generated
	 */
	void setPartOfComponent(ViewComponent value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return getPartOfComponent() != null;'"
	 * @generated
	 */
	boolean isPartOfViewComponent();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return getUsingViewComponentNamed() != null;'"
	 * @generated
	 */
	boolean isUsingViewComponent();

} // Assignment
