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

import com.rapiddweller.benerator.NonNullGenerator;

/**
 * {@link GeneratorWrapper} for {@link NonNullGenerator}s.<br/><br/>
 * Created: 27.07.2011 11:30:51
 * @author Volker Bergmann
 * @since 0.7.0
 */
public abstract class NonNullGeneratorWrapper<S, P> extends GeneratorWrapper<S, P> implements NonNullGenerator<P> {

  protected NonNullGeneratorWrapper(NonNullGenerator<S> source) {
    super(source);
  }

  @Override
  public NonNullGenerator<S> getSource() {
    return (NonNullGenerator<S>) super.getSource();
  }

  public void setSource(NonNullGenerator<S> source) {
    super.setSource(source);
  }

  protected final S generateFromNotNullSource() {
    return getSource().generate();
  }

  @Override
  public final ProductWrapper<P> generate(ProductWrapper<P> wrapper) {
    P result = generate();
    return (result != null ? wrapper.wrap(result) : null);
  }

}
