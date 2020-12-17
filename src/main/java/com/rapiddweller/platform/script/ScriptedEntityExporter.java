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

package com.rapiddweller.platform.script;

import com.rapiddweller.benerator.consumer.TextFileExporter;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.commons.ConfigurationError;
import com.rapiddweller.formats.script.ScriptedDocumentWriter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

/**
 * Script based entity exporter.
 * Three scripts may be combined for formatting header, generated document part(s) and footer<br/>
 * <br/>
 * Created: 01.09.2007 18:05:04
 *
 * @author Volker Bergmann
 */
public class ScriptedEntityExporter extends TextFileExporter {

    private static final Logger LOGGER = LogManager.getLogger(ScriptedEntityExporter.class);

    private String headerScript;
    private String partScript;
    private String footerScript;

    private ScriptedDocumentWriter<Entity> docWriter;

    // constructors ----------------------------------------------------------------------------------------------------

    public ScriptedEntityExporter() {
        this(null, null);
    }

    public ScriptedEntityExporter(String uri, String partScript) {
        this(uri, null, null, partScript, null);
    }

    public ScriptedEntityExporter(String uri, String encoding, String headerScript, String partScript, String footerScript) {
        super(uri, encoding, null);
        this.headerScript = headerScript;
        this.partScript = partScript;
        this.footerScript = footerScript;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getHeaderScript() {
        return headerScript;
    }

    public void setHeaderScript(String headerScript) {
        this.headerScript = headerScript;
    }

    public String getPartScript() {
        return partScript;
    }

    public void setPartScript(String partScript) {
        this.partScript = partScript;
    }

    public String getFooterScript() {
        return footerScript;
    }

    public void setFooterScript(String footerScript) {
        this.footerScript = footerScript;
    }

    // Consumer interface ----------------------------------------------------------------------------------------------

    @Override
    protected void postInitPrinter(Object object) {
        try {
            docWriter = new ScriptedDocumentWriter<Entity>(printer, headerScript, partScript, footerScript);
            if (append)
                docWriter.setWriteHeader(false);
        } catch (IOException e) {
            throw new ConfigurationError(e);
        }
    }

    @Override
    protected void startConsumingImpl(Object object) {
        try {
            LOGGER.debug("Exporting {}", object);
            Entity entity = (Entity) object;
            docWriter.writeElement(entity);
        } catch (IOException e) {
            throw new ConfigurationError(e);
        }
    }

}
