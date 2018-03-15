package com.yemyatthu.restaurantbooker.data.cache;

import com.yemyatthu.restaurantbooker.data.entity.TownshipEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.realm.Realm;


/**
 * Created by Ye Myat Thu on 2/8/2017.
 */


public class TownshipCacheImpl implements TownshipCache {
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;

    @Override
    public boolean isExpired() {
        Realm realm = Realm.getDefaultInstance();
        if (realm.where(TownshipEntity.class).count() != 0) {
            Date currentTime = new Date(System.currentTimeMillis());
            SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
            Date lastUpdated = null;
            try {
                lastUpdated = ISO8601DATEFORMAT.parse(realm.where(TownshipEntity.class).findFirst().getLastUpdated());
                boolean isExpired = currentTime.getTime() - lastUpdated.getTime() > EXPIRATION_TIME;
                if(isExpired){
                    realm.beginTransaction();
                    realm.delete(TownshipEntity.class);
                    realm.commitTransaction();
                    realm.close();
                }
                return isExpired;
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    @Override
    public boolean isCached() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(TownshipEntity.class).findAll() != null && realm.where(TownshipEntity.class).findAll().size() > 0;
    }

    @Override
    public Observable<List<TownshipEntity>> get() {
        List<TownshipEntity> townshipEntities = Realm.getDefaultInstance().where(TownshipEntity.class).findAll();
        return Observable.just(townshipEntities);
    }

    @Override
    public void put(List<TownshipEntity> townshipEntities) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(townshipEntities);
        realm.commitTransaction();
        realm.close();
    }
}
