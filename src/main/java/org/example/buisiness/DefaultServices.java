package org.example.buisiness;

public interface DefaultServices {
    void SelectClient_And_AddItToRedisDB(String id_client);
    void closeDbConncetion();
    void fetchRedisClient(String clientKey);
    void getCachedData(String clientKey);
    void dataSourceManager(String id_client);
}
