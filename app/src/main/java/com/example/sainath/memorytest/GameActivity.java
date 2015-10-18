package com.example.sainath.memorytest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sainath.memorytest.model.GridItem;
import com.example.sainath.memorytest.net.FlickrRequest;
import com.example.sainath.memorytest.model.GridViewAdapter;
import com.example.sainath.memorytest.utils.Constants;
import com.example.sainath.memorytest.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by sainath on 10/17/2015.
 *
 * Activity showing the main game screen.
 */
public class GameActivity extends AppCompatActivity {
    private int gameState = Constants.GAME_STATE_LEARN;
    private ArrayList<GridItem> mGridData;
    private GridViewAdapter mGridViewAdapter;
    public static int width1;
    TextView tvTimer;
    ImageView mImageView;
    GridItem itemToFind;
    CountDownTimer mCountDownTimer;
    private static int cntTries = 0;
    private static int cntFound = 0;
    private static int urlCnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width1 = metrics.widthPixels;

        if(savedInstanceState != null) {
            Log.d(Constants.TAG, "onCreate(): Restoring from Bundle");
            cntFound = savedInstanceState.getInt(Constants.COUNT_FOUND);
            cntTries = savedInstanceState.getInt(Constants.COUNT_TRIES);
            mGridData = savedInstanceState.getParcelableArrayList(Constants.GRID_DATA);
            setGameState(savedInstanceState.getInt(Constants.GAME_STATE));
        } else {
            Log.d(Constants.TAG, "onCreate(): Creating new!");
            mGridData = new ArrayList<>(Constants.NUM_IMAGES);
            setGameState(Constants.GAME_STATE_LEARN);
        }
        GridView gridview = (GridView) findViewById(R.id.gridview);
        mGridViewAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
        gridview.setAdapter(mGridViewAdapter);

        Log.d(Constants.TAG, "onCreate(): Setting ClickListener for images!");
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getGameState() == Constants.GAME_STATE_IDENTIFY) {
                    GridItem item = (GridItem) parent.getItemAtPosition(position);
                    cntTries++;

                    if (item.getTitle().equals(itemToFind.getTitle())) {
                        item.setIsShown(true);
                        cntFound++;
                        showNextImage();
                    }
                    checkIfGameOver();
                }
            }
        });

        mImageView = (ImageView)findViewById(R.id.imageIdentify);
        tvTimer = (TextView)findViewById(R.id.timer);

        if(!Utils.isConnected(this)) {
            Toast.makeText(this, R.string.connectivity_error, Toast.LENGTH_SHORT).show();

            tvTimer.setText(getResources().getString(R.string.connectivity_error)
                    +"\n\n"+getResources().getString(R.string.connectivity_required));
        }else {
            Log.d(Constants.TAG, "onCreate(): Requesting data from Flickr!!");
            FlickrRequest flickrRequest = new FlickrRequest(this, mGridData, mGridViewAdapter);
            flickrRequest.fetchImages(urlCnt);

            mCountDownTimer = new MyTimer(Constants.TIME_IN_MILLIS, Constants.SEC_IN_MILLIS);
            mCountDownTimer.start();
            Log.d(Constants.TAG, "onCreate(): Timer Started!!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Constants.TAG, "onResume(): in onResume!");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(Constants.TAG, "onSaveInstanceState(): Saving into Bundle");
        outState.putInt(Constants.COUNT_FOUND, cntFound);
        outState.putInt(Constants.COUNT_TRIES, cntTries);
        outState.putInt(Constants.GAME_STATE, getGameState());
        outState.putParcelableArrayList(Constants.GRID_DATA, mGridData);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(Constants.TAG, "onRestoreInstanceState(): Restoring from Bundle");
        super.onRestoreInstanceState(savedInstanceState);
        cntFound = savedInstanceState.getInt(Constants.COUNT_FOUND);
        cntTries = savedInstanceState.getInt(Constants.COUNT_TRIES);
        mGridData = savedInstanceState.getParcelableArrayList(Constants.GRID_DATA);
        setGameState(savedInstanceState.getInt(Constants.GAME_STATE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(Constants.TAG, "onOptionsItemSelected(): before switch");

        switch (item.getItemId()) {
            case R.id.refresh_menu:
                refreshGame();
                return true;
            case R.id.help_menu:
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    /**
     * Restart the game by re-initializing all the values.
     * Reuse the earlier collection of images and shuffle them.
     */
    public void replayGame() {
        Log.d(Constants.TAG, "replayGame(): Replaying the Game");
        cntTries = 0;
        cntFound = 0;
        Log.d(Constants.TAG, "replayGame(): Shuffling the images!");
        Collections.shuffle(mGridData);
        setGameState(Constants.GAME_STATE_LEARN);
        toggleViewVisibility();

        mCountDownTimer.cancel();
        mCountDownTimer = new MyTimer(Constants.TIME_IN_MILLIS, Constants.SEC_IN_MILLIS);
        mCountDownTimer.start();
        Log.d(Constants.TAG, "replayGame(): Started the timer!");
    }

    /**
     * Restart the game by re-initializing all the values.
     * Re-downloads a new set of images and shuffle them.
     */
    public void refreshGame() {
        Log.d(Constants.TAG, "refreshGame(): Replaying the Game");
        cntTries = 0;
        cntFound = 0;
        setGameState(Constants.GAME_STATE_LEARN);
        toggleViewVisibility();
        mGridData.clear();
        mGridViewAdapter.setGridData(mGridData);

        // Switch the URL to the next one.
        urlCnt = (urlCnt+1)%3;

        if(!Utils.isConnected(this)) {
            Toast.makeText(this, R.string.connectivity_error, Toast.LENGTH_SHORT).show();

            tvTimer.setText(getResources().getString(R.string.connectivity_error)
                    + "\n\n" + getResources().getString(R.string.connectivity_required));
        }else {
            Log.d(Constants.TAG, "refreshGame(): Requesting data from Flickr!!");
            FlickrRequest flickrRequest = new FlickrRequest(this, mGridData, mGridViewAdapter);
            flickrRequest.fetchImages(urlCnt);

            mCountDownTimer.cancel();
            mCountDownTimer = new MyTimer(Constants.TIME_IN_MILLIS, Constants.SEC_IN_MILLIS);
            mCountDownTimer.start();

            Log.d(Constants.TAG, "refreshGame(): Started the timer!");
        }
    }

    /**
     * Checks if all the images are identified and shows a dialog to exit or replay the game.
     */
    public void checkIfGameOver() {
        Log.d(Constants.TAG, "checkIfGameOver(): Checking if Game Over!");
        boolean isOver = (cntFound >= Constants.NUM_IMAGES);

        if(isOver) {
            Log.d(Constants.TAG, "checkIfGameOver(): Displaying dialog on Game Over!");
            AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
            builder.setMessage(getResources().getString(R.string.txt_game_over, cntTries))
                    .setCancelable(false)
                    .setNegativeButton(getResources().getString(R.string.btn_exit),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                }
                            })
                    .setPositiveButton(getResources().getString(R.string.btn_replay),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    replayGame();
                                }
                            });
            builder.create().show();
        }
    }

    /**
     * Selects an un-identified image at random and shows to the user.
     */
    public void showNextImage() {
        Log.d(Constants.TAG, "showNextImage(): Notifying data set changed!");
        mGridViewAdapter.notifyDataSetChanged();

        if(mGridData.size() > 0) {
            do {
                Random random = new Random();
                int pos = random.nextInt(Constants.NUM_IMAGES);
                itemToFind = mGridData.get(pos);
            } while (itemToFind.isShown() && cntFound < Constants.NUM_IMAGES);

            Log.d(Constants.TAG, "showNextImage(): Picasso loading the next image to be identified!");
            Picasso.with(getApplicationContext()).load(itemToFind.getImage()).placeholder(R.drawable.loading).into(mImageView);
        } else {
            Toast.makeText(GameActivity.this, R.string.data_fetch_failed, Toast.LENGTH_SHORT).show();
            Log.e(Constants.TAG, "showNextImage(): " + getResources().getString(R.string.data_fetch_failed));

            mImageView.setImageDrawable(GameActivity.this.getResources().getDrawable(R.drawable.placeholder));
        }
    }

    /**
     * Toggles the visibility of the view based on the game state
     */
    public void toggleViewVisibility() {
        if(getGameState() == Constants.GAME_STATE_IDENTIFY) {
            tvTimer.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
        } else {
            tvTimer.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.GONE);
        }
        mGridViewAdapter.notifyDataSetChanged();
    }

    public void identifyImages() {
        toggleViewVisibility();

        showNextImage();
    }

    /**
     * CountDownTimer to show the time remaining in the 'Learning' state.
     * Once the timer expires, it switches to the 'Identify' state and flips all the images.
     */
    public class MyTimer extends CountDownTimer {
        public MyTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvTimer.setText(millisUntilFinished / Constants.SEC_IN_MILLIS + " !!");
        }

        @Override
        public void onFinish() {
            tvTimer.setText("0 !!");

            // Set Game State to IDENTIFY
            setGameState(Constants.GAME_STATE_IDENTIFY);
            // Set all images to hiden
            for (GridItem item : mGridData) {
                item.setIsShown(false);
            }
            identifyImages();
        }
    }
}
