package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.utilities.StepLog;
import it.bologna.ausl.bdm.workflows.exceptions.PubblicaAttivitaException;
import it.bologna.ausl.bdm.workflows.processes.ProtocolloInUscitaProcess;
import it.bologna.ausl.masterchefclient.UpdateBabelParams;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author gdm
 */
public class AttendiAttoreTask0001 extends AttendiAttoreTask {
    private final Logger log = LogManager.getLogger(AttendiAttoreTask0001.class);
    
    public AttendiAttoreTask0001() {
    }

    @Override
    public String getAttore() {
        return (String) super.params.get(ATTORE_ATTESO_PARAM_NAME);
    }

    @Override
    public void setAttore(String attore) {
        super.params.put(ATTORE_ATTESO_PARAM_NAME, attore);
    }

    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {

        status = Bdm.BdmStatus.RUNNING;

        Step currentStep = (Step) runningContext.get(BdmProcess.CURRENT_STEP);
        
        Bag attendiAttoreParams = (Bag) ((Bag) params.get(currentStep.getStepType())).get(TASK_PARAMETER_KEY);
        
//        Bag attendiAttoreParams = (Bag) params.get(currentStep.getStepType() + "_" + TASK_PARAMETER_KEY);
        
//        String attoreRicevuto = (String) attendiAttoreParams.get(ATTORE_ATTESO_PARAM_NAME);
        // controllo se l'attore che mi è stato passato in "params" è quello atteso, nel caso continuo
        if (attendiAttoreParams != null && getAttore().equals((String) attendiAttoreParams.get(ATTORE_ATTESO_PARAM_NAME))) {

            // ok, ho ricevuto l'attore atteso, adesso depubblico l'attività pubblicata corrispondente

            // leggo dal contesto i parametri usati per la pubblicazione, saranno scritti sotto la chiave identificata 
            // dal template "PubblicaAttivitaTask.PUBBLICAZIONE_CORRENTE_PARAMS_TEMPLATE"

            String pubblicazioneCorrenteParamsKey = PubblicaAttivitaTask.PUBBLICAZIONE_CORRENTE_PARAMS_TEMPLATE
                    .replace("[id_step]",currentStep.getStepId())
                    .replace("[attore]", getAttore());
            String updateBabelParamsForInsertString = (String) context.get(pubblicazioneCorrenteParamsKey);
            JSONObject updateBabelParamsForInsertObj = (JSONObject) JSONValue.parse(updateBabelParamsForInsertString);
            UpdateBabelParams updateBabelParamsForInsert = UpdateBabelParams.parse(updateBabelParamsForInsertObj);

//            UpdateBabelParams updateBabelParamsForInsert = (UpdateBabelParams) context.get(pubblicazioneCorrenteParamsKey);
            
            // lancio la depubblicazione

            // mi aspetto che i parametri del masterchef siano scritti nel contesto all'avvio del processo
            String masterChefHost = (String) context.get(ProtocolloInUscitaProcess.MASTERCHEF_HOST);
            String masterChefQueue = (String) context.get(ProtocolloInUscitaProcess.MASTERCHEF_QUEUE);
            
            String returnQueue = getTaskType() + "_" + getTaskId();
            try {
                PubblicaAttivitaTask0001.deleteAttivita(masterChefHost, masterChefQueue, returnQueue, updateBabelParamsForInsert);
                
                // se la depubblicazione è avvenuta correttamente rimuovo i dati dal contesto
                context.remove(pubblicazioneCorrenteParamsKey);
            }
            catch (PubblicaAttivitaException ex) {
                log.error(ex);
//                status = Bdm.BdmStatus.ERROR;            
            }

            status = Bdm.BdmStatus.FINISHED;

            // inserisco nello stepLog corrente l'attore che ha fatto l'azione sotto la voce "attore_atteso" identificato dalla costante "ATTORE_ATTESO_PARAM_NAME"
            BdmProcess currentProcess = (BdmProcess) runningContext.get(BdmProcess.CURRENT_PROCESS);
            addAttoreInStepLog(currentProcess);
            
            return new Result(status, null, null);
        }
        return new Result(status, null, null);
    }

    private void addAttoreInStepLog(BdmProcess currentProcess) {
        List<StepLog> stepsLog = currentProcess.getStepsLog();
        
        // lo step corrente è l'ultimo negli stepsLog
        StepLog currentStepLog = stepsLog.get(stepsLog.size() - 1);
        currentStepLog.putInLogData(ATTORE_ATTESO_PARAM_NAME, getAttore());

    }
    
    @Override
    public void undo(Bag runningContext, Bag context, Bag parameters) {
        super.undo(runningContext, context, parameters);
    }
    
    @Override
    public void stepIn(Context c, Bag p) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTaskVersion() {
        return "0001";
    }
}
