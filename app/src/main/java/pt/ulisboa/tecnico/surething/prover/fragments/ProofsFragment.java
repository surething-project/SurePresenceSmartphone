package pt.ulisboa.tecnico.surething.prover.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pt.ulisboa.tecnico.surething.prover.R;
import pt.ulisboa.tecnico.surething.prover.activities.MainActivity;
import pt.ulisboa.tecnico.surething.prover.adapters.ProofAdapter;
import pt.ulisboa.tecnico.surething.prover.classes.ContextualProof;
import pt.ulisboa.tecnico.surething.prover.utils.VerticalSpaceProofDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProofsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProofsFragment extends Fragment {

    private ProofAdapter adapter;
    private ArrayList<ContextualProof> proofList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProofsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProofsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProofsFragment newInstance(String param1, String param2) {
        ProofsFragment fragment = new ProofsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proofList = ((MainActivity)getActivity()).getDatabase().getContextualizedProofs((MainActivity)getActivity());
        Log.d("on create proofs", proofList.toString());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_proofs, container, false);

        adapter = new ProofAdapter(proofList);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView proofs_list = view.findViewById(R.id.recyclerView_proofs);
        VerticalSpaceProofDecoration vspd = new VerticalSpaceProofDecoration(12);
        proofs_list.addItemDecoration(vspd);
        proofs_list.setLayoutManager(llm);
        proofs_list.setAdapter(adapter);

        return view;
    }
}