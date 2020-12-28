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

import com.rapiddweller.commons.StringUtil;

import java.math.BigDecimal;

/**
 * Provides utility methods for IBAN processing.<br/>
 * <br/>
 * Created at 12.07.2008 16:07:21
 *
 * @author Volker Bergmann
 * @since 0.5.4
 */
public class IBANUtil {

    private static final BigDecimal NINETYSEVEN = BigDecimal.valueOf(97);

    public static int checksum(String iban) {
        String tmp = (iban.substring(4) + iban.substring(0, 4)).toUpperCase();
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < tmp.length(); i++) {
            char c = tmp.charAt(i);
            if (c >= '0' && c <= '9')
                digits.append(c);
            else if (c >= 'A' && c <= 'Z') {
                int n = c - 'A' + 10;
                digits.append((char) ('0' + n / 10));
                digits.append((char) ('0' + (n % 10)));
            } else
                return -1;
        }
        BigDecimal n = new BigDecimal(digits.toString());
        return n.remainder(NINETYSEVEN).intValue();
    }

    public static String fixChecksum(String ibanTemplate) {
        int remainder = IBANUtil.checksum(ibanTemplate);
        String pp = StringUtil.padLeft(String.valueOf(98 - remainder), 2, '0');
        return ibanTemplate.substring(0, 2) + pp + ibanTemplate.substring(4);
    }
}
