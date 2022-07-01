package it.bologna.ausl.bdm.workflows.tasks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.bologna.ausl.bdm.core.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public abstract class AttendiAttoreTask extends Task {
    private final Logger log = LogManager.getLogger(AttendiAttoreTask.class);

    public static final String TASK_PARAMETER_KEY = AttendiAttoreTask.class.getSimpleName();

    public static final String ATTORE_ATTESO_PARAM_NAME = "attore_atteso";

    @Override
    public String getTaskType() {
        return getClass().getSimpleName();
    }
    
    @JsonIgnore
    public abstract String getAttore();

    @JsonIgnore
    public abstract void setAttore(String attore);
}
