import java.sql.*;

class Test  {
    public static void main(String[] args)  {
        Connection con = null;
        Statement stmt = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection( "jdbc:oracle:thin:@dbclick.kaist.ac.kr:1521:orcl", "hjjeong", "hjjeong");
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from product");

            while (rs.next()) {
                String product = rs.getString(1);
                System.out.println(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (Exception e) { }
        }
    }
}
