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

package com.rapiddweller.benerator;

import java.io.Closeable;
import java.io.Flushable;

import com.rapiddweller.benerator.storage.AbstractStorageSystem;
import com.rapiddweller.commons.Context;
import com.rapiddweller.formats.DataSource;
import com.rapiddweller.model.data.DescriptorProvider;
import com.rapiddweller.model.data.Entity;

/**
 * Abstract interface characterizing an Entity storage system. 
 * An implementation of this interface must inherit the class {@link AbstractStorageSystem}
 * if it is needs to be forward compatible.<br/><br/>
 * <br/>
 * Created: 27.06.2007 23:02:21
 * @since 0.4.0
 * @author Volker Bergmann
 */
public interface StorageSystem extends DescriptorProvider, Closeable, Flushable {

    /** Returns a name that identifies the database */
    @Override
	String getId();
    
    /** Creates an iterator that provides all entities of given type. */
    DataSource<Entity> queryEntities(String type, String selector, Context context);
    
    /** Queries for entity ids */
    DataSource<?> queryEntityIds(String type, String selector, Context context);

    /** Creates an Iterable for repetitive iteration through the results of the specified query. */
    DataSource<?> query(String selector, boolean simplify, Context context);
    
    /** Persists a new entity. */
    void store(Entity entity);
    
    /** Updates an existing entity. */
    void update(Entity entity);
    
    /** Executes a command on the storage system */
    Object execute(String command);
    
    /** Assures that all data that has been {@link #store(Entity)}d, is send to the target system. */
    @Override
	void flush();
    
    /** Closes the database. */
    @Override
	void close();
    
}
