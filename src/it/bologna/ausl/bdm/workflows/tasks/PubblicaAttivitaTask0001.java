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
import it.bologna.ausl.masterchefclient.WorkerData;
import it.bologna.ausl.redis.RedisClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public class PubblicaAttivitaTask0001 extends PubblicaAttivitaTask {
    private final Logger log = LogManager.getLogger(PubblicaAttivitaTask0001.class);

    private UpdateBabelParams updateBabelParamsForInsert;
    private String pubblicazioneCorrenteParamsKey;
    private String attorePubblicazione;
    private String masterChefHost;
    private String masterChefQueue;

    public PubblicaAttivitaTask0001() {
        setAuto(true);
    }

    public UpdateBabelParams getUpdateBabelParamsForInsert() {
        return updateBabelParamsForInsert;
    }

    public void setUpdateBabelParamsForInsert(UpdateBabelParams updateBabelParamsForInsert) {
        this.updateBabelParamsForInsert = updateBabelParamsForInsert;
    }

    public String getPubblicazioneCorrenteParamsKey() {
        return pubblicazioneCorrenteParamsKey;
    }

    public void setPubblicazioneCorrenteParamsKey(String pubblicazioneCorrenteParamsKey) {
        this.pubblicazioneCorrenteParamsKey = pubblicazioneCorrenteParamsKey;
    }

    @Override
    public String getAttorePubblicazione() {
        return attorePubblicazione;
    }

    public void setAttorePubblicazione(String attorePubblicazione) {
        this.attorePubblicazione = attorePubblicazione;
    }

    public String getMasterChefHost() {
        return masterChefHost;
    }

    public void setMasterChefHost(String masterChefHost) {
        this.masterChefHost = masterChefHost;
    }

    public String getMasterChefQueue() {
        return masterChefQueue;
    }

    public void setMasterChefQueue(String masterChefQueue) {
        this.masterChefQueue = masterChefQueue;
    }

    @Override
    public void init(Bag p) {
        super.init(p);
        attorePubblicazione = (String) super.getParams().get(ATTORE_PUBBLICAZIONE_PARAM_NAME);
//        JSONObject ubpObj = (JSONObject) super.getParams().get(UPDATE_BABEL_PARAMS_PARAM_NAME);
//        updateBabelParamsForInsert = UpdateBabelParams.parse(ubpObj);
        updateBabelParamsForInsert = (UpdateBabelParams) super.getParams().get(UPDATE_BABEL_PARAMS_PARAM_NAME);
    }

    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {
        // provo a leggere i parametri da "params" perché sono i più aggiornati (utile nel caso di step "SEQ"),
        // altrimenti se non ne trovo provo a leggere quelli inseriti dallo StepBuilder (in super.getParams())
        Step currentStep = (Step) runningContext.get(BdmProcess.CURRENT_STEP);

        try {
            Bag taskParams = (Bag) ((Bag) params.get(currentStep.getStepType())).get(TASK_PARAMETER_KEY);
            if (taskParams != null) {
                UpdateBabelParams ubp = (UpdateBabelParams) taskParams.get(attorePubblicazione);
                if (ubp != null)
                    updateBabelParamsForInsert = ubp;
//                if (ubpObj != null)
//                    updateBabelParamsForInsert = UpdateBabelParams.parse(ubpObj);
            }
        }
        catch (NullPointerException ex) {
        }

        if (updateBabelParamsForInsert != null) {
            // mi aspetto che i parametri del masterchef siano scritti nel contesto all'avvio del processo
            masterChefHost = (String) context.get(ProtocolloInUscitaProcess.MASTERCHEF_HOST);
            masterChefQueue = (String) context.get(ProtocolloInUscitaProcess.MASTERCHEF_QUEUE);
            try {

                // calcolo una coda temporanea per la scrittura del risultato della pubblicazione e lancio la pubblicazione
                String returnQueue = getTaskType() + "_" + getTaskId();
                runUpdateBabel(masterChefHost, masterChefQueue, returnQueue, updateBabelParamsForInsert);
                
                // inserisco nello stepLog corrente l'attore che ha fatto l'azione sotto la voce "attore_atteso" identificato dalla costante "ATTORE_ATTESO_PARAM_NAME"
                BdmProcess currentProcess = (BdmProcess) runningContext.get(BdmProcess.CURRENT_PROCESS);
                addAttorePubblicazioneInStepLog(currentProcess);
                
                // scrivo i parametri per la pubblicazione nel contesto in modo che il task AttendiAttore corrispondente a questa pubblicazione
                // li possa leggere per eseguire la depubblicazione una volta che l'attore ha fatto (quando riceve lo StepOn)
                // Saranno scritti nel contesto sotto la chiave: "(idStep)_(attore)" identificato dal template PUBBLICAZIONE_CORRENTE_PARAMS_TEMPLATE
                pubblicazioneCorrenteParamsKey = PUBBLICAZIONE_CORRENTE_PARAMS_TEMPLATE
                        .replace("[id_step]",((Step) runningContext.get(BdmProcess.CURRENT_STEP)).getStepId())
                        .replace("[attore]", attorePubblicazione);
                context.put(pubblicazioneCorrenteParamsKey, updateBabelParamsForInsert.toString());
                status = Bdm.BdmStatus.FINISHED;
            }
            catch (PubblicaAttivitaException ex) {
                log.error(ex);
//                status = Bdm.BdmStatus.ERROR;
            }
            catch (Exception ex) {
                log.error(ex);
                status = Bdm.BdmStatus.ERROR;
            }
        }
        else
            status = Bdm.BdmStatus.FINISHED;
        return new Result(status, null, null);
    }

    public static void runUpdateBabel(String masterChefHost, String masterChefQueue, String returnQueue, UpdateBabelParams updateBabelParamsForInsert) throws PubblicaAttivitaException {
        
        WorkerData wd = new WorkerData("bdm", "1", returnQueue, RESULT_QUEUE_EXPIRE_MILLIS);
        wd.addNewJob("1", "", updateBabelParamsForInsert);

        RedisClient rd = new RedisClient(masterChefHost, null);
        try {
            rd.put(wd.getStringForRedis(), masterChefQueue);
        }
        catch (Exception ex) {
            throw new PubblicaAttivitaException("Errore nella pubblicazione/depubblicazione dell'attività", ex);
        }
    }
    

    private void addAttorePubblicazioneInStepLog(BdmProcess currentProcess) {
        List<StepLog> stepsLog = currentProcess.getStepsLog();
        
        // lo step corrente è l'ultimo negli stepsLog
        StepLog currentStepLog = stepsLog.get(stepsLog.size() - 1);
        List<String> attoriPubblicazioniList = null;
        try {
            attoriPubblicazioniList = (List<String>) currentStepLog.getFromLogData(ATTORE_PUBBLICAZIONE_PARAM_NAME);
        }
        catch (Exception ex) {
//            log.error(ex);
        }
        if (attoriPubblicazioniList == null) {
            attoriPubblicazioniList = new ArrayList<>();
            currentStepLog.putInLogData(ATTORE_PUBBLICAZIONE_PARAM_NAME, attoriPubblicazioniList);
        }
        attoriPubblicazioniList.add(attorePubblicazione);
    }
    @Override
    public void undo(Bag runningContext, Bag context, Bag parameters) {
        try {
            super.undo(runningContext, context, parameters);
            String returnQueue = getTaskType() + "_" + getTaskId();
            deleteAttivita(masterChefHost, masterChefQueue, returnQueue, updateBabelParamsForInsert);
            
            // se la depubblicazione è avvenuta correttamente rimuovo i dati dal contesto
            context.remove(pubblicazioneCorrenteParamsKey);

//            status = Bdm.BdmStatus.FINISHED;
        }
        catch (PubblicaAttivitaException ex) {
            log.error(ex);
//                status = Bdm.BdmStatus.ERROR;
        }
    }

    public static void deleteAttivita(String masterChefHost, String masterChefQueue, String returnQueue, UpdateBabelParams updateBabelParamsForInsert) throws PubblicaAttivitaException {
        String idApplicazione = updateBabelParamsForInsert.getIdApplicazione();
        String tokenApplicazione = updateBabelParamsForInsert.getTokenApplicazione();
        String setAttivita = updateBabelParamsForInsert.getSetAttivita();
        String archiviazione = updateBabelParamsForInsert.getArchiviazione();
        String accessoEsclusivo = updateBabelParamsForInsert.getAccessoEsclusivo();
//        String actionType = updateBabelParams.getActionType();
        List listaAttivita = updateBabelParamsForInsert.getListaAttivita();

        UpdateBabelParams updateBabelParamsForDelete = new UpdateBabelParams(idApplicazione, tokenApplicazione, setAttivita, archiviazione, accessoEsclusivo, UpdateBabelParams.DELETE);

        listaAttivita.stream().forEach((attivitaObj) -> {
            Map attivita = (Map)attivitaObj;
            updateBabelParamsForDelete.addAttivita((String) attivita.get("idattivita"), null, null, null, null, 
                    null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
                    null, null, null, null, null, null, null, null, "false", null);
        });
        runUpdateBabel(masterChefHost, masterChefQueue, returnQueue, updateBabelParamsForDelete);
    }

    @Override
    public void stepIn(Context c, Bag p) {
    }

    @Override
    public String getTaskVersion() {
        return "0001";
    }

}
