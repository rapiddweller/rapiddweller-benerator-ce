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

import com.rapiddweller.benerator.factory.ConsumerMock;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

/**
 * Helper class for mocking JNDI functionality.<br/><br/>
 * Created: 21.10.2009 20:03:01
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class InitialContextFactoryMock implements InitialContextFactory, Context {

  @Override
  public Context getInitialContext(Hashtable<?, ?> environment) {
    return this;
  }

  @Override
  public Object addToEnvironment(String s, Object obj) {
    return null;
  }

  @Override
  public void bind(Name name, Object obj) {
  }

  @Override
  public void bind(String s, Object obj) {
  }

  @Override
  public void close() {
  }

  @Override
  public Name composeName(Name name, Name name1) {
    return null;
  }

  @Override
  public String composeName(String s, String s1) {
    return null;
  }

  @Override
  public Context createSubcontext(Name name) {
    return null;
  }

  @Override
  public Context createSubcontext(String s) {
    return null;
  }

  @Override
  public void destroySubcontext(Name name) {
  }

  @Override
  public void destroySubcontext(String s) {
  }

  @Override
  public Hashtable<?, ?> getEnvironment() {
    return null;
  }

  @Override
  public String getNameInNamespace() {
    return null;
  }

  @Override
  public NameParser getNameParser(Name name) {
    return null;
  }

  @Override
  public NameParser getNameParser(String s) {
    return null;
  }

  @Override
  public NamingEnumeration<NameClassPair> list(Name name) {
    return null;
  }

  @Override
  public NamingEnumeration<NameClassPair> list(String s) {
    return null;
  }

  @Override
  public NamingEnumeration<Binding> listBindings(Name name) {
    return null;
  }

  @Override
  public NamingEnumeration<Binding> listBindings(String s) {
    return null;
  }

  @Override
  public Object lookup(Name name) {
    return new ConsumerMock();
  }

  @Override
  public Object lookup(String s) {
    return new ConsumerMock();
  }

  @Override
  public Object lookupLink(Name name) {
    return null;
  }

  @Override
  public Object lookupLink(String s) {
    return null;
  }

  @Override
  public void rebind(Name name, Object obj) {
  }

  @Override
  public void rebind(String s, Object obj) {
  }

  @Override
  public Object removeFromEnvironment(String s) {
    return null;
  }

  @Override
  public void rename(Name name, Name name1) {
  }

  @Override
  public void rename(String s, String s1) {
  }

  @Override
  public void unbind(Name name) {
  }

  @Override
  public void unbind(String s) {
  }

}
