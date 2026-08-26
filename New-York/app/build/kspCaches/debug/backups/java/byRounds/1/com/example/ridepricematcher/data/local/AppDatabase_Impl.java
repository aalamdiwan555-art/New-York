package com.example.ridepricematcher.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.ridepricematcher.data.local.dao.AuditLogDao;
import com.example.ridepricematcher.data.local.dao.AuditLogDao_Impl;
import com.example.ridepricematcher.data.local.dao.EntitlementDao;
import com.example.ridepricematcher.data.local.dao.EntitlementDao_Impl;
import com.example.ridepricematcher.data.local.dao.LanguageDao;
import com.example.ridepricematcher.data.local.dao.LanguageDao_Impl;
import com.example.ridepricematcher.data.local.dao.PhraseDao;
import com.example.ridepricematcher.data.local.dao.PhraseDao_Impl;
import com.example.ridepricematcher.data.local.dao.UserPreferenceDao;
import com.example.ridepricematcher.data.local.dao.UserPreferenceDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile LanguageDao _languageDao;

  private volatile PhraseDao _phraseDao;

  private volatile UserPreferenceDao _userPreferenceDao;

  private volatile EntitlementDao _entitlementDao;

  private volatile AuditLogDao _auditLogDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `languages` (`id` TEXT NOT NULL, `locale` TEXT NOT NULL, `displayName` TEXT NOT NULL, `displayNameNative` TEXT NOT NULL, `aliases` TEXT NOT NULL, `enabled` INTEGER NOT NULL, `priceKeywords` TEXT NOT NULL, `distanceKeywords` TEXT NOT NULL, `durationKeywords` TEXT NOT NULL, `syncedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `phrases` (`id` TEXT NOT NULL, `languageId` TEXT NOT NULL, `type` TEXT NOT NULL, `phrase` TEXT NOT NULL, `enabled` INTEGER NOT NULL, `syncedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_preferences` (`userId` TEXT NOT NULL, `minimumPrice` REAL, `maximumPrice` REAL, `selectedLanguages` TEXT NOT NULL, `matchingEnabled` INTEGER NOT NULL, `syncedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `entitlements` (`userId` TEXT NOT NULL, `type` TEXT NOT NULL, `adFree` INTEGER NOT NULL, `lifetime` INTEGER NOT NULL, `subscriptionExpiresAt` TEXT, `syncedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `audit_logs` (`id` TEXT NOT NULL, `adminUserId` TEXT NOT NULL, `action` TEXT NOT NULL, `targetUserId` TEXT, `metadata` TEXT NOT NULL, `createdAt` TEXT NOT NULL, `syncedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '346a2ef183ac98e76b6e3b4de87732c4')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `languages`");
        db.execSQL("DROP TABLE IF EXISTS `phrases`");
        db.execSQL("DROP TABLE IF EXISTS `user_preferences`");
        db.execSQL("DROP TABLE IF EXISTS `entitlements`");
        db.execSQL("DROP TABLE IF EXISTS `audit_logs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsLanguages = new HashMap<String, TableInfo.Column>(10);
        _columnsLanguages.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("locale", new TableInfo.Column("locale", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("displayNameNative", new TableInfo.Column("displayNameNative", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("aliases", new TableInfo.Column("aliases", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("priceKeywords", new TableInfo.Column("priceKeywords", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("distanceKeywords", new TableInfo.Column("distanceKeywords", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("durationKeywords", new TableInfo.Column("durationKeywords", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLanguages.put("syncedAt", new TableInfo.Column("syncedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLanguages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLanguages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLanguages = new TableInfo("languages", _columnsLanguages, _foreignKeysLanguages, _indicesLanguages);
        final TableInfo _existingLanguages = TableInfo.read(db, "languages");
        if (!_infoLanguages.equals(_existingLanguages)) {
          return new RoomOpenHelper.ValidationResult(false, "languages(com.example.ridepricematcher.data.local.entity.CachedLanguageEntity).\n"
                  + " Expected:\n" + _infoLanguages + "\n"
                  + " Found:\n" + _existingLanguages);
        }
        final HashMap<String, TableInfo.Column> _columnsPhrases = new HashMap<String, TableInfo.Column>(6);
        _columnsPhrases.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("languageId", new TableInfo.Column("languageId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("phrase", new TableInfo.Column("phrase", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("syncedAt", new TableInfo.Column("syncedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPhrases = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPhrases = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPhrases = new TableInfo("phrases", _columnsPhrases, _foreignKeysPhrases, _indicesPhrases);
        final TableInfo _existingPhrases = TableInfo.read(db, "phrases");
        if (!_infoPhrases.equals(_existingPhrases)) {
          return new RoomOpenHelper.ValidationResult(false, "phrases(com.example.ridepricematcher.data.local.entity.CachedPhraseEntity).\n"
                  + " Expected:\n" + _infoPhrases + "\n"
                  + " Found:\n" + _existingPhrases);
        }
        final HashMap<String, TableInfo.Column> _columnsUserPreferences = new HashMap<String, TableInfo.Column>(6);
        _columnsUserPreferences.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserPreferences.put("minimumPrice", new TableInfo.Column("minimumPrice", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserPreferences.put("maximumPrice", new TableInfo.Column("maximumPrice", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserPreferences.put("selectedLanguages", new TableInfo.Column("selectedLanguages", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserPreferences.put("matchingEnabled", new TableInfo.Column("matchingEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserPreferences.put("syncedAt", new TableInfo.Column("syncedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserPreferences = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserPreferences = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserPreferences = new TableInfo("user_preferences", _columnsUserPreferences, _foreignKeysUserPreferences, _indicesUserPreferences);
        final TableInfo _existingUserPreferences = TableInfo.read(db, "user_preferences");
        if (!_infoUserPreferences.equals(_existingUserPreferences)) {
          return new RoomOpenHelper.ValidationResult(false, "user_preferences(com.example.ridepricematcher.data.local.entity.CachedUserPreferenceEntity).\n"
                  + " Expected:\n" + _infoUserPreferences + "\n"
                  + " Found:\n" + _existingUserPreferences);
        }
        final HashMap<String, TableInfo.Column> _columnsEntitlements = new HashMap<String, TableInfo.Column>(6);
        _columnsEntitlements.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntitlements.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntitlements.put("adFree", new TableInfo.Column("adFree", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntitlements.put("lifetime", new TableInfo.Column("lifetime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntitlements.put("subscriptionExpiresAt", new TableInfo.Column("subscriptionExpiresAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntitlements.put("syncedAt", new TableInfo.Column("syncedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEntitlements = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEntitlements = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEntitlements = new TableInfo("entitlements", _columnsEntitlements, _foreignKeysEntitlements, _indicesEntitlements);
        final TableInfo _existingEntitlements = TableInfo.read(db, "entitlements");
        if (!_infoEntitlements.equals(_existingEntitlements)) {
          return new RoomOpenHelper.ValidationResult(false, "entitlements(com.example.ridepricematcher.data.local.entity.CachedEntitlementEntity).\n"
                  + " Expected:\n" + _infoEntitlements + "\n"
                  + " Found:\n" + _existingEntitlements);
        }
        final HashMap<String, TableInfo.Column> _columnsAuditLogs = new HashMap<String, TableInfo.Column>(7);
        _columnsAuditLogs.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAuditLogs.put("adminUserId", new TableInfo.Column("adminUserId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAuditLogs.put("action", new TableInfo.Column("action", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAuditLogs.put("targetUserId", new TableInfo.Column("targetUserId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAuditLogs.put("metadata", new TableInfo.Column("metadata", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAuditLogs.put("createdAt", new TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAuditLogs.put("syncedAt", new TableInfo.Column("syncedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAuditLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAuditLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAuditLogs = new TableInfo("audit_logs", _columnsAuditLogs, _foreignKeysAuditLogs, _indicesAuditLogs);
        final TableInfo _existingAuditLogs = TableInfo.read(db, "audit_logs");
        if (!_infoAuditLogs.equals(_existingAuditLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "audit_logs(com.example.ridepricematcher.data.local.entity.AuditLogEntity).\n"
                  + " Expected:\n" + _infoAuditLogs + "\n"
                  + " Found:\n" + _existingAuditLogs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "346a2ef183ac98e76b6e3b4de87732c4", "8a12e2565aec482f9aa1c828cc0f4e4d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "languages","phrases","user_preferences","entitlements","audit_logs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `languages`");
      _db.execSQL("DELETE FROM `phrases`");
      _db.execSQL("DELETE FROM `user_preferences`");
      _db.execSQL("DELETE FROM `entitlements`");
      _db.execSQL("DELETE FROM `audit_logs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(LanguageDao.class, LanguageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PhraseDao.class, PhraseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserPreferenceDao.class, UserPreferenceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EntitlementDao.class, EntitlementDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AuditLogDao.class, AuditLogDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public LanguageDao languageDao() {
    if (_languageDao != null) {
      return _languageDao;
    } else {
      synchronized(this) {
        if(_languageDao == null) {
          _languageDao = new LanguageDao_Impl(this);
        }
        return _languageDao;
      }
    }
  }

  @Override
  public PhraseDao phraseDao() {
    if (_phraseDao != null) {
      return _phraseDao;
    } else {
      synchronized(this) {
        if(_phraseDao == null) {
          _phraseDao = new PhraseDao_Impl(this);
        }
        return _phraseDao;
      }
    }
  }

  @Override
  public UserPreferenceDao userPreferenceDao() {
    if (_userPreferenceDao != null) {
      return _userPreferenceDao;
    } else {
      synchronized(this) {
        if(_userPreferenceDao == null) {
          _userPreferenceDao = new UserPreferenceDao_Impl(this);
        }
        return _userPreferenceDao;
      }
    }
  }

  @Override
  public EntitlementDao entitlementDao() {
    if (_entitlementDao != null) {
      return _entitlementDao;
    } else {
      synchronized(this) {
        if(_entitlementDao == null) {
          _entitlementDao = new EntitlementDao_Impl(this);
        }
        return _entitlementDao;
      }
    }
  }

  @Override
  public AuditLogDao auditLogDao() {
    if (_auditLogDao != null) {
      return _auditLogDao;
    } else {
      synchronized(this) {
        if(_auditLogDao == null) {
          _auditLogDao = new AuditLogDao_Impl(this);
        }
        return _auditLogDao;
      }
    }
  }
}
