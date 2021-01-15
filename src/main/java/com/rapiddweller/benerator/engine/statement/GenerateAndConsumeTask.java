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

package com.rapiddweller.benerator.engine.statement;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rapiddweller.benerator.Consumer;
import com.rapiddweller.benerator.composite.ComponentBuilder;
import com.rapiddweller.benerator.engine.BeneratorContext;
import com.rapiddweller.benerator.engine.BeneratorMonitor;
import com.rapiddweller.benerator.engine.CurrentProductGeneration;
import com.rapiddweller.benerator.engine.LifeCycleHolder;
import com.rapiddweller.benerator.engine.ScopedLifeCycleHolder;
import com.rapiddweller.benerator.engine.ResourceManager;
import com.rapiddweller.benerator.engine.ResourceManagerSupport;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.StatementUtil;
import com.rapiddweller.benerator.wrapper.ProductWrapper;
import com.rapiddweller.common.Context;
import com.rapiddweller.common.ErrorHandler;
import com.rapiddweller.common.IOUtil;
import com.rapiddweller.common.MessageHolder;
import com.rapiddweller.common.Resettable;
import com.rapiddweller.script.Expression;
import com.rapiddweller.script.expression.ExpressionUtil;
import com.rapiddweller.task.PageListener;
import com.rapiddweller.task.Task;
import com.rapiddweller.task.TaskResult;

/**
 * Task that creates one data set instance per run() invocation and sends it to the specified consumer.<br/><br/>
 * Created: 01.02.2008 14:39:11
 * @author Volker Bergmann
 */
public class GenerateAndConsumeTask implements Task, PageListener, ResourceManager, MessageHolder {

	private final String taskName;
    private BeneratorContext context;
    private final ResourceManager resourceManager;
    
    protected final List<Statement> statements;
    private final List<ScopedLifeCycleHolder> scopeds;
    private Expression<Consumer> consumerExpr;

    private volatile AtomicBoolean initialized;
    private Consumer consumer;
    private String message;
	private final String productName;
    
    public GenerateAndConsumeTask(String taskName, String productName) {
    	this.taskName = taskName;
    	this.productName = productName;
        this.resourceManager = new ResourceManagerSupport();
        this.initialized = new AtomicBoolean(false);
    	this.statements = new ArrayList<>();
    	this.scopeds = new ArrayList<>();
    }

    // interface -------------------------------------------------------------------------------------------------------

    public void addStatement(Statement statement) {
    	this.statements.add(statement);
    }
    
    public void setStatements(List<Statement> statements) {
    	this.statements.clear();
    	for (Statement statement : statements)
    		this.addStatement(statement);
    }
    
    public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setConsumer(Expression<Consumer> consumerExpr) {
        this.consumerExpr = consumerExpr;
	}
    
    public Consumer getConsumer() {
    	return consumer;
    }
	
	public void init(BeneratorContext context) {
	    synchronized (initialized) {
	    	if (!initialized.get()) {
	    		this.context = context;
	    		this.consumer = ExpressionUtil.evaluate(consumerExpr, context);
    			resourceManager.addResource(consumer);
	    		injectConsumptionStart();
	    		injectConsumptionEnd();
	    		initialized.set(true);
	        	initStatements(context);
	        	checkScopes(statements, context);
	    	}
	    }
    }

	public String getProductName() {
		return productName;
	}

	public ProductWrapper<?> getRecentProduct() {
		return context.getCurrentProduct();
	}

    // Task interface implementation -----------------------------------------------------------------------------------
    
	@Override
	public String getTaskName() {
	    return taskName;
    }

    @Override
	public boolean isThreadSafe() {
        return false;
    }
    
    @Override
	public boolean isParallelizable() {
        return false;
    }
    
    @Override
	public TaskResult execute(Context ctx, ErrorHandler errorHandler) {
    	message = null;
    	if (!initialized.get())
    		init((BeneratorContext) ctx);
    	try {
    		boolean success = true;
        	for (int i = 0; i < statements.size(); i++) {
        		Statement statement = statements.get(i);
				success &= statement.execute(context);
				if (!success && (statement instanceof ValidationStatement)) {
					i = -1; // if the product is not valid, restart with the first statement
					success = true;
					continue;
				}
				if (!success) {
					if (statement instanceof MessageHolder)
						this.message = ((MessageHolder) statement).getMessage();
					break;
				}
			}
        	if (success)
        		BeneratorMonitor.INSTANCE.countGenerations(1);
        	enqueueResets(statements);
	        Thread.yield();
	        return (success ? TaskResult.EXECUTING : TaskResult.UNAVAILABLE);
    	} catch (Exception e) {
			errorHandler.handleError("Error in execution of task " + getTaskName(), e);
    		return TaskResult.EXECUTING; // stay available if the ErrorHandler has not canceled execution
    	}
    }
    
    public void reset() {
		for (Statement statement : statements) {
			statement = StatementUtil.getRealStatement(statement, context);
		    if (statement instanceof ScopedLifeCycleHolder) {
		    	ScopedLifeCycleHolder holder = (ScopedLifeCycleHolder) statement;
				holder.resetIfNeeded();
		    } else if (statement instanceof Resettable) {
		    	((Resettable) statement).reset();
		    }
		}
    }

	@Override
	public void close() {
        // close sub statements
        for (Statement statement : statements) {
			statement = StatementUtil.getRealStatement(statement, context);
		    if (statement instanceof Closeable)
		    	IOUtil.close((Closeable) statement);
		}
        // close resource manager
        resourceManager.close();
    }
    
    
    // PageListener interface ------------------------------------------------------------------------------------------
    
	@Override
	public void pageStarting() {
		// nothing special to do on page start
	}
    
    @Override
	public void pageFinished() {
    	IOUtil.flush(consumer);
    }
    

    // ResourceManager interface ---------------------------------------------------------------------------------------
    
	@Override
	public boolean addResource(Closeable resource) {
	    return resourceManager.addResource(resource);
    }
	
	// MessageHolder interface -----------------------------------------------------------------------------------------
	
	@Override
	public String getMessage() {
	    return message;
    }
	
	// java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + taskName + ')';
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private void injectConsumptionStart() {
		// find last sub member generation...
		int lastMemberIndex = - 1;
		for (int i = statements.size() - 1; i >= 0; i--) {
			Statement statement = statements.get(i);
			if (statement instanceof ComponentBuilder || statement instanceof CurrentProductGeneration 
					|| statement instanceof ValidationStatement || statement instanceof ConversionStatement) {
				lastMemberIndex = i;
				break;
			}
		}
		// ...and insert consumption start statement immediately after that one
		ConsumptionStatement consumption = new ConsumptionStatement(consumer, true, false);
		statements.add(lastMemberIndex + 1, consumption);
	}

	private void injectConsumptionEnd() {
		// find last sub generation statement...
		int lastSubGenIndex = statements.size() - 1;
		for (int i = statements.size() - 1; i >= 0; i--) {
			Statement statement = statements.get(i);
			if (statement instanceof GenerateOrIterateStatement) {
				lastSubGenIndex = i;
				break;
			}
		}
		// ...and insert consumption finish statement immediately after that one
		ConsumptionStatement consumption = new ConsumptionStatement(consumer, false, true);
		statements.add(lastSubGenIndex + 1, consumption);
	}

	public void initStatements(BeneratorContext context) {
		for (Statement statement : statements) {
			statement = StatementUtil.getRealStatement(statement, context);
			if (statement instanceof LifeCycleHolder)
			    ((LifeCycleHolder) statement).init(context);
		}
	}

    private void checkScopes(List<Statement> statements, BeneratorContext scopeContext) {
        for (Statement statement : statements) {
			statement = StatementUtil.getRealStatement(statement, scopeContext);
		    if (statement instanceof ScopedLifeCycleHolder) {
		    	ScopedLifeCycleHolder holder = (ScopedLifeCycleHolder) statement;
		    	String scope = holder.getScope();
		    	if (scope == null || productName.equals(scope))
		    		scopeds.add(holder);
		    } else if (statement instanceof GenerateOrIterateStatement) {
		    	GenerateOrIterateStatement subGenerate = (GenerateOrIterateStatement) statement;
		    	checkScopes(subGenerate.getTask().statements, subGenerate.getChildContext());
		    }
		}
    }

    private void enqueueResets(List<Statement> statements) {
    	for (ScopedLifeCycleHolder scoped : scopeds)
    		scoped.setResetNeeded(true);
    }

}
