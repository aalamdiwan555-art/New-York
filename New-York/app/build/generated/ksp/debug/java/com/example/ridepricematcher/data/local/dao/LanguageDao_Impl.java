package com.example.ridepricematcher.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.ridepricematcher.data.local.Converters;
import com.example.ridepricematcher.data.local.entity.CachedLanguageEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LanguageDao_Impl implements LanguageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CachedLanguageEntity> __insertionAdapterOfCachedLanguageEntity;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public LanguageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCachedLanguageEntity = new EntityInsertionAdapter<CachedLanguageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `languages` (`id`,`locale`,`displayName`,`displayNameNative`,`aliases`,`enabled`,`priceKeywords`,`distanceKeywords`,`durationKeywords`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CachedLanguageEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getLocale());
        statement.bindString(3, entity.getDisplayName());
        statement.bindString(4, entity.getDisplayNameNative());
        final String _tmp = __converters.fromStringList(entity.getAliases());
        statement.bindString(5, _tmp);
        final int _tmp_1 = entity.getEnabled() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        final String _tmp_2 = __converters.fromStringList(entity.getPriceKeywords());
        statement.bindString(7, _tmp_2);
        final String _tmp_3 = __converters.fromStringList(entity.getDistanceKeywords());
        statement.bindString(8, _tmp_3);
        final String _tmp_4 = __converters.fromStringList(entity.getDurationKeywords());
        statement.bindString(9, _tmp_4);
        statement.bindLong(10, entity.getSyncedAt());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM languages";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<CachedLanguageEntity> languages,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCachedLanguageEntity.insert(languages);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CachedLanguageEntity>> getEnabledLanguages() {
    final String _sql = "SELECT * FROM languages WHERE enabled = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"languages"}, new Callable<List<CachedLanguageEntity>>() {
      @Override
      @NonNull
      public List<CachedLanguageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLocale = CursorUtil.getColumnIndexOrThrow(_cursor, "locale");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfDisplayNameNative = CursorUtil.getColumnIndexOrThrow(_cursor, "displayNameNative");
          final int _cursorIndexOfAliases = CursorUtil.getColumnIndexOrThrow(_cursor, "aliases");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfPriceKeywords = CursorUtil.getColumnIndexOrThrow(_cursor, "priceKeywords");
          final int _cursorIndexOfDistanceKeywords = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceKeywords");
          final int _cursorIndexOfDurationKeywords = CursorUtil.getColumnIndexOrThrow(_cursor, "durationKeywords");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final List<CachedLanguageEntity> _result = new ArrayList<CachedLanguageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CachedLanguageEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLocale;
            _tmpLocale = _cursor.getString(_cursorIndexOfLocale);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpDisplayNameNative;
            _tmpDisplayNameNative = _cursor.getString(_cursorIndexOfDisplayNameNative);
            final List<String> _tmpAliases;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfAliases);
            _tmpAliases = __converters.toStringList(_tmp);
            final boolean _tmpEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp_1 != 0;
            final List<String> _tmpPriceKeywords;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPriceKeywords);
            _tmpPriceKeywords = __converters.toStringList(_tmp_2);
            final List<String> _tmpDistanceKeywords;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDistanceKeywords);
            _tmpDistanceKeywords = __converters.toStringList(_tmp_3);
            final List<String> _tmpDurationKeywords;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfDurationKeywords);
            _tmpDurationKeywords = __converters.toStringList(_tmp_4);
            final long _tmpSyncedAt;
            _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            _item = new CachedLanguageEntity(_tmpId,_tmpLocale,_tmpDisplayName,_tmpDisplayNameNative,_tmpAliases,_tmpEnabled,_tmpPriceKeywords,_tmpDistanceKeywords,_tmpDurationKeywords,_tmpSyncedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CachedLanguageEntity>> getAllLanguages() {
    final String _sql = "SELECT * FROM languages";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"languages"}, new Callable<List<CachedLanguageEntity>>() {
      @Override
      @NonNull
      public List<CachedLanguageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLocale = CursorUtil.getColumnIndexOrThrow(_cursor, "locale");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfDisplayNameNative = CursorUtil.getColumnIndexOrThrow(_cursor, "displayNameNative");
          final int _cursorIndexOfAliases = CursorUtil.getColumnIndexOrThrow(_cursor, "aliases");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfPriceKeywords = CursorUtil.getColumnIndexOrThrow(_cursor, "priceKeywords");
          final int _cursorIndexOfDistanceKeywords = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceKeywords");
          final int _cursorIndexOfDurationKeywords = CursorUtil.getColumnIndexOrThrow(_cursor, "durationKeywords");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final List<CachedLanguageEntity> _result = new ArrayList<CachedLanguageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CachedLanguageEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLocale;
            _tmpLocale = _cursor.getString(_cursorIndexOfLocale);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpDisplayNameNative;
            _tmpDisplayNameNative = _cursor.getString(_cursorIndexOfDisplayNameNative);
            final List<String> _tmpAliases;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfAliases);
            _tmpAliases = __converters.toStringList(_tmp);
            final boolean _tmpEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp_1 != 0;
            final List<String> _tmpPriceKeywords;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPriceKeywords);
            _tmpPriceKeywords = __converters.toStringList(_tmp_2);
            final List<String> _tmpDistanceKeywords;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfDistanceKeywords);
            _tmpDistanceKeywords = __converters.toStringList(_tmp_3);
            final List<String> _tmpDurationKeywords;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfDurationKeywords);
            _tmpDurationKeywords = __converters.toStringList(_tmp_4);
            final long _tmpSyncedAt;
            _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            _item = new CachedLanguageEntity(_tmpId,_tmpLocale,_tmpDisplayName,_tmpDisplayNameNative,_tmpAliases,_tmpEnabled,_tmpPriceKeywords,_tmpDistanceKeywords,_tmpDurationKeywords,_tmpSyncedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
