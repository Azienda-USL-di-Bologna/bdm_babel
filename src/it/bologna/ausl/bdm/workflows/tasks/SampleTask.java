package it.bologna.ausl.bdm.workflows.tasks;

import it.bologna.ausl.bdm.utilities.Bag;
import it.bologna.ausl.bdm.core.Bdm;
import it.bologna.ausl.bdm.core.Context;
import it.bologna.ausl.bdm.core.Result;
import it.bologna.ausl.bdm.core.Task;

/**
 *
 * @author andrea
 */
public class SampleTask extends Task {

    @Override
    public String getTaskType() {
        return "SampleTask";
    }

    @Override
    public Result execute(Bag runningContext, Bag context, Bag params) {
        status = Bdm.BdmStatus.FINISHED;
        return new Result(status, null, null);
    }

    @Override
    public void stepIn(Context c, Bag p) {
    }

    @Override
    public String getTaskVersion() {
        return "0001";
    }

}
