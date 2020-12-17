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

package com.rapiddweller.benerator.sample;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link ConstantGenerator}.<br/><br/>
 * Created: 11.10.2006 23:07:35
 * @since 0.1
 * @author Volker Bergmann
 */
public class ConstantGeneratorTest extends GeneratorClassTest {

    public ConstantGeneratorTest() {
        super(ConstantGenerator.class);
    }

    @Test
    public void testNull() {
        ConstantGenerator<String> generator = new ConstantGenerator<>(null, String.class);
        for (int i = 0; i < 100; i++)
            assertNull(GeneratorUtil.generateNullable(generator));
    }

    @Test    
    public void testString() {
        ConstantGenerator<String> generator = new ConstantGenerator<>("Alice");
        for (int i = 0; i < 100; i++)
            assertEquals("Alice", GeneratorUtil.generateNonNull(generator));
    }
    
}
