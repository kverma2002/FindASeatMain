package com.example.findaseat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterFragment extends Fragment {

    TextInputEditText fNameInput, lNameInput, emailInput,uscIDINput, affiliationInput, passwordInput;
    MaterialButton back, create;
    FirebaseAuth mAuth;

    ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        progressBar = view.findViewById(R.id.progressR);

        back = (MaterialButton) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new LogInFragment());
            }
        });

        emailInput = view.findViewById(R.id.email);
        fNameInput = view.findViewById(R.id.firstName);
        lNameInput = view.findViewById(R.id.lastName);
        uscIDINput = view.findViewById(R.id.uscid);
        affiliationInput = view.findViewById(R.id.affiliation);
        passwordInput = view.findViewById(R.id.password);
        create = view.findViewById(R.id.createAccount);



        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, fName, lName, affiliation, uscID;
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                fName = fNameInput.getText().toString();
                lName = lNameInput.getText().toString();
                affiliation = affiliationInput.getText().toString();
                uscID = uscIDINput.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(fName) ||
                        TextUtils.isEmpty(lName) || TextUtils.isEmpty(affiliation) || TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(uscID)) {
                    Toast.makeText(getActivity(), "Please Fillout All Fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    // Sign in success, update UI with the signed-in user's information
//                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(getActivity(), "Account Created.", Toast.LENGTH_SHORT).show();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });

            }
        });

        return view;
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, newFragment);
        fragmentTransaction.addToBackStack(null); // Add to back stack for back navigation
        fragmentTransaction.commit();
    }
}