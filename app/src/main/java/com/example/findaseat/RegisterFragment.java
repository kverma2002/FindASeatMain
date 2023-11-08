package com.example.findaseat;

import static com.google.common.reflect.Reflection.getPackageName;

import static java.lang.Package.getPackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class RegisterFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    TextInputEditText fNameInput, lNameInput, emailInput,uscIDINput, affiliationInput, passwordInput;
    MaterialButton back, create;
    Button imageSelect, imageUpload;
    FirebaseAuth mAuth;

    ProgressBar progressBar;
    FirebaseFirestore db;
    FirebaseStorage storage;

    Uri selectedImage;

    ImageView imageView;


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

        imageView = view.findViewById(R.id.profileImageView);

        Resources res = getResources();
        Context context = getContext();
        int drawableId = res.getIdentifier("usc_trojan.jpeg", "drawable", context.getPackageName());
        if (drawableId != 0) {
            // Construct a URI using the package name and resource ID
            selectedImage = Uri.parse("android.resource://" + context.getPackageName() + "/" + drawableId);

            // Set the URI as the image source
            imageView.setImageURI(selectedImage);
        } else {
            // Handle the case where the resource is not found
        }


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

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

        imageSelect = (Button) view.findViewById(R.id.choosePictureButton);

        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

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
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                Map<String, Object> user = new HashMap<>();
                user.put("first", fName);
                user.put("last", lName);
                user.put("email", email);
                user.put("uscID", uscID);
                user.put("affiliation", affiliation);
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + email);
                UploadTask uploadTask = storageRef.putFile(selectedImage);


                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                         if (task.isSuccessful()) {
                             storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                 String imageUrl = uri.toString();
                                 user.put("profileImageURL", selectedImage);
                                 mAuth.createUserWithEmailAndPassword(email, password)
                                         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                             @Override
                                             public void onComplete(@NonNull Task<AuthResult> task) {
                                                 if (task.isSuccessful()) {

                                                     progressBar.setVisibility(View.GONE);
                                                     db.collection("users")
                                                             .add(user)
                                                             .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                 @Override
                                                                 public void onSuccess(DocumentReference documentReference) {
                                                                     Toast.makeText(getActivity(), "Account Created.", Toast.LENGTH_SHORT).show();
                                                                     replaceFragment(new LogInFragment());
                                                                 }


                                                             })
                                                             .addOnFailureListener(new OnFailureListener() {
                                                                 @Override
                                                                 public void onFailure(@NonNull Exception e) {
                                                                     Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_SHORT).show();
                                                                     progressBar.setVisibility(View.GONE);
                                                                 }
                                                             });

                                                     // Sign in success, update UI with the signed-in user's information
//                                    FirebaseUser user = mAuth.getCurrentUser();
                                                 } else {
                                                     // If sign in fails, display a message to the user.
                                                 }
                                             }
                                         });


                             });
                         } else {
                             Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_SHORT).show();
                             progressBar.setVisibility(View.GONE);

                         }
                         // Image uploaded successfully
                         // You can get the download URL of the uploaded image here


                     }

                     ;
                 });
                        // Handle the error if the upload fails




            }
        });

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData(); // Get the selected image URI
            imageView.setImageURI(selectedImage);
            // Now, you can use this URI for uploading or displaying the image.
        }
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, newFragment);
        fragmentTransaction.addToBackStack(null); // Add to back stack for back navigation
        fragmentTransaction.commit();
    }
}