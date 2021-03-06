package sapotero.rxtest.views.menu.fields;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.db.requery.utils.Journals;
import sapotero.rxtest.db.requery.utils.JournalStatus;
import sapotero.rxtest.views.menu.builders.ButtonBuilder;
import sapotero.rxtest.views.menu.builders.ConditionBuilder;


public enum MainMenuItem {

  ALL (Journals.ALL, "Документы %s / Проекты %s",
    new MainMenuButton[]{
      MainMenuButton.PROJECTS,
      MainMenuButton.PERFORMANCE,
      MainMenuButton.PRIMARY_CONSIDERATION,
      MainMenuButton.VIEWED
    },
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FAVORITES.ne(false) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FROM_LINKS.eq( false ) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FROM_PROCESSED_FOLDER.eq( false ) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FROM_FAVORITES_FOLDER.eq( false ) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FROM_PROCESSED_FOLDER.eq( false ) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FROM_FAVORITES_FOLDER.eq( false ) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
    },
    true, false),

  INCOMING_DOCUMENTS (Journals.INCOMING_DOCUMENTS, "Входящие документы %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.eq(false) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.INCOMING_DOCUMENTS.getName() )  )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.INCOMING_DOCUMENTS.getName() )  )
    },
    false, false),

  CITIZEN_REQUESTS (Journals.CITIZEN_REQUESTS, "Обращения граждан %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.eq(false) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.CITIZEN_REQUESTS.getName() )  )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.CITIZEN_REQUESTS.getName() )  )
    },
    false, false),

  APPROVE_ASSIGN (Journals.APPROVE_ASSIGN, "Подписание/Согласование %s",
    new MainMenuButton[]{
      MainMenuButton.APPROVAL,
      MainMenuButton.ASSIGN,
      MainMenuButton.PROCESSED
    },
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FILTER.in( MainMenuButton.ButtonStatus.getProject() ) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.eq( false ) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FROM_LINKS.eq( false ) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FROM_PROCESSED_FOLDER.eq( false ) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FROM_FAVORITES_FOLDER.eq( false ) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
    },
    true, true),

  INCOMING_ORDERS (Journals.INCOMING_ORDERS, "НПА %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.eq(false) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.INCOMING_ORDERS.getName() )  )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.INCOMING_ORDERS.getName() )  )
    },
    false, false),

  ORDERS (Journals.ORDERS, "Приказы %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.eq(false) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.ORDERS.getName() )  )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.ORDERS.getName() )  )
    },
    false, false),

  ORDERS_DDO (Journals.ORDERS_DDO, "Приказы ДДО %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.eq(false) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.ORDERS_DDO.getName() )  )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.ORDERS_DDO.getName() )  )
    },
    false, false),

  IN_DOCUMENTS (Journals.IN_DOCUMENTS, "Внутренние документы %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.eq(false) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.OUTGOING_DOCUMENTS.getName() )  )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq("") ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.DOCUMENT_TYPE.eq( JournalStatus.OUTGOING_DOCUMENTS.getName() )  )
    },
    false, false),

  ON_CONTROL (Journals.ON_CONTROL, "На контроле %s", new MainMenuButton[]{},
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.CONTROL.eq( true ) ),
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.CONTROL.eq( true ) ),
    },
    true, true),
  PROCESSED (Journals.PROCESSED, "Обработанное %s", new MainMenuButton[]{},
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.eq( true ) ),
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.eq( true ) ),
    },
    true, true),

  FAVORITES (Journals.FAVORITES, "Избранное %s", new MainMenuButton[]{},
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FAVORITES.eq( true ) ),
      new ConditionBuilder( ConditionBuilder.Condition.OR, RDocumentEntity.FROM_FAVORITES_FOLDER.eq( true ) ),
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FAVORITES.eq( true ) ),
      new ConditionBuilder( ConditionBuilder.Condition.OR, RDocumentEntity.FROM_FAVORITES_FOLDER.eq( true ) ),

    },
    true, true
    // Общие документы - аппараты - пока не нужны от слова совсем
    // ),
    //  SHARED (Journals.SHARED, "Общие документы %s", new MainMenuButton[]{
    //    MainMenuButton.SHARED_PRIMARY,
    //    MainMenuButton.ASSIGN,
    //    MainMenuButton.APPROVAL,
    //    MainMenuButton.VIEWED,
    //    }, true,
    //    new ConditionBuilder[]{
    //      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq( "group" ) ),
    //    },
    //    new ConditionBuilder[]{
    //      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.ADDRESSED_TO_TYPE.eq( "group" ) ),
    //    },  false, false
  );

  private static final String TAG = "MainMenuItem";
  private final Integer index;
  private final MainMenuButton[] mainMenuButtons;
  private final String name;
  private final Boolean showOrganization;
  private final ConditionBuilder[] countConditions;
  private final ConditionBuilder[] queryConditions;
  private final boolean showAnyWay;
  private final ArrayList<ButtonBuilder> buttonsList = new ArrayList<>();

  private final boolean processed;

  MainMenuItem(final int index, final String name, final MainMenuButton[] mainMenuButtons, Boolean showOrganizations, ConditionBuilder[] countCounditions, ConditionBuilder[] queryConditions, boolean showAnyWay, boolean processed) {
    this.index = index;
    this.name  = name;
    this.mainMenuButtons = mainMenuButtons;
    this.showOrganization = showOrganizations;
    this.countConditions = countCounditions;
    this.queryConditions = queryConditions;
    this.showAnyWay = showAnyWay;
    this.processed = processed;
  }

  public boolean isProcessed() {
    return processed;
  }

  public boolean isShowAnyWay() {
    return showAnyWay;
  }

  public Integer getIndex(){
    return index;
  }

  public ConditionBuilder[] getCountConditions(){
    return countConditions;
  }

  public ConditionBuilder[] getQueryConditions(){
    return queryConditions;
  }

  public String getName(){
    return name;
  }

  public MainMenuButton[] getButtons(){
    return mainMenuButtons;
  }

  public ArrayList<ButtonBuilder> getButtonList(){
    return buttonsList;
  }

  public ArrayList<ButtonBuilder> getMainMenuButtons(){
//    Timber.tag(TAG).e("getMainMenuButtons %s", buttonsList);

    if ( buttonsList.size() == 0 ){

//      Timber.tag(TAG).e("buttonsList.size() == 0");

      if ( mainMenuButtons.length > 0 ){
        for (int i = 0, length = mainMenuButtons.length-1; i <= length; i++) {

//          Timber.tag("CMP").e( "length: %s", Arrays.toString(getQueryConditions()));

          ButtonBuilder button = new ButtonBuilder(
            mainMenuButtons[i].getFormat(),
            mainMenuButtons[i].getConditions(),
            getQueryConditions(),
            isShowAnyWay(),
            mainMenuButtons[i].getIndex()
          );

          buttonsList.add( button );
        }
      }
    }
    else {
      for (ButtonBuilder button: buttonsList){
//        Timber.tag(TAG).e("getMainMenuButtons else recalcuate");
//        button.recalculate();
      }
    }

    return buttonsList;
  }

  public Boolean isVisible(){
    return showOrganization;
  }

  @Override
  public String toString() {
    return name;
  }


  private static final Map<Integer,MainMenuItem> lookup = new HashMap<Integer,MainMenuItem>();

  static {
    for(MainMenuItem w : EnumSet.allOf(MainMenuItem.class)){
      lookup.put(w.getIndex(), w);
    }
  }

  public static MainMenuItem get(int index) {
    return lookup.get(index);
  }

}

