package sapotero.rxtest.db.mapper;

import com.google.gson.Gson;

import java.util.Collections;

import sapotero.rxtest.db.requery.models.decisions.RBlockEntity;
import sapotero.rxtest.db.requery.models.decisions.RPerformer;
import sapotero.rxtest.db.requery.models.decisions.RPerformerEntity;
import sapotero.rxtest.retrofit.models.document.Block;
import sapotero.rxtest.retrofit.models.document.Performer;
import timber.log.Timber;

// Maps between Block and RBlockEntity
public class BlockMapper extends AbstractMapper<Block, RBlockEntity> {

  @Override
  public RBlockEntity toEntity(Block model) {
    RBlockEntity entity = new RBlockEntity();

    entity.setUid(model.getId());
    entity.setNumber(model.getNumber());
    entity.setText(model.getText());
    entity.setFontSize(model.getFontSize());
    entity.setAppealText(model.getAppealText());
    entity.setTextBefore(model.getTextBefore());
    entity.setHidePerformers(model.getHidePerformers());
    entity.setToCopy(model.getToCopy());
    entity.setToFamiliarization(model.getToFamiliarization());

    if ( notEmpty(model.getPerformers() ) ) {
      PerformerMapper performerMapper = new PerformerMapper();

      for (Performer performerModel : model.getPerformers()) {

        Timber.e("USER: %s", new Gson().toJson(performerModel) );

        RPerformerEntity performerEntity = performerMapper.toEntity(performerModel);
        performerEntity.setBlock(entity);
        entity.getPerformers().add(performerEntity);
      }
    }

    return entity;
  }

  @Override
  public Block toModel(RBlockEntity entity) {
    Block model = new Block();

    setBaseFields( model, entity );
    model.setFontSize( entity.getFontSize() );
    model.setToCopy( entity.isToCopy() );
    model.setToFamiliarization( entity.isToFamiliarization() );
    setPerformers( model, entity, false );

    return model;
  }

  Block toFormattedModel(RBlockEntity entity) {
    Block formattedModel = new Block();

    setBaseFields( formattedModel, entity );
    formattedModel.setFontSize( entity.getFontSize() );
    formattedModel.setIndentation( "5" ); // Не трогать!
    setPerformers( formattedModel, entity, true );

    return formattedModel;
  }

  private void setBaseFields(Block model, RBlockEntity entity) {
    model.setId( entity.getUid() );
    model.setNumber( entity.getNumber() );
    model.setText( entity.getText() );
    model.setAppealText( entity.getAppealText() );
    model.setTextBefore( entity.isTextBefore() );
    model.setHidePerformers( entity.isHidePerformers() );
    model.setAskToReport( entity.getAppealText() != null && entity.getAppealText().contains("дол") );
    model.setAskToAcquaint( entity.getAppealText() != null && entity.getAppealText().contains("озн") );
  }

  private void setPerformers(Block model, RBlockEntity entity, boolean formatted) {
    if ( notEmpty( entity.getPerformers() ) ) {
      PerformerMapper performerMapper = new PerformerMapper();

      for (RPerformer _performer : entity.getPerformers()) {
        RPerformerEntity performerEntity = (RPerformerEntity) _performer;
        Performer performerModel = formatted ? performerMapper.toFormattedModel(performerEntity) : performerMapper.toModel(performerEntity);
        model.getPerformers().add(performerModel);
      }

      if ( !formatted ) {
        Collections.sort(model.getPerformers(), (o1, o2) -> o1.getNumber().compareTo(o2.getNumber()));
      }
    }
  }
}
