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

package com.rapiddweller.benerator.distribution.sequence;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.distribution.IndexBasedSampleGeneratorProxy;
import com.rapiddweller.benerator.distribution.Sequence;
import com.rapiddweller.benerator.wrapper.CloningEntityGenerator;
import com.rapiddweller.common.Assert;
import com.rapiddweller.model.data.Entity;

/**
 * Provides utility methods for {@link Sequence}s.<br/><br/>
 * Created: 22.09.2021 11:48:26
 * @author Volker Bergmann
 * @since 2.0.0
 */
public class SequenceUtil {

  private SequenceUtil() {
    // private constructor to prevent instantiation
  }

  /** Creates a {@link Generator} which fetches all the elements created by the source generator,
   *  puts them in a cache and serves the cached elements by generating index numbers with the
   *  associated number generator.
   *  @param sequence the {@link Sequence} to apply.
   *  @param source the generator which provides the source elements to be distributed. */
  @SuppressWarnings("unchecked")
  public static <T> Generator<T> applySequenceDetached(Sequence sequence, Generator<T> source, boolean unique) {
    Assert.notNull(source, "source");
    Generator<T> generator = new IndexBasedSampleGeneratorProxy<>(source, sequence, unique);
    if (Entity.class.equals(generator.getGeneratedType())) {
      // Attention: When applying a distribution to an entity data import (eg. serving as a seed for anonymized data),
      // then an imported entity could first get manipulated by a generator and then served to another
      // generator which assumes it is an original import, so the distribution needs to provide a clone.
      generator = (Generator<T>) new CloningEntityGenerator((Generator<Entity>) generator);
    }
    return generator;
  }

}
