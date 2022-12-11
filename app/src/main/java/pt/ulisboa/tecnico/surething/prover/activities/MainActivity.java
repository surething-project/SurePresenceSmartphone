package pt.ulisboa.tecnico.surething.prover.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import eu.surething_project.core.SignedLocationEndorsement;
import pt.ulisboa.tecnico.surething.prover.R;
import pt.ulisboa.tecnico.surething.prover.fragments.MainFragment;
import pt.ulisboa.tecnico.surething.prover.fragments.ProfileFragment;
import pt.ulisboa.tecnico.surething.prover.fragments.ProofsFragment;
import pt.ulisboa.tecnico.surething.prover.fragments.QrCodeFragment;
import pt.ulisboa.tecnico.surething.prover.fragments.ScannerFragment;
import pt.ulisboa.tecnico.surething.prover.utils.Constants;
import pt.ulisboa.tecnico.surething.prover.utils.DBHelper;
import pt.ulisboa.tecnico.surething.prover.utils.SaveSharedPreference;
import pt.ulisboa.tecnico.surething.prover.utils.api;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static int CAMERA_REQUEST = 0;
    private final static int FINE_LOCATION_REQUEST = 1;
    private ScannerFragment scannerFragment;
    private ProofsFragment proofsFragment = new ProofsFragment();
    private BottomNavigationView bottomNav;
    private NavController navController;
    private boolean WIFIstatus;

    public DBHelper database;

    private FusedLocationProviderClient fusedLocationClient;

    private Merlin merlin;
    private MerlinsBeard merlinsBeard;


    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft;
            Log.d("aiai", String.valueOf(item.getItemId()));
            Log.d("aiai", String.valueOf(R.id.nav_qrcode));
            switch (item.getItemId()) {
                case R.id.nav_home:
                    /*ft = getSupportFragmentManager().beginTransaction();
                    ft.setReorderingAllowed(true);
                    ft.replace(R.id.nav_host_fragment, MainFragment.class, null);
                    ft.commit();*/
                    navController.navigate(R.id.mainFragment);
                    break;

                case R.id.nav_qrcode:
                    /*ft = getSupportFragmentManager().beginTransaction();
                    ft.setReorderingAllowed(true);
                    ft.replace(R.id.nav_host_fragment, QrCodeFragment.class, null);
                    ft.commit();*/
                    navController.navigate(R.id.qrCodeFragment);
                    break;

                case R.id.nav_scan:
                    /*ft = getSupportFragmentManager().beginTransaction();
                    ft.setReorderingAllowed(true);
                    scannerFragment = new ScannerFragment();
                    ft.replace(R.id.nav_host_fragment, scannerFragment, null);
                    ft.commit();*/
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("no", "permissions");
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    }
                    else{ // we already have permissions
                        navController.navigate(R.id.scannerFragment);
                    }
                    break;

                case R.id.nav_proofs:
                    /*ft = getSupportFragmentManager().beginTransaction();
                    ft.setReorderingAllowed(true);
                    ft.replace(R.id.nav_host_fragment, proofsFragment, null);
                    ft.commit();*/
                    navController.navigate(R.id.proofsFragment);
                    break;

                case R.id.nav_profile:
                    /*ft = getSupportFragmentManager().beginTransaction();
                    ft.setReorderingAllowed(true);
                    ft.replace(R.id.nav_host_fragment, new ProfileFragment());
                    ft.commit();*/
                    navController.navigate(R.id.profileFragment);
                    break;
            }
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DBHelper(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        bottomNav.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        merlin = new Merlin.Builder().withDisconnectableCallbacks().withConnectableCallbacks().build(this);
        merlinsBeard = MerlinsBeard.from(this);

        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                // Do something you haz internet!
                Log.d("I GOT INTERNET", "WI-FI");
                WIFIstatus = true;
                //call the api class and send the endorsement + claim
            }
        });

        merlin.registerDisconnectable(new Disconnectable() {
            @Override
            public void onDisconnect() {
                // Do something you haz internet!
                Log.d("LOST INTERNET", "WI-FI");
                WIFIstatus = false;
                //call the api class and send the endorsement + claim
            }
        });

        WIFIstatus = merlinsBeard.isConnectedToWifi();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("no", "permissions");
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST);
        }
        else{ // we already have permissions
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d("location", location.toString());
                            }
                        }
                    });
        }

    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
        merlin.bind();
    }

    @Override
    protected void onPause(){
        merlin.unbind();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        int seletedItemId = bottomNav.getSelectedItemId();
        if (R.id.home != seletedItemId) {
            navController.navigate(R.id.mainFragment);
        } else {
            super.onBackPressed();
        }
    }
    /*public String postLocationProof(SureThingEntities.LocationProofProto lp){
        byte[] response = api.postAuthRequest(Constants.BASE_URL_VERIFIER, lp.toByteArray(), SaveSharedPreference.getPrefToken(MainActivity.this));
        return response.toString();
    }

    public class SubmitLPTask extends AsyncTask<SureThingEntities.LocationProofProto, Void, String> {
        @Override
        protected String doInBackground(SureThingEntities.LocationProofProto... lp) {

            return postLocationProof(lp[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("aiai", response);
        }
    }

    public class GetLPTask extends AsyncTask<Long, Void, byte[]> {
        @Override
        protected byte[] doInBackground(Long... ids) {
            Log.d("doinbackground", "doinbackground");
            return api.getRequest(Constants.BASE_URL_LEDGER, "id", ids[0]);
        }

        @Override
        protected void onPostExecute(byte[] response) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new ByteArrayInputStream(response));
                SureThingEntities.LocationProofProto proto =
                        (SureThingEntities.LocationProofProto) ois.readObject();
                Log.d("LPS", proto.toString());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                //Toast.makeText(this, "You cancelled the scanning!", Toast.LENGTH_LONG).show();
                navController.navigate(R.id.mainFragment);

            } else {
                Toast.makeText(this, "Presence successfully verified!", Toast.LENGTH_LONG).show();
                try {
                    // obtain signed location endorsement
                    SignedLocationEndorsement sle = SignedLocationEndorsement.parseFrom(Base64.decode(result.getContents().getBytes(), Base64.DEFAULT));
                    // store locally the location endorsement
                    getDatabase().insertEndorsement(sle.getEndorsement().getClaimId(), sle.toByteArray());
                    Log.d("endorsement", sle.toString());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("aiaiai", "aiaiaiai");
        switch (requestCode) {
            case CAMERA_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("aiai", "Permissions granted");
                    navController.navigate(R.id.scannerFragment);
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
            case FINE_LOCATION_REQUEST:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        Log.d("location", location.toString());
                                    }
                                }
                            });
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    public DBHelper getDatabase(){
        return database;
    }

    public boolean getWiFIstatus(){
        return this.WIFIstatus;
    }

    @SuppressLint("MissingPermission")
    public Location getLocation(){
        Task task = fusedLocationClient.getLastLocation();
        while(!task.isComplete()){

        }
        return (Location) task.getResult();
    }

}