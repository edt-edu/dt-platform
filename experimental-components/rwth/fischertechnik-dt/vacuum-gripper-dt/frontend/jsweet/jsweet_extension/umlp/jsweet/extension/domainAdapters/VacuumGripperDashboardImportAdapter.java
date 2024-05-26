/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;

public class VacuumGripperDashboardImportAdapter extends DomainImportAdapter {
  public VacuumGripperDashboardImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void setDomainName() {
    addDomainName("vacuumgripperdashboard");
  }
}
