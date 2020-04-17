package com.example.appchat;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SingUpActivity extends AppCompatActivity {

    Button btnSingUp, btnCancel;
    EditText edtUser,edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        registerSession();
        
        
        btnSingUp = (Button)findViewById(R.id.signUp_btnSignUp);
        btnCancel = (Button)findViewById(R.id.singUp_btnCancel);
        edtUser = (EditText)findViewById(R.id.singup_edtLogin);
        edtPassword = (EditText)findViewById(R.id.singup_edtPassword);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSingUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"OnClick",Toast.LENGTH_LONG).show();
                String user = edtUser.getText().toString();
                String password = edtPassword.getText().toString();
                QBUser qbUser = new QBUser(user,password);

                QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext(),"signUp Success",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(),"error"+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void registerSession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
             //   Toast.makeText(getBaseContext(),"login Success11",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(QBResponseException e) {
              //  Toast.makeText(getBaseContext(),"error11",Toast.LENGTH_LONG).show();
            }
        });
    }

}
