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

import java.util.List;

/**
 * Represents a natural happy family.<br/><br/>
 *
 * @since 0.1
 */
public class PersonNode {
    private long personID;
    private FamilyDataConstraint constraint;
    //Simple situation: 1 familyPerson -> 1 relation only
    private RelationData relation;

    public PersonNode() {
        this.constraint = new FamilyDataConstraint();
        this.relation = new RelationData();
    }

    public long getPersonID() {
        return personID;
    }

    public void setPersonID(long personID) {
        this.personID = personID;
    }

    public FamilyDataConstraint getConstraint() {
        return constraint;
    }

    public void setConstraint(FamilyDataConstraint constraint) {
        this.constraint = constraint;
    }

    public RelationData getRelation() {
        return relation;
    }

    public void setRelation(RelationData relation) {
        this.relation = relation;
    }

    @Override
    public synchronized String toString() {
        return "Member {personID: " + this.personID + " - " +
                "familyID: " + this.relation.getFamilyID() + " - " +
                "Role: " + this.relation.getFamilyRole() + " - " +
                "lastName: " + this.constraint.getConstraintLastName() + " - " +
                "age: " + this.constraint.getConstraintAge() + " - " +
                "gender: " + this.constraint.getConstraintGender() + "}"
                ;
    }
}
