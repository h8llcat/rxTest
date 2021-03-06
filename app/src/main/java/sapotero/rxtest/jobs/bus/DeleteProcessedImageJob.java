package sapotero.rxtest.jobs.bus;

import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import java.io.File;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.db.requery.models.images.RImage;
import sapotero.rxtest.db.requery.models.images.RImageEntity;
import sapotero.rxtest.retrofit.models.document.Image;
import sapotero.rxtest.utils.memory.models.InMemoryDocument;
import timber.log.Timber;


public class DeleteProcessedImageJob extends BaseJob {

  public static final int PRIORITY = 10;
  private final String uid;

  public DeleteProcessedImageJob(String uid) {
    super( new Params(PRIORITY).requireNetwork().persist() );
    this.uid = uid;
  }

  @Override
  public void onAdded() {
    if (uid != null) {
      Timber.e("DOCUMENT UID: %s", uid);
      RDocumentEntity doc = dataStore
        .select(RDocumentEntity.class)
        .where(RDocumentEntity.UID.eq(uid))
        .get().firstOrNull();

      if (doc != null) {
        long current = new Date().getTime()/1000;

        if (current - doc.getProcessedDate() > 0) {
          Set<RImage> images = doc.getImages();
          if (images.size() > 0){
            for (RImage img: images) {
              RImageEntity image = (RImageEntity) img;

              if ( !image.isDeleted() ) {
                File file = new File(getApplicationContext().getFilesDir(), image.getFileName() );
                boolean result = file.delete();
                if ( result ) {
                  Timber.e("DELETED IMAGE FILE: %s", image.getFileName());
                }

                // Set deleted in memory
                InMemoryDocument inMemoryDocument = store.getDocuments().get( doc.getUid() );
                if ( inMemoryDocument != null && inMemoryDocument.getImages() != null ) {
                  for ( Image inMemoryImage : inMemoryDocument.getImages() ) {
                    if ( Objects.equals( inMemoryImage.getFileName(), image.getFileName() ) ) {
                      inMemoryImage.setDeleted( true );
                      break;
                    }
                  }
                }

                // Set deleted in DB
                image.setDeleted(true);
                dataStore
                  .update(image)
                  .subscribeOn( Schedulers.io() )
                  .observeOn( AndroidSchedulers.mainThread() )
                  .subscribe(
                    data -> Timber.e("DELETED IMAGE: %s", data.getId()),
                    Timber::e
                  );

              } else {
                Timber.e("ALREADY DELETED IMAGE: %s", image.getId());
              }

            }
          }
        }

      }
    }

    /*
    * 0 - добавить поле грохнутое в RImage
    * 1 - найти все образы, грохнуть файлы, отметить образ как грохнутый
    * 2 - на предпросмотре образа - показать кнопку загрузить заново
    * */
  }

  @Override
  public void onRun() throws Throwable {

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