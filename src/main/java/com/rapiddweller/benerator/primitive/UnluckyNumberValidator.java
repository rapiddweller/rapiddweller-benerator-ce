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

package com.rapiddweller.benerator.primitive;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;

import com.rapiddweller.common.CollectionUtil;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.validator.bean.AbstractConstraintValidator;
import com.rapiddweller.domain.address.Country;

/**
 * Checks if a String contains an 'unlucky' number like 13 in western cultures or 4 in east-asian cultures. 
 * See <a href="http://www.knowledgerush.com/kr/encyclopedia/Unlucky_number/">knowledgerush.com</a>,
 * <a href="http://vortex-japan.seesaa.net/article/113266312.html">vortex-japan.seesaa.net</a>,
 * <a href="http://en.wikipedia.org/wiki/Numerology">Wikipedia: Numerology</a> 
 * and <a href="http://en.wikipedia.org/wiki/Numbers_in_Chinese_culture">Wikipedia: Numbers in Chinese culture</a> <br/>
 * <br/>
 * Created at 03.07.2009 07:46:20
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class UnluckyNumberValidator extends AbstractConstraintValidator<UnluckyNumber, String> {
	
	private static final String UNLUCKY_CN = "4,14";
	private static final String LUCKY_CN = "2,8,9,13";

	private static final String UNLUCKY_JP = "4,9";
	private static final String LUCKY_JP = "8";
	
	private static final String UNLUCKY_WESTERN = "13,69,616,666";
	private static final String LUCKY_WESTERN = "7";
	
	private static final String UNLUCKY_IT = UNLUCKY_WESTERN + ",17";
	
	private Set<String> luckyNumbers;
	private Set<String> unluckyNumbers;
	private boolean luckyNumberRequired;
	private boolean endOnly;
	
    public UnluckyNumberValidator() {
    	this(false);
    }

    public UnluckyNumberValidator(boolean luckyNumberRequired) {
    	this.luckyNumberRequired = luckyNumberRequired;
    	this.endOnly = false;
	    Country country = Country.getDefault();
	    if (Country.CHINA.equals(country)) {
	    	luckyNumbers = parseNumberSpec(LUCKY_CN);
	    	unluckyNumbers = parseNumberSpec(UNLUCKY_CN);
	    } else if (Country.JAPAN.equals(country)) {
	    	luckyNumbers = parseNumberSpec(LUCKY_JP);
		    unluckyNumbers = parseNumberSpec(UNLUCKY_JP);
	    } else if (Country.ITALY.equals(country)) {
	    	luckyNumbers = parseNumberSpec(LUCKY_WESTERN);
		    unluckyNumbers = parseNumberSpec(UNLUCKY_IT);
	    } else {
	    	luckyNumbers = parseNumberSpec(LUCKY_WESTERN);
		    unluckyNumbers = parseNumberSpec(UNLUCKY_WESTERN);
	    }
    }
    
	public boolean isLuckyNumberRequired() {
    	return luckyNumberRequired;
    }

	public void setLuckyNumberRequired(boolean luckyNumberRequired) {
    	this.luckyNumberRequired = luckyNumberRequired;
    }
	
	public void setLuckyNumbers(String... luckyNumbers) {
		this.luckyNumbers = CollectionUtil.toSet(luckyNumbers);
	}

	public void setUnluckyNumbers(String... unluckyNumbers) {
		this.unluckyNumbers = CollectionUtil.toSet(unluckyNumbers);
	}

    @Override
    public void initialize(UnluckyNumber parameters) {
    	super.initialize(parameters);
	    setLuckyNumberRequired(parameters.luckyNumberRequired());
    }
    
    public boolean isEndOnly() {
    	return endOnly;
    }

	public void setEndOnly(boolean endOnly) {
    	this.endOnly = endOnly;
    }

	@Override
	public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
		if (StringUtil.isEmpty(value))
			return false;
		else if (endOnly)
			return !endsWithUnluckyNumber(value);
		else if (containsUnluckyNumber(value))
			return false;
		else if (luckyNumberRequired)
			return containsLuckyNumber(value);
		else
			return true;
    }

	private boolean containsLuckyNumber(String candidate) {
	    for (String test : luckyNumbers)
			if (candidate.contains(test))
				return true;
	    return false;
    }

	private boolean endsWithUnluckyNumber(String candidate) {
	    for (String test : unluckyNumbers)
			if (candidate.endsWith(test))
				return true;
	    return false;
    }

	private boolean containsUnluckyNumber(String candidate) {
	    for (String test : unluckyNumbers)
			if (candidate.contains(test))
				return true;
	    return false;
    }

    private static Set<String> parseNumberSpec(String spec) {
	    String[] tokens = StringUtil.tokenize(spec, ',');
        return new HashSet<>(Arrays.asList(tokens));
    }

}
