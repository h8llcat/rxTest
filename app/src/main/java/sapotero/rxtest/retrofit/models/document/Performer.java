
package sapotero.rxtest.retrofit.models.document;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Performer {

    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("performer_id")
    @Expose
    private String performerId;
    @SerializedName("performer_type")
    @Expose
    private String performerType;
    @SerializedName("performer_text")
    @Expose
    private String performerText;
    @SerializedName("organization_text")
    @Expose
    private String organizationText;
    @SerializedName("is_original")
    @Expose
    private Boolean isOriginal;
    @SerializedName("is_responsible")
    @Expose
    private Boolean isResponsible;

    /**
     * 
     * @return
     *     The number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * 
     * @param number
     *     The number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * 
     * @return
     *     The performerId
     */
    public String getPerformerId() {
        return performerId;
    }

    /**
     * 
     * @param performerId
     *     The performer_id
     */
    public void setPerformerId(String performerId) {
        this.performerId = performerId;
    }

    /**
     * 
     * @return
     *     The performerType
     */
    public String getPerformerType() {
        return performerType;
    }

    /**
     * 
     * @param performerType
     *     The performer_type
     */
    public void setPerformerType(String performerType) {
        this.performerType = performerType;
    }

    /**
     * 
     * @return
     *     The performerText
     */
    public String getPerformerText() {
        return performerText;
    }

    /**
     * 
     * @param performerText
     *     The performer_text
     */
    public void setPerformerText(String performerText) {
        this.performerText = performerText;
    }

    /**
     * 
     * @return
     *     The organizationText
     */
    public String getOrganizationText() {
        return organizationText;
    }

    /**
     * 
     * @param organizationText
     *     The organization_text
     */
    public void setOrganizationText(String organizationText) {
        this.organizationText = organizationText;
    }

    /**
     * 
     * @return
     *     The isOriginal
     */
    public Boolean getIsOriginal() {
        return isOriginal;
    }

    /**
     * 
     * @param isOriginal
     *     The is_original
     */
    public void setIsOriginal(Boolean isOriginal) {
        this.isOriginal = isOriginal;
    }

    /**
     * 
     * @return
     *     The isResponsible
     */
    public Boolean getIsResponsible() {
        return isResponsible;
    }

    /**
     * 
     * @param isResponsible
     *     The is_responsible
     */
    public void setIsResponsible(Boolean isResponsible) {
        this.isResponsible = isResponsible;
    }

}