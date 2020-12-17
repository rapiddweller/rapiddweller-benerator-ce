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

package com.rapiddweller.benerator.template.xmlanon;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps a single anonymization to be applied to certain data nodes of different files.<br/><br/>
 * Created: 27.02.2014 14:12:00
 * @since 0.9.0
 * @author Volker Bergmann
 */

public class Anonymization {
	
	private String varname;
	
	/** Characterizes the files and their data nodes to which to apply the anonymization. */
	private List<Locator> locators;
	
	/** Key-value pairs of Benerator anonymization settings as they would appear in a descriptor file. */
	private List<Setting> settings;
	
	public Anonymization(String varname) {
		this.varname = varname;
		this.locators = new ArrayList<Locator>();
		this.settings = new ArrayList<Setting>();
	}
	
	public String getVarname() {
		return varname;
	}
	
	public void addLocator(Locator locator) {
		this.locators.add(locator);
	}
	
	public List<Locator> getLocators() {
		return locators;
	}
	
	public void addSetting(String key, String value) {
		this.settings.add(new Setting(key, value));
	}
	
	public List<Setting> getSettings() {
		return settings;
	}
	
	@Override
	public String toString() {
		return varname;
	}
	
}