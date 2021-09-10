/*
 * (c) Copyright 2006-2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

/**
 * Parent class for all generation components that can connect to the life cycle of a different component
 * (thus defining a 'scope').<br/><br/>
 * Created: 03.04.2012 19:28:48
 * @author Volker Bergmann
 * @since 0.7.6
 */
public abstract class AbstractScopedLifeCycleHolder implements ScopedLifeCycleHolder {

  private String scope;
  private boolean resetNeeded;

  public AbstractScopedLifeCycleHolder(String scope) {
    this.scope = scope;
    setResetNeeded(false);
  }

  @Override
  public String getScope() {
    return scope;
  }

  @Override
  public void setScope(String scope) {
    this.scope = scope;
  }

  @Override
  public boolean isResetNeeded() {
    return resetNeeded;
  }

  @Override
  public void setResetNeeded(boolean resetNeeded) {
    this.resetNeeded = resetNeeded;
  }

  @Override
  public void resetIfNeeded() {
    if (this.resetNeeded) {
      reset();
      setResetNeeded(false);
    }
  }

}
