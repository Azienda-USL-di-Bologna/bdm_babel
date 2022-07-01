package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.workflows.processes.ProtocolloInUscitaAuto;
import java.util.List;

/**
 *
 * @author Matteo Next
 */
public class AttendiJobsTask0001 extends AttendiJobsTask {

    /*
 * questo task non fa nulla, se non fermare il processo e attendere lo step on;
 * così facendo si aspetta la fine dei mestieri per proseguire con il processo
     */
    @Override
    public String getTaskVersion() {
        return "0001";
    }

    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {
        status = Bdm.BdmStatus.FINISHED;
        return new Result(status, null, null);
    }

    @Override
    public void stepIn(Context c, Bag p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
