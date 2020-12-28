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

package com.rapiddweller.benerator;

import static org.junit.Assert.*;

import java.util.Map;

import com.rapiddweller.commons.version.VersionInfo;
import org.junit.Test;

/**
 * Tests the {@link VersionInfo} class.<br/><br/>
 * Created: 23.03.2011 11:34:32
 * @since 0.6.6
 * @author Volker Bergmann
 */
public class VersionInfoTest {

	@Test
	public void testVersion() {
		VersionInfo version = getVersionInfo();
		checkVersionNumber(version.getVersion());
		System.out.println(version);
	}

	@Test
	public void testVerifyDependencies() {
		VersionInfo version = getVersionInfo();
		version.verifyDependencies();
	}

	@Test
	public void testDependencies() {
		VersionInfo version = getVersionInfo();
		Map<String, String> dependencies = version.getDependencies();
		assertEquals(5, dependencies.size());
		checkDependency("jdbacl", dependencies);
		checkDependency("format", dependencies);
		checkDependency("script", dependencies);
		checkDependency("contiperf", dependencies);
		checkDependency("common", dependencies);
	}
	
	private static void checkDependency(String name, Map<String, String> dependencies) {
		String dependencyVersion = dependencies.get(name);
		checkVersionNumber(dependencyVersion);
		System.out.println("using " + name + ' ' + dependencyVersion);
	}

	@SuppressWarnings("null")
	private static void checkVersionNumber(String versionNumber) {
		assertFalse("version number is empty", versionNumber == null || versionNumber.length() == 0);
		assertFalse("version number was not substituted", versionNumber.startsWith("${"));
	}
	
	private static VersionInfo getVersionInfo() {
		return VersionInfo.getInfo("benerator");
	}

}
