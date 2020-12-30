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

package com.rapiddweller.benerator.factory;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Encodings;
import org.junit.Test;

/**
 * Tests the {@link SourceFactory}.<br/><br/>
 * Created: 06.08.2011 13:13:19
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class SourceFactoryTest extends GeneratorTest {

    @Test
    public void testGetCSVCellGenerator() {
        Generator<String> generator = SourceFactory.createCSVCellGenerator("file://com/rapiddweller/csv/names-abc.csv", ',', Encodings.UTF_8);
        generator.init(context);
        assertEquals("Alice", nextProduct(generator));
        assertEquals("Bob", nextProduct(generator));
        assertEquals("Charly", nextProduct(generator));
        assertNull(generator.generate(new ProductWrapper<String>()));
    }

    @Test
    public void testGetArraySourceGenerator() {
        Generator<String[]> generator = SourceFactory.createCSVLineGenerator(
                "file://com/rapiddweller/csv/names-abc.csv", ',', Encodings.UTF_8, true);
        generator.init(context);
        assertEqualArrays(new String[] { "Alice", "Bob" }, nextProduct(generator));
        assertEqualArrays(new String[] { "Charly"}, nextProduct(generator));
        assertNull(generator.generate(new ProductWrapper<String[]>()));
    }

	protected <T> T nextProduct(Generator<T> generator) {
		return generator.generate(new ProductWrapper<T>()).unwrap();
	}

}
