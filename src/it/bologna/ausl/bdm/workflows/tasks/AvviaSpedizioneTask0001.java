package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.BdmProcess;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.core.Step;
import it.bologna.ausl.bdm.exception.BdmExeption;
import it.bologna.ausl.bdm.utilities.DbConnectionPoolManager;
import it.bologna.ausl.bdm.utilities.StepLog;
import it.bologna.ausl.bdm.workflows.processes.ProcessiDocumentali;
import it.bologna.ausl.spedizioniereobjectlibrary.Mail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;

/**
 *
 * @author gdm
 */
public class AvviaSpedizioneTask0001 extends AvviaSpedizioneTask {
    private final Logger log = LogManager.getLogger(AvviaSpedizioneTask0001.class);

    private String dbUri;
    private String idOggettoOrigine;
    private String tipoOggettoOrigine;
    private String idOggetto;
    private String idApplicazione;
    private Mail mail;
    private List<String> utentiDaNotificare;
    private String descrizioneOggetto;
    private Boolean spedisciGddoc;

    public AvviaSpedizioneTask0001() {
        super.setAuto(true);
    }

    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri(String dbUri) {
        this.dbUri = dbUri;
    }

    public String getIdOggettoOrigine() {
        return idOggettoOrigine;
    }

    public void setIdOggettoOrigine(String idOggettoOrigine) {
        this.idOggettoOrigine = idOggettoOrigine;
    }

    public String getTipoOggettoOrigine() {
        return tipoOggettoOrigine;
    }

    public void setTipoOggettoOrigine(String tipoOggettoOrigine) {
        this.tipoOggettoOrigine = tipoOggettoOrigine;
    }

    public String getIdOggetto() {
        return idOggetto;
    }

    public void setIdOggetto(String idOggetto) {
        this.idOggetto = idOggetto;
    }

    public String getIdApplicazione() {
        return idApplicazione;
    }

    public void setIdApplicazione(String idApplicazione) {
        this.idApplicazione = idApplicazione;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public List<String> getUtentiDaNotificare() {
        return utentiDaNotificare;
    }

    public void setUtentiDaNotificare(List<String> utentiDaNotificare) {
        this.utentiDaNotificare = utentiDaNotificare;
    }

    public String getDescrizioneOggetto() {
        return descrizioneOggetto;
    }

    public void setDescrizioneOggetto(String descrizioneOggetto) {
        this.descrizioneOggetto = descrizioneOggetto;
    }

    public Boolean getSpedisciGddoc() {
        return spedisciGddoc;
    }

    public void setSpedisciGddoc(Boolean spedisciGddoc) {
        this.spedisciGddoc = spedisciGddoc;
    }


    @Override
    public void init(Bag p) {
        super.init(p);
    }

    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {
        status = Bdm.BdmStatus.RUNNING;

        Step currentStep = (Step) runningContext.get(BdmProcess.CURRENT_STEP);
        BdmProcess currentProcess = (BdmProcess) runningContext.get(BdmProcess.CURRENT_PROCESS);

        Bag taskParams = null;
        try {
            taskParams = (Bag) ((Bag) params.get(currentStep.getStepType())).get(TASK_PARAMETER_KEY);   
        }
        catch (NullPointerException ex) {
        }

        StringBuilder queryEseguiteBuilder = new StringBuilder();
        boolean skippedStep = false;
        if (taskParams != null) {
            try {
                dbUri = (String) context.get(ProcessiDocumentali.DB_URI);
                idOggettoOrigine = (String) context.get(ProcessiDocumentali.ID_OGGETTO);
                tipoOggettoOrigine = (String) context.get(ProcessiDocumentali.TIPO_OGGETTO);
                idApplicazione = (String) context.get(ProcessiDocumentali.ID_APPLICAZIONE);

                List<Bag> listaSpedizioni = (List<Bag>) taskParams.get(LISTA_SPEDIZIONI_PARAM_NAME);

                DataSource ds = DbConnectionPoolManager.getConnection(dbUri);
                String queryInsertSpedizione = "INSERT INTO bds_tools.spedizioni_pec_globale( " +
                                "id_oggetto_origine, tipo_oggetto_origine, " +
                                "id_oggetto, id_applicazione, oggetto_da_spedire_json, " +
                                "utenti_da_notificare, process_id, descrizione_oggetto, spedisci_gddoc) " +
                                "VALUES (?, ?, " +
                                "?, ?, ?, " +
                                "?, ?, ?, ?)";

                Connection conn = ds.getConnection();
                PreparedStatement ps = null;
                try {
                    conn.setAutoCommit(false);
                    for (Bag spedizione: listaSpedizioni) {
                        int index = 1;
                        try {
                            idOggetto = (String) spedizione.get(ID_OGGETTO_PARAM_NAME);
                            String descrizioneOggettoTemplate = (String) spedizione.get(DESCRIZIONE_OGGETTO_PARAM_NAME);
            //                mail = Mail.parse((String)spedizione.get(MAIL_PARAM_NAME));
                            mail = (Mail)spedizione.get(MAIL_PARAM_NAME);
                            utentiDaNotificare = (List<String>) spedizione.get(UTENTI_DA_NOTIFICARE_PARAM_NAME);
                            spedisciGddoc = (Boolean) spedizione.get(SPEDISCI_GDDOC_PARAM_NAME);

                            String subjectMailTemplate = mail.getSubject();
                            String messageMailTemplate = mail.getMessage();
                            mail.setSubject(subjectMailTemplate
                                            .replace("[codice_registro]", (String) context.get(ProcessiDocumentali.CODICE_REGISTRO))
                                            .replace("[numero_registrazione]", (String) context.get(ProcessiDocumentali.NUMERAZIONE_NUMERO_GENERATO))
                                            .replace("[anno_registrazione]", (String) context.get(ProcessiDocumentali.NUMERAZIONE_ANNO_GENERATO))
                                            );
                            mail.setMessage(messageMailTemplate.replace("[oggetto]", (String) context.get(ProcessiDocumentali.OGGETTO)));

                            descrizioneOggetto = descrizioneOggettoTemplate
                                                .replace("[codice_registro]", (String) context.get(ProcessiDocumentali.CODICE_REGISTRO))
                                                .replace("[numero_registrazione]", (String) context.get(ProcessiDocumentali.NUMERAZIONE_NUMERO_GENERATO))
                                                .replace("[anno_registrazione]", (String) context.get(ProcessiDocumentali.NUMERAZIONE_ANNO_GENERATO))
                                                .replace("[oggetto]", (String) context.get(ProcessiDocumentali.OGGETTO));
                                    
                            ps = conn.prepareStatement(queryInsertSpedizione);
                            ps.setString(index++, idOggettoOrigine);
                            ps.setString(index++, tipoOggettoOrigine);
                            ps.setString(index++, idOggetto);
                            ps.setString(index++, idApplicazione);
                            ps.setString(index++, mail.getJSONString());
                            ps.setString(index++, String.join(";", utentiDaNotificare));
                            ps.setString(index++, currentProcess.getProcessId());
                            ps.setString(index++, descrizioneOggetto);
                            ps.setInt(index++, spedisciGddoc ? -1 : 0);

                            ps.execute();
                            queryEseguiteBuilder = queryEseguiteBuilder.append(ps.toString()).append("\n");
                        }
                        finally {
                            if (ps != null) {
                                ps.close();
                            }
                        }
                    }
                    conn.commit();
                    status = Bdm.BdmStatus.FINISHED;
                }
                catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                }
                finally {
                    conn.close();
                }
            }
            catch(Exception ex){
                log.error(ex);
                status = Bdm.BdmStatus.ERROR;
            }
        }
        else {
            skippedStep = true;
            status = Bdm.BdmStatus.FINISHED;
            log.info("no data, the step will be skipped...");
        }
        context.put(ProcessiDocumentali.AVVIO_SPEDIZIONE_ESEGUITO, !skippedStep);
        addQueryInStepLog(currentProcess, queryEseguiteBuilder.toString(), skippedStep);
        return new Result(status, null, null);
    }

    private void addQueryInStepLog(BdmProcess currentProcess, String queryEseguite, boolean skippedStep) {
        List<StepLog> stepsLog = currentProcess.getStepsLog();

//        // lo step corrente è l'ultimo negli stepsLog
        StepLog currentStepLog = stepsLog.get(stepsLog.size() - 1);
        currentStepLog.putInLogData(QUERY_ESEGUITA, queryEseguite);
        currentStepLog.putInLogData(SKIPPED_STEP, skippedStep);
    }

    @Override
    public void stepIn(Context c, Bag p) {
    }

    @Override
    public String getTaskVersion() {
        return "0001";
    }
}
