package sapotero.rxtest.db.mapper.utils;

import sapotero.rxtest.db.mapper.ActionMapper;
import sapotero.rxtest.db.mapper.AssistantMapper;
import sapotero.rxtest.db.mapper.BlockMapper;
import sapotero.rxtest.db.mapper.ColleagueMapper;
import sapotero.rxtest.db.mapper.ControlLabelMapper;
import sapotero.rxtest.db.mapper.DecisionMapper;
import sapotero.rxtest.db.mapper.DocumentMapper;
import sapotero.rxtest.db.mapper.ExemplarMapper;
import sapotero.rxtest.db.mapper.FavoriteUserMapper;
import sapotero.rxtest.db.mapper.ImageMapper;
import sapotero.rxtest.db.mapper.LinkMapper;
import sapotero.rxtest.db.mapper.PerformerMapper;
import sapotero.rxtest.db.mapper.PrimaryConsiderationMapper;
import sapotero.rxtest.db.mapper.RouteMapper;
import sapotero.rxtest.db.mapper.SignerMapper;
import sapotero.rxtest.db.mapper.StepMapper;
import sapotero.rxtest.db.mapper.TemplateMapper;

// Keeps all mappers in one place
public class Mappers {

  public Mappers() {
  }

  // Каждый раз создается новый экземпляр маппера, чтобы job одного пользователя не смогла поменать логин
  // в маппере другого (вход/выход в режим замещения)

  public ActionMapper getActionMapper() {
    return new ActionMapper();
  }

  public AssistantMapper getAssistantMapper() {
    return new AssistantMapper();
  }

  public BlockMapper getBlockMapper() {
    return new BlockMapper(this);
  }

  public ControlLabelMapper getControlLabelMapper() {
    return new ControlLabelMapper();
  }

  public DecisionMapper getDecisionMapper() {
    return new DecisionMapper(this);
  }

  public DocumentMapper getDocumentMapper() {
    return new DocumentMapper(this);
  }

  public ExemplarMapper getExemplarMapper() {
    return new ExemplarMapper();
  }

  public FavoriteUserMapper getFavoriteUserMapper() {
    return new FavoriteUserMapper(this);
  }

  public ImageMapper getImageMapper() {
    return new ImageMapper();
  }

  public LinkMapper getLinkMapper() {
    return new LinkMapper();
  }

  public PerformerMapper getPerformerMapper() {
    return new PerformerMapper();
  }

  public PrimaryConsiderationMapper getPrimaryConsiderationMapper() {
    return new PrimaryConsiderationMapper(this);
  }

  public RouteMapper getRouteMapper() {
    return new RouteMapper(this);
  }

  public SignerMapper getSignerMapper() {
    return new SignerMapper();
  }

  public StepMapper getStepMapper() {
    return new StepMapper();
  }

  public TemplateMapper getTemplateMapper() {
    return new TemplateMapper();
  }

  public ColleagueMapper getColleagueMapper() {
    return new ColleagueMapper();
  }
}
