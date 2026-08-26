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
import com.example.ridepricematcher.data.local.entity.CachedEntitlementEntity;
import java.lang.Class;
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
public final class EntitlementDao_Impl implements EntitlementDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CachedEntitlementEntity> __insertionAdapterOfCachedEntitlementEntity;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public EntitlementDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCachedEntitlementEntity = new EntityInsertionAdapter<CachedEntitlementEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `entitlements` (`userId`,`type`,`adFree`,`lifetime`,`subscriptionExpiresAt`,`syncedAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CachedEntitlementEntity entity) {
        statement.bindString(1, entity.getUserId());
        statement.bindString(2, entity.getType());
        final int _tmp = entity.getAdFree() ? 1 : 0;
        statement.bindLong(3, _tmp);
        final int _tmp_1 = entity.getLifetime() ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        if (entity.getSubscriptionExpiresAt() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSubscriptionExpiresAt());
        }
        statement.bindLong(6, entity.getSyncedAt());
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM entitlements WHERE userId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final CachedEntitlementEntity entitlement,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCachedEntitlementEntity.insert(entitlement);
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
  public Object getEntitlement(final String userId,
      final Continuation<? super CachedEntitlementEntity> $completion) {
    final String _sql = "SELECT * FROM entitlements WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CachedEntitlementEntity>() {
      @Override
      @Nullable
      public CachedEntitlementEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfAdFree = CursorUtil.getColumnIndexOrThrow(_cursor, "adFree");
          final int _cursorIndexOfLifetime = CursorUtil.getColumnIndexOrThrow(_cursor, "lifetime");
          final int _cursorIndexOfSubscriptionExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "subscriptionExpiresAt");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final CachedEntitlementEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final boolean _tmpAdFree;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAdFree);
            _tmpAdFree = _tmp != 0;
            final boolean _tmpLifetime;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfLifetime);
            _tmpLifetime = _tmp_1 != 0;
            final String _tmpSubscriptionExpiresAt;
            if (_cursor.isNull(_cursorIndexOfSubscriptionExpiresAt)) {
              _tmpSubscriptionExpiresAt = null;
            } else {
              _tmpSubscriptionExpiresAt = _cursor.getString(_cursorIndexOfSubscriptionExpiresAt);
            }
            final long _tmpSyncedAt;
            _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            _result = new CachedEntitlementEntity(_tmpUserId,_tmpType,_tmpAdFree,_tmpLifetime,_tmpSubscriptionExpiresAt,_tmpSyncedAt);
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
