/*
 * Copyright 2006-2009 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.visualization.molstructure;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import net.sf.mzmine.util.GUIUtils;
import net.sf.mzmine.util.InetUtils;
import net.sf.mzmine.util.components.MultiLineLabel;

public class MolStructureViewer extends JInternalFrame {

	private JSplitPane splitPane;
	private JLabel statusLabel;

	/**
	 * 
	 */
	public MolStructureViewer(String name, final URL structure2DAddress,
			final URL structure3DAddress) {

		super("Structure of " + name, true, true, true, true);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Main panel - contains a title (compound name) in the top, 2D
		// structure on the left, 3D structure on the right
		JPanel mainPanel = new JPanel(new BorderLayout());

		JLabel labelName = new JLabel(name, SwingConstants.CENTER);
		labelName.setOpaque(true);
		labelName.setBackground(Color.white);
		labelName.setForeground(Color.BLUE);
		Border one = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		Border two = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		labelName.setBorder(BorderFactory.createCompoundBorder(one, two));
		labelName.setFont(new Font("SansSerif", Font.BOLD, 18));
		mainPanel.add(labelName, BorderLayout.NORTH);

		JLabel loading2Dlabel = new JLabel("Loading 2D structure...",
				SwingConstants.CENTER);
		loading2Dlabel.setOpaque(true);
		loading2Dlabel.setBackground(Color.white);
		JLabel loading3Dlabel = new JLabel("Loading 3D structure...",
				SwingConstants.CENTER);
		loading3Dlabel.setOpaque(true);
		loading3Dlabel.setBackground(Color.white);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, loading2Dlabel, loading3Dlabel);
		splitPane.setResizeWeight(0.5);

		mainPanel.add(splitPane, BorderLayout.CENTER);

		statusLabel = new JLabel();
		GUIUtils.addMargin(statusLabel, 5);
		mainPanel.add(statusLabel, BorderLayout.SOUTH);

		add(mainPanel);

		setPreferredSize(new Dimension(900, 500));

		pack();
		
		// Set the initial splitter location, after the window is packed
		splitPane.setDividerLocation(500);

		if (structure2DAddress != null) {
			Thread loading2DThread = new Thread(new Runnable() {
				public void run() {
					load2DStructure(structure2DAddress);
				}
			}, "Structure loading thread");
			loading2DThread.start();
		} else {
			loading2Dlabel.setText("2D structure not available");
		}

		if (structure3DAddress != null) {
			Thread loading3DThread = new Thread(new Runnable() {
				public void run() {
					load3DStructure(structure3DAddress);
				}
			}, "Structure loading thread");
			loading3DThread.start();
		} else {
			loading3Dlabel.setText("3D structure not available");
		}

	}

	/**
	 * Load the structure passed as parameter in JChemViewer
	 * 
	 */
	private void load2DStructure(URL url) {

		JComponent newComponent;
		try {
			String structure2D = InetUtils.retrieveData(url);
			newComponent = new Structure2DComponent(structure2D, statusLabel);
		} catch (Exception e) {
			String errorMessage = "Could not load 2D structure\n"
					+ "Exception: " + e.toString();
			newComponent = new MultiLineLabel(errorMessage);
		}
		splitPane.setLeftComponent(newComponent);
		splitPane.setDividerLocation(500);
	}

	/**
	 * Load the structure passed as parameter in JmolViewer
	 * 
	 * @param structure
	 */
	private void load3DStructure(URL url) {

		JComponent newComponent;
		try {
			String structure3D = InetUtils.retrieveData(url);
			newComponent = new Structure3DComponent(structure3D);
		} catch (Exception e) {
			String errorMessage = "Could not load 3D structure\n"
					+ "Exception: " + e.toString();
			newComponent = new MultiLineLabel(errorMessage, 10);
		}
		splitPane.setRightComponent(newComponent);
		splitPane.setDividerLocation(500);
	}
}
