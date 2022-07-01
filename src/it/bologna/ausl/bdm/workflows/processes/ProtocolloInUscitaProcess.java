package it.bologna.ausl.bdm.workflows.processes;

/**
 *
 * @author andrea
 */
public abstract class ProtocolloInUscitaProcess extends ProcessiDocumentali {

    public static enum Steps {
        REDAZIONE,
        PARERI,
        CONTROLLO_SEGRETERIA,
        FIRMA,
        NUMERAZIONE,
        AVVIA_SPEDIZIONI,
        SPEDIZIONE_MANUALE,
        ASPETTA_SPEDIZIONI,
        FINE
    }

//    public static final String ID_OGGETTO = "id_oggetto";
//    public static final String TIPO_OGGETTO = "tipo_oggetto";
//    public static final String MASTERCHEF_HOST = "masterchef_host";
//    public static final String MASTERCHEF_QUEUE = "masterchef_queue";
    @Override
    public String getProcessType() {
        return "ProtocolloInUscita";
    }

//    public boolean isUserFinished(String user, Step step) {
//        List<AttendiAttoreTask> attendiAttoreTaks = step.getTaskList(AttendiAttoreTask.class);
////        return attendiAttoreTaks.stream().anyMatch(t -> ((t.getStatus() == BdmStatus.NOT_STARTED || t.getStatus() == BdmStatus.RUNNING) && t.getAttore().equals(user)));
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
//    }
//
//    public boolean isActionAllowed(String user) {
//        Step currentStep = getCurrentStep();
//        if (currentStep.getStepLogic() == Step.StepLogic.SEQ) {
//            List<AttendiAttoreTask> attendiAttoreTaks = currentStep.getTaskList(AttendiAttoreTask.class);
//            int userListPosition = findUserIndex(user, attendiAttoreTaks);
//            if (userListPosition == -1)
//                return false;
//            else if (userListPosition == 0)
//                return !isUserFinished(user, currentStep);
//            else {
//                for (int i = 0; i < userListPosition; i++) {
//                    String listUser = attendiAttoreTaks.get(i).getAttore();
//                    if (!isUserFinished(listUser, currentStep))
//                        return false;
//                }
//                return !isUserFinished(user, currentStep);
//            }
//
//        }
//        else {
//            return !isUserFinished(user, currentStep);
//        }
//    }
//
//    private int findUserIndex(String user, List<AttendiAttoreTask> attendiAttoreTaks) {
//        for (int i = 0; i<attendiAttoreTaks.size(); i++) {
//            if (attendiAttoreTaks.get(i).getAttore().equals(user))
//                return i;
//        }
//        return -1;
//    }
}
