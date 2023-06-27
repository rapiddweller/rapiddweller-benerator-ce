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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.dataset.DatasetUtil;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.domain.address.Country;
import com.rapiddweller.domain.person.*;

import java.util.*;


/**
 * Generates {@link FamilyPerson} beans.<br/><br/>
 * Created: 2023
 *
 * @since 0.1
 */
public class FamilyPersonGenerator extends CompositeGenerator<FamilyPerson> implements NonNullGenerator<FamilyPerson> {
    private PersonGenerator personGenerator;
    private FamilyContextGenerator familyContextGenerator;
    private FamilyContext familyContext;
    private int cursor = 0;

    public FamilyPersonGenerator() {
        this(Country.getDefault().getIsoCode(), Locale.getDefault());
    }

    public FamilyPersonGenerator(String datasetName) {
        this(datasetName, DatasetUtil.defaultLanguageForRegion(datasetName));
    }

    public FamilyPersonGenerator(String datasetName, Locale locale) {
        super(FamilyPerson.class);
        logger.debug("Instantiating PersonGenerator with dataset '{}' and locale '{}'", datasetName, locale);
        this.personGenerator = registerComponent(new PersonGenerator(datasetName, locale));
        this.familyContextGenerator = registerComponent(new FamilyContextGenerator());
    }

    // properties ------------------------------------------------------------------------------------------------------

    public PersonGenerator getPersonGenerator() {
        return personGenerator;
    }

    public void setPersonGenerator(PersonGenerator personGenerator) {
        this.personGenerator = personGenerator;
    }

    public FamilyContextGenerator getFamilyContextGenerator() {
        return familyContextGenerator;
    }

    public void setFamilyContextGenerator(FamilyContextGenerator familyContextGenerator) {
        this.familyContextGenerator = familyContextGenerator;
    }

    public int getCursor() {
        return cursor;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
    public synchronized void init(GeneratorContext context) {
        personGenerator.init(context);
        familyContextGenerator.init(context);
        super.init(context);
    }

    @Override
    public ProductWrapper<FamilyPerson> generate(
            ProductWrapper<FamilyPerson> wrapper) {
        return wrapper.wrap(generate());
    }

    @Override
    public FamilyPerson generate() {
        FamilyPerson familyPerson = new FamilyPerson();
        //Extract family data from familyContextGenerator, using cursor
        PersonNode familyPersonContext = getPersonContext();
        int constraintAge = familyPersonContext.getConstraint().getConstraintAge();
        String constraintGender = familyPersonContext.getConstraint().getConstraintGender();
        String constraintLastName = familyPersonContext.getConstraint().getConstraintLastName();
        long personID = familyPersonContext.getPersonID();
        long familyID = familyPersonContext.getRelation().getFamilyID();
        String role = familyPersonContext.getRelation().getFamilyRole().getLabel();
        //set constraint into person
        this.personGenerator = new PersonGenerator();
        this.personGenerator.setMinAgeYears(constraintAge);
        this.personGenerator.setMaxAgeYears(constraintAge);
        double femaleQuota = constraintGender.equalsIgnoreCase("female") ? 1 : 0;
        this.personGenerator.setFemaleQuota(femaleQuota);
        this.personGenerator.init(context);
        Person person = this.personGenerator.generate();
        person.setFamilyName(constraintLastName);
        //set data into familyPerson
        familyPerson.setPerson(person);
        familyPerson.setFamilyID(familyID);
        familyPerson.setPersonID(personID);
        familyPerson.setRole(role);
        return familyPerson;
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private PersonNode getPersonContext() {
        if (cursor == 0) {
            familyContext = familyContextGenerator.generate();
            cursor++;
            return familyContext.getFamilyPersonList().get(cursor - 1);
        } else if (cursor == familyContext.getFamilyPersonList().size() - 1) {
            cursor = 0;
            return familyContext.getFamilyPersonList().get(familyContext.getFamilyPersonList().size() - 1);
        } else {
            cursor++;
            return familyContext.getFamilyPersonList().get(cursor - 1);
        }
    }
    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
