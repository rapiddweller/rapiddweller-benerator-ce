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

import com.rapiddweller.common.IOUtil;
import com.rapiddweller.jdbacl.ColumnInfo;
import com.rapiddweller.model.data.ComplexTypeDescriptor;
import com.rapiddweller.model.data.DataModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * Default implementation of the {@link DBSystem} class.<br/><br/>
 * Created: 27.06.2007 23:04:19
 *
 * @author Volker Bergmann
 * @since 0.3
 */
public class DefaultDBSystem extends DBSystem {

    private final ConnectionHolder connectionHolder;

    public DefaultDBSystem(String id, String environment, DataModel dataModel) {
        super(id, environment, dataModel);
        this.connectionHolder = new ConnectionHolder(this);
    }

    public DefaultDBSystem(String id, String url, String driver, String user,
                           String password, DataModel dataModel) {
        super(id, url, driver, user, password, dataModel);
        this.connectionHolder = new ConnectionHolder(this);
    }

    @Override
    public void flush() {
        logger.debug("flush()");
        connectionHolder.commit();
    }

    @Override
    public void close() {
        logger.debug("close()");
        flush();
        IOUtil.close(connectionHolder);
        super.close();
    }

    @Override
    public Connection getConnection() {
        return connectionHolder.getConnection();
    }

    @Override
    protected PreparedStatement getSelectByPKStatement(
            ComplexTypeDescriptor descriptor) {
        return connectionHolder.getSelectByPKStatement(descriptor);
    }

    @Override
    protected PreparedStatement getStatement(ComplexTypeDescriptor descriptor,
                                             boolean insert,
                                             List<ColumnInfo> columnInfos) {
        return connectionHolder.getStatement(descriptor, insert, columnInfos);
    }

}
