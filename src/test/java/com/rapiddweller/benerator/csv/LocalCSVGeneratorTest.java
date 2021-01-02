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

package com.rapiddweller.benerator.csv;

import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.Encodings;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.benerator.util.GeneratorUtil;

import java.util.Set;
import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link LocalCSVGenerator}.<br/><br/>
 * Created: 14.06.2007 07:01:53
 * @since 0.1
 * @author Volker Bergmann
 */
public class LocalCSVGeneratorTest extends GeneratorTest {

    private static final String CSV_LOCAL_FILENAME = "com/rapiddweller/benerator/csv/local-names";

    @Test
    public void testEnglish() {
        Set<String> enNames = CollectionUtil.toSet("Alice", "Bob", "Charly");
        LocalCSVGenerator<String> enGen = new LocalCSVGenerator<>(String.class,
                CSV_LOCAL_FILENAME, Locale.ENGLISH, ".csv", Encodings.UTF_8);
        enGen.init(context);
        for (int i = 0; i < 10; i++) {
            String name = GeneratorUtil.generateNonNull(enGen);
            assertTrue(enNames.contains(name));
        }
    }

    @Test
    public void testGerman() {
        Set<String> deNames = CollectionUtil.toSet("Elise", "Robert", "Karl");
        LocalCSVGenerator<String> deGen = new LocalCSVGenerator<>(String.class,
                CSV_LOCAL_FILENAME, Locale.GERMAN, ".csv", Encodings.UTF_8);
        deGen.init(context);
        for (int i = 0; i < 10; i++) {
            String name = GeneratorUtil.generateNonNull(deGen);
            assertTrue(deNames.contains(name));
        }
    }
    
}
