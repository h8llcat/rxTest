package sapotero.rxtest.views.menu.fields;

import java.util.ArrayList;

import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.db.requery.utils.Fields;
import sapotero.rxtest.views.menu.builders.ButtonBuilder;
import sapotero.rxtest.views.menu.builders.ConditionBuilder;

public enum MainMenuItem {

  ALL ( 0, "Документы %s / Проекты %s",
    new MainMenuButton[]{
      MainMenuButton.PROJECTS,
      MainMenuButton.PERFORMANCE,
      MainMenuButton.PRIMARY_CONSIDERATION,
      MainMenuButton.VIEWED
    },
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FAVORITES.ne(false) ),
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.ne(true) )
    },
    new ConditionBuilder[]{}
  ),

  INCOMING_DOCUMENTS ( 1, "Входящие документы %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.UID.like( Fields.Journal.INCOMING_DOCUMENTS.getValue() + "%"  ) )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.UID.like( Fields.Journal.INCOMING_DOCUMENTS.getValue() + "%"  ) )
    }
  ),

  CITIZEN_REQUESTS ( 2, "Обращения граждан %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.UID.like( Fields.Journal.CITIZEN_REQUESTS.getValue() + "%"  ) )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.UID.like( Fields.Journal.CITIZEN_REQUESTS.getValue() + "%"  ) )
    }
  ),

  APPROVE_ASSIGN ( 3, "Подписание/Согласование %s",
    new MainMenuButton[]{
      MainMenuButton.APPROVAL,
      MainMenuButton.ASSIGN,
      MainMenuButton.PROCESSED
    },
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FILTER.eq( Fields.Status.SIGNING.getValue()  ) ),
      new ConditionBuilder( ConditionBuilder.Condition.OR,  RDocumentEntity.FILTER.eq( Fields.Status.APPROVAL.getValue() ) )
    },
    new ConditionBuilder[]{}
  ),

  INCOMING_ORDERS ( 4, "НПА %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.UID.like(  Fields.Journal.INCOMING_ORDERS.getValue() + "%"  ) )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.UID.like(  Fields.Journal.INCOMING_ORDERS.getValue() + "%"  ) )
    }
  ),

  ORDERS ( 5, "Приказы %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.UID.like( Fields.Journal.ORDERS.getValue() + "%"  ) )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.UID.like( Fields.Journal.ORDERS.getValue() + "%"  ) )
    }
  ),

  ORDERS_DDO ( 6, "Приказы ДДО %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.UID.like( Fields.Journal.ORDERS_DDO.getValue()  ) )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.UID.like( Fields.Journal.ORDERS_DDO.getValue()  ) )
    }
  ),

  IN_DOCUMENTS ( 7, "Внутренние документ %s", new MainMenuButton[]{
    MainMenuButton.PERFORMANCE,
    MainMenuButton.PRIMARY_CONSIDERATION,
    MainMenuButton.VIEWED
  },
    true,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.UID.like( Fields.Journal.ORDERS.getValue()+ "%"  ) )
    },
    new ConditionBuilder[]{}
  ),

  ON_CONTROL ( 8, "На контроле %s", new MainMenuButton[]{},
    false,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.CONTROL.eq( true ) )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.CONTROL.eq( true ) )
    }
  ),
  PROCESSED ( 9, "Обработанное %s", new MainMenuButton[]{},
    false,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.eq( true ) )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.PROCESSED.eq( true ) )
    }
  ),
  FAVORITES ( 10, "Избранное %s", new MainMenuButton[]{},
    false,
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FAVORITES.eq( true ) )
    },
    new ConditionBuilder[]{
      new ConditionBuilder( ConditionBuilder.Condition.AND, RDocumentEntity.FAVORITES.eq( true ) )
    }
  );

  private final Integer index;
  private final MainMenuButton[] mainMenuButtons;
  private final String name;
  private final Boolean showOrganization;
  private ConditionBuilder[] countConditions;
  private ConditionBuilder[] queryConditions;
  private final ArrayList<ButtonBuilder> buttonsList = new ArrayList<>();

  MainMenuItem(final int index, final String name, final MainMenuButton[] mainMenuButtons, Boolean showOrganizations, ConditionBuilder[] countCounditions, ConditionBuilder[] queryConditions) {
    this.index = index;
    this.name  = name;
    this.mainMenuButtons = mainMenuButtons;
    this.showOrganization = showOrganizations;
    this.countConditions = countCounditions;
    this.queryConditions = queryConditions;
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

  public ArrayList<ButtonBuilder> getMainMenuButtons(){

    if ( buttonsList.size() == 0 ){
      if ( mainMenuButtons.length > 0 ){
        for (int i = 0, length = mainMenuButtons.length-1; i <= length; i++) {

          ButtonBuilder button = new ButtonBuilder(
            mainMenuButtons[i].getFormat(),
            mainMenuButtons[i].getConditions(),
            getQueryConditions().length != 0 ? getQueryConditions()[0] : null
          );

          if (i == 0){
            button.setLeftCorner();
          } else if ( i == length ){
            button.setRightCorner();
          } else {
            button.setNoneCorner();
          }

          buttonsList.add( button );
        }
      }
    } else {
      for (ButtonBuilder button: buttonsList){
        button.recalculate();
      }
    }

    return buttonsList;
  }

  public Boolean isVisible(){
    return showOrganization;
  }

  public void recalcuate(){
    for (ButtonBuilder button: buttonsList){
      button.recalculate();
    }
  }

  @Override
  public String toString() {
    return name;
  }

}
