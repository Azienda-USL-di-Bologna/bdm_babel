
package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.core.Task;

/**
 *
 * @author Matteo Next
 */
public abstract class AttendiJobsTask extends Task {

    @Override
    public String getTaskType() {
        return getClass().getSimpleName();
    }

    
}
