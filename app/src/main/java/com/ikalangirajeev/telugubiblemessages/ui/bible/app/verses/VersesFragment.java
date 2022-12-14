package com.ikalangirajeev.telugubiblemessages.ui.bible.app.verses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.ikalangirajeev.telugubiblemessages.MainActivity;
import com.ikalangirajeev.telugubiblemessages.R;
import com.ikalangirajeev.telugubiblemessages.ui.SettingsFragment;
import com.ikalangirajeev.telugubiblemessages.ui.bible.app.linkedrefs.BottomSheetFragment;
import com.ikalangirajeev.telugubiblemessages.ui.bible.app.Data;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class VersesFragment extends Fragment {

    private VersesViewModel versesViewModel;
    private RecyclerView recyclerView;
    private VersesRecyclerViewAdapter myRecyclerViewAdapter;
    private NavController navController;


    private String bookName;
    private String bibleSelected;
    private int bookNumber;
    private int chapterNumber;
    private int chapterCount;
    private int verseNumber;
    private int highlightVerseNumber;
    private Vibrator vibrator;
    private SharedPreferences prefs;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        this.bibleSelected = getArguments().getString("bibleSelected", prefs.getString(SettingsFragment.PREF_SELECTED_BIBLE, "bible_english"));
        this.bookName = getArguments().getString("BookName");
        this.bookNumber = getArguments().getInt("BookNumber");
        this.chapterNumber = getArguments().getInt("ChapterNumber");
        this.chapterCount = getArguments().getInt("ChapterCount");
        this.verseNumber = getArguments().getInt("VerseNumber");
        this.highlightVerseNumber = getArguments().getInt("HighlightVerseNumber");
        this.vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.action_next).setVisible(true);
        menu.findItem(R.id.action_previous).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        final MenuItem menuNext = menu.findItem(R.id.action_next);
        final MenuItem menuPrevious = menu.findItem(R.id.action_previous);

        menuNext.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (chapterNumber < chapterCount) {

                    Bundle bundleNext = new Bundle();
                    bundleNext.putString("bibleSelected", bibleSelected);
                    bundleNext.putString("BookName", bookName);
                    bundleNext.putInt("BookNumber", bookNumber);
                    bundleNext.putInt("ChapterNumber", chapterNumber + 1);
                    bundleNext.putInt("ChapterCount", chapterCount);
                    bundleNext.putInt("VerseNumber", verseNumber);
                    navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.versesFragment, bundleNext, new NavOptions.Builder()
                            .setPopUpTo(R.id.versesFragment, true)
                            .setEnterAnim(R.anim.slide_in_right)
                            .setExitAnim(R.anim.slide_out_left)
                            .setPopEnterAnim(R.anim.slide_in_left)
                            .setPopExitAnim(R.anim.slide_out_right)
                            .build());
                    return true;
                } else {
                    Snackbar.make(getView(), "Chapters ended, only " + chapterCount + "Chapters are available", Snackbar.LENGTH_LONG).show();
                    return true;
                }
            }
        });

        menuPrevious.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (chapterNumber > 1) {
                    Bundle bundlePrevious = new Bundle();
                    bundlePrevious.putString("bibleSelected", bibleSelected);
                    bundlePrevious.putString("BookName", bookName);
                    bundlePrevious.putInt("BookNumber", bookNumber);
                    bundlePrevious.putInt("ChapterNumber", chapterNumber - 1);
                    bundlePrevious.putInt("ChapterCount", chapterCount);
                    bundlePrevious.putInt("VerseNumber", verseNumber);
                    navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.versesFragment, bundlePrevious, new NavOptions.Builder()
                            .setPopUpTo(R.id.versesFragment, true)
                            .setEnterAnim(R.anim.slide_in_left)
                            .setExitAnim(R.anim.slide_out_right)
                            .setPopEnterAnim(R.anim.slide_in_right)
                            .setPopExitAnim(R.anim.slide_out_left)
                            .build());
                    return true;
                } else {
                    Snackbar.make(getView(), "Chapters ended, Can't go much below", Snackbar.LENGTH_LONG).show();
                    return true;
                }
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        versesViewModel = new ViewModelProvider(this).get(VersesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_verses, container, false);


        recyclerView = root.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        myRecyclerViewAdapter = new VersesRecyclerViewAdapter(getActivity(), R.layout.card_view_verses);
        recyclerView.setAdapter(myRecyclerViewAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (ItemTouchHelper.LEFT == direction) {
                    Toast.makeText(getActivity(), "Linked References...", Toast.LENGTH_LONG).show();
                    BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("bibleSelected", bibleSelected);
                    bundle.putString("bookName", bookName);
                    bundle.putInt("bookNumber", bookNumber);
                    bundle.putInt("chapterNumber", chapterNumber);
                    bundle.putInt("verseNumber", viewHolder.getAdapterPosition() + 1);
                    bundle.putString("verseBody", myRecyclerViewAdapter.getDataAt(viewHolder.getAdapterPosition()).getBody());
                    bundle.putInt("verseId", myRecyclerViewAdapter.getDataAt(viewHolder.getAdapterPosition()).getRefLink());
                    bottomSheetFragment.setArguments(bundle);
                    bottomSheetFragment.show(getActivity().getSupportFragmentManager(), "bottomSheetFragment");
                    vibrator.vibrate(50);

                } else if (ItemTouchHelper.RIGHT == direction) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    int verseNumber = viewHolder.getAdapterPosition() + 1;
                    sendIntent.putExtra(Intent.EXTRA_TEXT, bookName + " " + chapterNumber + ":" + verseNumber + "\n"
                            + myRecyclerViewAdapter.getDataAt(viewHolder.getAdapterPosition()).getBody());
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                    vibrator.vibrate(50);
                    Toast.makeText(getActivity(), "Share...", Toast.LENGTH_LONG).show();
                }
            }
        }).attachToRecyclerView(recyclerView);


        versesViewModel.getData(bibleSelected, bookName, bookNumber, chapterNumber).observe(getViewLifecycleOwner(), new Observer<List<Data>>() {
            @Override
            public void onChanged(List<Data> dataList) {
                myRecyclerViewAdapter.setDataList(dataList);
                recyclerView.scrollToPosition(highlightVerseNumber - 1);
                myRecyclerViewAdapter.setHighLightPosition(highlightVerseNumber - 1);
                myRecyclerViewAdapter.setOnItemClickListener(new VersesRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(Data blogIndex, int position) {

                    }
                });
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }


}