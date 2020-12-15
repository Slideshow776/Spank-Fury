package no.sandramoen.spankfury;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.*;

public class AndroidLauncher extends AndroidApplication {
    // ------------------------------------------------------------------------
	private static final int RC_SIGN_IN = 9001;
    // ------------------------------------------------------------------------

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new SpankFuryGame(), config);

		// ------------------------------------------------------------------------
		startSignInIntent();
	}

    // ------------------------------------------------------------------------
	private void startSignInIntent() {
		GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
				GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
		Intent intent = signInClient.getSignInIntent();
		startActivityForResult(intent, RC_SIGN_IN);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			if (result.isSuccess()) {
				// The signed in account is stored in the result.
				GoogleSignInAccount signedInAccount = result.getSignInAccount();
			} else {
				String message = result.getStatus().getStatusMessage();
				// Status{statusCode=SIGN_IN_REQUIRED, resolution=null}
				if (message == null || message.isEmpty()) {
					message = getString(R.string.signin_other_error);
				}
				new AlertDialog.Builder(this).setMessage(message)
						.setNeutralButton(android.R.string.ok, null).show();
			}
		}
	}
    // ------------------------------------------------------------------------
}
