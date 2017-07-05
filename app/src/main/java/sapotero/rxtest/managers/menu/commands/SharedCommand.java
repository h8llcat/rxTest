package sapotero.rxtest.managers.menu.commands;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.events.document.DropControlEvent;
import sapotero.rxtest.events.utils.RecalculateMenuEvent;
import sapotero.rxtest.events.view.ShowSnackEvent;
import sapotero.rxtest.managers.menu.interfaces.Command;
import sapotero.rxtest.retrofit.OperationService;
import sapotero.rxtest.retrofit.models.OperationResult;
import sapotero.rxtest.utils.memory.fields.LabelType;

public abstract class SharedCommand extends AbstractCommand {

  public void onError(Command command, String message) {
    if (callback != null){
      callback.onCommandExecuteError(getType());
    }

    if ( settings.isOnline() ) {
      queueManager.setExecutedWithError( command, Collections.singletonList( message ) );
      setError();
    }
  }

  protected abstract void setError();

  private void checkMessage(Command command, String message, boolean recalculateMenu) {
    if (message != null && !message.toLowerCase().contains("успешно") ) {
      queueManager.setExecutedWithError( command, Collections.singletonList( message ) );
      setError();
    } else {
      queueManager.setExecutedRemote( command );
      setSuccess();
      if ( recalculateMenu ) {
        EventBus.getDefault().post( new RecalculateMenuEvent() );
      }
    }
  }

  protected abstract void setSuccess();

  private void onControlLabelSuccess(Command command, OperationResult result, RDocumentEntity doc, String TAG) {
    printLog( result, TAG );

    if ( Objects.equals(result.getType(), "danger") && result.getMessage() != null){
      EventBus.getDefault().post( new ShowSnackEvent( result.getMessage() ));

      if (doc != null) {
        EventBus.getDefault().post( new DropControlEvent( doc.isControl() ));
      }

      queueManager.setExecutedWithError( command, Collections.singletonList( result.getMessage() ) );
      setError();

    } else {
      checkMessage( command, result.getMessage(), false );
    }
  }

  private void onFolderSuccess(Command command, OperationResult data, boolean recalculateMenu, String TAG) {
    printLog( data, TAG );
    checkMessage( command, data.getMessage(), recalculateMenu );
  }

  private Observable<OperationResult> getOperationResultObservable(String document_id, String folder_id) {
    Retrofit retrofit = getOperationsRetrofit();

    OperationService operationService = retrofit.create( OperationService.class );

    ArrayList<String> uids = new ArrayList<>();
    uids.add( settings.getUid() );

    return operationService.shared(
      getType(),
      settings.getLogin(),
      settings.getToken(),
      uids,
      document_id == null ? settings.getUid() : document_id,
      settings.getStatusCode(),
      folder_id,
      null
    );
  }

  protected void remoteFolderOperation(Command command, String document_id, String folder_id, boolean recalculateMenu, String TAG) {
    printCommandType( command, TAG );

    Observable<OperationResult> info = getOperationResultObservable(document_id, folder_id);

    info.subscribeOn( Schedulers.computation() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        data -> onFolderSuccess( command, data, recalculateMenu, TAG ),
        error -> onError( command, error.getLocalizedMessage() )
      );
  }

  protected void remoteControlLabelOperation(Command command, String document_id, String TAG) {
    printCommandType( command, TAG );

    Observable<OperationResult> info = getOperationResultObservable(document_id, null);

    RDocumentEntity doc = dataStore
      .select(RDocumentEntity.class)
      .where(RDocumentEntity.UID.eq(document_id))
      .get().firstOrNull();

    info.subscribeOn( Schedulers.computation() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        result -> onControlLabelSuccess( command, result, doc, TAG ),
        error -> onError( command, error.getLocalizedMessage() )
      );
  }

  protected void setControlLabelSuccess(String document_id) {
    store.process(
      store.startTransactionFor(document_id)
        .removeLabel(LabelType.SYNC)
    );

    setChangedFalse(document_id);
  }
}
