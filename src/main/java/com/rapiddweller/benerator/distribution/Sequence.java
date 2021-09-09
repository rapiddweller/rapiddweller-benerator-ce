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

package com.rapiddweller.benerator.distribution;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.common.Assert;
import com.rapiddweller.common.BeanUtil;

/**
 * Provides access to specific Sequence number Generators.<br/><br/>
 * Created: 11.09.2006 21:12:57
 * @author Volker Bergmann
 * @since 0.1
 */
public abstract class Sequence extends AbstractDistribution {

  // interface -------------------------------------------------------------------------------------------------------

  /** Creates a {@link Generator} which takes the elements created by the source generator
   *  and provides them in an order specified by this {@link Sequence} object.
   *  Depending on the sequence implementation, that generator may eg.
   *  <ol>
   *    <li>either fetch all products by the source at once, buffer and iterate through them</li>
   *    <li>or call the source generator on demand</li>
   *  </ol>
   *  This (default) implementation uses the first alternative but may be overwritten by child classes.
   *  @param source the generator which provides the source elements to be distributed
   *  @param unique specifies if the created generator must guarantee that no source element is repeated */
  @Override
  public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
    return new IndexBasedSampleGeneratorProxy<>(Assert.notNull(source, "source"), this, unique);
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return BeanUtil.toString(this);
  }

}
