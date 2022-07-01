package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.core.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public abstract class PubblicaAttivitaTask extends Task {
    private final Logger log = LogManager.getLogger(PubblicaAttivitaTask.class);

    public static final String TASK_PARAMETER_KEY = PubblicaAttivitaTask.class.getSimpleName();
    
    public static final String UPDATE_BABEL_PARAMS_PARAM_NAME = "update_babel_params";
    public static final String ATTORE_PUBBLICAZIONE_PARAM_NAME = "attore_pubblicazione";
    public static final String PUBBLICAZIONE_CORRENTE_PARAMS_TEMPLATE = "[id_step]_[attore]";
    public static final int RESULT_QUEUE_EXPIRE_MILLIS = 5000;

    public abstract String getAttorePubblicazione();
    
    @Override
    public String getTaskType() {
        return getClass().getSimpleName();
    }
}
