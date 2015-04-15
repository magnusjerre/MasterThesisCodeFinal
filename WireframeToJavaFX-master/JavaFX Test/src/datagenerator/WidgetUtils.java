package datagenerator;

import com.wireframesketcher.model.Arrow;
import com.wireframesketcher.model.HLine;
import com.wireframesketcher.model.HScrollbar;
import com.wireframesketcher.model.Panel;
import com.wireframesketcher.model.Position;
import com.wireframesketcher.model.TabbedPane;
import com.wireframesketcher.model.VLine;
import com.wireframesketcher.model.VScrollbar;
import com.wireframesketcher.model.Widget;
import com.wireframesketcher.model.WidgetGroup;
import com.wireframesketcher.model.Window;

public class WidgetUtils {
	
	protected static Widget getSecondShallowestWidget(Arrow arrow, Widget widget) {
		
		if (isNotWidgetGroup(widget)) {
			return widget;
		}
		
		Point arrowHead = getArrowHeadPosition(arrow);
		WidgetGroup widgetGroup = (WidgetGroup) widget;
		Point offset = new Point(widgetGroup.getX(), widgetGroup.getY());
		for (Widget w : widgetGroup.getWidgets()) {
			
			int x = w.getX() + offset.x;
			int y = w.getY() + offset.y;
			if (pointIsInsideRectangle(arrowHead, x, y, w.getMeasuredWidth(), w.getMeasuredHeight())) {
				if (isNotBackgroundWidget(w)) {
					return w;
				}
			}
		}
		
		throw new RuntimeException("Didn't find a match for the arrow location");
		
	}
	
	protected static Widget getDeepestWidget(Arrow arrow, Widget widget) {
		
		Point arrowHead = getArrowHeadPosition(arrow);
		return getDeepestWidgetAtPoint(arrowHead, widget, new Point(widget.getX(), widget.getY()));
		
	}
	
	protected static Widget getDeepestWidgetAtPoint(Point arrowHead, Widget widget, Point offset) {
		
		if (isNotWidgetGroup(widget)) {
			return widget;
		}
		
		WidgetGroup widgetGroup = (WidgetGroup) widget;
		Widget output = null;
		for (Widget w : widgetGroup.getWidgets()) {
			
			int x = w.getX() + offset.x;
			int y = w.getY() + offset.y;
			if (pointIsInsideRectangle(arrowHead, x, y, w.getMeasuredWidth(), w.getMeasuredHeight())) {
				if (isNotBackgroundWidget(w)) {
					output = w;
					break;
				}
			}
			
		}
		
		if (isNotWidgetGroup(output)) {
			return output;
		}
		
		offset = new Point(offset.x + output.getX(), offset.y + output.getY());
		return getDeepestWidgetAtPoint(arrowHead, output, offset);
		
		
	}
	
	protected static boolean isNotWidgetGroup(Widget widget) {
		return !(widget instanceof WidgetGroup);
	}
	
	protected static boolean isNotBackgroundWidget(Widget widget) {
		
		if (widget instanceof Panel) {
			return false;
		}
		
		if (widget instanceof HScrollbar) {
			return false;
		}
		
		if (widget instanceof VScrollbar) {
			return false;
		}
		
		if (widget instanceof HLine) {
			return false;
		}
		
		if (widget instanceof VLine) {
			return false;
		}
		
		if (widget instanceof TabbedPane) {
			return false;
		}
		
		if (widget instanceof Window) {
			return false;
		}
		
		return true;
		
	}
	
	private static Point getArrowHeadPosition(Arrow arrow) {
		
		if (arrow.isLeft() == arrow.isRight()) {
			throw new RuntimeException("There is an error with the arrow. It's either not pointing, or it's pointing both ways.");
		}
		
		//Topmost and Leftmost point of line
		int x = arrow.getX();
		int y = arrow.getY();
		
		if (arrow.isRight() && arrow.getDirection() == Position.BOTTOM){
			x += arrow.getMeasuredWidth();
		} else if (arrow.isRight()){
			x += arrow.getMeasuredWidth();
			y += arrow.getMeasuredHeight();
		} else if (arrow.getDirection() == Position.BOTTOM){
			y += arrow.getMeasuredHeight();
		}
		
		return new Point(x,y);
		
	}
	
	private static boolean pointIsInsideRectangle(Point point, int left, int top, int width, int height) {
		
		int right = left + width;
		int bottom = top + height;
		
		if (point.x < left) {
			return false;
		}
		
		if (point.x > right) {
			return false;
		}
		
		if (point.y < top) {
			return false;
		}
		
		if (point.y > bottom) {
			return false;
		}
		
		return true;
		
	}
	
	private static class Point {
		
		public final int x, y;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString() {
			return String.format("{x: %d , y: %d }", x, y);
		}
	}

}
