package no.sandramoen.spankfury;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import androidx.annotation.NonNull;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.games.*;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import no.sandramoen.spankfury.utils.GooglePlayServices;
import org.jetbrains.annotations.NotNull;
import static java.lang.Math.toIntExact;

import java.util.Iterator;

public class AndroidLauncher extends AndroidApplication implements GooglePlayServices {
    private static final String token = "AndroidLauncher.java";
    private static Long highScore = 0L;
    private static Long startTime;

    private static final int RC_SIGN_IN = 9001;
    private static final int RC_UNUSED = 5001;
    private static final int RC_LEADERBOARD_UI = 9004;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mGoogleSignInAccount;
    private LeaderboardsClient mLeaderboardsClient;
    private PlayersClient mPlayersClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useWakelock = true;
        initialize(new SpankFuryGame(this), config);
    }

    private void startSignInIntent() {
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void popup() {
        // Display the 'Connecting' pop-up appropriately during sign-in.
        GamesClient gamesClient = Games.getGamesClient(AndroidLauncher.this, mGoogleSignInAccount);
        gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        gamesClient.setViewForPopups(((AndroidGraphics) AndroidLauncher.this.getGraphics()).getView());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                mGoogleSignInAccount = result.getSignInAccount();
                popup();

                // fetch clients
                mLeaderboardsClient = Games.getLeaderboardsClient(this, mGoogleSignInAccount);
                mPlayersClient = Games.getPlayersClient(this, mGoogleSignInAccount);
            } else {
                String message = result.getStatus().getStatusMessage();
                System.out.println("AndroidLauncher.java: " + result.getStatus());
                // Status{statusCode=SIGN_IN_REQUIRED, resolution=null}
                if (message == null || message.isEmpty()) {
                    message = "There was an issue with sign in: " + result.getStatus();
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    @Override
    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    public void signOut() {
        if (!isSignedIn()) {
            return;
        }

        popup();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mLeaderboardsClient = null;
                        mPlayersClient = null;
                    }
                });
    }

    @Override
    public void signIn() {
        startSignInIntent();
    }

    @Override
    public void submitScore(int score) {
        if (isSignedIn()) {
            // System.out.println(token + ": submitting score => " + score);
            mLeaderboardsClient.submitScore("CgkI4dKerKoaEAIQAA", score);
        }
    }

    @Override
    public void getLeaderboard() {
        // System.out.println(token + ": leaderboardsClient: " + leaderboardsClient);
        mLeaderboardsClient.getLeaderboardIntent("CgkI4dKerKoaEAIQAA")
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    @NotNull
    @Override
    public void fetchHighScore() {
        startTime = System.nanoTime();

        // https://stackoverflow.com/questions/24057643/top-5-scores-from-google-leaderboard
        mLeaderboardsClient.loadTopScores("CgkI4dKerKoaEAIQAA", LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC, 5, true).addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardsClient.LeaderboardScores>>() {
            @Override
            public void onSuccess(AnnotatedData<LeaderboardsClient.LeaderboardScores> leaderboardScoresAnnotatedData) {
                LeaderboardScoreBuffer scoreBuffer = leaderboardScoresAnnotatedData.get().getScores();
                Iterator<LeaderboardScore> it = scoreBuffer.iterator();

                highScore = it.next().getRawScore();
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
                System.out.println(token + ": We successfully found high score => " + highScore + ", it took duration => " + duration + "[ms]");
            }
        });
    }

    @NotNull
    @Override
    public int getHighScore() {
        // https://stackoverflow.com/questions/1590831/safely-casting-long-to-int-in-java
        return toIntExact(highScore);
    }
}
