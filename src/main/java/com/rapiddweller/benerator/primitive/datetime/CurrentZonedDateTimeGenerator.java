/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.primitive.datetime;

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Generates java.util.Date objects that represent the current date and time.<br/><br/>
 * Created: 05.07.2022 11:06:48
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class CurrentZonedDateTimeGenerator extends ThreadSafeNonNullGenerator<ZonedDateTime> {

	private ZoneId zoneId;

	// constructors --------------------------------------------------------------------------------------------------

	public CurrentZonedDateTimeGenerator() {
		this(ZoneId.systemDefault().getId());
	}

	public CurrentZonedDateTimeGenerator(String zoneId) {
		setZoneId(zoneId);
	}

	// properties ----------------------------------------------------------------------------------------------------

	public String getZoneId() {
		return zoneId.getId();
	}

	public void setZoneId(String zoneId) {
		this.zoneId = ZoneId.of(zoneId);
	}

	@Override
	public Class<ZonedDateTime> getGeneratedType() {
		return ZonedDateTime.class;
	}

	// life cycle interface ------------------------------------------------------------------------------------------

	@Override
	public void init(GeneratorContext context) {
		super.init(context);
		if (zoneId == null) {
			setZoneId(ZoneId.systemDefault().getId());
		}
	}

	@Override
	public ZonedDateTime generate() {
		return ZonedDateTime.now(zoneId);
	}

	// java.lang.Object overrides ------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
