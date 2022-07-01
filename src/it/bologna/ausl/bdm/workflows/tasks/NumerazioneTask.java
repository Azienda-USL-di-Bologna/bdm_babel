package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.core.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public abstract class NumerazioneTask extends Task {
    private final Logger log = LogManager.getLogger(NumerazioneTask.class);

    public static final String TASK_PARAMETER_KEY = NumerazioneTask.class.getSimpleName();
    
    public static final String NUMERO_ASSEGNATO = "numero_assegnato";
    
    public static final String ID_APPLICAZIONE_PARAM_NAME = "idapplicazione";
    public static final String TOKEN_APPLICAZIONE_PARAM_NAME = "tokenapplicazione";
    public static final String ID_OGGETTO_PARAM_NAME = "iddocumento";
    public static final String NUMERAZIONE_SEQ_PARAM_NAME = "nomesequenza";
    
    @Override
    public String getTaskType() {
        return getClass().getSimpleName();
    }
}
