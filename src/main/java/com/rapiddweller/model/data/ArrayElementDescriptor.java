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

/**
 * Describes an array element.<br/><br/>
 * Created: 30.04.2010 10:08:31
 *
 * @author Volker Bergmann
 * @since 0.6.1
 */
public class ArrayElementDescriptor extends ComponentDescriptor {

  /**
   * Instantiates a new Array element descriptor.
   *
   * @param index     the index
   * @param provider  the provider
   * @param typeName  the type name
   * @param localType the local type
   */
  public ArrayElementDescriptor(int index, DescriptorProvider provider,
                                String typeName, TypeDescriptor localType) {
    super(String.valueOf(index), provider, typeName, localType);
  }

  /**
   * Instantiates a new Array element descriptor.
   *
   * @param index    the index
   * @param provider the provider
   * @param typeName the type name
   */
  public ArrayElementDescriptor(int index, DescriptorProvider provider,
                                String typeName) {
    super(String.valueOf(index), provider, typeName);
  }

  /**
   * Instantiates a new Array element descriptor.
   *
   * @param index     the index
   * @param provider  the provider
   * @param localType the local type
   */
  public ArrayElementDescriptor(int index, DescriptorProvider provider,
                                TypeDescriptor localType) {
    super(String.valueOf(index), provider, localType);
  }

  /**
   * Gets index.
   *
   * @return the index
   */
  public int getIndex() {
    return Integer.parseInt(getName());
  }

  /**
   * Sets index.
   *
   * @param index the index
   */
  public void setIndex(int index) {
    setName(String.valueOf(index));
  }

}
