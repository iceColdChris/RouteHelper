/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package icecoldchris.routehelper.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import icecoldchris.routehelper.R;

/**
 * Login activity is the main activity of the Route Helper app,
 * it allows the user to login with their credentials, register a new account,
 * or reset the password for their current account.
 *
 * @author Chris Fahlin
 * @version %I%, %G%
 * @since 1.0
 */
public class LoginActivity extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * {@inheritDoc}
     * <p/>
     * On-top of the original functionality this method will
     * also setup the required Firebase connections.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Setup Firebase context
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("AUTH", "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(LoginActivity.this, "Authentication Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, RouteActivity.class));
                } else {
                    // User is signed out
                    Log.d("AUTH", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     * <p/>
     * On-top of the original functionality this method will
     * attach an authentication listener to the activity.
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * On-top of the original functionality this method will
     * remove the authentication listener from the activity.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * This method is called when the button labeled register is clicked,
     * It will move the user over to the register page where they can create
     * a new account.
     */
    public void registerClicked(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    /**
     * This method is called when the button labeled login is clicked,
     * It will try to authenticate the user and move them to the map screen
     * upon successful login.
     */
    public void authenticateLogin(View view) {

        String email = ((EditText) findViewById(R.id.email))
                .getText().toString();
        String password = ((EditText) findViewById(R.id.password))
                .getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("AUTH", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("AUTH", "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed, please check your Email or Password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * This method is called when the button labeled forgot is clicked,
     * It will send an email to whatever is currently typed into the email
     * field and allow the user to change their password.
     */
    public void onForgotPasswordClicked(View view) {

        String email = ((EditText) findViewById(R.id.email))
                .getText().toString();

        if (!email.isEmpty())/* The user has typed in their email */ {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("RECOVERY:", "Email sent.");
                                Toast.makeText(LoginActivity.this, "Email Sent!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else /* Email text field is empty */ {
            Toast.makeText(LoginActivity.this, "Please enter your email at least!", Toast.LENGTH_SHORT).show();
        }

    }
}
