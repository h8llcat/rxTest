package sapotero.rxtest.managers.menu.commands.templates;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sapotero.rxtest.db.requery.models.RTemplateEntity;
import sapotero.rxtest.events.decision.AddDecisionTemplateEvent;
import sapotero.rxtest.managers.menu.commands.AbstractCommand;
import sapotero.rxtest.managers.menu.receivers.DocumentReceiver;
import sapotero.rxtest.managers.menu.utils.CommandParams;
import sapotero.rxtest.retrofit.TemplatesService;
import sapotero.rxtest.retrofit.models.Template;
import timber.log.Timber;

public class CreateTemplate extends AbstractCommand {

  private final DocumentReceiver document;

  private String TAG = this.getClass().getSimpleName();

  public CreateTemplate(DocumentReceiver document){
    super();
    this.document = document;
  }

  public String getInfo(){
    return null;
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
    return "create_template";
  }

  @Override
  public void executeLocal() {
    queueManager.setExecutedLocal(this);

  }

  @Override
  public void executeRemote() {
    Retrofit retrofit = getRetrofit();

    TemplatesService templatesService = retrofit.create( TemplatesService.class );

    String type = null;
    if ( params.getLabel() != null && !Objects.equals(params.getLabel(), "decision")){
      type = params.getLabel();
    }

    Observable<Template> info = templatesService.create(
      settings.getLogin(),
      settings.getToken(),
      params.getComment(),
      type
    );

    info
      .subscribeOn( Schedulers.computation() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        data -> {
          queueManager.setExecutedRemote(this);

          insertTemplate(data);
        },
        error -> {
          if (callback != null){
            callback.onCommandExecuteError(getType());
          }
        }
      );
  }

  private void insertTemplate(Template data) {
    RTemplateEntity template = mappers.getTemplateMapper().toEntity(data);
    template.setType(params.getLabel());

    dataStore
      .insert(template)
      .toObservable()
      .subscribeOn( Schedulers.computation() )
      .observeOn( AndroidSchedulers.mainThread() )
      .subscribe(
        temp -> {
          Timber.tag(TAG).i("success");
          Timber.tag(TAG).i("%s", new Gson().toJson(temp));

          EventBus.getDefault().post( new AddDecisionTemplateEvent() );
        },
        error -> {
          Timber.tag(TAG).e(error);
        }
      );
  }
}
