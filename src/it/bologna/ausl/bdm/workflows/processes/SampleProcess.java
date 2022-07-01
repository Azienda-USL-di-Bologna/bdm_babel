/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.bologna.ausl.bdm.workflows.processes;

import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.core.Task;
import it.bologna.ausl.bdm.workflows.tasks.NumerazioneTask0001;
import it.bologna.ausl.bdm.workflows.tasks.SampleTask;
import it.bologna.ausl.bdm.workflows.tasks.SetCurrentStepTask0001;
import it.bologna.ausl.bdm.workflows.tasks.StepBackLogWriterTask0001;
import java.util.Arrays;

/**
 *
 * @author andrea
 */
public class SampleProcess extends BdmProcess {

    @Override
    public void init(Bag parameters) {
        setContext(parameters);
        Step s = new Step("SampleStep", "Sample Process", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.SEQ, Step.StepLogic.ALL));
        addStep(s);
        Task t = new SampleTask();
        s.addTask(t);

        // protocollazione
        Step numerazione = new Step("NumerazioneStep", "Numerazione", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.SEQ));
        NumerazioneTask0001 numerazioneTask = new NumerazioneTask0001();
        numerazione.addTask(numerazioneTask);
//        numerazione.addOnEnterTask(new StepBackLogWriterTask0001());
//        numerazione.addOnEnterTask(new SetCurrentStepTask0001());
        addStep(numerazione);
    }

    @Override
    public String getProcessVersion() {
        return "0.1";
    }

    @Override
    public String getProcessType() {
        return this.getClass().toString();
    }

    @Override
    public void setProcessType(String type) {

    }

}
