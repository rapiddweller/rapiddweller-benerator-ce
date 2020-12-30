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

package com.rapiddweller.domain.finance;

import com.rapiddweller.domain.address.CountryCode2Validator;
import com.rapiddweller.common.validator.bean.AbstractConstraintValidator;

import javax.validation.ConstraintValidatorContext;

/**
 * Validates IBANs.<br/>
 * <br/>
 * Created at 12.07.2008 14:58:39
 *
 * @author Volker Bergmann
 * @see "http://en.wikipedia.org/wiki/IBAN"
 * @see "http://de.wikipedia.org/wiki/International_Bank_Account_Number"
 * @since 0.5.4
 */
public class IBANValidator extends AbstractConstraintValidator<IBAN, String> {

    private final CountryCode2Validator countryCodeValidator = new CountryCode2Validator();

    @Override
    public boolean isValid(String iban, ConstraintValidatorContext context) {
        // check length
        if (iban == null || iban.length() < 15 || iban.length() > 32)
            return false;
        // check country code
        String countryCode = iban.substring(0, 2);
        if (!countryCodeValidator.valid(countryCode))
            return false;
        // check checksum
        int checksum = IBANUtil.checksum(iban);
        return (checksum == 1);
    }

}
