package com.ikalangirajeev.telugubiblemessages.ui.bible.app.verses;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ikalangirajeev.telugubiblemessages.ui.bible.app.Data;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.BibleDatabase;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.EnglishBible;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.EnglishBibleDao;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.HindiBible;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.HindiBibleDao;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.KannadaBible;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.KannadaBibleDao;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.MalayalamBible;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.MalayalamBibleDao;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.TamilBible;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.TamilBibleDao;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.TeluguBible;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.TeluguBibleDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class VersesViewModel extends AndroidViewModel {

    private static final String TAG = "VersesViewModel";

    private List<Data> dataList;
    private MutableLiveData<List<Data>> mutableLiveData;
    private BibleDatabase bibleDatabase;

    public VersesViewModel(@NonNull Application application) {
        super(application);
        this.bibleDatabase = BibleDatabase.getBibleDatabase(application);
        mutableLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Data>> getData(String bibleSelected, String bookName, int bookNumber, int chapterNumber) {

        CompletableFuture<List<Data>> completableFuture = CompletableFuture.supplyAsync(() -> {
            switch (bibleSelected) {
                case "bible_english":
                    EnglishBibleDao englishBibleDao = bibleDatabase.englishBibleDao();
                    return englishBibleDao.getEnglishBibleList(bookNumber, chapterNumber)
                            .stream()
                            .map(verse -> {
                                return new Data(bookName + " " + verse.getChapter() + ":" + verse.getVersecount(), verse.getVerse(), verse.getVerseid());
                            })
                            .collect(Collectors.toList());
                case "bible_tamil":
                    TamilBibleDao tamilBibleDao = bibleDatabase.tamilBibleDao();
                    return tamilBibleDao.getTamilBibleList(bookNumber, chapterNumber)
                            .stream()
                            .map(verse -> {
                                return new Data(bookName + " " + verse.getChapter() + ":" + verse.getVersecount(), verse.getVerse(), verse.getVerseid());
                            })
                            .collect(Collectors.toList());
                case "bible_kannada":
                    KannadaBibleDao kannadaBibleDao = bibleDatabase.kannadaBibleDao();
                    return kannadaBibleDao.getKannadaBibleList(bookNumber, chapterNumber)
                            .stream()
                            .map(verse -> {
                                return new Data(bookName + " " + verse.getChapter() + ":" + verse.getVersecount(), verse.getVerse(), verse.getVerseid());
                            })
                            .collect(Collectors.toList());
                case "bible_hindi":
                    HindiBibleDao hindiBibleDao = bibleDatabase.hindiBibleDao();
                    return hindiBibleDao.getHindiBibleList(bookNumber, chapterNumber)
                            .stream()
                            .map(verse -> {
                                return new Data(bookName + " " + verse.getChapter() + ":" + verse.getVersecount(), verse.getVerse(), verse.getVerseid());
                            })
                            .collect(Collectors.toList());
                case "bible_malayalam":
                    MalayalamBibleDao malayalamBibleDao = bibleDatabase.malayalamBibleDao();
                    return malayalamBibleDao.getMalayalamBibleList(bookNumber, chapterNumber)
                            .stream()
                            .map(verse -> {
                                return new Data(bookName + " " + verse.getChapter() + ":" + verse.getVersecount(), verse.getVerse(), verse.getVerseid());
                            })
                            .collect(Collectors.toList());
                default:
                    TeluguBibleDao teluguBibleDao = bibleDatabase.teluguBibleDao();
                    return teluguBibleDao.getTeluguBibleList(bookNumber, chapterNumber)
                            .stream()
                            .map(verse -> {
                                return new Data(bookName + " " + verse.getChapter() + ":" + verse.getVersecount(), verse.getVerse(), verse.getVerseid());
                            })
                            .collect(Collectors.toList());
            }
        });
        try {
            mutableLiveData.setValue(completableFuture.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mutableLiveData;
    }
}