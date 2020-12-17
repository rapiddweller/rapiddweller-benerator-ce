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

package com.rapiddweller.benerator.primitive.number;

import static org.junit.Assert.*;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import org.junit.Test;

/**
 * Tests the {@link NoiseInducer}.<br/><br/>
 * Created: 08.10.2010 21:42:30
 * @since 0.5.4
 * @author Volker Bergmann
 */
public class NoiseInducerTest {

	@Test
	public void testConvert_absolute() {
		NoiseInducer inducer = new NoiseInducer(-2., 2., 0.01);
		inducer.setContext(new DefaultBeneratorContext());
		inducer.setRelative(false);
		for (int i = 0; i < 100; i++) {
			Number result = inducer.convert(0.);
			assertTrue(result.intValue() >= -2. && result.intValue() <= 2.);
		}
	}
	
	@Test
	public void testConvert_relative() {
		NoiseInducer inducer = new NoiseInducer(-0.5, 0.5, 0.01);
		inducer.setRelative(true);
		inducer.setContext(new DefaultBeneratorContext());
		for (int i = 0; i < 100; i++) {
			assertEquals(0., inducer.convert(0.));
		}
	}

	@Test
	public void testConvertMinMax() {
		NoiseInducer inducer = new NoiseInducer(-2., 2., 1);
		inducer.setRelative(false);
		inducer.setContext(new DefaultBeneratorContext());
		for (int i = 0; i < 100; i++) {
			Number result = inducer.convert(0, -1., 1.);
			assertTrue(result.intValue() >= -1. && result.intValue() <= 1.);
		}
	}
	
}
