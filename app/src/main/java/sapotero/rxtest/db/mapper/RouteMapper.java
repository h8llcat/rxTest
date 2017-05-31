package sapotero.rxtest.db.mapper;

import sapotero.rxtest.db.mapper.utils.Mappers;
import sapotero.rxtest.db.requery.models.RRouteEntity;
import sapotero.rxtest.db.requery.models.RStep;
import sapotero.rxtest.db.requery.models.RStepEntity;
import sapotero.rxtest.retrofit.models.document.Route;
import sapotero.rxtest.retrofit.models.document.Step;
import sapotero.rxtest.utils.Settings;

// Maps between Route and RRouteEntity
public class RouteMapper extends AbstractMapper<Route, RRouteEntity> {

  public RouteMapper(Settings settings, Mappers mappers) {
    super(settings, mappers);
  }

  @Override
  public RRouteEntity toEntity(Route model) {
    RRouteEntity entity = new RRouteEntity();

    entity.setText( model.getTitle() );
    StepMapper stepMapper = mappers.getStepMapper();

    for (Step stepModel : model.getSteps() ) {
      RStepEntity stepEntity = stepMapper.toEntity( stepModel );
      stepEntity.setRoute( entity );
      entity.getSteps().add( stepEntity );
    }

    return entity;
  }

  @Override
  public Route toModel(RRouteEntity entity) {
    Route model = new Route();

    model.setTitle( entity.getText() );
    StepMapper stepMapper = mappers.getStepMapper();

    for (RStep step : entity.getSteps() ) {
      RStepEntity stepEntity = (RStepEntity) step;
      Step stepModel = stepMapper.toModel( stepEntity );
      model.getSteps().add( stepModel );
    }

    return model;
  }
}
