package com.abdullah_alsaad.JavaClass;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = BookItem.TABLE_BOOKITEM)
public class BookItem implements Serializable {

    public static final String TABLE_BOOKITEM = "bookTable";
    public static final String BOOKITEM_ID = "id";
    public static final String BOOKITEM_STRING_ID = "string_id"; //needed from firebase
    public static final String BOOKITEM_NAME = "name"; //needed from firebase
    public static final String BOOKITEM_PDF_URL = "pdfURL"; //needed from firebase
    public static final String BOOKITEM_PDF_DOWNLOAD_PATH = "pdfDownloadPath";
    public static final String BOOKITEM_CREATED_TIME = "createdTime"; //needed from firebase
    public static final String BOOKITEM_LARGE_IMAGE = "largeImage";
    public static final String BOOKITEM_IMAGE_URL = "imageURL"; //needed from firebase
    public static final String COMMENTABOOKMARK_COMMENT_AND_BOOKMARK = "commentABookmarks";
    public static final String COMMENTABOOKMARK_LAST_VISIT_PAGE = "lastVisitPage";
    public static final String COMMENTABOOKMARK_BOOK_IMAGE_BYTE = "bookImageByte";
    //generatedId = true,
    @DatabaseField(id = true, canBeNull = false, columnName = BOOKITEM_ID)
    private long id;

    @DatabaseField(columnName = BOOKITEM_STRING_ID)
    private String string_id;

    @DatabaseField(columnName = BOOKITEM_NAME)
    private String name;

    @DatabaseField(columnName = BOOKITEM_PDF_URL)
    private String pdfURL;

    @DatabaseField(columnName = BOOKITEM_PDF_DOWNLOAD_PATH)
    private String pdfDownloadPath;

    @DatabaseField(columnName = BOOKITEM_CREATED_TIME, format = "yyyy-MM-dd HH:mm:ss",
            dataType = DataType.DATE_STRING)
    private Date createdTime;

    @DatabaseField(columnName = BOOKITEM_IMAGE_URL)
    private String imageURL;

    @DatabaseField(dataType = DataType.BYTE_ARRAY, columnName = BOOKITEM_LARGE_IMAGE)
    private byte[] largeImage;

    @ForeignCollectionField(columnName = COMMENTABOOKMARK_COMMENT_AND_BOOKMARK, eager = false)
    ForeignCollection<CommentABookmark> commentABookmarks;

    @DatabaseField(columnName = COMMENTABOOKMARK_LAST_VISIT_PAGE)
    private int lastVisitPage = 0;

    @DatabaseField(dataType = DataType.BYTE_ARRAY,columnName = COMMENTABOOKMARK_BOOK_IMAGE_BYTE)
    byte[] bookImageByte;

    public ForeignCollection<CommentABookmark> getCommentABookmarks() {
        return commentABookmarks;
    }

    public void setCommentABookmarks(ForeignCollection<CommentABookmark> commentABookmarks) {
        this.commentABookmarks = commentABookmarks;
    }

    public String getPdfDownloadPath() {
        return pdfDownloadPath;
    }

    public void setPdfDownloadPath(String pdfDownloadPath) {
        this.pdfDownloadPath = pdfDownloadPath;
    }

    public String getPdfURL() {
        return pdfURL;
    }

    public void setPdfURL(String pdfURL) {
        this.pdfURL = pdfURL;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStringId() {
        return string_id;
    }

    public void setStringId(String stringId) {
        this.string_id = stringId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public byte[] getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(byte[] largeImage) {
        this.largeImage = largeImage;
    }

    public int getLastVisitPage() {
        return lastVisitPage;
    }

    public void setLastVisitPage(int lastVisitPage) {
        this.lastVisitPage = lastVisitPage;
    }

    public byte[] getBookImageByte() {
        return bookImageByte;
    }

    public void setBookImageByte(byte[] bookImageByte) {
        this.bookImageByte = bookImageByte;
    }
}

