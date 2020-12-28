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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorProxy;
import com.rapiddweller.benerator.wrapper.WrapperFactory;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.script.WeightedSample;

import java.util.List;

/**
 * Generates {@link Gender} objects.<br/>
 * <br/>
 * Created: 09.06.2006 21:45:23
 *
 * @author Volker Bergmann
 * @see Gender
 * @since 0.1
 */
public class GenderGenerator extends NonNullGeneratorProxy<Gender> {

    private double femaleQuota;

    // constructors ----------------------------------------------------------------------------------------------------

    public GenderGenerator() {
        this(0.5);
    }

    public GenderGenerator(double femaleQuota) {
        super(Gender.class);
        setFemaleQuota(femaleQuota);
    }

    // Generator interface implementation ------------------------------------------------------------------------------

    @Override
    public Class<Gender> getGeneratedType() {
        return Gender.class;
    }

    @Override
    public synchronized void init(GeneratorContext context) {
        assertNotInitialized();
        List<WeightedSample<Gender>> samples = CollectionUtil.toList(
                new WeightedSample<>(Gender.FEMALE, femaleQuota),
                new WeightedSample<>(Gender.MALE, 1 - femaleQuota)
        );
        Generator<Gender> source = context.getGeneratorFactory().createWeightedSampleGenerator(samples, Gender.class);
        setSource(WrapperFactory.asNonNullGenerator(source));
        super.init(context);
    }

    // properties ------------------------------------------------------------------------------------------------------

    public double getFemaleQuota() {
        return femaleQuota;
    }

    public void setFemaleQuota(double femaleQuota) {
        this.femaleQuota = femaleQuota;
    }

}
