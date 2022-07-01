package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.core.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public abstract class StepBackLogWriterTask extends Task {
    private final Logger log = LogManager.getLogger(StepBackLogWriterTask.class);

    public static final String TASK_PARAMETER_KEY = StepBackLogWriterTask.class.getSimpleName();

    public static final String ATTORE_AZIONE = "attore_azione";

    @Override
    public String getTaskType() {
        return getClass().getSimpleName();
    }
}
