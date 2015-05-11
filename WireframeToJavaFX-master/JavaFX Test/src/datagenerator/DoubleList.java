package datagenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * The DoubleList class contains two lists that synchronously add, remove and clear. This means that 
 * the lists will always be of the same size, and that an element at position 1 in the elements list 
 * hase a corresponding element at position 1 in the elementsAsMaster list.
 * @author Magnus Jerre
 *
 * @param <E>
 * @param <M>
 */
public class DoubleList <E, M> {
	
	List<E> elements;
	List<M> elementsAsMaster;
	
	public DoubleList() {
		elements = new ArrayList<E>();
		elementsAsMaster = new ArrayList<M>();
	}
	
	public M getMaster(E element) {
		
		int indexOfElement = elements.indexOf(element);
		return elementsAsMaster.get(indexOfElement);
		
	}
	
	public E getElement(M master) {
		
		int indexOfMaster = elementsAsMaster.indexOf(master);
		return elements.get(indexOfMaster);
		
	}
	
	public void add(E element, M master) {
		
		elements.add(element);
		elementsAsMaster.add(master);
		
	}
	
	public void remove(E element) {
		
		int indexOfElement = elements.indexOf(element);
		elements.remove(indexOfElement);
		elementsAsMaster.remove(indexOfElement);
		
	}
	
	public void clear() {
		
		elements.clear();
		elementsAsMaster.clear();
		
	}
	
	public int size() {
		
		return elements.size();
		
	}
	
	public Iterable<E> getElementsIterable() {
		
		return elements;
		
	}
	
	public Iterable<M> getMasterIterable() {
		
		return elementsAsMaster;
		
	}

}
