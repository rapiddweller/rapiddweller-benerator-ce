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

package com.rapiddweller.model.data;

import com.rapiddweller.script.Expression;

/**
 * Descriptor for attributes<br/><br/>
 * Created: 30.06.2007 07:29:43
 *
 * @author Volker Bergmann
 * @since 0.2
 */
public class PartDescriptor extends ComponentDescriptor {

  /**
   * Instantiates a new Part descriptor.
   *
   * @param name     the name
   * @param provider the provider
   */
  public PartDescriptor(String name, DescriptorProvider provider) {
    this(name, provider, (TypeDescriptor) null);
  }

  /**
   * Instantiates a new Part descriptor.
   *
   * @param name     the name
   * @param provider the provider
   * @param type     the type
   */
  public PartDescriptor(String name, DescriptorProvider provider,
                        String type) {
    this(name, provider, type, null, null, null);
  }

  /**
   * Instantiates a new Part descriptor.
   *
   * @param name      the name
   * @param provider  the provider
   * @param localType the local type
   */
  public PartDescriptor(String name, DescriptorProvider provider,
                        TypeDescriptor localType) {
    this(name, provider, localType, null, null);
  }

  /**
   * Instantiates a new Part descriptor.
   *
   * @param name      the name
   * @param provider  the provider
   * @param localType the local type
   * @param minCount  the min count
   * @param maxCount  the max count
   */
  public PartDescriptor(String name, DescriptorProvider provider,
                        TypeDescriptor localType, Expression<Long> minCount,
                        Expression<Long> maxCount) {
    this(name, provider, null, localType, minCount, maxCount);
  }

  /**
   * Instantiates a new Part descriptor.
   *
   * @param name      the name
   * @param provider  the provider
   * @param type      the type
   * @param localType the local type
   * @param minCount  the min count
   * @param maxCount  the max count
   */
  public PartDescriptor(String name, DescriptorProvider provider, String type,
                        TypeDescriptor localType, Expression<Long> minCount,
                        Expression<Long> maxCount) {
    super(name, provider, type, localType);
    setMinCount(minCount);
    setMaxCount(maxCount);
  }

}
