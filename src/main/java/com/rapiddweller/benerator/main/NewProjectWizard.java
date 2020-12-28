/*
 * (c) Copyright 2006-2020 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from rapiddweller GmbH & Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.rapiddweller.benerator.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.rapiddweller.benerator.gui.CreateProjectPanel;
import com.rapiddweller.commons.ui.I18NSupport;

/**
 * Main class for the benerator GUI.<br/>
 * <br/>
 * Created at 17.07.2008 09:32:54
 * @since 0.5.6
 * @author Volker Bergmann
 */
public class NewProjectWizard extends JFrame {
	
	private static final long serialVersionUID = -359209516189875124L;

	final I18NSupport i18n;
	final CreateProjectPanel mainPanel;

	public NewProjectWizard() {
		setIcons("com/rapiddweller/benerator/gui/benerator{0}.png", 16, 32, 64, 128);
		
		i18n = new I18NSupport("com/rapiddweller/benerator/gui/benerator", Locale.getDefault());
		mainPanel = new CreateProjectPanel(i18n);
		
		setTitle(i18n.getString("newProjectWizardTitle"));
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(mainPanel, BorderLayout.CENTER);
		getRootPane().setDefaultButton(mainPanel.getCreateButton());
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mainPanel.exit();
			}
		});
		pack();
		setLocationRelativeTo(null);
	}

	private void setIcons(String pattern, int... sizes) {
		List<Image> images = new ArrayList<>();
		for (int size : sizes) {
			String name = MessageFormat.format(pattern, size);
			images.add(new ImageIcon(name).getImage());
		}

		this.setIconImages(images);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new NewProjectWizard().setVisible(true);
	}
}
