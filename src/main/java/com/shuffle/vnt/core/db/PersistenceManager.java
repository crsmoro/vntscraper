package com.shuffle.vnt.core.db;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.shuffle.vnt.core.VntContext;
import com.shuffle.vnt.core.db.model.GenericEntity;

public class PersistenceManager<E extends GenericEntity> {
	
	private static final Log log = LogFactory.getLog(PersistenceManager.class);
	
	/*
	private static final String JDBC_DRIVER = "org.h2.Driver";  
	private static final String DB_URL = "jdbc:h2:./vnt";
	private static final String USER = "sa";
	private static final String PASS = "sa";
	private static Connection connection = null;
	static {
		try {
			log.debug("Loading driver");
			Class.forName(JDBC_DRIVER);
			log.debug("Driver loaded");
		} catch (ClassNotFoundException e) {
			log.error("Problem loading driver", e);
		}
		
		try {
			log.debug("Connecting to database");
			connection = DriverManager.getConnection(DB_URL,USER,PASS);
			log.debug("Connected to database");
		} catch (SQLException e) {
			log.error("Problem connecting to databse", e);
		}
	}
	*/
	private static final String databaseUrl = "jdbc:h2:./vnt";
	private static final String USER = "sa";
	private static final String PASS = "sa";
	private static ConnectionSource connection = null;
	
	static {
		try {
			connection = new JdbcConnectionSource(databaseUrl, USER, PASS);
			//TableUtils.createTableIfNotExists(connection, UserSeedbox.class);
		} catch (SQLException e) {
			log.error("Problem connecting to databse", e);
		}
		
		VntContext.fetchClasses().getSubTypesOf(GenericEntity.class).forEach(genericEntity -> {
			if (!genericEntity.getName().equals(GenericEntity.class.getName())) {
				
				try {
					log.info("Creating entity " + genericEntity.getSimpleName());
					TableUtils.createTableIfNotExists(connection, genericEntity);
				}
				catch (SQLException e) {
					log.error("Problem creating table " + genericEntity.getSimpleName(), e);
				}
				
			}
		});
	}
	
	private Dao<E,Long> dao;
	
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
		/*
		try {
			return dao.queryForFirst(queryBuilder.prepare());
		}
		catch (SQLException e) {
			log.error("Problem getting the result", e);
		}
		return null;
		*/
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
		}
		catch (SQLException e) {
			log.error("Problem setting where", e);
		}
		return this;
	}
	
	public PersistenceManager<E> eq(String columnName, Object value) {
		try {
			where.eq(columnName + (ClassUtils.isAssignable(value.getClass(), GenericEntity.class, true) ? "_id" : ""), value);
		}
		catch (SQLException e) {
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

	public void remove(E object) {
		try {
			dao.delete(object);
		}
		catch (SQLException e) {
			log.error("Error removing entity", e);
		}
	}

	public List<E> findAll() {
		try {
			if (where.toString().equals("empty where clause")) {
				queryBuilder.setWhere(null);
			}
			else {
				queryBuilder.setWhere(where);
			}
			return dao.query(queryBuilder.prepare());
		}
		catch (SQLException e) {
			log.error("Error getting objects", e);
		}
		return Collections.emptyList();
	}

	/*
	private static EntityManagerFactory entityManagerFactory;

	static {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("hibernate.connection.url", "jdbc:h2:" + System.getProperty("db.file", "./vnt"));
		entityManagerFactory = Persistence.createEntityManagerFactory("pu", properties);
	}

	public static <E extends GenericEntity> E findOne(Class<E> entity, Object primaryKey) {
		return findOne(entity, Restrictions.idEq(primaryKey));
	}

	public static <E extends GenericEntity> E save(E object) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		if (object.getId() != null) {
			object = entityManager.merge(object);
		} else {
			entityManager.persist(object);
		}
		entityTransaction.commit();
		entityManager.clear();
		entityManager.close();
		return object;
	}

	public static <E extends GenericEntity> void remove(E object) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		if (object.getId() != null) {
			Object objectToRemove = entityManager.find(object.getClass(), object.getId());
			entityManager.remove(objectToRemove);
		}
		entityTransaction.commit();
		entityManager.clear();
		entityManager.close();
	}

	public static <E extends GenericEntity> List<E> findAll(Class<E> entity) {
		return findAll(entity, null);
	}

	public static <E extends GenericEntity> List<E> findAll(Class<E> entity, Criterion criterion) {
		return findAll(entity, criterion, new String[0]);
	}

	@SuppressWarnings("unchecked")
	public static <E extends GenericEntity> List<E> findAll(Class<E> entity, Criterion criterion, String... lazyFieldsToLoad) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Session session = entityManager.unwrap(Session.class);
		Criteria criteria = session.createCriteria(entity);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		if (criterion != null) {
			criteria.add(criterion);
		}
		for (String field : lazyFieldsToLoad) {
			criteria.setFetchMode(field, FetchMode.JOIN);
		}
		List<E> results = criteria.list();
		entityManager.clear();
		entityManager.close();
		return results;
	}

	public static <E extends GenericEntity> E findOne(Class<E> entity, Criterion criterion) {
		return findOne(entity, criterion, new String[0]);
	}

	public static <E extends GenericEntity> E findOne(Class<E> entity, Criterion criterion, String... lazyFieldsToLoad) {
		List<E> results = findAll(entity, criterion, lazyFieldsToLoad);
		return results != null && !results.isEmpty() ? results.get(0) : null;
	}
	*/
}
