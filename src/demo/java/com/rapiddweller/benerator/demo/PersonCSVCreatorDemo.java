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

package com.rapiddweller.benerator.demo;

import com.rapiddweller.benerator.engine.DefaultBeneratorContext;
import com.rapiddweller.benerator.file.FileBuilder;
import com.rapiddweller.domain.person.PersonGenerator;
import com.rapiddweller.domain.person.Person;
import com.rapiddweller.common.DocumentWriter;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.script.Script;
import org.databene.document.csv.BeanCSVWriter;

import java.io.*;

/**
 * Demonstrates how to generated and format JavaBeans into a CSV file.<br/>
 * <br/>
 * Created: 06.06.2007 19:43:09
 */
public class PersonCSVCreatorDemo {

  private static final String FILE_NAME = "persons.csv";
  private static final int LENGTH = 5;

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws IOException the io exception
   */
  public static void main(String[] args) throws IOException {
    Writer out = null;
    try {
      out = new OutputStreamWriter(System.out);
      //out = new BufferedWriter(new FileWriter(FILE_NAME));
      DocumentWriter<Person> writer =
          new BeanCSVWriter<Person>(out, ',', (Script) null,
              (Script) null,
              "salutation", "title", "givenName", "familyName");
      System.out.println("Running...");
      long startMillis = System.currentTimeMillis();
      writer.setVariable("length", LENGTH);
      PersonGenerator generator = new PersonGenerator();
      generator.init(new DefaultBeneratorContext());
      FileBuilder.build(generator, LENGTH, writer);
      long elapsedTime = System.currentTimeMillis() - startMillis;
      System.out.println("Created file " + FILE_NAME + " with " + LENGTH +
          " entries " +
          "within " + (elapsedTime / 1000) + "s (" +
          (LENGTH * 1000L / elapsedTime) + " entries per second)");
    } finally {
      IOUtil.close(out);
    }
  }
}
