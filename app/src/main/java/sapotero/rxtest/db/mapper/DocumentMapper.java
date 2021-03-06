package sapotero.rxtest.db.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import sapotero.rxtest.db.requery.models.RDocumentEntity;
import sapotero.rxtest.db.requery.models.RLinks;
import sapotero.rxtest.db.requery.models.RLinksEntity;
import sapotero.rxtest.db.requery.models.RRoute;
import sapotero.rxtest.db.requery.models.RRouteEntity;
import sapotero.rxtest.db.requery.models.RSigner;
import sapotero.rxtest.db.requery.models.RSignerEntity;
import sapotero.rxtest.db.requery.models.actions.RAction;
import sapotero.rxtest.db.requery.models.actions.RActionEntity;
import sapotero.rxtest.db.requery.models.control_labels.RControlLabels;
import sapotero.rxtest.db.requery.models.control_labels.RControlLabelsEntity;
import sapotero.rxtest.db.requery.models.decisions.RDecision;
import sapotero.rxtest.db.requery.models.decisions.RDecisionEntity;
import sapotero.rxtest.db.requery.models.exemplars.RExemplar;
import sapotero.rxtest.db.requery.models.exemplars.RExemplarEntity;
import sapotero.rxtest.db.requery.models.images.RImage;
import sapotero.rxtest.db.requery.models.images.RImageEntity;
import sapotero.rxtest.retrofit.models.document.ControlLabel;
import sapotero.rxtest.retrofit.models.document.Decision;
import sapotero.rxtest.retrofit.models.document.DocumentInfo;
import sapotero.rxtest.retrofit.models.document.DocumentInfoAction;
import sapotero.rxtest.retrofit.models.document.Exemplar;
import sapotero.rxtest.retrofit.models.document.Image;
import sapotero.rxtest.retrofit.models.document.Route;
import sapotero.rxtest.retrofit.models.document.Signer;
import timber.log.Timber;

// Maps between DocumentInfo and RDocumentEntity
public class DocumentMapper extends AbstractMapper<DocumentInfo, RDocumentEntity> {

  private String login = "";
  private String currentUserId = "";

  public DocumentMapper withLogin(String login) {
    this.login = login;
    return this;
  }

  public DocumentMapper withCurrentUserId(String currentUserId) {
    this.currentUserId = currentUserId;
    return this;
  }

  @Override
  public RDocumentEntity toEntity(DocumentInfo model) {
    RDocumentEntity entity = new RDocumentEntity();

    setSimpleFields(entity, model);
    setNestedFields(entity, model, false);

    return entity;
  }

  @Override
  public DocumentInfo toModel(RDocumentEntity entity) {
    DocumentInfo model = new DocumentInfo();

    setSimpleFields(model, entity);
    setNestedFields(model, entity);

    return model;
  }

  public void setSimpleFields(RDocumentEntity entity, DocumentInfo model) {
    setBaseFields( entity, model );
    entity.setFavorites( false );
    entity.setProcessed( false );
    entity.setControl( false );
    entity.setFromLinks( false );
    entity.setFromProcessedFolder( false );
    entity.setFromFavoritesFolder( false );
    entity.setChanged( false );
    entity.setReturned( false );
    entity.setRejected( false );
    entity.setAgain( false );
  }

  public void setBaseFields(RDocumentEntity entity, DocumentInfo model) {
    entity.setUid( model.getUid() );
    entity.setUser( login );

    entity.setMd5( model.getMd5() );
    entity.setSortKey( model.getSortKey() );
    entity.setTitle( model.getTitle() );
    entity.setRegistrationNumber( model.getRegistrationNumber() );
    entity.setRegistrationDate( model.getRegistrationDate() );

    entity.setRegistrationDate( model.getRegistrationDate() );

    if ( model.getRegistrationDate() != null ) {
      try {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Date date = format.parse( model.getRegistrationDate() );

        SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy");
        entity.setYear( Integer.parseInt(fmtOut.format(date)) );
      } catch (ParseException e) {
        Timber.e(e);
      }
    }

    entity.setUrgency( model.getUrgency() );
    entity.setShortDescription( model.getShortDescription() );
    entity.setComment( model.getComment() );
    entity.setExternalDocumentNumber( model.getExternalDocumentNumber() );
    entity.setReceiptDate( model.getReceiptDate() );
    entity.setViewed( model.getViewed() );

    setSigner( entity, model.getSigner() );

    if ( exist( model.getSigner() ) ) {
      if ( notEmpty( model.getSigner().getOrganisation() ) ) {
        entity.setOrganization( model.getSigner().getOrganisation() );
      } else {
        entity.setOrganization( "Без организации" );
      }
    }
  }

  private void setSimpleFields(DocumentInfo model, RDocumentEntity entity) {
    model.setUid( entity.getUid() );
    model.setMd5( entity.getMd5() );
    model.setSortKey( entity.getSortKey() );
    model.setTitle( entity.getTitle() );
    model.setRegistrationNumber( entity.getRegistrationNumber() );
    model.setRegistrationDate( entity.getRegistrationDate() );
    model.setUrgency( entity.getUrgency() );
    model.setShortDescription( entity.getShortDescription() );
    model.setComment( entity.getComment() );
    model.setExternalDocumentNumber( entity.getExternalDocumentNumber() );
    model.setReceiptDate( entity.getReceiptDate() );
    model.setViewed( entity.isViewed() );

    setSigner( model, entity.getSigner() );
  }

  public void setNestedFields(RDocumentEntity entity, DocumentInfo model, boolean processed) {
    setDecisions(entity, model.getDecisions(), processed);
    setRoute(entity, model.getRoute());
    setExemplars(entity, model.getExemplars());
    setImages(entity, model.getImages());
    setControlLabels(entity, model.getControlLabels());
    setActions(entity, model.getActions());
    setLinks(entity, model.getLinks());
    setInfoCard(entity, model.getInfoCard());
  }

  private void setNestedFields(DocumentInfo model, RDocumentEntity entity) {
    setDecisions(model, entity.getDecisions());
    setRoute(model, entity.getRoute());
    setExemplars(model, entity.getExemplars());
    setImages(model, entity.getImages());
    setControlLabels(model, entity.getControlLabels());
    setActions(model, entity.getActions());
    setLinks(model, entity.getLinks());
    setInfoCard(model, entity.getInfoCard());
  }

  public void setJournal(RDocumentEntity entity, String journal) {
    set( entity::setDocumentType, journal );
  }

  public void setFilter(RDocumentEntity entity, String filter) {
    set( entity::setFilter, filter );
  }

  public void setShared(RDocumentEntity entity, boolean shared) {
    if ( shared || Objects.equals(entity.getAddressedToType(), "group") ) {
      entity.setAddressedToType("group");
    } else {
      entity.setAddressedToType("");
    }
  }

  public void setSigner(RDocumentEntity entity, Signer signerModel) {
    entity.setSigner( null );

    if ( exist( signerModel ) ) {
      RSignerEntity signerEntity = new SignerMapper().toEntity( signerModel );
      entity.setSigner( signerEntity );
    }
  }

  public void setSigner(DocumentInfo model, RSigner signer) {
    if ( exist( signer ) ) {
      RSignerEntity signerEntity = (RSignerEntity) signer;
      Signer signerModel = new SignerMapper().toModel( signerEntity );
      model.setSigner( signerModel );
    }
  }

  private void setInfoCard(RDocumentEntity entity, String infoCard) {
    set( entity::setInfoCard, infoCard );
  }

  private void setInfoCard(DocumentInfo model, String infoCard) {
    set( model::setInfoCard, infoCard );
  }

  private void setLinks(RDocumentEntity entity, List<String> links) {
    entity.getLinks().clear();
    entity.setFirstLink(null);

    if ( notEmpty( links ) ) {
      LinkMapper linkMapper = new LinkMapper();

      for (String linkModel : links) {
        RLinksEntity linkEntity = linkMapper.toEntity( linkModel );
        linkEntity.setDocument( entity );
        entity.getLinks().add( linkEntity );
      }
    }
  }

  private void setLinks(DocumentInfo model, Set<RLinks> links) {
    if ( notEmpty( links ) ) {
      LinkMapper linkMapper = new LinkMapper();

      for (RLinks link : links) {
        RLinksEntity linksEntity = (RLinksEntity) link;
        String linkModel = linkMapper.toModel( linksEntity );
        model.getLinks().add( linkModel );
      }
    }
  }

  private void setControlLabels(RDocumentEntity entity, List<ControlLabel> controlLabels) {
    entity.getControlLabels().clear();
    boolean isControl = false;

    if ( notEmpty( controlLabels ) ) {
      ControlLabelMapper controlLabelMapper = new ControlLabelMapper();

      for (ControlLabel labelModel : controlLabels ) {
        RControlLabelsEntity labelEntity = controlLabelMapper.toEntity( labelModel );
        labelEntity.setDocument( entity );
        entity.getControlLabels().add( labelEntity );

        if ( Objects.equals( labelModel.getOfficialId(), currentUserId )
          && Objects.equals( labelModel.getState(), "Отмечен для постановки на контроль") ) {
          isControl = true;
        }
      }
    }

    entity.setControl( isControl );
  }

  private void setControlLabels(DocumentInfo model, Set<RControlLabels> controlLabels) {
    if ( notEmpty( controlLabels ) ) {
      ControlLabelMapper controlLabelMapper = new ControlLabelMapper();

      for (RControlLabels label : controlLabels ) {
        RControlLabelsEntity labelEntity = (RControlLabelsEntity) label;
        ControlLabel labelModel = controlLabelMapper.toModel( labelEntity );
        model.getControlLabels().add( labelModel );
      }
    }
  }

  private void setImages(RDocumentEntity entity, List<Image> images) {
    entity.getImages().clear();

    if ( notEmpty( images ) ) {
      ImageMapper imageMapper = new ImageMapper();

      for (Image imageModel : images ) {
        RImageEntity imageEntity = imageMapper.toEntity( imageModel );
        imageEntity.setDocument( entity );
        entity.getImages().add( imageEntity );
      }
    }
  }

  private void setImages(DocumentInfo model, Set<RImage> images) {
    if ( notEmpty( images ) ) {
      ImageMapper imageMapper = new ImageMapper();

      for (RImage image : images ) {
        RImageEntity imageEntity = (RImageEntity) image;
        Image imageModel = imageMapper.toModel( imageEntity );
        model.getImages().add( imageModel );
      }
    }
  }

  private void setExemplars(RDocumentEntity entity, List<Exemplar> exemplars) {
    entity.getExemplars().clear();

    if ( notEmpty( exemplars ) ) {
      ExemplarMapper exemplarMapper = new ExemplarMapper();

      for (Exemplar exemplarModel : exemplars ) {
        RExemplarEntity exemplarEntity = exemplarMapper.toEntity( exemplarModel );
        exemplarEntity.setDocument( entity );
        entity.getExemplars().add( exemplarEntity );
      }
    }
  }

  private void setExemplars(DocumentInfo model, Set<RExemplar> exemplars) {
    if ( notEmpty( exemplars ) ) {
      ExemplarMapper exemplarMapper = new ExemplarMapper();

      for (RExemplar exemplar : exemplars ) {
        RExemplarEntity exemplarEntity = (RExemplarEntity) exemplar;
        Exemplar exemplarModel = exemplarMapper.toModel( exemplarEntity );
        model.getExemplars().add( exemplarModel );
      }
    }
  }

  private void setActions(RDocumentEntity entity, List<DocumentInfoAction> actions) {
    entity.getActions().clear();

    if ( notEmpty( actions ) ) {
      ActionMapper actionMapper = new ActionMapper();

      for (DocumentInfoAction actionModel : actions ) {
        RActionEntity actionEntity = actionMapper.toEntity( actionModel );
        actionEntity.setDocument( entity );
        entity.getActions().add( actionEntity );
      }
    }
  }

  private void setActions(DocumentInfo model, Set<RAction> actions) {
    if ( notEmpty( actions ) ) {
      ActionMapper actionMapper = new ActionMapper();

      for (RAction action : actions ) {
        RActionEntity actionEntity = (RActionEntity) action;
        DocumentInfoAction actionModel = actionMapper.toModel( actionEntity );
        model.getActions().add( actionModel );
      }
    }
  }

  private void setRoute(RDocumentEntity entity, Route route) {
    entity.setRoute( null );

    if ( exist( route ) ) {
      RRouteEntity routeEntity = new RouteMapper().toEntity( route );
      entity.setRoute( routeEntity );
    }
  }

  private void setRoute(DocumentInfo model, RRoute route) {
    if ( exist( route ) ) {
      RRouteEntity routeEntity = (RRouteEntity) route;
      Route routeModel = new RouteMapper().toModel( routeEntity );
      model.setRoute( routeModel );
    }
  }

  private void setDecisions(RDocumentEntity entity, List<Decision> decisions, Boolean processed) {
    Boolean red = false;
    Boolean with_decision = false;

    entity.getDecisions().clear();

    if ( notEmpty( decisions ) ) {
      with_decision = true;
      DecisionMapper decisionMapper = new DecisionMapper();

      for (Decision decisionModel : decisions ) {
        if ( decisionModel.getRed() ) {
          red = true;
        }

        RDecisionEntity decisionEntity = decisionMapper.toEntity( decisionModel );
        decisionEntity.setDocument( entity );
        entity.getDecisions().add( decisionEntity );
      }
    }

    entity.setWithDecision( with_decision );

    if ( !processed ) {
      entity.setRed( red );
    }
  }

  private void setDecisions(DocumentInfo model, Set<RDecision> decisions) {
    if ( notEmpty( decisions ) ) {
      DecisionMapper decisionMapper = new DecisionMapper();

      for (RDecision decision : decisions ) {
        RDecisionEntity decisionEntity = (RDecisionEntity) decision;
        Decision decisionModel = decisionMapper.toModel( decisionEntity );
        model.getDecisions().add( decisionModel );
      }
    }
  }
}
