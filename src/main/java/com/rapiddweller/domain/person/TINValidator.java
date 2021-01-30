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

import com.rapiddweller.common.validator.bean.AbstractConstraintValidator;

import javax.validation.ConstraintValidatorContext;

/**
 * Validates European Tax Identification Numbers.<br/>
 * <br/>
 * Created at 27.08.2008 00:06:33
 *
 * @author Volker Bergmann
 * @since 0.5.5
 */
public class TINValidator extends AbstractConstraintValidator<TIN, String> {

    public static int calculateChecksum(String number) {
        int product = 0;
        for (int i = 0; i < 10; i++) {
            int sum = (number.charAt(i) - '0' + product) % 10;
            if (sum == 0) {
                sum = 10;
            }
            product = (sum * 2) % 11;
        }
        int checksum = 11 - product;
        if (checksum == 10) {
            checksum = 0;
        }
        return checksum;
    }

    @Override
    public boolean isValid(String number, ConstraintValidatorContext context) {
        if (number == null || number.length() != 11) {
            return false;
        }
        boolean[] digitUsed = new boolean[10];
        // assure that at most one digit is used twice
        int doubleCount = 0;
        for (int i = 0; i < 10; i++) {
            int digit = number.charAt(i) - '0';
            boolean used = digitUsed[digit];
            if (!used) {
                digitUsed[digit] = true;
            } else {
                doubleCount++;
                if (doubleCount == 2) {
                    return false;
                }
            }
        }
        // assure that there is exactly one digit used twice
        if (doubleCount == 0) {
            return false;
        }
        int checksum = calculateChecksum(number);
        return (number.charAt(10) == checksum + '0');
    }

}
