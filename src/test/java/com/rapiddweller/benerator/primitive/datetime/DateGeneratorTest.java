package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.dataset.CompositeDatasetGenerator;
import com.rapiddweller.benerator.distribution.AttachedWeight;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.IndexBasedSampleGeneratorProxyTest;
import com.rapiddweller.benerator.distribution.sequence.RandomLongGenerator;
import com.rapiddweller.benerator.wrapper.AsNonNullGenerator;
import com.rapiddweller.benerator.wrapper.SingleSourceArrayGenerator;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DateGeneratorTest {
    @Test
    public void testConstructor() {
        DateGenerator actualDateGenerator = new DateGenerator();
        actualDateGenerator.setMax(new Date());
        actualDateGenerator.setMin(new Date());
        actualDateGenerator.setDistribution(new AttachedWeight<Object>());
        Class<?> expectedGeneratedType = Date.class;
        assertSame(expectedGeneratedType, actualDateGenerator.getGeneratedType());
        assertEquals("DateGenerator[AsNonNullGenerator[RandomLongGenerator]]", actualDateGenerator.toString());
    }

    @Test
    public void testConstructor2() {
        DateGenerator actualDateGenerator = new DateGenerator();
        assertEquals("DateGenerator[AsNonNullGenerator[RandomLongGenerator]]", actualDateGenerator.toString());
        assertTrue(actualDateGenerator.isThreadSafe());
        NonNullGenerator<Long> source = actualDateGenerator.getSource();
        assertEquals("AsNonNullGenerator[RandomLongGenerator]", source.toString());
        Generator<Long> source1 = ((AsNonNullGenerator<Long>) source).getSource();
        assertEquals("RandomLongGenerator", source1.toString());
        assertEquals(86400000L, ((RandomLongGenerator) source1).getGranularity().longValue());
    }

    @Test
    public void testConstructor3() {
        Date min = new Date(1L);
        DateGenerator actualDateGenerator = new DateGenerator(min, new Date(1L), 1L);

        assertEquals("DateGenerator[AsNonNullGenerator[RandomLongGenerator]]", actualDateGenerator.toString());
        assertTrue(actualDateGenerator.isThreadSafe());
        NonNullGenerator<Long> source = actualDateGenerator.getSource();
        assertEquals("AsNonNullGenerator[RandomLongGenerator]", source.toString());
        Generator<Long> source1 = ((AsNonNullGenerator<Long>) source).getSource();
        assertEquals("RandomLongGenerator", source1.toString());
        assertEquals(1L, ((RandomLongGenerator) source1).getMin().longValue());
        assertEquals(1L, ((RandomLongGenerator) source1).getMax().longValue());
        assertEquals(1L, ((RandomLongGenerator) source1).getGranularity().longValue());
    }

    @Test
    public void testConstructor4() {
        DateGenerator actualDateGenerator = new DateGenerator(null, new Date(1L), 1L);

        assertEquals("DateGenerator[AsNonNullGenerator[RandomLongGenerator]]", actualDateGenerator.toString());
        assertTrue(actualDateGenerator.isThreadSafe());
        NonNullGenerator<Long> source = actualDateGenerator.getSource();
        assertEquals("AsNonNullGenerator[RandomLongGenerator]", source.toString());
        Generator<Long> source1 = ((AsNonNullGenerator<Long>) source).getSource();
        assertEquals("RandomLongGenerator", source1.toString());
        assertEquals(Long.MIN_VALUE, ((RandomLongGenerator) source1).getMin().longValue());
        assertEquals(1L, ((RandomLongGenerator) source1).getMax().longValue());
        assertEquals(1L, ((RandomLongGenerator) source1).getGranularity().longValue());
    }

    @Test
    public void testConstructor5() {
        DateGenerator actualDateGenerator = new DateGenerator(new Date(1L), null, 1L);

        assertEquals("DateGenerator[AsNonNullGenerator[RandomLongGenerator]]", actualDateGenerator.toString());
        assertTrue(actualDateGenerator.isThreadSafe());
        NonNullGenerator<Long> source = actualDateGenerator.getSource();
        assertEquals("AsNonNullGenerator[RandomLongGenerator]", source.toString());
        Generator<Long> source1 = ((AsNonNullGenerator<Long>) source).getSource();
        assertEquals("RandomLongGenerator", source1.toString());
        assertEquals(1L, ((RandomLongGenerator) source1).getMin().longValue());
        assertEquals(1L, ((RandomLongGenerator) source1).getGranularity().longValue());
    }

    @Test
    public void testConstructor6() {
        Date min = new Date(1L);
        Date max = new Date(1L);
        Distribution distribution = mock(Distribution.class);
        CompositeDatasetGenerator<Object> source = new CompositeDatasetGenerator<Object>("Nesting", "Dataset Name", true);

        Class<?> componentType = Object.class;
        CompositeDatasetGenerator<Object> source1 = new CompositeDatasetGenerator<Object>("Nesting", "Dataset Name", true);

        Class<?> componentType1 = Object.class;
        CompositeDatasetGenerator<Object> source2 = new CompositeDatasetGenerator<Object>("Nesting", "Dataset Name", true);

        Class<?> componentType2 = Object.class;
        when(distribution.createNumberGenerator((Class<Number>) any(), (Number) any(), (Number) any(), (Number) any(),
                anyBoolean()))
                .thenReturn(new SingleSourceArrayGenerator(source, componentType,
                        new SingleSourceArrayGenerator(source1, componentType1, new SingleSourceArrayGenerator(source2,
                                componentType2, new SingleSourceArrayGenerator(null, Object.class, null)))));
        DateGenerator actualDateGenerator = new DateGenerator(min, max, 1L, distribution);

        assertEquals("DateGenerator[SingleSourceArrayGenerator[CompositeDatasetGenerator[WeightedGeneratorGenerator[]]]]",
                actualDateGenerator.toString());
        assertTrue(actualDateGenerator.isThreadSafe());
        verify(distribution).createNumberGenerator((Class<Number>) any(), (Number) any(), (Number) any(), (Number) any(),
                anyBoolean());
    }

    @Test
    public void testConstructor7() {
        Date min = new Date(1L);
        Date max = new Date(1L);
        Distribution distribution = mock(Distribution.class);
        CompositeDatasetGenerator<Object> source = new CompositeDatasetGenerator<Object>("Nesting", "Dataset Name", true);

        Class<?> componentType = Object.class;
        CompositeDatasetGenerator<Object> source1 = new CompositeDatasetGenerator<Object>("Nesting", "Dataset Name", true);

        Class<?> componentType1 = Object.class;
        CompositeDatasetGenerator<Object> source2 = new CompositeDatasetGenerator<Object>("Nesting", "Dataset Name", true);

        Class<?> componentType2 = Object.class;
        when(distribution.createNumberGenerator((Class<Number>) any(), (Number) any(), (Number) any(), (Number) any(),
                anyBoolean()))
                .thenReturn(new SingleSourceArrayGenerator(source, componentType,
                        new SingleSourceArrayGenerator(source1, componentType1, new SingleSourceArrayGenerator(source2,
                                componentType2, new SingleSourceArrayGenerator(null, Object.class, null)))));
        DateGenerator actualDateGenerator = new DateGenerator(min, max, 1L, distribution, true);

        assertEquals("DateGenerator[SingleSourceArrayGenerator[CompositeDatasetGenerator[WeightedGeneratorGenerator[]]]]",
                actualDateGenerator.toString());
        assertTrue(actualDateGenerator.isThreadSafe());
        verify(distribution).createNumberGenerator((Class<Number>) any(), (Number) any(), (Number) any(), (Number) any(),
                anyBoolean());
    }

    @Test
    public void testSetMax() {
        DateGenerator dateGenerator = new DateGenerator();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        dateGenerator.setMax(Date.from(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant()));
    }

    @Test
    public void testIsThreadSafe() {
        assertTrue((new DateGenerator()).isThreadSafe());
    }

    @Test
    public void testIsThreadSafe2() {
        Date min = new Date(1L);
        Date max = new Date(1L);
        assertFalse(
                (new DateGenerator(min, max, 1L, new IndexBasedSampleGeneratorProxyTest.TestDistribution())).isThreadSafe());
    }

    @Test
    public void testIsParallelizable() {
        assertTrue((new DateGenerator()).isParallelizable());
    }

    @Test
    public void testIsParallelizable2() {
        Date min = new Date(1L);
        Date max = new Date(1L);
        assertFalse((new DateGenerator(min, max, 1L, new IndexBasedSampleGeneratorProxyTest.TestDistribution()))
                .isParallelizable());
    }
}

