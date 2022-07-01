package it.bologna.ausl.bdm.workflows.processes;

/**
 *
 * @author andrea
 */
public abstract class SmistamentoProcess extends ProcessiDocumentali {

    public static enum Steps {
        SEGRETERIA,
        RESPONSABILE,
        ASSEGNAZIONE
    }

//    public static final String ID_OGGETTO = "id_oggetto";
//    public static final String TIPO_OGGETTO = "tipo_oggetto";
//    public static final String MASTERCHEF_HOST = "masterchef_host";
//    public static final String MASTERCHEF_QUEUE = "masterchef_queue";
    @Override
    public String getProcessType() {
        return "Smistamento";
    }

}
