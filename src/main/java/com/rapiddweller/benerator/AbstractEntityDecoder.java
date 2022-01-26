/* (c) Copyright 2022 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator;

import com.rapiddweller.common.Encodings;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

import java.util.List;

/**
 * Abstract implementation of the {@link EntityDecoder} interface.<br/><br/>
 * Created: 26.01.2022 17:34:46
 * @author Volker Bergmann
 * @since 3.0.0
 */
public abstract class AbstractEntityDecoder implements EntityDecoder {

	protected final String encoding;

	protected AbstractEntityDecoder() {
		this(Encodings.UTF_8);
	}

	protected AbstractEntityDecoder(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public Entity decodeEntity(String code) {
		return decodeEntity(StringUtil.toBytes(code, encoding));
	}

	@Override
	public Entity decodeEntity(byte[] code) {
		return decodeEntity(StringUtil.toString(code, encoding));
	}

	@Override
	public List<Entity> decodeList(String code, ComplexTypeDescriptor descriptor) {
		return decodeList(StringUtil.toBytes(code, encoding), descriptor);
	}

	@Override
	public List<Entity> decodeList(byte[] code, ComplexTypeDescriptor descriptor) {
		return decodeList(StringUtil.toString(code, encoding), descriptor);
	}

}
