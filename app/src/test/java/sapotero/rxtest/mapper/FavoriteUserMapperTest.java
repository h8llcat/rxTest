package sapotero.rxtest.mapper;

import org.junit.Before;
import org.junit.Test;

import sapotero.rxtest.db.mapper.FavoriteUserMapper;
import sapotero.rxtest.db.requery.models.RFavoriteUserEntity;
import sapotero.rxtest.retrofit.models.Oshs;
import sapotero.rxtest.views.adapters.utils.PrimaryConsiderationPeople;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FavoriteUserMapperTest {

  private FavoriteUserMapper mapper;
  private Oshs dummyOshs;
  private Integer dummySortIndex;
  private RFavoriteUserEntity entity;
  private Oshs model;
  private PrimaryConsiderationPeople people;
  private String dummyLogin;

  @Before
  public void init() {
    dummyOshs = PrimaryConsiderationMapperTest.generateOshs();
    dummySortIndex = PrimaryConsiderationMapperTest.generateDummySortIndex();
    dummyLogin = "dummyLogin";
  }

  @Test
  public void toEntity() {
    mapper = new FavoriteUserMapper();
    entity = mapper.withLogin(dummyLogin).toEntity(dummyOshs);
    entity.setSortIndex( dummySortIndex );

    assertNotNull( entity );
    assertEquals( 0, entity.getId() );
    assertEquals( dummyOshs.getOrganization(), entity.getOrganization() );
    assertEquals( dummyOshs.getFirstName(), entity.getFirstName() );
    assertEquals( dummyOshs.getLastName(), entity.getLastName() );
    assertEquals( dummyOshs.getMiddleName(), entity.getMiddleName() );
    assertEquals( dummyOshs.getGender(), entity.getGender() );
    assertEquals( dummyOshs.getPosition(), entity.getPosition() );
    assertEquals( dummyOshs.getId(), entity.getUid() );
    assertEquals( dummyOshs.getName(), entity.getName() );
    assertEquals( dummyOshs.getIsGroup(), entity.isIsGroup() );
    assertEquals( dummyOshs.getIsOrganization(), entity.isIsOrganization() );
    assertEquals( dummyLogin, entity.getUser() );
    assertEquals( dummySortIndex, entity.getSortIndex() );
  }

  @Test
  public void toModel() {
    mapper = new FavoriteUserMapper();
    entity = mapper.toEntity(dummyOshs);
    model = mapper.toModel(entity);

    assertNotNull( model );
    assertEquals( dummyOshs.getOrganization(), model.getOrganization() );
    assertEquals( dummyOshs.getFirstName(), model.getFirstName() );
    assertEquals( dummyOshs.getLastName(), model.getLastName() );
    assertEquals( dummyOshs.getMiddleName(), model.getMiddleName() );
    assertEquals( dummyOshs.getGender(), model.getGender() );
    assertEquals( dummyOshs.getPosition(), model.getPosition() );
    assertEquals( dummyOshs.getId(), model.getId() );
    assertEquals( dummyOshs.getName(), model.getName() );
    assertEquals( dummyOshs.getIsGroup(), model.getIsGroup() );
    assertEquals( dummyOshs.getIsOrganization(), model.getIsOrganization() );
  }

  @Test
  public void toPrimaryConsiderationPeople() {
    mapper = new FavoriteUserMapper();
    entity = mapper.toEntity(dummyOshs);
    entity.setSortIndex(dummySortIndex);
    people = mapper.toPrimaryConsiderationPeople(entity);

    assertNotNull( people );
    assertEquals( dummyOshs.getOrganization(), people.getOrganization() );
    assertEquals( dummyOshs.getGender(), people.getGender() );
    assertEquals( dummyOshs.getPosition(), people.getPosition() );
    assertEquals( dummyOshs.getId(), people.getId() );
    assertEquals( dummyOshs.getName(), people.getName() );
    assertEquals( dummyOshs.getIsOrganization(), people.isOrganization() );
    assertEquals( dummySortIndex, people.getSortIndex() );
  }

  @Test
  public void hasDiff() {
    mapper = new FavoriteUserMapper();

    RFavoriteUserEntity entity1 = mapper.toEntity(dummyOshs);
    RFavoriteUserEntity entity2 = mapper.toEntity(dummyOshs);

    boolean hasDiff = mapper.hasDiff(entity1, entity2);

    assertFalse( hasDiff );

    entity2.setUid("");
    hasDiff = mapper.hasDiff(entity1, entity2);

    assertTrue( hasDiff );
  }
}
