package sapotero.rxtest.jobs.bus;

import com.birbit.android.jobqueue.Params;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
import sapotero.rxtest.db.requery.utils.Deleter;
import sapotero.rxtest.db.requery.utils.V2FilterType;
import sapotero.rxtest.events.stepper.load.StepperLoadDocumentEvent;
import sapotero.rxtest.events.view.UpdateCurrentDocumentEvent;
import sapotero.rxtest.retrofit.DocumentService;
import sapotero.rxtest.retrofit.models.document.Action;
import sapotero.rxtest.retrofit.models.document.Card;
import sapotero.rxtest.retrofit.models.document.DocumentInfo;
import sapotero.rxtest.retrofit.models.document.Exemplar;
import sapotero.rxtest.retrofit.models.document.Person;
import sapotero.rxtest.retrofit.models.document.Route;
import sapotero.rxtest.retrofit.models.document.Status;
import sapotero.rxtest.retrofit.models.document.Step;
import timber.log.Timber;

abstract class DocumentJob extends BaseJob {

  public String currentUserId;

  DocumentJob(Params params) {
    super(params);
  }

  public <T> boolean notEmpty(Collection<T> collection) {
    return collection != null && collection.size() > 0;
  }

  public String getJournalName(String journal) {
    String journalName = null;

    if ( exist( journal ) ) {
      String[] index = journal.split("_production_db_");
      journalName = index[0];
    }

    return journalName;
  }

  public boolean exist(Object obj) {
    return obj != null;
  }

  void loadDocument(String uid, String TAG) {
    if ( !Objects.equals( login, settings.getLogin() ) ) {
      // Загружаем документ только если логин не сменился (режим замещения)
      Timber.tag(TAG).d("Login changed, quit loading %s", uid);
      return;
    }

    Observable<DocumentInfo> info = getDocumentInfoObservable(uid);

    info
      .subscribeOn( Schedulers.io() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        doc -> {
          EventBus.getDefault().post( new StepperLoadDocumentEvent( doc.getUid()) );
          doAfterLoad( doc );
        },
        error -> {
          Timber.tag(TAG).e(error);
          EventBus.getDefault().post( new StepperLoadDocumentEvent("Error downloading document info") );
        }
      );
  }

  private Observable<DocumentInfo> getDocumentInfoObservable(String uid) {
    Retrofit retrofit = getRetrofit();
    DocumentService documentService = retrofit.create( DocumentService.class );
    return documentService.getInfo(
            uid,
            login,
            settings.getToken()
    );
  }

  private Retrofit getRetrofit() {
    return new Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(settings.getHost() + "v3/documents/")
            .client(okHttpClient)
            .build();
  }

  abstract public void doAfterLoad(DocumentInfo document);

  RDocumentEntity createDocument(DocumentInfo documentReceived, String status, boolean shared) {
    DocumentMapper documentMapper = new DocumentMapper().withLogin(login).withCurrentUserId(currentUserId);
    RDocumentEntity doc = documentMapper.toEntity(documentReceived);

    documentMapper.setFilter(doc, status);
    documentMapper.setShared(doc, shared);

    return doc;
  }

  void saveDocument(DocumentInfo documentReceived, RDocumentEntity documentToSave, boolean isLink, String TAG) {
    if ( !Objects.equals( login, settings.getLogin() ) ) {
      // Сохраняем документ только если логин не сменился (режим замещения)
      Timber.tag(TAG).d("Login changed, quit saving %s", documentToSave.getUid());
      return;
    }

    RDocumentEntity existingDoc =
      dataStore
        .select(RDocumentEntity.class)
        .where(RDocumentEntity.UID.eq( documentToSave.getUid() ))
        .get().firstOrNull();

    if ( isLink ) {
      // If link, insert only if doesn't exist
      if ( !exist( existingDoc ) ) {
        insert(documentReceived, documentToSave, isLink, TAG);
      }
    } else {
      // If not link and doesn't exist, insert
      if ( !exist( existingDoc ) ) {
        insert(documentReceived, documentToSave, isLink, TAG);
      } else {
        // If not link and exists and is from links, delete existing and insert new instead
        if ( existingDoc.isFromLinks() ) {
          new Deleter().deleteDocument(existingDoc, TAG);
          insert(documentReceived, documentToSave, isLink, TAG);
        }
      }
    }
  }

  private void insert(DocumentInfo documentReceived, RDocumentEntity documentToSave, boolean isLink, String TAG) {
    dataStore
      .insert( documentToSave )
      .toObservable()
      .subscribeOn( Schedulers.io() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        result -> {
          Timber.tag(TAG).d("Created " + result.getUid());
          doAfterUpdate(result);
          loadLinkedData( documentReceived, result, isLink );
        },
        error -> Timber.tag(TAG).e(error)
      );
  }

  void updateDocument(DocumentInfo documentReceived, RDocumentEntity documentToUpdate, String TAG) {
    dataStore
      .update( documentToUpdate )
      .subscribeOn( Schedulers.io() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        result -> {
          Timber.tag(TAG).d("Updated MD5 " + result.getMd5());
          doAfterUpdate(result);
          loadLinkedData( documentReceived, result, false );
          EventBus.getDefault().post( new UpdateCurrentDocumentEvent( result.getUid() ) );
        },
        error -> Timber.tag(TAG).e(error)
      );
  }

  abstract public void doAfterUpdate(RDocumentEntity document);

  private void loadLinkedData(DocumentInfo documentReceived, RDocumentEntity documentSaved, boolean isLink) {
    if ( !isLink ) {
      loadImages( documentSaved.getImages() );
      loadLinks( documentReceived.getLinks(), documentReceived.getUid() );
      loadCards( documentReceived.getRoute() );
    }
  }

  private void loadImages(Set<RImage> images) {
    if ( notEmpty( images ) ) {
      for (RImage _image : images) {
        settings.addTotalDocCount(1);
        RImageEntity image = (RImageEntity) _image;
        jobManager.addJobInBackground( new DownloadFileJob( settings.getHost(), image.getPath(), image.getMd5() + "_" + image.getTitle(), image.getId(), login ) );
      }
    }
  }

  private void loadLinks(List<String> links, String parentUid) {
    if ( notEmpty( links ) ) {
      int i = 0;
      for (String link : links) {
        // For the first link in the list save link's registration number in the first link field
        loadLinkedDoc( link, parentUid, i == 0 );
        i++;
      }
    }
  }

  private void loadCards(Route route) {
    if ( exist( route ) ) {
      for (Step step : nullGuard( route.getSteps() )) {
        for (Card card : nullGuard( step.getCards() )) {
          loadLinkedDoc( card.getUid(), null, false );
        }
      }
    }
  }

  // Return empty list if input list is null
  private <T> List<T> nullGuard(List<T> list) {
    return list != null ? list : Collections.EMPTY_LIST;
  }

  private void loadLinkedDoc(String linkUid, String parentUid, boolean saveFirstLink) {
    if ( exist( linkUid ) ) {
      settings.addTotalDocCount(1);
      jobManager.addJobInBackground( new CreateLinksJob( linkUid, parentUid, saveFirstLink, login, currentUserId ) );
    }
  }

  // True, если текущий статус какого-либо экземпляра адресован текущему пользователю
  // или согласно маршруту документ должен поступить текущему пользователю.
  boolean addressedToCurrentUser(DocumentInfo document, RDocumentEntity documentEntity, DocumentMapper documentMapper) {
    boolean result = false;

    for (Exemplar exemplar : nullGuard( document.getExemplars() ) ) {
      List<Status> statuses = exemplar.getStatuses();
      if ( notEmpty( statuses ) ) {
        Status currentStatus = statuses.get( statuses.size() - 1 );
        if ( Objects.equals( currentStatus.getAddressedToId(), currentUserId ) ) {
          if ( Objects.equals( currentStatus.getStatusCode(), V2FilterType.PRIMARY.getName() )
            || Objects.equals( currentStatus.getStatusCode(), V2FilterType.FOR_REPORT.getName() ) ) {
            result = true;
            break;
          }
        }
      }
    }

    if ( document.getRoute() != null && document.getRoute().getSteps() != null  ) {
      List<Step> steps = document.getRoute().getSteps();

      // Сначала смотрим шаги Подписывающие, потом Согласующие
      List<String> titles = new ArrayList<>();
      titles.add("Подписывающие");
      titles.add("Согласующие");

      for (String title : titles) {
        // resolved https://tasks.n-core.ru/browse/MPSED-2149
        // Блок подписание в маршруте прохождения
        // Идем по шагам в обратном порядке, так как, например,
        // может быть несколько блоков Подписывающие с одним и тем же пользователем,
        // а нам нужен последний актуальный
        for (int i = steps.size() - 1; i >= 0; i-- ) {
          Step step = steps.get(i);
          if ( Objects.equals( step.getTitle(), title) ) {
            if ( checkStep(step, title, documentEntity, documentMapper) ) {
              return true;
            }
          }
        }
      }
    }

    return result;
  }

  private boolean checkStep(Step step, String title, RDocumentEntity documentEntity, DocumentMapper documentMapper) {
    for ( Person person : nullGuard( step.getPeople() ) ) {
      if ( Objects.equals( person.getOfficialId(), currentUserId ) ) {
        List<Action> actions = person.getActions();
        if ( notEmpty( actions ) ) {
          String lastActionStatus = actions.get( actions.size() - 1 ).getStatus();
          if ( !lastActionStatus.toLowerCase().contains("отклонено")
            && !lastActionStatus.toLowerCase().contains("согласовано")
            && !lastActionStatus.toLowerCase().contains("подписано") ) {
            if ( title.equals( "Подписывающие" ) ) {
              documentMapper.setFilter( documentEntity, V2FilterType.SIGNING.getName() );
            } else {
              documentMapper.setFilter( documentEntity, V2FilterType.APPROVAL.getName() );
            }
            return true;
          }
        }
      }
    }

    return false;
  }
}