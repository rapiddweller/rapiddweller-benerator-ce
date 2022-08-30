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

package com.rapiddweller.benerator.util;

import com.rapiddweller.common.Context;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.util.DataIteratorProxy;
import com.rapiddweller.common.Expression;

import java.util.Iterator;

/**
 * {@link Iterator} proxy which filters its source's output with a (boolean) filter expression.<br/><br/>
 * Created: 08.03.2011 11:51:51
 * @param <E> the type parameter
 * @author Volker Bergmann
 * @since 0.5.8
 */
public class FilterExIterator<E> extends DataIteratorProxy<E> {

  final Expression<Boolean> filterEx;
  final Context context;

  public FilterExIterator(DataIterator<E> source, Expression<Boolean> filterEx, Context context) {
    super(source);
    this.filterEx = filterEx;
    this.context = context;
  }

  // TODO akell need to eval with project data
  @Override
  public DataContainer<E> next(DataContainer<E> wrapper) {
    DataContainer<E> tmp;
    while ((tmp = super.next(wrapper)) != null) {
      context.set("_candidate", tmp.getData());
      Boolean evaluation = filterEx.evaluate(context);
      if (evaluation == null) {
        throw ExceptionFactory.getInstance().missingInfo("No filter expression");
      }
      if (evaluation) {
        return tmp;
      }
    }

    return null;
  }

}
