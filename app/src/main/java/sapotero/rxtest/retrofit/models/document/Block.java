
package sapotero.rxtest.retrofit.models.document;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Block implements Serializable{

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("appeal_text")
    @Expose
    private String appealText = "";
    @SerializedName("text_before")
    @Expose
    private Boolean textBefore;
    @SerializedName("hide_performers")
    @Expose
    private Boolean hidePerformers;
    @SerializedName("to_copy")
    @Expose
    private Boolean toCopy;
    @SerializedName("to_familiarization")
    @Expose
    private Boolean toFamiliarization;
    @SerializedName("performers")
    @Expose
    private List<Performer> performers = new ArrayList<Performer>();


    @SerializedName("ask_to_report")
    @Expose
    private Boolean askToReport  = false ;

    @SerializedName("ask_to_acquaint")
    @Expose
    private Boolean askToAcquaint = false;

    @SerializedName("indentation")
    @Expose
    private String indentation = "5";

    @SerializedName("font_size")
    @Expose
    private String fontSize;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public Boolean getAskToReport() {
        return askToReport;
    }

    public void setAskToReport(Boolean askToReport) {
        this.askToReport = askToReport;
    }

    public Boolean getAskToAcquaint() {
        return askToAcquaint;
    }

    public void setAskToAcquaint(Boolean askToAcquaint) {
        this.askToAcquaint = askToAcquaint;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getIndentation() {
        return indentation;
    }

    public void setIndentation(String indentation) {
        this.indentation = indentation;
    }

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
     *     The text
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @param text
     *     The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 
     * @return
     *     The appealText
     */
    public String getAppealText() {
        return appealText;
    }

    /**
     * 
     * @param appealText
     *     The appeal_text
     */
    public void setAppealText(String appealText) {
        this.appealText = appealText;
    }

    /**
     * 
     * @return
     *     The textBefore
     */
    public Boolean getTextBefore() {
        return textBefore;
    }

    /**
     * 
     * @param textBefore
     *     The text_before
     */
    public void setTextBefore(Boolean textBefore) {
        this.textBefore = textBefore;
    }

    /**
     * 
     * @return
     *     The hidePerformers
     */
    public Boolean getHidePerformers() {
        return hidePerformers;
    }

    /**
     * 
     * @param hidePerformers
     *     The hide_performers
     */
    public void setHidePerformers(Boolean hidePerformers) {
        this.hidePerformers = hidePerformers;
    }

    /**
     * 
     * @return
     *     The toCopy
     */
    public Boolean getToCopy() {
        return toCopy;
    }

    /**
     * 
     * @param toCopy
     *     The to_copy
     */
    public void setToCopy(Boolean toCopy) {
        this.toCopy = toCopy;
    }

    /**
     * 
     * @return
     *     The toFamiliarization
     */
    public Boolean getToFamiliarization() {
        return toFamiliarization;
    }

    /**
     * 
     * @param toFamiliarization
     *     The to_familiarization
     */
    public void setToFamiliarization(Boolean toFamiliarization) {
        this.toFamiliarization = toFamiliarization;
    }

    /**
     * 
     * @return
     *     The performers
     */
    public List<Performer> getPerformers() {
        return performers;
    }

    /**
     * 
     * @param performers
     *     The performers
     */
    public void setPerformers(List<Performer> performers) {
        this.performers = performers;
    }
}
