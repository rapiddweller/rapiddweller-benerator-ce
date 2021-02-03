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

package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.benerator.primitive.HiLoGenerator;
import com.rapiddweller.benerator.wrapper.WrapperFactory;

/**
 * * Creates Unique keys efficiently by connecting a database, retrieving a (unique) sequence value
 * and building sub keys of it.<br/>
 * <br/>
 * Created at 06.07.2009 07:57:08
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class QueryHiLoGenerator extends HiLoGenerator {

  /**
   * The constant DEFAULT_MAX_LO.
   */
  protected static final int DEFAULT_MAX_LO = 100;

  /**
   * Instantiates a new Query hi lo generator.
   *
   * @param selector the selector
   * @param source   the source
   */
  public QueryHiLoGenerator(String selector, StorageSystem source) {
    this(selector, source, DEFAULT_MAX_LO);
  }

  /**
   * Instantiates a new Query hi lo generator.
   *
   * @param selector the selector
   * @param source   the source
   * @param maxLo    the max lo
   */
  public QueryHiLoGenerator(String selector, StorageSystem source,
                            int maxLo) {
    super(WrapperFactory
            .asNonNullGenerator(new QueryLongGenerator(selector, source)),
        maxLo);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + '[' + hiGenerator + ',' + maxLo +
        ']';
  }

}
