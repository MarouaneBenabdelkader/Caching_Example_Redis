package org.example.dao;

public interface CachedDataManagerDao {

     void selectClientFromMySqlDB_And_AddItToRedisDB(String no_client);
     void closeConnection();
     void fetchClientFromRedisDataBase(String clientKey);
    void useCacheData(String clientKey);
    void dataSelector(String clientId);

}
