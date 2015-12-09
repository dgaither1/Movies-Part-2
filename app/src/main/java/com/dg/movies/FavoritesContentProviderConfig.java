package com.dg.movies;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

//
// This class works with the Simple SQL library to setup the Content Provider.  The library will generate the needed files
//
@SimpleSQLConfig(
        name = "FavoritesProvider",
        authority = "com.dg.movies.authority",
        database = "favorites.db",
        version = 1)
public class FavoritesContentProviderConfig implements ProviderConfig {

    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}
