package it.bologna.ausl.bdm.workflows.processes;

import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.utilities.Bag;

/**
 *
 * @author Matteo Next
 */
public abstract class ProtocolloInUscitaAuto extends ProcessiDocumentali {

    public static final String FILES_TO_MERGE_KEY = "files_to_merge";

    @Override
    public String getProcessType() {
        return "ProtocolloInUscitaAuto";
    }

}
