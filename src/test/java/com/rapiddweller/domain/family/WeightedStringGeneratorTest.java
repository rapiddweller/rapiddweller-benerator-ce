/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */
package com.rapiddweller.domain.family;

import com.rapiddweller.benerator.test.GeneratorClassTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link WeightedStringGenerator}.<br/><br/>
 *
 * @since 2.1.0
 */
public class WeightedStringGeneratorTest extends GeneratorClassTest {
    public WeightedStringGeneratorTest() {
        super(WeightedStringGenerator.class);
    }
    @Test
    public void generateAndWeightTest() {
        String[] value = new String[] {"case1","case2","case3","case4","case5","case6"};
        double[] weight = new double[] {0.1,0.3,0.1,0.2,0.2,0.1};
        WeightedStringGenerator generator = new WeightedStringGenerator(value, weight);
        generator.init(context);
        List<String> result = new ArrayList<>();
        for(int i=0; i<100_000; i++) {
            result.add(generator.generate());
        }
        Assert.assertFalse(result.isEmpty());
        List<Long> resultCountEachString = Stream.of(value).map(e -> result.stream().filter(e1 -> e1.equalsIgnoreCase(e)).count()).collect(Collectors.toList());
        long sum = resultCountEachString.stream().mapToLong(Long::longValue).sum();
        List<Double> resultPercentEachString = resultCountEachString.stream().map(e -> e.doubleValue()/sum).collect(Collectors.toList());
        System.out.println(resultPercentEachString.toString());
        // deviation for these value is less than 5% for this check, number of value generated = 100_000
        double deviation = 0.05;
        for (int i=0; i<resultPercentEachString.size();i++) {
            Assert.assertTrue(resultPercentEachString.get(i)>=weight[i]-deviation && resultPercentEachString.get(i)<=weight[i]+deviation);
        }
    }

    @Test
    public void otherMethodTest() {
        String[] value = new String[] {"case1","case2","case3","case4","case5","case6"};
        double[] weight = new double[] {0.1,0.3,0.1,0.2,0.2,0.1};
        WeightedStringGenerator generator = new WeightedStringGenerator();
        generator.setWeight(weight);
        generator.setValue(value);
        assertEquals(weight, generator.getWeight());
        assertEquals(value, generator.getValue());
        assertEquals(String.class, generator.getGeneratedType());
        assertTrue(generator.isThreadSafe());
        assertTrue(generator.isParallelizable());
    }
}
