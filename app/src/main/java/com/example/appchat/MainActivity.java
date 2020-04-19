package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class MainActivity extends AppCompatActivity {

    static final String APP_ID ="81656";
    static final String AUTH_KEY ="aBSyT5gqfuUVpcG";
    static final String AUTH_SECRET ="VuRzSts8De4W9n7";
    static final String ACCOUNT_KEY ="xGvq6AHMYnxb_XTuT_FS";

    Button btn_Login,btn_singUp;
    EditText edtUser,edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initalizeFramwork();

        btn_Login = (Button)findViewById(R.id.main_btnLogin);
        btn_singUp = (Button)findViewById(R.id.main_btnSignUp);
        edtUser = (EditText)findViewById(R.id.main_edtLogin);
        edtPassword = (EditText)findViewById(R.id.main_edtPassword);

        btn_singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SingUpActivity.class));
            }
        });

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = edtUser.getText().toString();
                final String password = edtPassword.getText().toString();
                QBUser qbUser = new QBUser(user,password);

                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext(),"login Success",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this,ChatDialogsActivity.class);
                        intent.putExtra("user",user);
                        intent.putExtra("password",password);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void initalizeFramwork() {
        QBSettings.getInstance().init(getApplicationContext(),APP_ID,AUTH_KEY,AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

    }
}
