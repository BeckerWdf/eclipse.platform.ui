/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.ui.dialogs;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.ILabelProvider;

/**
 * A class to select elements out of a list of elements.
 * 
 * @since 2.0
 */
public class ElementListSelectionDialog extends AbstractElementListSelectionDialog {
	
	private Object[] fElements;
	
	/**
	 * Creates a list selection dialog.
	 * @param parent   the parent widget.
	 * @param renderer the label renderer.
	 */	
	public ElementListSelectionDialog(Shell parent,	ILabelProvider renderer) {
		super(parent, renderer);
	}

	/**
	 * Sets the elements of the list.
	 * @param elements the elements of the list.
	 */
	public void setElements(Object[] elements) {
		fElements= elements;
	}

	/*
	 * @see SelectionStatusDialog#computeResult()
	 */
	protected void computeResult() {
		setResult(Arrays.asList(getSelectedElements()));
	}
	
	/*
	 * @see Dialog#createDialogArea(Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite contents= (Composite) super.createDialogArea(parent);
		
		createMessageArea(contents);
		createFilterText(contents);
		createFilteredList(contents);

		setListElements(fElements);

		List initialSelections= getInitialSelections();
		if (initialSelections != null)
			setSelection(initialSelections.toArray());
					
		return contents;
	}
}