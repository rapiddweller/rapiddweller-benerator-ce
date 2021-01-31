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

import org.databene.document.csv.CSVToJavaBeanMapper;
import org.databene.webdecs.DataContainer;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.benerator.demo.product.Product;

import java.io.IOException;
import java.io.BufferedReader;

/**
 * Demonstrates how to create (simple) bean graphs from a CSV file.<br/>
 * <br/>
 * Created: 19.07.2007 07:06:22
 *
 * @author Volker Bergmann
 */
public class BeanGraphDemo {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = IOUtil.getReaderForURI(
                "com/rapiddweller/benerator/products.csv");
        CSVToJavaBeanMapper<Product> mapper =
                new CSVToJavaBeanMapper<Product>(reader, Product.class);
        DataContainer<Product> value = new DataContainer<Product>();
        while ((value = mapper.next(value)) != null) {
            System.out.println(value.getData());
        }
    }

}
