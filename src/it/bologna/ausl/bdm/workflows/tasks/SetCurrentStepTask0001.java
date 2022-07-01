package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.processes.utils.UtilityFunctions;
import it.bologna.ausl.bdm.workflows.processes.ProcessiDocumentali;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public class SetCurrentStepTask0001 extends SetCurrentStepTask {
    private final Logger log = LogManager.getLogger(SetCurrentStepTask0001.class);

    private String idApplicazione;
    private String tokenApplicazione;
    private String idOggetto;
    private String tipoOggetto;
    private String setCurrentTaskServletUrl;

    public SetCurrentStepTask0001() {
        setAuto(true);
    }

    @Override
    public void init(Bag p) {
        super.init(p);
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

    public String getSetCurrentTaskServletUrl() {
        return setCurrentTaskServletUrl;
    }

    public void setSetCurrentTaskServletUrl(String setCurrentTaskServletUrl) {
        this.setCurrentTaskServletUrl = setCurrentTaskServletUrl;
    }

    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {

        Step currentStep = (Step) runningContext.get(BdmProcess.CURRENT_STEP);
        
        idApplicazione = (String) context.get(ProcessiDocumentali.ID_APPLICAZIONE);
        tokenApplicazione = (String) context.get(ProcessiDocumentali.TOKEN_APPLICAZIONE);
        idOggetto = (String) context.get(ProcessiDocumentali.ID_OGGETTO);
        tipoOggetto = (String) context.get(ProcessiDocumentali.TIPO_OGGETTO);

        setCurrentTaskServletUrl = (String) context.get(ProcessiDocumentali.SET_CURRENT_STEP_SERVLET_URL);

        Map<String, byte[]> requestParams = new HashMap<>();
        requestParams.put(ID_APPLICAZIONE_PARAM_NAME, idApplicazione.getBytes());
        requestParams.put(TOKEN_APPLICAZIONE_PARAM_NAME, tokenApplicazione.getBytes());
        requestParams.put(ID_OGGETTO_PARAM_NAME, idOggetto.getBytes());
        requestParams.put(TIPO_OGGETTO_PARAM_NAME, tipoOggetto.getBytes());
        requestParams.put(CURRENT_STEP_PARAM_NAME, currentStep.getStepType().getBytes());

        try {
            UtilityFunctions.sendHttpMessage(setCurrentTaskServletUrl, null, null, requestParams, "POST", null);
            status = Bdm.BdmStatus.FINISHED;
        }
        catch (Exception ex) {
            log.error(ex);
            status = Bdm.BdmStatus.ERROR;
        }

        return new Result(status, null, null);
    }

    @Override
    public void stepIn(Context c, Bag p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTaskVersion() {
        return "0001";
    }
}
