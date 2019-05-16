package com.abdullah_alsaad.OrmLiteDatabseHelper;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.abdullah_alsaad.JavaClass.BookItem;
import com.abdullah_alsaad.JavaClass.CommentABookmark;
import com.abdullah_alsaad.JavaClass.QuestionAndAnswer;
import com.abdullah_alsaad.JavaClass.Topic;
import com.abdullah_alsaad.generic.AppUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbPer {
    Context context;

    public DbPer(Context context) {
        this.context = context;
    }

    public static void saveBookItemToLocalDatabase(Context context, BookItem bookItem) {
        if (context == null) {
            return;
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        helper.getWritableDatabase();
        try {
            Dao<BookItem, Integer> lendDbDao = helper.getBookItemDao();
            lendDbDao.createOrUpdate(bookItem);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }
    }

    public static void deleteBookItemToLocalDatabase(Context context, int id) {
        if (context == null) {
            return;
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        helper.getWritableDatabase();
        try {
            Dao<BookItem, Integer> lendDbDao = helper.getBookItemDao();
            lendDbDao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }
    }

    public static boolean saveCommentABookmarkToLocalDatabase(Context context, CommentABookmark commentABookmark) {
        if (context == null) {
            return false;
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        helper.getWritableDatabase();
        try {
            Dao<CommentABookmark, Integer> commentABookmarkDao = helper.getCommentABookmarkDao();
            commentABookmarkDao.createOrUpdate(commentABookmark);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }
        return false;
    }

    public static boolean deleteCommentBookmark(Context context, long id) {
        if (context == null) {
            return false;
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        Dao<CommentABookmark, Integer> commentABookmarkDbDao = helper.getCommentABookmarkDao();
        try {
            commentABookmarkDbDao.deleteById((int) id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<CommentABookmark> getAllCommentAndBookmark(Context context, String type, long id) {
        if (context == null) {
            return null;
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        Dao<CommentABookmark, Integer> commentABookmarkDbDao = helper.getCommentABookmarkDao();
        try {
            QueryBuilder<CommentABookmark, Integer> builder = commentABookmarkDbDao.queryBuilder();
            builder.where().eq(CommentABookmark.COMMENTABOOKMARK_TYPE, type).and().eq(CommentABookmark.COMMENTABOOKMARK_BOOK_ID, (int) id);
            PreparedQuery<CommentABookmark> preparedQuery = builder.prepare();
            List<CommentABookmark> allCommentAndBookmark = commentABookmarkDbDao.query(preparedQuery);
            return allCommentAndBookmark;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<BookItem> getAllBookItems(Context context) {
        if (context == null) {
            return null;
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        Dao<BookItem, Integer> bookItemDbDao = helper.getBookItemDao();
        try {
            QueryBuilder<BookItem, Integer> builder = bookItemDbDao.queryBuilder();
            builder.orderBy("id", true);
            return bookItemDbDao.query(builder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Integer getBookItemLastItemInserted(Context context) {
        if (context == null) {
            return null;
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        Dao<BookItem, Integer> bookItemDbDao = helper.getBookItemDao();
        try {
            QueryBuilder<BookItem, Integer> queryBuilder = bookItemDbDao.queryBuilder();
            queryBuilder.selectRaw("MAX(`id`)");
            String[] values = bookItemDbDao.queryRaw(queryBuilder.prepareStatementString()).getFirstResult();
//            AppUtil.showToast(context,values[0]);
            if (values.length > 0) {
                if (values[0] != null) {
                    return Integer.parseInt(values[0]);
                }
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getTopicItemLastItemInserted(Context context) {
        if (context == null) {
            return null;
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        Dao<Topic, Integer> topicDbDao = helper.getTopicDao();
        try {
            QueryBuilder<Topic, Integer> queryBuilder = topicDbDao.queryBuilder();
            queryBuilder.selectRaw("MAX(`id`)");
            String[] values = topicDbDao.queryRaw(queryBuilder.prepareStatementString()).getFirstResult();
//            AppUtil.showToast(context,values[0]);
            if (values.length > 0) {
                if (values[0] != null) {
                    return Integer.parseInt(values[0]);
                }
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveTopicLocally(Context context, Topic topic) {
        if (context != null) {
            DatabaseHelper helper = new DatabaseHelper(context);
            helper.getWritableDatabase();
            try {
                Dao<Topic, Integer> topicDbDao = helper.getTopicDao();
                topicDbDao.createOrUpdate(topic);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                helper.close();
            }
        }
    }

    public static List<Topic> getAllTopic(Context context, long parentId,String fragmentName) {
        List<Topic> allTopic = new ArrayList<>();
        if (context != null) {
            DatabaseHelper helper = new DatabaseHelper(context);
            Dao<Topic, Integer> topicDao = helper.getTopicDao();
            try {
                QueryBuilder<Topic, Integer> builder = topicDao.queryBuilder();
//                builder.where().eq(Topic.TOPIC_TYPE, type).and().eq(Topic.TOPIC_PARENT_ID, parentId);
                builder.where().eq(Topic.TOPIC_PARENT_ID, parentId).and().eq(Topic.TOPIC_FRAGMENT_NAME, fragmentName);;
                PreparedQuery<Topic> preparedQuery = builder.prepare();
                allTopic = topicDao.query(preparedQuery);
                return allTopic;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
            return allTopic;
    }

    public static String getTopicTitle(Context context, long topicId) {
        Topic topic = new Topic();
        if (context != null) {
            DatabaseHelper helper = new DatabaseHelper(context);
            Dao<Topic, Integer> topicDao = helper.getTopicDao();
            try {
                QueryBuilder<Topic, Integer> builder = topicDao.queryBuilder();
                builder.where().eq(Topic.TOPIC_ID, topicId);
                PreparedQuery<Topic> preparedQuery = builder.prepare();
                topic = topicDao.queryForFirst(preparedQuery);
                if(topic != null){
                    return topic.getName();
                }else{
                    return "";
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void saveQuestionAndAnswerLocally(Context context, QuestionAndAnswer questionAndAnswer) {
        if (context != null) {
            DatabaseHelper helper = new DatabaseHelper(context);
            helper.getWritableDatabase();
            try {
                Dao<QuestionAndAnswer, Integer> questionAndAnswerDao = helper.getQuestionAndAnswerDao();
                questionAndAnswerDao.createOrUpdate(questionAndAnswer);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                helper.close();
            }
        }
    }

    public static List<QuestionAndAnswer> getAllQuestionAndAnswer(Context context, long parentId) {
        List<QuestionAndAnswer> allQuestionAndAnswer = new ArrayList<>();
        if (context != null) {
            DatabaseHelper helper = new DatabaseHelper(context);
            Dao<QuestionAndAnswer, Integer> questionAndAnswerDao = helper.getQuestionAndAnswerDao();
            try {
                QueryBuilder<QuestionAndAnswer, Integer> builder = questionAndAnswerDao.queryBuilder();
//                builder.where().eq(Topic.TOPIC_TYPE, type).and().eq(Topic.TOPIC_PARENT_ID, parentId);
                builder.where().eq(QuestionAndAnswer.QUESTIONANDANSWER_PARENT_ID, parentId);
                PreparedQuery<QuestionAndAnswer> preparedQuery = builder.prepare();
                allQuestionAndAnswer = questionAndAnswerDao.query(preparedQuery);
                return allQuestionAndAnswer;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return allQuestionAndAnswer;
    }

    public static QuestionAndAnswer getQuestionAndAnswerTitle(FragmentActivity context, long parentId) {
        QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer();
        if (context != null) {
            DatabaseHelper helper = new DatabaseHelper(context);
            Dao<QuestionAndAnswer, Integer> questionAndAnswersDao = helper.getQuestionAndAnswerDao();
            try {
                QueryBuilder<QuestionAndAnswer, Integer> builder = questionAndAnswersDao.queryBuilder();
                builder.where().eq(QuestionAndAnswer.QUESTIONANDANSWER_ID, parentId);
                PreparedQuery<QuestionAndAnswer> preparedQuery = builder.prepare();
                questionAndAnswer = questionAndAnswersDao.queryForFirst(preparedQuery);
                if(questionAndAnswer != null){
                    return questionAndAnswer;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Integer getQuestionAndAnswerLastItemInserted(Context context) {
        if (context == null) {
            return null;
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        Dao<QuestionAndAnswer, Integer> questionAndAnswersDbDao = helper.getQuestionAndAnswerDao();
        try {
            QueryBuilder<QuestionAndAnswer, Integer> queryBuilder = questionAndAnswersDbDao.queryBuilder();
            queryBuilder.selectRaw("MAX(`id`)");
            String[] values = questionAndAnswersDbDao.queryRaw(queryBuilder.prepareStatementString()).getFirstResult();
//            AppUtil.showToast(context,values[0]);
            if (values.length > 0) {
                if (values[0] != null) {
                    return Integer.parseInt(values[0]);
                }
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insertBookImage(Context context, Long stringId, byte[] logoImagedata){
        if (context == null) {
            return;
        }
        DatabaseHelper helper = new DatabaseHelper(context);
        Dao<BookItem, Integer> bookItemDbDao = helper.getBookItemDao();
        try {
            UpdateBuilder<BookItem, Integer> updateBuilder = bookItemDbDao.updateBuilder();
            updateBuilder.where().eq(BookItem.BOOKITEM_ID,  stringId);
            updateBuilder.updateColumnValue(BookItem.COMMENTABOOKMARK_BOOK_IMAGE_BYTE, AppUtil.decodeFile(logoImagedata));
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
