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

package com.rapiddweller.platform.xml;

import static org.junit.Assert.assertEquals;

import com.rapiddweller.common.xml.XMLUtil;
import com.rapiddweller.model.data.DataModel;
import com.rapiddweller.model.data.DefaultDescriptorProvider;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.Entity;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Tests the {@link XMLPlatformUtil}.<br/><br/>
 * Created: 16.01.2014 13:57:18
 *
 * @author Volker Bergmann
 * @since 0.9.0
 */

public class XMLPlatformUtilTest {

    @Test
    public void test() {
        Element element = XMLUtil.parseStringAsElement("<person age='23'><name>Alice</name></person>");
        DescriptorProvider provider = new DefaultDescriptorProvider("test", new DataModel());
        Entity entity = XMLPlatformUtil.convertElement2Entity(element, provider);
        assertEquals("person", entity.type());
        assertEquals("23", entity.get("age"));
        assertEquals("Alice", entity.get("name"));
    }

    @Test
    public void testConvertToString() {
        assertEquals("value", XMLPlatformUtil.convertToString("value"));
    }

    @Test
    public void testNormalizeName() {
        assertEquals("Name", XMLPlatformUtil.normalizeName("Name"));
    }

}
