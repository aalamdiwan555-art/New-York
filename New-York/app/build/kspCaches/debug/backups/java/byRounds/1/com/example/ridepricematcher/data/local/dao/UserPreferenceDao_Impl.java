package com.example.ridepricematcher.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.ridepricematcher.data.local.Converters;
import com.example.ridepricematcher.data.local.entity.CachedUserPreferenceEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserPreferenceDao_Impl implements UserPreferenceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CachedUserPreferenceEntity> __insertionAdapterOfCachedUserPreferenceEntity;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public UserPreferenceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCachedUserPreferenceEntity = new EntityInsertionAdapter<CachedUserPreferenceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_preferences` (`userId`,`minimumPrice`,`maximumPrice`,`selectedLanguages`,`matchingEnabled`,`syncedAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CachedUserPreferenceEntity entity) {
        statement.bindString(1, entity.getUserId());
        if (entity.getMinimumPrice() == null) {
          statement.bindNull(2);
        } else {
          statement.bindDouble(2, entity.getMinimumPrice());
        }
        if (entity.getMaximumPrice() == null) {
          statement.bindNull(3);
        } else {
          statement.bindDouble(3, entity.getMaximumPrice());
        }
        final String _tmp = __converters.fromStringList(entity.getSelectedLanguages());
        statement.bindString(4, _tmp);
        final int _tmp_1 = entity.getMatchingEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        statement.bindLong(6, entity.getSyncedAt());
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM user_preferences WHERE userId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final CachedUserPreferenceEntity preferences,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCachedUserPreferenceEntity.insert(preferences);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final String userId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, userId);
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getPreferences(final String userId,
      final Continuation<? super CachedUserPreferenceEntity> $completion) {
    final String _sql = "SELECT * FROM user_preferences WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CachedUserPreferenceEntity>() {
      @Override
      @Nullable
      public CachedUserPreferenceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMinimumPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "minimumPrice");
          final int _cursorIndexOfMaximumPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "maximumPrice");
          final int _cursorIndexOfSelectedLanguages = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedLanguages");
          final int _cursorIndexOfMatchingEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "matchingEnabled");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final CachedUserPreferenceEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final Double _tmpMinimumPrice;
            if (_cursor.isNull(_cursorIndexOfMinimumPrice)) {
              _tmpMinimumPrice = null;
            } else {
              _tmpMinimumPrice = _cursor.getDouble(_cursorIndexOfMinimumPrice);
            }
            final Double _tmpMaximumPrice;
            if (_cursor.isNull(_cursorIndexOfMaximumPrice)) {
              _tmpMaximumPrice = null;
            } else {
              _tmpMaximumPrice = _cursor.getDouble(_cursorIndexOfMaximumPrice);
            }
            final List<String> _tmpSelectedLanguages;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSelectedLanguages);
            _tmpSelectedLanguages = __converters.toStringList(_tmp);
            final boolean _tmpMatchingEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMatchingEnabled);
            _tmpMatchingEnabled = _tmp_1 != 0;
            final long _tmpSyncedAt;
            _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            _result = new CachedUserPreferenceEntity(_tmpUserId,_tmpMinimumPrice,_tmpMaximumPrice,_tmpSelectedLanguages,_tmpMatchingEnabled,_tmpSyncedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
