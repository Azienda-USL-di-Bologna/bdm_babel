package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.workflows.exceptions.PubblicaAttivitaException;
import it.bologna.ausl.bdm.workflows.processes.ProtocolloInUscitaProcess;
import it.bologna.ausl.masterchefclient.UpdateBabelParams;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author gdm
 */
public class DePubblicaAttivitaRimasteTask0001 extends DePubblicaAttivitaRimasteTask {
private final Logger log = LogManager.getLogger(DePubblicaAttivitaRimasteTask0001.class);

    public DePubblicaAttivitaRimasteTask0001() {
        setAuto(true);
    }

    @Override
    /**
     * ciclo su tutti i parametri nel contesto alla ricerca di quelli della pubblicazione delle attività di questo step, per ognuno
     * eseguo la depubblicazione
     */
    public Result execute(Bag runningContext, Bag context, Bag params) {
        // mi aspetto che i parametri del masterchef siano scritti nel contesto all'avvio del processo
        String masterChefHost = (String) context.get(ProtocolloInUscitaProcess.MASTERCHEF_HOST);
        String masterChefQueue = (String) context.get(ProtocolloInUscitaProcess.MASTERCHEF_QUEUE);

        // prendo tutti i parametri del contesto e ne estraggo le chiavi 
        Map<String, Object> contextParameters = context.getParameters();
        Set<String> keys = contextParameters.keySet();
        
        
        // calcolo un'espressione regolare che matcha con i parametri corrispondenti alla pubblicazione ([StepId]_[.*])
        String SerchingPubblicazioneParamNameExpression = PubblicaAttivitaTask.PUBBLICAZIONE_CORRENTE_PARAMS_TEMPLATE
                .replace("[id_step]", ((Step) runningContext.get(BdmProcess.CURRENT_STEP)).getStepId())
                .replace("[attore]", ".*");
        
        // ciclo sulle chiavi
        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
            
            String key = iterator.next();
            // per ogni chiave che matcha con l'espressione regolare eseguo la depubblicazione
            if (key.matches(SerchingPubblicazioneParamNameExpression)) {
                
                String updateBabelParamsForInsertString = (String) context.get(key);
                JSONObject updateBabelParamsForInsertObj = (JSONObject) JSONValue.parse(updateBabelParamsForInsertString);
                UpdateBabelParams updateBabelParamsForInsert = UpdateBabelParams.parse(updateBabelParamsForInsertObj);
                
                if (updateBabelParamsForInsert != null) {
                    String returnQueue = getTaskType() + "_" + getTaskId() + "_" + key;
                    try {
                        PubblicaAttivitaTask0001.deleteAttivita(masterChefHost, masterChefQueue, returnQueue, updateBabelParamsForInsert);
                        
                        // se la depubblicazione è avvenuta correttamente rimuovo i dati dal contesto
                        iterator.remove();
                    }
                    catch (PubblicaAttivitaException ex) {
                        log.error(ex);
//                            status = Bdm.BdmStatus.ERROR;
                    }
                }
            }
        }
        status = Bdm.BdmStatus.FINISHED;
        return new Result(status, null, null);
    }

    @Override
    public void undo(Bag runningContext, Bag context, Bag parameters) {
        super.undo(runningContext, context, parameters);
    }

    @Override
    public void stepIn(Context c, Bag p) {
    }

    @Override
    public String getTaskVersion() {
        return "0001";
    }
}