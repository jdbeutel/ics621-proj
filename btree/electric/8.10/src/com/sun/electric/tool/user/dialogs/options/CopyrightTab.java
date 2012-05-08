/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: CopyrightTab.java
 *
 * Copyright (c) 2009 Sun Microsystems and Static Free Software
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
package com.sun.electric.tool.user.dialogs.options;

import com.sun.electric.database.text.Setting;
import com.sun.electric.tool.io.IOTool;
import com.sun.electric.tool.user.dialogs.PreferencesFrame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Class to handle the "Copyright" tab of the Preferences dialog.
 */
public class CopyrightTab extends PreferencePanel
{
    private Setting useCopyrightMessageSetting = IOTool.getUseCopyrightMessageSetting();
    private Setting copyrightMessageSetting = IOTool.getCopyrightMessageSetting();
	private JTextArea copyrightTextArea;

    /** Creates new form CopyrightTab */
	public CopyrightTab(PreferencesFrame parent, boolean modal)
	{
		super(parent, modal);
		initComponents();
	}

	/** return the JPanel to use for the project preferences. */
	public JPanel getProjectPreferencesPanel() { return projectSettings; }

	/** return the name of this preferences tab. */
	public String getName() { return "Copyright"; }

	/**
	 * Method called at the start of the dialog.
	 * Caches current values and displays them in the Copyright tab.
	 */
	public void init()
	{
		// project preferences
		if (getBoolean(useCopyrightMessageSetting)) copyrightUse.setSelected(true); else
			copyrightNone.setSelected(true);

		copyrightTextArea = new JTextArea();
		copyrightMessage.setViewportView(copyrightTextArea);
		copyrightTextArea.setText(getString(copyrightMessageSetting));
		copyrightTextArea.addKeyListener(new KeyAdapter()
		{
			public void keyTyped(KeyEvent evt) { copyrightMessageKeyTyped(evt); }
		});
	}

	public void term()
	{
		// project preferences
        setBoolean(useCopyrightMessageSetting, copyrightUse.isSelected());
        setString(copyrightMessageSetting, copyrightTextArea.getText());
	}

	/**
	 * Method called when the factory reset is requested.
	 */
	public void reset()
	{
	}

	private void copyrightMessageKeyTyped(KeyEvent evt)
	{
		copyrightUse.setSelected(true);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        whichCopyright = new javax.swing.ButtonGroup();
        projectSettings = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        copyrightNone = new javax.swing.JRadioButton();
        copyrightUse = new javax.swing.JRadioButton();
        copyrightMessage = new javax.swing.JScrollPane();
        jLabel5 = new javax.swing.JLabel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Tool Options");
        setName("");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        projectSettings.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("A Copyright message can be added to every generated deck");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        projectSettings.add(jLabel4, gridBagConstraints);

        whichCopyright.add(copyrightNone);
        copyrightNone.setText("No copyright message");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 4);
        projectSettings.add(copyrightNone, gridBagConstraints);

        whichCopyright.add(copyrightUse);
        copyrightUse.setText("Use this copyright message:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 4);
        projectSettings.add(copyrightUse, gridBagConstraints);

        copyrightMessage.setMinimumSize(new java.awt.Dimension(200, 200));
        copyrightMessage.setPreferredSize(new java.awt.Dimension(200, 200));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        projectSettings.add(copyrightMessage, gridBagConstraints);

        jLabel5.setText("Do not put comment characters in this message");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        projectSettings.add(jLabel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane().add(projectSettings, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)//GEN-FIRST:event_closeDialog
	{
		setVisible(false);
		dispose();
	}//GEN-LAST:event_closeDialog

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane copyrightMessage;
    private javax.swing.JRadioButton copyrightNone;
    private javax.swing.JRadioButton copyrightUse;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel projectSettings;
    private javax.swing.ButtonGroup whichCopyright;
    // End of variables declaration//GEN-END:variables

}