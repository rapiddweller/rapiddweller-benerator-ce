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

package com.rapiddweller.benerator.util;

/**
 * Provides utility methods for Luhn check digit calculation.<br/><br/>
 * Created: 18.10.2009 10:06:01
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class LuhnUtil {
	
	private LuhnUtil() { }
	
    /**
     * Calculates the last digit expected for a number that passes the Luhn test,
     * ignoring the last digit. This is useful for creating Luhn numbers.
     * The actual evaluation if a number passes the test is done by 
     * {@link #luhnValid(CharSequence)}.
     * @see "http://en.wikipedia.org/wiki/Luhn_algorithm"
     */
	public static char requiredCheckDigit(CharSequence number) {
		int sum = 0;
		int multiplier = 2;
		for (int i = number.length() - 2; i >= 0; i--) {
			int digit = number.charAt(i) - '0';
			int partialSum = digit * multiplier;
			sum += (partialSum > 9 ? 1 + (partialSum % 10) : partialSum);
			multiplier = 1 + (multiplier % 2);
		}
		return (char) ('0' + (10 - sum % 10) % 10); 
	}
	
    /**
     * Tests a number against the Luhn algorithm
     * @see #requiredCheckDigit(CharSequence)
     * @see "http://en.wikipedia.org/wiki/Luhn_algorithm"
     */
	public static boolean luhnValid(CharSequence number) {
		return (requiredCheckDigit(number) == number.charAt(number.length() - 1)); 
	}
	
}
