package sapotero.rxtest.views.custom.stepper.build.steps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import javax.inject.Inject;

import rx.Subscription;
import sapotero.rxtest.R;
import sapotero.rxtest.application.EsdApplication;
import sapotero.rxtest.db.requery.utils.DocumentStateSaver;
import sapotero.rxtest.events.stepper.auth.StepperDcCheckEvent;
import sapotero.rxtest.events.stepper.auth.StepperDcCheckFailEvent;
import sapotero.rxtest.events.stepper.auth.StepperDcCheckSuccesEvent;
import sapotero.rxtest.events.stepper.auth.StepperLoginCheckEvent;
import sapotero.rxtest.events.stepper.auth.StepperLoginCheckFailEvent;
import sapotero.rxtest.events.stepper.auth.StepperLoginCheckSuccessEvent;
import sapotero.rxtest.events.utils.LoadedFromDbEvent;
import sapotero.rxtest.utils.ISettings;
import sapotero.rxtest.utils.memory.MemoryStore;
import sapotero.rxtest.views.custom.stepper.BlockingStep;
import sapotero.rxtest.views.custom.stepper.StepperLayout;
import sapotero.rxtest.views.custom.stepper.VerificationError;
import sapotero.rxtest.views.custom.stepper.util.AuthType;
import timber.log.Timber;

public class StepperAuthFragment extends Fragment implements BlockingStep {

  @Inject ISettings settings;
  @Inject JobManager jobManager;
  @Inject MemoryStore store;

  final String TAG = this.getClass().getSimpleName();

  private MaterialDialog loadingDialog;

  private FrameLayout stepper_auth_password_wrapper;
  private FrameLayout stepper_auth_dc_wrapper;
  private EditText passwordEditText;

  private Subscription auth_type_subscription;

  private AuthType authType = AuthType.PASSWORD;

  private VerificationError error = new VerificationError("error");

  private StepperLayout.OnNextClickedCallback callback;

  private boolean startAuthorization = false;
  private String oldLogin;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    EsdApplication.getManagerComponent().inject(this);

    View view = inflater.inflate(R.layout.stepper_auth, container, false);

    stepper_auth_password_wrapper = (FrameLayout) view.findViewById(R.id.stepper_auth_password_wrapper);
    stepper_auth_dc_wrapper       = (FrameLayout) view.findViewById(R.id.stepper_auth_dc_wrapper);
    passwordEditText = (EditText) stepper_auth_dc_wrapper.findViewById(R.id.stepper_auth_dc_password);

    initialize();

    if ( EventBus.getDefault().isRegistered(this) ) {
      EventBus.getDefault().unregister(this);
    }
    EventBus.getDefault().register(this);

    return view;
  }

  private void initialize() {
    hideAllFields();
    attachSettings();
    prepareDialog();
  }

  private void prepareDialog() {
    if (loadingDialog == null){
      loadingDialog = new MaterialDialog.Builder( getContext() )
        .title(R.string.app_name)
        .content(R.string.action_settings)
        .cancelable(false)
        .progress(true, 0).build();
    }
  }

  private void attachSettings() {
    if (settings.getAuthType() == null) {
      settings.setAuthType( authType );
    }

    auth_type_subscription = settings.getAuthTypePreference().asObservable().subscribe(type -> {
      switch ( type ){
        case DS:
          authType = AuthType.DS;
          showDc();
          break;
        case PASSWORD:
          authType = AuthType.PASSWORD;
          showPassword();
          break;
      }
    }, Timber::e);
  }

  private void hideAllFields(){
    stepper_auth_password_wrapper.setVisibility(View.GONE);
    stepper_auth_dc_wrapper.setVisibility(View.GONE);
  }

  @Override
  public void onDestroy(){
    super.onDestroy();
    if (auth_type_subscription != null) {
      auth_type_subscription.unsubscribe();
    }

    if ( EventBus.getDefault().isRegistered(this) ) {
      EventBus.getDefault().unregister(this);
    }

  }

  @Override
  @StringRes
  public int getName() {
    return R.string.stepper_auth;
  }

  @Override
  public VerificationError verifyStep() {

    switch ( authType ){
      case DS:
        String enteredText = passwordEditText.getText().toString();

        if (enteredText.equals("qwerty")) {
          setAuthTypePassword();
        } else {
          initStartAuthorization();
          EventBus.getDefault().post( new StepperDcCheckEvent( enteredText ) );
        }

        break;
      case PASSWORD:
        EditText login = (EditText) stepper_auth_password_wrapper.findViewById(R.id.stepper_auth_username);
        EditText pwd   = (EditText) stepper_auth_password_wrapper.findViewById(R.id.stepper_auth_password);
//        EditText host  = (EditText) stepper_auth_password_wrapper.findViewById(R.id.stepper_auth_host);

        initStartAuthorization();
        EventBus.getDefault().post(
          new StepperLoginCheckEvent(
            login.getText().toString(),
            pwd.getText().toString()
          )
        );
        break;
    }
    error = null;

    return error;
  }

  private void initStartAuthorization() {
    oldLogin = settings.getLogin();
    startAuthorization = true;
  }

  @Override
  public void onSelected() {
    passwordEditText.setText("");
    jobManager.cancelJobsInBackground(null, TagConstraint.ANY, "DocJob");
  }

  @Override
  public void onError(@NonNull VerificationError error) {
//    Toast.makeText( getContext(), "Errror", Toast.LENGTH_SHORT ).show();
  }

  private void showPassword(){
    stepper_auth_password_wrapper.setVisibility(View.VISIBLE);
    stepper_auth_dc_wrapper.setVisibility(View.GONE);
  }

  private void showDc(){
    stepper_auth_password_wrapper.setVisibility(View.GONE);
    stepper_auth_dc_wrapper.setVisibility(View.VISIBLE);
  }

  @Override
  @UiThread
  public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
    if (authType != AuthType.PASSWORD){
      loadingDialog.show();
    }
    settings.setStartLoadData( true );
    settings.setFavoritesLoaded( false );
    settings.setProcessedLoaded( false );
    this.callback = callback;
  }

  @Override
  @UiThread
  public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
//    Toast.makeText(this.getContext(), "Your custom back action. Here you should cancel currently running operations", Toast.LENGTH_SHORT).show();
    setAuthTypeDc();
    callback.goToPrevStep();
  }

  private void setAuthType( AuthType type ) {
    settings.setAuthType( type );
  }

  private void setSignWithDc( Boolean signWithDc ) {
    settings.setSignedWithDc( signWithDc );
  }

  private void setAuthTypeDc() {
    setAuthType( AuthType.DS );
    setSignWithDc( true );
  }

  private void setAuthTypePassword() {
    setAuthType( AuthType.PASSWORD );
    setSignWithDc( false );
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(StepperDcCheckSuccesEvent event) throws Exception {
    Timber.tag(TAG).d("SignFileCommand success");
    initDocuments();
  }

  private void initDocuments() {
    // Если сменился логин, сохраняем / восстанавливаем состояние документов, общих для этих двух пользователей (как в режиме замещения)
    if ( oldLogin != null && !Objects.equals( oldLogin, "" ) && !Objects.equals( oldLogin, settings.getLogin() ) ) {
      new DocumentStateSaver().saveRestoreDocumentStates( settings.getLogin(), oldLogin, TAG );
    }

    store.clearAndLoadFromDb();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(StepperDcCheckFailEvent event) throws Exception {
    Timber.tag(TAG).d("SignFileCommand fail");

    if (event.error != null) {
      Toast.makeText( getContext(), event.error, Toast.LENGTH_SHORT ).show();
    }

    loadingDialog.dismiss();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(StepperLoginCheckSuccessEvent event) throws Exception {
    Timber.tag(TAG).d("login success");
    initDocuments();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(StepperLoginCheckFailEvent event) throws Exception {
    Timber.tag(TAG).d("login fail");

    if (event.error != null) {
      Toast.makeText( getContext(), event.error, Toast.LENGTH_SHORT ).show();
    }

    loadingDialog.dismiss();
  }

  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
  public void onMessageEvent(LoadedFromDbEvent event) {
    EventBus.getDefault().removeStickyEvent(event);

    if ( startAuthorization ) {
      startAuthorization = false;
      Timber.tag("LoadFromDb").i("StepperAuthFragment: handle LoadedFromDbEvent");

      if (callback != null) {
        loadingDialog.dismiss();
        callback.goToNextStep();
      }
    }
  }
}