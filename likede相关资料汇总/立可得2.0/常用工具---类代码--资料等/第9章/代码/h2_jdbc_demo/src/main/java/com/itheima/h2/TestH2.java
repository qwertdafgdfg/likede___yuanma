package  com.itheima.h2;

import java.sql.*;

public class TestH2 {

    public static void main(String[] args) {
        Connection conn=null;
        try {
            Class.forName("org.h2.Driver");
            //服务器模式
             conn = DriverManager.
                    getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");
            //嵌入模式，只能一个客户端
//            conn = DriverManager.
//                    getConnection("jdbc:h2:~/test", "sa", "");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS TEST_H2;");
            stmt.executeUpdate("CREATE TABLE TEST_H2(ID INT PRIMARY KEY,NAME VARCHAR(255));");
            stmt.executeUpdate("INSERT INTO TEST_H2 VALUES(1, 'Hello_Mem');");
            ResultSet rs = stmt.executeQuery("SELECT * FROM TEST_H2");
            while(rs.next()) {
                System.out.println(rs.getInt("ID")+","+rs.getString("NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }
}
