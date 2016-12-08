package sapotero.rxtest.views.menu.builders;

import io.requery.query.Expression;
import io.requery.query.LogicalCondition;

public class ConditionBuilder {

  public enum Condition{
    WHERE,
    AND,
    OR;
  }

  private final Condition condition;

  private LogicalCondition<? extends Expression<String>, ?> field;

  public ConditionBuilder(Condition condition, LogicalCondition<? extends Expression<String>, ?> field) {
    this.condition = condition;
    this.field = field;
  }

  public Condition getCondition() {
    return condition;
  }

  public String toString(){
    return String.format( "NAME: %s - %s %s %s ", condition, field.getLeftOperand(), field.getOperator(), field.getRightOperand() );
  }

  public LogicalCondition<? extends Expression<String>, ?> getField() {
    return field;
  }

}