package com.impakter.seller.dblocal.realmobject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ContactObj extends RealmObject {
    @PrimaryKey
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
