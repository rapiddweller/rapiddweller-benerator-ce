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

package com.rapiddweller.platform.xml;

import java.util.regex.Pattern;

import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.TypeDescriptor;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the XMLSchemaNativeTypeProvider.<br/><br/>
 * Created: 21.03.2008 09:44:43
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class XMLSchemaNativeTypeProviderTest {
    
    private final XMLNativeTypeDescriptorProvider provider = new XMLNativeTypeDescriptorProvider("xsd", new DataModel());

    @Test
    public void testDurationType() {
        checkIllegalValues("duration", 
                "PDT");
        checkLegalValues("duration", 
                "P3D", 
                "-P3D", 
                "PT6M", 
                "P2MT", 
                "-P1Y2M3DT5H6M7S", 
                "P100D");
    }

    @Test
    public void testGYearMonthType() {
        checkIllegalValues("gYearMonth", 
                "A-B", 
                "2000", 
                "01-12", 
                "2000-13", 
                "2000-1", 
                "2000-00");
        checkLegalValues("gYearMonth", 
                "2000-01", 
                "2000-12", 
                "1970-01", 
                "1969-12", 
                "0001-12", 
                "-2004-01", 
                "10000-01", 
                "-10000-01");
    }

    @Test
    public void testGYearType() {
        checkIllegalValues("gYear", 
                "A", 
                "-",
                "10",
                "999");
        checkLegalValues("gYear", 
                "1969", 
                "1970", 
                "1971", 
                "1999", 
                "2000", 
                "2001",
                "0001",
                "-0001",
                "10000",
                "-10000");
    }

    @Test
    public void testGMonthType() {
        checkIllegalValues("gMonth", 
                "A", 
                "-",
                "1",
                "00",
                "13");
        checkLegalValues("gMonth", 
                "01", 
                "12");
    }

    @Test
    public void testGDayType() {
        checkIllegalValues("gDay", 
                "A", 
                "-",
                "1",
                "00",
                "32");
        checkLegalValues("gDay", 
                "01", 
                "31");
    }

    @Test
    public void testGMonthDayType() {
        checkIllegalValues("gMonthDay", 
                "0-1", 
                "1-1", 
                "1-0", 
                "1-32",
                "01-1", 
                "1-01");
        checkLegalValues("gMonthDay",
                "12-31", 
                "01-01");
    }

    @Test
    public void testHexBinaryType() {
        checkIllegalValues("hexBinary", 
                "0", 
                "AG");
        checkLegalValues("hexBinary", 
                "",
                "0123456789ABCDEF", 
                "0123456789abcdef", 
                "00", 
                "FF", 
                "ff");
    }

    @Test
    public void testBase64BinaryType() {
        checkIllegalValues("base64Binary", 
                "?"); 
        checkLegalValues("base64Binary",
                "",
                "VGVzdA==", 
                "SGVsbG8gV29ybGQh");
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private void checkLegalValues(String typeName, String... values) {
        checkPatterns(typeName, true, values);
    }

    private void checkIllegalValues(String typeName, String... values) {
        checkPatterns(typeName, false, values);
    }

    private void checkPatterns(String typeName, boolean expectedMatch, String... values) {
        TypeDescriptor type = provider.getTypeDescriptor(typeName);
        Pattern p = Pattern.compile(type.getPattern());
        for (String pattern : values) {
            String errorMessage = "Value assumed to be " + (expectedMatch ? "legal" : "illegal") + " for type '" + typeName + "': " + pattern;
            assertEquals(errorMessage, expectedMatch, p.matcher(pattern).matches());
        }
    }
}
