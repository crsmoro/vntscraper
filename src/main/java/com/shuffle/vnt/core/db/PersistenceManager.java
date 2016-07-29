package com.shuffle.vnt.core.db;

import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.shuffle.vnt.core.VntContext;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.db.persister.ClassPersister;
import com.shuffle.vnt.core.db.persister.ServiceParserDataPersister;

public class PersistenceManager<E extends GenericEntity> {

	private static final Log log = LogFactory.getLog(PersistenceManager.class);

	private static final String dbFile = System.getProperty("db.file", "./vnt");
	private static final String databaseUrl = "jdbc:h2:" + dbFile;
	private static final String USER = "sa";
	private static final String PASS = "sa";
	private static ConnectionSource connection = null;
	private static boolean createTables = true;
	static {

		verifyDBFile();

		setupDB();

		createDBTables();

	}

	private static void verifyDBFile() {
		File file = new File(dbFile + ".mv.db");
		log.debug("DB File path: " + file.getAbsolutePath());
		if (file.exists()) {
			createTables = false;
			log.info("DB File already exists, will not create the tables");
		}
	}

	private static void setupDB() {
		try {
			connection = new JdbcConnectionSource(databaseUrl, USER, PASS);
			DataPersisterManager.registerDataPersisters(ServiceParserDataPersister.getSingleton());
			DataPersisterManager.registerDataPersisters(ClassPersister.getSingleton());
		} catch (SQLException e) {
			log.error("Problem connecting to databse", e);
		}
	}

	private static void createDBTables() {
		if (createTables) {
			VntContext.fetchClasses().getSubTypesOf(GenericEntity.class).forEach(genericEntity -> {
				if (!genericEntity.getName().equals(GenericEntity.class.getName())) {

					try {
						log.info("Creating table entity " + genericEntity.getSimpleName());
						TableUtils.createTableIfNotExists(connection, genericEntity);
					} catch (SQLException e) {
						log.error("Problem creating table " + genericEntity.getSimpleName(), e);
					}

				}
			});
		}
	}

	private Dao<E, Long> dao;

	private QueryBuilder<E, Long> queryBuilder;

	private Where<E, Long> where;

	private PersistenceManager() {

	}

	public static <E extends GenericEntity> PersistenceManager<E> getDao(Class<E> entity) {
		try {
			PersistenceManager<E> instance = new PersistenceManager<E>();
			instance.dao = DaoManager.createDao(connection, entity);
			instance.queryBuilder = instance.dao.queryBuilder();
			instance.where = instance.queryBuilder.where();
			return instance;
		} catch (SQLException e) {
			log.error("Problem getting dao", e);
		}
		return null;
	}

	public E findOne() {
		List<E> results = findAll();
		return (!results.isEmpty() ? results.get(0) : null);
	}

	public E findOne(Long primaryKey) {
		idEq(primaryKey);
		return findOne();
	}

	public QueryBuilder<E, Long> getQueryBuilder() {
		return queryBuilder;
	}

	public Where<E, Long> getWhere() {
		return where;
	}

	public PersistenceManager<E> where(Where<E, Long> where) {
		queryBuilder.setWhere(where);
		return this;
	}

	public PersistenceManager<E> idEq(Long id) {
		try {
			where.idEq(id);
		} catch (SQLException e) {
			log.error("Problem setting where", e);
		}
		return this;
	}

	public PersistenceManager<E> eq(String columnName, Object value) {
		try {
			where.eq(columnName + (value != null && ClassUtils.isAssignable(value.getClass(), GenericEntity.class, true) ? "_id" : ""), value);
		} catch (SQLException e) {
			log.error("Problem setting where", e);
		}
		return this;
	}

	public PersistenceManager<E> and() {
		and(1);
		return this;
	}

	public PersistenceManager<E> and(int numClauses) {
		where.and(numClauses);
		return this;
	}

	public PersistenceManager<E> or() {
		or(1);
		return this;
	}

	public PersistenceManager<E> or(int numClauses) {
		where.or(numClauses);
		return this;
	}

	public E save(E object) {
		try {
			dao.createOrUpdate(object);
		} catch (SQLException e) {
			log.error("Error saving entity", e);
		}
		return object;
	}
	
	public void remove(Long id) {
		remove(findOne(id));
	}

	public void remove(E object) {
		try {
			dao.delete(object);
		} catch (SQLException e) {
			log.error("Error removing entity", e);
		}
	}

	public List<E> findAll() {
		try {
			if (where.toString().equals("empty where clause")) {
				queryBuilder.setWhere(null);
			} else {
				queryBuilder.setWhere(where);
			}
			return dao.query(queryBuilder.prepare());
		} catch (SQLException e) {
			log.error("Error getting objects", e);
		}
		return Collections.emptyList();
	}
}
