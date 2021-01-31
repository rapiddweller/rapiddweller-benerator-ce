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

import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.sample.NonNullSampleGenerator;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.FileUtil;
import com.rapiddweller.common.IOUtil;

import java.io.File;

/**
 * Generates {@link File} objects which represent files and/or directories in a parent directory.<br/><br/>
 * Created: 24.02.2010 10:47:44
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class FileGenerator extends NonNullSampleGenerator<File> { 
	
	private String rootUri;
	private String filter;
	private boolean recursive;
	private boolean folders;
	private boolean files;
	
	public FileGenerator() {
	    this(".", null, false, false, true);
    }
	
	public FileGenerator(String rootUri, String filter, boolean recursive, boolean files, boolean folders) {
		super(File.class);
	    this.rootUri = rootUri;
	    this.filter = filter;
	    this.recursive = recursive;
	    this.folders = folders;
	    this.files = files;
    }
	
	// properties ------------------------------------------------------------------------------------------------------

	public void setRootUri(String rootUri) {
    	this.rootUri = rootUri;
    }

	public void setFilter(String filter) {
    	this.filter = filter;
    }

	public void setRecursive(boolean recursive) {
    	this.recursive = recursive;
    }

	public void setFolders(boolean folders) {
    	this.folders = folders;
    }

	public void setFiles(boolean files) {
    	this.files = files;
    }

	public void setContext(Context context) {
	    this.context = (BeneratorContext) context;
    }
	
	// implementation --------------------------------------------------------------------------------------------------
	
	@Override
	public void init(GeneratorContext context) {
		assertNotInitialized();
    	try {
            String baseUri = IOUtil.resolveRelativeUri(rootUri, context.getContextUri());
            File baseFile = new File(baseUri);
			setValues(FileUtil.listFiles(baseFile, filter, recursive, files, folders));
            super.init(context);
        } catch (Exception e) {
            throw new ConfigurationError(e);
        }
	}

}
