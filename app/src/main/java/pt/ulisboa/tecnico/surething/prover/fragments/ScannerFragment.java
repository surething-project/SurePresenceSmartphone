package pt.ulisboa.tecnico.surething.prover.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import androidx.fragment.app.FragmentTransaction;
import eu.surething_project.core.SignedLocationClaim;
import eu.surething_project.core.SignedLocationEndorsement;
import pt.ulisboa.tecnico.surething.pose.Enc_Structure;
import pt.ulisboa.tecnico.surething.prover.R;
import pt.ulisboa.tecnico.surething.prover.activities.MainActivity;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScannerFragment extends Fragment{

    private final static String KEY_QR_CODE = "QR";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final static int CAMERA_REQUEST = 0;
    private View view;

    public ScannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScannerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScannerFragment newInstance(String param1, String param2) {
        ScannerFragment fragment = new ScannerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_scanner, container, false);

        createScanner();

        return view;

    }

    public void createScanner(){
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("QR Code");
        integrator.autoWide();
        integrator.setCameraId(0);
        integrator.initiateScan();

    }

    @Override
    public void onResume() {
        super.onResume();
        //if(mScannerView != null){
            // Register ourselves as a handler for scan results.
        //}
    }

    @Override
    public void onPause() {
        super.onPause();
        //if(mScannerView != null){
            // Stop camera on pause
        //}
    }


}