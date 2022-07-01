package it.bologna.ausl.bdm.workflows.processes;

import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.workflows.tasks.AspettaSpedizioneStepBuilderTask0001;
import it.bologna.ausl.bdm.workflows.tasks.AspettaSpedizioneTask0001;
import it.bologna.ausl.bdm.workflows.tasks.AvviaSpedizioneTask0001;
import it.bologna.ausl.bdm.workflows.tasks.DataCreationAPITask0001;
import it.bologna.ausl.bdm.workflows.tasks.AttendiJobsTask0001;
import it.bologna.ausl.bdm.workflows.tasks.NumerazioneTask0001;
import it.bologna.ausl.bdm.workflows.tasks.SetCurrentStepTask0001;
import it.bologna.ausl.bdm.workflows.tasks.StepBackLogWriterTask0001;
import java.util.Arrays;

/**
 *
 * @author Matteo Next
 */
public class ProtocolloInUscitaAuto0001 extends ProtocolloInUscitaAuto {

    public static enum Steps {
        NUMERAZIONE,
        ATTENDI_JOBS,
        AVVIA_SPEDIZIONI,
        ASPETTA_SPEDIZIONI,
        FINE
    }

    @Override
    public void init(Bag parameters) {
        setContext(parameters);

        Step numerazione = new Step(Steps.NUMERAZIONE.name(), "Numerazione", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.SEQ));
        NumerazioneTask0001 numerazioneTask = new NumerazioneTask0001();
        numerazione.addOnEnterTask(new StepBackLogWriterTask0001());
        numerazione.addOnEnterTask(new SetCurrentStepTask0001());
        numerazione.addTask(numerazioneTask);
        addStep(numerazione);

        AttendiJobsTask0001 jobTask = new AttendiJobsTask0001();
        Step attendiJobsStep = new Step(Steps.ATTENDI_JOBS.name(), "Step dopo la numerazione", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.SEQ));
        attendiJobsStep.addOnEnterTask(new StepBackLogWriterTask0001());
        attendiJobsStep.addOnEnterTask(new SetCurrentStepTask0001());
        attendiJobsStep.addTask(jobTask);
        addStep(attendiJobsStep);

        Step avviaSpedizione = new Step(Steps.AVVIA_SPEDIZIONI.name(), "Avvia Spedizioni", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.ANY, Step.StepLogic.SEQ, Step.StepLogic.ALL));
        AvviaSpedizioneTask0001 avviaSpedizioneTask = new AvviaSpedizioneTask0001();
        avviaSpedizione.addOnEnterTask(new StepBackLogWriterTask0001());
        avviaSpedizione.addOnEnterTask(new SetCurrentStepTask0001());
        avviaSpedizione.addTask(avviaSpedizioneTask);
        addStep(avviaSpedizione);

        Step aspettaSpedizioni = new Step(Steps.ASPETTA_SPEDIZIONI.name(), "Aspetta Spedizioni", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.ANY, Step.StepLogic.SEQ, Step.StepLogic.ALL));
        AspettaSpedizioneTask0001 sbAspettaSpedizione = new AspettaSpedizioneTask0001();
        aspettaSpedizioni.addOnEnterTask(new StepBackLogWriterTask0001());
        aspettaSpedizioni.addOnEnterTask(new SetCurrentStepTask0001());
        aspettaSpedizioni.addTask(sbAspettaSpedizione);
        addStep(aspettaSpedizioni);

        Step fine = new Step(Steps.FINE.name(), "Fine", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.ANY, Step.StepLogic.SEQ, Step.StepLogic.ALL));
        fine.addOnEnterTask(new StepBackLogWriterTask0001());
        fine.addOnEnterTask(new SetCurrentStepTask0001());
        addStep(fine);

    }

    @Override
    public String getProcessVersion() {
        return "0001";
    }

}
