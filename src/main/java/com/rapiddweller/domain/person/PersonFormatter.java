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

package com.rapiddweller.domain.person;

import com.rapiddweller.common.*;
import com.rapiddweller.common.converter.ThreadSafeConverter;

import java.util.Locale;
import java.util.Set;

/**
 * Formats {@link Person} objects.<br/><br/>
 * Created: 22.02.2010 12:41:37
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public abstract class PersonFormatter extends ThreadSafeConverter<Person, String> {

    public static final PersonFormatter WESTERN = new Western();
    public static final PersonFormatter EASTERN = new Eastern();
    private static final Set<Locale> EASTERN_LOCALES = CollectionUtil.toSet(
            Locales.CHINESE, Locales.JAPANESE, Locales.KOREAN, Locales.THAI, Locales.VIETNAMESE
    );

    public PersonFormatter() {
        super(Person.class, String.class);
    }

    public static PersonFormatter getInstance(Locale locale) {
        return (EASTERN_LOCALES.contains(LocaleUtil.language(locale)) ? EASTERN : WESTERN);
    }

    @Override
    public String convert(Person person) throws ConversionException {
        return format(person);
    }

    public abstract String format(Person person);

    protected void appendSeparated(String part, StringBuilder builder) {
        if (!StringUtil.isEmpty(part)) {
            if (builder.length() > 0)
                builder.append(' ');
            builder.append(part);
        }
    }

    static class Western extends PersonFormatter {

        @Override
        public String format(Person person) {
            StringBuilder builder = new StringBuilder();
            appendSeparated(person.getSalutation(), builder);
            appendSeparated(person.getAcademicTitle(), builder);
            appendSeparated(person.getNobilityTitle(), builder);
            appendSeparated(person.getGivenName(), builder);
            appendSeparated(person.getFamilyName(), builder);
            return builder.toString();
        }
    }

    static class Eastern extends PersonFormatter {

        @Override
        public String format(Person person) {
            StringBuilder builder = new StringBuilder();
            appendSeparated(person.getSalutation(), builder);
            appendSeparated(person.getAcademicTitle(), builder);
            appendSeparated(person.getNobilityTitle(), builder);
            appendSeparated(person.getFamilyName(), builder);
            appendSeparated(person.getGivenName(), builder);
            return builder.toString();
        }
    }

}
