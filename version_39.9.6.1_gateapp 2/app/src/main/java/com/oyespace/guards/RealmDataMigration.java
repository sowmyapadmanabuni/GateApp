package com.oyespace.guards;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

public class RealmDataMigration implements io.realm.RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();
        Log.e("OLD_REALM", "" + oldVersion);
        Log.e("NEW_REALM", "" + newVersion);

        /*
        if (oldVersion == 2) {
            schema.create("ExitVisitorLog")
                    .addField("vlVisLgID", int.class, FieldAttribute.PRIMARY_KEY)
                    .addField("reRgVisID", int.class)
                    .addField("mEMemID", int.class)
                    .addField("vlfName", String.class)
                    .addField("vllName", String.class)
                    .addField("vlMobile", String.class)
                    .addField("vlVisType", String.class)
                    .addField("vlComName", String.class)
                    .addField("vLPOfVis", String.class)
                    .addField("vlVisCnt", int.class)
                    .addField("vLVehNum", String.class)
                    .addField("vLVehType", String.class)
                    .addField("vLItmCnt", int.class)
                    .addField("unUniName", String.class)
                    .addField("vLVerStat", String.class)
                    .addField("vLGtName", String.class)
                    .addField("uNUnitID", int.class)
                    .addField("asAssnID", int.class)
                    .addField("vlEntryT", String.class)
                    .addField("vlExitT", String.class)
                    .addField("vldCreated", String.class)
                    .addField("vldUpdated", String.class)
                    .addField("vlEntryImg", String.class);
        }
         */

    }
}
