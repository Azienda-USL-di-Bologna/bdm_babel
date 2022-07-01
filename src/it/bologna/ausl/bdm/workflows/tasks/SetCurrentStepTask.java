package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.core.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public abstract class SetCurrentStepTask extends Task {
    private final Logger log = LogManager.getLogger(SetCurrentStepTask.class);

    public static final String TASK_PARAMETER_KEY = SetCurrentStepTask.class.getSimpleName();
    //SetCurrentStep
    public static final String ID_APPLICAZIONE_PARAM_NAME = "idapplicazione";
    public static final String TOKEN_APPLICAZIONE_PARAM_NAME = "tokenapplicazione";
    public static final String ID_OGGETTO_PARAM_NAME = "idoggetto";
    public static final String TIPO_OGGETTO_PARAM_NAME = "tipooggetto";
    public static final String CURRENT_STEP_PARAM_NAME = "currentstep";

    @Override
    public String getTaskType() {
        return getClass().getSimpleName();
    }
}
