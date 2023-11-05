package com.example.findaseat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findaseat.Utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class LogInFragment extends Fragment {

    TextInputEditText emailInput, passwordInput;
    MaterialButton login;

    User user;

    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    ProgressBar progressBar;
    FirebaseFirestore db;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        mDatabase = FirebaseDatabase.getInstance().getReference();

        View view=inflater.inflate(R.layout.fragment_log_in, container, false);

        db = FirebaseFirestore.getInstance();

        User user = (User) getActivity().getApplicationContext();

        progressBar = view.findViewById(R.id.progress);
        emailInput = view.findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        passwordInput = view.findViewById(R.id.password);
        login = (MaterialButton) view.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(getActivity(), "Success.", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user1 = mAuth.getCurrentUser();

                                    db.collection("users")
                                            .whereEqualTo("email", email)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            if (document.contains("first") &&
                                                                    document.contains("last") &&
                                                                    document.contains("email") &&
                                                                    document.contains("affiliation") &&
                                                                    document.contains("uscID")) {

                                                                user.setFirst(document.getString("first"));
                                                                user.setLast(document.getString("last"));
                                                                user.setEmail(document.getString("email"));
                                                                user.setUscID(document.getString("uscID"));
                                                                user.setAffiliation(document.getString("affiliation"));
                                                                user.setLoggedIn(true);
                                                            } else {
                                                                System.err.println("Document does not contain the 'email' field.");
                                                            }
                                                        }
                                                    } else {

                                                    }
                                                }
                                            });


                                    replaceFragment(new ProfileFragment());
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
        MaterialButton reg = (MaterialButton) view.findViewById(R.id.createNew);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new RegisterFragment());
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