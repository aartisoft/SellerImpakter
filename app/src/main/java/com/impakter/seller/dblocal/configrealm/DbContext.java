package com.impakter.seller.dblocal.configrealm;

import android.content.Context;

import com.impakter.seller.dblocal.realmobject.ContactObj;
import com.impakter.seller.dblocal.realmobject.RecentSearchObj;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Minh Toan on 15/09/2018
 */

public class DbContext {
    private Realm realm;

    public DbContext(Context context) {
        Realm.init(context);
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context)
//                .name(Realm.DEFAULT_REALM_NAME)
//                .schemaVersion(0)
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();

    }

    private static DbContext instance;

    public static DbContext getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new DbContext(context);
        }
    }

//    public void addItemToCart(final CartItemRealmObj object) {
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.copyToRealmOrUpdate(object);
//
//            }
//        });
//    }
//
//    public CartItemRealmObj getCartByKey(String primaryKey) {
//        CartItemRealmObj cartItemRealmObj = realm.where(CartItemRealmObj.class).equalTo("primaryKeyItem", primaryKey).findFirst();
//        return cartItemRealmObj;
//    }
//
//    public List<CartItemRealmObj> getCartItems() {
//        RealmResults<CartItemRealmObj> dataRealmResults =
//                realm.where(CartItemRealmObj.class).findAll();
//        return dataRealmResults;
//    }
//
//    //===============
//    boolean isDeleted;
//
//    public boolean deleteCartItem(String primaryKeyItem) {
//        final RealmResults<CartItemRealmObj> results = realm.where(CartItemRealmObj.class)
//                .equalTo("primaryKeyItem", primaryKeyItem).findAll();
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                isDeleted = results.deleteAllFromRealm();
//            }
//        });
//        return isDeleted;
//    }
//
//    public boolean deleteAllCartItem() {
//        final RealmResults<CartItemRealmObj> results = realm.where(CartItemRealmObj.class).findAll();
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                isDeleted = results.deleteAllFromRealm();
//            }
//        });
//        return isDeleted;
//    }
//
//    public boolean isExist(final String primaryKey) {
//        RealmResults<CartItemRealmObj> dataRealmResults =
//                realm.where(CartItemRealmObj.class).equalTo("primaryKeyItem", primaryKey).findAll();
//        return dataRealmResults.size() != 0;
//    }

    public void addToRecentSearch(final RecentSearchObj keyword) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(keyword);
            }
        });
    }

    public List<RecentSearchObj> getRecentSearch() {
        RealmResults<RecentSearchObj> dataRealmResults =
                realm.where(RecentSearchObj.class).findAll();
        return dataRealmResults;
    }

    private boolean isDeleted;

    public boolean deleteRecentSearcdItem(String primaryKey) {
        final RealmResults<RecentSearchObj> results = realm.where(RecentSearchObj.class)
                .equalTo("keyWord", primaryKey).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                isDeleted = results.deleteAllFromRealm();
            }
        });
        return isDeleted;
    }

    public void addContactToDB(final ContactObj email) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(email);
            }
        });
    }

    public List<ContactObj> getContacts() {
        RealmResults<ContactObj> dataRealmResults =
                realm.where(ContactObj.class).findAll();
        return dataRealmResults;
    }

    public boolean deleteContact(String email) {
        final RealmResults<ContactObj> results = realm.where(ContactObj.class)
                .equalTo("email", email).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                isDeleted = results.deleteAllFromRealm();
            }
        });
        return isDeleted;
    }
}
