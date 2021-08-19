/*
 * (c) Copyright 2021 by rapiddweller GmbH & Volker Bergmann. All rights reserved.
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

package com.rapiddweller.platform;

import com.rapiddweller.benerator.test.ModelTest;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.SimpleTypeDescriptor;
import org.junit.Before;

/**
 * Creates a 'person' type and three instances,
 * aliceEntity, bobEntity and charlyEntity, for testing.<br/><br/>
 * Created: 17.08.2021 06:52:45
 *
 * @author Volker Bergmann
 * @since 1.2.0
 */
public abstract class ABCTest extends ModelTest {

  protected ComplexTypeDescriptor personType;
  protected Entity aliceEntity, bobEntity, carEntity, charlyEntity;

  @Before
  public void setUpABC() {
    SimpleTypeDescriptor stringType = dataModel.getPrimitiveTypeDescriptor(String.class);
    SimpleTypeDescriptor intType = dataModel.getPrimitiveTypeDescriptor(int.class);
    ComplexTypeDescriptor carType = createComplexType("Car");
    carType.addComponent(createPart("name", stringType));
    personType = createComplexType("Person");
    personType.addComponent(createPart("name", stringType));
    personType.addComponent(createPart("age", intType));
    personType.addComponent(createPart("notes", stringType));
    personType.addComponent(createPart("car", carType));
    aliceEntity = createEntity("Person", "name", "Alice", "age", 23, "notes", "");
    bobEntity = createEntity("Person", "name", "Bob", "age", 34, "notes", null);
    carEntity = createEntity("Car", "maker", "Audi");
    charlyEntity = createEntity("Person", "name", "Charly", "age", 45, "notes", null, "car", carEntity);
  }

}
