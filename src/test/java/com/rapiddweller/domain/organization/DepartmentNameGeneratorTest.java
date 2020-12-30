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

package com.rapiddweller.domain.organization;

import static org.junit.Assert.*;

import java.util.Locale;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.common.LocaleUtil;
import org.junit.Test;

/**
 * Tests the {@link DepartmentNameGenerator}.<br/>
 * <br/>
 * Created: 14.10.2009 10:44:23
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class DepartmentNameGeneratorTest extends GeneratorClassTest {
	
	public DepartmentNameGeneratorTest() {
	    super(DepartmentNameGenerator.class);
    }

	@Test
	public void testLocales() {
		Runnable runner = new Runnable() {
			@Override
			@SuppressWarnings("synthetic-access")
			public void run() {
				logger.debug("Checking Locale " + Locale.getDefault());
				DepartmentNameGenerator generator = new DepartmentNameGenerator();
				generator.init(context);
				for (int i = 0; i < 100; i++) {
					String product = generator.generate();
					logger.debug(product);
					assertNotNull(product);
				}
            }
		};
		LocaleUtil.runInLocale(Locale.US, runner);
		LocaleUtil.runInLocale(Locale.GERMAN, runner);
		LocaleUtil.runInLocale(new Locale("XX"), runner);
	}
	
}
