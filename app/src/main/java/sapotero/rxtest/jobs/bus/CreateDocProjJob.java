package sapotero.rxtest.jobs.bus;

import com.birbit.android.jobqueue.Params;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sapotero.rxtest.events.stepper.load.StepperLoadDocumentEvent;
import sapotero.rxtest.retrofit.models.document.DocumentInfo;
import timber.log.Timber;

abstract class CreateDocProjJob extends BaseJob {

  CreateDocProjJob(Params params) {
    super(params);
  }

  abstract public void create(DocumentInfo document);

  void loadDocument(String uid, String TAG) {
    Observable<DocumentInfo> info = getDocumentInfoObservable(uid);

    info
      .subscribeOn( Schedulers.io() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        doc -> {
          create( doc );
          EventBus.getDefault().post( new StepperLoadDocumentEvent( doc.getUid()) );
        },
        error -> {
          Timber.tag(TAG).e(error);
          EventBus.getDefault().post( new StepperLoadDocumentEvent("Error downloading document info on create") );
        }
      );
  }
}
