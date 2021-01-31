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

import com.rapiddweller.benerator.wrapper.NonNullGeneratorWrapper;

import java.io.File;

/**
 * Generates file and/or directory names out of a directory.<br/><br/>
 * Created: 24.02.2010 06:30:22
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class FileNameGenerator extends NonNullGeneratorWrapper<File, String> {

	public FileNameGenerator() {
	    this(".", null, false, true, false);
    }
	
	public FileNameGenerator(String rootUri, String filter, boolean recursive, boolean files, boolean folders) {
		super(new FileGenerator(rootUri, filter, recursive, folders, files));
	    setRootUri(rootUri);
	    setFilter(filter);
	    setRecursive(recursive);
	    setFolders(folders);
	    setFiles(files);
    }

	// properties ------------------------------------------------------------------------------------------------------

	public void setRootUri(String rootUri) {
	    ((FileGenerator) getSource()).setRootUri(rootUri);
    }

	public void setFilter(String filter) {
		((FileGenerator) getSource()).setFilter(filter);
    }

	public void setFiles(boolean files) {
		((FileGenerator) getSource()).setFiles(files);
    }

	public void setFolders(boolean folders) {
		((FileGenerator) getSource()).setFolders(folders);
    }

	public void setRecursive(boolean recursive) {
		((FileGenerator) getSource()).setRecursive(recursive);
    }

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
	    return generateFromSource().unwrap().getAbsolutePath();
    }

}
