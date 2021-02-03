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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.jdbacl.DBUtil;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * MBean implementation for monitoring Benerator.<br/><br/>
 * Created: 27.07.2010 21:15:28
 *
 * @author Volker Bergmann
 * @since 0.6.3
 */
public class BeneratorMonitor implements BeneratorMonitorMBean {

  /**
   * The constant INSTANCE.
   */
  public static final BeneratorMonitor INSTANCE;

  static {
    try {
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();
      INSTANCE = new BeneratorMonitor();
      ObjectName name = new ObjectName("benerator:service=monitor");
      server.registerMBean(INSTANCE, name);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * The Latest time stamp.
   */
  long latestTimeStamp;
  /**
   * The Latest generation count.
   */
  long latestGenerationCount = 0;
  /**
   * The Total generation count.
   */
  long totalGenerationCount = 0;
  /**
   * The Current throughput.
   */
  int currentThroughput;

  private BeneratorMonitor() {
    ControlThread controlThread = new ControlThread();
    controlThread.setDaemon(true);
    controlThread.start();
  }

  /**
   * Count generations.
   *
   * @param newGenerations the new generations
   */
  public synchronized void countGenerations(int newGenerations) {
    totalGenerationCount += newGenerations;
  }

  @Override
  public long getTotalGenerationCount() {
    return totalGenerationCount;
  }

  @Override
  public long getCurrentThroughput() {
    return currentThroughput;
  }

  /**
   * Sets total generation count.
   *
   * @param totalGenerationCount the total generation count
   */
  public void setTotalGenerationCount(long totalGenerationCount) {
    this.totalGenerationCount = totalGenerationCount;
  }

  @Override
  public int getOpenConnectionCount() {
    return DBUtil.getOpenConnectionCount();
  }

  @Override
  public int getOpenResultSetCount() {
    return DBUtil.getOpenResultSetCount();
  }

  @Override
  public int getOpenStatementCount() {
    return DBUtil.getOpenStatementCount();
  }

  @Override
  public int getOpenPreparedStatementCount() {
    return DBUtil.getOpenPreparedStatementCount();
  }

  @Override
  public void reset() {
    this.latestTimeStamp = 0;
    this.latestGenerationCount = 0;
    this.totalGenerationCount = 0;
    this.currentThroughput = 0;
  }

  /**
   * The type Control thread.
   */
  class ControlThread extends Thread {
    @Override
    public void run() {
      try {
        latestTimeStamp = System.nanoTime();
        //noinspection InfiniteLoopStatement
        while (true) {
          Thread.sleep(500);
          update();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    /**
     * Update.
     */
    public void update() {
      long currentGenerationCount = totalGenerationCount;
      long currentTime = System.nanoTime();
      currentThroughput = (int) ((currentGenerationCount - latestGenerationCount) * 1000000000 / (currentTime - latestTimeStamp));
      latestTimeStamp = currentTime;
      latestGenerationCount = currentGenerationCount;
    }
  }

}
