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

import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;

import java.util.Random;

/**
 * Generates European Tax Identification Numbers (like the German 'Steueridentifikationsnummer').<br/>
 * <br/>
 * Created at 27.08.2008 00:20:11
 *
 * @author Volker Bergmann
 * @since 0.5.5
 */
public class TINGenerator extends ThreadSafeNonNullGenerator<String> {

    private final Random random = new Random();

    @Override
    public Class<String> getGeneratedType() {
        return String.class;
    }

    @Override
    public String generate() {
        char[] buffer = new char[10];
        // create a 10-digit string of which each digit is used at most once
        boolean[] digitsUsed = new boolean[10];
        int doubleCount = 0;
        for (int i = 0; i < 10; i++) {
            boolean done = true;
            do {
                int digit = random.nextInt(10);
                if (!digitsUsed[digit]) {
                    buffer[i] = (char) ('0' + digit);
                    digitsUsed[digit] = true;
                    done = true;
                } else if (doubleCount == 0) {
                    buffer[i] = (char) ('0' + digit);
                    doubleCount++;
                    done = true;
                } else
                    done = false;
            } while (!done);
        }
        // assure there is a double digit
        if (doubleCount == 0) {
            int i = random.nextInt(10);
            int j;
            do {
                j = random.nextInt(10);
            } while (j == i);
            buffer[j] = buffer[i];
        }
        // append checksum
        String s = String.valueOf(buffer);
        int checksum = TINValidator.calculateChecksum(s);
        return s + (char) (checksum + '0');
    }

}
