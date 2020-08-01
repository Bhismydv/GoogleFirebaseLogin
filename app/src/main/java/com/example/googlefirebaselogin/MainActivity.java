package com.example.googlefirebaselogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    SignInButton signInButton;
    GoogleSignInClient googleSignInAccount;
    FirebaseAuth firebaseAuth;
    Button signOut;
    int RESULT_CODE_SIGNIN=999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth=FirebaseAuth.getInstance();
        //initialization
        signInButton=findViewById(R.id.signInButton);

        signOut=findViewById(R.id.SignOut);
        signOut.setVisibility(View.INVISIBLE);

        //configure signin to request the user's Id,email address and basic
        //profile id and basic profile are include in default_signin

        GoogleSignInOptions gso=
                new GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        //build a googlesignin with the options specified by gso
        googleSignInAccount= GoogleSignIn.getClient(this,gso);

        //attach a onClickListener

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInAccount.signOut();
                signOut.setVisibility(View.INVISIBLE);
                signInButton.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "logged out", Toast.LENGTH_SHORT).show();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInM();
            }
        });
    }

    private void signInM() {
        Intent intent=googleSignInAccount.getSignInIntent();
        startActivityForResult(intent,RESULT_CODE_SIGNIN);
    }


    //here we handle the result of the activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==RESULT_CODE_SIGNIN){
            //just to verify the code
            //create ta task object and use GoogleSignInAccount from Intent and write a separate method to handle signin result
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        //we use try catch block because of exception

        try {
            signInButton.setVisibility(View.INVISIBLE);
            GoogleSignInAccount account=task.getResult(ApiException.class);
            Toast.makeText(this, "Sign In", Toast.LENGTH_SHORT).show();

            //signin successfull now show the authentication
            FirebaseGoogleAuth(account);
        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {

        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);

        //here we are checking the auth credentials and checking tha task is successful or not and display tha messgae

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                    UpdateUI(firebaseUser);
                }else {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //inside update ui we can get the user info and display it when required
    private void UpdateUI(FirebaseUser firebaseUser) {
        signOut.setVisibility(View.VISIBLE);

        //getLastSignedAccount return the account
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if (account!=null){
            String personName=account.getDisplayName();
            String personGivenName=account.getGivenName();
            String personEmail=account.getEmail();
            String personId=account.getId();

            Toast.makeText(this, personId+""+personEmail+""+personGivenName+""+personId, Toast.LENGTH_SHORT).show();
        }
    }
}
