package com.example.findaseat;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findaseat.Utils.User;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;


public class ProfileFragment extends Fragment {



    TextView name, uscid, affiliation, email;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        User user = (User) getActivity().getApplicationContext();
        Log.d("UserDebug", "First Name: " + user.getFirst());
        Log.d("UserDebug", "Last Name: " + user.getLast());
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        System.out.println(user.getEmail());
        name = (TextView) view.findViewById(R.id.name);
        uscid = (TextView) view.findViewById(R.id.uscid);
        affiliation= (TextView) view.findViewById(R.id.affiliation);
        email = (TextView)view.findViewById(R.id.em);

        name.setText(user.getFirst() + " " + user.getLast());
        email.setText(user.getEmail());
        uscid.setText("ID:" + user.getUscID());
        affiliation.setText(user.getAffiliation());
        this.setText(view);
        return view;
    }

    private void setText(View view) {
        User user = (User) getActivity().getApplicationContext();
        name = (TextView) view.findViewById(R.id.name);
        uscid = (TextView) view.findViewById(R.id.uscid);
        affiliation= (TextView) view.findViewById(R.id.affiliation);
        email = (TextView)view.findViewById(R.id.em);

        name.setText(user.getFirst() + " " + user.getLast());
        email.setText(user.getEmail());
        uscid.setText("ID:" + user.getUscID());
        affiliation.setText(user.getAffiliation());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Perform any additional UI-related setup here
        // You can access and manipulate UI elements within the fragment's view.

        User user = (User) getActivity().getApplicationContext();
        name = (TextView) view.findViewById(R.id.name);
        uscid = (TextView) view.findViewById(R.id.uscid);
        affiliation= (TextView) view.findViewById(R.id.affiliation);
        email = (TextView)view.findViewById(R.id.em);

        // Add event listeners or set initial values for UI elements
        name.setText(user.getFirst() + " " + user.getLast());
        email.setText(user.getEmail());
        uscid.setText("ID:" + user.getUscID());
        affiliation.setText(user.getAffiliation());

        // You can also use the view parameter to access UI elements
        // and perform other UI-related operations.

        TextView history =(TextView) view.findViewById(R.id.reserveHistory);

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(this, ReserveHistory.class);
                //startActivity(intent);
                replaceFragment(new ReserveHistoryFragment());
            }
        });
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, newFragment);
        fragmentTransaction.addToBackStack(null); // Add to back stack for back navigation
        fragmentTransaction.commit();
    }

}