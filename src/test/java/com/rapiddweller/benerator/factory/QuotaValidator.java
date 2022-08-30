/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.factory;

import com.rapiddweller.common.Filter;
import com.rapiddweller.common.Validator;

import java.util.Collection;

/**
 * Validates an expected quota of filter matches in a collection with tolerance.<br/><br/>
 * Created: 02.08.2022 13:51:33
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class QuotaValidator implements Validator<Collection<Object>> {

	private final String name;
	private final Filter<Object> filter;
	private final double expectedQuota;
	private final double tolerance;
	private boolean verbose;

	public QuotaValidator(String name, Filter<Object> filter, double expectedQuota, double tolerance) {
		this.name = name;
		this.filter = filter;
		this.expectedQuota = expectedQuota;
		this.tolerance = tolerance;
		this.verbose = true;
	}

	@SuppressWarnings("unused")
	public boolean isVerbose() {
		return verbose;
	}

	@SuppressWarnings("unused")
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	@Override
	public boolean valid(Collection<Object> collection) {
		int matchCount = 0;
		for (Object product : collection) {
			if (filter.accept(product)) {
				matchCount++;
			}
		}
		double actualQuota = ((double) matchCount) / collection.size();
		boolean valid = Math.abs(actualQuota - expectedQuota) <= tolerance;
		if (verbose && !valid) {
			System.err.println("Actual " + name + " quota is " + actualQuota
				+ " vs. an expected quota of " + expectedQuota);
		}
		return valid;
	}

}
