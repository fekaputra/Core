/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.backend.execution.pipeline;

import java.util.Map;

import org.springframework.core.Ordered;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;

/**
 * Provide action that should be perform before starts the the pipeline
 * execution. Must not execute the DPU. The PreExecutors can be called in random
 * order, but they all will be called before the call of {@link Executor}.
 * The {@link PreExecutor}s are used as a singletons, so they
 * must be able to run concurrently on multiple instances.
 * The PreExecutors are executed in order that is defined by {@link Ordered}
 * 
 * @author Petyr
 */
public interface PreExecutor extends Ordered {

    /**
     * Should perform pre-execution actions. If return false then the execution
     * is cancelled. In such case it should publish instance of {@link cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineEvent} with problem description.
     * 
     * @param execution
     * @param contexts
     * @param graph
     *            Dependency graph used for execution.
     * @param success
     *            False if the execution it self will not be executed.
     * @return False if the post-executor failed.
     */
    public boolean preAction(PipelineExecution execution,
            Map<Node, Context> contexts,
            DependencyGraph graph,
            boolean success);

}
