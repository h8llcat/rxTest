package sapotero.rxtest.db.requery.query;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.f2prateek.rx.preferences.RxSharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.query.Scalar;
import io.requery.query.WhereAndOr;
import io.requery.rx.SingleEntityStore;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import sapotero.rxtest.application.EsdApplication;
import sapotero.rxtest.db.requery.models.RDocument;
import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.db.requery.models.RSignerEntity;
import sapotero.rxtest.retrofit.models.documents.Document;
import sapotero.rxtest.views.adapters.DocumentsAdapter;
import sapotero.rxtest.views.adapters.OrganizationAdapter;
import sapotero.rxtest.views.adapters.models.OrganizationItem;
import sapotero.rxtest.views.custom.OrganizationSpinner;
import sapotero.rxtest.views.menu.MenuBuilder;
import sapotero.rxtest.views.menu.builders.ConditionBuilder;
import sapotero.rxtest.views.menu.fields.MainMenuItem;
import timber.log.Timber;

public class DBQueryBuilder {

  @Inject SingleEntityStore<Persistable> dataStore;
  @Inject RxSharedPreferences settings;

  private final String TAG = this.getClass().getSimpleName();

  private final Context    context;
  private DocumentsAdapter adapter;
  private ArrayList<ConditionBuilder> conditions;
  private ProgressBar progressBar;
  private TextView documents_empty_list;
  private CompositeSubscription subscribe;
  private OrganizationAdapter organizationAdapter;
  private OrganizationSpinner organizationSelector;
  private Boolean withFavorites;
  private MenuBuilder menuBuilder;
  private RecyclerView recyclerView;
  private MainMenuItem item;

  public DBQueryBuilder(Context context) {
    this.context = context;
    EsdApplication.getComponent(context).inject(this);
  }

  public DBQueryBuilder withAdapter(DocumentsAdapter rAdapter) {
    this.adapter = rAdapter;
    return this;
  }


  public DBQueryBuilder withEmptyView(TextView documents_empty_list) {
    this.documents_empty_list = documents_empty_list;
    return this;
  }
  public DBQueryBuilder withOrganizationsAdapter(OrganizationAdapter organization_adapter) {
    this.organizationAdapter = organization_adapter;
    return this;
  }

  public DBQueryBuilder withOrganizationSelector(OrganizationSpinner organization_selector) {
    this.organizationSelector = organization_selector;
    return this;
  }

  public DBQueryBuilder withProgressBar(ProgressBar progress) {
    this.progressBar = progress;
    return this;
  }

  public void execute(Boolean refreshSpinner){

    if (conditions != null) {


      if (refreshSpinner){
        findOrganizations();
      }


      progressBar.setVisibility(ProgressBar.VISIBLE);

      WhereAndOr<Result<RDocumentEntity>> query =
        dataStore
          .select(RDocumentEntity.class)
          .where(RDocumentEntity.USER.eq( settings.getString("login").get() ) );

      WhereAndOr<Scalar<Integer>> queryCount =
        dataStore
          .count(RDocument.class)
          .where(RDocumentEntity.USER.eq( settings.getString("login").get() ));



      Boolean hasProcessed = false;
      if ( conditions.size() > 0 ){


        for (ConditionBuilder condition : conditions ){
          Timber.tag(TAG).i( "++ %s", condition.toString() );

          switch ( condition.getCondition() ){
            case AND:
              query = query.and( condition.getField() );
              queryCount = queryCount.and( condition.getField() );
              break;
            case OR:
              query = query.or( condition.getField() );
              queryCount = queryCount.or( condition.getField() );
              break;
            default:
              break;
          }

          if (condition.getField().getLeftOperand() == RDocumentEntity.PROCESSED){
            hasProcessed = true;
          }
        }
      }

      if (!hasProcessed){
        query = query.and( RDocumentEntity.PROCESSED.eq(false) );
      }


      if (withFavorites){
        query = query.or( RDocumentEntity.FAVORITES.eq(true) );
      }

      Integer count = queryCount.get().value();
      if ( count != null || count != 0 ){
        hideEmpty();
      }

      unsubscribe();
      adapter.clear();

      if (count == 0){
        showEmpty();
      } else {
        Timber.v( "queryCount: %s", count );
        subscribe.add(
          query
            .orderBy( RDocumentEntity.SORT_KEY.desc() )
            .get()
            .toSelfObservable()
            .subscribeOn(Schedulers.io())
            .observeOn( AndroidSchedulers.mainThread() )
            .subscribe(this::addByOne, this::error)
        );
      }


    }
  }

//
//  private void addMany(List<RDocumentEntity> results) {
//
//    Timber.tag(TAG).i( "results: %s", results.size() );
//
//    if ( results.size() > 0) {
//      hideEmpty();
//
//      for (RDocumentEntity doc: results) {
//        Timber.tag(TAG).i( "mass add doc: %s", doc );
//
//        if (doc != null) {
//          RSignerEntity signer = (RSignerEntity) doc.getSigner();
//
//          boolean[] selected_index = organizationSelector.getSelected();
//          ArrayList<String> ids = new ArrayList<>();
//
//          for (int i = 0; i < selected_index.length; i++) {
//            if ( selected_index[i] ){
//              ids.add( organizationAdapter.getItem(i).getName() );
//            }          }
//
//          // resolved https://tasks.n-core.ru/browse/MVDESD-12625
//          // *1) *Фильтр по организациям.
//          String organization = signer.getOrganisation();
//
//          if (selected_index.length == 0){
//            addDocument(doc);
//          } else {
//            if ( ids.contains(organization) || withFavorites && doc.isFavorites() ){
//              addDocument(doc);
//            }
//          }
//        }
//      }
//
//    } else {
//      showEmpty();
//    }
//  }

  private void error(Throwable error) {
    Timber.tag(TAG).e(error);
  }

  public void addByOne(Result<RDocumentEntity> docs){



    docs
      .toObservable()
      .subscribeOn(Schedulers.io())
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe( doc -> {


        RSignerEntity signer = (RSignerEntity) doc.getSigner();

        boolean[] selected_index = organizationSelector.getSelected();
        ArrayList<String> ids = new ArrayList<>();

        for (int i = 0; i < selected_index.length; i++) {
          if ( selected_index[i] ){
            ids.add( organizationAdapter.getItem(i).getName() );
          }
        }

        // resolved https://tasks.n-core.ru/browse/MVDESD-12625
        // *1) *Фильтр по организациям.
        String organization = signer.getOrganisation();

        if (selected_index.length == 0){
          addDocument(doc);
        } else {
          if ( ids.contains(organization) || withFavorites && doc.isFavorites() ){
            addDocument(doc);
          }
        }

      }, this::error);

  }

  private void addDocument(RDocumentEntity doc) {
    // настройка
    // если включена настройка "Отображать документы без резолюции"
    if ( settings.getBoolean("settings_view_type_show_without_project").get() ){
      addOne(doc);
    } else {
      if ( menuBuilder.getItem().isShowAnyWay() ){
        addOne(doc);
      } else {
        if (doc.isWithDecision() != null && doc.isWithDecision()){
          addOne(doc);
        }
      }
    }
  }

  public void addList(Result<RDocumentEntity> docs){

    docs
      .toObservable()
      .subscribeOn(Schedulers.io())
      .observeOn( AndroidSchedulers.mainThread() )
      .toList()
      .subscribe( list -> {
        addList(list, recyclerView);
      }, this::error);

  }

  private void unsubscribe() {
    if (subscribe == null) {
      subscribe = new CompositeSubscription();
    }
    if (subscribe.hasSubscriptions()) {
      subscribe.clear();
    }
  }

  public void executeWithConditions(ArrayList<ConditionBuilder> conditions, boolean withFavorites, MainMenuItem item) {
    this.item = item;
    this.conditions = conditions;
    this.withFavorites = withFavorites;
    execute(true);
  }
  private void addList(List<RDocumentEntity> docs, RecyclerView recyclerView) {
    ArrayList<Document> list_dosc = new ArrayList<>();

    if ( list_dosc.size() == 0 ){
      showEmpty();
    }

    progressBar.setVisibility(ProgressBar.GONE);
    adapter.setDocuments(docs, this.recyclerView);

  }

  private void addOne(RDocumentEntity _document) {
    progressBar.setVisibility(ProgressBar.GONE);
    adapter.addItem(_document);
  }

  private void showEmpty(){
    progressBar.setVisibility(ProgressBar.GONE);
    documents_empty_list.setVisibility(View.VISIBLE);
  }

  private void hideEmpty(){
    documents_empty_list.setVisibility(View.GONE);
  }

  private void findOrganizations() {
    Timber.i( "findOrganizations" );
    organizationAdapter.clear();
    organizationSelector.clear();

    WhereAndOr<Result<RSignerEntity>> query = dataStore
      .select(RSignerEntity.class)
      .distinct()
      .join(RDocumentEntity.class)
      .on( RDocumentEntity.SIGNER_ID.eq(RSignerEntity.ID) )
      .where(RDocumentEntity.USER.eq( settings.getString("login").get() ));

    if ( conditions.size() > 0 ){

      for (ConditionBuilder condition : conditions ){
        switch ( condition.getCondition() ){
          case AND:
            query = query.and( condition.getField() );
            break;
          case OR:
            query = query.or( condition.getField() );
            break;
          default:
            break;
        }
      }
    }

    query
      .get()
      .toObservable()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .toList()
      .subscribe(

        signers -> {


          // resolved https://tasks.n-core.ru/browse/MVDESD-12625
          // Фильтр по организациям.

          HashMap< String, Integer> organizations = new HashMap< String, Integer>();


          for (RSignerEntity signer: signers){
            String key = signer.getOrganisation();

            if ( !organizations.containsKey( key ) ){
              organizations.put(key, 0);
            }

            Integer value = organizations.get(key);
            value += 1;

            organizations.put( key, value  );
          }

          for ( String organization: organizations.keySet()) {
            Timber.d( "org:  %s | %s", organization, organizations.get(organization) );
            organizationAdapter.add( new OrganizationItem( organization, organizations.get(organization) ) );
          }

        },
        error -> {
          Timber.tag("ERROR").e(error);
        }

      );

  }

  public int getFavoritesCount(){
    return dataStore
      .count(RDocumentEntity.UID)
      .where(RDocumentEntity.USER.eq( settings.getString("login").get() ) )
      .and(RDocumentEntity.FAVORITES.eq(true))
      .and(RDocumentEntity.FILTER.ne("link"))
      .get().value();
  }


  public DBQueryBuilder withItem(MenuBuilder menuBuilder) {
    this.menuBuilder = menuBuilder;
    return this;
  }

  public DBQueryBuilder withRecycleView(RecyclerView recyclerView) {
    this.recyclerView = recyclerView;
    return this;
  }
}
