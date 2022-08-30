/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.factory;

import com.rapiddweller.common.Filter;

import java.util.Objects;

/**
 * Validates a null quota with tolerance.<br/><br/>
 * Created: 02.08.2022 18:35:56
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class NullQuotaValidator extends QuotaValidator {
	public NullQuotaValidator(double expectedQuota, double tolerance) {
		super("null", Objects::isNull, expectedQuota, tolerance);
	}
}
