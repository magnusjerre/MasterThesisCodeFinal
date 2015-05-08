/**
 */
package data;

import com.wireframesketcher.model.Widget;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>View Component</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link data.ViewComponent#getName <em>Name</em>}</li>
 *   <li>{@link data.ViewComponent#getAssignments <em>Assignments</em>}</li>
 *   <li>{@link data.ViewComponent#getWidget <em>Widget</em>}</li>
 *   <li>{@link data.ViewComponent#getExpectedType <em>Expected Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see data.DataPackage#getViewComponent()
 * @model
 * @generated
 */
public interface ViewComponent extends EObject {
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
	 * @see data.DataPackage#getViewComponent_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link data.ViewComponent#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Assignments</b></em>' reference list.
	 * The list contents are of type {@link data.Assignment}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Assignments</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Assignments</em>' reference list.
	 * @see data.DataPackage#getViewComponent_Assignments()
	 * @model
	 * @generated
	 */
	EList<Assignment> getAssignments();

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
	 * @see data.DataPackage#getViewComponent_Widget()
	 * @model
	 * @generated
	 */
	Widget getWidget();

	/**
	 * Sets the value of the '{@link data.ViewComponent#getWidget <em>Widget</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Widget</em>' reference.
	 * @see #getWidget()
	 * @generated
	 */
	void setWidget(Widget value);

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
	 * @see data.DataPackage#getViewComponent_ExpectedType()
	 * @model
	 * @generated
	 */
	String getExpectedType();

	/**
	 * Sets the value of the '{@link data.ViewComponent#getExpectedType <em>Expected Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expected Type</em>' attribute.
	 * @see #getExpectedType()
	 * @generated
	 */
	void setExpectedType(String value);

} // ViewComponent
