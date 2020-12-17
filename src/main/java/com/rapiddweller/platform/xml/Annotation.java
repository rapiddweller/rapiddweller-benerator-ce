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

import com.rapiddweller.commons.xml.XMLUtil;
import org.w3c.dom.Element;

/**
 * Represents an XML annotation with a documentation String and an appInfo element.<br/><br/>
 * Created: 27.02.2008 09:51:45
 *
 * @author Volker Bergmann
 * @since 0.5.0
 */
public class Annotation {

    private final String documentation;
    private final Element appInfo;

    public Annotation(Element element) {
        this(getDocumentation(element), getAppInfo(element));
    }

    public Annotation(String documentation, Element appInfo) {
        this.documentation = documentation;
        this.appInfo = appInfo;
    }

    // interface -------------------------------------------------------------------------------------------------------

    private static Element getAppInfo(Element element) {
        return XMLUtil.getChildElement(element, false, false, "appinfo");
    }

    private static String getDocumentation(Element element) {
        Element docElement = XMLUtil.getChildElement(element, false, false, "documentation");
        return XMLUtil.getText(docElement);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    /**
     * @return the documentation
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * @return the appInfo
     */
    public Element getAppInfo() {
        return appInfo;
    }

}
