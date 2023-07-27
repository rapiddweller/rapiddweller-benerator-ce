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

import java.util.HashMap;
import java.util.Map;

/**
 * Contain a Map of relation attribute name as {@link String} corresponding to Object implemented {@link Constraint}.<br/>
 * Can add or remove Constraint to reflect the constraint attributes from 1 entity to related entity.
 */
public class RelationConstraints {
    private final Map<String, Object> constraintFields;

    // Constructor ------------------------------------------------------------------------------------------------------

    public RelationConstraints() {
        this(new HashMap<>());
    }
    public RelationConstraints(Map<String, Object> constraintFields) {
        this.constraintFields = constraintFields;
    }

    // Getter -----------------------------------------------------------------------------------------------------------

    public Map<String, Object> getConstraintFields() {
        return constraintFields;
    }

    // Util -------------------------------------------------------------------------------------------------------------
    public void registerOrUpdateConstraint(String constraintName, Object constraint) {
        constraintFields.put(constraintName, constraint);
    }

    public void removeConstraintByName(String constraintName) {
        constraintFields.entrySet().removeIf(entry -> entry.getKey().equals(constraintName));
    }

    public Object getConstraintByName(String constraintName) {
        return constraintFields.get(constraintName);
    }

    public boolean isConstraintNameExist(String constraintName) {
        return constraintFields.containsKey(constraintName);
    }

}
