/*
 * (c) Copyright 2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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
import com.rapiddweller.common.ProgrammerError;
import com.rapiddweller.model.data.Entity;

/**
 * Wraps a {@link com.rapiddweller.benerator.Generator} and clones its products.
 * This is useful for wrapping Generators which iterate several times over
 * the same base dataset.<br/><br/>
 * Created: 22.09.2021 10:58:27
 * @author Volker Bergmann
 * @since 1.2.0
 */
public class CloningEntityGenerator extends GeneratorProxy<Entity> {

  public CloningEntityGenerator(Generator<Entity> source) {
    super(source);
  }

  @Override
  public ProductWrapper<Entity> generate(ProductWrapper<Entity> wrapper) {
    wrapper = super.generate(wrapper);
    if (wrapper == null) {
      return null;
    }
    try {
      return wrapper.wrap(wrapper.unwrap().clone());
    } catch (CloneNotSupportedException e) {
      throw new ProgrammerError("Error cloning an entity", e);
    }
  }
}