package com.shuffle.vnt.core.db.persister;

import java.sql.SQLException;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.LongType;
import com.j256.ormlite.support.DatabaseResults;
import com.shuffle.vnt.core.service.ServiceFactory;
import com.shuffle.vnt.core.service.ServiceParser;
import com.shuffle.vnt.core.service.ServiceParserData;

public class ServiceParserDataPersister extends LongType {

	public ServiceParserDataPersister() {
		super(SqlType.LONG, new Class<?>[] { ServiceParserData.class });
	}

	private static ServiceParserDataPersister instance = new ServiceParserDataPersister();

	public static ServiceParserDataPersister getSingleton() {
		return instance;
	}

	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
		ServiceParserData serviceParserData = (ServiceParserData) javaObject;
		return serviceParserData.getId();
	}

	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
		return null;
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		try {
			Class<? extends ServiceParser> serviceParserClass = (Class<? extends ServiceParser>) ServiceParser.class.forName(results.getString(3));
			return ServiceFactory.getInstance(serviceParserClass).getData(results.getLong(4));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
