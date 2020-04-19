package com.example.appchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.appchat.Adapter.ChatDialogsAdapter;
import com.example.appchat.Common.Common;
import com.example.appchat.Holder.QBUserHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
public class ChatDialogsActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    ListView listViewChatDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialogs);

        listViewChatDialog = (ListView)findViewById(R.id.listView_ChatDialogs) ;
        floatingActionButton = findViewById(R.id.chatDialogs_addUser);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDialogsActivity.this,ListUserActivity.class);
                startActivity(intent);
            }
        });
    createSessionForChat();

    listViewChatDialog = (ListView)findViewById(R.id.listView_ChatDialogs);
    listViewChatDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            QBChatDialog qbChatDialog = (QBChatDialog)listViewChatDialog.getAdapter().getItem(position);
            Intent intent = new Intent(ChatDialogsActivity.this, ChatMessageActivity.class);
            intent.putExtra(Common.DIALOG_EXTRA,qbChatDialog);
            startActivity(intent);

        }
    });
    loadChatDialogs();

    }

    private void loadChatDialogs() {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);
        QBRestChatService.getChatDialogs(null,requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                ChatDialogsAdapter adapter = new ChatDialogsAdapter(getBaseContext(),qbChatDialogs);
                listViewChatDialog.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR",e.getMessage());
            }
        });

    }


    private void createSessionForChat() {
        final ProgressDialog mDialog = new ProgressDialog(ChatDialogsActivity.this);
        mDialog.setMessage("please waiting...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        String user,password;
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");
        //Load all user and save cache
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUserHolder.getInstance().putUsers(qbUsers);
                Toast.makeText(getBaseContext(),"createSessionForChat111 Success",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });


        final QBUser qbUser = new QBUser(user,password);

        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }
                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        mDialog.dismiss();

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR",""+e.getMessage());
                    }
                });
            }



            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

}
