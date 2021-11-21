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

package com.rapiddweller.platform.contiperf;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.exception.ApplicationException;
import com.rapiddweller.contiperf.Invoker;

/**
 * {@link Consumer} implementation that calls a ContiPerf {@link PerfTrackingConsumer}.<br/><br/>
 * Created: 22.10.2009 16:17:14
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class PerfTrackingConsumer extends PerfTrackingWrapper implements Consumer {

  private String id;
  private Consumer target;

  // constructors ----------------------------------------------------------------------------------------------------

  public PerfTrackingConsumer() {
    this(null);
  }

  public PerfTrackingConsumer(Consumer target) {
    this(target, "Unnamed");
  }

  public PerfTrackingConsumer(Consumer target, String id) {
    this.id = id;
    this.target = target;
  }

  // properties ------------------------------------------------------------------------------------------------------

  public void setId(String id) {
    this.id = id;
  }

  public void setTarget(Consumer target) {
    this.target = target;
  }

  // Consumer interface implementation -------------------------------------------------------------------------------

  @Override
  public void startConsuming(ProductWrapper<?> wrapper) {
    try {
      getOrCreateTracker().invoke(new Object[] {wrapper});
    } catch (ApplicationException e) {
      throw e;
    } catch (Exception e) {
      throw BeneratorExceptionFactory.getInstance().operationFailed("Error consuming " + wrapper, e);
    }
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
    super.close();
    target.close();
  }

  // PerfTrackingWrapper callback method implementation --------------------------------------------------------------

  @Override
  protected Invoker getInvoker() {
    return new ConsumerInvoker(id, target);
  }

}
