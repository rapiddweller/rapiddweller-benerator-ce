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

import com.rapiddweller.benerator.engine.BeneratorContext;

/**
 * Parent class for {@link EntitySource}s that import entities from files.<br/><br/>
 * Created: 12.03.2010 11:05:13
 *
 * @author Volker Bergmann
 * @since 0.6.0
 */
public abstract class FileBasedEntitySource extends AbstractEntitySource {

  /**
   * The Uri.
   */
  protected String uri;

  /**
   * Instantiates a new File based entity source.
   *
   * @param uri the uri
   */
  public FileBasedEntitySource(String uri) {
    this(uri, null);
  }

  /**
   * Instantiates a new File based entity source.
   *
   * @param uri     the uri
   * @param context the context
   */
  public FileBasedEntitySource(String uri, BeneratorContext context) {
    this.uri = uri;
    this.context = context;
  }

  // properties ------------------------------------------------------------------------------------------------------

  /**
   * Gets uri.
   *
   * @return the uri
   */
  public String getUri() {
    return uri;
  }

  /**
   * Sets uri.
   *
   * @param uri the uri
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  /**
   * Resolve uri string.
   *
   * @return the string
   */
  protected String resolveUri() {
    return context.resolveRelativeUri(uri);
  }

  // java.lang.Object overrides --------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + uri + "]";
  }

}
