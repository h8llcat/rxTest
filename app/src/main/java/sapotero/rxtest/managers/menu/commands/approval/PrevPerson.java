package sapotero.rxtest.managers.menu.commands.approval;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import sapotero.rxtest.events.view.ShowNextDocumentEvent;
import sapotero.rxtest.managers.menu.commands.ApprovalSigningCommand;
import sapotero.rxtest.managers.menu.utils.CommandParams;

public class PrevPerson extends ApprovalSigningCommand {

  public PrevPerson(CommandParams params) {
    super(params);
  }

  @Override
  public String getType() {
    return "prev_person";
  }

  @Override
  public void executeLocal() {
    saveOldLabelValues(); // Must be before queueManager.add(this), because old label values are stored in params
    addToQueue();
    EventBus.getDefault().post( new ShowNextDocumentEvent( getParams().getDocument() ));

    startRejectedOperationInMemory();
    startRejectedOperationInDb();
    setAsProcessed();

    queueManager.setExecutedLocal(this);
  }

  @Override
  public void executeRemote() {
    approvalSigningRemote();
  }

  @Override
  public void finishOnOperationSuccess() {
    finishRejectedOperationOnSuccess();
  }

  @Override
  public void finishOnOperationError(List<String> errors) {
    finishRejectedProcessedOperationOnError( errors );
  }
}
