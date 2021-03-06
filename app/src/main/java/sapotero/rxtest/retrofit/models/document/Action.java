package sapotero.rxtest.retrofit.models.document;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Action {

  @SerializedName("date")
  @Expose
  private String date;

  @SerializedName("status")
  @Expose
  private String status;

  @SerializedName("comment")
  @Expose
  private String comment;

  /**
   *
   * @return
   * The date
   */
  public String getDate() {
    return date;
  }

  /**
   *
   * @param date
   * The date
   */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   *
   * @return
   * The star
   */
  public String getStatus() {
    return status;
  }

  /**
   *
   * @param status
   * The star
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   *
   * @return
   * The comment
   */
  public String getComment() {
    return comment;
  }

  /**
   *
   * @param comment
   * The comment
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

}