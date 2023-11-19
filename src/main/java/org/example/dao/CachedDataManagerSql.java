package org.example.dao;

import org.example.models.Client;
import redis.clients.jedis.Jedis;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CachedDataManagerSql implements CachedDataManagerDao {
    private final   Jedis jedis;
    private Client cachedClient;
    private Connection connection;
    static final private String url = "jdbc:mysql://localhost:3306/bigdata";
    static final private String username = "root";
    static final private String password = "";
    public CachedDataManagerSql() {
        this.jedis = new Jedis("localhost", 6379);
        connectToMySql();
    }
    private void connectToMySql() {
        System.out.println("Connecting database...");

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    @Override
    public void selectClientFromMySqlDB_And_AddItToRedisDB(String no_client) {
        try {
            // Mesure du temps pour la récupération depuis MySQL
            long startTimeSql = System.currentTimeMillis();
            ResultSet resultSet;
            // Sélection des données du client depuis la base de données MySQL
            String sqlQuery = "SELECT * FROM client WHERE no_client = ?";
            PreparedStatement pstmt = connection.prepareStatement(sqlQuery);
            pstmt.setString(1, no_client);
            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                cachedClient = new Client(resultSet.getInt("no_client"), resultSet.getString("nom"), resultSet.getString("adresse"));
                // Préparation des données pour Redis
                Map<String, String> hash = new HashMap<>();
                hash.put("id_client", String.valueOf(cachedClient.getNo_client()));
                hash.put("nom", cachedClient.getName());
                hash.put("adresse", cachedClient.getAdress());

                // Stockage dans Redis
                jedis.hset("client:" + no_client, hash);
            }
            cachedClient = null;
            long stopTimeSql = System.currentTimeMillis();
            long elapsedTimeSql = stopTimeSql - startTimeSql;

            // Mesure du temps pour la récupération depuis Redis
            long startTimeRedis = System.currentTimeMillis();

            // Récupération des données depuis Redis

            Map<String, String> insertedData = jedis.hgetAll("client:" + no_client);
            if (insertedData.isEmpty()) {
                System.out.println("Failed to insert into Redis or retrieval issue.");
            } else {
                System.out.println("Data verified in Redis: " + insertedData);
            }
            long stopTimeRedis = System.currentTimeMillis();
            long elapsedTimeRedis = stopTimeRedis - startTimeRedis;

            System.out.println("Temps de récupération depuis MySQL: " + elapsedTimeSql + " ms");
            System.out.println("Temps de récupération depuis Redis: " + elapsedTimeRedis + " ms");

            // Utilisation des données récupérées si nécessaire...

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void fetchClientFromRedisDataBase(String clientKey) {
        Map<String, String> mapcl = jedis.hgetAll(clientKey);
        if (!mapcl.isEmpty()) {
            try {
                // Assurez que le champ 'no_client' n'est pas null et que c'est un entier valide.
                String noClientStr = mapcl.get("id_client");
                if (noClientStr != null) {
                    int noClient = Integer.parseInt(noClientStr);
                    String nom = mapcl.get("nom");
                    String adresse = mapcl.get("adresse");

                    cachedClient = new Client(noClient, nom, adresse);
                    System.out.println("==> Client: {id=" + cachedClient.getNo_client() +
                            ", nom=" + cachedClient.getName() +
                            ", adresse=" + cachedClient.getAdress() + "}");
                } else {
                    System.out.println("No client ID found in Redis for key: " + clientKey);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error parsing client ID from Redis data: " + e.getMessage());
            }
        } else {
            System.out.println("Client not found in Redis with key: " + clientKey);
        }
    }
    @Override
    public void useCacheData(String clientKey) {
        if (cachedClient == null) {
            fetchClientFromRedisDataBase(clientKey);
        } else {
            // Utiliser les données mises en cache de cachedClient
            // Par exemple, afficher les informations ou les utiliser dans une logique métier
            System.out.println("Using cached data for client: {id=" + cachedClient.getNo_client() +
                    ", nom=" + cachedClient.getName() +
                    ", adresse=" + cachedClient.getAdress() + "}");
        }
    }
    @Override
    public void dataSelector(String clientId) {
        String clientKey = "client:" + clientId; // La clé sous laquelle les données du client sont stockées dans Redis

        // Essayer d'utiliser les données en cache
        useCacheData(clientKey);

        // Si les données du client ne sont pas en cache, les sélectionner de MySQL et les ajouter à Redis
        if (cachedClient == null) {
            selectClientFromMySqlDB_And_AddItToRedisDB(clientId);
        }
        cachedClient = null;
    }
    @Override
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot close the connection!", ex);
        }
    }
}
