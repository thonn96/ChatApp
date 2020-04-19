package com.example.appchat.Common;

import com.example.appchat.Holder.QBUserHolder;
import com.quickblox.users.model.QBUser;

import java.util.List;

public class Common {
    public static final String DIALOG_EXTRA = "Dialogs";
    public static String createChatDialogName(List<Integer> qbUser){
        List<QBUser> qbUsers1 = QBUserHolder.getInstance().getUserByIds(qbUser);
        StringBuilder name = new StringBuilder();
        for(QBUser user:qbUsers1){
            name.append(user.getFullName()).append(" ");
        }
        if(name.length()>30){
            name = name.replace(30,name.length()-1,"...");
        }
        return name.toString();
    }
}
