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
import com.example.ridepricematcher.data.local.entity.CachedPhraseEntity;
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
public final class PhraseDao_Impl implements PhraseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CachedPhraseEntity> __insertionAdapterOfCachedPhraseEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public PhraseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCachedPhraseEntity = new EntityInsertionAdapter<CachedPhraseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `phrases` (`id`,`languageId`,`type`,`phrase`,`enabled`,`syncedAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CachedPhraseEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getLanguageId());
        statement.bindString(3, entity.getType());
        statement.bindString(4, entity.getPhrase());
        final int _tmp = entity.getEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getSyncedAt());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM phrases";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<CachedPhraseEntity> phrases,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCachedPhraseEntity.insert(phrases);
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
  public Flow<List<CachedPhraseEntity>> getPhrasesForLanguage(final String languageId) {
    final String _sql = "SELECT * FROM phrases WHERE languageId = ? AND enabled = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, languageId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"phrases"}, new Callable<List<CachedPhraseEntity>>() {
      @Override
      @NonNull
      public List<CachedPhraseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageId = CursorUtil.getColumnIndexOrThrow(_cursor, "languageId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final List<CachedPhraseEntity> _result = new ArrayList<CachedPhraseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CachedPhraseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageId;
            _tmpLanguageId = _cursor.getString(_cursorIndexOfLanguageId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            final long _tmpSyncedAt;
            _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            _item = new CachedPhraseEntity(_tmpId,_tmpLanguageId,_tmpType,_tmpPhrase,_tmpEnabled,_tmpSyncedAt);
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
  public Flow<List<CachedPhraseEntity>> getPhrasesByType(final String type) {
    final String _sql = "SELECT * FROM phrases WHERE type = ? AND enabled = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"phrases"}, new Callable<List<CachedPhraseEntity>>() {
      @Override
      @NonNull
      public List<CachedPhraseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLanguageId = CursorUtil.getColumnIndexOrThrow(_cursor, "languageId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "enabled");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final List<CachedPhraseEntity> _result = new ArrayList<CachedPhraseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CachedPhraseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLanguageId;
            _tmpLanguageId = _cursor.getString(_cursorIndexOfLanguageId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final boolean _tmpEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _tmpEnabled = _tmp != 0;
            final long _tmpSyncedAt;
            _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            _item = new CachedPhraseEntity(_tmpId,_tmpLanguageId,_tmpType,_tmpPhrase,_tmpEnabled,_tmpSyncedAt);
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
