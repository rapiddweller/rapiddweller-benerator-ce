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

package com.rapiddweller.benerator.consumer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rapiddweller.benerator.test.ModelTest;
import com.rapiddweller.commons.Patterns;
import com.rapiddweller.commons.SystemInfo;
import com.rapiddweller.commons.converter.TimestampFormatter;
import com.rapiddweller.model.data.Entity;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the {@link ConsoleExporter}.<br/><br/>
 * Created at 11.04.2008 06:58:53
 * @since 0.5.2
 * @author Volker Bergmann
 */
public class ConsoleExporterTest extends ModelTest {
	
	private static final String LF = SystemInfo.getLineSeparator();

	@Test
	public void testSimpleTypes() {
		check("Test" + LF, "Test");
		check("1" + LF, 1);
		check("1" + LF, 1.);
		check("true" + LF, true);
	}

	@Test
	public void testDate() {
		Date date = new Date(((60 + 2) * 60 + 3) * 1000);
		check(new SimpleDateFormat("yyyy-MM-dd").format(date) + LF, date);
	}

	@Test
	public void testTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		TimestampFormatter formatter = new TimestampFormatter(Patterns.DEFAULT_DATETIME_SECONDS_PATTERN + '.');
		check(formatter.format(timestamp) + LF, timestamp);
	}
	
	@Test
	public void testEntity() {
		Entity entity = createEntity("e", "i", 3, "d", 5., "s", "sss");
		check("e[i=3, d=5, s=sss]" + LF, entity);
	}
	
	@Test
	public void testLimit() {
		Entity entity = createEntity("e", "i", 3, "d", 5., "s", "sss");
		check(new ConsoleExporter(1L), "e[i=3, d=5, s=sss]" + LF + '.', entity, entity);
	}
	
	@Test
	public void testIndent() {
		Entity entity = createEntity("e", "i", 3, "d", 5., "s", "sss");
		check(new ConsoleExporter(-1L, "xxx"), "xxxe[i=3, d=5, s=sss]" + LF, entity);
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------

	private static void check(String expectedOut, Object... ins) {
		check(new ConsoleExporter(), expectedOut, ins);
	}
	
	private static void check(ConsoleExporter exporter, String expectedOut, Object... ins) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		exporter.setOut(new PrintStream(stream));
		try {
			for (Object in : ins) {
				exporter.startProductConsumption(in);
				exporter.finishProductConsumption(in);
			}
			exporter.flush();
			assertEquals(expectedOut, stream.toString());
		} finally {
			exporter.close();
		}
	}
	
}
