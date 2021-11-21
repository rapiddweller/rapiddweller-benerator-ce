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

import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.exception.ParseException;
import com.rapiddweller.common.file.FileResourceNotFoundException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Tests the {@link CityManager}.<br/><br/>
 * Created: 11.02.2010 18:27:23
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class CityManagerTest {

    @Test
    public void testCityHelperConstructor2() {
        State state = new State();
        CityManager.CityHelper actualCityHelper = new CityManager.CityHelper(state, new CityId("Name", "Name Extension"),
                new String[]{"foo", "foo", "foo"}, "Area Code");
        actualCityHelper.setPostalCode("Postal Code");
        assertEquals("Postal Code", actualCityHelper.getPostalCode());
    }

    @Test
    public void testCreateCityId() {
        assertThrows(ParseException.class, () -> CityManager.createCityId(new HashMap<String, String>(1), 2));
    }

    @Test
    public void testCreateCityId2() {
        HashMap hashMap = new HashMap();
        hashMap.put((String) "nameExtension", "foo");
        hashMap.put((String) "city", "foo");
        hashMap.put((String) "name", "foo");
        hashMap.put((String) "municipality", "foo");
        CityId actualCreateCityIdResult = CityManager.createCityId(hashMap, 1);
        assertEquals("foo", actualCreateCityIdResult.getName());
        assertNull(actualCreateCityIdResult.getNameExtension());
    }

    @Test
    public void testCreateCityId3() {
        HashMap hashMap = new HashMap();
        hashMap.put((String) "nameExtension", "foo");
        hashMap.put((String) "city", "foo");
        hashMap.put((String) "name", "foo");
        CityId actualCreateCityIdResult = CityManager.createCityId(hashMap, 1);
        assertEquals("foo", actualCreateCityIdResult.getName());
        assertNull(actualCreateCityIdResult.getNameExtension());
    }

    @Test
    public void testCreateCityId4() {
        HashMap hashMap = new HashMap();
        hashMap.put((String) "nameExtension", "foo");
        hashMap.put((String) "name", "foo");
        CityId actualCreateCityIdResult = CityManager.createCityId(hashMap, 1);
        assertEquals("foo", actualCreateCityIdResult.getName());
        assertEquals("foo", actualCreateCityIdResult.getNameExtension());
    }

    @Test
    public void testCreateCityId5() {
        HashMap<String, String> stringStringMap = new HashMap<String, String>(1);
        stringStringMap.put("municipality", "");
        assertThrows(ParseException.class, () -> CityManager.createCityId(stringStringMap, 2));
    }

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

    @Test
    public void testCreateCityReadFromFile() throws IOException {
        CityManager.persistCities(Country.UNITED_KINGDOM, "target/test_city.csv");
        HashMap map = new HashMap();
        map.put("state.country.isoCode", "state.country.isoCode");
        map.put("state.id", "state.id");
        map.put("name", "name");
        map.put("nameExtension", "nameExtension");
        map.put("areaCode", "areaCode");
        map.put("language", "language");
        CityManager.readCities(Country.AFGHANISTAN, "target/test_city.csv", map);
        assertTrue(new File("target/test_city.csv").exists());
    }

    @Test
    public void testyReadFromNullFile() {
        HashMap map = new HashMap();
        assertThrows(FileResourceNotFoundException.class, () ->CityManager.readCities(Country.AFGHANISTAN, "/", map));
        assertThrows(ConfigurationError.class, () ->CityManager.readCities(Country.AFGHANISTAN, null, map));
    }

}
