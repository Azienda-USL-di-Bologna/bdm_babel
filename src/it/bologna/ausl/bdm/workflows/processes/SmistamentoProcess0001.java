package it.bologna.ausl.bdm.workflows.processes;

import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.workflows.tasks.AspettaSpedizioneStepBuilderTask0001;
import it.bologna.ausl.bdm.workflows.tasks.AspettaSpedizioneTask0001;
import it.bologna.ausl.bdm.workflows.tasks.AttendiAttoriStepBuilderTask0001;
import it.bologna.ausl.bdm.workflows.tasks.AvviaSpedizioneTask0001;
import it.bologna.ausl.bdm.workflows.tasks.NumerazioneTask0001;
import it.bologna.ausl.bdm.workflows.tasks.SetCurrentStepTask0001;
import it.bologna.ausl.bdm.workflows.tasks.StepBackLogWriterTask0001;
import java.util.Arrays;

/**
 *
 * @author Giuseppe De Marco (gdm)
 */
public class SmistamentoProcess0001 extends SmistamentoProcess {

    public SmistamentoProcess0001() {
    }

    @Override
    public void init(Bag parameters) {

        context = parameters;
        
        Step segreteria = new Step(Steps.SEGRETERIA.name(), "Segreteria", Step.StepLogic.ANY, Arrays.asList(Step.StepLogic.ANY));
       
        segreteria.addOnEnterTask(new StepBackLogWriterTask0001());
        segreteria.addOnEnterTask(new SetCurrentStepTask0001());
        AttendiAttoriStepBuilderTask0001 sbSegreteria = new AttendiAttoriStepBuilderTask0001();
        segreteria.addTask(sbSegreteria);
        addStep(segreteria);

        Step responsabile = new Step(Steps.RESPONSABILE.name(), "Responsabile", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.SEQ));
        responsabile.addBackwardStep(segreteria.getStepId()); // dal reponsabile si può mandare indietro alla segreteria
        responsabile.addOnEnterTask(new StepBackLogWriterTask0001());
        responsabile.addOnEnterTask(new SetCurrentStepTask0001());
        AttendiAttoriStepBuilderTask0001 sbResponsabile = new AttendiAttoriStepBuilderTask0001();
        responsabile.addTask(sbResponsabile);
        addStep(responsabile);
        
        // step finale. Quando uno smistamento finisce va in assegnazione
        // scrive lo stato di assegnazione sullo smistamento
        Step assegnazione = new Step(Steps.ASSEGNAZIONE.name(), "Assegnazione", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.ANY, Step.StepLogic.SEQ, Step.StepLogic.ALL));
        assegnazione.addOnEnterTask(new StepBackLogWriterTask0001());
        assegnazione.addTask(new SetCurrentStepTask0001());
        addStep(assegnazione);
        
        // aggiungo il forward step dalla segreteria e dal responsabile verso la fine (assegnazione) per quando bisogna terminare lo smistamento.
        segreteria.addForwardStep(assegnazione.getStepId());
        responsabile.addForwardStep(assegnazione.getStepId());
        
    }

    @Override
    public String getProcessVersion() {
        return "0001";
    }
}
