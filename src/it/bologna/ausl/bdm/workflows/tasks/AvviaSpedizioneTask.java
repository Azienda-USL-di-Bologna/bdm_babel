package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.core.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public abstract class AvviaSpedizioneTask extends Task {
    private final Logger log = LogManager.getLogger(AvviaSpedizioneTask.class);

    public static final String TASK_PARAMETER_KEY = AvviaSpedizioneTask.class.getSimpleName();
    
    public static final String LISTA_SPEDIZIONI_PARAM_NAME = "lista_spedizioni";
    public static final String ID_OGGETTO_PARAM_NAME = "id_oggetto";
    public static final String MAIL_PARAM_NAME = "mail";
    public static final String UTENTI_DA_NOTIFICARE_PARAM_NAME = "utenti_da_notificare";
    public static final String DESCRIZIONE_OGGETTO_PARAM_NAME = "descrizione_oggetto";
    public static final String SPEDISCI_GDDOC_PARAM_NAME = "spedisci_gddoc";
    
    // parametri da inserire nel logData
    public static final String QUERY_ESEGUITA = "query_eseguita";
    public static final String SKIPPED_STEP = "skipped";

    @Override
    public String getTaskType() {
        return getClass().getSimpleName();
    }
}
