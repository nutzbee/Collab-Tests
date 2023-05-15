package foodrecommender.system.global;

import android.net.ConnectivityManager;
import android.net.Network;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class NetworkConnectivityCallback extends ConnectivityManager.NetworkCallback {

    private Snackbar snackbar;

    public NetworkConnectivityCallback(View rootView) {
        snackbar = Snackbar.make(rootView, "No internet connection", Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    public void onAvailable(Network network) {
        // Network is available
        if (snackbar.isShown()){
            snackbar.dismiss();
        }
    }

    @Override
    public void onLost(Network network) {
        // Network is available
        if (!snackbar.isShown()){
            snackbar.show();
        }
    }
}
