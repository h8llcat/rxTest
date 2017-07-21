package sapotero.rxtest.mapper;

import org.junit.Before;
import org.junit.Test;

import sapotero.rxtest.db.mapper.PerformerMapper;
import sapotero.rxtest.db.requery.models.decisions.RPerformerEntity;
import sapotero.rxtest.retrofit.models.Oshs;
import sapotero.rxtest.retrofit.models.document.IPerformer;
import sapotero.rxtest.retrofit.models.document.Performer;
import sapotero.rxtest.views.adapters.utils.PrimaryConsiderationPeople;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PerformerMapperText {

  private PerformerMapper mapper;
  private Performer dummyPerformer;
  private PrimaryConsiderationPeople dummyPrimaryConsiderationPeople;
  private Oshs dummyOshs;
  private RPerformerEntity entity;
  private Performer model;

  @Before
  public void init() {
    mapper = new PerformerMapper();
    generatePerformer();
    generatePrimaryConsiderationPeople();
    generateOshs();
  }

  private void generatePerformer() {
    dummyPerformer = new Performer();
    dummyPerformer.setId( "5936884c029a000222000001" );
    dummyPerformer.setPerformerId( "58f88dfc776b000026000001" );
    dummyPerformer.setIsOriginal( true );
    dummyPerformer.setIsResponsible( false );
    dummyPerformer.setOrganization( false );
    dummyPerformer.setNumber( 1 );
    dummyPerformer.setPerformerType( "mvd_person" );
    dummyPerformer.setPerformerText( "Сотрудник_а2 A.T. (Сотрудник ОДИР)" );
    dummyPerformer.setPerformerGender( "Мужской" );
    dummyPerformer.setOrganizationText( "ОДиР ГУ МВД России по Самарской области" );
  }

  private void generatePrimaryConsiderationPeople() {
    dummyPrimaryConsiderationPeople = new PrimaryConsiderationPeople();
    dummyPrimaryConsiderationPeople.setPosition( "Сотрудник ОДИР" );
    dummyPrimaryConsiderationPeople.setName( "Сотрудник_а2 A.T." );
    dummyPrimaryConsiderationPeople.setId( "58f88dfc776b000026000001" );
    dummyPrimaryConsiderationPeople.setOrganization( "ОДиР ГУ МВД России по Самарской области" );
    dummyPrimaryConsiderationPeople.setGender( "Мужской" );
    dummyPrimaryConsiderationPeople.setIsOrganization( false );
  }

  private void generateOshs() {
    dummyOshs = new Oshs();
    dummyOshs.setId( "58f88dfc776b000026000001" );
    dummyOshs.setIsOrganization( false );
    dummyOshs.setIsGroup( false );
    dummyOshs.setName( "Сотрудник_а2 A.T." );
    dummyOshs.setOrganization( "ОДиР ГУ МВД России по Самарской области" );
    dummyOshs.setPosition( "Сотрудник ОДИР" );
    dummyOshs.setLastName( "Сотрудник_а2" );
    dummyOshs.setFirstName( "Android" );
    dummyOshs.setMiddleName( "Test" );
    dummyOshs.setGender( "Мужской" );
    dummyOshs.setImage( null );
  }

  @Test
  public void toEntity() {
    entity = mapper.toEntity(dummyPerformer);

    assertNotNull( entity );
    assertEquals( 0, entity.getId() );
    assertEquals( dummyPerformer.getId(), entity.getUid() );
    assertEquals( dummyPerformer.getPerformerId(), entity.getPerformerId() );
    assertEquals( dummyPerformer.getIsOriginal(), entity.isIsOriginal() );
    assertEquals( dummyPerformer.getIsResponsible(), entity.isIsResponsible() );
    assertEquals( dummyPerformer.getOrganization(), entity.isIsOrganization() );
    assertEquals( dummyPerformer.getNumber(), entity.getNumber() );
    assertEquals( dummyPerformer.getPerformerType(), entity.getPerformerType() );
    assertEquals( dummyPerformer.getPerformerText(), entity.getPerformerText() );
    assertEquals( dummyPerformer.getPerformerGender(), entity.getPerformerGender() );
    assertEquals( dummyPerformer.getOrganizationText(), entity.getOrganizationText() );
  }

  @Test
  public void toModel() {
    entity = mapper.toEntity(dummyPerformer);
    model = mapper.toModel(entity);

    assertNotNull( model );
    assertEquals( dummyPerformer.getId(), model.getId() );
    assertEquals( dummyPerformer.getPerformerId(), model.getPerformerId() );
    assertEquals( dummyPerformer.getIsOriginal(), model.getIsOriginal() );
    assertEquals( dummyPerformer.getIsResponsible(), model.getIsResponsible() );
    assertEquals( dummyPerformer.getOrganization(), model.getOrganization() );
    assertEquals( dummyPerformer.getNumber(), model.getNumber() );
    assertEquals( dummyPerformer.getPerformerType(), model.getPerformerType() );
    assertEquals( dummyPerformer.getPerformerText(), model.getPerformerText() );
    assertEquals( dummyPerformer.getPerformerGender(), model.getPerformerGender() );
    assertEquals( dummyPerformer.getOrganizationText(), model.getOrganizationText() );
  }

  @Test
  public void isOrganization() {
    Performer performer = new Performer();

    performer.setPerformerType( "mvd_person" );
    assertFalse( performer.getOrganization() );

    performer.setPerformerType( "mvd_organisation" );
    assertTrue( performer.getOrganization() );
  }

  @Test
  public void hasDiff() {
    RPerformerEntity entity1 = mapper.toEntity(dummyPerformer);
    RPerformerEntity entity2 = mapper.toEntity(dummyPerformer);
    boolean hasDiff = mapper.hasDiff(entity1, entity2);

    assertFalse( hasDiff );

    entity2.setUid("");
    hasDiff = mapper.hasDiff(entity1, entity2);

    assertTrue( hasDiff );
  }

  @Test
  public void convertFromPerformerToPerformer() {
    IPerformer destination = mapper.convert(dummyPerformer, PerformerMapper.DestinationType.PERFORMER);

    assertNotNull( destination );
    assertTrue( destination instanceof Performer );
    assertEquals( dummyPerformer.getId(), destination.getIPerformerUid() );
    assertEquals( dummyPerformer.getNumber(), destination.getIPerformerNumber() );
    assertEquals( dummyPerformer.getPerformerId(), destination.getIPerformerId() );
    assertEquals( dummyPerformer.getPerformerType(), destination.getIPerformerType() );
    assertEquals( dummyPerformer.getPerformerText(), destination.getIPerformerName() );
    assertEquals( dummyPerformer.getPerformerGender(), destination.getIPerformerGender() );
    assertEquals( dummyPerformer.getOrganizationText(), destination.getIPerformerOrganizationName() );
    assertEquals( null, destination.getIPerformerAssistantId() );
    assertEquals( null, destination.getIPerformerPosition() );
    assertEquals( null, destination.getIPerformerLastName() );
    assertEquals( null, destination.getIPerformerFirstName() );
    assertEquals( null, destination.getIPerformerMiddleName() );
    assertEquals( null, destination.getIPerformerImage() );
    assertEquals( dummyPerformer.getIsOriginal(), destination.isIPerformerOriginal() );
    assertEquals( dummyPerformer.getIsResponsible(), destination.isIPerformerResponsible() );
    assertEquals( null, destination.isIPerformerGroup() );
    assertEquals( dummyPerformer.getOrganization(), destination.isIPerformerOrganization() );
  }

  @Test
  public void convertFromPerformerToPrimaryConsiderationPeople() {
    IPerformer destination = mapper.convert(dummyPerformer, PerformerMapper.DestinationType.PRIMARYCONSIDERATIONPEOPLE);

    assertNotNull( destination );
    assertTrue( destination instanceof PrimaryConsiderationPeople);
    assertEquals( dummyPerformer.getId(), destination.getIPerformerUid() );
    assertEquals( null, destination.getIPerformerNumber() );
    assertEquals( dummyPerformer.getPerformerId(), destination.getIPerformerId() );
    assertEquals( null, destination.getIPerformerType() );
    assertEquals( dummyPerformer.getPerformerText(), destination.getIPerformerName() );
    assertEquals( dummyPerformer.getPerformerGender(), destination.getIPerformerGender() );
    assertEquals( dummyPerformer.getOrganizationText(), destination.getIPerformerOrganizationName() );
    assertEquals( null, destination.getIPerformerAssistantId() );
    assertEquals( null, destination.getIPerformerPosition() );
    assertEquals( null, destination.getIPerformerLastName() );
    assertEquals( null, destination.getIPerformerFirstName() );
    assertEquals( null, destination.getIPerformerMiddleName() );
    assertEquals( null, destination.getIPerformerImage() );
    assertEquals( dummyPerformer.getIsOriginal(), destination.isIPerformerOriginal() );
    assertEquals( dummyPerformer.getIsResponsible(), destination.isIPerformerResponsible() );
    assertEquals( null, destination.isIPerformerGroup() );
    assertEquals( dummyPerformer.getOrganization(), destination.isIPerformerOrganization() );
  }

  @Test
  public void convertFromPerformerToOshs() {
    IPerformer destination = mapper.convert(dummyPerformer, PerformerMapper.DestinationType.OSHS);

    assertNotNull( destination );
    assertTrue( destination instanceof Oshs );
    assertEquals( null, destination.getIPerformerUid() );
    assertEquals( null, destination.getIPerformerNumber() );
    assertEquals( dummyPerformer.getPerformerId(), destination.getIPerformerId() );
    assertEquals( null, destination.getIPerformerType() );
    assertEquals( dummyPerformer.getPerformerText(), destination.getIPerformerName() );
    assertEquals( dummyPerformer.getPerformerGender(), destination.getIPerformerGender() );
    assertEquals( dummyPerformer.getOrganizationText(), destination.getIPerformerOrganizationName() );
    assertEquals( null, destination.getIPerformerAssistantId() );
    assertEquals( null, destination.getIPerformerPosition() );
    assertEquals( null, destination.getIPerformerLastName() );
    assertEquals( null, destination.getIPerformerFirstName() );
    assertEquals( null, destination.getIPerformerMiddleName() );
    assertEquals( null, destination.getIPerformerImage() );
    assertEquals( null, destination.isIPerformerOriginal() );
    assertEquals( null, destination.isIPerformerResponsible() );
    assertEquals( null, destination.isIPerformerGroup() );
    assertEquals( dummyPerformer.getOrganization(), destination.isIPerformerOrganization() );
  }

  @Test
  public void convertFromPrimaryConsiderationPeopleToPerformer() {
    IPerformer destination = mapper.convert(dummyPrimaryConsiderationPeople, PerformerMapper.DestinationType.PERFORMER);

    assertNotNull( destination );
    assertTrue( destination instanceof Performer );
    assertEquals( null, destination.getIPerformerUid() );
    assertEquals( null, destination.getIPerformerNumber() );
    assertEquals( dummyPrimaryConsiderationPeople.getId(), destination.getIPerformerId() );
    assertEquals( null, destination.getIPerformerType() );
    assertEquals( dummyPrimaryConsiderationPeople.getName(), destination.getIPerformerName() );
    assertEquals( dummyPrimaryConsiderationPeople.getGender(), destination.getIPerformerGender() );
    assertEquals( dummyPrimaryConsiderationPeople.getOrganization(), destination.getIPerformerOrganizationName() );
    assertEquals( null, destination.getIPerformerAssistantId() );
    assertEquals( null, destination.getIPerformerPosition() );
    assertEquals( null, destination.getIPerformerLastName() );
    assertEquals( null, destination.getIPerformerFirstName() );
    assertEquals( null, destination.getIPerformerMiddleName() );
    assertEquals( null, destination.getIPerformerImage() );
    assertEquals( dummyPrimaryConsiderationPeople.isOriginal(), destination.isIPerformerOriginal() );
    assertEquals( dummyPrimaryConsiderationPeople.isResponsible(), destination.isIPerformerResponsible() );
    assertEquals( null, destination.isIPerformerGroup() );
    assertEquals( dummyPrimaryConsiderationPeople.isOrganization(), destination.isIPerformerOrganization() );
  }

  @Test
  public void convertFromPrimaryConsiderationPeopleToPrimaryConsiderationPeople() {
    IPerformer destination = mapper.convert(dummyPrimaryConsiderationPeople, PerformerMapper.DestinationType.PRIMARYCONSIDERATIONPEOPLE);

    assertNotNull( destination );
    assertTrue( destination instanceof PrimaryConsiderationPeople );
    assertEquals( null, destination.getIPerformerUid() );
    assertEquals( null, destination.getIPerformerNumber() );
    assertEquals( dummyPrimaryConsiderationPeople.getId(), destination.getIPerformerId() );
    assertEquals( null, destination.getIPerformerType() );
    assertEquals( dummyPrimaryConsiderationPeople.getName(), destination.getIPerformerName() );
    assertEquals( dummyPrimaryConsiderationPeople.getGender(), destination.getIPerformerGender() );
    assertEquals( dummyPrimaryConsiderationPeople.getOrganization(), destination.getIPerformerOrganizationName() );
    assertEquals( null, destination.getIPerformerAssistantId() );
    assertEquals( dummyPrimaryConsiderationPeople.getPosition(), destination.getIPerformerPosition() );
    assertEquals( null, destination.getIPerformerLastName() );
    assertEquals( null, destination.getIPerformerFirstName() );
    assertEquals( null, destination.getIPerformerMiddleName() );
    assertEquals( null, destination.getIPerformerImage() );
    assertEquals( dummyPrimaryConsiderationPeople.isOriginal(), destination.isIPerformerOriginal() );
    assertEquals( dummyPrimaryConsiderationPeople.isResponsible(), destination.isIPerformerResponsible() );
    assertEquals( null, destination.isIPerformerGroup() );
    assertEquals( dummyPrimaryConsiderationPeople.isOrganization(), destination.isIPerformerOrganization() );
  }

  @Test
  public void convertFromPrimaryConsiderationPeopleToOshs() {
    IPerformer destination = mapper.convert(dummyPrimaryConsiderationPeople, PerformerMapper.DestinationType.OSHS);

    assertNotNull( destination );
    assertTrue( destination instanceof Oshs);
    assertEquals( null, destination.getIPerformerUid() );
    assertEquals( null, destination.getIPerformerNumber() );
    assertEquals( dummyPrimaryConsiderationPeople.getId(), destination.getIPerformerId() );
    assertEquals( null, destination.getIPerformerType() );
    assertEquals( dummyPrimaryConsiderationPeople.getName(), destination.getIPerformerName() );
    assertEquals( dummyPrimaryConsiderationPeople.getGender(), destination.getIPerformerGender() );
    assertEquals( dummyPrimaryConsiderationPeople.getOrganization(), destination.getIPerformerOrganizationName() );
    assertEquals( null, destination.getIPerformerAssistantId() );
    assertEquals( dummyPrimaryConsiderationPeople.getPosition(), destination.getIPerformerPosition() );
    assertEquals( null, destination.getIPerformerLastName() );
    assertEquals( null, destination.getIPerformerFirstName() );
    assertEquals( null, destination.getIPerformerMiddleName() );
    assertEquals( null, destination.getIPerformerImage() );
    assertEquals( null, destination.isIPerformerOriginal() );
    assertEquals( null, destination.isIPerformerResponsible() );
    assertEquals( null, destination.isIPerformerGroup() );
    assertEquals( dummyPrimaryConsiderationPeople.isOrganization(), destination.isIPerformerOrganization() );
  }

  @Test
  public void convertFromOshsToPerformer() {
    IPerformer destination = mapper.convert(dummyOshs, PerformerMapper.DestinationType.PERFORMER);

    assertNotNull( destination );
    assertTrue( destination instanceof Performer );
    assertEquals( null, destination.getIPerformerUid() );
    assertEquals( null, destination.getIPerformerNumber() );
    assertEquals( dummyOshs.getId(), destination.getIPerformerId() );
    assertEquals( null, destination.getIPerformerType() );
    assertEquals( dummyOshs.getName(), destination.getIPerformerName() );
    assertEquals( dummyOshs.getGender(), destination.getIPerformerGender() );
    assertEquals( dummyOshs.getOrganization(), destination.getIPerformerOrganizationName() );
    assertEquals( null, destination.getIPerformerAssistantId() );
    assertEquals( null, destination.getIPerformerPosition() );
    assertEquals( null, destination.getIPerformerLastName() );
    assertEquals( null, destination.getIPerformerFirstName() );
    assertEquals( null, destination.getIPerformerMiddleName() );
    assertEquals( null, destination.getIPerformerImage() );
    assertEquals( null, destination.isIPerformerOriginal() );
    assertEquals( null, destination.isIPerformerResponsible() );
    assertEquals( null, destination.isIPerformerGroup() );
    assertEquals( dummyOshs.getIsOrganization(), destination.isIPerformerOrganization() );
  }

  @Test
  public void convertFromOshsToPrimaryConsiderationPeople() {
    IPerformer destination = mapper.convert(dummyOshs, PerformerMapper.DestinationType.PRIMARYCONSIDERATIONPEOPLE);

    assertNotNull( destination );
    assertTrue( destination instanceof PrimaryConsiderationPeople );
    assertEquals( null, destination.getIPerformerUid() );
    assertEquals( null, destination.getIPerformerNumber() );
    assertEquals( dummyOshs.getId(), destination.getIPerformerId() );
    assertEquals( null, destination.getIPerformerType() );
    assertEquals( dummyOshs.getName(), destination.getIPerformerName() );
    assertEquals( dummyOshs.getGender(), destination.getIPerformerGender() );
    assertEquals( dummyOshs.getOrganization(), destination.getIPerformerOrganizationName() );
    assertEquals( dummyOshs.getAssistantId(), destination.getIPerformerAssistantId() );
    assertEquals( dummyOshs.getPosition(), destination.getIPerformerPosition() );
    assertEquals( null, destination.getIPerformerLastName() );
    assertEquals( null, destination.getIPerformerFirstName() );
    assertEquals( null, destination.getIPerformerMiddleName() );
    assertEquals( null, destination.getIPerformerImage() );
    assertEquals( false, destination.isIPerformerOriginal() );
    assertEquals( false, destination.isIPerformerResponsible() );
    assertEquals( null, destination.isIPerformerGroup() );
    assertEquals( dummyOshs.getIsOrganization(), destination.isIPerformerOrganization() );
  }

  @Test
  public void convertFromOshsToOshs() {
    IPerformer destination = mapper.convert(dummyOshs, PerformerMapper.DestinationType.OSHS);

    assertNotNull( destination );
    assertTrue( destination instanceof Oshs );
    assertEquals( null, destination.getIPerformerUid() );
    assertEquals( null, destination.getIPerformerNumber() );
    assertEquals( dummyOshs.getId(), destination.getIPerformerId() );
    assertEquals( null, destination.getIPerformerType() );
    assertEquals( dummyOshs.getName(), destination.getIPerformerName() );
    assertEquals( dummyOshs.getGender(), destination.getIPerformerGender() );
    assertEquals( dummyOshs.getOrganization(), destination.getIPerformerOrganizationName() );
    assertEquals( dummyOshs.getAssistantId(), destination.getIPerformerAssistantId() );
    assertEquals( dummyOshs.getPosition(), destination.getIPerformerPosition() );
    assertEquals( dummyOshs.getLastName(), destination.getIPerformerLastName() );
    assertEquals( dummyOshs.getFirstName(), destination.getIPerformerFirstName() );
    assertEquals( dummyOshs.getMiddleName(), destination.getIPerformerMiddleName() );
    assertEquals( dummyOshs.getImage(), destination.getIPerformerImage() );
    assertEquals( null, destination.isIPerformerOriginal() );
    assertEquals( null, destination.isIPerformerResponsible() );
    assertEquals( dummyOshs.getIsGroup(), destination.isIPerformerGroup() );
    assertEquals( dummyOshs.getIsOrganization(), destination.isIPerformerOrganization() );
  }
}
