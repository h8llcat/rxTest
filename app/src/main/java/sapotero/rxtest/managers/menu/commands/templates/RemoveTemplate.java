package sapotero.rxtest.managers.menu.commands.templates;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;

import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sapotero.rxtest.db.requery.models.RTemplateEntity;
import sapotero.rxtest.events.decision.AddDecisionTemplateEvent;
import sapotero.rxtest.managers.menu.commands.AbstractCommand;
import sapotero.rxtest.managers.menu.utils.CommandParams;
import sapotero.rxtest.retrofit.TemplatesService;
import sapotero.rxtest.retrofit.models.Template;

public class RemoveTemplate extends AbstractCommand {

  public RemoveTemplate(CommandParams params) {
    super(params);
  }

  public void registerCallBack(Callback callback){
    this.callback = callback;
  }

  @Override
  public void execute() {
    queueManager.add(this);
  }

  @Override
  public String getType() {
    return "remove_template";
  }

  @Override
  public void executeLocal() {
    queueManager.setExecutedLocal(this);

    if ( callback != null ){
      callback.onCommandExecuteSuccess( getType() );
    }
  }

  @Override
  public void executeRemote() {
    Retrofit retrofit = getRetrofit();

    TemplatesService templatesService = retrofit.create( TemplatesService.class );

    Observable<Template> info = templatesService.remove(
      getParams().getUuid(),
      getParams().getLogin(),
      settings.getToken()
    );

    info
      .subscribeOn( Schedulers.computation() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        data -> {
          queueManager.setExecutedRemote(this);
          remove();
        },
        error -> {
          if (callback != null){
            callback.onCommandExecuteError(getType());
          }

          if ( settings.isOnline() ) {
            queueManager.setExecutedWithError( this, Collections.singletonList( error.getLocalizedMessage() ) );
          }
        }
      );
  }

  private void remove() {
    dataStore
      .delete( RTemplateEntity.class )
      .where(RTemplateEntity.UID.eq( getParams().getUuid() ))
      .get().value();

    EventBus.getDefault().post( new AddDecisionTemplateEvent() );
  }
}
