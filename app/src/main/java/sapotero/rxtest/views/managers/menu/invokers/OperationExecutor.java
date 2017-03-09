package sapotero.rxtest.views.managers.menu.invokers;

import sapotero.rxtest.views.managers.menu.interfaces.Command;
import timber.log.Timber;

public class OperationExecutor {
  private final String TAG = this.getClass().getSimpleName();
  private Command command;


  public OperationExecutor setCommand(Command command){
    this.command = command;

    return this;
  }
  public void execute(){
    Timber.tag(TAG).i("start execute");
    command.execute();
    command.executeLocal();
  }

}