package com.shuffle.vnt.core.db.persister;

import java.sql.SQLException;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

public class ClassPersister extends StringType {

	private static ClassPersister instance = new ClassPersister();

	private ClassPersister() {
		super(SqlType.STRING, new Class[] { Class.class });
	}

	public static ClassPersister getSingleton() {
		return instance;
	}

	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
		return ((Class<?>) javaObject).getName();
	}

	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
		try {
			return Class.forName(sqlArg.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
