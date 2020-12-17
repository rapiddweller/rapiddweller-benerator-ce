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

package com.rapiddweller.benerator.consumer;

import java.util.ArrayList;
import java.util.List;

import com.rapiddweller.benerator.Consumer;



/**
 * {@link Consumer} implementation that stores all consumed objects in a {@link List}.<br/><br/>
 * Created: 23.01.2011 08:17:14
 * @since 0.6.4
 * @author Volker Bergmann
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ListConsumer extends AbstractConsumer {
	
	private static int DEFAULT_CAPACITY = 10;
	
	private List consumedData;

	public ListConsumer() {
		this(DEFAULT_CAPACITY);
	}

	public ListConsumer(int capacity) {
		this.consumedData = new ArrayList(capacity);
	}

	@Override
	public void startProductConsumption(Object data) {
		this.consumedData.add(data);
	}

	public List getConsumedData() {
		return consumedData;
	}

	public void clear() {
		this.consumedData.clear();
	}
	
}
