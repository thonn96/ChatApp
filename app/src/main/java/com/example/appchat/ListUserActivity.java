package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.appchat.Adapter.ListUserAdapter;
import com.example.appchat.Common.Common;
import com.example.appchat.Holder.QBUserHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListUserActivity extends AppCompatActivity {
    ListView listViewUsers;
    Button btnCreateChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        retrieveAllUser();
        listViewUsers = (ListView)findViewById(R.id.listViewUsers);
        listViewUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        btnCreateChat = (Button)findViewById(R.id.btn_create_chat);
        btnCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countChoice = listViewUsers.getCount();
                if(listViewUsers.getCheckedItemPositions().size() == 1 ){
                    createPrivateChat(listViewUsers.getCheckedItemPositions());
                }else if (listViewUsers.getCheckedItemPositions().size() > 1){
                    createGroupChat(listViewUsers.getCheckedItemPositions());
                }
            }
        });
    }
    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mDialog = new ProgressDialog(ListUserActivity.this);
        mDialog.setMessage("please waiting...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        int countChoice = listViewUsers.getCount();
        ArrayList<Integer> occupantIdList = new ArrayList<>();
        for(int i = 0 ;i < countChoice; i++){
            if(checkedItemPositions.get(i)){
                QBUser user = (QBUser)listViewUsers.getItemAtPosition(i);
                occupantIdList.add(user.getId());
            }
        }
        // Create Chat Dialog
        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(Common.createChatDialogName(occupantIdList));
        dialog.setType(QBDialogType.PRIVATE);
        dialog.setOccupantsIds(occupantIdList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mDialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR",e.getMessage());
            }
        });
    }

    private void createGroupChat(SparseBooleanArray checkedItemPositions) {

        final ProgressDialog mDialog = new ProgressDialog(ListUserActivity.this);
        mDialog.setMessage("please waiting...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        int countChoice = listViewUsers.getCount();

        for(int i = 0 ;i < countChoice; i++){
            if(checkedItemPositions.get(i)){
                QBUser user = (QBUser)listViewUsers.getItemAtPosition(i);
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());
                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        mDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Create private chat Dialog Success",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR",e.getMessage());
                    }
                });
            }
        }
    }

    private void retrieveAllUser() {
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {

            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                //add to Cache
                QBUserHolder.getInstance().putUsers(qbUsers);

                ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<QBUser>();
                for (QBUser user : qbUsers) {
                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin()))
                        qbUserWithoutCurrent.add(user);

                }

                //create adapter show list
                ListUserAdapter adapter = new ListUserAdapter(getBaseContext(), qbUserWithoutCurrent);
                listViewUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }

}

