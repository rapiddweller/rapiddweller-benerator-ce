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

package com.rapiddweller.benerator.gui;

/**
 * Represents a Maven dependency.<br/>
 * <br/>
 * Created at 29.11.2008 23:25:08
 *
 * @author Volker Bergmann
 * @since 0.5.6
 */
public class MavenDependency {

  private String groupId;
  private String artifactId;
  private String version;

  /**
   * Instantiates a new Maven dependency.
   */
  public MavenDependency() {
    this(null, null, null);
  }

  /**
   * Instantiates a new Maven dependency.
   *
   * @param groupId    the group id
   * @param artifactId the artifact id
   * @param version    the version
   */
  public MavenDependency(String groupId, String artifactId, String version) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  /**
   * Gets group id.
   *
   * @return the group id
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * Sets group id.
   *
   * @param groupId the group id
   */
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  /**
   * Gets artifact id.
   *
   * @return the artifact id
   */
  public String getArtifactId() {
    return artifactId;
  }

  /**
   * Sets artifact id.
   *
   * @param artifactId the artifact id
   */
  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  /**
   * Gets version.
   *
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets version.
   *
   * @param version the version
   */
  public void setVersion(String version) {
    this.version = version;
  }

}
