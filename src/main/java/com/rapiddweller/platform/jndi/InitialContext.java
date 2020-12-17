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

package com.rapiddweller.platform.jndi;

import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Provides InitialContext operations with a more convenient property-based setup..<br/><br/>
 * Created: 21.10.2009 19:47:17
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class InitialContext {

    private String url;
    private String factory;
    private String user;
    private String password;

    private javax.naming.InitialContext realContext;

    public InitialContext() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // interface -------------------------------------------------------------------------------------------------------

    public Object lookup(String name) throws NamingException {
        return getRealContext().lookup(name);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private javax.naming.InitialContext getRealContext() throws NamingException {
        if (realContext == null)
            init();
        return realContext;
    }

    private void init() throws NamingException {
        if (factory != null) {
            Properties p = new Properties();
            p.setProperty(Context.INITIAL_CONTEXT_FACTORY, factory);
            p.setProperty(Context.PROVIDER_URL, url);
            p.setProperty(Context.SECURITY_PRINCIPAL, user);
            p.setProperty(Context.SECURITY_CREDENTIALS, password);
            realContext = new javax.naming.InitialContext(p);
        } else
            realContext = new javax.naming.InitialContext();
    }

}
