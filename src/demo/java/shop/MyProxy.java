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

package shop;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.consumer.ConsumerProxy;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.IOUtil;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Simple {@link Consumer} proxy implementation that logs an entity before it is forwarded to the target consumer.<br/>
 * <br/>
 * Created: 26.08.2007 14:47:40
 */
public class MyProxy extends ConsumerProxy {

  private static Logger logger = LogManager.getLogger(MyProxy.class);

  /**
   * Instantiates a new My proxy.
   */
  public MyProxy() {
    this(null);
  }

  /**
   * Instantiates a new My proxy.
   *
   * @param target the target
   */
  public MyProxy(Consumer target) {
    super(target);
  }

  // Consumer interface ----------------------------------------------------------------------------------------------

  /**
   * Start consuming.
   *
   * @param wrapper the wrapper
   */
  @Override
  public void startConsuming(ProductWrapper<?> wrapper) {
    logger.info(wrapper.toString());
    target.startConsuming(wrapper);
  }

  /**
   * Finish consuming.
   *
   * @param wrapper the wrapper
   */
  @Override
  public void finishConsuming(ProductWrapper<?> wrapper) {
    logger.info(wrapper.toString());
    target.finishConsuming(wrapper);
  }

  /**
   * Flush.
   */
  @Override
  public void flush() {
    target.flush();
  }

  /**
   * Close.
   */
  @Override
  public void close() {
    IOUtil.close(target);
  }

}
