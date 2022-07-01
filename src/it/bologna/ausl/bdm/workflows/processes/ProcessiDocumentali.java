package it.bologna.ausl.bdm.workflows.processes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.bologna.ausl.bdm.core.Bdm.BdmStatus;
import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.core.Task;
import it.bologna.ausl.bdm.exception.ProcessWorkFlowException;
import it.bologna.ausl.bdm.processes.utils.ProcessoDucumentaleStepInformation;
import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.utilities.StepLog;
import it.bologna.ausl.bdm.workflows.tasks.AttendiAttoreTask;
import it.bologna.ausl.bdm.workflows.tasks.AttendiAttoriStepBuilderTask;
import it.bologna.ausl.bdm.workflows.tasks.NumerazioneTask;
import it.bologna.ausl.bdm.workflows.tasks.PubblicaAttivitaTask;
import it.bologna.ausl.bdm.workflows.tasks.StepBackLogWriterTask;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;

/**
 *
 * @author gdm
 */
public abstract class ProcessiDocumentali extends BdmProcess {

    public static enum Steps {
        NUMERAZIONE,
        FINE
    }
    
    public static final String ID_APPLICAZIONE = "id_applicazione";
    public static final String TOKEN_APPLICAZIONE = "token_applicazione";
    public static final String ID_OGGETTO = "id_oggetto";
    public static final String TIPO_OGGETTO = "tipo_oggetto";
    public static final String MASTERCHEF_HOST = "masterchef_host";
    public static final String MASTERCHEF_QUEUE = "masterchef_queue";
    public static final String OGGETTO = "oggetto"; //oggetto del documento
    public static final String CODICE_REGISTRO = "codice_registro"; // codice del registro (es. PG)

    public static final String NUMERAZIONE_SERVICE_URL = "numerazione_service_url";
    public static final String NUMERAZIONE_SEQ = "numerazione_seq";
    public static final String NUMERAZIONE_RETRY_TIME = "numerazione_retry_time";
    public static final String NUMERAZIONE_MAX_RETRY = "numerazione_max_retry";
    
    public static final String NUMERAZIONE_NUMERO_GENERATO = "numerazione_numero_generato";
    public static final String NUMERAZIONE_ANNO_GENERATO = "numerazione_anno_generato";
    
    public static final String SET_CURRENT_STEP_SERVLET_URL = "set_current_step_servlet_url";
    
    public static final String DB_URI = "db_uri";
    public static final String AVVIO_SPEDIZIONE_ESEGUITO = "avvio_spedizione_eseguito";
    

    @Override
    public String getProcessType() {
        return getClass().getSimpleName();
    }

    /**
     * torna se l'utente ha già eseguito l'azione in un certo step
     * @param user
     * @param step
     * @return "true" se l'utente ha già eseguito l'azione in un certo step, "false" altrimenti
     */
    @JsonIgnore
    public boolean isUserFinished(String user, Step step) {
        List<AttendiAttoreTask> attendiAttoreTaks = step.getTaskList(AttendiAttoreTask.class);
//        return attendiAttoreTaks.stream().anyMatch(t -> ((t.getStatus() == BdmStatus.NOT_STARTED || t.getStatus() == BdmStatus.RUNNING) && t.getAttore().equals(user)));
//        return attendiAttoreTaks.stream().anyMatch(
//                t -> 
//                        (
//                            (
//                                t.getStatus() == BdmStatus.FINISHED || 
//                                t.getStatus() == BdmStatus.ERROR || 
//                                t.getStatus() == BdmStatus.ABORTED
//                            ) 
//                            && t.getAttore().equals(user)
//                        )
//                );
        for (AttendiAttoreTask t : attendiAttoreTaks) {
            if ((t.getStatus() == BdmStatus.FINISHED || t.getStatus() == BdmStatus.ERROR || t.getStatus() == BdmStatus.ABORTED) && 
                    t.getAttore().equals(user)) {
                return true;
            }
        }
        return false;
    }

    /**
     * torna se l'utente può agire nello step corrente
     * @param user
     * @return "true" se l'utente può agire nello step corrente, "false" altrimenti
     */
    @JsonIgnore
    public boolean isActionAllowed(String user) {
        if (status == BdmStatus.RUNNING) {
            Step currentStep = getCurrentStep();
            List<AttendiAttoreTask> attendiAttoreTaks = currentStep.getTaskList(AttendiAttoreTask.class);
            int userListPosition = findUserIndex(user, attendiAttoreTaks);

            if (currentStep.getStepLogic() == Step.StepLogic.SEQ) {
                if (userListPosition == -1)
                    return false;
                else if (userListPosition == 0)
                    return !isUserFinished(user, currentStep);
                else {
                    for (int i = 0; i < userListPosition; i++) {
                        String listUser = attendiAttoreTaks.get(i).getAttore();
                        if (!isUserFinished(listUser, currentStep))
                            return false;
                    }
                    return !isUserFinished(user, currentStep);
                }
            }
            else {
                return userListPosition >= 0 && !isUserFinished(user, currentStep);
            }
        }
        else
            return false;
    }

    /**
     * torna un oggetto che descrive lo step precedentemente eseguito
     * @return un oggetto che descrive lo step precedentemente eseguito
     */
    @JsonIgnore
    public ProcessoDucumentaleStepInformation getPreviousStepInformation() {
        ProcessoDucumentaleStepInformation stepInfo = null;
        
        List<StepLog> stepsLogList = getStepsLog();
        if (stepsLogList != null && stepsLogList.size() > 1) {
            // prendo il penultimo step log perché è quello che descrive lo step precendete (a meno che gli ultimi 2 non siano dello stesso step, in quel caso prendo l'ultimo)
            // TODO: trovare un modo migliore di farlo che questo è brutto
            StepLog previousStepLog = null;

            int lastStepLogIndex;

            if (stepsLogList.get(stepsLogList.size() - 1).getStepType().equals(stepsLogList.get(stepsLogList.size() - 2).getStepType()))
                lastStepLogIndex = stepsLogList.size() - 1;
            else
                lastStepLogIndex = stepsLogList.size() - 2;

            String stepId = null;
            String stepType = null;
            DateTime executionDate = null;
            List<String> attoriPubblicazioni = null;
            String stepOnUser = null;
            String stepToUser = null;

            boolean found = false;
            do {
                try {
                    previousStepLog = stepsLogList.get(lastStepLogIndex);
                }
                catch (Exception ex) {
                }
                if (previousStepLog != null) {
                    Bag logData = previousStepLog.getLogData();
                    if (logData != null) {
                        boolean skippedStep = false;
                        try {
                            skippedStep = (boolean) logData.get(AttendiAttoriStepBuilderTask.SKIPPED_STEP);
                        }
                        catch (Exception ex) {
                        }

                        if (!skippedStep) {
                            found = true;
                            try {
                                attoriPubblicazioni = (List<String>) logData.get(PubblicaAttivitaTask.ATTORE_PUBBLICAZIONE_PARAM_NAME);
                            }
                            catch (Exception ex) {
                            }
                            stepType = previousStepLog.getStepType();
                            executionDate = previousStepLog.getExecutionDate();
                            stepOnUser = (String) logData.get(AttendiAttoreTask.ATTORE_ATTESO_PARAM_NAME);
                            stepToUser = (String) logData.get(StepBackLogWriterTask.ATTORE_AZIONE);
                        }
                    } 
                }
                lastStepLogIndex --;
            }
            while (!found && lastStepLogIndex >= 0);
            
            if (found)
                stepInfo = new ProcessoDucumentaleStepInformation(stepId, stepType, executionDate, attoriPubblicazioni, stepOnUser, stepToUser);
        }
        return stepInfo;
    }

    /**
     * torna un oggetto che descrive lo step in cui si arriverà a seguito di uno step-on
     * @param stepOnUser l'utente che ha fatto lo step-on
     * @param stepOnParams i parametri passati allo step-on (servono per calcolare gli utenti dei vari step)
     * @return un oggetto che descrive lo step in cui si arriverà a seguito di uno step-on
     */
    @JsonIgnore
    public ProcessoDucumentaleStepInformation getNextStepInformation(String stepOnUser, Bag stepOnParams) {
        ProcessoDucumentaleStepInformation stepInformation = null;

        boolean found = false;
        Step nextStep = getCurrentStep();
        
        boolean currentStepDone = true;
        if (nextStep.getStepLogic() != Step.StepLogic.ANY) {
            List<AttendiAttoreTask> attendiAttoreTaskList = nextStep.getTaskList(AttendiAttoreTask.class);

            if (attendiAttoreTaskList != null && !attendiAttoreTaskList.isEmpty()) {
                List<AttendiAttoreTask> attendiAttoreTaskListFiltered = attendiAttoreTaskList.stream().filter(t -> (!t.getAttore().equals(stepOnUser) && (t.getStatus() == BdmStatus.NOT_STARTED || t.getStatus() == BdmStatus.RUNNING))).collect(Collectors.toList());
                
                if (attendiAttoreTaskListFiltered != null && !attendiAttoreTaskListFiltered.isEmpty()) {
                    List<String> stepUsersRemaining = new ArrayList<>();
                    
                    if (nextStep.getStepLogic() == Step.StepLogic.ALL) {
                        attendiAttoreTaskListFiltered.stream().forEach(t -> {stepUsersRemaining.add(t.getAttore());});
                    }
                    else {
                        for (int i = nextStep.getCurrentTaskIndex() + 1; i < nextStep.getTaskList().size(); i ++) {
                            Task task = null;
                            try {
                                task = nextStep.getTaskList().get(i);
                            }
                            catch (Exception ex) {
                                break;
                            }
                            if (task != null && AttendiAttoreTask.class.isAssignableFrom(task.getClass())) {
                                stepUsersRemaining.add(((AttendiAttoreTask)task).getAttore());
                                break;
                            }
                        }
                    }
                    if (!stepUsersRemaining.isEmpty()) {
                        currentStepDone = false;
                        String stepId = nextStep.getStepId();
                        String stepType = nextStep.getStepType();
                        stepInformation = new ProcessoDucumentaleStepInformation(stepId, stepType, null, stepUsersRemaining, stepOnUser, null);
                    }
                }
            }
        }

        if (currentStepDone) {
            do {
                nextStep = getNextStep(nextStep.getStepId());

                // se c'è uno step successivo estraggo gli utenti appartenenti a quello step dai parametri di step-on
                if (nextStep != null) {
                    List<String> stepUsers = extractUsersFromStepOnParams(stepOnParams, nextStep);

                    // se ci sono utenti di quello step, allora quello step sarà il prossimo
                    if (stepUsers != null && !stepUsers.isEmpty()) {
                        found = true;
                        String stepId = nextStep.getStepId();
                        String stepType = nextStep.getStepType();
                        stepInformation = new ProcessoDucumentaleStepInformation(stepId, stepType, null, stepUsers, stepOnUser, null);
                    }
                }
            }
            while (nextStep != null && !found);
        }

        return stepInformation;
    }

    /**
     * torna un oggetto ProcessoDucumentaleStepInformation che descrive lo step in cui si vuole arrivare a seguito di uno step-to/sstep-back
     * @param stepId lo step-id nel quale si vuole arrivare
     * @param stepToUser l'utente che ha fatto lo step-to/step-back
     * @param stepOnParams i parametri passati allo step-on (servono per calcolare gli utenti dei vari step)
     * @param processDirection
     * @return un oggetto ProcessoDucumentaleStepInformation che descrive lo step in cui si vuole arrivare
     * @throws ProcessWorkFlowException nul caso lo step-id non è tra quelli nei quali è consentito arrivare
     */
    @JsonIgnore
    public ProcessoDucumentaleStepInformation getStepInformation(String stepId, String stepToUser, Bag stepOnParams, ProcessDirection processDirection) throws ProcessWorkFlowException {
        ProcessoDucumentaleStepInformation stepInformation = null;
        if (processDirection == ProcessDirection.BACKWARD) {
            if (getBackwardSteps().contains(stepId)) {
                Step step = getStep(stepId);
                String stepType = step.getStepType();
                List<String> stepUsers = extractUsersFromStepOnParams(stepOnParams, step);
                if (stepUsers != null && !stepUsers.isEmpty())
                    stepInformation = new ProcessoDucumentaleStepInformation(stepId, stepType, null, stepUsers, null, stepToUser);
            }
            else {
                throw new ProcessWorkFlowException("step isn't into allowed backward steps");
            }
        }

        return stepInformation;
    }

    /**
     * torna la lista degli step contenenti utenti in cui è possibile tornare
     * @param stepOnParams i parametri passati allo step-on (servono per calcolare gli utenti dei vari step)
     * @return la lista degli step contenenti utenti in cui è possibile tornare
     */
    @JsonIgnore
    public List<Step> getBackwardStepsWhitUsers(Bag stepOnParams) {        
        List<Step> backwardStepsWhitUsers = new ArrayList<>();

        List<String> backwardSteps = getBackwardSteps();
        backwardSteps.stream().forEach(
                stepId -> {
                    Step step = getStep(stepId);
                    if (step != null) {
                        List<String> stepUsers = extractUsersFromStepOnParams(stepOnParams, step);
                        if (stepUsers != null && !stepUsers.isEmpty())
                            backwardStepsWhitUsers.add(step);
                    }
                }
        );

        return backwardStepsWhitUsers;
    }

    /**
     * Torna, se esiste il numero assegnato al documento, null se non esiste
     * @return il numero assegnato al documento, null se non esiste
     */
    public String getDocumentNumber() {
        String numero = null;
        List<StepLog> stepsLogList = getStepsLog();
        if (stepsLogList != null && stepsLogList.size() > 0) {
            try {
                StepLog stepLogNumerazione = stepsLogList.stream().filter(stepLog -> stepLog.getStepType().equals(Steps.NUMERAZIONE.name())).findFirst().get();
                if (stepLogNumerazione != null) {
                    numero = (String) stepLogNumerazione.getLogData().get(NumerazioneTask.NUMERO_ASSEGNATO);
                }
            }
            catch (Exception ex) {
            }
        }
        return numero;
    }
    
    @JsonIgnore
    private List<String> extractUsersFromStepOnParams(Bag stepOnParams, Step step) {
        List<String> users = new ArrayList<>();
        
        List<Bag> stepBuildingParams = null;
        try {
            stepBuildingParams = ((List<Bag>) ((Bag) stepOnParams.get(step.getStepType())).get(AttendiAttoriStepBuilderTask.TASK_PARAMETER_KEY));
        }
        catch (Exception ex) {
        }
        
        if (stepBuildingParams != null) {
            for (Bag stepParams : stepBuildingParams) {
               String user = (String) stepParams.get(AttendiAttoreTask.ATTORE_ATTESO_PARAM_NAME);
               users.add(user);
               if (step.getStepLogic() == Step.StepLogic.SEQ)
                   break;
            }
        }
        
        return users;
        //if (users.isEmpty())
    }

    @JsonIgnore
    private int findUserIndex(String user, List<AttendiAttoreTask> attendiAttoreTaks) {
        for (int i = 0; i<attendiAttoreTaks.size(); i++) {
            if (attendiAttoreTaks.get(i).getAttore().equals(user))
                return i;
        }
        return -1;
    }
    
    @JsonIgnore
    public List<Task> getUserTasks(String user, Step step) {
        List<AttendiAttoreTask> attendiAttoreTaks = step.getTaskList(AttendiAttoreTask.class);
        List res = new ArrayList(attendiAttoreTaks.stream().filter(task -> task.getAttore().equals(user)).collect(Collectors.toList()));
        List<PubblicaAttivitaTask> pubblicaAttivitaTasks = step.getTaskList(PubblicaAttivitaTask.class);
        res.addAll(pubblicaAttivitaTasks.stream().filter(task -> task.getAttorePubblicazione().equals(user)).collect(Collectors.toList()));
        return res;
    }
}
