/**
 */
package data.impl;

import data.DataPackage;
import data.Selection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Selection</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link data.impl.SelectionImpl#getName <em>Name</em>}</li>
 *   <li>{@link data.impl.SelectionImpl#getExpectedType <em>Expected Type</em>}</li>
 *   <li>{@link data.impl.SelectionImpl#getLayoutId <em>Layout Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SelectionImpl extends MinimalEObjectImpl.Container implements Selection {
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
	 * The default value of the '{@link #getLayoutId() <em>Layout Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLayoutId()
	 * @generated
	 * @ordered
	 */
	protected static final String LAYOUT_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLayoutId() <em>Layout Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLayoutId()
	 * @generated
	 * @ordered
	 */
	protected String layoutId = LAYOUT_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SelectionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DataPackage.Literals.SELECTION;
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
			eNotify(new ENotificationImpl(this, Notification.SET, DataPackage.SELECTION__NAME, oldName, name));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DataPackage.SELECTION__EXPECTED_TYPE, oldExpectedType, expectedType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLayoutId() {
		return layoutId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLayoutId(String newLayoutId) {
		String oldLayoutId = layoutId;
		layoutId = newLayoutId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataPackage.SELECTION__LAYOUT_ID, oldLayoutId, layoutId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DataPackage.SELECTION__NAME:
				return getName();
			case DataPackage.SELECTION__EXPECTED_TYPE:
				return getExpectedType();
			case DataPackage.SELECTION__LAYOUT_ID:
				return getLayoutId();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DataPackage.SELECTION__NAME:
				setName((String)newValue);
				return;
			case DataPackage.SELECTION__EXPECTED_TYPE:
				setExpectedType((String)newValue);
				return;
			case DataPackage.SELECTION__LAYOUT_ID:
				setLayoutId((String)newValue);
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
			case DataPackage.SELECTION__NAME:
				setName(NAME_EDEFAULT);
				return;
			case DataPackage.SELECTION__EXPECTED_TYPE:
				setExpectedType(EXPECTED_TYPE_EDEFAULT);
				return;
			case DataPackage.SELECTION__LAYOUT_ID:
				setLayoutId(LAYOUT_ID_EDEFAULT);
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
			case DataPackage.SELECTION__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DataPackage.SELECTION__EXPECTED_TYPE:
				return EXPECTED_TYPE_EDEFAULT == null ? expectedType != null : !EXPECTED_TYPE_EDEFAULT.equals(expectedType);
			case DataPackage.SELECTION__LAYOUT_ID:
				return LAYOUT_ID_EDEFAULT == null ? layoutId != null : !LAYOUT_ID_EDEFAULT.equals(layoutId);
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
		result.append(", layoutId: ");
		result.append(layoutId);
		result.append(')');
		return result.toString();
	}

} //SelectionImpl
