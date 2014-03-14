package com.joker.livingstone.util;

import android.database.sqlite.SQLiteDatabase;

public class DBHelper {
	private static final String DATABASE_PATH = "/data/data/com.joker.livingstone/lib/libHS.db.so";

	private static ThreadLocal<SQLiteDatabase> local = new ThreadLocal<SQLiteDatabase>() {
		@Override
		public SQLiteDatabase get() {
			return SQLiteDatabase.openDatabase(DATABASE_PATH, null,
					SQLiteDatabase.OPEN_READONLY
							| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			// return super.get();
		}
	};

	public static SQLiteDatabase get() {
		return local.get();
		// return SQLiteDatabase.openDatabase(DATABASE_PATH, null,
		// SQLiteDatabase.OPEN_READONLY
		//					| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}
}
