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
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * A {@link Consumer} that logs the entities that it receives at info level.<br/><br/>
 * Created: 26.08.2007 14:52:40
 * @author Volker Bergmann
 * @since 0.4.0
 */
public class LoggingConsumer extends AbstractConsumer {

  private static final Logger logger = LoggerFactory.getLogger(LoggingConsumer.class);

  private static final long UNLIMITED = -1;

  private final  long offset;
  private final long maxLogs;
  private long cursor;

  public LoggingConsumer() {
    this(0);
  }

  public LoggingConsumer(long offset) {
    this(offset, UNLIMITED);
  }

  public LoggingConsumer(long offset, long maxLogs) {
    this.offset = offset;
    this.maxLogs = maxLogs;
    this.cursor = 0;
  }

  @Override
  public void startProductConsumption(Object object) {
    if (shouldLog()) {
      logger.info("startConsuming({})", object);
    }
  }

  @Override
  public void finishProductConsumption(Object object) {
    if (shouldLog()) {
      logger.info("finishConsuming({})", object);
    }
    cursor++;
  }

  @Override
  public void flush() {
    logger.info("flush()");
  }

  @Override
  public void close() {
    logger.info("close()");
  }

  boolean shouldLog() {
    return cursor >= offset && (maxLogs == UNLIMITED || cursor < offset + maxLogs);
  }

}
