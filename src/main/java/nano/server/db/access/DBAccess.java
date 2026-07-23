package nano.server.db.access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

import nano.server.enums.ServerProperties;

public class DBAccess {
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	
	public DBAccess() throws DBException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			this.connection = DriverManager.getConnection(ServerProperties.DB_HOST.getValue(),
					ServerProperties.DB_USER.getValue(), ServerProperties.DB_PASS.getValue());

			this.statement = connection.createStatement();
		} catch (Exception e) {
			String errorMessage = String.format(Locale.getDefault(),
					"Exception trying to stablish the connection: %s",
					e.getLocalizedMessage());
			
			throw new DBException(errorMessage, e);
		}
	}

	public ResultSet getResultSet(DBScript script) throws DBException {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("CALL ").append(script.getMethod()).append("(");
			if (script.getParams() != null && script.getParams().size() > 0) {
				for (int i = 0; i < script.getParams().size(); i++) {
					if (i > 0) {
						stringBuilder.append(", ");
					}
					
					DBParam param = script.getParams().get(i);
					
					if (param.isNull()) {
						stringBuilder.append("null");
					} else {
						if (param.isQuoted()) {
							if (param.isAList()) {
								stringBuilder.append("'").append(param.getListValues()).append("'");
							} else {
								stringBuilder.append("'").append(param.getValue()).append("'");
							}
						} else {
							stringBuilder.append(param.getValue());
						}
					}
				}
			}
			stringBuilder.append(");");
			
			resultSet = statement.executeQuery(stringBuilder.toString());
			
			return resultSet; 
		} catch (Exception e) {
			throw new DBException(script, e);
		}
	}
	
	public void close() {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
