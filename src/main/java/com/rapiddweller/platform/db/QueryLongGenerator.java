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
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.converter.ConverterManager;

/**
 * Generates {@link Long} values based on a database query.<br/><br/>
 * Created at 06.07.2009 07:58:45
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class QueryLongGenerator extends QueryGenerator<Long> {

  @SuppressWarnings("rawtypes")
  private Converter converter;

  public QueryLongGenerator() {
    this(null, null);
  }

  public QueryLongGenerator(String selector, StorageSystem source) {
    super(selector, source, true);
  }

  @Override
  public Class<Long> getGeneratedType() {
    return Long.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ProductWrapper<Long> generate(ProductWrapper<Long> wrapper) {
    Object input = super.generateFromSource();
    if (converter == null) {
      converter = ConverterManager.getInstance()
          .createConverter(input.getClass(), Long.class);
    }
    return wrapper.wrap((Long) converter.convert(input));
  }

}
