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

package com.rapiddweller.domain.net;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.NonNullGenerator;
import com.rapiddweller.benerator.primitive.RegexStringGenerator;
import com.rapiddweller.benerator.wrapper.CompositeGenerator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;

import static com.rapiddweller.benerator.util.GeneratorUtil.generateNonNull;

/**
 * Creates an Internet domain name from random characters.<br/><br/>
 * Created at 23.04.2008 22:44:29
 *
 * @author Volker Bergmann
 * @since 0.5.2
 */
public class RandomDomainGenerator extends CompositeGenerator<String> implements NonNullGenerator<String> {

    private final RegexStringGenerator nameGenerator;
    private final TopLevelDomainGenerator tldGenerator;

    public RandomDomainGenerator() {
        super(String.class);
        this.nameGenerator = registerComponent(new RegexStringGenerator("[a-z]{4,12}"));
        this.tldGenerator = registerComponent(new TopLevelDomainGenerator());
    }

    @Override
    public synchronized void init(GeneratorContext context) {
        nameGenerator.init(context);
        tldGenerator.init(context);
        super.init(context);
    }

    @Override
    public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
        return wrapper.wrap(generate());
    }

    @Override
    public String generate() {
        return nameGenerator.generate() + '.' + generateNonNull(tldGenerator);
    }

}
