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

import com.rapiddweller.commons.ConfigurationError;
import com.rapiddweller.commons.Encodings;
import com.rapiddweller.commons.IOUtil;
import com.rapiddweller.commons.Validator;
import com.rapiddweller.commons.validator.bean.AbstractConstraintValidator;

import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * {@link Validator} that verifies postal codes.<br/><br/>
 * Created: 28.08.2010 15:27:35
 *
 * @author Volker Bergmann
 * @since 0.6.4
 */
public class PostalCodeValidator extends AbstractConstraintValidator<PostalCode, String> {

    Pattern pattern;

    public PostalCodeValidator() {
        this(Country.getDefault().getIsoCode());
    }

    public PostalCodeValidator(String countryCode) {
        setCountry(countryCode);
    }

    @Override
    public void initialize(PostalCode params) {
        setCountry(params.country());
    }

    private void setCountry(String countryCode) {
        try {
            Map<String, String> formats = IOUtil.readProperties("/com/rapiddweller/domain/address/postalCodeFormat.properties", Encodings.UTF_8);
            pattern = Pattern.compile(formats.get(countryCode));
        } catch (IOException e) {
            throw new ConfigurationError("Error initializing " + getClass().getSimpleName() +
                    " with country code '" + countryCode + "'");
        }
    }

    @Override
    public boolean isValid(String candidate, ConstraintValidatorContext context) {
        return (candidate != null && pattern.matcher(candidate).matches());
    }

}
