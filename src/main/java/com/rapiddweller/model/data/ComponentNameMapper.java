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

package com.rapiddweller.model.data;

import com.rapiddweller.benerator.primitive.ValueMapper;
import com.rapiddweller.common.ConversionException;
import com.rapiddweller.common.converter.ThreadSafeConverter;

import java.util.Map;

/**
 * Converts the names of Entity components.<br/><br/>
 * Created: 22.02.2010 19:42:49
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class ComponentNameMapper extends ThreadSafeConverter<Entity, Entity> {

    private final ValueMapper nameMapper;

    public ComponentNameMapper() {
        this(null);
    }

    public ComponentNameMapper(String mappingSpec) {
        super(Entity.class, Entity.class);
        this.nameMapper = new ValueMapper(mappingSpec, true);
    }

    public void setMappings(String mappingSpec) {
        nameMapper.setMappings(mappingSpec);
    }

    @Override
    public Entity convert(Entity input) throws ConversionException {
        Entity output = new Entity(input.descriptor());
        for (Map.Entry<String, Object> component : input.getComponents().entrySet()) {
            String inCptName = component.getKey();
            String outCptName = (String) nameMapper.convert(inCptName);
            output.setComponent(outCptName, input.get(inCptName));
        }
        return output;
    }

}
