/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.model.data.Entity;

/**
 * Abstract implementation of the {@link EntityEncoder} interface.<br/><br/>
 * Created: 26.01.2022 18:30:39
 * @author Volker Bergmann
 * @since 3.0.0
 */
public abstract class AbstractEntityEncoder implements EntityEncoder {

	private String encoding;

	protected AbstractEntityEncoder() {
		this(Encodings.UTF_8);
	}

	protected AbstractEntityEncoder(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public String encodeEntityAsString(Entity entity) {
		return StringUtil.toString(encodeEntityAsBytes(entity), encoding);
	}

	@Override
	public byte[] encodeEntityAsBytes(Entity entity) {
		return StringUtil.toBytes(encodeEntityAsString(entity), encoding);
	}

}
