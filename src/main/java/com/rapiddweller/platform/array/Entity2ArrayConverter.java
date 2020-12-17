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

package com.rapiddweller.platform.array;

import com.rapiddweller.model.data.Entity;
import com.rapiddweller.commons.ArrayFormat;
import com.rapiddweller.commons.CollectionUtil;
import com.rapiddweller.commons.converter.ThreadSafeConverter;

/**
 * Converts an Entity's features values to an array of objects.<br/>
 * <br/>
 * Created: 26.08.2007 12:27:45
 *
 * @author Volker Bergmann
 */
public class Entity2ArrayConverter extends ThreadSafeConverter<Entity, Object[]> {

    private String[] featureNames;

    public Entity2ArrayConverter() {
        this(null);
    }

    public Entity2ArrayConverter(String[] featureNames) {
        super(Entity.class, Object[].class);
        this.featureNames = featureNames;
    }

    @Override
    public Object[] convert(Entity entity) {
        if (entity == null)
            return null;
        if (featureNames == null)
            initFeatureNamesFromTemplate(entity);
        Object[] result = new Object[featureNames.length];
        for (int i = 0; i < featureNames.length; i++)
            result[i] = entity.getComponent(featureNames[i]);
        return result;
    }

    private void initFeatureNamesFromTemplate(Entity entity) {
        this.featureNames = CollectionUtil.toArray(entity.getComponents().keySet());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + ArrayFormat.format(featureNames) + "]";
    }

}
