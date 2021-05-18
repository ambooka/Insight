package com.msah.insight.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msah.insight.MainActivity;
import com.msah.insight.R;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button login, register;
    EditText userName, email, password, confirmPassword;
    private DatabaseReference reference;
    private NavController navController;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        MainActivity.fragClass = getClass();
        navController = NavHostFragment.findNavController(this);
        checkUser();
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        register = (Button)view.findViewById(R.id.btn_register);
        login = (Button)view.findViewById(R.id.btn_login);
        userName = (EditText)view.findViewById(R.id.tv_username);
        email = (EditText)view.findViewById(R.id.tv_email);
        password = (EditText)view.findViewById(R.id.tv_password);
        confirmPassword = (EditText)view.findViewById(R.id.tv_confirmPass);

        auth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername = Objects.requireNonNull(userName.getText()).toString();
                String txtEmail = Objects.requireNonNull(email.getText()).toString();
                String txtPassword = Objects.requireNonNull(password.getText()).toString();

                if(TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword))
                    Toast.makeText(getContext(),"All fields are required", Toast.LENGTH_SHORT).show();
                else if(txtPassword.length() < 6 )
                    Toast.makeText(getContext(),"password must be must be a least 6 characters", Toast.LENGTH_SHORT).show();
                else
                    signUp(txtUsername,txtEmail,txtPassword);

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail = Objects.requireNonNull(email.getText()).toString();
                String txtPassword = Objects.requireNonNull(password.getText()).toString();

                if(TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword))
                    Toast.makeText(getContext(),"All fields are required", Toast.LENGTH_SHORT).show();
                else
                {
                    auth.signInWithEmailAndPassword(txtEmail,txtPassword).addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {

                            navController.navigate(R.id.action_Login_to_homePage);

                        }
                        else
                            Toast.makeText(getContext(),"Email or password not valid ", Toast.LENGTH_SHORT).show();

                    });
                }
            }
        });


        return view;
    }
    private void signUp(String userName,String email, String password)
    {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                FirebaseUser firebaseUser = auth.getCurrentUser();

                String userId = firebaseUser.getUid();
                reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                HashMap<String, String > hashMap = new HashMap<>();
                hashMap.put("id", userId);
                hashMap.put("userName", userName);
                hashMap.put("imageURL", "default");
                hashMap.put("status","offline");
                hashMap.put("search",userName.toLowerCase());

                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            //navigate navController.navigate(R.id.action_register_to_homePage);
                            navController.navigate(R.id.action_Login_to_homePage);


                        }

                    }
                });
            }
            else
                Toast.makeText(getContext(), "register not success", Toast.LENGTH_SHORT).show();
        });
    }


    private void checkUser()
    {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
        {
            firebaseUser.getUid();
            navController.navigate(R.id.action_Login_to_homePage);
        }
    }

}