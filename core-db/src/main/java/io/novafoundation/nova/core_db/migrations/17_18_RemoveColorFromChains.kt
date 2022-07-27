package io.novafoundation.nova.core_db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val RemoveColorFromChains_17_18 = object : Migration(17, 18) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()

        // rename
        database.execSQL("ALTER TABLE chains RENAME TO chains_old")

        // new table
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `chains` (
            `id` TEXT NOT NULL,
            `parentId` TEXT,
            `name` TEXT NOT NULL,
            `icon` TEXT NOT NULL,
            `prefix` INTEGER NOT NULL,
            `isEthereumBased` INTEGER NOT NULL,
            `isTestNet` INTEGER NOT NULL,
            `hasCrowdloans` INTEGER NOT NULL,
            `additional` TEXT,
            `url` TEXT,
            `overridesCommon` INTEGER,
            `staking_url` TEXT,
            `staking_type` TEXT,
            `history_url` TEXT,
            `history_type` TEXT,
            `crowdloans_url` TEXT,
            `crowdloans_type` TEXT,
            PRIMARY KEY(`id`)
        )
            """.trimIndent()
        )

        // insert to new from old
        database.execSQL(
            // select all but color
            """
            INSERT INTO chains
            SELECT id, parentId, name, icon, prefix, isEthereumBased, isTestNet, hasCrowdloans, additional, url, overridesCommon,
             staking_url, staking_type, history_url, history_type, crowdloans_url, crowdloans_type
            FROM chains_old
            """.trimIndent()
        )

        // delete old
        database.execSQL("DROP TABLE chains_old")

        database.setTransactionSuccessful()
        database.endTransaction()
    }
}
