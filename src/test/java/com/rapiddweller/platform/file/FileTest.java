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

package com.rapiddweller.platform.file;

import java.io.File;
import java.io.IOException;

import com.rapiddweller.commons.IOUtil;

/**
 * Parent class for file-related tests.<br/>
 * <br/>
 * Created at 17.09.2009 10:25:10
 * @since 0.6.0
 * @author Volker Bergmann
 */

public abstract class FileTest {

    protected File createSource1() throws IOException {
	    File sourceFile1;
	    sourceFile1 = new File("target" + File.separator + "FT1.txt");
	    IOUtil.writeTextFile(sourceFile1.getAbsolutePath(), "ABC");
	    return sourceFile1;
    }
	
    protected File createSource2() throws IOException {
	    File sourceFile2;
	    sourceFile2 = new File("target" + File.separator + "FT2.txt");
	    IOUtil.writeTextFile(sourceFile2.getAbsolutePath(), "123");
	    return sourceFile2;
    }

    protected String prefix() {
    	return getClass().getSimpleName();
    }

}
