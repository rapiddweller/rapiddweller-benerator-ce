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

package com.rapiddweller.domain.math;

import com.rapiddweller.benerator.primitive.number.RecurrenceRelationNumberGenerator;

/**
 * Generates numbers according to the <i>Padovan Sequence</i>.
 * It is defined recursively by
 * <ul>
 *   <li>P(0) = P(1) = P(2) = 1</li>
 *   <li>P(n) = P(n-2) + P(n-3)</li>
 * </ul>
 * <br/>
 * Created at 03.07.2009 13:22:41
 *
 * @author Volker Bergmann
 * @see PadovanSequence
 * @since 0.6.0
 */

public class PadovanLongGenerator extends RecurrenceRelationNumberGenerator<Long> {

    private final boolean unique;

    public PadovanLongGenerator(Long min, Long max, boolean unique) {
        super(Long.class, 3, min, max);
        this.unique = unique;
    }

    // RecurrenceRelationNumberGenerator interface implementation ------------------------------------------------------

    @Override
    protected Long a0(int n) {
        return 1L;
    }

    @Override
    protected Long aN() {
        return aN(-2) + aN(-3);
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected void resetMembers() {
        super.resetMembers();
        if (unique) {
            // take out the first two '1' elements
            generate();
            generate();
        }
    }

}
