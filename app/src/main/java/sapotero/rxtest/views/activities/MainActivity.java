package sapotero.rxtest.views.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import sapotero.rxtest.R;
import sapotero.rxtest.application.EsdApplication;
import sapotero.rxtest.application.config.Constant;
import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.db.requery.models.RSignerEntity;
import sapotero.rxtest.events.bus.GetDocumentInfoEvent;
import sapotero.rxtest.events.bus.UpdateDocumentJobEvent;
import sapotero.rxtest.events.rx.InsertRxDocumentsEvent;
import sapotero.rxtest.jobs.bus.UpdateAuthTokenJob;
import sapotero.rxtest.retrofit.DocumentsService;
import sapotero.rxtest.retrofit.models.Oshs;
import sapotero.rxtest.retrofit.models.documents.Document;
import sapotero.rxtest.retrofit.models.documents.Documents;
import sapotero.rxtest.retrofit.models.documents.Signer;
import sapotero.rxtest.retrofit.utils.MeService;
import sapotero.rxtest.retrofit.utils.RetrofitManager;
import sapotero.rxtest.views.adapters.DocumentsAdapter;
import sapotero.rxtest.views.adapters.models.FilterItem;
import sapotero.rxtest.views.adapters.pagination.PagingRecyclerViewAdapter;
import sapotero.rxtest.views.adapters.utils.StatusAdapter;
import sapotero.rxtest.views.views.CircleLeftArrow;
import sapotero.rxtest.views.views.CircleRightArrow;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

  private Preference<String> TOKEN;
  private Preference<String> LOGIN;
  private Preference<String> PASSWORD;

  private String total;
  private String TAG = MainActivity.class.getSimpleName();

  @SuppressLint("StaticFieldLeak")
  private static MainActivity context;

  @SuppressLint("StaticFieldLeak")
  protected static RecyclerView rvv;

  @BindView(R.id.documentsRecycleView) RecyclerView rv;
  @BindView(R.id.progressBar) View progressBar;
  @BindView(R.id.DOCUMENT_TYPE) Spinner DOCUMENT_TYPE_SELECTOR;
  @BindView(R.id.JOURNAL_TYPE)  Spinner JOURNAL_TYPE_SELECTOR;
  @BindView(R.id.ORGANIZATION)  Spinner ORGANIZATION_SELECTOR;

  @BindView(R.id.activity_main_right_button) CircleRightArrow rightArrow;
  @BindView(R.id.activity_main_left_button)  CircleLeftArrow leftArrow;



  @BindView(R.id.documents_empty_list)  View documents_empty_list;


  @BindView(R.id.toolbar) Toolbar toolbar;

  @Inject JobManager jobManager;
  @Inject OkHttpClient okHttpClient;
  @Inject RxSharedPreferences settings;
  @Inject SingleEntityStore<Persistable> dataStore;

  private StatusAdapter filterAdapter;
  private List<sapotero.rxtest.retrofit.models.documents.Document> loaded_documents;
  private DrawerBuilder drawer;

  CompositeSubscription subscriptions;

  private final int SETTINGS_VIEW_TYPE_ALL                = 10;
  private final int SETTINGS_VIEW_TYPE_INCOMING_DOCUMENTS = 11;
  private final int SETTINGS_VIEW_TYPE_CITIZEN_REQUESTS   = 12;
  private final int SETTINGS_VIEW_TYPE_INCOMING_ORDERS    = 13;
  private final int SETTINGS_VIEW_TYPE_INTERNAL           = 14;
  private final int SETTINGS_VIEW_TYPE_ORDERS             = 15;
  private final int SETTINGS_VIEW_TYPE_ORDERS_MVD         = 16;
  private final int SETTINGS_VIEW_TYPE_ORDERS_DDO         = 17;
  private final int SETTINGS_VIEW_TYPE_APPROVE            = 18;

  private final int SETTINGS_VIEW                         = 20;
  private final int SETTINGS_TEMPLATES                    = 21;
  private final int SETTINGS_TEMPLATES_OFF                = 22;
  private Subscription loader;
  private PagingRecyclerViewAdapter recyclerViewAdapter;
  private Subscription pagingSubscription;
  private DocumentsAdapter RAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    context = this;
    ButterKnife.bind(this);
    EsdApplication.getComponent(this).inject(this);

    loadSettings();

    progressBar.setVisibility(ProgressBar.INVISIBLE);
    setAdapters();

    GridLayoutManager gridLayoutManager = new GridLayoutManager( this, 2, GridLayoutManager.VERTICAL, false );
    rv.setLayoutManager(gridLayoutManager);

    loadMe();


    toolbar.setTitle("Все документы");
    toolbar.setTitleTextColor( getResources().getColor( R.color.md_grey_100 ) );
    toolbar.setSubtitleTextColor( getResources().getColor( R.color.md_grey_400 ) );

    toolbar.inflateMenu(R.menu.info);
    toolbar.setOnMenuItemClickListener(item -> {
      switch ( item.getItemId() ){
        case R.id.action_test:

          dataStore
            .select(RDocumentEntity.class)
            .get()
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
              doc -> {
                RSignerEntity sign = (RSignerEntity) doc.getSigner();
                Timber.d("DOCUMENT Count" + sign.getName() );
              }
            );

          break;
        default:

          try {
            jobManager.addJobInBackground( new UpdateAuthTokenJob());
          } catch ( Exception e){
            Log.d( "_ERROR", e.toString() );
          }
          break;
      }
      return false;
    });
  }

  private void drawer_build_bottom() {
    drawer
      .addDrawerItems(

        new SectionDrawerItem().withName(R.string.drawer_item_settings),

        new SecondaryDrawerItem()
          .withName(R.string.drawer_item_settings_account)
          .withIcon(MaterialDesignIconic.Icon.gmi_accounts)
          .withIdentifier(SETTINGS_VIEW),
        new SecondaryDrawerItem()
          .withName(R.string.drawer_item_settings_templates)
          .withIcon(MaterialDesignIconic.Icon.gmi_comment_edit)
          .withIdentifier(SETTINGS_TEMPLATES),
        new SecondaryDrawerItem()
          .withName(R.string.drawer_item_settings_templates_off)
          .withIcon(MaterialDesignIconic.Icon.gmi_comment_list)
          .withIdentifier(SETTINGS_TEMPLATES_OFF),

        new DividerDrawerItem(),
        new SecondaryDrawerItem()
          .withName(R.string.drawer_item_debug)
          .withIcon(MaterialDesignIconic.Icon.gmi_developer_board)
          .withIdentifier(99)
      )
      .withOnDrawerItemClickListener(
        (view, position, drawerItem) -> {

          Class<?> activity;
          switch ((int) drawerItem.getIdentifier()){
            case SETTINGS_VIEW:
              activity = SettingsActivity.class;
              break;
            case SETTINGS_TEMPLATES:
              activity = SettingsActivity.class;
              break;
            case SETTINGS_TEMPLATES_OFF:
              activity = SettingsActivity.class;
              break;
            default:
              activity = SettingsActivity.class;
              break;
          }

          Timber.tag(TAG).i( String.valueOf(view) );
          Timber.tag(TAG).i( String.valueOf(position) );
          Timber.tag(TAG).i( String.valueOf( drawerItem.getIdentifier() ));

          Intent intent = new Intent(this, activity);
          startActivity(intent);

          return false;

        }
      )
      .build();
  }

  private void drawer_build_head() {
    AccountHeader headerResult = new AccountHeaderBuilder()
      .withActivity(this)
      .withHeaderBackground(R.drawable.header)
      .addProfiles(
        new ProfileDrawerItem()
          .withName("Admin")
          .withEmail("admin_id")
          .withIcon(MaterialDesignIconic.Icon.gmi_account)
      )
      .withOnAccountHeaderListener(
        (view, profile, currentProfile) -> false
      )
      .build();

    drawer = new DrawerBuilder()
      .withActivity(this)
      .withToolbar(toolbar)
      .withActionBarDrawerToggle(true)
      .withHeader(R.layout.drawer_header)
      .withAccountHeader(headerResult);

    drawer.addDrawerItems(
      new SectionDrawerItem().withName( R.string.drawer_item_journals )
    );
  }

  private void loadSettings() {
    LOGIN = settings.getString("login");
    PASSWORD = settings.getString("password");
    TOKEN = settings.getString("token");

    Timber.tag(TAG).v("LOGIN: "+ LOGIN.get() );
    Timber.tag(TAG).v("PASSWORD: "+ PASSWORD.get() );
    Timber.tag(TAG).v("TOKEN: "+ TOKEN.get() );
  }

  @Override
  public void onStart() {
    super.onStart();

    if ( subscriptions == null ){
      subscriptions = new CompositeSubscription();
    }

    if ( !EventBus.getDefault().isRegistered(this) ){
      EventBus.getDefault().register(this);
    }
  }


  public void rxSettings(){
    drawer_build_head();

    Preference<Set<String>> set = settings.getStringSet("settings_view_journals");
//    set.get().forEach(this::drawer_add_item);

    for (String journal: set.get() ) {
      drawer_add_item(journal);
    }

    drawer_build_bottom();
  }

  private void drawer_add_item(String item) {
//    settings_view_start_page_values
    int index = Arrays.asList((getResources().getStringArray(R.array.settings_view_start_page_values))).indexOf(item);
    String title = Arrays.asList((getResources().getStringArray(R.array.settings_view_start_page))).get(index);
    Long identifier = Long.valueOf(Arrays.asList((getResources().getStringArray(R.array.settings_view_start_page_identifier))).get(index));

    Timber.tag(TAG).v(" !index "+index + " " + title);
    drawer.addDrawerItems(
      new PrimaryDrawerItem()
        .withName( title )
        .withIdentifier(identifier)
    );
  }

  @Override
  public void onResume(){
    super.onResume();

    subscriptions = new CompositeSubscription();
    rxSettings();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if ( subscriptions != null ){
      subscriptions.unsubscribe();
    }

  }

  @Override
  public void onStop() {
    if ( EventBus.getDefault().isRegistered(this) ){
      EventBus.getDefault().unregister(this);
    }
    super.onStop();
  }

  private void setAdapters() {

    List<FilterItem> filters = new ArrayList<FilterItem>();

    String[] filter_types = getResources().getStringArray(R.array.DOCUMENT_TYPES_VALUE);
    String[] filter_names = getResources().getStringArray(R.array.DOCUMENT_TYPES);

    for (int i = 0; i < filter_types.length; i++) {
      filters.add(new FilterItem( filter_names[i] , filter_types[i], "0"));
    }

    filterAdapter = new StatusAdapter(this, filters );
    DOCUMENT_TYPE_SELECTOR.setAdapter(filterAdapter);

    DOCUMENT_TYPE_SELECTOR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

        Timber.tag(TAG).e("DOCUMENT_TYPE_SELECTOR.setOnItemSelectedListener " + position + " | " +parentView.getAdapter().getCount() );

        if (position+1 == parentView.getAdapter().getCount()){
          loadFromDB();
        } else {
          loadDocuments();
        }
        toolbar.setSubtitle( filterAdapter.getItem(position).getName() );
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });

    // устанавливаем тип документов
    JOURNAL_TYPE_SELECTOR = (Spinner) findViewById(R.id.JOURNAL_TYPE);
    ArrayAdapter<CharSequence> journal_adapter = ArrayAdapter.createFromResource(this, R.array.JOURNAL_TYPES, android.R.layout.simple_spinner_item);
    journal_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    JOURNAL_TYPE_SELECTOR.setAdapter(journal_adapter);

    JOURNAL_TYPE_SELECTOR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        int spinner_pos = JOURNAL_TYPE_SELECTOR.getSelectedItemPosition();
        String[] document_type = getResources().getStringArray(R.array.JOURNAL_TYPES_VALUE);
        String type = String.valueOf(document_type[spinner_pos]);

        Toast.makeText(context, type, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });

    // устанавливаем тип документов
    ArrayAdapter<CharSequence> organization_adapter = ArrayAdapter.createFromResource(this, R.array.ORGANIZATIONS, android.R.layout.simple_spinner_item);
    organization_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    ORGANIZATION_SELECTOR.setAdapter(organization_adapter);

    ORGANIZATION_SELECTOR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });

    Observable.from(filter_types).subscribe(
      this::loadDocumentsCountByType
    );
  }

  private void loadFromDB() {
//  RxDataSource<String> rxDataSource = new RxDataSource<>(dataSet);

//    recyclerViewAdapter = new PagingRecyclerViewAdapter();
//    pagingSubscription = PaginationTool
//      .paging(rv, offset -> ResponseManager.getInstance().getEmulateResponse(offset, 20), 20)
//      .observeOn(AndroidSchedulers.mainThread())
//      .subscribe(new Subscriber<List<Document>>() {
//        @Override
//        public void onCompleted() {
//        }
//
//        @Override
//        public void onError(Throwable e) {
//        }
//
//        @Override
//        public void onNext(List<Document> items) {
//          recyclerViewAdapter.addNewItems(items);
//          recyclerViewAdapter.notifyItemInserted(recyclerViewAdapter.getItemCount() - items.size());
//        }
//      });

    RAdapter = new DocumentsAdapter(this, new ArrayList<Document>());
    rv.setAdapter(RAdapter);

    dataStore
      .select(RDocumentEntity.class)
      .get()
      .toObservable()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(doc -> {
        Document document = new Document();
        document.setUid( doc.getUid() );
        document.setMd5( doc.getMd5() );
        document.setSortKey( doc.getSortKey() );
        document.setTitle( doc.getTitle() );
        document.setRegistrationNumber( doc.getRegistrationNumber() );
        document.setRegistrationDate( doc.getRegistrationDate() );
        document.setUrgency( doc.getUrgency() );
        document.setShortDescription( doc.getShortDescription() );
        document.setComment( doc.getComment() );
        document.setExternalDocumentNumber( doc.getExternalDocumentNumber() );
        document.setReceiptDate( doc.getReceiptDate() );

        RSignerEntity r_signer = (RSignerEntity) doc.getSigner();
        Signer signer = new Signer();
        signer.setId( r_signer.getUid() );
        signer.setName( r_signer.getName() );
        signer.setOrganisation( r_signer.getOrganisation() );
        signer.setType( r_signer.getType() );
        document.setSigner(signer);

        RAdapter.addItem( document );
      });


  }

  private void loadDocumentsCountByType( String TYPE){

    Retrofit retrofit = new RetrofitManager( this, Constant.HOST + "/v3/", okHttpClient).process();
    DocumentsService documentsService = retrofit.create( DocumentsService.class );

    Observable<Documents> documents = documentsService.getDocuments( LOGIN.get(), TOKEN.get(), TYPE, 0,0);

    documents.subscribeOn( Schedulers.io() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        data -> {
          total = data.getMeta().getTotal();

          if ( total != null && Integer.valueOf(total) > 0 ){


            String[] values = getResources().getStringArray(R.array.DOCUMENT_TYPES_VALUE);

            int index = -1;
            for (int i=0;i<values.length;i++) {
              if (values[i].equals(TYPE)) {
                index = i;
                break;
              }
            }

            Timber.tag(TAG).i( TYPE + " - " + total + " | " + index );

            FilterItem filterItem = filterAdapter.getItem(index);

            filterItem.setCount( total );
            filterAdapter.notifyDataSetChanged();
          }

        },
        error -> {
          Timber.tag(TAG).d( "_ERROR", error.getMessage() );
        });

  }

  private void loadDocuments(){

    progressBar.setVisibility(ProgressBar.VISIBLE);

    Retrofit retrofit = new RetrofitManager( this, Constant.HOST + "/v3/", okHttpClient).process();
    DocumentsService documentsService = retrofit.create( DocumentsService.class );

    int spinner_pos = DOCUMENT_TYPE_SELECTOR.getSelectedItemPosition();

    String[] document_type = getResources().getStringArray(R.array.DOCUMENT_TYPES_VALUE);
    String type = String.valueOf(document_type[spinner_pos]);

    Observable<Documents> documents = documentsService.getDocuments(LOGIN.get(), TOKEN.get(), type, 100, 0);

    loader = documents.subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(
        data -> {
          progressBar.setVisibility(ProgressBar.INVISIBLE);

          List<sapotero.rxtest.retrofit.models.documents.Document> docs = data.getDocuments();

          for (Document d: docs ) {
            insertRDoc(d);
          }


          DocumentsAdapter documentsAdapter = new DocumentsAdapter(this, docs);
          rv.setAdapter(documentsAdapter);

          rvv = rv;

          if (docs.size() == 0) {
            rv.setVisibility(View.GONE);
            documents_empty_list.setVisibility(View.VISIBLE);
          } else {
            rv.setVisibility(View.VISIBLE);
            documents_empty_list.setVisibility(View.GONE);
          }

        },
        error -> {
          Log.d("_ERROR", error.getMessage());
          progressBar.setVisibility(ProgressBar.INVISIBLE);

          Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
  }

  private void insertRDoc(Document d) {
    RDocumentEntity rd = new RDocumentEntity();

    rd.setUid( d.getUid() );
    rd.setMd5( d.getMd5() );
    rd.setSortKey( d.getSortKey() );
    rd.setTitle( d.getTitle() );
    rd.setRegistrationNumber( d.getRegistrationNumber() );
    rd.setRegistrationDate( d.getRegistrationDate() );
    rd.setUrgency( d.getUrgency() );
    rd.setShortDescription( d.getShortDescription() );
    rd.setComment( d.getComment() );
    rd.setExternalDocumentNumber( d.getExternalDocumentNumber() );
    rd.setReceiptDate( d.getReceiptDate() );
    rd.setViewed( d.getViewed() );

    RSignerEntity signer = new RSignerEntity();
    signer.setUid( d.getSigner().getId() );
    signer.setName( d.getSigner().getName() );
    signer.setOrganisation( d.getSigner().getOrganisation() );
    signer.setType( d.getSigner().getType() );

    rd.setSigner( signer );

    dataStore.insert(rd)
      .toObservable()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(
        result -> {
          Timber.d("inserted ++ " + result.getUid());
        },
        error ->{
          Timber.d("insertedError ++ " + error.getStackTrace() );
        }
      );
  }

  private void loadMe() {
    Retrofit retrofit = new RetrofitManager( this, Constant.HOST + "/v3/", okHttpClient).process();
    MeService meService = retrofit.create( MeService.class );

    Observable<Oshs> info = meService.get( LOGIN.get(), TOKEN.get());

    info.subscribeOn( Schedulers.io() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        me -> {
          Timber.tag(TAG).d( "ME " +  me.getName() );
          Preference<String> current_user = settings.getString("current_user");
          current_user.set( new Gson().toJson( me, Oshs.class ) );
        },
        error -> {
          Timber.tag(TAG).d( "ERROR " + error.getMessage() );
        });

  }

  @OnClick(R.id.activity_main_left_button)
  public void setLeftArrowArrow(){
    showNextType(false);
  }

  @OnClick(R.id.activity_main_right_button)
  public void setRightArrow(){
    showNextType(true);
  }

  public void showNextType( Boolean next){
    unsubscribe();
    int position = next ? filterAdapter.next() : filterAdapter.prev();
    DOCUMENT_TYPE_SELECTOR.setSelection(position);
  }

  public void unsubscribe(){
    if (loader != null && !loader.isUnsubscribed()) {
      loader.unsubscribe();
    }
  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  public void onMessageEvent(GetDocumentInfoEvent event) {
    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(UpdateDocumentJobEvent event) {
    RAdapter.findByUid(event.uid);
//    Toast.makeText(context, event.uid, Toast.LENGTH_SHORT).show();
  }


  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(InsertRxDocumentsEvent event) {
    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show();
  }

}
