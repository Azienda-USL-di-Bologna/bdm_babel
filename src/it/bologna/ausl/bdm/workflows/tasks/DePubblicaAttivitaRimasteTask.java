package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.core.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public abstract class DePubblicaAttivitaRimasteTask extends Task {
    private final Logger log = LogManager.getLogger(DePubblicaAttivitaRimasteTask.class);

    public static final String TASK_PARAMETER_KEY = DePubblicaAttivitaRimasteTask.class.getSimpleName();
    
    @Override
    public String getTaskType() {
        return getClass().getSimpleName();
    }
}
