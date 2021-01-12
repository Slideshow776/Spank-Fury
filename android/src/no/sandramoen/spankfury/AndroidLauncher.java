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
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import no.sandramoen.spankfury.utils.GooglePlayServices;

public class AndroidLauncher extends AndroidApplication implements GooglePlayServices {
    private static boolean enableGooglePlayServices = true;

    private static final int RC_SIGN_IN = 9001;
    private static final int RC_UNUSED = 5001;
    private static final int RC_LEADERBOARD_UI = 9004;

    private GoogleSignInClient signInClient;
    private LeaderboardsClient leaderboardsClient;
    private PlayersClient playersClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useWakelock = true;
        initialize(new SpankFuryGame(this), config);

        if (enableGooglePlayServices)
            startSignInIntent();
    }

    private void startSignInIntent() {
        signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (enableGooglePlayServices) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // The signed in account is stored in the result.
                    // System.out.println("MYMESSAGE: " + "Success! : D"); // TODO: REMOVE
                    GoogleSignInAccount signedInAccount = result.getSignInAccount();

                    // Display the 'Connecting' pop-up appropriately during sign-in.
                    GamesClient gamesClient = Games.getGamesClient(AndroidLauncher.this, signedInAccount);
                    gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                    gamesClient.setViewForPopups(((AndroidGraphics) AndroidLauncher.this.getGraphics()).getView());

                    // fetch clients
                    leaderboardsClient = Games.getLeaderboardsClient(this, signedInAccount);
                    playersClient = Games.getPlayersClient(this, signedInAccount);
                } else {
                    // System.out.println("MYMESSAGE: " + "unsuccesful : ("); // TODO: REMOVE
                    String message = result.getStatus().getStatusMessage();
                    // Status{statusCode=SIGN_IN_REQUIRED, resolution=null}
                    if (message == null || message.isEmpty()) {
                        message = "There was an issue with sign in.  Please try again later.";
                    }
                    new AlertDialog.Builder(this).setMessage(message)
                            .setNeutralButton(android.R.string.ok, null).show();
                }
            }
        }
    }

    @Override
    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    public void signOut() {
        System.out.println("MYMESSAGE: Signing out...");

        if (!isSignedIn()) {
            return;
        }

        signInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        leaderboardsClient = null;
                        playersClient = null;
                    }
                });
    }

    @Override
    public void signIn() {
        startSignInIntent();
    }

    @Override
    public void submitScore(int score) {
        if (isSignedIn())
            leaderboardsClient.submitScore(getString(R.string.leaderboard_highscore), score);
    }

    @Override
    public void getLeaderboard() {
        if (isSignedIn())
            leaderboardsClient.getLeaderboardIntent(getString(R.string.leaderboard_highscore))
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, RC_LEADERBOARD_UI);
                        }
                    });
    }
}
