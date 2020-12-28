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

package com.rapiddweller.benerator.anno;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Mimics a {@link java.lang.reflect.Method} but allows overwriting annotations. 
 * This can be used to support annotations of external libraries by replacing 
 * them with their Benerator equivalents.<br/><br/>
 * Created: 05.04.2013 09:48:49
 * @since 0.8.2
 * @author Volker Bergmann
 */

public class MethodDescriptor {
	
	private final Method method;
	private Annotation[] annotations;
	private final Annotation[][] parameterAnnotations;

	public MethodDescriptor(Method method) {
		this.method = method;
		this.annotations = method.getAnnotations();
		this.parameterAnnotations = method.getParameterAnnotations();
	}

	public String getName() {
		return method.getName();
	}

	public Class<?> getDeclaringClass() {
		return method.getDeclaringClass();
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}
	
	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}

	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		for (Annotation a : annotations)
			if (annotationType.isAssignableFrom(a.getClass()))
				return (T) a;
		return null;
	}

	public Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}

	public Annotation[][] getParameterAnnotations() {
		return parameterAnnotations;
	}
	
	public void setParameterAnnotations(int paramIndex, Annotation[] annotations) {
		this.parameterAnnotations[paramIndex] = annotations;
	}
	
}
