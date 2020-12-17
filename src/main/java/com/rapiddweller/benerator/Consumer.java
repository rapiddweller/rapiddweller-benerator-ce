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

package com.rapiddweller.benerator;

import java.io.Closeable;
import java.io.Flushable;

import com.rapiddweller.benerator.wrapper.ProductWrapper;

/**
 * Abstraction of an object that consumes (possibly larger quantities of) objects.
 * Consumation is a two-step process: For each object A to be consumed, Benerator 
 * first calls {@link #startConsuming(ProductWrapper)}, then 
 * {@link #finishConsuming(ProductWrapper)} with a wrapper of this object: 
 * <pre>
 * 	startConsuming(A);
 * 	finishConsuming(A);
 * </pre>
 * If an object A has a 'sub object' B, (defined via nested a &gt;generate&lt; 
 * statement), Benerator represents the recursion by the following invocation sequence:
 * <pre>
 * 	startConsuming(A);
 * 	startConsuming(B);
 * 	finishConsuming(B);
 * 	finishConsuming(A);
 * </pre>
 * <br/>
 * Created: 01.02.2008 16:15:09
 * @since 0.4.0
 * @author Volker Bergmann
 */
public interface Consumer extends Flushable, Closeable {
	
	/** Starts consumption of an object. For invocation details see the class documentation. */
    void startConsuming(ProductWrapper<?> wrapper);
    
	/** Starts consumption of an object. For invocation details see the class documentation. */
    void finishConsuming(ProductWrapper<?> wrapper);
    
    /** Is called by Benerator for advising the Consumer to finish processing of the objects 
     *  consumed so far. In Benerator descriptor files, the flushing behavior is controlled 
     *  by the <code>pageSize</code> attribute. */
    @Override
	void flush();
    
    /** When called, the implementor has to close and free all resources. 
     * It will not receive any more calls. */
    @Override
	void close();
    
}
