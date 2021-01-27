package it.pietro.subscriptionsmanager.view.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

public class CustomListModel<T> extends AbstractListModel<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final transient List<T> list;
	
	public CustomListModel() {
		list = new ArrayList<>(Collections.emptyList());
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public T getElementAt(int index) {
		return list.get(index);
	}
	
	public List<T> getList() {
		return list;
	}
	
	public void addElement(T sub) {
		if (!list.contains(sub)) {
			list.add(sub);
			fireContentsChanged(this, 0, getSize());
		}
	}
	
	public void removeElement(T sub) {
		list.remove(sub);
		fireContentsChanged(this, 0, getSize());
	}
}
