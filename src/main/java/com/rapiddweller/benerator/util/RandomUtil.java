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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.rapiddweller.common.StringUtil;
import com.rapiddweller.script.DatabeneScriptParser;
import com.rapiddweller.script.WeightedSample;

/**
 * Provides utility functions for generating numbers in an interval.<br/>
 * <br/>
 * Created: 03.09.2006 13:23:02
 * @since 0.1
 * @author Volker Bergmann
 */
public class RandomUtil {

    /** The basic random provider */
    private static final Random random = new Random();

    /** Generates a random long value in the range from min to max */
    public static long randomLong(long min, long max) {
        if (min > max)
            throw new IllegalArgumentException("min (" + min + ") > max (" + max + ")");
        long range = max - min + 1;
        long result;
        if (range != 0)
            result = min + (random.nextLong() % range);
        else
            result = random.nextLong();
        if (result < min)
            result += range;
        return result;
    }

    /** Generates a random int value in the range from min to max */
    public static int randomInt(int min, int max) {
        if (min > max)
            throw new IllegalArgumentException("min > max: " + min + " > " + max);
        int range = max - min + 1;
        int result;
        if (range != 0)
            result = min + (random.nextInt() % range);
        else
            result = random.nextInt();
        if (result < min)
            result += range;
        return result;
    }

    @SafeVarargs
    public static <T> T randomElement(T ... values) {
    	if (values.length == 0)
    		throw new IllegalArgumentException("Cannot choose random value from an empty array");
        return values[random.nextInt(values.length)];
    }
    
    public static <T> T randomElement(List<T> values) {
        return values.get(randomIndex(values));
    }

    public static int randomIndex(List<?> values) {
    	if (values.size() == 0)
    		throw new IllegalArgumentException("Cannot create random index for an empty array");
        return random.nextInt(values.size());
    }

	public static char randomDigit(int min) {
	    return (char) ('0' + min + random.nextInt(10 - min));
    }

	public static float randomProbability() {
	    return random.nextFloat();
    }

	public static Date randomDate(Date min, Date max) {
		return new Date(randomLong(min.getTime(), max.getTime()));
	}
	
	public static Object randomFromWeightLiteral(String literal) {
		if (StringUtil.isEmpty(literal))
			return null;
	    WeightedSample<?>[] samples = DatabeneScriptParser.parseWeightedLiteralList(literal);
        int sampleCount = samples.length;
        if (sampleCount == 1)
        	return samples[0];
        
        // normalize weights
        float[] probSum = new float[sampleCount];
        double sum = 0;
        for (int i = 0; i < sampleCount; i++) {
            double weight = samples[i].getWeight();
			if (weight < 0)
                throw new IllegalArgumentException("Negative weight in literal: " + literal);
            sum += weight;
            probSum[i] = (float) sum;
        }
        if (sum == 0)
        	return samples[randomInt(0, sampleCount)]; // for unweighted values, use simple random
        for (int i = 0; i < sampleCount; i++)
            probSum[i] /= (float) sum;
        
        // choose an item
        float probability = randomProbability();
        int i = Arrays.binarySearch(probSum, probability);
        if (i < 0)
            i = - i - 1;
        if (i >= probSum.length)
            i = probSum.length - 1;
        return samples[i].getValue();
	}
	
}
