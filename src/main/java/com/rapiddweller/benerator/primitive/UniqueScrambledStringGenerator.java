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

package com.rapiddweller.benerator.primitive;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.wrapper.MultiGeneratorWrapper;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.commons.CharSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Generates unique strings of variable length.<br/>
 * <br/>
 * Created: 16.11.2007 11:56:15
 * @author Volker Bergmann
 */
public class UniqueScrambledStringGenerator extends MultiGeneratorWrapper<String, String> implements VarLengthStringGenerator {

    private int minLength;
    private int maxLength;
    private final Set<Character> chars;

    // constructors ----------------------------------------------------------------------------------------------------

    public UniqueScrambledStringGenerator() {
        this(new CharSet('A', 'Z').getSet(), 4, 8);
    }

    public UniqueScrambledStringGenerator(Set<Character> chars, int minLength, int maxLength) {
    	super(String.class);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.chars = chars;
    }
    
    // properties ------------------------------------------------------------------------------------------------------

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    // Generator interface ---------------------------------------------------------------------------------------------

    @Override
    public void init(GeneratorContext context) {
    	assertNotInitialized();
    	// create sub generators
        List<Generator<? extends String>> subGens = new ArrayList<>(maxLength - minLength + 1);
        for (int i = minLength; i <= maxLength; i++)
            subGens.add(new UniqueFixedLengthStringGenerator(chars, i, false));
        setSources(subGens);
        super.init(context);
    }

	@Override
	public String generateWithLength(int length) {
		ProductWrapper<String> wrapper = generateFromSource(length - minLength, getSourceWrapper());
		return (wrapper != null ? wrapper.unwrap() : null);
	}

	@Override
	public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
    	assertInitialized();
    	return generateFromRandomSource(wrapper);
    }

	@Override
	public String generate() {
		ProductWrapper<String> wrapper = generate(getResultWrapper());
		return (wrapper != null ? wrapper.unwrap() : null);
	}

    // java.lang.Object overrides --------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + minLength + "<=length<=" + maxLength + ", " +
                "charSet=" + chars + "]";
    }

}
