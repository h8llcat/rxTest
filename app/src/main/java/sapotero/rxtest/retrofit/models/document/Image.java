
package sapotero.rxtest.retrofit.models.document;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Image implements Serializable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("md5")
    @Expose
    private String md5;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("content_type")
    @Expose
    private String contentType;
    @SerializedName("signed")
    @Expose
    private Boolean signed;

    private transient boolean deleted = false;
    private transient String fileName = "";
    private transient boolean noFreeSpace = false;
    private transient String imageId;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
     *     The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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
     *     The size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * 
     * @param size
     *     The size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * 
     * @return
     *     The path
     */
    public String getPath() {
        return path;
    }

    /**
     * 
     * @param path
     *     The path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 
     * @return
     *     The contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * 
     * @param contentType
     *     The content_type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * 
     * @return
     *     The signed
     */
    public Boolean getSigned() {
        return signed;
    }

    /**
     * 
     * @param signed
     *     The signed
     */
    public void setSigned(Boolean signed) {
        this.signed = signed;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isNoFreeSpace() {
        return noFreeSpace;
    }

    public void setNoFreeSpace(boolean noFreeSpace) {
        this.noFreeSpace = noFreeSpace;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String id) {
        this.imageId = id;
    }
}
