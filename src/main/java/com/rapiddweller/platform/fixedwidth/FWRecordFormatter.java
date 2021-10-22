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

package com.rapiddweller.platform.fixedwidth;

import com.rapiddweller.common.Assert;
import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.common.Converter;
import com.rapiddweller.common.converter.AccessingConverter;
import com.rapiddweller.common.converter.ConverterChain;
import com.rapiddweller.common.converter.FormatFormatConverter;
import com.rapiddweller.format.fixedwidth.FixedWidthColumnDescriptor;
import com.rapiddweller.format.fixedwidth.FixedWidthUtil;
import com.rapiddweller.model.data.ComponentAccessor;
import com.rapiddweller.model.data.Entity;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Locale;

/**
 * Formats Entities' attributes as a fixed-width table.<br/><br/>
 * Created: 20.02.2014 14:03:25
 * @author Volker Bergmann
 * @since 0.9.0
 */
public class FWRecordFormatter {

  private final Converter<Entity, String>[] converters;

  @SuppressWarnings({"unchecked", "rawtypes"})
  public FWRecordFormatter(String columnFormatList, String nullString, Locale locale) {
    Assert.notNull(columnFormatList, "columnFormatList");
    try {
      FixedWidthColumnDescriptor[] descriptors = FixedWidthUtil.parseBeanColumnsSpec(columnFormatList, "", nullString, locale).getColumnDescriptors();
      this.converters = new Converter[descriptors.length];
      for (int i = 0; i < descriptors.length; i++) {
        FixedWidthColumnDescriptor descriptor = descriptors[i];
        ConverterChain<Entity, String> chain = new ConverterChain<>();
        chain.addComponent(new AccessingConverter<>(Entity.class, Object.class, new ComponentAccessor(descriptor.getName())));
        chain.addComponent(new FormatFormatConverter(String.class, descriptor.getFormat(), true));
        this.converters[i] = chain;
      }
    } catch (ParseException e) {
      throw new ConfigurationError("Invalid column definition: " + columnFormatList, e);
    }
  }

  public void format(Entity entity, PrintWriter printer) {
    for (Converter<Entity, String> converter : converters) {
      printer.print(converter.convert(entity));
    }
  }

}
