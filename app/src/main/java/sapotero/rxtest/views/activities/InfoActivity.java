package sapotero.rxtest.views.activities;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.sqlbrite.BriteDatabase;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import sapotero.rxtest.EsdConfig;
import sapotero.rxtest.R;
import sapotero.rxtest.db.Auth;
import sapotero.rxtest.models.document.Decision;
import sapotero.rxtest.models.document.DocumentInfo;
import sapotero.rxtest.retrofit.DocumentService;
import sapotero.rxtest.views.adapters.DocumentsAdapter;
import sapotero.rxtest.views.adapters.TabPagerAdapter;
import sapotero.rxtest.views.fragments.InfoCardFragment;

public class InfoActivity extends AppCompatActivity implements InfoCardFragment.OnFragmentInteractionListener {

  private  TextView uid;
  private  TextView sort_key;
  private  TextView title;
  private  TextView registration_number;
  private  TextView urgency;
  private  TextView short_description;
  private  TextView comment;
  private  TextView external_document_number;
  private  TextView receipt_date;
  private  TextView signer;
  private  TextView organisation;

  private  TableLayout decision_table;
  private  TableLayout route_table;
  private  TableRow decision_row;
  private  TableRow route_row;

  private static byte[] CARD;

  private  WebView info_card;

  private static String TOKEN    = "";
  private static String LOGIN    = "";
  private static String PASSWORD = "";
  private static Integer POSITION = 0;
  private View loader;

  private DocumentInfo DOCUMENT;

  private Auth Auth;

  @Inject
  BriteDatabase db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(R.style.AppTheme);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_info);

    Auth = new Auth(this);

    uid                      = (TextView) findViewById(R.id._uid);
    sort_key                 = (TextView) findViewById(R.id.SortKey);
    title                    = (TextView) findViewById(R.id._title);
    registration_number      = (TextView) findViewById(R.id.registration_number);
    urgency                  = (TextView) findViewById(R.id.urgency);
    short_description        = (TextView) findViewById(R.id.short_description);
    comment                  = (TextView) findViewById(R.id.comment);
    external_document_number = (TextView) findViewById(R.id.external_document_number);
    receipt_date             = (TextView) findViewById(R.id.receipt_date);

    decision_table = (TableLayout) findViewById(R.id.decision_table);
    decision_row = (TableRow) findViewById(R.id.decision_row);

    route_table = (TableLayout) findViewById(R.id.route_table);
    route_row = (TableRow) findViewById(R.id.route_row);

//    info_card = (WebView) findViewById(R.id.info_card);

    loader  = findViewById(R.id.loader);
    loader.setVisibility(ProgressBar.VISIBLE);

    signer = (TextView) findViewById(R.id.signer);
    organisation = (TextView) findViewById(R.id.organisation);

    Bundle extras = getIntent().getExtras();


    // Get the ViewPager and set it's PagerAdapter so that it can display items
    ViewPager viewPager = (ViewPager) findViewById(R.id.tab_main);
    viewPager.setAdapter( new TabPagerAdapter(getSupportFragmentManager(), InfoActivity.this) );


    // Give the TabLayout the ViewPager
    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(viewPager);
    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        if ( Objects.equals( tab.getPosition(), 1 ) ) {
          WebView webView = (WebView) findViewById(R.id.web_infocard);
          try {
            if ( CARD.length != 0 ){

            }
            webView.loadData( new String(CARD, "UTF-8"), "text/html; charset=utf-8", "utf-8" );
          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
          }
        }
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });

    if (extras != null) {
        LOGIN    = extras.getString( EsdConfig.LOGIN);
        TOKEN    = extras.getString( EsdConfig.TOKEN);
        PASSWORD = extras.getString( EsdConfig.PASSWORD);
        POSITION = extras.getInt(String.valueOf(EsdConfig.POSITION));

        Log.d( "__INTENT", LOGIN );
        Log.d( "__INTENT", PASSWORD );
        Log.d( "__INTENT", TOKEN );

        DocumentsAdapter rvAdapter = (DocumentsAdapter) MainActivity.rv.getAdapter();

        Retrofit retrofit = new Retrofit.Builder()
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .baseUrl("http://mobile.esd.n-core.ru/v3/documents/")
          .build();

        DocumentService documentService = retrofit.create( DocumentService.class );

        Observable<DocumentInfo> info = documentService.getInfo(
          rvAdapter.getItem(POSITION).getUid(),
          LOGIN,
          TOKEN
        );

        info.subscribeOn( Schedulers.newThread() )
          .observeOn( AndroidSchedulers.mainThread() )
          .subscribe(
            data -> {

              DOCUMENT = data;

              loader.setVisibility(ProgressBar.INVISIBLE);

              title.setText( data.getTitle() );
              uid.setText( data.getUid() );
  //            sort_key.setText( data.getSortKey() );
              registration_number.setText(data.getRegistrationNumber());
              urgency.setText( data.getUrgency() );
              short_description.setText( data.getShortDescription() );
              external_document_number.setText(data.getExternalDocumentNumber());
              receipt_date.setText(data.getReceiptDate());
              comment.setText( data.getComment() );

              signer.setText( data.getSigner().getName() );
              organisation.setText( data.getSigner().getOrganisation() );

              if ( data.getDecisions().size() >= 1 ){
                Log.d( "__ERROR", String.valueOf(data.getDecisions().size()));
                createDecisionTableHeader();

                for (Decision decision: data.getDecisions()) {
                  addRowToDecisionTable( decision );
                }
              } else {
                decision_row.setVisibility(TableRow.INVISIBLE);
              }

              CARD = Base64.decode( data.getInfoCard().getBytes(), Base64.DEFAULT );

            },
            error -> {
              loader.setVisibility(ProgressBar.INVISIBLE);
                Log.d( "_ERROR", error.getMessage() );
                Toast.makeText( this, error.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.info, menu);

    menu
      .add(0, 0, 0, "Информационная карточка")
      .setIcon(android.R.drawable.ic_dialog_info)
      .setOnMenuItemClickListener(
        item -> {

          ArrayList<ArrayList<String>> result = Auth.select("*", null, null, null, null);
          Log.d( "_DATA", result.toString() );

          return true;
      })
      .setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS);

    menu
      .add(0, 0, 0, "Добавить")
      .setIcon(android.R.drawable.ic_input_add)
      .setOnMenuItemClickListener(
        item -> {

          if ( Auth.hasUser( LOGIN ) ){
            Auth.update( Auth.token, TOKEN, Auth.login, LOGIN );
          } else {
            Auth.insert(LOGIN, TOKEN, null, null);
          };

          return true;
        })
      .setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS);

    menu
      .add(0, 0, 0, "Удалить")
      .setIcon(android.R.drawable.ic_delete)
      .setOnMenuItemClickListener(
        item -> {

          Auth.deleteAll();

          return true;
        })
      .setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS);

    menu
      .add(0, 0, 0, "Insert")
      .setIcon(android.R.drawable.ic_menu_week)
      .setOnMenuItemClickListener(
        item -> {

          Observable.defer(new Func0<Observable<Observable>>() {
            @Override
            public Observable<Observable> call() {
              return Observable.just(massInsert());
            }
          });

          return true;


        })
      .setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS);

    return true;
  }

  private Observable massInsert() {
    Auth.massInsert();
    return Observable.just(1);
  }

  public void createDecisionTableHeader(){
    TableRow header = new TableRow(this);

    TextView field_num = new TextView(this);
    field_num.setText(" № ");
    field_num.setTextColor( Color.BLACK );
    header.addView(field_num);

    TextView field_type = new TextView(this);
    field_type.setText(" Тип ");
    field_type.setTextColor( Color.BLACK );
    header.addView(field_type);

    TextView field_date = new TextView(this);
    field_date.setText(" Дата ");
    field_date.setTextColor( Color.BLACK );
    header.addView(field_date);

    TextView field_resolution = new TextView(this);
    field_resolution.setText(" Резолюция ");
    field_resolution.setTextColor( Color.BLACK );
    header.addView(field_resolution);

    TextView field_status = new TextView(this);
    field_status.setText(" Статус ");
    field_status.setTextColor( Color.BLACK );
    header.addView(field_status);

    decision_table.addView(header);
  }

  public void addRowToDecisionTable(Decision data){

    TableRow row = new TableRow(this);

    TextView field_num = new TextView(this);
    field_num.setText( data.getId() );
    row.addView(field_num);

    TextView field_type = new TextView(this);
    field_type.setText( data.getSigner() );
    row.addView(field_type);

    TextView field_date = new TextView(this);
    field_date.setText( data.getDate() );
    row.addView(field_date);

    TextView field_resolution = new TextView(this);
    field_resolution.setText( data.getLetterhead() );
    row.addView(field_resolution);

    TextView field_status = new TextView(this);
    field_status.setText(data.getApproved() ? "Утверждена" : "Не утверждена" );
    row.addView(field_status);

    decision_table.addView(row);

  }

  @Override
  public void onFragmentInteraction(Uri uri) {

  }


  @Override
  protected void onDestroy() {

    Auth.close();
    super.onDestroy();
  }

}
