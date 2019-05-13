package com.abdullah_alsaad.OrmLiteDatabseHelper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.abdullah_alsaad.JavaClass.BookItem;
import com.abdullah_alsaad.JavaClass.CommentABookmark;
import com.abdullah_alsaad.JavaClass.Topic;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "abdullah-alsaad.sqlite";
    private static final int DATABASE_VERSION = 1;
    private Dao<BookItem, Integer> bookItemDao = null;
    private Dao<CommentABookmark, Integer> commentABookmark = null;
    private Dao<Topic, Integer> topic = null;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource,BookItem.class);
            TableUtils.createTableIfNotExists(connectionSource,CommentABookmark.class);
            TableUtils.createTableIfNotExists(connectionSource,Topic.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            List<String> allSql = new ArrayList<String>();
            switch(oldVersion)
            {
                case 2:
//                    allSql.add("IF COL_LENGTH('bookTable', 'stringId') IS NULL\n" +
//                            "BEGIN\n" +
//                            "    ALTER TABLE `bookTable` ADD COLUMN stringId STRING;\n" +
//                            "END");
                    //allSql.add("altere AdData add column `new_col` VARCHAR");
                    //allSql.add("altere AdData add column `new_col2` VARCHAR");
            }
            for (String sql : allSql) {
                db.execSQL(sql);
            }
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "exception during onUpgrade", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<BookItem, Integer> getBookItemDao() {
        if (null == bookItemDao) {
            try {
                bookItemDao = getDao(BookItem.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return bookItemDao;
    }
    public Dao<CommentABookmark, Integer> getCommentABookmarkDao() {
        if (null == commentABookmark) {
            try {
                commentABookmark = getDao(CommentABookmark.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return commentABookmark;
    }
    public Dao<Topic, Integer> getTopicDao() {
        if (null == topic) {
            try {
                topic = getDao(Topic.class);
            }catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return topic;
    }
}
