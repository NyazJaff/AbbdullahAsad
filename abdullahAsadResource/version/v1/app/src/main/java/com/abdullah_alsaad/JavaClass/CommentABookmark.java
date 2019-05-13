package com.abdullah_alsaad.JavaClass;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = CommentABookmark.TABLE_COMMENTABOOKMARK)
public class CommentABookmark implements Serializable{

    public static final String TABLE_COMMENTABOOKMARK = "commentABookmark";
    public static final String COMMENTABOOKMARK_ID = "id";
    public static final String COMMENTABOOKMARK_COMMENT = "comment";
    public static final String COMMENTABOOKMARK_TYPE = "type";
    public static final String COMMENTABOOKMARK_PAGE = "pageNumber";
    public static final String COMMENTABOOKMARK_BOOK_ID = "bookItem";
    public static final String COMMENTABOOKMARK_CREATED_DATE = "createdDate";

    public static final String BOOKMARK = "bookmark";
    public static final String COMMENT = "comment";

    @DatabaseField(generatedId = true,columnName = COMMENTABOOKMARK_ID)
    private long id;
    @DatabaseField(foreign = true, canBeNull = false, columnName = COMMENTABOOKMARK_BOOK_ID, foreignAutoRefresh = true)
    private BookItem bookItem;

    @DatabaseField(columnName = COMMENTABOOKMARK_COMMENT)
    private String comment;
    @DatabaseField(columnName = COMMENTABOOKMARK_TYPE)
    private String type;
    @DatabaseField(columnName = COMMENTABOOKMARK_PAGE)
    private int pageNumber;
    @DatabaseField(columnName = COMMENTABOOKMARK_CREATED_DATE, format="yyyy-MM-dd HH:mm:ss",
            dataType= DataType.DATE_STRING)
    private Date createdTime;

    public CommentABookmark(){}
    public CommentABookmark (String comment,int pageNumber,BookItem bookItem){
        this.comment = comment;
        this.pageNumber = pageNumber;
        this.bookItem = bookItem;
        this.type = COMMENT;
    }
    public CommentABookmark (int pageNumber,BookItem bookItem){
        this.pageNumber = pageNumber;
        this.bookItem = bookItem;
        this.type = BOOKMARK;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getPageNumber() {
        return pageNumber;
    }
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    public BookItem getBookItem() {
        return bookItem;
    }
    public void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
    }
    public Date getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
