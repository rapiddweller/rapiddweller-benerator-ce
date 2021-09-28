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

package com.rapiddweller.benerator.consumer;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.ThreadAware;

/**
 * Parent class for {@link Consumer}s that serve as proxy to other Consumers.<br/><br/>
 * Created: 22.10.2009 16:18:07
 * @author Volker Bergmann
 * @since 0.6.0
 */
public abstract class ConsumerProxy implements Consumer, ThreadAware {

  protected Consumer target;

  protected ConsumerProxy(Consumer target) {
    this.target = target;
  }

  @Override
  public boolean isThreadSafe() {
    return (target instanceof ThreadAware && ((ThreadAware) target).isThreadSafe());
  }

  @Override
  public boolean isParallelizable() {
    return false;
  }

  public Consumer getTarget() {
    return target;
  }

  public void setTarget(Consumer target) {
    this.target = target;
  }

  @Override
  public void startConsuming(ProductWrapper<?> wrapper) {
    target.startConsuming(wrapper);
  }

  @Override
  public void finishConsuming(ProductWrapper<?> wrapper) {
    target.finishConsuming(wrapper);
  }

  @Override
  public void flush() {
    target.flush();
  }

  @Override
  public void close() {
    target.close();
  }

}
