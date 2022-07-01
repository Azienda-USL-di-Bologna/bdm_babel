package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author gdm
 */
public class AspettaSpedizioneTask0001 extends AspettaSpedizioneTask {
    private final Logger log = LogManager.getLogger(AspettaSpedizioneTask0001.class);
    
    public AspettaSpedizioneTask0001() {
    }

    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {
        status = Bdm.BdmStatus.FINISHED;
        return new Result(status, null, null);
    }

    @Override
    public void undo(Bag runningContext, Bag context, Bag parameters) {
        super.undo(runningContext, context, parameters);
    }
    
    @Override
    public void stepIn(Context c, Bag p) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTaskVersion() {
        return "0001";
    }
}
