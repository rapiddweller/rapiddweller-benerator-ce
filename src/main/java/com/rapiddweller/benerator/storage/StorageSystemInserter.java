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

package com.rapiddweller.benerator.storage;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.benerator.composite.EntityTypeChanger;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;

/**
 * {@link Consumer} implementation that inserts entities into database tables.<br/><br/>
 * Created: 02.08.2010 19:38:56
 * @author Volker Bergmann
 * @since 0.6.3
 */
public class StorageSystemInserter extends StorageSystemConsumer {

  private final ComplexTypeDescriptor targetType;

  public StorageSystemInserter(StorageSystem system) {
    this(system, null);
  }

  public StorageSystemInserter(StorageSystem system, ComplexTypeDescriptor targetType) {
    super(system);
    this.targetType = targetType;
  }

  @Override
  public void startProductConsumption(Object object) {
    Entity entity = (Entity) object;
    if (targetType == null) {
      system.store(entity);
    } else {
      system.store(EntityTypeChanger.changeType(entity, targetType));
    }
  }

  public ComplexTypeDescriptor getTargetType() {
    return targetType;
  }
}
