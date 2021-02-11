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

import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.util.DataIteratorFromJavaIterator;
import com.rapiddweller.model.data.AbstractEntitySource;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.EntitySource;
import com.rapiddweller.model.data.PartDescriptor;

import java.util.List;

/**
 * {@link EntitySource} implementation for testing.<br/>
 * <br/>
 * Created: 11.03.2010 12:42:48
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class PersonSource extends AbstractEntitySource {

  @Override
  public DataIterator<Entity> iterator() {
    return new DataIteratorFromJavaIterator<>(createPersons().iterator(), Entity.class);
  }

  /**
   * Create persons list.
   *
   * @return the list
   */
  public List<Entity> createPersons() {
    return CollectionUtil.toList(createAlice(), createBob(provider()));
  }

  /**
   * Create alice entity.
   *
   * @return the entity
   */
  public Entity createAlice() {
    return new Entity(createPersonDescriptor(), "name", "Alice", "age", "23");
  }

  /**
   * Create bob entity.
   *
   * @param provider the provider
   * @return the entity
   */
  public Entity createBob(DescriptorProvider provider) {
    return new Entity(createPersonDescriptor(), "name", "Bob", "age", "34");
  }

  /**
   * Create person descriptor complex type descriptor.
   *
   * @return the complex type descriptor
   */
  public ComplexTypeDescriptor createPersonDescriptor() {
    return new ComplexTypeDescriptor(
        "Person", provider()).withComponent(new PartDescriptor("name", provider(), "string"))
        .withComponent(new PartDescriptor("age", provider(), "int"));
  }

  private DescriptorProvider provider() {
    return context.getLocalDescriptorProvider();
  }

}
