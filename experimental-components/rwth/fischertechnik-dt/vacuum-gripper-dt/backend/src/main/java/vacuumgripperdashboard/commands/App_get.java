package vacuumgripperdashboard.commands;

import vacuumgripperdashboard.VacuumGripperDashboardManager;

import java.util.Collection;
import java.util.List;

public class App_get extends App_getTOP {
    public App_get(long gemId) {
        super(gemId);
    }

    public Collection<Long> getNewlySubscribedGemIds(){
        // Singleton => Client asks for instance without knowing gemId
        // Correct gemId is still needed in CommandManager for sync
        if(getGemId() == -1){
            return List.of(VacuumGripperDashboardManager.getApp().getGemId());
        }
        return List.of(getGemId());

    }
}