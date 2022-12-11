package pt.ulisboa.tecnico.surething.prover.fragments;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import org.jetbrains.annotations.NotNull;

import eu.surething_project.core.LocationClaim;
import eu.surething_project.core.SignedLocationClaim;
import pt.ulisboa.tecnico.surething.pose.Enc_Structure;
import pt.ulisboa.tecnico.surething.prover.R;
import pt.ulisboa.tecnico.surething.prover.activities.MainActivity;
import pt.ulisboa.tecnico.surething.prover.activities.QrCodeActivity;
import pt.ulisboa.tecnico.surething.prover.utils.CoreHandler;
import pt.ulisboa.tecnico.surething.prover.utils.POSEHandler;
import pt.ulisboa.tecnico.surething.prover.utils.crypto;

import static android.content.Context.WINDOW_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QrCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QrCodeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ImageView qrCode;
    private Button btn_qr;
    private EditText editInfo;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    private Enc_Structure enc_structure;

    public QrCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QrCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QrCodeFragment newInstance(String param1, String param2) {
        QrCodeFragment fragment = new QrCodeFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_code, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState){


        Location location = ((MainActivity) getActivity()).getLocation();

        Log.d("qr location", location.toString());

        LocationClaim claim = CoreHandler.createLocationClaim(this.getContext(), location);

        byte[] signature = crypto.sign(this.getContext(), claim.toByteArray());

        SignedLocationClaim slc = CoreHandler.createSignedLocationClaim(claim, CoreHandler.createSignature(signature));

        enc_structure = POSEHandler.createEnc_Structure(this.getContext(), slc.toByteArray()); // signed location claim

        qrCode = view.findViewById(R.id.iv_qrCode);

        generateQRcode();

        ((MainActivity)getActivity()).getDatabase().insertClaim(slc.getClaim().getClaimId(), slc.toByteArray());

        Log.d("claim", slc.toString());

        Log.d("retrieved from db", ((MainActivity)getActivity()).getDatabase().getClaim(slc.getClaim().getClaimId()).toString());
    }

    private void generateQRcode(){
        // below line is for getting
        // the windowmanager service.
        WindowManager manager = (WindowManager) this.getContext().getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(new String(Base64.encode(enc_structure.toByteArray(), Base64.DEFAULT)), null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
    }
}