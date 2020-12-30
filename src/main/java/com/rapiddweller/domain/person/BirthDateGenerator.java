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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.distribution.SequenceManager;
import com.rapiddweller.benerator.primitive.datetime.DateGenerator;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorProxy;
import com.rapiddweller.common.Period;
import com.rapiddweller.common.TimeUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Creates {@link Date} objects for a person's birth day.<br/>
 * <br/>
 * Created: 13.06.2006 07:15:03
 *
 * @author Volker Bergmann
 * @since 0.1
 */
public class BirthDateGenerator extends NonNullGeneratorProxy<Date> {

    private int minAgeYears;
    private int maxAgeYears;

    public BirthDateGenerator() {
        this(18, 80);
    }

    public BirthDateGenerator(int minAgeYears, int maxAgeYears) {
        super(Date.class);
        this.minAgeYears = minAgeYears;
        this.maxAgeYears = maxAgeYears;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public int getMinAgeYears() {
        return minAgeYears;
    }

    public void setMinAgeYears(int minAgeYears) {
        this.minAgeYears = minAgeYears;
    }

    public int getMaxAgeYears() {
        return maxAgeYears;
    }

    public void setMaxAgeYears(int maxAgeYears) {
        this.maxAgeYears = maxAgeYears;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
    public synchronized void init(GeneratorContext context) {
        Calendar min = TimeUtil.calendar(TimeUtil.tomorrow());
        min.add(Calendar.YEAR, -maxAgeYears - 1);
        Calendar max = TimeUtil.calendar(TimeUtil.today());
        max.add(Calendar.YEAR, -minAgeYears);
        setSource(new DateGenerator(min.getTime(), max.getTime(), Period.DAY.getMillis(), SequenceManager.RANDOM_SEQUENCE));
        super.init(context);
    }

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[minAgeYears=" + minAgeYears + ", maxAgeYears=" + maxAgeYears + ']';
    }

}
