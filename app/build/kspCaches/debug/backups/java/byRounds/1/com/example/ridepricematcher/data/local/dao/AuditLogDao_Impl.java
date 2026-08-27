package com.example.ridepricematcher.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.ridepricematcher.data.local.Converters;
import com.example.ridepricematcher.data.local.entity.AuditLogEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AuditLogDao_Impl implements AuditLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AuditLogEntity> __insertionAdapterOfAuditLogEntity;

  private final Converters __converters = new Converters();

  public AuditLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAuditLogEntity = new EntityInsertionAdapter<AuditLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `audit_logs` (`id`,`adminUserId`,`action`,`targetUserId`,`metadata`,`createdAt`,`syncedAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AuditLogEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getAdminUserId());
        statement.bindString(3, entity.getAction());
        if (entity.getTargetUserId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getTargetUserId());
        }
        final String _tmp = __converters.fromStringMap(entity.getMetadata());
        statement.bindString(5, _tmp);
        statement.bindString(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getSyncedAt());
      }
    };
  }

  @Override
  public Object insert(final AuditLogEntity log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAuditLogEntity.insert(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AuditLogEntity>> getAll() {
    final String _sql = "SELECT * FROM audit_logs ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"audit_logs"}, new Callable<List<AuditLogEntity>>() {
      @Override
      @NonNull
      public List<AuditLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAdminUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "adminUserId");
          final int _cursorIndexOfAction = CursorUtil.getColumnIndexOrThrow(_cursor, "action");
          final int _cursorIndexOfTargetUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "targetUserId");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final List<AuditLogEntity> _result = new ArrayList<AuditLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AuditLogEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpAdminUserId;
            _tmpAdminUserId = _cursor.getString(_cursorIndexOfAdminUserId);
            final String _tmpAction;
            _tmpAction = _cursor.getString(_cursorIndexOfAction);
            final String _tmpTargetUserId;
            if (_cursor.isNull(_cursorIndexOfTargetUserId)) {
              _tmpTargetUserId = null;
            } else {
              _tmpTargetUserId = _cursor.getString(_cursorIndexOfTargetUserId);
            }
            final Map<String, String> _tmpMetadata;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __converters.toStringMap(_tmp);
            final String _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            final long _tmpSyncedAt;
            _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            _item = new AuditLogEntity(_tmpId,_tmpAdminUserId,_tmpAction,_tmpTargetUserId,_tmpMetadata,_tmpCreatedAt,_tmpSyncedAt);
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
