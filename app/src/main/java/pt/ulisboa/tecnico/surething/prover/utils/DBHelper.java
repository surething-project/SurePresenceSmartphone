package pt.ulisboa.tecnico.surething.prover.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.surething_project.core.LocationClaim;
import eu.surething_project.core.SignedLocationClaim;
import eu.surething_project.core.SignedLocationEndorsement;
import pt.ulisboa.tecnico.surething.prover.activities.MainActivity;
import pt.ulisboa.tecnico.surething.prover.classes.ContextualProof;

public class DBHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "SurePresence";
    public static final String KEY = "uuid";
    public static final String CLAIM = "claim";
    public static final String ENDORSEMENT = "endorsement";
    public static final String STATUS = "status";
    public static final String TABLE_CLAIM = "SignedLocationClaims";
    public static final String TABLE_ENDORSEMENTS = "SignedLocationEndorsements";

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_CLAIM + " (" + KEY + " TEXT PRIMARY KEY, " + CLAIM + " BLOB NOT NULL); ");
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_ENDORSEMENTS + " (" + KEY + " TEXT PRIMARY KEY, " + ENDORSEMENT + " BLOB NOT NULL, " + STATUS + " INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CLAIM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ENDORSEMENTS);
        onCreate(sqLiteDatabase);

    }

    public boolean insertClaim (String uuid, byte[] claim) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY, uuid);
        contentValues.put(CLAIM, claim);
        db.insert(TABLE_CLAIM, null, contentValues);
        return true;
    }

    public boolean insertEndorsement (String uuid, byte[] endorsement) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY, uuid);
        contentValues.put(ENDORSEMENT, endorsement);
        contentValues.put(STATUS, 0);
        db.insert(TABLE_ENDORSEMENTS, null, contentValues);
        return true;
    }

    public SignedLocationClaim getClaim(String uuid){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CLAIM + " where " + KEY + " =?",new String[] { uuid });
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                byte[] claim = cursor.getBlob(cursor.getColumnIndex(CLAIM));
                try {
                    return SignedLocationClaim.parseFrom(claim);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    public List<SignedLocationEndorsement> getEndorsements(String uuid){
        List<SignedLocationEndorsement> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_ENDORSEMENTS + " where " + KEY + " = " + uuid,null);
        while(cursor.moveToNext()){
            byte[] endorsement = cursor.getBlob(cursor.getColumnIndex(ENDORSEMENT));
            try {
                list.add(SignedLocationEndorsement.parseFrom(endorsement));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public int getStatus(String uuid){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_ENDORSEMENTS + " where " + KEY + " = " + uuid,null);
        return cursor.getInt(cursor.getColumnIndex(STATUS));
    }

    public ArrayList<ContextualProof> getContextualizedProofs(Context context){
        // Most import function
        // Retrieves all location claims/endorsements
        // Contextualizes all the information there existent to user friendly information

        /*
        1. Get all claims.
        2. For each claim, check if there are endorsements.
        3. If there are, check their status.
        4. Merge all that information in contextualized proofs.
         */
        Log.d("PEDIDNDO", "TODAS");
        ArrayList<ContextualProof> contextualProofList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor_claim = db.rawQuery("select * from " + TABLE_CLAIM,null);
        while(cursor_claim.moveToNext()) {
            String uuid = cursor_claim.getString(cursor_claim.getColumnIndex(KEY));

            try {
                SignedLocationClaim slc = SignedLocationClaim.parseFrom(cursor_claim.getBlob(cursor_claim.getColumnIndex(CLAIM))); // this is the claim

                Cursor cursor_endor = db.rawQuery("select * from " + TABLE_ENDORSEMENTS + " where " + KEY + " =?", new String[] { uuid }); // lets get all endorsements

                //check if has been endorsed
                if(cursor_endor.getCount() == 0){ // not endorsed, just a simple claim
                    ContextualProof contextualProof = ContextualizeProof(context, uuid, null, slc, 0);
                    contextualProofList.add(contextualProof);
                    continue; // move to the next claim
                }

                while (cursor_endor.moveToNext()) {
                    Log.d("tem de entrar aqui", "aqui");
                    byte[] endorsement = cursor_endor.getBlob(cursor_endor.getColumnIndex(ENDORSEMENT));
                    SignedLocationEndorsement sle = SignedLocationEndorsement.parseFrom(endorsement);
                    int status = cursor_endor.getInt(cursor_endor.getColumnIndex(STATUS));
                    ContextualProof cp = ContextualizeProof(context, uuid, sle, slc, status);
                    contextualProofList.add(cp);
                    Log.d("CONTEXTUAL", cp.toString());
                }

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        Log.d("array", contextualProofList.toString());
        return contextualProofList;
    }

    private ContextualProof ContextualizeProof(Context context, String uuid, SignedLocationEndorsement sle, SignedLocationClaim slc, int status){
        Location location = new Location(LocationManager.GPS_PROVIDER); // was early obtained through GPS
        location.setLatitude(slc.getClaim().getLocation().getLatLng().getLatitude());
        location.setLongitude(slc.getClaim().getLocation().getLatLng().getLongitude());

        MainActivity activity = (MainActivity) context;
        String address = location.getLatitude() + " " + location.getLongitude();
        String city = "";
        Log.d("WIFI STATUS", String.valueOf(activity.getWiFIstatus()));
        // THIS REQUIRES A WI-FI CONNECTION
        if(activity.getWiFIstatus()){
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(context, Locale.getDefault());

            try {
                Log.d("location", location.toString());
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("addresses", addresses.toString());
            address = addresses.get(0).getThoroughfare() + ", " + addresses.get(0).getSubThoroughfare(); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
        }

        boolean endorsed = false;
        String witnessID = null;
        if(sle != null){
            endorsed = true;
            witnessID = sle.getEndorsement().getWitnessId();
        }

        ContextualProof wrapper = new ContextualProof(uuid,
                slc.getClaim().getTime().getTimestamp().getSeconds(),
                location,
                address,
                city,
                status,
                endorsed, witnessID);

        Log.d("wrapper", wrapper.toString());
        return wrapper;

    }

}
