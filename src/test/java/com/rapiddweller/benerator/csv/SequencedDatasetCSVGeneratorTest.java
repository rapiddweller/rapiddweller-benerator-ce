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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.test.GeneratorTest;
import com.rapiddweller.commons.Encodings;
import org.junit.Test;


/**
 * Tests the {@link SequencedDatasetCSVGenerator}.<br/><br/>
 * Created: 18.02.2010 00:09:59
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class SequencedDatasetCSVGeneratorTest extends GeneratorTest {
	
	private static final String FILENAME_PATTERN = "com/rapiddweller/benerator/csv/city_{0}.csv";
	private static final String NESTING = "com/rapiddweller/benerator/csv/area";
	private static final char SEPARATOR = ',';
	private static final String ENCODING = Encodings.UTF_8;
	private static final Sequence DISTRIBUTION = SequenceManager.STEP_SEQUENCE;

	private static final String NORTH_AMERICA = "north_america";
	private static final String SOUTH_AMERICA = "south_america";
	private static final String ANGLO_AMERICA = "anglo_america";
	private static final String LATIN_AMERICA = "latin_america";
	private static final String AMERICA = "america";
	
	private static final String SAO_PAOLO = "Sao Pãolo";
	private static final String BRASILIA = "Brasilia";
	private static final String MEXICO = "Mexico";
	private static final String VILLAHERMOSA = "Villahermosa";
	private static final String NEW_YORK = "New York";
	private static final String SAN_FRANCISCO = "San Francisco";

	@Test
	public void test() {
		expectUniquelyGeneratedSet(createDatasetGenerator(NORTH_AMERICA), NEW_YORK, SAN_FRANCISCO, MEXICO, VILLAHERMOSA);
		expectUniquelyGeneratedSet(createDatasetGenerator(SOUTH_AMERICA), SAO_PAOLO, BRASILIA);
		expectUniquelyGeneratedSet(createDatasetGenerator(ANGLO_AMERICA), NEW_YORK, SAN_FRANCISCO);
		expectUniquelyGeneratedSet(createDatasetGenerator(LATIN_AMERICA), MEXICO, VILLAHERMOSA, SAO_PAOLO, BRASILIA);
		expectUniquelyGeneratedSet(createDatasetGenerator(AMERICA), NEW_YORK, SAN_FRANCISCO, MEXICO, VILLAHERMOSA, SAO_PAOLO, BRASILIA);

	}

	private Generator<String> createDatasetGenerator(String datasetName) {
	    SequencedDatasetCSVGenerator<String> generator = new SequencedDatasetCSVGenerator<String>(
				FILENAME_PATTERN, SEPARATOR, datasetName, NESTING, DISTRIBUTION, ENCODING, context);
	    generator.init(context);
		return generator;
    }

}
