package com.oyespace.guards;

import android.util.Log;
import io.realm.DynamicRealm;
import io.realm.RealmSchema;

public class RealmDataMigration implements io.realm.RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();
        Log.e("OLD_REALM",""+oldVersion);
        Log.e("NEW_REALM",""+newVersion);

    }
}
