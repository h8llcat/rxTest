package sapotero.rxtest.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import sapotero.rxtest.R;
import sapotero.rxtest.application.EsdApplication;
import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.db.requery.models.images.RImage;
import sapotero.rxtest.db.requery.models.images.RImageEntity;
import sapotero.rxtest.jobs.bus.ReloadProcessedImageJob;
import sapotero.rxtest.retrofit.models.document.Image;
import sapotero.rxtest.utils.Settings;
import sapotero.rxtest.utils.memory.MemoryStore;
import sapotero.rxtest.utils.memory.fields.LabelType;
import sapotero.rxtest.utils.memory.utils.Transaction;
import sapotero.rxtest.views.activities.DocumentImageFullScreenActivity;
import sapotero.rxtest.views.adapters.DocumentLinkAdapter;
import sapotero.rxtest.views.custom.CircleLeftArrow;
import sapotero.rxtest.views.custom.CircleRightArrow;
import timber.log.Timber;

public class InfoCardDocumentsFragment extends Fragment implements AdapterView.OnItemClickListener, GestureDetector.OnDoubleTapListener {

  public static final int REQUEST_CODE_INDEX = 1;
  @Inject Settings settings;
  @Inject SingleEntityStore<Persistable> dataStore;
  @Inject MemoryStore store;
  @Inject JobManager jobManager;

  private OnFragmentInteractionListener mListener;
  private Context mContext;
  private String TAG = this.getClass().getSimpleName();

  private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";

  @BindView(R.id.pdfView) PDFView pdfView;

  @BindView(R.id.info_card_pdf_fullscreen_prev_document) CircleLeftArrow prev_document;
  @BindView(R.id.info_card_pdf_fullscreen_next_document) CircleRightArrow next_document;
  @BindView(R.id.info_card_pdf_fullscreen_document_counter) TextView document_counter;
  @BindView(R.id.info_card_pdf_fullscreen_page_title)       TextView document_title;
  @BindView(R.id.info_card_pdf_fullscreen_page_counter)     TextView page_counter;
  @BindView(R.id.info_card_pdf_fullscreen_button) FrameLayout fullscreen;
  @BindView(R.id.deleted_image) FrameLayout deletedImage;
  @BindView(R.id.broken_image) FrameLayout broken_image;
  @BindView(R.id.info_card_pdf_reload) Button reloadImageButton;

  @BindView(R.id.info_card_pdf_no_files) TextView no_files;
  @BindView(R.id.info_card_pdf_wrapper)  FrameLayout pdf_wrapper;

  @BindView(R.id.fragment_info_card_urgency_title) TextView urgency;

  @BindView(R.id.open_in_another_app_wrapper) LinearLayout open_in_another_app_wrapper;

  private int index = 0;

  private DocumentLinkAdapter adapter;
  private String uid;
  private Boolean withOutZoom = false;

  private File file;
  private String contentType;

  public InfoCardDocumentsFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_info_card_documents, container, false);
    ButterKnife.bind(this, view);
    EsdApplication.getManagerComponent().inject( this );

    if (null != savedInstanceState) {
      index = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
    }

    updateDocument();

    return view;
  }

  public void updateDocument(){

    ArrayList<Image> documents = new ArrayList<Image>();
    adapter = new DocumentLinkAdapter(mContext, documents);

    RDocumentEntity document = dataStore
      .select(RDocumentEntity.class)
      .where(RDocumentEntity.UID.eq(uid == null ? settings.getUid() : uid))
      .get()
      .firstOrNull();

    Timber.tag("IMAGESSS").e("%s", document.getUid() );

    //resolved https://tasks.n-core.ru/browse/MVDESD-12626 - срочность
    if ( document.getUrgency() != null ){
      urgency.setVisibility(View.VISIBLE);
    }

    index = settings.getImageIndex();

    if (document.getImages().size() > 0){
      adapter.clear();

      List<RImageEntity> tmp = new ArrayList<>();

      for (RImage image : document.getImages()) {
        RImageEntity img = (RImageEntity) image;
        Timber.tag(TAG).i("image " + img.getTitle() );
        tmp.add(img);
      }

      try {
        Collections.sort(tmp, (o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()));
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        Collections.sort(tmp, (o1, o2) -> o1.getNumber().compareTo( o2.getNumber() ));
      } catch (Exception e) {
        e.printStackTrace();
      }


      for (RImageEntity image : tmp) {
        Timber.tag(TAG).e("image: %s %s", image.getNumber(), image.getCreatedAt());
        adapter.add( image );
      }

      showPdf();

      no_files.setVisibility(View.GONE);
      pdf_wrapper.setVisibility(View.VISIBLE);

    } else {
      disablePdfView();
      no_files.setVisibility(View.VISIBLE);
      pdf_wrapper.setVisibility(View.GONE);
      open_in_another_app_wrapper.setVisibility(View.GONE);
    }

  }

  private void setPdfPreview() throws FileNotFoundException {

    Image image = adapter.getItem(index);

    if ( image.isDeleted() ){
      showDownloadButton();
    } else {

      contentType = image.getContentType();
      document_title.setText( image.getTitle() );

      file = new File(getContext().getFilesDir(), String.format( "%s_%s", image.getMd5(), image.getTitle() ));

      if ( Objects.equals(contentType, "application/pdf") ) {
        InputStream targetStream = new FileInputStream(file);

        if (file.exists()) {
          pdfView
            .fromStream(targetStream)
//         .fromFile( file )
            .enableSwipe(true)
            .enableDoubletap(true)
            .defaultPage(0)
            .swipeHorizontal(false)
            .onRender((nbPages, pageWidth, pageHeight) -> pdfView.fitToWidth())
            .onLoad(nbPages -> {
              Timber.tag(TAG).i(" onLoad");
            })
            .onError(t -> {
              Timber.tag(TAG).i(" onError");
            })
            .onDraw((canvas, pageWidth, pageHeight, displayedPage) -> {
              Timber.tag(TAG).i(" onDraw");
            })
            .onPageChange((page, pageCount) -> {
              Timber.tag(TAG).i(" onPageChange");
              updatePageCount();
            })
            .enableAnnotationRendering(true)
            .scrollHandle(null)
            .load();

          pdfView.useBestQuality(false);
          pdfView.setDrawingCacheEnabled(true);
          pdfView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
//          pdfView.stopFling();

//        pdfView.useBestQuality(true);
//        pdfView.setDrawingCacheEnabled(true);
//        pdfView.stopFling();
        }

        pdfView.setVisibility(View.VISIBLE);
        open_in_another_app_wrapper.setVisibility(View.GONE);
        page_counter.setVisibility(View.VISIBLE);

      } else {
        pdfView.setVisibility(View.GONE);
        open_in_another_app_wrapper.setVisibility(View.VISIBLE);
        page_counter.setVisibility(View.INVISIBLE);
      }

      updateDocumentCount();
      updatePageCount();
      updateZoomVisibility();

    }



  }

  private void showDownloadButton() {
    deletedImage.setVisibility(View.VISIBLE);
  }

  private void updateZoomVisibility() {
    if (withOutZoom){
      fullscreen.setVisibility(View.GONE);
    }
    deletedImage.setVisibility(View.GONE);
  }

  public void updateDocumentCount(){
    document_counter.setText( String.format("%s из %s", index + 1, adapter.getCount()) );
  }

  public void updatePageCount(){
    page_counter.setText( String.format("%s из %s страниц", pdfView.getCurrentPage() + 1, pdfView.getPageCount()) );
  }

  @OnClick(R.id.info_card_pdf_fullscreen_prev_document)
  public void setLeftArrowArrow() {
    Timber.tag(TAG).i( "BEFORE %s - %s", index, adapter.getCount() );
    if ( index <= 0 ){
      index = adapter.getCount()-1;
    } else {
      index--;
    }
    Timber.tag(TAG).i( "AFTER %s - %s", index, adapter.getCount() );

    settings.setImageIndex( index );
    showPdf();
  }

  @OnClick(R.id.info_card_pdf_reload)
  public void reloadImage(){

    Transaction transaction = new Transaction();
    transaction
      .from( store.getDocuments().get( settings.getUid() ) )
      .setLabel(LabelType.SYNC);
    store.process( transaction );

    jobManager.addJobInBackground( new ReloadProcessedImageJob( settings.getUid() ) );

    getActivity().finish();
  }

  @OnClick(R.id.info_card_pdf_fullscreen_next_document)
  public void setRightArrow() {
    Timber.tag(TAG).i( "BEFORE %s - %s", index, adapter.getCount() );
    if ( index >= adapter.getCount()-1 ){
      index = 0;
    } else {
      index++;
    }
    Timber.tag(TAG).i( "AFTER %s - %s", index, adapter.getCount() );

    settings.setImageIndex( index );
    showPdf();
  }

  private void showPdf() {
    try {
      hideBrokenImage();
      setPdfPreview();
    } catch (FileNotFoundException e) {
      Timber.e(e);
      disablePdfView();
      showBrokenImage();
    }
  }

  private void showBrokenImage() {
    broken_image.setVisibility(View.VISIBLE);
  }

  private void disablePdfView() {
    pdfView.setOnDragListener(null);
    pdfView.setOnTouchListener(null);
    pdfView.recycle();
  }

  private void hideBrokenImage() {
    broken_image.setVisibility(View.GONE);
  }

  @OnClick(R.id.info_card_pdf_fullscreen_button)
  public void fullscreen() {
    // Start DocumentImageFullScreenActivity, which uses another instance of this fragment for full screen PDF view.
    Intent intent = DocumentImageFullScreenActivity.newIntent( getContext(), adapter.getItems(), index );
    startActivityForResult(intent, REQUEST_CODE_INDEX);
  }

  // This is called, when DocumentImageFullScreenActivity returns
  // (needed to switch to the image shown in full screen).
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if ( resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_INDEX ) {
      index = settings.getImageIndex();
      showPdf();
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
      mContext = context;
    } else {
      throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();

//    if ( EventBus.getDefault().isRegistered(this) ){
//      EventBus.getDefault().unregister(this);
//    }

    pdfView.recycle();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean onSingleTapConfirmed(MotionEvent e) {
    return false;
  }

  @Override
  public boolean onDoubleTap(MotionEvent motionEvent) {
    return false;
  }


  @Override
  public boolean onDoubleTapEvent(MotionEvent e) {
    return false;
  }

  public InfoCardDocumentsFragment withUid(String uid) {
    this.uid = uid;
    return this;
  }

  public InfoCardDocumentsFragment withOutZoom(Boolean withOutZoom) {
    this.withOutZoom = withOutZoom;
    return this;
  }

  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }

//  @Subscribe(threadMode = ThreadMode.MAIN)
//  public void onMessageEvent(FileDownloadedEvent event) {
//    Log.d("FileDownloadedEvent", event.path);
//
//  }
//
//  @Subscribe(threadMode = ThreadMode.MAIN)
//  public void onMessageEvent(UpdateCurrentDocumentEvent event) throws Exception {
//    Timber.tag(TAG).w("UpdateCurrentDocumentEvent %s", event.uid);
//    if (Objects.equals(event.uid, settings.getUid())){
//      updateDocument();
//    }
//  }

  // resolved https://tasks.n-core.ru/browse/MVDESD-13415
  // Если ЭО имеет формат, отличный от PDF, предлагать открыть во внешнем приложении
  @OnClick(R.id.open_in_another_app)
  public void openInAnotherApp() {
    if ( file != null && contentType != null) {
      Uri contentUri = FileProvider.getUriForFile(getContext(), "sed.mobile.fileprovider", file);
      Intent intent = new Intent();
      intent.setAction(Intent.ACTION_VIEW);
      intent.setDataAndType(contentUri, contentType);
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

      Intent chooser = Intent.createChooser(intent, "Открыть с помощью");

      if (intent.resolveActivity(getContext().getPackageManager()) != null) {
        startActivity(chooser);
      } else {
        Toast.makeText(getContext(), "Подходящие приложения не установлены", Toast.LENGTH_SHORT).show();
      }
    }
  }

}
