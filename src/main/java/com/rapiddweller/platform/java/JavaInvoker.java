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

package com.rapiddweller.platform.java;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.consumer.AbstractConsumer;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.commons.BeanUtil;

/**
 * {@link Consumer} implementation that maps input data to parameters
 * and invokes a method on a Java object with them.<br/><br/>
 * Created: 21.10.2009 17:23:22
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class JavaInvoker extends AbstractConsumer {

    private Object target;
    private String methodName;

    // constructors ----------------------------------------------------------------------------------------------------

    public JavaInvoker() {
        this(null, null);
    }

    public JavaInvoker(Object target, String methodName) {
        this.target = target;
        this.methodName = methodName;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    // Consumer interface impelementation ------------------------------------------------------------------------------

    @Override
    public void startProductConsumption(Object object) {
        if (object instanceof Entity)
            invokeByEntity((Entity) object);
        else if (object.getClass().isArray())
            invokeByArray((Object[]) object);
        else
            invokeByObject(object);
    }

    // private helper methods ------------------------------------------------------------------------------------------

    private void invokeByEntity(Entity object) {
        Object[] args = object.getComponents().values().toArray();
        if (target instanceof Class)
            BeanUtil.invokeStatic((Class<?>) target, methodName, args);
        else
            BeanUtil.invoke(false, target, methodName, args);
    }

    private void invokeByArray(Object[] args) {
        if (target instanceof Class)
            BeanUtil.invokeStatic((Class<?>) target, methodName, args);
        else
            BeanUtil.invoke(target, methodName, args);
    }

    private void invokeByObject(Object object) {
        if (target instanceof Class)
            BeanUtil.invokeStatic((Class<?>) target, methodName, object);
        else
            BeanUtil.invoke(target, methodName, object);
    }

}
