
package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.core.Task;
import it.bologna.ausl.bdm.utilities.Bag;
import static it.bologna.ausl.bdm.workflows.tasks.SetCurrentStepTask.ID_APPLICAZIONE_PARAM_NAME;
import static it.bologna.ausl.bdm.workflows.tasks.SetCurrentStepTask.TOKEN_APPLICAZIONE_PARAM_NAME;

/**
 *
 * @author Matteo Next
 */
public abstract class DataCreationAPITask extends Task {

    public final String CREATE_DOCUMENT_API_URL_KEY = "create_document_api_url";
    public final String ID_APPLICAZIONE_PARAM_NAME = "id_applicazione";
    public final String TOKEN_APPLICAZIONE_PARAM_NAME = "token_applicazione";
    
    @Override
    public String getTaskType() {
        return getClass().getSimpleName();
    }

    
    
}
