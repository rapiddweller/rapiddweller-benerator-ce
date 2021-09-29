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

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.DocumentWriter;

import java.io.IOException;

/**
 * Calls a Generator several times and writes its output to a DocumentWriter.<br/><br/>
 * Created: 08.06.2007 17:53:50
 * @author Volker Bergmann
 */
public class FileBuilder {

  private FileBuilder() {
    // private constructor to prevent instantiation
  }

  public static <T> void build(Generator<T> generator, int length, DocumentWriter<T> writer) throws IOException {
    writer.setVariable("part_count", length);
    ProductWrapper<T> wrapper = new ProductWrapper<>();
    for (int i = 0; i < length && wrapper != null; i++) {
      writer.setVariable("part_index", i);
      wrapper = generator.generate(wrapper);
      if (wrapper != null) {
        writer.writeElement(wrapper.unwrap());
      }
    }
    writer.close();
  }

}
