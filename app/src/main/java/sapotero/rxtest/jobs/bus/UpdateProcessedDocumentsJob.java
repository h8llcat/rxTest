package sapotero.rxtest.jobs.bus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sapotero.rxtest.db.mapper.DocumentMapper;
import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.db.requery.models.images.RImage;
import sapotero.rxtest.db.requery.models.images.RImageEntity;
import sapotero.rxtest.events.stepper.load.StepperLoadDocumentEvent;
import sapotero.rxtest.events.view.UpdateCurrentDocumentEvent;
import sapotero.rxtest.retrofit.DocumentService;
import sapotero.rxtest.retrofit.models.document.Card;
import sapotero.rxtest.retrofit.models.document.DocumentInfo;
import sapotero.rxtest.retrofit.models.document.Step;
import timber.log.Timber;

public class UpdateProcessedDocumentsJob extends BaseJob {

  public static final int PRIORITY = 1;

  private String processed_folder;

  private String uid;
  private String TAG = this.getClass().getSimpleName();
  private DocumentInfo document;


  public UpdateProcessedDocumentsJob(String uid, String processed_folder) {
    super( new Params(PRIORITY).requireNetwork().persist() );
    this.uid = uid;
    this.processed_folder = processed_folder;
  }

  @Override
  public void onAdded() {
  }

  @Override
  public void onRun() throws Throwable {

    Retrofit retrofit = new Retrofit.Builder()
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .baseUrl(settings.getHost() + "v3/documents/")
      .client(okHttpClient)
      .build();

    DocumentService documentService = retrofit.create( DocumentService.class );

    Observable<DocumentInfo> info = documentService.getInfo(
      uid,
      settings.getLogin(),
      settings.getToken()
    );

    info
      .subscribeOn( Schedulers.io() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        doc -> {
          document = doc;
          Timber.tag(TAG).d("recv title - %s", doc.getTitle() );

          update( exist(doc.getUid()) );

          if ( doc.getLinks() != null && doc.getLinks().size() > 0 ){

            for (String link: doc.getLinks()) {
              jobManager.addJobInBackground( new UpdateLinkJob( link ) );
            }

          }

          if ( doc.getRoute() != null && doc.getRoute().getSteps().size() > 0 ){
            for (Step step: doc.getRoute().getSteps() ) {
              if ( step.getCards() != null && step.getCards().size() > 0){
                for (Card card: step.getCards() ) {
                  if (card.getUid() != null) {
                    jobManager.addJobInBackground( new UpdateLinkJob( card.getUid() ) );
                  }
                }
              }
            }
          }

        },
        error -> {
          error.printStackTrace();
        }

      );
  }



  @NonNull
  private Boolean exist(String uid){

    boolean result = false;

    Integer count = dataStore
      .count(RDocumentEntity.UID)
      .where(RDocumentEntity.UID.eq(uid))
      .get().value();

    if( count != 0 ){
      result = true;
    }

    Timber.tag(TAG).v("exist " + result );

    return result;
  }

  @NonNull
  private Observable<RDocumentEntity> create(DocumentInfo d){
    RDocumentEntity rd = new RDocumentEntity();
    DocumentMapper documentMapper = mappers.getDocumentMapper();

    documentMapper.setSimpleFields(rd, d);
    documentMapper.setFilter(rd, "");
    rd.setFolder(processed_folder);
    rd.setProcessed(true);
    rd.setFromProcessedFolder(true);

    return dataStore.insert( rd ).toObservable();
  }

  private void update(Boolean exist){

    if (exist) {
      updateDocumentInfo();
    } else {
      create(document)
        .subscribeOn( Schedulers.io() )
        .observeOn( AndroidSchedulers.mainThread() )
        .subscribe(
          this::createNewDocument,
          Throwable::printStackTrace
        );
    }
  }

  private void createNewDocument(RDocumentEntity documentEntity){

    RDocumentEntity rDoc;

    if (documentEntity != null){
      rDoc = documentEntity;
    } else {
      rDoc = dataStore
        .select(RDocumentEntity.class)
        .where(RDocumentEntity.UID.eq( document.getUid() ))
        .get()
        .first();
    }

    rDoc.setUser( settings.getLogin() );
    rDoc.setFromProcessedFolder(true);
    rDoc.setProcessed(true);
    rDoc.setFolder(processed_folder);

    DocumentMapper documentMapper = mappers.getDocumentMapper();

    documentMapper.setSigner(rDoc, document.getSigner());
    documentMapper.setFilter(rDoc, "");

    documentMapper.setNestedFields(rDoc, document, true);

    dataStore.update(rDoc)
      .toObservable()
      .subscribeOn( Schedulers.io() )
      .observeOn( Schedulers.io() )
      .subscribe(
        result -> {
          Timber.tag(TAG).d("updated " + result.getUid());

          if ( result.getImages() != null && result.getImages().size() > 0 ){

            for (RImage _image : result.getImages()) {

              RImageEntity image = (RImageEntity) _image;
              jobManager.addJobInBackground( new DownloadFileJob(settings.getHost(), image.getPath(), image.getMd5()+"_"+image.getTitle(), image.getId() ) );
            }

          }
        },
        error ->{
          error.printStackTrace();
        }
      );
  }

  private void updateDocumentInfo(){

    RDocumentEntity doc = dataStore
      .select(RDocumentEntity.class)
      .where(RDocumentEntity.UID.eq(uid))
      .get().first();

    DocumentMapper documentMapper = mappers.getDocumentMapper();

    if ( !Objects.equals( document.getMd5(), doc.getMd5() ) ){
      Timber.tag("MD5").d("not equal %s - %s",document.getMd5(), doc.getMd5() );

      doc.setMd5( document.getMd5() );
      doc.setUser( settings.getLogin() );
      doc.setFromProcessedFolder(true);
      doc.setProcessed(true);
      doc.setFolder(processed_folder);

      documentMapper.setSigner(doc, document.getSigner());
      documentMapper.setNestedFields(doc, document, true);

      dataStore
        .update( doc )
        .subscribeOn( Schedulers.io() )
        .observeOn( AndroidSchedulers.mainThread() )
        .subscribe(
          result -> {
            Timber.tag("MD5").d("updateDocumentInfo " + result.getMd5());

            EventBus.getDefault().post( new StepperLoadDocumentEvent(doc.getUid()) );

            if ( result.getImages() != null && result.getImages().size() > 0  ){

              for (RImage _image : result.getImages()) {

                RImageEntity image = (RImageEntity) _image;
                jobManager.addJobInBackground( new DownloadFileJob(settings.getHost(), image.getPath(), image.getMd5()+"_"+image.getTitle(), image.getId() ) );
              }

            }
          },
          error ->{
            error.printStackTrace();
          }
        );

      EventBus.getDefault().post( new UpdateCurrentDocumentEvent( doc.getUid() ) );
    } else {
      Timber.tag("MD5").d("equal");
    }
  }

  @Override
  protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
    return RetryConstraint.createExponentialBackoff(runCount, 1000);
  }
  @Override
  protected void onCancel(@CancelReason int cancelReason, @Nullable Throwable throwable) {
    // Job has exceeded retry attempts or shouldReRunOnThrowable() has decided to cancel.
  }
}
