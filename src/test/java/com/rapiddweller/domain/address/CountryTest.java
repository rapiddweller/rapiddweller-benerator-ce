/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the {@link Country} class.<br/><br/>
 * Created at 26.08.2008 11:46:32
 * @author Volker Bergmann
 * @since 0.5.5
 */
public class CountryTest {

  @Test
  public void testInstances() {
    assertEquals(Country.GERMANY, Country.getInstance("DE"));
    assertEquals(Country.US, Country.getInstance("US"));
    assertEquals(Country.UNITED_KINGDOM, Country.getInstance("GB"));
    assertNotNull(Country.getInstance("SO"));
    assertNotNull(Country.GREAT_BRITAIN);
    assertNotNull(Country.SAN_MARINO);
    assertNotNull(Country.BOSNIA_AND_HERZEGOVINA);
    assertNotNull(Country.CZECH_REPUBLIC);
    assertNotNull(Country.UNITED_ARAB_EMIRATES);
    assertNotNull(Country.QATAR);
    assertNotNull(Country.SAUDI_ARABIA);
    assertNotNull(Country.SOUTH_AFRICA);
    assertNotNull(Country.KOREA_PR);
    assertNotNull(Country.KOREA_R);
    assertNotNull(Country.NEW_ZEALAND);
    assertNotNull(Country.SRI_LANKA);
  }

  @Test
  public void testGeneralProperties() {
    for (Country country : Country.getInstances()) {
      assertNotNull(country.getDefaultLanguageLocale());
      assertNotNull(country.getIsoCode());
      assertNotNull(country.getDisplayName());
      assertNotNull(country.getName());
      assertNotNull(country.getLocalName());
    }
  }

  @Test
  public void testUS() {
    assertEquals("US", Country.US.getIsoCode());
    assertEquals(Locale.US, Country.US.getDefaultLanguageLocale());
  }

  @Test
  public void testDE() {
    assertEquals("DE", Country.GERMANY.getIsoCode());
    assertEquals(Locale.GERMANY, Country.GERMANY.getDefaultLanguageLocale());
  }

}
