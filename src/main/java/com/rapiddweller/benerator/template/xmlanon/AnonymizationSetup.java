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


package com.rapiddweller.benerator.template.xmlanon;

import java.util.List;

/**
 * Wraps the complete configuration of a multi-file anonymization.<br/><br/>
 * Created: 27.02.2014 14:10:16
 *
 * @author Volker Bergmann
 * @since 0.9.0
 */
public class AnonymizationSetup {

  /**
   * The Files.
   */
  final List<String> files;
  /**
   * The Anonymizations.
   */
  final List<Anonymization> anonymizations;

  /**
   * Instantiates a new Anonymization setup.
   *
   * @param files          the files
   * @param anonymizations the anonymizations
   */
  public AnonymizationSetup(List<String> files, List<Anonymization> anonymizations) {
    this.files = files;
    this.anonymizations = anonymizations;
  }

  /**
   * Gets files.
   *
   * @return the files
   */
  public List<String> getFiles() {
    return files;
  }

  /**
   * Gets anonymizations.
   *
   * @return the anonymizations
   */
  public List<Anonymization> getAnonymizations() {
    return anonymizations;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + anonymizations.toString() + files.toString();
  }
}