package sapotero.rxtest.utils.memory.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import sapotero.rxtest.application.EsdApplication;
import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.utils.Settings;
import sapotero.rxtest.utils.memory.models.InMemoryDocument;
import sapotero.rxtest.views.menu.builders.ConditionBuilder;
import timber.log.Timber;

public class Filter {

  @Inject Settings settings;

  private final String TAG = this.getClass().getSimpleName();
  private final ArrayList<ConditionBuilder> conditions;

  private final ArrayList<String> statuses = new ArrayList<>();
  private final ArrayList<String> types    = new ArrayList<>();

  private Boolean isProcessed = false;
  private Boolean isFavorites = false;
  private Boolean isControl   = false;

  public Filter(ArrayList<ConditionBuilder> conditions) {
    this.conditions = conditions;
    processConditions();
    EsdApplication.getManagerComponent().inject(this);
  }

  // FIXME: 06.07.17
  // убрать чейнинг .filter().filter()...
  // из всех мест, заменить везде на 1 метод

  private void processConditions() {
    if (conditions != null) {
      for (ConditionBuilder condition : conditions ){
        if (condition.getField().getLeftOperand() == RDocumentEntity.FILTER){

//          try {
//            statuses.addAll(((ArrayList<String>) condition.getField().getRightOperand()));
//          } catch (Exception e) {
//            statuses.add( String.valueOf(condition.getField().getRightOperand()) );
//          }

          try {
            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> array = new Gson().fromJson(String.valueOf(condition.getField().getRightOperand()), listType);

            for (String st: array ) {
              statuses.add(st);
            }

            Timber.tag(TAG).e( "$$ %s | %s", array, array.size() );
          } catch (JsonSyntaxException e) {

            statuses.add( String.valueOf(condition.getField().getRightOperand()) );
          }

        }

        if (condition.getField().getLeftOperand() == RDocumentEntity.DOCUMENT_TYPE){

          String indexName = getIndexName( String.valueOf(condition.getField().getRightOperand()));
          if (indexName != null) {
            Timber.tag(TAG).w("new index: %s", String.valueOf(condition.getField().getRightOperand()));
            types.add( indexName );
//            types.add( String.valueOf(condition.getField().getRightOperand()) );
          }
        }

        if (condition.getField().getLeftOperand() == RDocumentEntity.CONTROL){
          if ( condition.getField().getRightOperand().equals(true)  ){
            isControl = true;
          }
        }

        if ( condition.getField().getLeftOperand() == RDocumentEntity.PROCESSED ){
          if ( condition.getField().getRightOperand().equals(true)  ){
            isProcessed = true;
          }
        }

        if (condition.getField().getLeftOperand() == RDocumentEntity.FAVORITES){
          if ( condition.getField().getRightOperand().equals(true)  ){
            isFavorites = true;
          }
        }

      }
    }
  }

  public Boolean hasStatuses() {
    return statuses.size() > 0;
  }

  public Boolean hasTypes() {
    return types.size() > 0;
  }

  public ArrayList<String> getTypes() {
    return types;
  }

  public ArrayList<String> getStatuses() {
    return statuses;
  }

  public Boolean getProcessed() {
    return isProcessed;
  }

  public Boolean isProcessed(InMemoryDocument doc) {
    Boolean result = true;

    // Фильтруем обработанные для всех журналов, кроме Избранное и На контроле
    if ( !isFavorites && !isControl ) {
      result = isProcessed == doc.isProcessed();
    }

    return result;
  }

  public Boolean isNotProcessed(InMemoryDocument doc) {
    return isProcessed != doc.isProcessed();
  }

  public Boolean byYear(InMemoryDocument doc) {
    return settings.getYears().contains( String.valueOf(doc.getYear()) );
  }

  public Boolean isFavorites(InMemoryDocument doc) {
    Boolean result = true;

    if ( isFavorites ){
      result = doc != null && doc.getDocument() != null && doc.getDocument().getFavorites() != null &&  doc.getDocument().getFavorites() || doc != null && doc.getDocument() != null && doc.getDocument().isFromFavoritesFolder();
    }

    return result;
  }

  public Boolean isControl(InMemoryDocument doc) {
    Boolean result = true;

    if ( isControl ){
      result = doc != null && doc.getDocument() != null && doc.getDocument().getControl() != null && doc.getDocument().getControl();
    }

    return result;
  }

  public Boolean byType(InMemoryDocument document) {
    return types.size() <= 0 || types.contains( document.getIndex() );
  }

  public Boolean byStatus(InMemoryDocument document) {
    return statuses.size() == 0 || statuses.contains( document.getFilter() );
  }

  public static Boolean isChanged(String s1, String s2){
    return !android.text.TextUtils.equals(s1, s2);
  }

  public static Integer bySortKey(InMemoryDocument imd1, InMemoryDocument imd2) {
    int result = -1;

    if (imd1.getDocument().getSortKey() != null && imd2.getDocument().getSortKey() != null) {
      result = imd1.getDocument().getSortKey().compareTo( imd2.getDocument().getSortKey() );
    }

    return result;
  }

  public Boolean getControl() {
    return isControl;
  }

  public Boolean getFavorites() {
    return isFavorites;
  }

  public static String getIndexName(String raw_index) {
    String indexName = null;

    if ( raw_index != null ) {
      String[] index = raw_index.split("_production_db_");
      indexName = index[0];
    }

    return indexName;
    }



}