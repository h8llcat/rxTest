package sapotero.rxtest.db.mapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Type;

import sapotero.rxtest.db.mapper.utils.Mappers;
import sapotero.rxtest.db.requery.models.RStepEntity;
import sapotero.rxtest.retrofit.models.document.Step;
import sapotero.rxtest.utils.Settings;

// Maps between Step and RStepEntity
public class StepMapper extends AbstractMapper<Step, RStepEntity> {

  private Gson gson;

  public StepMapper(Settings settings, Mappers mappers) {
    super(settings, mappers);
    gson = new Gson();
  }

  @Override
  public RStepEntity toEntity(Step model) {
    RStepEntity entity = new RStepEntity();

    entity.setTitle( model.getTitle() );
    entity.setNumber( model.getNumber() );
    set( entity::setPeople, model.getPeople() );
    set( entity::setCards, model.getCards() );
    set( entity::setAnother_approvals, model.getAnotherApprovals() );

    return entity;
  }

  @Override
  public Step toModel(RStepEntity entity) {
    Step model = new Step();

    model.setTitle( entity.getTitle() );
    model.setNumber( entity.getNumber() );
    set( model::setPeople, entity.getPeople() );
    set( model::setCards, entity.getCards() );
    set( model::setAnotherApprovals, entity.getAnother_approvals() );

    return model;
  }

  private <T> String listToJson(List<T> list) {
    return gson.toJson( list );
  }

  private <T> ArrayList<T> jsonToList(String jsonString) {
    Type listType = new TypeToken<ArrayList<T>>(){}.getType();
    return gson.fromJson(jsonString, listType);
  }

  private <T> void set(StringFieldSetter stringFieldSetter, List<T> list) {
    if ( notEmpty( list ) ) {
      stringFieldSetter.setField( listToJson( list ) );
    }
  }

  private <T> void set(ListFieldSetter<T> listFieldSetter, String jsonString) {
    if ( notEmpty( jsonString ) ) {
      listFieldSetter.setField( jsonToList( jsonString ) );
    }
  }
}