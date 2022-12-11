package pt.ulisboa.tecnico.surething.prover.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;
import pt.ulisboa.tecnico.surething.prover.R;
import pt.ulisboa.tecnico.surething.prover.classes.ContextualProof;

public class ProofAdapter extends RecyclerView.Adapter<ProofAdapter.ViewHolder>{

    private List<ContextualProof> proofs;
    private final String VERIFIED = "Verified by ";
    private final String NOT_VERIFIED = "Presence not yet verified";
    private final String SENT = "Stored in the server";
    private final String NOT_SENT = "Not stored in the server";

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView address;
        private TextView city;
        private TextView time;

        private ImageView witness_status_img;
        private TextView witness_status;

        private ImageView verifier_status_img;
        private TextView verifier_status;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            address = (TextView) view.findViewById(R.id.proof_location_street);
            city = (TextView) view.findViewById(R.id.proof_location_city);
            time = (TextView) view.findViewById(R.id.proof_time);

            witness_status_img = view.findViewById(R.id.witness_img);
            witness_status = view.findViewById(R.id.witness_status);

            verifier_status_img = view.findViewById(R.id.proof_sent);
            verifier_status = view.findViewById(R.id.proof_sent_text);
        }

        public TextView getAddress() {
            return address;
        }

        public TextView getCity() {
            return city;
        }

        public TextView getTime() {
            return time;
        }

        public ImageView getWitness_status_img() {
            return witness_status_img;
        }

        public TextView getWitness_status() {
            return witness_status;
        }

        public ImageView getVerifier_status_img() {
            return verifier_status_img;
        }

        public TextView getVerifier_status() {
            return verifier_status;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public ProofAdapter(ArrayList<ContextualProof> dataSet) {
        proofs = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.proof_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        ContextualProof cp = proofs.get(position);

        viewHolder.getAddress().setText(cp.getAddress());
        viewHolder.getCity().setText(cp.getCity());

        Calendar cal = Calendar.getInstance(Locale.getDefault()); // for now is default but we can use the one in the location
        cal.setTimeInMillis(cp.getTime()); // timestamp is already in milliseconds
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        viewHolder.getTime().setText(date);

        if(cp.isEndorsed()){
            viewHolder.getWitness_status_img().setImageResource(R.drawable.ic_correct);
            viewHolder.getWitness_status().setText(VERIFIED + cp.getWitnessID());
        }
        else{
            viewHolder.getWitness_status_img().setImageResource(R.drawable.ic_close);
            viewHolder.getWitness_status().setText(NOT_VERIFIED);
        }

        if(cp.isSentToVerifier() == 1){
            viewHolder.getVerifier_status_img().setImageResource(R.drawable.ic_correct);
            viewHolder.getVerifier_status().setText(SENT);
        }
        else{
            viewHolder.getVerifier_status_img().setImageResource(R.drawable.ic_close);
            viewHolder.getVerifier_status().setText(NOT_SENT);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return proofs.size();
    }
}
