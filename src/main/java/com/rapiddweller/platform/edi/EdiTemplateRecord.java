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

package com.rapiddweller.platform.edi;

import com.rapiddweller.common.converter.AnyConverter;
import com.rapiddweller.platform.template.DefaultTemplateRecord;

import java.util.List;
import java.util.Map;

/**
 * TODO Document class.<br/><br/>
 * Created: 30.06.2014 11:14:26
 *
 * @author Volker Bergmann
 * @since TODO version
 */
public class EdiTemplateRecord extends DefaultTemplateRecord {

  @Override
  public Object get(String name) {
    if ("recursiveSegmentCount".equals(name)) {
      return calculateRecursiveSegmentCount();
    } else {
      return super.get(name);
    }
  }

  private int calculateRecursiveSegmentCount() {
    int sum = getBaseSegmentCount();
    for (Map.Entry<String, ?> component : components.entrySet()) {
      if (component.getValue() instanceof List) {
        for (Object listItem : (List<?>) component.getValue()) {
          if (listItem instanceof EdiTemplateRecord) {
            sum += ((EdiTemplateRecord) listItem)
                .calculateRecursiveSegmentCount();
          }
        }
      }
    }
    return sum;
  }

  private int getBaseSegmentCount() {
    Object baseSegmentCountSpec = get("baseSegmentCount");
    if (baseSegmentCountSpec != null) {
      return AnyConverter.convert(baseSegmentCountSpec, Integer.class);
    } else {
      return 0;
    }
  }

}
