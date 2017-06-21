package sapotero.rxtest.managers.menu.commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sapotero.rxtest.application.EsdApplication;
import sapotero.rxtest.db.mapper.utils.Mappers;
import sapotero.rxtest.db.requery.models.decisions.RDisplayFirstDecisionEntity;
import sapotero.rxtest.retrofit.OperationService;
import sapotero.rxtest.retrofit.models.OperationResult;
import sapotero.rxtest.retrofit.models.v2.DecisionError;
import sapotero.rxtest.services.MainService;
import sapotero.rxtest.utils.Settings;
import sapotero.rxtest.utils.memory.MemoryStore;
import sapotero.rxtest.utils.queue.QueueManager;
import sapotero.rxtest.managers.menu.interfaces.Command;
import sapotero.rxtest.managers.menu.interfaces.Operation;
import sapotero.rxtest.managers.menu.utils.CommandParams;
import timber.log.Timber;


public abstract class AbstractCommand implements Serializable, Command, Operation {

  @Inject public OkHttpClient okHttpClient;
  @Inject public Settings settings;
  @Inject public Mappers mappers;
  @Inject public SingleEntityStore<Persistable> dataStore;
  @Inject public QueueManager queueManager;
  @Inject public MemoryStore store;

  public CommandParams params;

  public AbstractCommand() {
    EsdApplication.getManagerComponent().inject(this);
  }

  public abstract void withParams(CommandParams params);

  public Callback callback;
  public interface Callback {
    void onCommandExecuteSuccess(String command);
    void onCommandExecuteError(String type);
  }

  protected Observable<OperationResult> getApprovalSignOperationResultObservable(String uid, String official_id) {
    Retrofit retrofit = getOperationsRetrofit();

    OperationService operationService = retrofit.create( OperationService.class );

    ArrayList<String> uids = new ArrayList<>();
    uids.add(uid);

    String comment = null;
    if ( params.getComment() != null ){
      comment = params.getComment();
    }

    String sign = null;

    try {
      sign = MainService.getFakeSign( settings.getPin(), null );
    } catch (Exception e) {
      e.printStackTrace();
    }

    return operationService.approvalSign(
      getType(),
      settings.getLogin(),
      settings.getToken(),
      uids,
      comment,
      settings.getStatusCode(),
      official_id,
      sign
    );
  }

  public Retrofit getOperationsRetrofit() {
    return new Retrofit.Builder()
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .baseUrl( settings.getHost() + "v3/operations/" )
      .client( okHttpClient )
      .build();
  }

  public Retrofit getRetrofit() {
    return new Retrofit.Builder()
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .baseUrl( settings.getHost() )
      .client( okHttpClient )
      .build();
  }

  // resolved https://tasks.n-core.ru/browse/MVDESD-13258
  // 1. Созданные мной и подписант я
  protected void checkCreatorAndSignerIsCurrentUser(DecisionError data, String TAG) {
    String decisionUid = data.getDecisionUid();

    // Если создал резолюцию я и подписант я, то сохранить UID этой резолюции в отдельную таблицу
    if ( decisionUid != null && !decisionUid.equals("") ) {
      if ( Objects.equals( data.getDecisionSignerId(), settings.getCurrentUserId() ) ) {
        RDisplayFirstDecisionEntity rDisplayFirstDecisionEntity = new RDisplayFirstDecisionEntity();
        rDisplayFirstDecisionEntity.setDecisionUid( decisionUid );
        rDisplayFirstDecisionEntity.setUserId( settings.getCurrentUserId() );

        dataStore
          .insert( rDisplayFirstDecisionEntity )
          .toObservable()
          .subscribeOn(Schedulers.computation())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
            result -> Timber.tag(TAG).v("Added decision to display first decision table"),
            error -> Timber.tag(TAG).e(error)
          );
      }
    }
  }
}
