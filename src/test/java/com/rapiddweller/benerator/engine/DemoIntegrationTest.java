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

package com.rapiddweller.benerator.engine;

import com.rapiddweller.benerator.test.BeneratorIntegrationTest;
import com.rapiddweller.common.FileUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Integration test for Benerator's Demo Files.<br/><br/>
 * <p>
 * Created at 30.12.2020
 *
 * @author Alexander Kell
 * @since 1.1.0
 */
public class DemoIntegrationTest extends BeneratorIntegrationTest {
    private static final Logger logger = LogManager.getLogger(DemoIntegrationTest.class);

    String ROOT = "src/demo/resources/";

    private void parseAndExecute() throws IOException {
        for (File file : Objects.requireNonNull(new File(ROOT, context.getContextUri()).listFiles())) {
            String filename = file.getPath();
            if (FileUtil.isXMLFile(filename)) {
                logger.info(filename);
                parseAndExecuteFile(filename);
            }
        }
    }

    @Test
    public void DemoFilesPostprocess() throws IOException {
        context.setContextUri("/demo/file");
        parseAndExecuteFile("/demo/file/postprocess-import.ben.xml");
    }

    @Test
    public void DemoFilesImportFixedWidth() throws IOException {
        context.setContextUri("/demo/file");
        parseAndExecuteFile("/demo/file/import_fixed_width.ben.xml");
    }

    @Test
    public void DemoFilesGreetingCSV() throws IOException {
        context.setContextUri("/demo/file");
        parseAndExecuteFile("/demo/file/greetings_csv.ben.xml");
    }

    @Test
    public void DemoFilesCSVIO() throws IOException {
        context.setContextUri("/demo/file");
        parseAndExecuteFile("/demo/file/csv_io.ben.xml");
    }

    @Test
    public void DemoFilesXMLByScript() throws IOException {
        context.setContextUri("/demo/file");
        parseAndExecuteFile("/demo/file/create_xml_by_script.ben.xml");
    }

    @Test
    public void DemoFilesCreateXML() throws IOException {
        context.setContextUri("/demo/file");
        parseAndExecuteFile("/demo/file/create_xml.ben.xml");
    }

    @Test
    public void DemoFilesCreateXLSL() throws IOException {
        context.setContextUri("/demo/file");
        parseAndExecuteFile("/demo/file/create_xls.ben.xml");
    }


    @Test
    public void DemoFilesCreateDates() {
        try{
        context.setContextUri("/demo/file");
        parseAndExecuteFile("/demo/file/create_dates.ben.xml");
        }
        catch (Exception e){
            logger.info(e);
        }
    }

    @Test
    public void DemoFilesCreateCSV() throws IOException {
        context.setContextUri("/demo/file");
        parseAndExecuteFile("/demo/file/create_csv.ben.xml");
    }

    @Test
    public void DemoFilesXLSDemo() throws IOException {
        context.setContextUri("/demo/file");
        parseAndExecuteFile("/demo/file/xls-demo.ben.xml");
    }

    @Test
    public void DemoDbUser() throws IOException {
        context.setContextUri("/demo/db");
        parseAndExecuteFile("/demo/db/user.ben.xml");
    }

    @Test
    public void DemoDbCompositeKey() throws IOException {
        context.setContextUri("/demo/db");
        parseAndExecuteFile("/demo/db/compositekey.ben.xml");
    }

    @Test
    public void DemoScriptCode() throws IOException {
        context.setContextUri("/demo/script");
        parseAndExecuteFile("/demo/script/scriptcode.ben.xml");

    }

    @Test
    public void DemoScriptFile() throws IOException {
        context.setContextUri("/demo/script");
        parseAndExecuteFile("/demo/script/scriptfile.ben.xml");
    }

    @Ignore
    @Test
    public void ShopScript() throws IOException {
        context.setContextUri("/demo/shop");
        parseAndExecuteFile("/demo/shop/multischema.ben.xml");
    }

}
