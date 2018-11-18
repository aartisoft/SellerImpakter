package com.impakter.seller.dblocal.realmobject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RoomObjectOl extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;

    public RoomObjectOl() {
    }

    public RoomObjectOl(String id, String title) {
        this.id = id;
        this.name = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}