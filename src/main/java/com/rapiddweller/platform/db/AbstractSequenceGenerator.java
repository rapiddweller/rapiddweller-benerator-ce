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

package com.rapiddweller.platform.db;

import com.rapiddweller.benerator.Generator;
import com.rapiddweller.benerator.GeneratorContext;
import com.rapiddweller.benerator.InvalidGeneratorSetupException;
import com.rapiddweller.benerator.util.ThreadSafeNonNullGenerator;
import com.rapiddweller.commons.StringUtil;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Abstract parent class for database-sequence-related {@link Generator}s.<br/><br/>
 * Created: 24.07.2011 06:16:59
 *
 * @author Volker Bergmann
 * @since 0.7.0
 */
public abstract class AbstractSequenceGenerator extends ThreadSafeNonNullGenerator<Long> {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected String name;
    protected DBSystem database;

    public AbstractSequenceGenerator(String name, DBSystem database) {
        this.name = name;
        this.database = database;
    }

    // properties ------------------------------------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DBSystem getDatabase() {
        return database;
    }

    public void setDatabase(DBSystem database) {
        this.database = database;
    }

    // Generator interface implementation ------------------------------------------------------------------------------

    @Override
    public Class<Long> getGeneratedType() {
        return Long.class;
    }

    @Override
    public synchronized void init(GeneratorContext context) {
        if (database == null)
            throw new InvalidGeneratorSetupException("No 'source' database defined");
        if (StringUtil.isEmpty(name))
            throw new InvalidGeneratorSetupException("No sequence 'name' defined");
        super.init(context);
    }

    // helpers ---------------------------------------------------------------------------------------------------------

    protected long fetchSequenceValue() {
        return database.nextSequenceValue(name);
    }

}
