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

import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Resettable;
import com.rapiddweller.common.ThreadAware;

/**
 * This is the basic Generator interface, the mother of all generators.<br/>
 * <br/>
 * <b>Generator States</b><br/>
 * A Generator may be in one of three states:
 * <ul>
 *   <li><i>constructing</i>: The generator is under construction.
 *       This may take several steps, since generators need to be JavaBeans.
 *       The generator may transit into the available state automatically
 *       or manually when the validate() method is called.</li>
 *   <li><i>available</i>: Generator construction is done and the generator is available.
 *       The user may loop the Generator via generate().</li>
 *   <li><i>unavailable</i>: The Generator may become unavailable automatically if its value space is depleted or
 *        manually when close() has been invoked. The Generator may be made <i>available</i> again by calling reset().
 *        When <i>unavailable</i>, the generator must be in a state in which it can be safely garbage collected.</li>
 * </ul>
 *
 * <b>Developer Notes:</b><br/>
 * When implementing a custom generator, you should make it a JavaBean:
 * <ul>
 *   <li>Implement a public default (no-arg) constructor</li>
 *   <li>make each relevant property configurable by a set-method</li>
 * </ul>
 * <br/>
 * Created: 07.06.2006 18:51:28
 * @since 0.1
 * @author Volker Bergmann
 */
public interface Generator<E> extends ThreadAware, Resettable, Closeable {

    /** Declares the type of the objects returned by the generate() method. */
    Class<E> getGeneratedType();

    void init(GeneratorContext context);
    
    boolean wasInitialized();
    
    /** Returns an instance of the generic type E, using the {@link ProductWrapper} 
     *  instance provided as argument. 
     *  The wrapper may wrap a null value as a regular generator product. 
     *  If the generator is not available (any more), it returns null instead of 
     *  the ProductWrapper instance. */
    ProductWrapper<E> generate(ProductWrapper<E> wrapper);

    /**
     * Closes the generator. After invocation the state is <i>unavailable</i>.
     */
    @Override
	void close();

}
