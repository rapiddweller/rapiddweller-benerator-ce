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

package com.rapiddweller.benerator.wrapper;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.distribution.Distribution;
import com.rapiddweller.benerator.distribution.SequenceManager;

/**
 * Creates arrays of random length filled with random bytes.
 * @author Volker Bergmann
 * @since 0.3.04
 */
public class ByteArrayGenerator extends SingleSourceArrayGenerator<Byte, byte[]> {
	
    // constructors ----------------------------------------------------------------------------------------------------

    public ByteArrayGenerator() {
        this(null, 0, 30);
    }

    public ByteArrayGenerator(Generator<Byte> source, int minLength, int maxLength) {
        this(source, minLength, maxLength, SequenceManager.RANDOM_SEQUENCE);
    }

    public ByteArrayGenerator(Generator<Byte> source, int minLength, int maxLength, Distribution distribution) {
        super(source, byte.class, minLength, maxLength, distribution);
    }

    @Override
    public ProductWrapper<byte[]> generate(ProductWrapper<byte[]> wrapper) {
        Integer length = generateCardinal();
        if (length == null)
        	return null;
        byte[] array = new byte[length.intValue()];
        for (int i = 0; i < length; i++)
            array[i] = generateFromSource().unwrap();
        return wrapper.wrap(array);
    }
    
}
