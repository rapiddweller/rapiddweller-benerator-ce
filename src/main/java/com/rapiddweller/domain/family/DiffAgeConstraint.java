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

package com.rapiddweller.domain.family;

import com.rapiddweller.common.ConversionException;

import static com.rapiddweller.benerator.util.RandomUtil.randomInt;

public class DiffAgeConstraint implements Constraint<Integer>{
    private Integer maxDiffAge;
    private Integer minDiffAge;

    // Constructor ------------------------------------------------------------------------------------------------------
    //default constructor that config same value as source
    public DiffAgeConstraint() {
        this(0,0);
    }
    public DiffAgeConstraint(Integer minDiffAge, Integer maxDiffAge) {
        this.minDiffAge = minDiffAge;
        this.maxDiffAge = maxDiffAge;
    }
    // properties ------------------------------------------------------------------------------------------------------

    public void setMaxDiffAge(Integer maxDiffAge) {
        this.maxDiffAge = maxDiffAge;
    }

    public void setMinDiffAge(Integer minDiffAge) {
        this.minDiffAge = minDiffAge;
    }

    // implement Constraint Interface ----------------------------------------------------------------------------------
    @Override
    public Class<Integer> getSourceType() {
        return Integer.class;
    }

    @Override
    public Class<Integer> getTargetType() {
        return Integer.class;
    }

    @Override
    public Integer convert(Integer sourceValue) throws ConversionException {
        if(sourceValue + minDiffAge<=0) {
            if (sourceValue + maxDiffAge<=0) {
                return randomInt(1, sourceValue - 18);
            } else {
                return randomInt(1, sourceValue + maxDiffAge);
            }
        } else {
            return randomInt(sourceValue + minDiffAge, sourceValue + maxDiffAge);
        }
    }

    @Override
    public boolean isParallelizable() {
        return false;
    }

    @Override
    public boolean isThreadSafe() {
        return false;
    }
}
