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

import com.rapiddweller.domain.person.Person;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a family person with additional family identification data:<br/>
 * - personID: long<br/>
 * - familyID: long<br/>
 * - familyRole: {@link FamilyRole}<br/>
 * - relations: contain a Map of related {@link FamilyPerson} with corresponding {@link RelationStatus} to that person<br/>
 */
public class FamilyPerson extends Person {
    private long personID;
    private long familyID;
    private FamilyRole familyRole;
    private Map<FamilyPerson, RelationStatus> relations;

    // Constructor -----------------------------------------------------------------------------------------------
    public FamilyPerson(Locale locale) {
        super(locale);
        this.relations = new HashMap<>();
    }

    public FamilyPerson() {
        this(Locale.getDefault());
    }

    // Getter/Setter -----------------------------------------------------------------------------------------------

    public long getPersonID() {
        return personID;
    }

    public void setPersonID(long personID) {
        this.personID = personID;
    }

    public long getFamilyID() {
        return familyID;
    }

    public void setFamilyID(long familyID) {
        this.familyID = familyID;
    }

    public FamilyRole getFamilyRole() {
        return familyRole;
    }

    public void setFamilyRole(FamilyRole familyRole) {
        this.familyRole = familyRole;
    }

    public Map<FamilyPerson, RelationStatus> getRelations() {
        return relations;
    }

    public void setRelations(Map<FamilyPerson, RelationStatus> relations) {
        this.relations = relations;
    }

    // Access FamilyPerson with condition -------------------------------------------------------------------------------------------------------

    public void addRelationStatusWithPerson(FamilyPerson relatedMember, RelationStatus relationStatus) {
        this.relations.put(relatedMember, relationStatus);
        //add revert relations to related member
        switch (relationStatus) {
            case DIVORCED:
                relatedMember.getRelations().put(this, RelationStatus.DIVORCED);
                break;
            case MARRIAGE:
                relatedMember.getRelations().put(this, RelationStatus.MARRIAGE);
                break;
            case BIOLOGICAL:
                relatedMember.getRelations().put(this, RelationStatus.CARETAKER);
                break;
            case CARETAKER:
                relatedMember.getRelations().put(this, RelationStatus.BIOLOGICAL);
                break;
            case ADOPTED:
                relatedMember.getRelations().put(this, RelationStatus.BENEFIT_PROVIDER);
                break;
            case BENEFIT_PROVIDER:
                relatedMember.getRelations().put(this, RelationStatus.ADOPTED);
                break;
            default:
                break;
        }
    }

    @Override
    public synchronized String toString() {
        return "FamilyPerson{" +
                "personID: " + personID +
                ", familyID: " + familyID +
                ", familyRole: " + familyRole +
                ", familyName: " + this.getFamilyName() +
                ", givenName: " + this.getGivenName() +
                ", gender: " + this.getGender() +
                ", age: " + this.getAge() +
                ", email: " + this.getEmail() +
                '}';
    }

}
