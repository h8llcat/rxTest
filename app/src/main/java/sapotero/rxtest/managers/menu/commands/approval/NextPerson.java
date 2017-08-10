package sapotero.rxtest.managers.menu.commands.approval;

import org.greenrobot.eventbus.EventBus;

import rx.schedulers.Schedulers;
import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.db.requery.models.utils.RApprovalNextPersonEntity;
import sapotero.rxtest.events.view.ShowNextDocumentEvent;
import sapotero.rxtest.managers.menu.commands.ApprovalSigningCommand;
import sapotero.rxtest.managers.menu.utils.CommandParams;
import timber.log.Timber;

public class NextPerson extends ApprovalSigningCommand {

  public NextPerson(CommandParams params) {
    super(params);
  }

  public void registerCallBack(Callback callback){
    this.callback = callback;
  }

  @Override
  public void execute() {
    queueManager.add(this);
    EventBus.getDefault().post( new ShowNextDocumentEvent( true,  getParams().getDocument() ));

    setSyncAndProcessedInMemory();

    setTaskStarted( getParams().getDocument(), false );
    setAsProcessed();
  }

  @Override
  public String getType() {
    return "next_person";
  }

  @Override
  public void executeLocal() {
    dataStore
      .update(RDocumentEntity.class)
      .set( RDocumentEntity.PROCESSED, true)
      .set( RDocumentEntity.CHANGED, true)
      .where(RDocumentEntity.UID.eq(getParams().getDocument()))
      .get()
      .value();

    sendSuccessCallback();

    queueManager.setExecutedLocal(this);
  }

  @Override
  public void executeRemote() {
    RApprovalNextPersonEntity rApprovalNextPersonEntity = getApprovalNextPersonEntity( getParams().getDocument() );

    if ( rApprovalNextPersonEntity != null && rApprovalNextPersonEntity.isTaskStarted() ) {
      Timber.tag(TAG).i( "Task already started, quit" );
      return;
    }

    if ( rApprovalNextPersonEntity == null ) {
      createNewRApprovalNextPersonEntity( getParams().getDocument() );
    } else {
      setTaskStarted( getParams().getDocument(), true );
    }

    printCommandType();
    remoteOperation();
  }

  private RApprovalNextPersonEntity createNewRApprovalNextPersonEntity(String uid) {
    RApprovalNextPersonEntity rApprovalNextPersonEntity = new RApprovalNextPersonEntity();
    rApprovalNextPersonEntity.setDocumentUid( uid );
    rApprovalNextPersonEntity.setTaskStarted( true );

    dataStore
      .insert(rApprovalNextPersonEntity)
      .toObservable()
      .subscribeOn(Schedulers.computation())
      .subscribeOn(Schedulers.computation())
      .subscribe(
        data -> Timber.tag(TAG).v( "inserted RApprovalNextPersonEntity %s", data.getDocumentUid() ),
        Timber::e
      );

    return rApprovalNextPersonEntity;
  }

  private RApprovalNextPersonEntity getApprovalNextPersonEntity(String documentUid) {
    return dataStore
      .select( RApprovalNextPersonEntity.class )
      .where( RApprovalNextPersonEntity.DOCUMENT_UID.eq( documentUid ) )
      .get().firstOrNull();
  }

  private void setTaskStarted(String documentUid, boolean value) {
    int count = dataStore
      .update( RApprovalNextPersonEntity.class )
      .set( RApprovalNextPersonEntity.TASK_STARTED, value )
      .where( RApprovalNextPersonEntity.DOCUMENT_UID.eq( documentUid ) )
      .get()
      .value();

    Timber.tag(TAG).i("Set task started = %s, count = %s", value, count);
  }

  @Override
  public void onRemoteError() {
    setTaskStarted( getParams().getDocument(), false );
  }
}
