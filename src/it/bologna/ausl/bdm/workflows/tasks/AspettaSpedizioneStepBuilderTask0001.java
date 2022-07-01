package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.utilities.StepLog;
import it.bologna.ausl.bdm.workflows.processes.ProcessiDocumentali;
import static it.bologna.ausl.bdm.workflows.tasks.AspettaSpedizioneStepBuilderTask.SKIPPED_STEP;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author andrea
 */
public class AspettaSpedizioneStepBuilderTask0001 extends AspettaSpedizioneStepBuilderTask {
Logger log = LogManager.getLogger(AspettaSpedizioneStepBuilderTask0001.class);

    public AspettaSpedizioneStepBuilderTask0001() {
        setAuto(true);
    }

    /**
     * 
     * @param runningContext
     * @param context
     * @param params
     *
     * @return 
     */
    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {
        status = Bdm.BdmStatus.RUNNING;

        Step currentStep = (Step) runningContext.get(BdmProcess.CURRENT_STEP);
        
        boolean skippedStep = false;
        boolean avvioSpedizioneEseguito = false;
        try {
            avvioSpedizioneEseguito = (boolean) context.get(ProcessiDocumentali.AVVIO_SPEDIZIONE_ESEGUITO);
        }
        catch (Exception ex) {
        }
        
        log.info("avvioSpedizioneEseguito: " + avvioSpedizioneEseguito);
        if (avvioSpedizioneEseguito) {
            currentStep.addTask(new AspettaSpedizioneTask0001());
        }
        else {
            skippedStep = true;
            log.info("avvioSpedizioneEseguito is false or not present, the step will be skipped...");
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
    
//    private void clearCurrentStep(Step currentStep) {
//        
//        // tolgo tutti i task escluso me stesso
//        if (currentStep.getTaskList() != null)
//            currentStep.getTaskList().removeIf(t -> !t.getTaskId().equals(getTaskId()));
////            currentStep.getTaskList().removeIf(t -> !AttendiAttoriStepBuilderTask.class.isAssignableFrom(t.getClass()));
//        
//        // tolgo tutti i task in entrata
////        step.getEnterTaskList().clear();
//        
//        // tolgo tutti i task in uscita
//        if (currentStep.getExitTaskList() != null)
//            currentStep.getExitTaskList().clear();
//    }
//
//    @Override
//    public void undo(Bag runningContext, Bag context, Bag parameters) {
//        super.undo(runningContext, context, parameters);
//        Step currentStep = (Step) runningContext.get(BdmProcess.CURRENT_STEP);
//        clearCurrentStep(currentStep);
//    }
    
    @Override
    public void stepIn(Context c, Bag p) {
    }

    @Override
    public String getTaskVersion() {
        return "0001";
    }

}
