package sapotero.rxtest.jobs.bus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.EventBus;

import sapotero.rxtest.db.mapper.DocumentMapper;
import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.events.stepper.load.StepperLoadDocumentEvent;
import sapotero.rxtest.retrofit.models.document.DocumentInfo;

// Creates documents from processed folder (no journal,
public class CreateProcessedJob extends DocProjJob {

  public static final int PRIORITY = 1;

  private String TAG = this.getClass().getSimpleName();

  private String uid;
  private String folder;

  public CreateProcessedJob(String uid, String folder) {
    super( new Params(PRIORITY).requireNetwork().persist() );
    this.uid = uid;
    this.folder = folder;
  }

  @Override
  public void onAdded() {
  }

  @Override
  public void onRun() throws Throwable {
    loadDocument(uid, TAG);
  }

  @Override
  public void doAfterLoad(DocumentInfo document) {
    DocumentMapper documentMapper = mappers.getDocumentMapper();
    RDocumentEntity doc = new RDocumentEntity();

    documentMapper.setSimpleFields(doc, document);
    documentMapper.setNestedFields(doc, document, true);

    documentMapper.setJournal(doc, "");
    documentMapper.setFilter(doc, "");
    documentMapper.setShared(doc, false);
    doc.setFolder(folder);
    doc.setFromProcessedFolder(true);
    doc.setProcessed(true);

    saveDocument(document, doc, false, TAG);
  }

  @Override
  protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
    return RetryConstraint.createExponentialBackoff(runCount, 1000);
  }

  @Override
  protected void onCancel(@CancelReason int cancelReason, @Nullable Throwable throwable) {
    // Job has exceeded retry attempts or shouldReRunOnThrowable() has decided to cancel.
    EventBus.getDefault().post( new StepperLoadDocumentEvent("Error creating processed document (job cancelled)") );
  }
}
