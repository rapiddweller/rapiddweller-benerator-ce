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

import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Tests the {@link City} class.<br/><br/>
 * Created at 02.05.2008 13:27:53
 *
 * @author Volker Bergmann
 * @since 0.5.3
 */
public class CityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstructor() {
        City actualCity = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        assertEquals("Addition", actualCity.getNameExtension());
        assertEquals("Area Code", actualCity.getAreaCode());
        assertEquals(3, actualCity.getPostalCodes().length);
        assertNull(actualCity.getCountry());
        assertEquals("Name", actualCity.getName());
    }

    @Test
    public void testConstructor2() {
        City actualCity = new City(new State(), "Name", "Addition", null, "Area Code");
        assertEquals("Addition", actualCity.getNameExtension());
        assertEquals("Area Code", actualCity.getAreaCode());
        assertEquals(0, actualCity.getPostalCodes().length);
        assertNull(actualCity.getCountry());
        assertEquals("Name", actualCity.getName());
    }

    @Test
    public void testConstructor3() {
        thrown.expect(IllegalArgumentException.class);
        new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, null);
    }

    @Test
    public void testSetNameExtension() {
        City city = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        city.setNameExtension("Name Extension");
        assertEquals("Name Extension", city.getNameExtension());
    }

    @Test
    public void testSetPostalCodes() {
        City city = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        String[] stringArray = new String[]{"foo", "foo", "foo"};
        city.setPostalCodes(stringArray);
        assertSame(stringArray, city.getPostalCodes());
    }

    @Test
    public void testAddPostalCode() {
        City city = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        city.addPostalCode("Postal Code");
        assertEquals(4, city.getPostalCodes().length);
    }

    @Test
    public void testgetPostalCodes() {
        assertEquals(3, (new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code"))
                .getPostalCodes().length);
    }

    @Test
    public void testSetZipCodes() {
        City city = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        String[] stringArray = new String[]{"foo", "foo", "foo"};
        city.setZipCodes(stringArray);
        assertSame(stringArray, city.getPostalCodes());
    }

    @Test
    public void testAddZipCode() {
        City city = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        city.addZipCode("21654");
        assertEquals(4, city.getPostalCodes().length);
    }

    @Test
    public void testSetAreaCode() {
        City city = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        city.setAreaCode("4105551212");
        assertEquals("4105551212", city.getAreaCode());
    }

    @Test
    public void testSetState() {
        City city = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        city.setState(new State());
        assertNull(city.getCountry());
    }

    @Test
    public void testGetCountry() {
        assertNull(
                (new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code")).getCountry());
        assertNull((new City(null, "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code")).getCountry());
    }

    @Test
    public void testGetLanguage() {
        assertNull((new City(null, "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code")).getLanguage());
    }

    @Test
    public void testGetLanguage2() {
        Locale locale = new Locale("en");
        State state = new State();
        state.setDefaultLanguageLocale(locale);
        assertSame(locale,
                (new City(state, "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code")).getLanguage());
    }

    @Test
    public void testGetLanguage3() {
        City city = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        Locale locale = new Locale("en");
        city.setLanguage(locale);
        assertSame(locale, city.getLanguage());
    }

    @Test
    public void testSetLanguage() {
        City city = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        Locale locale = new Locale("en");
        city.setLanguage(locale);
        assertSame(locale, city.getLanguage());
    }

    @Test
    public void testSetPopulation() {
        City city = new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code");
        city.setPopulation(2);
        assertEquals(2, city.getPopulation());
    }

    @Test
    public void testToString() {
        assertEquals("Name Addition",
                (new City(new State(), "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code")).toString());
        assertEquals("Name ",
                (new City(new State(), "Name", " ", new String[]{"foo", "foo", "foo"}, "Area Code")).toString());
        assertEquals("Name",
                (new City(new State(), "Name", "", new String[]{"foo", "foo", "foo"}, "Area Code")).toString());
    }

    @Test
    public void testEquals2() {
        assertNotEquals("o", (new City(new State(), "Name", "Addition",
                new String[] {"foo", "foo", "foo"}, "Area Code")));
    }

    @Test
    public void testEquals3() {
        assertNotEquals(null, (new City(new State(), "Name", "Addition",
                new String[] {"foo", "foo", "foo"}, "Area Code")));
    }

    @Test
    public void testHashCode() {
        State state = new State();
        state.setName("Name");
        assertEquals(-1593670502,
                (new City(state, "Name", "Addition", new String[]{"foo", "foo", "foo"}, "Area Code")).hashCode());
    }

    @Test
    public void testEquals() {
        State bavaria = new State("BY");
        City city = new City(bavaria, "Munich", null, null, "89");
        assertNotEquals(null, city);
        assertNotEquals(city, bavaria);
        assertEquals(city, city);
        assertEquals(city, new City(bavaria, "Munich", null, null, "89"));
        assertNotEquals(city, new City(bavaria, "Nuremberg", null, null, "89"));
    }

}
