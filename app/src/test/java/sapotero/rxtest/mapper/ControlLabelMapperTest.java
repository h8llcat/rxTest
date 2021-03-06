package sapotero.rxtest.mapper;


import org.junit.Before;
import org.junit.Test;

import sapotero.rxtest.db.mapper.ControlLabelMapper;
import sapotero.rxtest.db.requery.models.control_labels.RControlLabelsEntity;
import sapotero.rxtest.retrofit.models.document.ControlLabel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ControlLabelMapperTest {

  private ControlLabelMapper mapper;
  private ControlLabel dummyControlLabel;
  private RControlLabelsEntity entity;
  private ControlLabel model;

  @Before
  public void init() {
    dummyControlLabel = generateControlLabel();
  }

  public static ControlLabel generateControlLabel() {
    ControlLabel dummyControlLabel = new ControlLabel();
    dummyControlLabel.setCreatedAt( "20.07.2017 15:33" );
    dummyControlLabel.setOfficialId( "58f88dfc776b000026000001" );
    dummyControlLabel.setOfficialName( "Сотрудник_а2 A.T." );
    dummyControlLabel.setSkippedOfficialId( null );
    dummyControlLabel.setSkippedOfficialName( null );
    dummyControlLabel.setState( "Отмечен для постановки на контроль" );
    return dummyControlLabel;
  }

  @Test
  public void toEntity() {
    mapper = new ControlLabelMapper();
    entity = mapper.toEntity(dummyControlLabel);

    verifyControlLabel( dummyControlLabel, entity );
  }

  public static void verifyControlLabel(ControlLabel expected, RControlLabelsEntity actual) {
    assertNotNull( actual );
    assertEquals( 0, actual.getId() );
    assertEquals( expected.getCreatedAt(), actual.getCreatedAt() );
    assertEquals( expected.getOfficialId(), actual.getOfficialId() );
    assertEquals( expected.getOfficialName(), actual.getOfficialName() );
    assertEquals( expected.getSkippedOfficialId(), actual.getSkippedOfficialId() );
    assertEquals( expected.getSkippedOfficialName(), actual.getSkippedOfficialName() );
    assertEquals( expected.getState(), actual.getState() );
  }

  @Test
  public void toModel() {
    mapper = new ControlLabelMapper();
    entity = mapper.toEntity(dummyControlLabel);
    model = mapper.toModel(entity);

    verifyControlLabel( dummyControlLabel, model );
  }

  public static void verifyControlLabel(ControlLabel dummyControlLabel, ControlLabel model) {
    assertNotNull( model );
    assertEquals( dummyControlLabel.getCreatedAt(), model.getCreatedAt() );
    assertEquals( dummyControlLabel.getOfficialId(), model.getOfficialId() );
    assertEquals( dummyControlLabel.getOfficialName(), model.getOfficialName() );
    assertEquals( dummyControlLabel.getSkippedOfficialId(), model.getSkippedOfficialId() );
    assertEquals( dummyControlLabel.getSkippedOfficialName(), model.getSkippedOfficialName() );
    assertEquals( dummyControlLabel.getState(), model.getState() );
  }

  @Test
  public void hasDiff() {
    mapper = new ControlLabelMapper();

    RControlLabelsEntity entity1 = mapper.toEntity(dummyControlLabel);
    RControlLabelsEntity entity2 = mapper.toEntity(dummyControlLabel);

    boolean hasDiff = mapper.hasDiff(entity1, entity2);

    assertFalse( hasDiff );

    entity2.setOfficialId( "" );
    hasDiff = mapper.hasDiff(entity1, entity2);

    assertTrue( hasDiff );
  }
}
