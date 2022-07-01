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
public class ProtocolloInUscitaProcess0001 extends ProtocolloInUscitaProcess {

    public ProtocolloInUscitaProcess0001() {
    }

    @Override
    public void init(Bag parameters) {

        context = parameters;
        
        Step redazione = new Step(Steps.REDAZIONE.name(), "Redazione", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.SEQ, Step.StepLogic.ALL));
        redazione.addOnEnterTask(new StepBackLogWriterTask0001());
        redazione.addOnEnterTask(new SetCurrentStepTask0001());
        AttendiAttoriStepBuilderTask0001 sbRedazione = new AttendiAttoriStepBuilderTask0001();
        redazione.addTask(sbRedazione);
        addStep(redazione);

        Step pareri = new Step(Steps.PARERI.name(), "Pareri", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.SEQ, Step.StepLogic.ALL));
        pareri.addBackwardStep(redazione.getStepId());
        pareri.addBackwardStep(pareri.getStepId());
        pareri.addOnEnterTask(new StepBackLogWriterTask0001());
        pareri.addOnEnterTask(new SetCurrentStepTask0001());
        AttendiAttoriStepBuilderTask0001 sbPareri = new AttendiAttoriStepBuilderTask0001();
        pareri.addTask(sbPareri);
        addStep(pareri);

        Step controlloSegreteria = new Step(Steps.CONTROLLO_SEGRETERIA.name(), "Controllo segreteria", Step.StepLogic.ANY, Arrays.asList(Step.StepLogic.ANY));
        controlloSegreteria.addBackwardStep(redazione.getStepId());
        controlloSegreteria.addBackwardStep(pareri.getStepId());
        controlloSegreteria.addOnEnterTask(new StepBackLogWriterTask0001());
        controlloSegreteria.addOnEnterTask(new SetCurrentStepTask0001());
        AttendiAttoriStepBuilderTask0001 sbControlloSegreteria = new AttendiAttoriStepBuilderTask0001();
        controlloSegreteria.addTask(sbControlloSegreteria);
        addStep(controlloSegreteria);

        Step firma = new Step(Steps.FIRMA.name(), "Firma", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.SEQ, Step.StepLogic.ALL));
        firma.addBackwardStep(redazione.getStepId());
        firma.addBackwardStep(pareri.getStepId());
        firma.addBackwardStep(firma.getStepId());
        firma.addOnEnterTask(new StepBackLogWriterTask0001());
        firma.addOnEnterTask(new SetCurrentStepTask0001());
        AttendiAttoriStepBuilderTask0001 sbFirma = new AttendiAttoriStepBuilderTask0001();
        firma.addTask(sbFirma);
        addStep(firma);

        // protocollazione
        Step numerazione = new Step(Steps.NUMERAZIONE.name(), "Numerazione", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.SEQ));
        NumerazioneTask0001 numerazioneTask = new NumerazioneTask0001();
        numerazione.addTask(numerazioneTask);
        numerazione.addOnEnterTask(new StepBackLogWriterTask0001());
        numerazione.addOnEnterTask(new SetCurrentStepTask0001());
        addStep(numerazione);

        // avvia spedizione
        Step avviaSpedizione = new Step(Steps.AVVIA_SPEDIZIONI.name(), "Avvia Spedizioni", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.ANY, Step.StepLogic.SEQ, Step.StepLogic.ALL));
        avviaSpedizione.addOnEnterTask(new StepBackLogWriterTask0001());
        avviaSpedizione.addOnEnterTask(new SetCurrentStepTask0001());
        AvviaSpedizioneTask0001 avviaSpedizioneTask = new AvviaSpedizioneTask0001();
        avviaSpedizione.addTask(avviaSpedizioneTask);
        addStep(avviaSpedizione);

        // spedizione manuale
        Step spedizioneManuale = new Step(Steps.SPEDIZIONE_MANUALE.name(), "Spedizione Manuale", Step.StepLogic.ANY, Arrays.asList(Step.StepLogic.ANY));
        spedizioneManuale.addOnEnterTask(new StepBackLogWriterTask0001());
        spedizioneManuale.addOnEnterTask(new SetCurrentStepTask0001());
        AttendiAttoriStepBuilderTask0001 sbSpedizioneManuale = new AttendiAttoriStepBuilderTask0001();
        spedizioneManuale.addTask(sbSpedizioneManuale);
        addStep(spedizioneManuale);
        
        // aspetta spedizioni
        // il task AspettaSpedizioneStepBuilderTask inserisce, a run time, il task ApettaSpedizioniTask solo se il task AvviaSpedizioneTask ha inserito almeno una spedizione
        // se il task ApettaSpedizioniTask viene inserito, il processo si ferma in attesa di uno stepon(che verrà eseguito dallo spedizioniere una volta completata la spedizione)
        // se il task ApettaSpedizioniTask non viene inserito lo step AspettaSpedizioni sarà saltato
        Step aspettaSpedizioni = new Step(Steps.ASPETTA_SPEDIZIONI.name(), "Aspetta Spedizioni", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.ANY, Step.StepLogic.SEQ, Step.StepLogic.ALL));
        aspettaSpedizioni.addOnEnterTask(new StepBackLogWriterTask0001());
        aspettaSpedizioni.addOnEnterTask(new SetCurrentStepTask0001());
        AspettaSpedizioneStepBuilderTask0001 sbAspettaSpedizione = new AspettaSpedizioneStepBuilderTask0001();
        aspettaSpedizioni.addTask(sbAspettaSpedizione);
//        AspettaSpedizioneTask0001 aspettaSpedizioneTask = new AspettaSpedizioneTask0001();
//        aspettaSpedizioni.addTask(aspettaSpedizioneTask);
        addStep(aspettaSpedizioni);
        
        // step di fine
        // scrive lo stato di fine sul documento
        Step fine = new Step(Steps.FINE.name(), "Fine", Step.StepLogic.SEQ, Arrays.asList(Step.StepLogic.ANY, Step.StepLogic.SEQ, Step.StepLogic.ALL));
        fine.addOnEnterTask(new StepBackLogWriterTask0001());
        fine.addTask(new SetCurrentStepTask0001());
        addStep(fine);
        
    }

    @Override
    public String getProcessVersion() {
        return "0001";
    }
}
