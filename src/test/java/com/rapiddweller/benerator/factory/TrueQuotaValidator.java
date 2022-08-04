/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.factory;

/**
 * Validates a quota of 'true' values in a {@link java.util.Collection} of {@link Boolean}s.<br/><br/>
 * Created: 02.08.2022 18:38:40
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class TrueQuotaValidator extends QuotaValidator {
	public TrueQuotaValidator(double expectedQuota, double tolerance) {
		super("true", (product) -> ((Boolean) product), expectedQuota, tolerance);
	}
}
