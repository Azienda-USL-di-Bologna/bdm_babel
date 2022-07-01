package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.utilities.StepLog;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author andrea
 */
public class AttendiAttoriStepBuilderTask0001 extends AttendiAttoriStepBuilderTask {
Logger log = LogManager.getLogger(AttendiAttoriStepBuilderTask0001.class);

//    public static final String STEP_BUILDING_PARAMS_NAME = "attori"; // nome del parametro da leggere per creare i tasks
//    public static final String STEP_TYPE_PARAM_NAME = "step_type"; // nome del parametro che contiene lo step che voglio creare, serve perché voglio evitare di crearlo se non ci sono attori che riguardano lo step in cui sto andando

    public AttendiAttoriStepBuilderTask0001() {
        setAuto(true);
    }

    /**
     * 
     * @param runningContext
     * @param context
     * @param params conterrà un json nel formato seguente:
     * stepType (es REDAZIONE,PARERI,...) : AttendiAttoriStepBuilderTask0001 {
     *                                          [
     *                                             {
     *                                                 attore_atteso = "id_attore"
     *                                                 update_babel_params: {update_babel_params}
     *                                             },
     *                                         ...
     *                                           ]
     *                                      }
     *
     * @return 
     */
    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {
        status = Bdm.BdmStatus.RUNNING;

        Step currentStep = (Step) runningContext.get(BdmProcess.CURRENT_STEP);
        
//        clearStep(currentStep);
        
        List<Bag> stepBuildingParams = null;
        try {
            stepBuildingParams = ((List<Bag>) ((Bag) params.get(currentStep.getStepType())).get(TASK_PARAMETER_KEY));
        }
        catch (Exception ex) {
            log.error(ex);
        }

        boolean skippedStep = false;
        if (stepBuildingParams != null) {
        
            for (Bag stepParams : stepBuildingParams) {

                PubblicaAttivitaTask0001 pubblicatoreAttivitaTask = new PubblicaAttivitaTask0001();
                Bag pubblicaAttivitaParams = new Bag();
                pubblicaAttivitaParams.put(PubblicaAttivitaTask.UPDATE_BABEL_PARAMS_PARAM_NAME, stepParams.get(PubblicaAttivitaTask.UPDATE_BABEL_PARAMS_PARAM_NAME));
                pubblicaAttivitaParams.put(PubblicaAttivitaTask.ATTORE_PUBBLICAZIONE_PARAM_NAME, stepParams.get(AttendiAttoreTask.ATTORE_ATTESO_PARAM_NAME));
                pubblicatoreAttivitaTask.init(pubblicaAttivitaParams);
                currentStep.addTask(pubblicatoreAttivitaTask);

                AttendiAttoreTask0001 attoreTask = new AttendiAttoreTask0001();
                Bag taskParams = new Bag();
                taskParams.put(AttendiAttoreTask.ATTORE_ATTESO_PARAM_NAME, stepParams.get(AttendiAttoreTask.ATTORE_ATTESO_PARAM_NAME));
                attoreTask.init(taskParams);
                currentStep.addTask(attoreTask);
            }

            // inserisco un task da eseguire all'usita per la depubblicazione delle attività, è utile solo se lo step è di tipo ANY, negli altri Step non depubblicaherà niente
            DePubblicaAttivitaRimasteTask0001 dePubblicaAttivitaRimasteTask = new DePubblicaAttivitaRimasteTask0001();
            currentStep.addOnExitTask(dePubblicaAttivitaRimasteTask);
        }
        else {
            skippedStep = true;
            log.info("no data, the step will be skipped...");
        }

        BdmProcess currentProcess = (BdmProcess) runningContext.get(BdmProcess.CURRENT_PROCESS);
        addSkipInformationInStepLog(currentProcess, skippedStep);
        status = Bdm.BdmStatus.FINISHED;
        return new Result(status, null, null);
    }

    private void addSkipInformationInStepLog(BdmProcess currentProcess, boolean skippedStep) {
        List<StepLog> stepsLog = currentProcess.getStepsLog();

        // lo step corrente è l'ultimo negli stepsLog
        StepLog currentStepLog = stepsLog.get(stepsLog.size() - 1);
        currentStepLog.putInLogData(SKIPPED_STEP, skippedStep);
    }
    
    private void clearCurrentStep(Step currentStep) {
        
        // tolgo tutti i task escluso me stesso
        if (currentStep.getTaskList() != null)
            currentStep.getTaskList().removeIf(t -> !t.getTaskId().equals(getTaskId()));
//            currentStep.getTaskList().removeIf(t -> !AttendiAttoriStepBuilderTask.class.isAssignableFrom(t.getClass()));
        
        // tolgo tutti i task in entrata
//        step.getEnterTaskList().clear();
        
        // tolgo tutti i task in uscita
        if (currentStep.getExitTaskList() != null)
            currentStep.getExitTaskList().clear();
    }

    @Override
    public void undo(Bag runningContext, Bag context, Bag parameters) {
        super.undo(runningContext, context, parameters);
        Step currentStep = (Step) runningContext.get(BdmProcess.CURRENT_STEP);
        clearCurrentStep(currentStep);
    }
    
    @Override
    public void stepIn(Context c, Bag p) {
    }

    @Override
    public String getTaskVersion() {
        return "0001";
    }

}
