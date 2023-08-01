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

/**
 * Constraint that convert method return role as {@link FamilyRole} corresponding with Source in a peer relation (age comparison).<br/>
 *
 */
public class PeerRoleConstraint extends AbstractRoleConstraint{

    // implement abstract method in AbstractRoleConstraint -------------------------------------------------------------
    @Override
    public FamilyRole convert(FamilyRole sourceValue) throws ConversionException {
        switch (sourceValue) {
            case FATHER:
                return FamilyRole.MOTHER;
            case MOTHER:
                return FamilyRole.FATHER;
            case MOTHER_DIVERSE:
                return FamilyRole.MOTHER_DIVERSE;
            case FATHER_DIVERSE:
                return FamilyRole.FATHER_DIVERSE;
            case GRANDFATHER:
                return FamilyRole.GRANDMOTHER;
            case GRANDMOTHER:
                return FamilyRole.GRANDFATHER;
            default:
                throw new IllegalArgumentException("This FamilyRole is not suitable for peer relation.");
        }

    }
}
