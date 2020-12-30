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

package com.rapiddweller.benerator.archetype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;

import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Filter;
import com.rapiddweller.common.IOUtil;

/**
 * Represents a Benerator project archetype.<br/><br/>
 * Created at 18.02.2009 07:34:50
 * @since 0.5.9
 * @author Volker Bergmann
 */

public class Archetype implements Serializable {
	
    private static final long serialVersionUID = 2552120042802481049L;
    
	private final String id;
	private final URL url;
	private final URL iconUrl;
	private final String description;

	public Archetype(URL url) {
	    try {
	    	String urlString = url.toString();
	    	this.id = urlString.substring(urlString.lastIndexOf('/') + 1);
	        this.url = url;
	        URL infoUrl = new URL(url.toString() + "/ARCHETYPE-INF");
	        iconUrl = new URL(infoUrl.toString() + "/icon.gif");
	        this.description = resolveDescription(id, infoUrl);
        } catch (Exception e) {
        	throw new ConfigurationError("Error reading archetype info from " + url, e);
        }
    }

	private static String resolveDescription(String id, URL infoUrl) throws IOException {
	    URL descriptionUrl = new URL(infoUrl.toString() + "/description.properties");
	    String desc = null;
	    try {
		    InputStream descriptionFileStream = descriptionUrl.openStream();
		    Properties descriptions = new Properties();
			descriptions.load(descriptionFileStream);
            // try to get the name in the user's default locale...
		    desc = descriptions.getProperty(Locale.getDefault().getLanguage());
		    if (desc == null) // ...if no such name was defined, fall back to the English name (if it exists)
		    	desc = descriptions.getProperty("en");
		    if (desc == null) { // if there is even no English name, choose an arbitrary one
		    	Collection<Object> values = descriptions.values();
		    	if (values.size() > 0)
		    		desc = values.iterator().next().toString();
		    }
			descriptionFileStream.close();
	    } catch (FileNotFoundException e) {
	    	// no description file defined
	    }
	    finally {

		}
	    // if no description was found, choose the archetype id as description
	    return (desc != null ? desc : id);
    }

	public String getId() {
		return id;
	}
	
	public String getDescription() {
    	return description;
    }

	public URL getIconURL() {
    	return iconUrl;
    }

	public void copyFilesTo(File targetFolder, FolderLayout layout) throws IOException {
		copyNonSourceFilesTo(targetFolder);
		copySourceFilesTo(targetFolder, layout);
    }

	private void copyNonSourceFilesTo(File targetFolder) throws IOException {
		IOUtil.copyDirectory(url, targetFolder, candidate -> !candidate.contains("ARCHETYPE-INF") && !candidate.contains("/src/"));
		copySchemaTo(targetFolder);
    }

	private void copySourceFilesTo(File targetFolder, FolderLayout layout) throws IOException {
		copySourceDirectory(targetFolder, "src/main/java", layout);
		copySourceDirectory(targetFolder, "src/main/resources", layout);
		copySourceDirectory(targetFolder, "src/test/java", layout);
		copySourceDirectory(targetFolder, "src/test/resources", layout);
		copySchemaTo(targetFolder);
    }

	private void copySourceDirectory(File targetFolder, String subFolder, FolderLayout layout) throws IOException {
	    URL srcUrl = new URL(url.toString() + '/' + subFolder);
	    targetFolder = new File(targetFolder, layout.mapSubFolder(subFolder));
	    targetFolder.mkdir();
	    if (IOUtil.listResources(srcUrl).length > 0)
	    	IOUtil.copyDirectory(srcUrl, targetFolder, null);
    }

	private void copySchemaTo(File targetFolder) throws IOException {
		String xmlSchemaPath = BeneratorFactory.getSchemaPathForCurrentVersion();
	    URL schemaUrl = getClass().getClassLoader().getResource(xmlSchemaPath);
	    if (schemaUrl == null)
	    	throw new FileNotFoundException("File not found: " + xmlSchemaPath);
		InputStream in = schemaUrl.openStream();
		File file = new File(targetFolder, xmlSchemaPath.substring(xmlSchemaPath.lastIndexOf('/')));
		OutputStream out = new FileOutputStream(file);
		IOUtil.transfer(in, out);
		in.close();
    }

	@Override
	public String toString() {
		return description;
	}

	@Override
    public int hashCode() {
	    return id.hashCode();
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null || getClass() != obj.getClass())
		    return false;
	    Archetype that = (Archetype) obj;
	    return (this.id.equals(that.id));
    }
	
}
