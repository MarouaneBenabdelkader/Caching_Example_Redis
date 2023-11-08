package org.example;

import org.example.models.CachedDataManager;

public class Main {
    public static void main(String[] args) {
        CachedDataManager cdatamanager = new CachedDataManager();
        cdatamanager.DataSelector("3"); // Initie la sélection des données
    }
}