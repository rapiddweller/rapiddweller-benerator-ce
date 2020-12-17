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

package com.rapiddweller.benerator.test;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.util.UnsafeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * Mock implementation of the {@link Generator} interface.<br/>
 * <br/>
 * Created at 29.12.2008 07:11:25
 * @since 0.5.7
 * @author Volker Bergmann
 */

public class GeneratorMock extends UnsafeGenerator<Integer> {
	
	public int value;

	public GeneratorMock() {
		this(1);
	}

	public GeneratorMock(int value) {
		this.value = value;
		latestInstance = this;
	}

	public void setValue(int value) {
		this.value = value;
	}

    @Override
	public Class<Integer> getGeneratedType() {
	    return Integer.class;
    }
	
	@Override
	public ProductWrapper<Integer> generate(ProductWrapper<Integer> wrapper) {
		return wrapper.wrap(value);
	}

	public static GeneratorMock latestInstance = null;

}
