package sapotero.rxtest.managers.menu.factories;

import sapotero.rxtest.managers.menu.commands.decision.AddAndApproveDecision;
import sapotero.rxtest.managers.menu.commands.decision.AddDecision;
import sapotero.rxtest.managers.menu.commands.decision.AddTemporaryDecision;
import sapotero.rxtest.managers.menu.commands.decision.ApproveDecision;
import sapotero.rxtest.managers.menu.commands.decision.RejectDecision;
import sapotero.rxtest.managers.menu.commands.decision.SaveAndApproveDecision;
import sapotero.rxtest.managers.menu.commands.decision.SaveDecision;
import sapotero.rxtest.managers.menu.commands.file.SignFile;
import sapotero.rxtest.managers.menu.commands.performance.ApprovalPerformance;
import sapotero.rxtest.managers.menu.commands.performance.DelegatePerformance;
import sapotero.rxtest.managers.menu.commands.primary_consideration.PrimaryConsideration;
import sapotero.rxtest.managers.menu.commands.report.FromTheReport;
import sapotero.rxtest.managers.menu.commands.report.ReturnToPrimaryConsideration;
import sapotero.rxtest.managers.menu.commands.shared.AddToFolder;
import sapotero.rxtest.managers.menu.commands.shared.CheckControlLabel;
import sapotero.rxtest.managers.menu.commands.shared.DoNothing;
import sapotero.rxtest.managers.menu.commands.shared.RemoveFromFolder;
import sapotero.rxtest.managers.menu.commands.shared.UncheckControlLabel;
import sapotero.rxtest.managers.menu.commands.templates.CreateTemplate;
import sapotero.rxtest.managers.menu.commands.templates.RemoveTemplate;
import sapotero.rxtest.managers.menu.commands.templates.UpdateTemplate;
import sapotero.rxtest.managers.menu.commands.update.UpdateDocumentCommand;
import sapotero.rxtest.managers.menu.interfaces.Command;
import sapotero.rxtest.managers.menu.utils.CommandParams;
import timber.log.Timber;

public class CommandFactory {
  private static CommandFactory instance;
  private final String TAG = this.getClass().getSimpleName();

  private CommandParams params;

  public static CommandFactory getInstance() {
    if (instance == null){
      instance = new CommandFactory();
    }
    return instance;
  }

  public enum Operation {
    FILE_SIGN {
      @Override
      public Command getCommand(CommandParams params) {
        return new SignFile(params);
      }

      @Override
      public String getRussianName() {
        return "Подписание электронного образа";
      }
    },

    FROM_THE_REPORT {
      @Override
      public Command getCommand(CommandParams params) {
        return new FromTheReport(params);
      }

      @Override
      public String getRussianName() {
        return "Возврат с доклада";
      }
    },

    RETURN_TO_THE_PRIMARY_CONSIDERATION {
      @Override
      public Command getCommand(CommandParams params) {
        return new ReturnToPrimaryConsideration(params);
      }

      @Override
      public String getRussianName() {
        return "Отклонение документа с возвратом на первичное рассмотрение";
      }
    },

    DELEGATE_PERFORMANCE {
      @Override
      public Command getCommand(CommandParams params) {
        return new DelegatePerformance(params);
      }

      @Override
      public String getRussianName() {
        return "Передача исполнения";
      }
    },

    TO_THE_APPROVAL_PERFORMANCE {
      @Override
      public Command getCommand(CommandParams params) {
        return new ApprovalPerformance(params);
      }

      @Override
      public String getRussianName() {
        return "Исполнение без ответа";
      }
    },

    TO_THE_PRIMARY_CONSIDERATION {
      @Override
      public Command getCommand(CommandParams params) {
        return new PrimaryConsideration(params);
      }

      @Override
      public String getRussianName() {
        return "Передача первичного рассмотрения";
      }
    },

    APPROVAL_CHANGE_PERSON {
      @Override
      public Command getCommand(CommandParams params) {
        return new sapotero.rxtest.managers.menu.commands.approval.ChangePerson(params);
      }

      @Override
      public String getRussianName() {
        return "Передача согласования";
      }
    },

    APPROVAL_NEXT_PERSON {
      @Override
      public Command getCommand(CommandParams params) {
        return new sapotero.rxtest.managers.menu.commands.approval.NextPerson(params);
      }

      @Override
      public String getRussianName() {
        return "Передача согласования документа следующему в маршруте ДЛ";
      }
    },

    APPROVAL_PREV_PERSON {
      @Override
      public Command getCommand(CommandParams params) {
        return new sapotero.rxtest.managers.menu.commands.approval.PrevPerson(params);
      }

      @Override
      public String getRussianName() {
        return "Передача согласования документа предыдущему в маршруте ДЛ";
      }
    },

    SIGNING_CHANGE_PERSON {
      @Override
      public Command getCommand(CommandParams params) {
        return new sapotero.rxtest.managers.menu.commands.signing.ChangePerson(params);
      }

      @Override
      public String getRussianName() {
        return "Передача подписания";
      }
    },

    SIGNING_NEXT_PERSON {
      @Override
      public Command getCommand(CommandParams params) {
        return new sapotero.rxtest.managers.menu.commands.signing.NextPerson(params);
      }

      @Override
      public String getRussianName() {
        return "Передача подписания документа следующему в маршруте ДЛ";
      }
    },

    SIGNING_PREV_PERSON {
      @Override
      public Command getCommand(CommandParams params) {
        return new sapotero.rxtest.managers.menu.commands.signing.PrevPerson(params);
      }

      @Override
      public String getRussianName() {
        return "Передача подписания документа предыдущему в маршруте ДЛ";
      }
    },

    ADD_TO_FOLDER {
      @Override
      public Command getCommand(CommandParams params) {
        return new AddToFolder(params);
      }

      @Override
      public String getRussianName() {
        return "Добавление в избранное";
      }
    },

    REMOVE_FROM_FOLDER {
      @Override
      public Command getCommand(CommandParams params) {
        return new RemoveFromFolder(params);
      }

      @Override
      public String getRussianName() {
        return "Удаление из избранного";
      }
    },

    CHECK_CONTROL_LABEL {
      @Override
      public Command getCommand(CommandParams params) {
        return new CheckControlLabel(params);
      }

      @Override
      public String getRussianName() {
        return "Установка отметки о необходимости постановки на контроль";
      }
    },

    UNCHECK_CONTROL_LABEL {
      @Override
      public Command getCommand(CommandParams params) {
        return new UncheckControlLabel(params);
      }

      @Override
      public String getRussianName() {
        return "Удаление отметки о необходимости постановки на контроль";
      }
    },

    INCORRECT {
      @Override
      public Command getCommand(CommandParams params) {
        return new DoNothing(params);
      }

      @Override
      public String getRussianName() {
        return "Операция заглушка для тестов";
      }
    },

    SAVE_DECISION {
      @Override
      public Command getCommand(CommandParams params) {
        return new SaveDecision(params);
      }

      @Override
      public String getRussianName() {
        return "Сохранение резолюции";
      }
    },

    CREATE_DECISION {
      @Override
      public Command getCommand(CommandParams params) {
        return new AddDecision(params);
      }

      @Override
      public String getRussianName() {
        return "Создание резолюции";
      }
    },

    CREATE_TEMPORARY_DECISION {
      @Override
      public Command getCommand(CommandParams params) {
        return new AddTemporaryDecision(params);
      }

      @Override
      public String getRussianName() {
        return "*Создание резолюции";
      }
    },

    SAVE_AND_APPROVE_DECISION {
      @Override
      public Command getCommand(CommandParams params) {
        return new SaveAndApproveDecision(params);
      }

      @Override
      public String getRussianName() {
        return "Сохранение и подписание резолюции";
      }
    },

    CREATE_AND_APPROVE_DECISION {
      @Override
      public Command getCommand(CommandParams params) {
        return new AddAndApproveDecision(params);
      }

      @Override
      public String getRussianName() {
        return "Создание и подписание резолюции";
      }
    },

    APPROVE_DECISION {
      @Override
      public Command getCommand(CommandParams params) {
        return new ApproveDecision(params);
      }

      @Override
      public String getRussianName() {
        return "Подписание резолюции";
      }
    },

    REJECT_DECISION {
      @Override
      public Command getCommand(CommandParams params) {
        return new RejectDecision(params);
      }

      @Override
      public String getRussianName() {
        return "Отклонение резолюции";
      }
    },

    CREATE_DECISION_TEMPLATE {
      @Override
      public Command getCommand(CommandParams params) {
        return new CreateTemplate(params);
      }

      @Override
      public String getRussianName() {
        return "Создание шаблона резолюции";
      }
    },

    UPDATE_DECISION_TEMPLATE {
      @Override
      public Command getCommand(CommandParams params) {
        return new UpdateTemplate(params);
      }

      @Override
      public String getRussianName() {
        return "Изменение шаблона резолюции";
      }
    },

    DELETE_DECISION_TEMPLATE {
      @Override
      public Command getCommand(CommandParams params) {
        return new RemoveTemplate(params);
      }

      @Override
      public String getRussianName() {
        return "Удаление шаблона резолюции";
      }
    },

    UPDATE_DOCUMENT {
      @Override
      public Command getCommand(CommandParams params) {
        return new UpdateDocumentCommand(params);
      }

      @Override
      public String getRussianName() {
        return "Обновление документа";
      }
    };

    public static Operation getOperation(String operation_type){
      Operation operation = Operation.INCORRECT;

      switch ( operation_type ){
        case "sapotero.rxtest.managers.menu.commands.file.SignFile":
          operation = Operation.FILE_SIGN;
          break;

        case "sapotero.rxtest.managers.menu.commands.decision.AddDecision":
          operation = Operation.CREATE_DECISION;
          break;
        case "sapotero.rxtest.managers.menu.commands.decision.AddTemporaryDecision":
          operation = Operation.CREATE_TEMPORARY_DECISION;
          break;
        case "sapotero.rxtest.managers.menu.commands.decision.AddAndApproveDecision":
          operation = Operation.CREATE_AND_APPROVE_DECISION;
          break;

        case "sapotero.rxtest.managers.menu.commands.decision.SaveDecision":
          operation = Operation.SAVE_DECISION;
          break;
        case "sapotero.rxtest.managers.menu.commands.decision.SaveAndApproveDecision":
          operation = Operation.SAVE_AND_APPROVE_DECISION;
          break;

        case "sapotero.rxtest.managers.menu.commands.decision.ApproveDecision":
          operation = Operation.APPROVE_DECISION;
          break;
        case "sapotero.rxtest.managers.menu.commands.decision.RejectDecision":
          operation = Operation.REJECT_DECISION;
          break;

        case "sapotero.rxtest.managers.menu.commands.report.ReturnToPrimaryConsideration":
          operation = Operation.RETURN_TO_THE_PRIMARY_CONSIDERATION;
          break;
        // sent_to_the_report (отправлен на доклад)
        case "sapotero.rxtest.managers.menu.commands.report.FromTheReport":
          operation = Operation.FROM_THE_REPORT;
          break;

        // sent_to_the_performance (Отправлен на исполнение)
        case "sapotero.rxtest.managers.menu.commands.performance.DelegatePerformance":
          operation = Operation.DELEGATE_PERFORMANCE;
          break;
        case "sapotero.rxtest.managers.menu.commands.performance.ApprovalPerformance":
          operation = Operation.TO_THE_APPROVAL_PERFORMANCE;
          break;

        // primary_consideration (первичное рассмотрение)
        case "sapotero.rxtest.managers.menu.commands.primary_consideration.PrimaryConsideration":
          operation = Operation.TO_THE_PRIMARY_CONSIDERATION;
          break;

        // approval (согласование проектов документов)
        case "sapotero.rxtest.managers.menu.commands.approval.ChangePerson":
          operation = Operation.APPROVAL_CHANGE_PERSON;
          break;
        case "sapotero.rxtest.managers.menu.commands.approval.NextPerson":
          operation = Operation.APPROVAL_NEXT_PERSON;
          break;
        case "sapotero.rxtest.managers.menu.commands.approval.PrevPerson":
          operation = Operation.APPROVAL_PREV_PERSON;
          break;

        // approval (согласование проектов документов)
        case "sapotero.rxtest.managers.menu.commands.signing.ChangePerson":
          operation = Operation.SIGNING_CHANGE_PERSON;
          break;
        case "sapotero.rxtest.managers.menu.commands.signing.NextPerson":
          operation = Operation.SIGNING_NEXT_PERSON;
          break;
        case "sapotero.rxtest.managers.menu.commands.signing.PrevPerson":
          operation = Operation.SIGNING_PREV_PERSON;
          break;

        case "sapotero.rxtest.managers.menu.commands.shared.AddToFolder":
          operation = Operation.ADD_TO_FOLDER;
          break;
        case "sapotero.rxtest.managers.menu.commands.shared.RemoveFromFolder":
          operation = Operation.REMOVE_FROM_FOLDER;
          break;

        case "sapotero.rxtest.managers.menu.commands.shared.CheckControlLabel":
          operation = Operation.CHECK_CONTROL_LABEL;
          break;
        case "sapotero.rxtest.managers.menu.commands.shared.UncheckControlLabel":
          operation = Operation.UNCHECK_CONTROL_LABEL;
          break;

        case "sapotero.rxtest.managers.menu.commands.shared.DoNothing":
          operation = Operation.INCORRECT;
          break;

        case "sapotero.rxtest.managers.menu.commands.templates.CreateTemplate":
          operation = Operation.CREATE_DECISION_TEMPLATE;
          break;
        case "sapotero.rxtest.managers.menu.commands.templates.UpdateTemplate":
          operation = Operation.UPDATE_DECISION_TEMPLATE;
          break;
        case "sapotero.rxtest.managers.menu.commands.templates.RemoveTemplate":
          operation = Operation.DELETE_DECISION_TEMPLATE;
          break;
        case "sapotero.rxtest.managers.menu.commands.update.UpdateDocumentCommand":
          operation = Operation.UPDATE_DOCUMENT;
          break;
      }

      return operation;
    }

    public abstract Command getCommand(CommandParams params);
    public abstract String getRussianName();
  }

  private CommandFactory() {
  }

  public CommandFactory withParams(CommandParams params) {
    Timber.tag(TAG).w("withParams" );
    this.params = params;
    return this;
  }

  public Command build(CommandFactory.Operation operation) {
    Timber.tag(TAG).w("build" );

    Command command = operation.getCommand(params);

    Timber.tag("CommandFactory").w("after build" );
    return command;
  }
}
