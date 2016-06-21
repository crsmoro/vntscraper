package com.shuffle.vnt.util;

import java.lang.reflect.Field;
import java.sql.SQLException;

import com.j256.ormlite.field.DataPersister;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.support.DatabaseResults;

public class ClassPersister implements DataPersister {

	private static ClassPersister instance = new ClassPersister();

	private ClassPersister() {
	}

	public static ClassPersister getSingleton() {
		return instance;
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
		return defaultStr;
	}

	@Override
	public Object javaToSqlArg(FieldType fieldType, Object obj) throws SQLException {
		return ((Class<?>) obj).getName();
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		return results.getString(columnPos);
	}

	@Override
	public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		try {
			return Class.forName(results.getString(columnPos));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
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

	@Override
	public SqlType getSqlType() {
		return SqlType.STRING;
	}

	@Override
	public boolean isStreamType() {
		return false;
	}

	@Override
	public Object resultStringToJava(FieldType fieldType, String stringValue, int columnPos) throws SQLException {
		try {
			return Class.forName(stringValue);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Class<?>[] getAssociatedClasses() {
		return new Class[0];
	}

	@Override
	public String[] getAssociatedClassNames() {
		return null;
	}

	@Override
	public Object makeConfigObject(FieldType fieldType) throws SQLException {
		return null;
	}

	@Override
	public Object convertIdNumber(Number number) {
		return null;
	}

	@Override
	public boolean isValidGeneratedType() {
		return false;
	}

	@Override
	public boolean isValidForField(Field field) {
		return false;
	}

	@Override
	public Class<?> getPrimaryClass() {
		return null;
	}

	@Override
	public boolean isEscapedDefaultValue() {
		return false;
	}

	@Override
	public boolean isEscapedValue() {
		return false;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isComparable() {
		return false;
	}

	@Override
	public boolean isAppropriateId() {
		return false;
	}

	@Override
	public boolean isArgumentHolderRequired() {
		return false;
	}

	@Override
	public boolean isSelfGeneratedId() {
		return false;
	}

	@Override
	public Object generateId() {
		return null;
	}

	@Override
	public int getDefaultWidth() {
		return 0;
	}

	@Override
	public boolean dataIsEqual(Object obj1, Object obj2) {
		return obj1.equals(obj2);
	}

	@Override
	public boolean isValidForVersion() {
		return false;
	}

	@Override
	public Object moveToNextValue(Object currentValue) {
		return null;
	}

}
