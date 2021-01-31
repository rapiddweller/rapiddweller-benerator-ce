/*
 * Copyright (C) 2011-2015 Volker Bergmann (volker.bergmann@bergmann-it.de).
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rapiddweller.benerator.script.graalpy;

import com.rapiddweller.common.IOUtil;
import com.rapiddweller.benerator.script.GraalScript;
import com.rapiddweller.format.script.Script;
import com.rapiddweller.format.script.ScriptFactory;
import org.graalvm.polyglot.Engine;

import java.io.IOException;

/**
 *
 * Created at 30.12.2020
 *
 * @author Alexander Kell
 * @since 1.1.0
 */

public class GraalPyScriptFactory implements ScriptFactory {

	private static final String LANGUAGE = "python";
	private final Engine pythonEngine;

	public GraalPyScriptFactory() {
		this.pythonEngine = Engine.newBuilder().build();
	}


	@Override
	public Script parseText(String text) {
		return parseText(text, pythonEngine);
	}

	@Override
	public Script readFile(String uri) throws IOException {
		String text = IOUtil.getContentOfURI(uri);
		return parseText(text);
	}

	private static Script parseText(String text, Engine generalEngine) {
		if (!generalEngine.getLanguages().containsKey("python")) {
			throw new IllegalStateException(String.format("A language with id '%s' is not installed", LANGUAGE));
		}
		else
		{
			return new GraalScript(text, generalEngine, LANGUAGE);
		}
	}

}