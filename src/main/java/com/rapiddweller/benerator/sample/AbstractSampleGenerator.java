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

package com.rapiddweller.benerator.sample;

import com.rapiddweller.benerator.util.ThreadSafeGenerator;

import java.util.Collection;

/**
 * Generates values from a list of samples.<br/><br/>
 * Created: 29.04.2008 07:30:08
 * @since 0.5.2
 * @author Volker Bergmann
 */
public abstract class AbstractSampleGenerator<E> extends ThreadSafeGenerator<E> {
	
	private final Class<E> generatedType;

    public AbstractSampleGenerator(Class<E> generatedType) {
		this.generatedType = generatedType;
	}

    @Override
	public Class<E> getGeneratedType() {
        return generatedType;
    }
    
    /** Adds values to the sample list */
    public <T extends E> void setValues(Iterable<T> values) {
        clear();
        if (values != null)
            for (T value : values)
                addValue(value);
    }

    /** Sets the sample list to the specified values */
    @SafeVarargs
    public final <T extends E> void setValues(T... values) {
        clear();
        if (values != null)
            for (E value : values)
                addValue(value);
    }

	/** Adds values to the sample list */
    @SafeVarargs
    public final <T extends E> void addValues(T... values) {
        if (values != null)
            for (T value : values)
                addValue(value);
    }

    /** Adds values to the sample list */
    public <T extends E> void addValues(Collection<T> values) {
        if (values != null)
            for (T value : values)
                addValue(value);
    }

    /** Adds a value to the sample list */
    public abstract <T extends E> void addValue(T value);

    /** Removes all values from the sample list */
    public abstract void clear();
    
    public abstract long getVariety();
    
}
