package sapotero.rxtest.managers.menu.commands.decision;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.db.requery.models.decisions.RDecisionEntity;
import sapotero.rxtest.events.document.ForceUpdateDocumentEvent;
import sapotero.rxtest.events.document.UpdateDocumentEvent;
import sapotero.rxtest.events.view.InvalidateDecisionSpinnerEvent;
import sapotero.rxtest.managers.menu.commands.AbstractCommand;
import sapotero.rxtest.managers.menu.receivers.DocumentReceiver;
import sapotero.rxtest.managers.menu.utils.CommandParams;
import sapotero.rxtest.retrofit.DocumentService;
import sapotero.rxtest.retrofit.models.document.Decision;
import sapotero.rxtest.retrofit.models.v2.DecisionError;
import sapotero.rxtest.retrofit.models.wrapper.DecisionWrapper;
import timber.log.Timber;

public class RejectDecision extends AbstractCommand {

  private final DocumentReceiver document;

  private String TAG = this.getClass().getSimpleName();

  private String folder_id;
  private RDecisionEntity decision;
  private String decisionId;

  public RejectDecision(DocumentReceiver document){
    super();
    this.document = document;
  }

  public String getInfo(){
    return null;
  }

  public void registerCallBack(Callback callback){
    this.callback = callback;
  }

  public RejectDecision withDecision(RDecisionEntity decision){
    this.decision = decision;
    return this;
  }
  public RejectDecision withDecisionId(String decisionId){
    this.decisionId = decisionId;
    return this;
  }

  @Override
  public void execute() {

    queueManager.add(this);

    String uid = params.getDecisionModel().getId();
//    store.setLabel(LabelType.SYNC, uid);
//    store.setField(FieldType.PROCESSED, true, uid);

    updateLocal();

  }


  private void updateLocal() {

    Timber.tag(TAG).e("1 updateLocal params%s", new Gson().toJson( params ));


    Integer count = dataStore
      .update(RDecisionEntity.class)
      .set(RDecisionEntity.TEMPORARY, true)
      .where(RDecisionEntity.UID.eq(params.getDecisionModel().getId()))
      .get().value();


    String uid = null;

    if (Objects.equals(params.getDecisionModel().getSignerId(), settings.getCurrentUserId())){

      if (params.getDecisionModel().getDocumentUid() != null && !Objects.equals(params.getDecisionModel().getDocumentUid(), "")){
        uid = params.getDecisionModel().getDocumentUid();
      }

      if (params.getDocument() != null && !Objects.equals(params.getDocument(), "")){
        uid = params.getDocument();
      }

      if (document.getUid() != null && !Objects.equals(document.getUid(), "")){
        uid = document.getUid();
      }


      Timber.tag(TAG).i( "3 updateLocal document uid:\n%s\n%s\n%s\n", params.getDecisionModel().getDocumentUid(), params.getDocument(), document.getUid() );


      Integer dec = dataStore
        .update(RDocumentEntity.class)
        .set(RDocumentEntity.PROCESSED, true)
        .set(RDocumentEntity.MD5, "")
        .where(RDocumentEntity.UID.eq( uid ))
        .get().value();

      Timber.tag(TAG).e("3 updateLocal document %s | %s", uid, dec > 0);

//      EventBus.getDefault().post( new ShowNextDocumentEvent());
    }

    Observable.just("").timeout(100, TimeUnit.MILLISECONDS).subscribe(
      data -> {
        Timber.tag("slow").e("exec");

        EventBus.getDefault().post( new InvalidateDecisionSpinnerEvent( params.getDecisionModel().getId() ));
      }, error -> {
        Timber.tag(TAG).e(error);
      }
    );
  }



  @Override
  public String getType() {
    return "reject_decision";
  }

  @Override
  public void executeLocal() {
    if ( callback != null ){
      callback.onCommandExecuteSuccess( getType() );
    }

    queueManager.setExecutedLocal(this);
  }

  @Override
  public void executeRemote() {
    Timber.tag(TAG).i( "type: %s", this.getClass().getName() );

    Retrofit retrofit = new Retrofit.Builder()
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .baseUrl( settings.getHost() )
      .client( okHttpClient )
      .build();

    Decision formated_decision;

    if ( params.getDecisionModel() != null ){
      formated_decision = params.getDecisionModel();
    } else {
      formated_decision = mappers.getDecisionMapper().toFormattedModel( decision );
    }

    formated_decision.setApproved(false);
    formated_decision.setCanceled(true);
    formated_decision.setDocumentUid(null);

    if (params.getComment() != null){
      formated_decision.setComment( String.format( "Причина отклонения: %s", params.getComment() ) );
    }

    DecisionWrapper wrapper = new DecisionWrapper();
    wrapper.setDecision(formated_decision);

    String json_d = new Gson().toJson( wrapper );
    Timber.w("decision_json: %s", json_d);


    RequestBody json = RequestBody.create(
      MediaType.parse("application/json"),
      json_d
    );

    Timber.tag(TAG).e("DECISION");
    Timber.tag(TAG).e("%s", json);

    DocumentService operationService = retrofit.create( DocumentService.class );

    Observable<DecisionError> info = operationService.update(
      decisionId,
      settings.getLogin(),
      settings.getToken(),
      json
    );

    info.subscribeOn( Schedulers.computation() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        data -> {

          if (data.getErrors() !=null && data.getErrors().size() > 0){
            queueManager.setExecutedWithError(this, data.getErrors());
            EventBus.getDefault().post( new ForceUpdateDocumentEvent( data.getDocumentUid() ));
          } else {

            if (callback != null ){
              callback.onCommandExecuteSuccess( getType() );
              EventBus.getDefault().post( new UpdateDocumentEvent( document.getUid() ));
            }

            queueManager.setExecutedRemote(this);

            String uid = document.getUid();
//            store.removeLabel(LabelType.SYNC, uid);
          }

        },
        error -> {
          Timber.tag(TAG).i("error: %s", error);
          if (callback != null){
            callback.onCommandExecuteError(getType());
          }
//          queueManager.setExecutedWithError(this, Collections.singletonList("http_error"));

          String uid = document.getUid();
//          store.removeLabel(LabelType.SYNC, uid);
//          store.setField(FieldType.PROCESSED, false, uid);

          EventBus.getDefault().post( new ForceUpdateDocumentEvent( params.getDecisionModel().getDocumentUid() ));
        }
      );
  }

  @Override
  public void withParams(CommandParams params) {
    this.params = params;
  }

  @Override
  public CommandParams getParams() {
    return params;
  }
}
