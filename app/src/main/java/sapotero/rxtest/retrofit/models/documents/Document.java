package sapotero.rxtest.retrofit.models.documents;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Document implements Serializable {

  @SerializedName("uid")
  @Expose
  private String uid;
  @SerializedName("md5")
  @Expose
  private String md5;
  @SerializedName("sort_key")
  @Expose
  private Integer sortKey = -1;

  @SerializedName("title")
  @Expose
  private String title;

  @SerializedName("registration_number")
  @Expose
  private String registrationNumber;

  @SerializedName("registration_date")
  @Expose
  private String registrationDate;

  @SerializedName("urgency")
  @Expose
  private String urgency;

  @SerializedName("short_description")
  @Expose
  private String shortDescription;

  @SerializedName("comment")
  @Expose
  private String comment;

  @SerializedName("external_document_number")
  @Expose
  private String externalDocumentNumber;

  @SerializedName("receipt_date")
  @Expose
  private String receiptDate;

  @SerializedName("signer")
  @Expose
  private Signer signer;

  @SerializedName("viewed")
  @Expose
  private Boolean viewed = false;

  @SerializedName("control")
  @Expose
  private Boolean control = false;

  @SerializedName("favorites")
  @Expose
  private Boolean favorites = false;



  // Дополнительные поля, которые не будут сериализованы
  private transient boolean red = false;
  private transient boolean fromFavoritesFolder = false;
  private transient boolean fromProcessedFolder = false;
  private transient boolean processed = false;
  private transient boolean isProject = false;
  private transient boolean returned = false;
  private transient boolean rejected = false;
  private transient boolean again = false;
  private transient String firstLink = "";
  private transient String addressedToType = "";
  private transient boolean fromLinks = false;

  public Boolean changed;

  public boolean isProject() {
    return isProject;
  }

  public Document setProject(boolean project) {
    isProject = project;
    return this;
  }

  public String getFirstLink() {
    return firstLink;
  }

  public boolean isProcessed() {
    return processed;
  }

  public void setProcessed(boolean processed) {
    this.processed = processed;
  }

  public void setFirstLink(String firstLink) {
    this.firstLink = firstLink;
  }

  public Boolean getControl() {
      return control;
  }

  public void setControl(Boolean control) {
      this.control = control;
  }
  public Boolean getFavorites() {
      return favorites;
  }

  public void setFavorites(Boolean favorites) {
      this.favorites = favorites;
  }

  public String getOrganization() {
      return organization;
  }

  public void setOrganization(String organization) {
      this.organization = organization;
  }


  public String organization;

  public Boolean getChanged() {
      return changed;
  }

  public void setChanged(Boolean changed) {
      this.changed = changed;
  }



  public String statusCode;

  public String getStatusCode() {
      return statusCode;
  }
  public void setStatusCode(String statusCode) {
      this.statusCode = statusCode;

  }

  /**
   *
   * @return
   *     The uid
   */
  public String getUid() {
      return uid;
  }

  /**
   *
   * @param uid
   *     The uid
   */
  public void setUid(String uid) {
      this.uid = uid;
  }

  /**
   *
   * @return
   *     The md5
   */
  public String getMd5() {
      return md5;
  }

  /**
   *
   * @param md5
   *     The md5
   */
  public void setMd5(String md5) {
      this.md5 = md5;
  }

  /**
   *
   * @return
   *     The sortKey
   */
  public Integer getSortKey() {
      return sortKey;
  }

  /**
   *
   * @param sortKey
   *     The sort_key
   */
  public void setSortKey(Integer sortKey) {
      this.sortKey = sortKey;
  }

  /**
   *
   * @return
   *     The position
   */
  public String getTitle() {
      return title;
  }

  /**
   *
   * @param title
   *     The position
   */
  public void setTitle(String title) {
      this.title = title;
  }

  /**
   *
   * @return
   *     The registrationNumber
   */
  public String getRegistrationNumber() {
      return registrationNumber;
  }

  /**
   *
   * @param registrationNumber
   *     The registration_number
   */
  public void setRegistrationNumber(String registrationNumber) {
      this.registrationNumber = registrationNumber;
  }

  /**
   *
   * @return
   *     The registrationDate
   */
  public String getRegistrationDate() {
      return registrationDate;
  }

  /**
   *
   * @param registrationDate
   *     The registration_date
   */
  public void setRegistrationDate(String registrationDate) {
      this.registrationDate = registrationDate;
  }

  /**
   *
   * @return
   *     The urgency
   */
  public String getUrgency() {
      return urgency;
  }

  /**
   *
   * @param urgency
   *     The urgency
   */
  public void setUrgency(String urgency) {
      this.urgency = urgency;
  }

  /**
   *
   * @return
   *     The shortDescription
   */
  public String getShortDescription() {
      return shortDescription;
  }

  /**
   *
   * @param shortDescription
   *     The short_description
   */
  public void setShortDescription(String shortDescription) {
      this.shortDescription = shortDescription;
  }

  /**
   *
   * @return
   *     The comment
   */
  public String getComment() {
      return comment;
  }

  /**
   *
   * @param comment
   *     The comment
   */
  public void setComment(String comment) {
      this.comment = comment;
  }

  /**
   *
   * @return
   *     The externalDocumentNumber
   */
  public String getExternalDocumentNumber() {
      return externalDocumentNumber;
  }

  /**
   *
   * @param externalDocumentNumber
   *     The external_document_number
   */
  public void setExternalDocumentNumber(String externalDocumentNumber) {
      this.externalDocumentNumber = externalDocumentNumber;
  }

  /**
   *
   * @return
   *     The receiptDate
   */
  public String getReceiptDate() {
      return receiptDate;
  }

  /**
   *
   * @param receiptDate
   *     The receipt_date
   */
  public void setReceiptDate(String receiptDate) {
      this.receiptDate = receiptDate;
  }

  /**
   *
   * @return
   *     The signer
   */
  public Signer getSigner() {
      return signer;
  }

  /**
   *
   * @param signer
   *     The signer
   */
  public void setSigner(Signer signer) {
      this.signer = signer;
  }

  /**
   *
   * @return
   *     The viewed
   */
  public Boolean getViewed() {
      return viewed;
  }

  /**
   *
   * @param viewed
   *     The viewed
   */
  public void setViewed(Boolean viewed) {
      this.viewed = viewed;
  }

  public void setRed(boolean red) {
      this.red = red;
  }

  public boolean isRed() {
      return red;
  }


  public void setFromFavoritesFolder(boolean fromFavoritesFolder) {
      this.fromFavoritesFolder = fromFavoritesFolder;
  }

  public void setFromProcessedFolder(boolean fromProcessedFolder) {
      this.fromProcessedFolder = fromProcessedFolder;
  }

  public boolean isFromFavoritesFolder() {
      return fromFavoritesFolder;
  }

  public boolean isFromProcessedFolder() {
      return fromProcessedFolder;
  }

  public boolean isReturned() {
    return returned;
  }

  public void setReturned(boolean returned) {
    this.returned = returned;
  }

  public boolean isRejected() {
    return rejected;
  }

  public void setRejected(boolean rejected) {
    this.rejected = rejected;
  }

  public boolean isAgain() {
    return again;
  }

  public void setAgain(boolean again) {
    this.again = again;
  }

  public String getAddressedToType() {
    return addressedToType;
  }

  public void setAddressedToType(String addressedToType) {
    this.addressedToType = addressedToType;
  }

  public boolean isFromLinks() {
    return fromLinks;
  }

  public void setFromLinks(boolean fromLinks) {
    this.fromLinks = fromLinks;
  }
}
