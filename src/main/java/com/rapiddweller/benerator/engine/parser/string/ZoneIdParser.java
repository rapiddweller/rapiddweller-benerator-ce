/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.engine.parser.string;

import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.parser.AbstractParser;

import java.time.ZoneId;

/**
 * Parses Java {@link java.time.ZoneId} names.<br/><br/>
 * Created: 05.07.2022 16:42:12
 * @author Volker Bergmann
 * @since 3.0.0
 */
public class ZoneIdParser extends AbstractParser<ZoneId> {

	public ZoneIdParser() {
		super("zone id expression");
	}

	@Override
	protected ZoneId parseImpl(String spec) {
		if (StringUtil.isEmpty(spec)) {
			return ZoneId.systemDefault();
		} else {
			return ZoneId.of(spec);
		}
	}

}