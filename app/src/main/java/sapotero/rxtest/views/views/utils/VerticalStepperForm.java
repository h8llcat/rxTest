package sapotero.rxtest.views.views.utils;


import android.view.View;
import android.widget.RelativeLayout;

public interface VerticalStepperForm {
  View createStepContentView(int stepNumber);
  void onStepOpening(int stepNumber);
  void sendData();
  void setFinalView(RelativeLayout stepContent);
}