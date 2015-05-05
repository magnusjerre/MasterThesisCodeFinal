/**
 */
package data.impl;

import com.wireframesketcher.model.Widget;

import data.Assignment;
import data.DataPackage;
import data.ViewComponent;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Assignment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link data.impl.AssignmentImpl#getStatement <em>Statement</em>}</li>
 *   <li>{@link data.impl.AssignmentImpl#getWidget <em>Widget</em>}</li>
 *   <li>{@link data.impl.AssignmentImpl#getUsingViewComponentNamed <em>Using View Component Named</em>}</li>
 *   <li>{@link data.impl.AssignmentImpl#getPartOfComponent <em>Part Of Component</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AssignmentImpl extends MinimalEObjectImpl.Container implements Assignment {
	/**
	 * The default value of the '{@link #getStatement() <em>Statement</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStatement()
	 * @generated
	 * @ordered
	 */
	protected static final String STATEMENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getStatement() <em>Statement</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStatement()
	 * @generated
	 * @ordered
	 */
	protected String statement = STATEMENT_EDEFAULT;

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
	 * The default value of the '{@link #getUsingViewComponentNamed() <em>Using View Component Named</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUsingViewComponentNamed()
	 * @generated
	 * @ordered
	 */
	protected static final String USING_VIEW_COMPONENT_NAMED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUsingViewComponentNamed() <em>Using View Component Named</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUsingViewComponentNamed()
	 * @generated
	 * @ordered
	 */
	protected String usingViewComponentNamed = USING_VIEW_COMPONENT_NAMED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getPartOfComponent() <em>Part Of Component</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPartOfComponent()
	 * @generated
	 * @ordered
	 */
	protected ViewComponent partOfComponent;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AssignmentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DataPackage.Literals.ASSIGNMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getStatement() {
		return statement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStatement(String newStatement) {
		String oldStatement = statement;
		statement = newStatement;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataPackage.ASSIGNMENT__STATEMENT, oldStatement, statement));
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DataPackage.ASSIGNMENT__WIDGET, oldWidget, widget));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DataPackage.ASSIGNMENT__WIDGET, oldWidget, widget));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUsingViewComponentNamed() {
		return usingViewComponentNamed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUsingViewComponentNamed(String newUsingViewComponentNamed) {
		String oldUsingViewComponentNamed = usingViewComponentNamed;
		usingViewComponentNamed = newUsingViewComponentNamed;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataPackage.ASSIGNMENT__USING_VIEW_COMPONENT_NAMED, oldUsingViewComponentNamed, usingViewComponentNamed));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ViewComponent getPartOfComponent() {
		if (partOfComponent != null && partOfComponent.eIsProxy()) {
			InternalEObject oldPartOfComponent = (InternalEObject)partOfComponent;
			partOfComponent = (ViewComponent)eResolveProxy(oldPartOfComponent);
			if (partOfComponent != oldPartOfComponent) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DataPackage.ASSIGNMENT__PART_OF_COMPONENT, oldPartOfComponent, partOfComponent));
			}
		}
		return partOfComponent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ViewComponent basicGetPartOfComponent() {
		return partOfComponent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPartOfComponent(ViewComponent newPartOfComponent) {
		ViewComponent oldPartOfComponent = partOfComponent;
		partOfComponent = newPartOfComponent;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DataPackage.ASSIGNMENT__PART_OF_COMPONENT, oldPartOfComponent, partOfComponent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isPartOfViewComponent() {
		return getPartOfComponent() != null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isUsingViewComponent() {
		return getUsingViewComponentNamed() != null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DataPackage.ASSIGNMENT__STATEMENT:
				return getStatement();
			case DataPackage.ASSIGNMENT__WIDGET:
				if (resolve) return getWidget();
				return basicGetWidget();
			case DataPackage.ASSIGNMENT__USING_VIEW_COMPONENT_NAMED:
				return getUsingViewComponentNamed();
			case DataPackage.ASSIGNMENT__PART_OF_COMPONENT:
				if (resolve) return getPartOfComponent();
				return basicGetPartOfComponent();
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
			case DataPackage.ASSIGNMENT__STATEMENT:
				setStatement((String)newValue);
				return;
			case DataPackage.ASSIGNMENT__WIDGET:
				setWidget((Widget)newValue);
				return;
			case DataPackage.ASSIGNMENT__USING_VIEW_COMPONENT_NAMED:
				setUsingViewComponentNamed((String)newValue);
				return;
			case DataPackage.ASSIGNMENT__PART_OF_COMPONENT:
				setPartOfComponent((ViewComponent)newValue);
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
			case DataPackage.ASSIGNMENT__STATEMENT:
				setStatement(STATEMENT_EDEFAULT);
				return;
			case DataPackage.ASSIGNMENT__WIDGET:
				setWidget((Widget)null);
				return;
			case DataPackage.ASSIGNMENT__USING_VIEW_COMPONENT_NAMED:
				setUsingViewComponentNamed(USING_VIEW_COMPONENT_NAMED_EDEFAULT);
				return;
			case DataPackage.ASSIGNMENT__PART_OF_COMPONENT:
				setPartOfComponent((ViewComponent)null);
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
			case DataPackage.ASSIGNMENT__STATEMENT:
				return STATEMENT_EDEFAULT == null ? statement != null : !STATEMENT_EDEFAULT.equals(statement);
			case DataPackage.ASSIGNMENT__WIDGET:
				return widget != null;
			case DataPackage.ASSIGNMENT__USING_VIEW_COMPONENT_NAMED:
				return USING_VIEW_COMPONENT_NAMED_EDEFAULT == null ? usingViewComponentNamed != null : !USING_VIEW_COMPONENT_NAMED_EDEFAULT.equals(usingViewComponentNamed);
			case DataPackage.ASSIGNMENT__PART_OF_COMPONENT:
				return partOfComponent != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case DataPackage.ASSIGNMENT___IS_PART_OF_VIEW_COMPONENT:
				return isPartOfViewComponent();
			case DataPackage.ASSIGNMENT___IS_USING_VIEW_COMPONENT:
				return isUsingViewComponent();
		}
		return super.eInvoke(operationID, arguments);
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
		result.append(" (statement: ");
		result.append(statement);
		result.append(", usingViewComponentNamed: ");
		result.append(usingViewComponentNamed);
		result.append(')');
		return result.toString();
	}

} //AssignmentImpl
