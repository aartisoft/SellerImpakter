package com.impakter.seller.dblocal.Convertobject;

import com.impakter.seller.dblocal.realmobject.ContactObj;
import com.impakter.seller.dblocal.realmobject.RecentSearchObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 11/10/2017.
 */

public class ConvertObject {
    // TODO: 17/09/2018 Toan

    public static ArrayList<String> convertRealmStringToString(List<RecentSearchObj> recentSearchObjs) {
        ArrayList<String> listKeyWord = new ArrayList<>();
        for (RecentSearchObj item : recentSearchObjs) {
            listKeyWord.add(item.getKeyWord());
        }
        return listKeyWord;
    }
    public static ArrayList<String> convertContactObjToString(List<ContactObj> contactObjs) {
        ArrayList<String> listEmail = new ArrayList<>();
        for (ContactObj item : contactObjs) {
            listEmail.add(item.getEmail());
        }
        return listEmail;
    }
}
