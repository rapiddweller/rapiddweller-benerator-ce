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

package com.rapiddweller.benerator.factory;

import java.util.Date;

import com.rapiddweller.commons.TimeUtil;
import com.rapiddweller.commons.converter.NumberToNumberConverter;

/**
 * {@link DefaultsProvider} implementation which creates gentle defaults, trying to provoke as little errors as possible.<br/><br/>
 * Created: 15.07.2011 21:16:59
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class GentleDefaultsProvider implements DefaultsProvider {

	@Override
	public <T extends Number> T defaultMin(Class<T> numberType) {
		return NumberToNumberConverter.convert(1, numberType);
	}
	
	@Override
	public <T extends Number> T defaultMax(Class<T> numberType) {
		return NumberToNumberConverter.convert(9, numberType);
	}
	
	@Override
	public <T extends Number> T defaultGranularity(Class<T> numberType) {
		return NumberToNumberConverter.convert(1, numberType);
	}
	
	@Override
	public int defaultMinLength() {
		return 1;
	}
	
	@Override
	public Integer defaultMaxLength() {
		return 30;
	}
	
	@Override
	public boolean defaultNullable() {
		return false;
	}
	
	@Override
	public double defaultNullQuota() {
		return 1.;
	}

	@Override
	public Date defaultMinDate() {
		return TimeUtil.today();
	}

	@Override
	public Date defaultMaxDate() {
		return TimeUtil.addYears(TimeUtil.today(), 2);
	}

}