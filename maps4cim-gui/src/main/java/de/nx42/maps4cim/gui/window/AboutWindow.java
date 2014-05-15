/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 - 2014 Sebastian Straub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.nx42.maps4cim.gui.window;

import java.awt.Window;

import de.nx42.maps4cim.ResourceLoader;
import de.nx42.maps4cim.gui.window.template.TextFieldPanel;

public class AboutWindow extends TextFieldPanel {

	private static final long serialVersionUID = 5583448871617188269L;

	public AboutWindow(Window owner) {
        super(owner, MESSAGES.getString("AboutWindow.this.title"), getMessageText());
        setBounds(200, 150, 695, 580);
    }

	protected static String getMessageText() {
	    String img = geHtmlImageTag(ResourceLoader.addBasePath("img/splash-8bit.png"));
        return "<html>" + img + MESSAGES.getString("AboutWindow.editorPaneAboutText.text") + "</html>";
	}

    protected static String geHtmlImageTag(String image) {
        return "<img src='" +  ClassLoader.getSystemResource(image) + "'>";
    }

}