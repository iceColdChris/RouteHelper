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
package icecoldchris.routehelper.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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
 * Register activity is the activity that will allow users to create a
 * new account on the web server, provided they enter a valid email and
 * their passwords match.
 *
 * @author Chris Fahlin
 * @version %I%, %G%
 * @since 1.0
 */
public class RegisterActivity extends Activity {

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
        setContentView(R.layout.activity_register);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("AUTH:", "onAuthStateChanged:signed_in:" + user.getUid());
                    startActivity(new Intent(RegisterActivity.this, RouteActivity.class));
                } else {
                    // User is signed out
                    Log.d("AUTH:", "onAuthStateChanged:signed_out");
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
     * This method is called when the user hits the button called register,
     * it will check that the passwords match and the email is valid then create
     * a new user on the web server.
     */
    public void onRegister(View view) {
        String email = ((EditText) findViewById(R.id.reg_email))
                .getText().toString();
        String password = ((EditText) findViewById(R.id.reg_password))
                .getText().toString();
        String confirmPassword = ((EditText) findViewById(R.id.reg_confirm_password))
                .getText().toString();


        if (isValidEmail(email)) /* Check to make sure the user enters a valid email */ {
            if (password.equals(confirmPassword)) /* Check to make sure the user entered the same passwords. */ {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("AUTH:", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Could not create user!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } else/* If the passwords don't match. */ {
                Toast.makeText(RegisterActivity.this, "Please check that your passwords match!", Toast.LENGTH_SHORT).show();
            }
        } else/* If the user does not enter a valid email string */ {
            Toast.makeText(RegisterActivity.this, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method is called when the user hits the button labeled cancel,
     * it will bring the user back to the login screen.
     */
    public void onCancel(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * This will check that the user has entered a valid email.
     *
     * @param target the email string
     * @return True if the email is valid, False if the email is not valid
     */
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
