package vacuumgripperdashboard.commands;

import umlp.backendrte.command.StatusResult;

public class EmptyFunc extends EmptyFuncTOP {
    @Override
    public StatusResult doAction(){
        return StatusResult.ok(getId());
    }
}
