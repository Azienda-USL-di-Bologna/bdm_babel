package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.core.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public abstract class AttendiAttoriStepBuilderTask extends Task {
    private final Logger log = LogManager.getLogger(AttendiAttoriStepBuilderTask.class);

    public static final String TASK_PARAMETER_KEY = AttendiAttoriStepBuilderTask.class.getSimpleName();
    public static final String SKIPPED_STEP = "skipped";
    
    @Override
    public String getTaskType() {
        return getClass().getSimpleName();
    }
}
