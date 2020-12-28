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

import java.util.List;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.util.UnsafeNonNullGenerator;
import com.rapiddweller.commons.ArrayUtil;
import com.rapiddweller.commons.CollectionUtil;

/**
 * Generates value sequences derived from seed sequences.<br/>
 * <br/>
 * Created at 12.07.2009 09:04:43
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class SeedGenerator<E> extends UnsafeNonNullGenerator<E[]>{
	
	private final Class<E> atomType;
	private final Class<E[]> targetType;
	private final SeedManager<E> atomProvider;
	
	@SuppressWarnings("unchecked")
    public SeedGenerator(Class<E> atomType, int depth) {
		if (depth <= 0)
			throw new InvalidGeneratorSetupException("depth: " + depth);
		this.atomType = atomType;
	    this.targetType = ArrayUtil.arrayType(atomType);
	    this.atomProvider = new SeedManager<>(atomType, depth);
    }

	private E generate(List<E> predecessors) {
		E result;
		do {
			int preSize = predecessors.size();
			if (preSize < 1)
				throw new IllegalArgumentException("Predecessor list is empty");
			if (predecessors.get(0) != null)
				throw new IllegalArgumentException("Predecessor list must start with null");
			SeedManager<E> generator = atomProvider;
			for (int i = Math.max(0, preSize - getDepth() + 1); i < preSize; i++)
				generator = generator.getSuccessor(predecessors.get(i));
			result = generator.randomAtom();
		} while (predecessors.size() == 1 && result == null);
		return result;
	}

    public int getDepth() {
	    return atomProvider.getDepth();
    }
    
    @SafeVarargs
	public final void addSample(E... sequence) {
    	E[] atoms = wrapWithNulls(sequence);
    	for (int i = 0; i <= atoms.length - getDepth(); i++)
    		atomProvider.addSequence(i, atoms);
    }

    private E[] wrapWithNulls(E[] sequence) {
	    E[] result = ArrayUtil.newInstance(atomType, sequence.length + 2);
	    System.arraycopy(sequence, 0, result, 1, sequence.length);
	    return result;
    }

	@Override
	public Class<E[]> getGeneratedType() {
	    return targetType;
    }
	
	@Override
	public void init(GeneratorContext context) {
		assertNotInitialized();
		atomProvider.init();
		super.init(context);
	}
	
	@Override
    public E[] generate() {
		assertInitialized();
	    List<E> tmp = CollectionUtil.toList((E) null);
	    do {
	    	tmp.add(generate(tmp));
	    } while (CollectionUtil.lastElement(tmp) != null);
	    return CollectionUtil.extractArray(tmp, atomType, 1, tmp.size() - 1);
    }

    public void printState() {
	    printState("");
    }

    public void printState(String indent) {
	    System.out.println(this);
	    atomProvider.printState(indent + "+ ");
    }

}