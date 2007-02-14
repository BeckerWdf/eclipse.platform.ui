/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stefan Xenos, IBM - bug 156790: Adopt GridLayoutFactory within JFace
 *******************************************************************************/
package org.eclipse.jface.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * The IconAndMessageDialog is the abstract superclass of dialogs that have an
 * icon and a message as the first two widgets. In this dialog the icon and
 * message are direct children of the shell in order that they can be read by
 * accessibility tools more easily.
 */
public abstract class IconAndMessageDialog extends Dialog {
    /**
     * Message (a localized string).
     */
    protected String message;

    /**
     * Message label is the label the message is shown on.
     */
    protected Label messageLabel;

    /**
     * Return the label for the image.
     */
    protected Label imageLabel;

    /**
     * Constructor for IconAndMessageDialog.
     * 
     * @param parentShell
     *            the parent shell, or <code>null</code> to create a top-level
     *            shell
     */
    public IconAndMessageDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create the area the message will be shown in.
     * @param composite The composite to parent from.
     * @return Control
     */
    protected Control createMessageArea(Composite composite) {
        // create composite
        // create image
        Image image = getImage();
        if (image != null) {
            imageLabel = new Label(composite, SWT.NULL);
            image.setBackground(imageLabel.getBackground());
            imageLabel.setImage(image);
            addAccessibleListeners(imageLabel,image);
            GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(imageLabel);
        }
        // create message
        if (message != null) {
            messageLabel = new Label(composite, getMessageLabelStyle());
            messageLabel.setText(message);
            GridDataFactory.fillDefaults()
            	.align(SWT.FILL, SWT.BEGINNING)
            	.grab(true, false)
            	.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
            	.applyTo(messageLabel);
        }
        return composite;
    }


    private String getAccessibleMessageFor(Image image){
    	if(image.equals(getErrorImage())) {
			return JFaceResources.getString("error");//$NON-NLS-1$
		}
    	
    	if(image.equals(getWarningImage())) {
			return JFaceResources.getString("warning");//$NON-NLS-1$
		}
    	
    	if(image.equals(getInfoImage())) {
			return JFaceResources.getString("info");//$NON-NLS-1$
		}
    	
    	if(image.equals(getQuestionImage())) {
			return JFaceResources.getString("question"); //$NON-NLS-1$
		}
    	
    	return null;
    }
    
    /**
     * Add an accessible listener to the label if it can be 
     * inferred from the image.
     * @param label
     * @param image
     */
	private void addAccessibleListeners(Label label, final Image image) {
		label.getAccessible().addAccessibleListener(
				new AccessibleAdapter(){
    				public void getName(AccessibleEvent event) {
    					final String accessibleMessage = getAccessibleMessageFor(image);
    					if(accessibleMessage == null) {
							return;
						}    					
    					event.result = accessibleMessage;
    				}
    			}
		);
	}

	/**
     * Returns the style for the message label.
     * 
     * @return the style for the message label
     * 
     * @since 3.0
     */
    protected int getMessageLabelStyle() {
        return SWT.WRAP;
    }

    /*
     * @see Dialog.createButtonBar()
     */
    protected Control createButtonBar(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults()
        	.numColumns(0) // this is incremented by createButton
        	.equalWidth(true)
        	.applyTo(composite);
        
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).span(2,1).applyTo(composite);
        composite.setFont(parent.getFont());
        // Add the buttons to the button bar.
        createButtonsForButtonBar(composite);
        return composite;
    }

    /**
     * Returns the image to display beside the message in this dialog.
     * <p>
     * Subclasses may override.
     * </p>
     * 
     * @return the image to display beside the message
     * @since 2.0
     */
    protected abstract Image getImage();

    /*
     * @see Dialog.createContents(Composite)
     */
    protected Control createContents(Composite parent) {
        // initialize the dialog units
        initializeDialogUnits(parent);
        Point defaultMargins = LayoutConstants.getMargins();
        Point defaultSpacing = LayoutConstants.getSpacing();
        GridLayoutFactory.fillDefaults()
        	.margins(defaultMargins.x, defaultMargins.y * 3 / 2)
        	.spacing(defaultSpacing.x * 2, defaultSpacing.y)
        	.numColumns(2)
        	.applyTo(parent);
        
        GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);
        createDialogAndButtonArea(parent);
        return parent;
    }

    /**
     * Create the dialog area and the button bar for the receiver.
     * 
     * @param parent
     */
    protected void createDialogAndButtonArea(Composite parent) {
        // create the dialog area and button bar
        dialogArea = createDialogArea(parent);
        buttonBar = createButtonBar(parent);
        //Apply to the parent so that the message gets it too.
        applyDialogFont(parent);
    }

    /**
     * Return the <code>Image</code> to be used when 
     * displaying an error. 
     * 
     * @return image  the error image
     */
    public Image getErrorImage() {
        return getSWTImage(SWT.ICON_ERROR);
    }

    /**
     * Return the <code>Image</code> to be used when 
     * displaying a warning. 
     * 
     * @return image  the warning image
     */
    public Image getWarningImage() {
        return getSWTImage(SWT.ICON_WARNING);
    }

    /**
     * Return the <code>Image</code> to be used when 
     * displaying information. 
     * 
     * @return image  the information image
     */
    public Image getInfoImage() {
        return getSWTImage(SWT.ICON_INFORMATION);
    }

    /**
     * Return the <code>Image</code> to be used when 
     * displaying a question. 
     * 
     * @return image  the question image
     */
    public Image getQuestionImage() {
        return getSWTImage(SWT.ICON_QUESTION);
    }

    /**
     * Get an <code>Image</code> from the provide SWT image
     * constant.
     * 
     * @param imageID the SWT image constant
     * @return image  the image
     */
    private Image getSWTImage(final int imageID) {
        Shell shell = getShell();
        final Display display;
        if (shell == null) {
            shell = getParentShell();
        }
        if (shell == null) {
            display = Display.getCurrent();
        } else {
            display = shell.getDisplay();
        }

        final Image[] image = new Image[1];
        display.syncExec(new Runnable() {
            public void run() {
                image[0] = display.getSystemImage(imageID);
            }
        });

        return image[0];

    }

}
