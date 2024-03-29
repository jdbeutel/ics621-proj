/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: SetFocus.java
 *
 * Copyright (c) 2004 Sun Microsystems and Static Free Software
 *
 * Electric(tm) is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Electric(tm) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Electric(tm); see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, Mass 02111-1307, USA.
 */
package com.sun.electric.tool.user.dialogs;

import com.sun.electric.database.text.TextUtils;
import com.sun.electric.technology.Technology;
import com.sun.electric.tool.user.ui.EditWindow;
import com.sun.electric.tool.user.ui.EditWindowFocusBrowser;
import com.sun.electric.tool.user.ui.TopLevel;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.geom.Point2D;

/**
 * Class to handle the "Set Focus" dialog.
 */
public class SetFocus extends EDialog
{
	private EditWindow wnd;

	public static void showSetFocusDialog()
	{
		EditWindow wnd = EditWindow.needCurrent();
		if (wnd == null) return;
		if (wnd.getCell() == null)
		{
			System.out.println("Must be editing a cell in order to manipulate focus.");
			return;
		}
		SetFocus dialog = new SetFocus(TopLevel.getCurrentJFrame(), wnd);
		dialog.setVisible(true);
	}

	/** Creates new form Set Focus */
	public SetFocus(Frame parent, EditWindow wnd)
	{
		super(parent, true);
		this.wnd = wnd;
		initComponents();

		// make all text fields select-all when entered
	    EDialog.makeTextFieldSelectAllOnTab(xCenter);
	    EDialog.makeTextFieldSelectAllOnTab(yCenter);
	    EDialog.makeTextFieldSelectAllOnTab(unitsAcross);

	    getRootPane().setDefaultButton(ok);
		Dimension sz = wnd.getSize();
		Point2D offset = wnd.getOffset();
		double scale = wnd.getScale();
		Technology tech = wnd.getCell().getTechnology();
		xCenter.setText(TextUtils.formatDistance(offset.getX(), tech));
		yCenter.setText(TextUtils.formatDistance(offset.getY(), tech));
		unitsAcross.setText(TextUtils.formatDistance(sz.getWidth() / scale, tech));
		finishInitialization();
	}

	protected void escapePressed() { cancel(null); }

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        cancel = new javax.swing.JButton();
        ok = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        xCenter = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        yCenter = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        unitsAcross = new javax.swing.JTextField();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Set Focus");
        setName("");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(cancel, gridBagConstraints);

        ok.setText("OK");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ok(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(ok, gridBagConstraints);

        jLabel1.setText("X Center:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jLabel1, gridBagConstraints);

        xCenter.setColumns(12);
        xCenter.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(xCenter, gridBagConstraints);

        jLabel2.setText("Y Center:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jLabel2, gridBagConstraints);

        yCenter.setColumns(12);
        yCenter.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(yCenter, gridBagConstraints);

        jLabel3.setText("Horizontal Grid Units:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(jLabel3, gridBagConstraints);

        unitsAcross.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(unitsAcross, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void cancel(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancel
	{//GEN-HEADEREND:event_cancel
		closeDialog(null);
	}//GEN-LAST:event_cancel

	private void ok(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ok
	{//GEN-HEADEREND:event_ok
		Technology tech = wnd.getCell().getTechnology();
		double xC = TextUtils.atofDistance(xCenter.getText(), tech);
		double yC = TextUtils.atofDistance(yCenter.getText(), tech);
		double across = TextUtils.atofDistance(unitsAcross.getText(), tech);

		Dimension sz = wnd.getSize();
		wnd.setOffset(new Point2D.Double(xC, yC));
		wnd.setScale(sz.getWidth() / across);
		EditWindowFocusBrowser fb = wnd.getSavedFocusBrowser();
		fb.updateCurrentFocus();
		wnd.fullRepaint();
		closeDialog(null);
	}//GEN-LAST:event_ok

	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)//GEN-FIRST:event_closeDialog
	{
		setVisible(false);
		dispose();
	}//GEN-LAST:event_closeDialog

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton ok;
    private javax.swing.JTextField unitsAcross;
    private javax.swing.JTextField xCenter;
    private javax.swing.JTextField yCenter;
    // End of variables declaration//GEN-END:variables
}
