package sapotero.rxtest.application.components;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import sapotero.rxtest.application.modules.EsdModule;
import sapotero.rxtest.application.modules.SettingsModule;
import sapotero.rxtest.db.requery.query.DBQueryBuilder;
import sapotero.rxtest.events.utils.SubscriptionsModule;
import sapotero.rxtest.jobs.bus.BaseJob;
import sapotero.rxtest.jobs.utils.JobModule;
import sapotero.rxtest.retrofit.utils.OkHttpModule;
import sapotero.rxtest.views.activities.DecisionConstructorActivity;
import sapotero.rxtest.views.activities.DocumentImageFullScreenActivity;
import sapotero.rxtest.views.activities.DocumentInfocardFullScreenActivity;
import sapotero.rxtest.views.activities.InfoActivity;
import sapotero.rxtest.views.activities.LoginActivity;
import sapotero.rxtest.views.activities.MainActivity;
import sapotero.rxtest.views.activities.SettingsActivity;
import sapotero.rxtest.views.activities.SettingsTemplatesActivity;
import sapotero.rxtest.views.adapters.DecisionAdapter;
import sapotero.rxtest.views.adapters.DocumentsAdapter;
import sapotero.rxtest.views.adapters.OshsAutoCompleteAdapter;
import sapotero.rxtest.views.adapters.models.DocumentTypeItem;
import sapotero.rxtest.views.dialogs.SelectOshsDialogFragment;
import sapotero.rxtest.views.fragments.DecisionFragment;
import sapotero.rxtest.views.fragments.DecisionPreviewFragment;
import sapotero.rxtest.views.fragments.InfoCardDocumentsFragment;
import sapotero.rxtest.views.fragments.InfoCardFieldsFragment;
import sapotero.rxtest.views.fragments.InfoCardLinksFragment;
import sapotero.rxtest.views.fragments.InfoCardWebViewFragment;
import sapotero.rxtest.views.fragments.RoutePreviewFragment;
import sapotero.rxtest.views.interfaces.DataLoaderInterface;
import sapotero.rxtest.views.interfaces.DocumentManager;
import sapotero.rxtest.views.managers.db.factories.DocumentFactory;
import sapotero.rxtest.views.managers.menu.OperationManager;
import sapotero.rxtest.views.managers.menu.commands.AbstractCommand;
import sapotero.rxtest.views.managers.menu.commands.performance.ApprovalPerformance;
import sapotero.rxtest.views.managers.menu.commands.performance.DelegatePerformance;
import sapotero.rxtest.views.managers.menu.commands.report.FromTheReport;
import sapotero.rxtest.views.managers.menu.commands.report.ReturnToPrimaryConsideration;
import sapotero.rxtest.views.managers.view.DecisionManager;
import sapotero.rxtest.views.menu.builders.ButtonBuilder;
import sapotero.rxtest.views.services.AuthService;

@Singleton
@Component(modules = {
  EsdModule.class,
  JobModule.class,
  SubscriptionsModule.class,
  OkHttpModule.class,
  SettingsModule.class
})

public interface EsdComponent {
  void inject(LoginActivity activity);
  void inject(MainActivity  activity);
  void inject(InfoActivity  activity);
  void inject(SettingsActivity activity);
  void inject(DecisionConstructorActivity activity);
  void inject(DocumentImageFullScreenActivity activity);
  void inject(DocumentInfocardFullScreenActivity activity);
  void inject(SettingsTemplatesActivity activity);



  void inject(AuthService service);

  void inject(DecisionAdapter adapter);
  void inject(DocumentsAdapter adapter);
  void inject(OshsAutoCompleteAdapter context);

  void inject(DecisionFragment fragment);
  void inject(DecisionPreviewFragment fragment);
  void inject(InfoCardWebViewFragment fragment);
  void inject(InfoCardDocumentsFragment fragment);
  void inject(SelectOshsDialogFragment fragment);
  void inject(InfoCardFieldsFragment fragment);
  void inject(InfoCardLinksFragment fragment);
  void inject(RoutePreviewFragment fragment);




  void inject(BaseJob job);


  void inject(DataLoaderInterface context);
  void inject(DocumentManager context);

  void inject(DocumentTypeItem context);
  void inject(ButtonBuilder context);

  void inject(DBQueryBuilder context);


  void inject(DocumentFactory context);
  void inject(DecisionManager context);

  void inject(OperationManager context);
  void inject(AbstractCommand context);

  void inject(FromTheReport context);
  void inject(ReturnToPrimaryConsideration context);
  void inject(ApprovalPerformance context);
  void inject(DelegatePerformance context);

  Application application();
}