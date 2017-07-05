package sapotero.rxtest.managers.menu.commands.shared;

import org.greenrobot.eventbus.EventBus;

import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.db.requery.utils.Deleter;
import sapotero.rxtest.events.utils.NoDocumentsEvent;
import sapotero.rxtest.managers.menu.commands.SharedCommand;
import sapotero.rxtest.managers.menu.receivers.DocumentReceiver;
import sapotero.rxtest.utils.memory.fields.LabelType;
import sapotero.rxtest.utils.memory.utils.Transaction;
import timber.log.Timber;

public class RemoveFromFolder extends SharedCommand {

  private final DocumentReceiver document;

  private String TAG = this.getClass().getSimpleName();

  private String folder_id;
  private String document_id;

  public RemoveFromFolder(DocumentReceiver document){
    super();
    this.document = document;
  }

  public String getInfo(){
    return null;
  }

  public void registerCallBack(Callback callback){
    this.callback = callback;
  }

  public RemoveFromFolder withFolder(String uid){
    folder_id = uid;
    return this;
  }

  public RemoveFromFolder withDocumentId(String uid) {
    this.document_id = uid;
    return this;
  }

  @Override
  public void execute() {
    Integer count = dataStore
      .update(RDocumentEntity.class)
      .set( RDocumentEntity.FAVORITES, false )
      .set( RDocumentEntity.CHANGED, true )
      .where(RDocumentEntity.UID.eq(document_id))
      .get().value();


    Transaction transaction = new Transaction();
    transaction
      .from( store.getDocuments().get(document_id) )
      .setLabel(LabelType.SYNC)
      .removeLabel(LabelType.FAVORITES);
    store.process( transaction );

    queueManager.add(this);
  }

  @Override
  public String getType() {
    return "remove_from_folder";
  }

  @Override
  public void executeLocal() {

    queueManager.setExecutedLocal(this);


    if ( callback != null ){
      callback.onCommandExecuteSuccess( getType() );
    }
  }

  @Override
  public void executeRemote() {
    remoteFolderOperation( this, document_id, folder_id, true, TAG );
  }

  @Override
  protected void setSuccess() {
    RDocumentEntity documentEntity = dataStore
      .select( RDocumentEntity.class )
      .where( RDocumentEntity.UID.eq( document_id ) )
      .get().firstOrNull();

    if ( documentEntity != null && documentEntity.isFromFavoritesFolder() != null && documentEntity.isFromFavoritesFolder() ) {
      EventBus.getDefault().post( new NoDocumentsEvent() );

      store.getDocuments().remove( document_id );
      new Deleter().deleteDocument( documentEntity, TAG );

    } else {
      Transaction transaction = new Transaction();
      transaction
        .from( store.getDocuments().get(document_id) )
        .removeLabel(LabelType.SYNC)
        .removeLabel(LabelType.FAVORITES);
      store.process( transaction );

      setChangedFalse(document_id);
    }
  }

  @Override
  protected void setError() {
    Transaction transaction = new Transaction();
    transaction
      .from( store.getDocuments().get(document_id) )
      .removeLabel(LabelType.SYNC)
      .setLabel(LabelType.FAVORITES);
    store.process( transaction );

    dataStore
      .update( RDocumentEntity.class )
      .set( RDocumentEntity.CHANGED, false )
      .set( RDocumentEntity.FAVORITES, true )
      .where( RDocumentEntity.UID.eq( document_id ) )
      .get()
      .value();
  }
}
