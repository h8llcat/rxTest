package sapotero.rxtest.retrofit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthToken {

  @SerializedName("auth_token")
  @Expose
  private String authToken;

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

}