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

package com.rapiddweller.benerator.dataset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rapiddweller.commons.Named;

/**
 * Defines a data set that may be nested.<br/><br/>
 * Created: 21.03.2008 12:31:13
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class Dataset implements Named {
	
	// attributes ------------------------------------------------------------------------------------------------------
	
    private final String id;
    private final String type;
    private final String name;
    private final Set<Dataset> parents;
    private final List<Dataset> subSets;
    
    // constructor -----------------------------------------------------------------------------------------------------

    Dataset(String type, String name) {
        if (type == null)
            throw new IllegalArgumentException("type is null");
        if (name == null)
            throw new IllegalArgumentException("name is null");
        this.id = type + ':' + name;
        this.type = type;
        this.name = name;
        this.parents = new HashSet<>();
        this.subSets = new ArrayList<>();
    }
    
    // interface -------------------------------------------------------------------------------------------------------
    
    public String getType() {
        return type;
    }

    @Override
	public String getName() {
        return name;
    }
    
	public void addParent(Dataset parent) {
		this.parents.add(parent);
	}
    
	public Set<Dataset> getParents() {
		return parents;
	}
	
    public void addSubSet(Dataset subSet) {
        subSets.add(subSet);
        subSet.addParent(this);
    }
    
    public List<Dataset> getSubSets() {
        return subSets;
    }
    
    public boolean isAtomic() {
    	return subSets.isEmpty();
    }
    
    public List<Dataset> allAtomicSubSets() {
    	List<Dataset> atomicSubSets = new ArrayList<>();
        for (Dataset subSet : subSets) {
            if (subSet.getSubSets().size() == 0)
                atomicSubSets.add(subSet);
            else
                atomicSubSets.addAll(subSet.allAtomicSubSets());
        }
        return atomicSubSets;
    }
    
	public boolean contains(String searchedChildName) {
        for (Dataset subSet : subSets) {
        	if (searchedChildName.equals(subSet.getName()))
        		return true;
        	return subSet.contains(searchedChildName);
        }
        return false;
	}
	


    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        final Dataset that = (Dataset) other;
        return this.id.equals(that.id);
    }

}
