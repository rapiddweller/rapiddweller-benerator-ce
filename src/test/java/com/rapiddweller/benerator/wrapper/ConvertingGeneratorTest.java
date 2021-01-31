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

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.ConstantTestGenerator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.ThreadSafeConverter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link ConvertingGenerator}.<br/><br/>
 * Created: 11.10.2006 23:12:21
 * @since 0.1
 * @author Volker Bergmann
 */
public class ConvertingGeneratorTest extends GeneratorTest {

    @Test
    public void test() {
        ConstantTestGenerator<Integer> source = new ConstantTestGenerator<>(1);
        TestConverter converter = new TestConverter();
        ConvertingGenerator<Integer, String> generator = new ConvertingGenerator<>(source, converter);
        assertEquals("constructor", source.getLastMethodCall());
        assertEquals("1", GeneratorUtil.generateNonNull(generator));
        assertEquals("1", GeneratorUtil.generateNonNull(generator));
        assertSame(generator.getSource(), source);
        generator.reset();
        assertEquals("reset", source.getLastMethodCall());
        generator.close();
        assertEquals("close", source.getLastMethodCall());
    }

    private static class TestConverter extends ThreadSafeConverter<Integer, String> {

		public TestConverter() {
			super(Integer.class, String.class);
		}

		@Override
		public String convert(Integer sourceValue) throws ConversionException {
            return String.valueOf(sourceValue);
        }
    }
    
}
