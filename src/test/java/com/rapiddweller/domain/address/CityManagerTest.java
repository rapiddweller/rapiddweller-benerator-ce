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

package com.rapiddweller.domain.address;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Tests the {@link CityManager}.<br/><br/>
 * Created: 11.02.2010 18:27:23
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class CityManagerTest {

    @Test
    public void testCityHelperConstructor() {
        CityId cityId = new CityId("Name", "Name Extension");
        CityManager.CityHelper actualCityHelper = new CityManager.CityHelper(new State(), cityId,
                new String[]{"foo", "foo", "foo"}, "Area Code");
        assertEquals("Name Extension", actualCityHelper.getNameExtension());
        assertEquals("Area Code", actualCityHelper.getAreaCode());
        assertEquals(3, actualCityHelper.getPostalCodes().length);
        assertNull(actualCityHelper.getCountry());
        assertEquals("Name", actualCityHelper.getName());
    }

    @Test
    public void testCityHelperSetPostalCode() {
        CityId cityId = new CityId("Name", "Name Extension");
        CityManager.CityHelper cityHelper = new CityManager.CityHelper(new State(), cityId,
                new String[]{"foo", "foo", "foo"}, "Area Code");
        cityHelper.setPostalCode("Postal Code");
        assertEquals("Postal Code", cityHelper.getPostalCode());
    }

    @Test
    public void testGenerateGermanCity() {
        assertNotNull(Country.GERMANY.generateCity());
    }

    @Test
    public void testGermanAreaCodes() {
        Pattern pattern = Pattern.compile("\\d{2,5}");
        for (City city : Country.GERMANY.getCities()) {
            String areaCode = city.getAreaCode();
            assertTrue("Illegal area code: " + areaCode, pattern.matcher(areaCode).matches());
        }
    }

}
