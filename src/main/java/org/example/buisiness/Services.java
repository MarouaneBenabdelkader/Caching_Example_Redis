package org.example.buisiness;

import org.example.dao.CachedDataManagerDao;
import org.example.dao.CachedDataManagerSql;

public class Services implements DefaultServices {
    private static DefaultServices instance = null;
    private CachedDataManagerDao cachedDataManagerDao;

    private Services(CachedDataManagerDao cachedDataManagerInstance) {
        this.cachedDataManagerDao = cachedDataManagerInstance;
    }
    public static DefaultServices  getInstance(){
        if (instance == null) {
            instance = new Services(new CachedDataManagerSql());
        }
        return instance;
    }

    @Override
    public void SelectClient_And_AddItToRedisDB(String id_client) {
        cachedDataManagerDao.selectClientFromMySqlDB_And_AddItToRedisDB(id_client);
    }

    @Override
    public void closeDbConncetion() {
        cachedDataManagerDao.closeConnection();
    }

    @Override
    public void fetchRedisClient(String clientKey) {
        cachedDataManagerDao.fetchClientFromRedisDataBase(clientKey);
    }

    @Override
    public void getCachedData(String clientKey) {
        cachedDataManagerDao.useCacheData(clientKey);
    }

    @Override
    public void dataSourceManager(String id_client) {
         cachedDataManagerDao.dataSelector(id_client);
    }
}
