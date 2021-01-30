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

import com.rapiddweller.common.NullSafeComparator;
import com.rapiddweller.common.OrderedMap;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a state.<br/>
 * <br/>
 * Created: 27.07.2007 17:37:12
 *
 * @author Volker Bergmann
 * @since 0.3
 */
public class State {

    private String id;
    private String name;
    private Country country;
    private final Map<CityId, City> cities;
    private int population;
    private Locale defaultLanguageLocale;

    // constructors ----------------------------------------------------------------------------------------------------

    public State() {
        this(null);
    }

    public State(String id) {
        this.id = id;
        this.cities = new OrderedMap<>();
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultLanguage() {
        return getDefaultLanguageLocale().getLanguage();
    }

    public void setDefaultLanguage(String defaultLanguage) {
        setDefaultLanguageLocale(new Locale(defaultLanguage));
    }

    public Locale getDefaultLanguageLocale() {
        if (defaultLanguageLocale != null) {
            return defaultLanguageLocale;
        } else {
            return country.getDefaultLanguageLocale();
        }
    }

    public void setDefaultLanguageLocale(Locale defaultLanguage) {
        this.defaultLanguageLocale = defaultLanguage;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    // city handling ---------------------------------------------------------------------------------------------------

    public City getCity(CityId id) {
        country.checkCities();
        return cities.get(id);
    }

    public Collection<City> getCities() {
        country.checkCities();
        return cities.values();
    }

    public void addCity(CityId id, City city) {
        city.setState(this);
        cities.put(id, city);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return (name != null ? name : id);
    }

    @Override
    public int hashCode() {
        return NullSafeComparator.hashCode(country) * 31 + name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final State that = (State) obj;
        if (!NullSafeComparator.equals(this.country, that.country)) {
            return false;
        }
        return name.equals(that.name);
    }

}
