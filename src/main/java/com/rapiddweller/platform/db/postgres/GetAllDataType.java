package com.rapiddweller.platform.db.postgres;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetAllDataType {

    public static void main(String[] args) {
        Connection conn = null;

        String url = "jdbc:postgresql://localhost:5432/benerator";
        String user = "root";
        String password = "Benerator123!";
        List<String> types = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(url, user, password);

            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTypeInfo();

            while (rs.next()) {
                String dataTypeName = rs.getString("TYPE_NAME");
                types.add(dataTypeName);
                System.out.println(dataTypeName);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
