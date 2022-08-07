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

package com.rapiddweller.benerator.file;

import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.benerator.wrapper.NonNullGeneratorWrapper;

import java.io.File;
import java.io.IOException;

/**
 * Scans a folder or file system tree and provides a random element
 * of this structure at each call to {@link #generate()}.
 * The file selection can be reduced to only <b>files</b> or <b>folders</b> or both,
 * the scan may be flat or <b>recursive</b>, a regular expression may be used
 * to <b>filter</b> for names and the file name format may be local,
 * absolute or canonical<br/><br/>
 * Created: 24.02.2010 06:30:22
 * @author Volker Bergmann
 * @since 0.6.0
 */
public class FileNameGenerator extends NonNullGeneratorWrapper<File, String> {

  private PathType pathType;

  /** Default constructor initializing the generator to scan all file types recursively
   *  starting at the working directory without filtering and provide their absolute paths. */
  public FileNameGenerator() {
    this(".", null, false, true, false);
  }

  /** Constructor for backwards compatibility which uses {@link PathType#absolute}. */
  public FileNameGenerator(
      String rootUri, String filter, boolean recursive, boolean files, boolean folders) {
    this(rootUri, filter, PathType.absolute, recursive, files, folders);
  }

  /** Full constructor */
  public FileNameGenerator(String rootUri, String filter, PathType pathType,
                           boolean recursive, boolean files, boolean folders) {
    super(new FileGenerator(rootUri, filter, recursive, folders, files));
    setRootUri(rootUri);
    setFilter(filter);
    setFileNameType(pathType);
    setRecursive(recursive);
    setFolders(folders);
    setFiles(files);
  }

  // properties ------------------------------------------------------------------------------------------------------

  /** Sets the root folder for the file system scan. */
  public void setRootUri(String rootUri) {
    ((FileGenerator) getSource()).setRootUri(rootUri);
  }

  /** Sets a regular expression for filtering file names.
   *  It is only applied to the local file names, not to composite paths. */
  public void setFilter(String filter) {
    ((FileGenerator) getSource()).setFilter(filter);
  }

  /** Sets the type of file name to generate. @see {@link PathType} */
  public void setFileNameType(PathType pathType) {
    this.pathType = pathType;
  }

  /** If set to true, files are included, otherwise not */
  public void setFiles(boolean files) {
    ((FileGenerator) getSource()).setFiles(files);
  }

  /** If set to true, folders are included, otherwise not */
  public void setFolders(boolean folders) {
    ((FileGenerator) getSource()).setFolders(folders);
  }

  /** If set to true, the file system is scanned recursively, otherwise just the root uri */
  public void setRecursive(boolean recursive) {
    ((FileGenerator) getSource()).setRecursive(recursive);
  }

  /** If set to true, the generator assures, each file is generated only once. */
  public void setUnique(boolean unique) {
    ((FileGenerator) getSource()).setUnique(unique);
  }


  // Generator implementation ----------------------------------------------------------------------------------------

  @Override
  public Class<String> getGeneratedType() {
    return String.class;
  }

  @Override
  public String generate() {
    File file = generateFromSource().unwrap();
    switch (pathType) {
      case canonical: return canonicalPath(file);
      case local: return file.getName();
      default: return file.getAbsolutePath();
    }
  }

  private String canonicalPath(File file) {
    try {
      return file.getCanonicalPath();
    } catch (IOException e) {
      throw BeneratorExceptionFactory.getInstance().internalError(
          "Error calculating canonical path for " + file, e);
    }
  }

  public enum PathType { // TODO v3.0.0 integration test for FileNameType support
    /** Represents the absolute file name, @see {@link File#getAbsolutePath()}. */
    absolute,
    /** Represents the canonical file name, @see {{@link File#getCanonicalPath()}}. */
    canonical,
    /** Represents the local file name, @see {@link File#getName()}. */
    local
  }

}
