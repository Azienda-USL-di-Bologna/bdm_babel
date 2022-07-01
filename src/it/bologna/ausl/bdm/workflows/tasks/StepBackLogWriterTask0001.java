package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.utilities.StepLog;
import java.util.List;

/**
 *
 * @author gdm
 */
public class StepBackLogWriterTask0001 extends StepBackLogWriterTask {

    public StepBackLogWriterTask0001() {
        setAuto(true);
    }

    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {
        status = Bdm.BdmStatus.FINISHED;
        return new Result(status, null, null);
    }

    @Override
    public void stepIn(Context c, Bag p) {
    }

    @Override
    public void undo(Bag runningContext, Bag context, Bag parameters) {
        super.undo(runningContext, context, parameters);

        try {
            Step currentStep = (Step) runningContext.get(BdmProcess.CURRENT_STEP);
            Bag attoreAzioneParams = (Bag) ((Bag) parameters.get(currentStep.getStepType())).get(TASK_PARAMETER_KEY);
            Object attoreAzioneObj = attoreAzioneParams.get(ATTORE_AZIONE);
            String attoreAzione = null;
            if (attoreAzioneObj != null) {
                attoreAzione = (String) attoreAzioneObj;
            }
            if (attoreAzione != null && !attoreAzione.equals("")) {
                BdmProcess currentProcess = (BdmProcess) runningContext.get(BdmProcess.CURRENT_PROCESS);
            addAttoreAzioneInStepLog(currentProcess, attoreAzione);
            }
        }
        catch (Exception ex) {
            //ex.printStackTrace(System.out);
        }
    }
    
    private void addAttoreAzioneInStepLog(BdmProcess currentProcess, String attoreAzione) {
        List<StepLog> stepsLog = currentProcess.getStepsLog();
        
        // lo step corrente è l'ultimo negli stepsLog
        StepLog currentStepLog = stepsLog.get(stepsLog.size() - 1);
        currentStepLog.putInLogData(ATTORE_AZIONE, attoreAzione);
    }

    @Override
    public String getTaskVersion() {
        return "00001";
    }
}
