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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.common.converter.ConverterManager;
import com.rapiddweller.common.converter.ParseFormatConverter;
import com.rapiddweller.benerator.test.GeneratorClassTest;
import com.rapiddweller.benerator.util.GeneratorUtil;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link SequencedCSVSampleGenerator}.<br/>
 * Created: 26.07.2007 18:16:11
 * @author Volker Bergmann
 */
public class SequencedCSVSampleGeneratorTest extends GeneratorClassTest {

    public SequencedCSVSampleGeneratorTest() {
        super(SequencedCSVSampleGenerator.class);
    }

    private static final String DATE_FILE_PATH = "com/rapiddweller/benerator/csv/dates.csv";
    private static final String BIG_FILE_NAME = "many_dates.csv";

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @Test
    public void testSmallSet() throws ParseException {
    	// prepare
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        ParseFormatConverter<Date> converter = new ParseFormatConverter<Date>(Date.class, format, false);
        SequencedCSVSampleGenerator<Date> generator = new SequencedCSVSampleGenerator<Date>(DATE_FILE_PATH, converter);
        generator.init(context);
        // test
        List<Date> expectedDates = CollectionUtil.toList(sdf.parse("01.02.2003"), sdf.parse("02.02.2003"), sdf.parse("03.02.2003"));
        for (int i = 0; i < 100; i++) {
            Date generatedDate = GeneratorUtil.generateNonNull(generator);
            assertTrue("generated date not in expected value set: " + sdf.format(generatedDate),
                    expectedDates.contains(generatedDate));
        }
    }

    @Test
    public void testBigSet() throws Exception {
    	File csvFile = new File(BIG_FILE_NAME);
    	try {
    		// prepare
	    	PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(csvFile)));
	    	// create large CSV file
	        for (int i = 0; i < 200000; i++)
	        	writer.println(i % 100);
	    	writer.close();
	    	
	    	// test generator
	    	Converter<String, Integer> converter = ConverterManager.getInstance().createConverter(
	    			String.class, Integer.class);
	        SequencedCSVSampleGenerator<Integer> generator 
	        	= new SequencedCSVSampleGenerator<Integer>(BIG_FILE_NAME, converter);
	        generator.init(context);
	        for (int i = 0; i < 1000; i++) {
	            int product = GeneratorUtil.generateNonNull(generator);
	            assertTrue("generated value not in expected value range: " + product, 0 <= product && product <= 99);
	        }
    	} finally {
	        // delete CSV file
	        FileUtil.deleteIfExists(csvFile);
    	}
    }
    
}
