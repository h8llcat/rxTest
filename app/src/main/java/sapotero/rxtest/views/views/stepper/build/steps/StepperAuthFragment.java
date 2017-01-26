package sapotero.rxtest.views.views.stepper.build.steps;

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
import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import javax.inject.Inject;

import rx.Subscription;
import sapotero.rxtest.R;
import sapotero.rxtest.application.EsdApplication;
import sapotero.rxtest.application.config.Constant;
import sapotero.rxtest.events.stepper.auth.StepperDcCheckEvent;
import sapotero.rxtest.events.stepper.auth.StepperDcCheckFailEvent;
import sapotero.rxtest.events.stepper.auth.StepperDcCheckSuccesEvent;
import sapotero.rxtest.events.stepper.auth.StepperLoginCheckEvent;
import sapotero.rxtest.events.stepper.auth.StepperLoginCheckFailEvent;
import sapotero.rxtest.events.stepper.auth.StepperLoginCheckSuccessEvent;
import sapotero.rxtest.views.views.stepper.BlockingStep;
import sapotero.rxtest.views.views.stepper.StepperLayout;
import sapotero.rxtest.views.views.stepper.VerificationError;
import sapotero.rxtest.views.views.stepper.util.AuthType;
import timber.log.Timber;

public class StepperAuthFragment extends Fragment implements BlockingStep {

  @Inject RxSharedPreferences settings;

  final String TAG = this.getClass().getSimpleName();

  private MaterialDialog loadingDialog;

  private FrameLayout stepper_auth_password_wrapper;
  private FrameLayout stepper_auth_dc_wrapper;

  private Subscription auth_type_subscription;

  private AuthType authType = AuthType.PASSWORD;

  private VerificationError error = new VerificationError("error");

  private StepperLayout.OnNextClickedCallback callback;
  private Preference<String> host;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    EsdApplication.getComponent( getContext() ).inject(this);

    View view = inflater.inflate(R.layout.stepper_auth, container, false);

    stepper_auth_password_wrapper = (FrameLayout) view.findViewById(R.id.stepper_auth_password_wrapper);
    stepper_auth_dc_wrapper       = (FrameLayout) view.findViewById(R.id.stepper_auth_dc_wrapper);

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
    setHostDefault();
  }

  private void setHostDefault() {
    EditText host  = (EditText) stepper_auth_password_wrapper.findViewById(R.id.stepper_auth_host);
    if ( Objects.equals(host.getText().toString(), "") ){
      host.setText( settings.getString("settings_username_host").get() );
    }

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
    Preference<AuthType> auth_type = settings.getEnum("stepper.auth_type", AuthType.class);

    host = settings.getString("settings_username_host");
    if (host.get() == null){
      host.set(Constant.HOST);
    }

    if (auth_type.get() == null) {
      auth_type.set( authType );
    }

    auth_type_subscription = auth_type.asObservable().subscribe(type -> {
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
    });
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
        EditText password = (EditText) stepper_auth_dc_wrapper.findViewById(R.id.stepper_auth_dc_password);
        EventBus.getDefault().post( new StepperDcCheckEvent( password.getText().toString() ) );
        break;
      case PASSWORD:
        EditText login = (EditText) stepper_auth_password_wrapper.findViewById(R.id.stepper_auth_username);
        EditText pwd   = (EditText) stepper_auth_password_wrapper.findViewById(R.id.stepper_auth_password);
        EditText host  = (EditText) stepper_auth_password_wrapper.findViewById(R.id.stepper_auth_host);

        Timber.e( "HOST: %s %s", host.getText(), Objects.equals(host.getText().toString(), "") );

        EventBus.getDefault().post(
          new StepperLoginCheckEvent(
            login.getText().toString(),
            pwd.getText().toString(),
            host.getText().toString()
          )
        );
        break;
    }
    error = null;

    return error;
  }

  @Override
  public void onSelected() {
  }

  @Override
  public void onError(@NonNull VerificationError error) {
    Toast.makeText( getContext(), "Errror", Toast.LENGTH_SHORT ).show();
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
    loadingDialog.show();
    this.callback = callback;
  }

  @Override
  @UiThread
  public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
    Toast.makeText(this.getContext(), "Your custom back action. Here you should cancel currently running operations", Toast.LENGTH_SHORT).show();
    callback.goToPrevStep();
  }


  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(StepperDcCheckSuccesEvent event) throws Exception {
    Timber.tag(TAG).d("Sign success");
    if (callback != null) {
      loadingDialog.hide();
      callback.goToNextStep();
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(StepperDcCheckFailEvent event) throws Exception {
    Timber.tag(TAG).d("Sign fail");

    if (event.error != null) {
      Toast.makeText( getContext(), event.error, Toast.LENGTH_SHORT ).show();
    }

    loadingDialog.hide();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(StepperLoginCheckSuccessEvent event) throws Exception {
    Timber.tag(TAG).d("login success");
    if (callback != null) {
      loadingDialog.hide();
      callback.goToNextStep();
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(StepperLoginCheckFailEvent event) throws Exception {
    Timber.tag(TAG).d("login fail");

    if (event.error != null) {
      Toast.makeText( getContext(), event.error, Toast.LENGTH_SHORT ).show();
    }

    loadingDialog.hide();
  }

}