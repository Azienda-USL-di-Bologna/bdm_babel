package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.processes.utils.UtilityFunctions;
import it.bologna.ausl.bdm.utilities.StepLog;
import it.bologna.ausl.bdm.workflows.processes.ProcessiDocumentali;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public class NumerazioneTask0001 extends NumerazioneTask {

    private final Logger log = LogManager.getLogger(NumerazioneTask0001.class);

    private String numerazioneServiceUrl;
    private String numerazioneSeq;
    private String numerazioneRetryTime;
    private String numerazioneMaxRetry;
    private String idApplicazione;
    private String tokenApplicazione;
    private String idOggetto;
    private String tipoOggetto;

    public NumerazioneTask0001() {
        setAuto(true);
    }

    public String getNumerazioneServiceUrl() {
        return numerazioneServiceUrl;
    }

    public void setNumerazioneServiceUrl(String numerazioneServiceUrl) {
        this.numerazioneServiceUrl = numerazioneServiceUrl;
    }

    public String getNumerazioneSeq() {
        return numerazioneSeq;
    }

    public void setNumerazioneSeq(String numerazioneSeq) {
        this.numerazioneSeq = numerazioneSeq;
    }

    public String getNumerazioneRetryTime() {
        return numerazioneRetryTime;
    }

    public void setNumerazioneRetryTime(String numerazioneRetryTime) {
        this.numerazioneRetryTime = numerazioneRetryTime;
    }

    public String getNumerazioneMaxRetry() {
        return numerazioneMaxRetry;
    }

    public void setNumerazioneMaxRetry(String numerazioneMaxRetry) {
        this.numerazioneMaxRetry = numerazioneMaxRetry;
    }

    public String getIdApplicazione() {
        return idApplicazione;
    }

    public void setIdApplicazione(String idApplicazione) {
        this.idApplicazione = idApplicazione;
    }

    public String getTokenApplicazione() {
        return tokenApplicazione;
    }

    public void setTokenApplicazione(String tokenApplicazione) {
        this.tokenApplicazione = tokenApplicazione;
    }

    public String getIdOggetto() {
        return idOggetto;
    }

    public void setIdOggetto(String idOggetto) {
        this.idOggetto = idOggetto;
    }

    public String getTipoOggetto() {
        return tipoOggetto;
    }

    public void setTipoOggetto(String tipoOggetto) {
        this.tipoOggetto = tipoOggetto;
    }

    @Override
    public void init(Bag p) {
        super.init(p);
    }

    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {
        log.info("execute task numerazione");
        idOggetto = (String) context.get(ProcessiDocumentali.ID_OGGETTO);
        tipoOggetto = (String) context.get(ProcessiDocumentali.TIPO_OGGETTO);
        idApplicazione = (String) context.get(ProcessiDocumentali.ID_APPLICAZIONE);
        tokenApplicazione = (String) context.get(ProcessiDocumentali.TOKEN_APPLICAZIONE);
        numerazioneServiceUrl = (String) context.get(ProcessiDocumentali.NUMERAZIONE_SERVICE_URL);
        numerazioneSeq = (String) context.get(ProcessiDocumentali.NUMERAZIONE_SEQ);
        numerazioneRetryTime = (String) context.get(ProcessiDocumentali.NUMERAZIONE_RETRY_TIME);
        numerazioneMaxRetry = (String) context.get(ProcessiDocumentali.NUMERAZIONE_MAX_RETRY);

        // TODO: implemetare il retry in caso di errore e l'invio della mail quando c'è un errore
        try {
//            String updateNumberServiceUrl = UPDATE_NUMBER_FUNCTION_TEMPLATE.replace("[numerazione_seq]", numerazioneSeq);
            Map<String, byte[]> requestParams = new HashMap<>();
            requestParams.put(ID_APPLICAZIONE_PARAM_NAME, idApplicazione.getBytes());
            requestParams.put(TOKEN_APPLICAZIONE_PARAM_NAME, tokenApplicazione.getBytes());
            requestParams.put(ID_OGGETTO_PARAM_NAME, idOggetto.getBytes());
            requestParams.put(NUMERAZIONE_SEQ_PARAM_NAME, numerazioneSeq.getBytes());
            String numeroAndAnno = UtilityFunctions.sendHttpMessage(numerazioneServiceUrl, null, null, requestParams, "POST", null);

            // la servlet torna numero/anno
            String[] numeroAndAnnoSplitted = numeroAndAnno.split("/");
            String numero = numeroAndAnnoSplitted[0];
            String anno = numeroAndAnnoSplitted[1];

            // inserisco il numero e l'anno nel context
            context.put(ProcessiDocumentali.NUMERAZIONE_NUMERO_GENERATO, numero);
            context.put(ProcessiDocumentali.NUMERAZIONE_ANNO_GENERATO, anno);

            // inserisco nello stepLog corrente l'attore che ha fatto l'azione sotto la voce "attore_atteso" identificato dalla costante "ATTORE_ATTESO_PARAM_NAME"
            BdmProcess currentProcess = (BdmProcess) runningContext.get(BdmProcess.CURRENT_PROCESS);
            addNumeroInStepLog(currentProcess, numero);

            status = Bdm.BdmStatus.FINISHED;
        } catch (Exception ex) {
            log.error(ex);
            status = Bdm.BdmStatus.ERROR;
        }

        log.info("fine task numerazione");
        return new Result(status, null, null);
    }

    private void addNumeroInStepLog(BdmProcess currentProcess, String numeroAssegnato) {
        List<StepLog> stepsLog = currentProcess.getStepsLog();

        // lo step corrente è l'ultimo negli stepsLog
        StepLog currentStepLog = stepsLog.get(stepsLog.size() - 1);
        currentStepLog.putInLogData(NUMERO_ASSEGNATO, numeroAssegnato);

    }

    @Override
    public void stepIn(Context c, Bag p) {
    }

    @Override
    public String getTaskVersion() {
        return "0001";
    }

}
