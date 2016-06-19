package com.shuffle.vnt.core.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.shuffle.vnt.core.db.model.GenericEntity;

public class PersistenceManager {

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
}
