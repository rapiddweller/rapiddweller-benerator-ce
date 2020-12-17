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


package com.rapiddweller.benerator.storage;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.StorageSystem;
import com.rapiddweller.model.data.DataModel;

/**
 * Abstract implementation of the StorageSystem interface.
 * When writing a custom implementation of SystemStorage interface, 
 * inherit from this class for assuring future compatibility.
 * If the interface would change in future versions, the future 
 * version of this class will try to compensate.<br/>
 * <br/>
 * Created: 27.01.2008 07:25:39
 * @since 0.4.0
 * @author Volker Bergmann
 */
public abstract class AbstractStorageSystem implements StorageSystem {
	
	protected DataModel dataModel;
	
	public AbstractStorageSystem() {
		this.dataModel = null;
	}

	@Override
	public DataModel getDataModel() {
		return dataModel;
	}
	
	@Override
	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}
	
	@Override
	public Object execute(String command) {
		throw new UnsupportedOperationException("execute() not supported by " + this);
	}
	
	public Consumer updater() {
		return new StorageSystemUpdater(this); 
	}
	
}
