package sapotero.rxtest.views.menu.factories;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import sapotero.rxtest.application.EsdApplication;
import sapotero.rxtest.events.service.UpdateDocumentsByStatusEvent;
import sapotero.rxtest.utils.ISettings;
import sapotero.rxtest.views.adapters.DocumentTypeAdapter;
import sapotero.rxtest.views.adapters.models.DocumentTypeItem;
import sapotero.rxtest.views.custom.OrganizationSpinner;
import sapotero.rxtest.views.menu.builders.ButtonBuilder;
import sapotero.rxtest.views.menu.builders.ConditionBuilder;
import sapotero.rxtest.views.menu.fields.MainMenuButton;
import sapotero.rxtest.views.menu.fields.MainMenuItem;
import timber.log.Timber;

public class ItemsBuilder implements ButtonBuilder.Callback {

  @Inject ISettings settings;

  private String TAG = this.getClass().getSimpleName();
  private final Context context;
  private RadioGroup view;
  private Spinner journalSpinner;
  private DocumentTypeAdapter journalSpinnerAdapter;
  private Callback callback;
  private LinearLayout organizationsLayout;
  private OrganizationSpinner organizationSelector;
  private CheckBox favoritesButton;
  private String user;
  private Integer index = -1;
  private Integer view_index = -1;


  public boolean isVisible() {
    return getSelectedItem().isVisible();
  }

  public void setUser(String user) {
    this.user = user;
  }

  public interface Callback {
    void onMenuUpdate(ArrayList<ConditionBuilder> result);
  }

  public void registerCallBack(Callback callback){
    this.callback = callback;
  }



  public ItemsBuilder(Context context) {
    this.context = context;
    EsdApplication.getDataComponent().inject(this);
  }

  public void setSpinner(Spinner selector) {
    journalSpinner = selector;

    journalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        view_index = -1;
        updateView();
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });
  }



  public void setOrganizationsLayout(LinearLayout organizationsLayout) {
    this.organizationsLayout = organizationsLayout;
  }

  public void setOrganizationSelector(OrganizationSpinner organizationSelector) {
    this.organizationSelector = organizationSelector;
  }

  public void setFavoritesButton(CheckBox favoritesButton) {
    this.favoritesButton = favoritesButton;
    this.favoritesButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
      Timber.tag("checkbox").i( "checked: %s", isChecked );
      onButtonBuilderUpdate(index);
    });
  }

  public void setSpinnerDefaults() {
    Timber.i( "setSpinnerDefaults" );
    List<DocumentTypeItem> document_types = new ArrayList<>();

    for ( MainMenuItem mainMenuItem : MainMenuItem.values()) {
      document_types.add( new DocumentTypeItem( context, mainMenuItem, user ) );
    }

    journalSpinnerAdapter = new DocumentTypeAdapter(context, document_types);
    journalSpinner.setAdapter(journalSpinnerAdapter);

    // без привязки к доступным журналам
    // resolved https://tasks.n-core.ru/browse/MVDESD-13318
    // открывать "Все документы" если выбран журнал по умолчанию,
    // которого нет в активных журналах

    if (settings.getStartJournal() != null){

      int journal = 0;
      if ( settings.getJournals().contains( settings.getStartJournal() ) ){
        journal = Integer.parseInt( settings.getStartJournal() );
      }

      journalSpinner.setSelection( journal );

    }
  }

  public void prev() {
    journalSpinner.setSelection( journalSpinnerAdapter.prev() );
  }
  public void next() {
    journalSpinner.setSelection( journalSpinnerAdapter.next() );
  }

  public void get( int type ) {
    journalSpinner.setSelection( type );
  }

  public RadioGroup getView() {
    return view;
  }

  public void updateView(MainMenuItem mainMenuItem) {
    if (view == null){
      view = new RadioGroup(context);
    }

    Timber.e("++ index: %s | %s", index, view_index);

    view.removeAllViews();

    RadioGroup button_group = getButtonGroupLayout(context);

    if ( mainMenuItem.getMainMenuButtons().size() > 0 ){
//      for ( ButtonBuilder button: mainMenuItem.getMainMenuButtons() ){
      Boolean isSet = false;

      for (int i = 0; i < mainMenuItem.getMainMenuButtons().size(); i++) {
        ButtonBuilder button = mainMenuItem.getMainMenuButtons().get(i);

        RadioButton button_view = button.getView(context);
        button_group.addView( button_view );
        button.registerCallBack(this);

        // resolved https://tasks.n-core.ru/browse/MVDESD-12879
        // Стартовая страница: Должен быть выбор отображаемого раздела документов при запуске
        // На рассмотрение, Первичное рассмотрение, Рассмотренные

        if (view_index == -1){
          switch ( settings.getStartPage() ){
            case "report":
              if ( button.getIndex() == 2 ) {
                ((RadioButton) button_group.getChildAt(i)).setChecked(true);
                isSet = true;
              }
              break;
            case "primary_consideration":
              if ( button.getIndex() == 3 ) {
                ((RadioButton) button_group.getChildAt(i)).setChecked(true);
                isSet = true;
              }
              break;
            case "processed":
              if ( button.getIndex() == 4 || button.getIndex() == 7 ) {
                ((RadioButton) button_group.getChildAt(i)).setChecked(true);
                isSet = true;
              }
              break;
          }
        }

        if (Objects.equals(index, button.getIndex())){
          view_index = button_view.getId();
          Timber.e("button: %s | %s", index, view_index);
        }

      }
//
      if (!isSet){
        ((RadioButton) button_group.getChildAt(0)).setChecked(true);
      }
//
//      // если отключена первичка, но она есть в кнопках
      if (settings.isHidePrimaryConsideration()){
        ((RadioButton) button_group.getChildAt(0)).setChecked(true);
      }

      if (view_index != -1){
        Timber.e("last_checked_index %s", view_index);
        try {
          button_group.check(view_index);
        } catch (Exception e) {
          Timber.e(e);
        }
      }

    }
    else {
      onButtonBuilderUpdate(index);
    }

    if (organizationsLayout != null){
      organizationsLayout.setVisibility( mainMenuItem.isVisible() ?  View.VISIBLE : View.GONE);
    }

    view.addView( button_group );
  }

  public void updateView() {
    updateView(getSelectedItem());
  }

  public void updateCount() {

  }

  public MainMenuItem getSelectedItem(){
    return journalSpinnerAdapter.getItem(journalSpinner.getSelectedItemPosition()).getMainMenuItem();
  }

  private ArrayList<ConditionBuilder> getConditions(){
    MainMenuItem mainMenuItem = getSelectedItem();

    ArrayList<ConditionBuilder> result = new ArrayList<>();

    Collections.addAll(result, mainMenuItem.getQueryConditions() );
    if ( !mainMenuItem.isProcessed() ){
      Collections.addAll(result, MainMenuButton.getByIndex(index).getConditions() );
    }

    if ( mainMenuItem.getIndex() == 3 ){
      Collections.addAll(result, MainMenuButton.getByIndex(index).getConditions() );
    }

    return result;
  }

  private RadioGroup getButtonGroupLayout(Context context) {
    RadioGroup view = new RadioGroup(context);
    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT,
      1.0f
    );
    view.setOrientation(RadioGroup.HORIZONTAL);
    view.setLayoutParams(params);
    return view;
  }

  @Override
  public void onButtonBuilderUpdate(Integer index) {

    try {
      this.index = index;
      Timber.tag(TAG).i( "+ set last_checked_index %s | %s" ,index, view_index);
      Timber.tag("TabChanged").d( "Send UpdateDocumentsByStatusEvent: %s, index: %s", settings.isTabChanged(), index);
      if ( settings.isTabChanged() ) {
        EventBus.getDefault().post( new UpdateDocumentsByStatusEvent( getSelectedItem(), MainMenuButton.getByIndex(index) ) );
      }
      callback.onMenuUpdate( getConditions() );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
