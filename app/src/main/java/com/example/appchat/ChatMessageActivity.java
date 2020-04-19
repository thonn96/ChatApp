package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.appchat.Adapter.ChatMessageAdapter;
import com.example.appchat.Common.Common;
import com.example.appchat.Holder.QBChatMessageHolder;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChatMessageActivity extends AppCompatActivity {
    QBChatDialog qbChatDialog;
    ListView listViewChatMessages;
    ImageButton submitButton;
    EditText edtContent;
    ChatMessageAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        initViews();
        initChatDialogs();
        retrieveMessage();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QBChatMessage chatMessage = new QBChatMessage();
                chatMessage.setBody(edtContent.getText().toString());
                chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                chatMessage.setSaveToHistory(true);
                //qbChatDialog.sendMessage(chatMessage);
                sendMessage(chatMessage);
                //put message to cache
                QBChatMessageHolder.getInstance().putMessage(qbChatDialog.getDialogId(),chatMessage);
                ArrayList<QBChatMessage> messages = QBChatMessageHolder.getInstance().getChatMessageByDialogId(qbChatDialog.getDialogId());
                adapter = new ChatMessageAdapter(getBaseContext(),messages);
                listViewChatMessages.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //remove text to edti text
                edtContent.setText("");
                edtContent.setFocusable(true);
            }
        });
    }

    private  void sendMessage(final QBChatMessage chatMessage){
        qbChatDialog.sendMessage(chatMessage, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.e("SEND_SUCCESS", chatMessage.getBody());
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("SEND_FAIL", e.getMessage());
            }
        });
    }

    private void retrieveMessage() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500);
        if(qbChatDialog != null){
            QBRestChatService.getDialogMessages(qbChatDialog,messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    Toast.makeText(getBaseContext(),"retrieveMessage Success",Toast.LENGTH_LONG).show();
                    // Put message to cache
                    QBChatMessageHolder.getInstance().putMessages(qbChatDialog.getDialogId(),qbChatMessages);
                    adapter = new ChatMessageAdapter(getBaseContext(),qbChatMessages);
                    listViewChatMessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
    }


    private void initChatDialogs() {
        qbChatDialog = (QBChatDialog)getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
        qbChatDialog.initForChat(QBChatService.getInstance());
        //Register listener Incoming Message
        QBIncomingMessagesManager incomingMessages = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessages.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                Toast.makeText(getBaseContext(),"processMessage Success",Toast.LENGTH_LONG).show();
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
                Toast.makeText(getBaseContext(),"processMessage fail",Toast.LENGTH_LONG).show();
            }
        });
        Toast.makeText(getBaseContext(),"initChatDialogs ",Toast.LENGTH_LONG).show();
        qbChatDialog.addMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                Toast.makeText(getBaseContext(),"initChatDialogs Success",Toast.LENGTH_LONG).show();//e chỉ o
                //cache message
                QBChatMessageHolder.getInstance().putMessage(qbChatMessage.getDialogId(),qbChatMessage);
                ArrayList<QBChatMessage> messages = QBChatMessageHolder.getInstance().getChatMessageByDialogId(qbChatMessage.getDialogId());
                adapter = new ChatMessageAdapter(getBaseContext(),messages);
                listViewChatMessages.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
                Log.e("ERROR",e.getMessage());
            }
        });
    }

    private void initViews() {
        listViewChatMessages = (ListView)findViewById(R.id.list_of_message);
        submitButton = (ImageButton)findViewById(R.id.send_button);
        edtContent = (EditText)findViewById(R.id.edt_content);


    }
}
