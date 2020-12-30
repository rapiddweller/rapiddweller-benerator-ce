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

import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.model.data.FileBasedEntitySource;
import com.rapiddweller.platform.array.Array2EntityConverter;
import com.rapiddweller.common.*;
import com.rapiddweller.common.bean.ArrayPropertyExtractor;
import com.rapiddweller.common.converter.ArrayConverter;
import com.rapiddweller.common.converter.ConverterChain;
import com.rapiddweller.common.converter.NoOpConverter;
import com.rapiddweller.common.format.PadFormat;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.DataSource;
import com.rapiddweller.format.fixedwidth.FixedWidthColumnDescriptor;
import com.rapiddweller.format.fixedwidth.FixedWidthLineSource;
import com.rapiddweller.format.fixedwidth.FixedWidthRowTypeDescriptor;
import com.rapiddweller.format.fixedwidth.FixedWidthUtil;
import com.rapiddweller.format.util.ConvertingDataIterator;

import java.text.ParseException;
import java.util.Locale;

/**
 * Reads Entities from a fixed-width file.<br/>
 * <br/>
 * Created at 07.11.2008 18:18:24
 *
 * @author Volker Bergmann
 * @since 0.5.6
 */
public class FixedWidthEntitySource extends FileBasedEntitySource {

    private static final Escalator escalator = new LoggerEscalator();
    protected DataSource<String[]> source;
    protected Converter<String[], Entity> converter;
    private Locale locale;
    private String encoding;
    private String entityTypeName;
    private ComplexTypeDescriptor entityDescriptor;
    private FixedWidthColumnDescriptor[] descriptors;
    private String lineFilter;
    private final boolean initialized;
    private final Converter<String, String> preprocessor;

    public FixedWidthEntitySource() {
        this(null, null, SystemInfo.getFileEncoding(), null);
    }

    public FixedWidthEntitySource(String uri, ComplexTypeDescriptor entityDescriptor,
                                  String encoding, String lineFilter, FixedWidthColumnDescriptor... descriptors) {
        this(uri, entityDescriptor, new NoOpConverter<>(), encoding, lineFilter, descriptors);
    }

    public FixedWidthEntitySource(String uri, ComplexTypeDescriptor entityDescriptor,
                                  Converter<String, String> preprocessor, String encoding, String lineFilter,
                                  FixedWidthColumnDescriptor... descriptors) {
        super(uri);
        this.locale = Locale.getDefault();
        this.encoding = encoding;
        this.entityDescriptor = entityDescriptor;
        this.entityTypeName = (entityDescriptor != null ? entityDescriptor.getName() : null);
        this.descriptors = descriptors;
        this.preprocessor = preprocessor;
        this.initialized = false;
        this.lineFilter = lineFilter;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEntity() {
        return entityTypeName;
    }

    public void setEntity(String entity) {
        this.entityTypeName = entity;
    }

    /**
     * @throws ParseException
     * @deprecated use {@link #setColumns(String)}
     */
    @Deprecated
    public void setProperties(String properties) throws ParseException {
        escalator.escalate("The property 'properties' of class " + getClass() + "' has been renamed to 'columns'. " +
                "Please fix the property name in your configuration", this.getClass(), "setProperties()");
        setColumns(properties);
    }

    public void setColumns(String columns) throws ParseException {
        FixedWidthRowTypeDescriptor rowTypeDescriptor = FixedWidthUtil.parseBeanColumnsSpec(
                columns, entityTypeName, null, this.locale);
        this.descriptors = rowTypeDescriptor.getColumnDescriptors();
    }

    // Iterable interface ----------------------------------------------------------------------------------------------

    public void setLineFilter(String lineFilter) {
        this.lineFilter = lineFilter;
    }

    @Override
    public Class<Entity> getType() {
        if (!initialized)
            init();
        return Entity.class;
    }

    @Override
    public DataIterator<Entity> iterator() {
        if (!initialized)
            init();
        return new ConvertingDataIterator<>(this.source.iterator(), converter);
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void init() {
        if (this.entityDescriptor == null)
            this.entityDescriptor = new ComplexTypeDescriptor(entityTypeName, context.getLocalDescriptorProvider());
        if (ArrayUtil.isEmpty(descriptors))
            throw new InvalidGeneratorSetupException("Missing column descriptors. " +
                    "Use the 'columns' property of the " + getClass().getSimpleName() + " to define them.");
        this.source = createSource();
        this.converter = createConverter();
    }

    private DataSource<String[]> createSource() {
        PadFormat[] formats = ArrayPropertyExtractor.convert(descriptors, "format", PadFormat.class);
        return new FixedWidthLineSource(resolveUri(), formats, true, encoding, lineFilter);
    }

    @SuppressWarnings("unchecked")
    private Converter<String[], Entity> createConverter() {
        String[] featureNames = ArrayPropertyExtractor.convert(descriptors, "name", String.class);
        Array2EntityConverter a2eConverter = new Array2EntityConverter(entityDescriptor, featureNames, true);
        Converter<String[], String[]> aConv = new ArrayConverter<>(String.class, String.class, preprocessor);
        return new ConverterChain<>(aConv, a2eConverter);
    }

}
