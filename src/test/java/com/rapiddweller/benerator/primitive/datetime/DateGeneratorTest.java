package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.dataset.CompositeDatasetGenerator;
import com.rapiddweller.benerator.distribution.AttachedWeight;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.IndexBasedSampleGeneratorProxyTest;
import com.rapiddweller.benerator.distribution.sequence.RandomLongGenerator;
import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.wrapper.AsNonNullGenerator;
import com.rapiddweller.benerator.wrapper.SingleSourceArrayGenerator;
import com.rapiddweller.common.Period;
import com.rapiddweller.common.TimeUtil;
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
        actualDateGenerator.setDistribution(new AttachedWeight<>());
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testConstructor6() {
        Date min = new Date(1L);
        Date max = new Date(1L);
        Distribution distribution = mock(Distribution.class);
        CompositeDatasetGenerator<Object> source = new CompositeDatasetGenerator<>("Nesting", "Dataset Name", true);

        Class<?> componentType = Object.class;
        CompositeDatasetGenerator<Object> source1 = new CompositeDatasetGenerator<>("Nesting", "Dataset Name", true);

        Class<?> componentType1 = Object.class;
        CompositeDatasetGenerator<Object> source2 = new CompositeDatasetGenerator<>("Nesting", "Dataset Name", true);

        Class<?> componentType2 = Object.class;
        when(distribution.createNumberGenerator(any(), any(), any(), any(), anyBoolean()))
            .thenReturn(new SingleSourceArrayGenerator(source, componentType,
                            new SingleSourceArrayGenerator(source1, componentType1,
                                new SingleSourceArrayGenerator(source2, componentType2,
                                    new SingleSourceArrayGenerator(null, Object.class, null)))));
        DateGenerator actualDateGenerator = new DateGenerator(min, max, 1L, distribution);

        assertEquals("DateGenerator[SingleSourceArrayGenerator[CompositeDatasetGenerator[WeightedGeneratorGenerator[]]]]",
                actualDateGenerator.toString());
        assertTrue(actualDateGenerator.isThreadSafe());
        verify(distribution).createNumberGenerator(any(), any(), any(), any(),
                anyBoolean());
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testConstructor7() {
        Date min = new Date(1L);
        Date max = new Date(1L);
        Distribution distribution = mock(Distribution.class);
        CompositeDatasetGenerator<Object> source = new CompositeDatasetGenerator<>("Nesting", "Dataset Name", true);

        Class<?> componentType = Object.class;
        CompositeDatasetGenerator<Object> source1 = new CompositeDatasetGenerator<>("Nesting", "Dataset Name", true);

        Class<?> componentType1 = Object.class;
        CompositeDatasetGenerator<Object> source2 = new CompositeDatasetGenerator<>("Nesting", "Dataset Name", true);

        Class<?> componentType2 = Object.class;
        when(distribution.createNumberGenerator(any(), any(), any(), any(),
                anyBoolean()))
                .thenReturn(
                    new SingleSourceArrayGenerator(source, componentType,
                        new SingleSourceArrayGenerator(source1, componentType1,
                            new SingleSourceArrayGenerator(source2, componentType2,
                                new SingleSourceArrayGenerator(null, Object.class, null)))));
        DateGenerator actualDateGenerator = new DateGenerator(min, max, 1L, distribution, true);

        assertEquals("DateGenerator[SingleSourceArrayGenerator[CompositeDatasetGenerator[WeightedGeneratorGenerator[]]]]",
                actualDateGenerator.toString());
        assertTrue(actualDateGenerator.isThreadSafe());
        verify(distribution).createNumberGenerator(any(), any(), any(), any(),
                anyBoolean());
    }

    @Test
    public void testSetMax() {
        DateGenerator dateGenerator = new DateGenerator();
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        Date maxDate = Date.from(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant());
        dateGenerator.setMax(maxDate);
        DefaultBeneratorContext context = new DefaultBeneratorContext();
        dateGenerator.init(context);
        for (int i = 0; i < 1000; i++) {
            assertTrue(dateGenerator.generate().compareTo(maxDate) <= 0);
        }
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

    @Test
    public void test() {
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0, 1), Period.DAY.getMillis());
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0, 10), Period.DAY.getMillis());
        check(TimeUtil.date(1970, 6, 1), TimeUtil.date(1970, 6, 10), Period.DAY.getMillis());
        check(TimeUtil.date(1970, 0, 1), TimeUtil.date(1970, 0, 3), Period.HOUR.getMillis());
        check(TimeUtil.date(1970, 6, 1), TimeUtil.date(1970, 6, 3), Period.HOUR.getMillis());
        check(TimeUtil.date(1970, 6, 1), TimeUtil.date(1970, 6, 3), Period.MILLISECOND.getMillis());
    }

    private void check(Date min, Date max, long granularity) {
        DateGenerator generator = new DateGenerator(min, max, granularity);
        DefaultBeneratorContext context = new DefaultBeneratorContext();
        generator.init(context);
        for (int i = 0; i < 10000; i++) {
            Date date = generator.generate();
            assertNotNull(date);
            assertFalse(date.before(min));
            assertFalse(date.after(max));
            long time = date.getTime();
            long time0 = min.getTime();
            assertEquals(0, (time - time0) % granularity);
        }
    }

}

