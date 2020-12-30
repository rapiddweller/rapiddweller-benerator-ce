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

import static org.junit.Assert.*;

import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.context.DefaultContext;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.util.DataIteratorFromJavaIterator;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.DynamicExpression;
import org.junit.Test;

/**
 * Tests the {@link FilterExIterator}.<br/><br/>
 * Created: 08.03.2011 14:24:18
 * @since 0.5.8
 * @author Volker Bergmann
 */
public class FilterExIteratorTest {

	@Test
	public void test() {
		Context context = new DefaultContext();
		Expression<Boolean> expression = new IsThreeExpression();
		DataIterator<Integer> source = new DataIteratorFromJavaIterator<Integer>(
				CollectionUtil.toList(2, 3, 4).iterator(), Integer.class);
		FilterExIterator<Integer> iterator = new FilterExIterator<Integer>(source, expression, context);
		assertEquals(3, iterator.next(new DataContainer<Integer>()).getData().intValue());
		assertNull(iterator.next(new DataContainer<Integer>()));
	}
	
	class IsThreeExpression extends DynamicExpression<Boolean> {

		@Override
		public Boolean evaluate(Context context) {
			Integer candidateValue = (Integer) context.get("_candidate");
			return (candidateValue != null && candidateValue.intValue() == 3);
		}

	}

}
