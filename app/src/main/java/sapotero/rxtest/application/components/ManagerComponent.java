package sapotero.rxtest.application.components;

import dagger.Subcomponent;
import sapotero.rxtest.application.scopes.ManagerScope;
import sapotero.rxtest.jobs.bus.BaseJob;
import sapotero.rxtest.jobs.utils.JobModule;
import sapotero.rxtest.managers.DataLoaderManager;
import sapotero.rxtest.managers.menu.commands.AbstractCommand;
import sapotero.rxtest.managers.menu.utils.OperationManagerModule;
import sapotero.rxtest.managers.toolbar.ToolbarManager;
import sapotero.rxtest.services.MainService;
import sapotero.rxtest.utils.queue.utils.QueueManagerModule;
import sapotero.rxtest.views.activities.DecisionConstructorActivity;
import sapotero.rxtest.views.activities.InfoActivity;
import sapotero.rxtest.views.activities.MainActivity;
import sapotero.rxtest.views.activities.SettingsTemplatesActivity;
import sapotero.rxtest.views.adapters.DecisionAdapter;
import sapotero.rxtest.views.fragments.DecisionPreviewFragment;
import sapotero.rxtest.views.fragments.DecisionRejectionTemplateFragment;
import sapotero.rxtest.views.fragments.DecisionTemplateFragment;
import sapotero.rxtest.views.fragments.InfoActivityDecisionPreviewFragment;

@ManagerScope
@Subcomponent(modules = {
  JobModule.class,
  QueueManagerModule.class,
  OperationManagerModule.class,
})

public interface ManagerComponent {
  void inject(MainActivity activity);
  void inject(InfoActivity activity);
  void inject(DecisionConstructorActivity activity);
  void inject(SettingsTemplatesActivity activity);

  void inject(MainService service);

  void inject(InfoActivityDecisionPreviewFragment fragment);
  void inject(DecisionTemplateFragment fragment);
  void inject(DecisionRejectionTemplateFragment fragment);
  void inject(DecisionPreviewFragment fragment);

  void inject(DecisionAdapter adapter);

  void inject(BaseJob job);

  void inject(ToolbarManager toolbarManager);
  void inject(DataLoaderManager dataLoaderManager);
  void inject(AbstractCommand abstractCommand);
}