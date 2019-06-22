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
    private UserInfo user;
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

    public UserInfo getUser() {
        if (user == null && prefs.getInt(UID, -1) >= 0) {
            user = new UserInfo();
            user.setUid(prefs.getInt(UID, -1));
            user.setPasswordMd5(prefs.getString(PASSWORD_MD5, null));
            user.setName(prefs.getString(USER_NAME, null));
            user.setAvatarPath(prefs.getString(AVATAR_URL, null));
        }
        return user;
    }

    public void saveUser(UserInfo user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(UID, user.getUid());
        editor.putString(PASSWORD_MD5, user.getPasswordMd5());
        editor.putString(USER_NAME, user.getName());
        editor.putString(AVATAR_URL, user.getAvatarPath());
        editor.apply();
        this.user = user;
    }

    public List<Book> getBookCollection() {
        if (bookCollection == null) {
            bookCollection = LitePal.where("userId = ?", "" + getUser().getUid())
                    .order("lastTime desc").find(Book.class);
        }
        return bookCollection;
    }

    private Book saveBook(BookInfo bookInfo) {
        boolean flag = false;
        Book book = LitePal.where("code = ?", "" + bookInfo.id).findFirst(Book.class);
        if (book == null) {
            book = new Book();
            flag = true;
        }
        book.setCode(bookInfo.id);
        book.setTitle(bookInfo.tile);
        book.setAuthorId(bookInfo.authorId);
        book.setIntroduction(bookInfo.introduction);
        book.setCoverPath(bookInfo.coverPath);
        book.setPublishTime(bookInfo.publishTime);
        book.setReadingChapterId(bookInfo.readingChapterId);
        book.setLastTime(bookInfo.lastTime);
        book.setUserId(LocalDataManager.getInstance(context).getUser().getUid());
        book.save();
        if (flag) return book;
        else return null;
    }

    public void saveBookCollection(List<BookInfo> books) {
        for (BookInfo bookInfo : books) {
            saveBook(bookInfo);
        }
        bookCollection = getBookCollection();
    }

    public void addBook(Book book) {
        if (book == null || bookCollection.contains(book)) return;
        book.save();
        bookCollection.add(0, book);
    }

    public void removeBook(Book book) {
        bookCollection.remove(book);
        LitePal.delete(Book.class, book.getId());
    }

    public List<Volume> getCatalog(Book book) {
        return LitePal.where("book_id = ?", "" + book.getId()).find(Volume.class, true);
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

    public void handleCatalog(Book book, List<VolumeInfo> catalog, boolean shouldSave) {
        List<Volume> volumes = new ArrayList<>();
        for (VolumeInfo volumeInfo : catalog) {
            volumes.add(handleVolume(book, volumeInfo, shouldSave));
        }
        book.setVolumes(volumes);
    }
}
