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

package com.rapiddweller.task;

import java.lang.reflect.InvocationTargetException;

/**
 * Provides invocation of an implementation of java.lang.Runnable from the command line.<br/>
 * <br/>
 * Created: 22.01.2007 10:11:15
 *
 * @since 0.2
 */
public class RunnableMain {

    /**
     * Instantiates the class specified by args[0] and calls its run() method.
     * The class needs to implement the interface java.lang.Runnable and to provide a public default constructor.
     *
     * @param args a String array of length 1 containing the name of the class to instantiate and execute
     * @throws ClassNotFoundException if the class could not be found
     * @throws IllegalAccessException if the constructor is not public
     * @throws InstantiationException if the class is abstract
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchMethodException,
            InvocationTargetException {
        assert args.length == 1;
        String className = args[0];
        Class<? extends Runnable> type =
                (Class<? extends Runnable>) Class.forName(className);
        Runnable task = type.getDeclaredConstructor().newInstance();
        task.run();
    }
}
