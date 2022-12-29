package com.ikalangirajeev.telugubiblemessages.ui.bible.app.chapters;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ikalangirajeev.telugubiblemessages.ui.bible.app.Data;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.BibleDatabase;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.EnglishBibleDao;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.HindiBibleDao;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.KannadaBibleDao;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.MalayalamBibleDao;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.TamilBibleDao;
import com.ikalangirajeev.telugubiblemessages.ui.roombible.TeluguBibleDao;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChaptersViewModel extends AndroidViewModel {

    private static final String TAG = "ChaptersViewModel";

    private Integer versesCount;

    private BibleDatabase bibleDatabase;
    private MutableLiveData<List<Data>> mLiveData;

    public ChaptersViewModel(@NonNull Application application) {
        super(application);
        this.bibleDatabase = BibleDatabase.getBibleDatabase(application);
        this.mLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Data>> getText(String bibleSelected, int bookNumber, int chaptersCount) {
        CompletableFuture<List<Data>> completableFuture = CompletableFuture.supplyAsync(() -> {
            switch (bibleSelected) {
                case "bible_english":
                    return IntStream.rangeClosed(1, chaptersCount)
                            .mapToObj(index -> {
                                EnglishBibleDao englishBibleDao = bibleDatabase.englishBibleDao();
                                versesCount = englishBibleDao.getVersesCount(bookNumber, index);
                                return new Data(String.valueOf(index), versesCount + " Verses");
                            })
                            .collect(Collectors.toList());
                case "bible_tamil":
                    return IntStream.rangeClosed(1, chaptersCount)
                            .mapToObj(index -> {
                                TamilBibleDao tamilBibleDao = bibleDatabase.tamilBibleDao();
                                versesCount = tamilBibleDao.getVersesCount(bookNumber, index);
                                return new Data(String.valueOf(index), versesCount + " வசனங்கள்");
                            })
                            .collect(Collectors.toList());
                case "bible_kannada":
                    return IntStream.rangeClosed(1, chaptersCount)
                            .mapToObj(index -> {
                                KannadaBibleDao kannadaBibleDao = bibleDatabase.kannadaBibleDao();
                                versesCount = kannadaBibleDao.getVersesCount(bookNumber, index);
                                return new Data(String.valueOf(index), versesCount + " ಪದ್ಯಗಳು");
                            })
                            .collect(Collectors.toList());
                case "bible_hindi":
                    return IntStream.rangeClosed(1, chaptersCount)
                            .mapToObj(index -> {
                                HindiBibleDao hindiBibleDao = bibleDatabase.hindiBibleDao();
                                versesCount = hindiBibleDao.getVersesCount(bookNumber, index);
                                return new Data(String.valueOf(index), "   " + versesCount + " छंद   ");
                            })
                            .collect(Collectors.toList());
                case "bible_malayalam":
                    return IntStream.rangeClosed(1, chaptersCount)
                            .mapToObj(index -> {
                                MalayalamBibleDao malayalamBibleDao = bibleDatabase.malayalamBibleDao();
                                versesCount = malayalamBibleDao.getVersesCount(bookNumber, index);
                                return new Data(String.valueOf(index), versesCount + " വാക്യങ്ങൾ");
                            })
                            .collect(Collectors.toList());
                default:
                    return IntStream.rangeClosed(1, chaptersCount)
                            .mapToObj(index -> {
                                TeluguBibleDao teluguBibleDao = bibleDatabase.teluguBibleDao();
                                versesCount = teluguBibleDao.getVersesCount(bookNumber, index);
                                return new Data(String.valueOf(index), versesCount + " వచనాలు");
                            })
                            .collect(Collectors.toList());
            }
        });
        try {
            mLiveData.setValue(completableFuture.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mLiveData;
    }
}