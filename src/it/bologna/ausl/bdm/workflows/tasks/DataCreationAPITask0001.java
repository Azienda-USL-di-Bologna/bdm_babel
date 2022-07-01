/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.bologna.ausl.bdm.workflows.tasks;


import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.processes.utils.UtilityFunctions;
import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.workflows.processes.ProcessiDocumentali;
import static it.bologna.ausl.bdm.workflows.tasks.SetCurrentStepTask.ID_APPLICAZIONE_PARAM_NAME;
import static it.bologna.ausl.bdm.workflows.tasks.SetCurrentStepTask.TOKEN_APPLICAZIONE_PARAM_NAME;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Matteo Next
 */
public class DataCreationAPITask0001 extends DataCreationAPITask {  //Task per la creazione dei dati 
    private final Logger log = LogManager.getLogger(DataCreationAPITask0001.class);
    private final String TASK_DATA_KEY = "data";
    
    private String idApplicazione;
    private String tokenApplicazione;

    public String getIdApplicazione() {
        return idApplicazione;
    }

    public void setIdApplicazione(String idApplicazione) {
        this.idApplicazione = idApplicazione;
    }

    public String getTokenApplicazione() {
        return tokenApplicazione;
    }

    public void setTokenApplicazione(String tokenApplicazione) {
        this.tokenApplicazione = tokenApplicazione;
    }
    
    
    public DataCreationAPITask0001() {
        super.setAuto(true);
    }

    @Override
    public String getTaskVersion() {
        return "0001";
    }

    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {
        status = Bdm.BdmStatus.RUNNING;
        try {
            Step currentStep = (Step) runningContext.get(BdmProcess.CURRENT_STEP);

            idApplicazione = (String) context.get(ProcessiDocumentali.ID_APPLICAZIONE);
            tokenApplicazione = (String) context.get(ProcessiDocumentali.TOKEN_APPLICAZIONE);


            Map<String, byte[]> requestParams = new HashMap<>();
            requestParams.put(ID_APPLICAZIONE_PARAM_NAME, idApplicazione.getBytes());
            requestParams.put(TOKEN_APPLICAZIONE_PARAM_NAME, tokenApplicazione.getBytes());
            Bag stepParams = (Bag) params.get(currentStep.getStepType());
            Bag taskParams = (Bag) stepParams.get(getTaskType());
            Map<String, Object> taskData = (Map<String, Object>) taskParams.get(TASK_DATA_KEY);
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(taskData);
            requestParams.put(TASK_DATA_KEY, byteOut.toByteArray());
            String idDocCreato = UtilityFunctions.sendHttpMessage(CREATE_DOCUMENT_API_URL_KEY, null, null, requestParams, "POST", null);
            status = Bdm.BdmStatus.FINISHED;
            Bag b = new Bag();
            b.put("id_doc", idDocCreato);
            context.put(ProcessiDocumentali.ID_OGGETTO, idDocCreato);
            
            return new Result(status, b, "OK");
        }
        catch (Exception ex) {
            log.error(ex);
            status = Bdm.BdmStatus.ERROR;
            return new Result(status, null, ex.toString());
        }
        

    }

    @Override
    public void stepIn(Context c, Bag p) {
        
    }
    
}
