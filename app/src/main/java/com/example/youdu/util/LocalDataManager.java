package com.example.youdu.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.youdu.bean.BookInfo;
import com.example.youdu.bean.UserInfo;
import com.example.youdu.bean.VolumeInfo;
import com.example.youdu.bean.db.Book;
import com.example.youdu.bean.db.Chapter;
import com.example.youdu.bean.db.Volume;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class LocalDataManager {
    private static final String UID = "uid";
    private static final String PASSWORD_MD5 = "password-md5";
    private static final String USER_NAME = "name";
    private static final String AVATAR_URL = "avatar-url";
    private static LocalDataManager localDataManager;
    private Context context;
    private SharedPreferences prefs;
    private UserInfo userInfo;
    private List<Book> bookCollection;

    private LocalDataManager(Context context) {
        this.context = context.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public static LocalDataManager getInstance(Context context) {
        if (localDataManager == null) {
            synchronized (LocalDataManager.class) {
                if (localDataManager == null) {
                    localDataManager = new LocalDataManager(context);
                }
            }
        }
        return localDataManager;
    }

    public UserInfo getUserInfo() {
        if (userInfo == null && prefs.getInt(UID, -1) >= 0) {
            userInfo = new UserInfo();
            userInfo.setUid(prefs.getInt(UID, -1));
            userInfo.setPasswordMd5(prefs.getString(PASSWORD_MD5, null));
            userInfo.setName(prefs.getString(USER_NAME, null));
            userInfo.setAvatarPath(prefs.getString(AVATAR_URL, null));
        }
        return userInfo;
    }

    public void saveUser(UserInfo userInfo) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(UID, userInfo.getUid());
        editor.putString(PASSWORD_MD5, userInfo.getPasswordMd5());
        editor.putString(USER_NAME, userInfo.getName());
        editor.putString(AVATAR_URL, userInfo.getAvatarPath());
        editor.apply();
        this.userInfo = userInfo;
    }

    public List<Book> getBookCollection() {
        if (bookCollection == null) {
            bookCollection = LitePal.where("userId = ?", "" + getUserInfo().getUid())
                    .order("lastTime desc").find(Book.class);
        }
        return bookCollection;
    }

    private Book handleBook(BookInfo bookInfo, boolean isCollection) {
        Book book = LitePal.where("code = ?", "" + bookInfo.id).findFirst(Book.class);
        if (book == null) {
            book = new Book();
        }
        book.setCode(bookInfo.id);
        book.setTitle(bookInfo.tile);
        book.setAuthorId(bookInfo.authorId);
        book.setIntroduction(bookInfo.introduction);
        book.setCoverPath(bookInfo.coverPath);
        book.setPublishTime(bookInfo.publishTime);
        book.setReadingChapterId(bookInfo.readingChapterId);
        book.setLastTime(bookInfo.lastTime);
        book.setUserId(LocalDataManager.getInstance(context).getUserInfo().getUid());
        if (isCollection) book.save();
        return book;
    }

    public List<Book> handleBookList(List<BookInfo> books, boolean isCollection) {
        List<Book> list = new ArrayList<>();
        for (BookInfo bookInfo : books) {
            Book book = handleBook(bookInfo, isCollection);
            list.add(book);
        }
        if (isCollection) bookCollection = getBookCollection();
        return list;
    }

    public void addBook(Book book) {
        if (book == null || bookCollection.contains(book)) return;
        book.save();
        bookCollection.add(0, book);
    }

    public void removeBook(Book book) {
        bookCollection.remove(book);
        if (book.isSaved()) {
            book.delete();
        }
    }

    public List<Volume> getCatalog(Book book) {
        List<Volume> volumes = LitePal.where("book_id = ?", "" + book.getId())
                .order("number").find(Volume.class);
        for (Volume volume : volumes) {
            List<Chapter> chapters = LitePal.where("volume_id = ?", "" + volume.getId())
                    .order("number").find(Chapter.class);
            volume.setChapters(chapters);
        }
        return volumes;
    }

    private Chapter handleChapter(Volume volume, VolumeInfo.ChapterInfo chapterInfo, boolean shouldSave) {
        Chapter chapter = LitePal.where("code = ?", "" + chapterInfo.id).findFirst(Chapter.class);
        if (chapter == null) {
            chapter = new Chapter();
        }
        chapter.setCode(chapterInfo.id);
        chapter.setNumber(chapterInfo.number);
        chapter.setTitle(chapterInfo.tile);
        chapter.setContentPath(chapterInfo.contentPath);
        chapter.setUpdateTime(chapterInfo.updateTime);
        chapter.setVolume(volume);
        if (shouldSave) chapter.save();
        return chapter;
    }

    private Volume handleVolume(Book book, VolumeInfo volumeInfo, boolean shouldSave) {
        Volume volume = LitePal.where("code = ?", "" + volumeInfo.id).findFirst(Volume.class);
        if (volume == null) {
            volume = new Volume();
        }
        volume.setCode(volumeInfo.id);
        volume.setTitle(volumeInfo.title);
        volume.setCoverPath(volumeInfo.coverPath);
        volume.setBook(book);
        if (shouldSave) volume.save();
        List<Chapter> chapters = new ArrayList<>();
        for (VolumeInfo.ChapterInfo chapterInfo : volumeInfo.chapters) {
            chapters.add(handleChapter(volume, chapterInfo, shouldSave));
        }
        volume.setChapters(chapters);
        return volume;
    }

    public void handleCatalog(Book book, List<VolumeInfo> catalog) {
        boolean shouldSave = bookCollection.contains(book);
        List<Volume> volumes = new ArrayList<>();
        for (VolumeInfo volumeInfo : catalog) {
            volumes.add(handleVolume(book, volumeInfo, shouldSave));
        }
        book.setVolumes(volumes);
    }

    public String getChapterContent(int chapterCode) {
        if (chapterCode == 0) return null;
        Chapter chapter = LitePal.where("code = ?", "" + chapterCode).findFirst(Chapter.class);
        return chapter.getContent();
    }

    public boolean saveNovelContent(int chapterCode, String content) {
        Chapter chapter = LitePal.where("code = ?", "" + chapterCode).findFirst(Chapter.class);
        if (chapter == null) return false;
        chapter.setContent(content);
        return true;
    }
}
