/**
 */
package data.impl;

import com.wireframesketcher.model.Widget;

import data.Assignment;
import data.DataPackage;
import data.ViewComponent;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>View Component</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link data.impl.ViewComponentImpl#getName <em>Name</em>}</li>
 *   <li>{@link data.impl.ViewComponentImpl#getAssignments <em>Assignments</em>}</li>
 *   <li>{@link data.impl.ViewComponentImpl#getWidget <em>Widget</em>}</li>
 *   <li>{@link data.impl.ViewComponentImpl#getExpectedType <em>Expected Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ViewComponentImpl extends MinimalEObjectImpl.Container implements ViewComponent {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAssignments() <em>Assignments</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAssignments()
	 * @generated
	 * @ordered
	 */
	protected EList<Assignment> assignments;

	/**
	 * The cached value of the '{@link #getWidget() <em>Widget</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWidget()
	 * @generated
	 * @ordered
	 */
	protected Widget widget;

	/**
	 * The default value of the '{@link #getExpectedType() <em>Expected Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpectedType()
	 * @generated
	 * @ordered
	 */
	protected static final String EXPECTED_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExpectedType() <em>Expected Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpectedType()
	 * @generated
	 * @ordered
	 */
	protected String expectedType = EXPECTED_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ViewComponentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DataPackage.Literals.VIEW_COMPONENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataPackage.VIEW_COMPONENT__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Assignment> getAssignments() {
		if (assignments == null) {
			assignments = new EObjectResolvingEList<Assignment>(Assignment.class, this, DataPackage.VIEW_COMPONENT__ASSIGNMENTS);
		}
		return assignments;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Widget getWidget() {
		if (widget != null && widget.eIsProxy()) {
			InternalEObject oldWidget = (InternalEObject)widget;
			widget = (Widget)eResolveProxy(oldWidget);
			if (widget != oldWidget) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DataPackage.VIEW_COMPONENT__WIDGET, oldWidget, widget));
			}
		}
		return widget;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Widget basicGetWidget() {
		return widget;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWidget(Widget newWidget) {
		Widget oldWidget = widget;
		widget = newWidget;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataPackage.VIEW_COMPONENT__WIDGET, oldWidget, widget));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getExpectedType() {
		return expectedType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExpectedType(String newExpectedType) {
		String oldExpectedType = expectedType;
		expectedType = newExpectedType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataPackage.VIEW_COMPONENT__EXPECTED_TYPE, oldExpectedType, expectedType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DataPackage.VIEW_COMPONENT__NAME:
				return getName();
			case DataPackage.VIEW_COMPONENT__ASSIGNMENTS:
				return getAssignments();
			case DataPackage.VIEW_COMPONENT__WIDGET:
				if (resolve) return getWidget();
				return basicGetWidget();
			case DataPackage.VIEW_COMPONENT__EXPECTED_TYPE:
				return getExpectedType();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DataPackage.VIEW_COMPONENT__NAME:
				setName((String)newValue);
				return;
			case DataPackage.VIEW_COMPONENT__ASSIGNMENTS:
				getAssignments().clear();
				getAssignments().addAll((Collection<? extends Assignment>)newValue);
				return;
			case DataPackage.VIEW_COMPONENT__WIDGET:
				setWidget((Widget)newValue);
				return;
			case DataPackage.VIEW_COMPONENT__EXPECTED_TYPE:
				setExpectedType((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case DataPackage.VIEW_COMPONENT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case DataPackage.VIEW_COMPONENT__ASSIGNMENTS:
				getAssignments().clear();
				return;
			case DataPackage.VIEW_COMPONENT__WIDGET:
				setWidget((Widget)null);
				return;
			case DataPackage.VIEW_COMPONENT__EXPECTED_TYPE:
				setExpectedType(EXPECTED_TYPE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DataPackage.VIEW_COMPONENT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DataPackage.VIEW_COMPONENT__ASSIGNMENTS:
				return assignments != null && !assignments.isEmpty();
			case DataPackage.VIEW_COMPONENT__WIDGET:
				return widget != null;
			case DataPackage.VIEW_COMPONENT__EXPECTED_TYPE:
				return EXPECTED_TYPE_EDEFAULT == null ? expectedType != null : !EXPECTED_TYPE_EDEFAULT.equals(expectedType);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", expectedType: ");
		result.append(expectedType);
		result.append(')');
		return result.toString();
	}

} //ViewComponentImpl
