import java.sql.*;
import java.util.Scanner;

public class hw4main {
	
	private static final String sid = "";
	private static final String spw = "";
	private static final String surl = "jdbc:oracle:thin:@dbclick.kaist.ac.kr:1521:orcl";
	
	private static String get_p1_query() {
		Scanner sc = new Scanner(System.in); 
		
		System.out.print("maximum price: ");
		float max_price = sc.nextFloat();
		
		System.out.print("minimum speed: ");
		float min_speed = sc.nextFloat();
		
		System.out.print("minimum RAM: ");
		float min_ram = sc.nextFloat();
		
		System.out.print("minimum hard disk: ");
		float min_hd = sc.nextFloat();
		
		System.out.print("minimum screen size: ");
		float min_ss = sc.nextFloat();
		
		String query = String.format(
				"SELECT * FROM Laptop NATURAL JOIN (SELECT maker, model FROM Product)"
				+ " WHERE price <= %f"
				+ " AND speed >= %f"
				+ " AND ram >= %f"
				+ " AND hd >= %f"
				+ " AND screen >= %f",
				max_price, min_speed, min_ram, min_hd, min_ss);
		
		return query;
	}

	private static String[] get_p2_query_arr() {
		Scanner sc = new Scanner(System.in); 
		
		System.out.print("manufacturer: ");
		String man = sc.nextLine();
		
		System.out.print("model: ");
		int model = sc.nextInt();
		
		System.out.print("speed: ");
		float speed = sc.nextFloat();
		
		System.out.print("RAM: ");
		float ram = sc.nextFloat();
		
		System.out.print("hard disk: ");
		float hd = sc.nextFloat();
		
		System.out.print("price: ");
		float price = sc.nextFloat();
		
		String query_1 = String.format(
				"SELECT * FROM PC WHERE model = %d",
				model);
		
		String query_2 = String.format(
				"INSERT INTO Product(maker, model, type)"
				+ " VALUES ('%s', %d, 'pc')",
				man, model);
		
		String query_3 = String.format(
				"INSERT INTO PC(model, speed, ram, hd, price)"
				+ " VALUES(%d, %f, %f, %f, %f)",
				model, speed, ram, hd, price);
		
		String query_4 = "SELECT * FROM Product";
		
		String query_5 = "SELECT * FROM PC";

		String[] r_arr = {query_1, query_2, query_3, query_4, query_5};
		
		return r_arr;
	}
	
	private static String get_p3_query() {
		Scanner sc = new Scanner(System.in); 
		
		System.out.print("price: ");
		float price = sc.nextFloat();
		
		String query = String.format(
				"SELECT maker, model, ram FROM PC"
				+ " NATURAL JOIN (SELECT maker, model FROM Product)"
				+ " ORDER BY abs(price - %f)",
				price);
		
		return query;
	}
	
	private static String[] get_p4_query_arr(){
		Scanner sc = new Scanner(System.in); 
		
		System.out.print("manufacturer: ");
		String man = sc.nextLine();
		
		String query_1 = String.format(
				"SELECT model, type, speed, ram, hd, price"
				+ " FROM PC NATURAL JOIN Product"
				+ " WHERE maker = '%s'",
				man);
		
		String query_2 = String.format(
				"SELECT model, type, speed, ram, hd, screen, price"
				+ " FROM Laptop NATURAL JOIN Product"
				+ " WHERE maker = '%s'",
				man);

		String query_3 = String.format(
				"SELECT Product.model, Product.type, color, Printer.type, price"
				+ " FROM Printer, Product"
				+ " WHERE maker = '%s'"
				+ " AND Printer.model = Product.model",
				man);
		
		String[] r_arr = {query_1, query_2, query_3};
		
		return r_arr;
	}
	
	private static String get_p5_query() {
		Scanner sc = new Scanner(System.in); 
		
		System.out.print("Budget: ");
		float budget = sc.nextFloat();
		
		System.out.print("minimum speed: ");
		float min_speed = sc.nextFloat();
		
		String query = String.format(
				"SELECT PC.model, Printer.model, PC.price, Printer.price, Printer.color"
				+ " FROM PC, Printer"
				+ " WHERE PC.speed >= %f"
				+ " AND PC.price+Printer.price <= %f"
				+ " ORDER BY PC.price+Printer.price asc, Printer.color desc",
				min_speed, budget);

		return query;
	}
	
	public static void main(String[] args) {
		
        Connection con = null;
        Statement stmt = null;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(surl, sid, spw);
            stmt = con.createStatement();
            
            Scanner sc = new Scanner(System.in); 
            int p_num;
            String query;
            String[] query_arr;
            while(true){
            	System.out.print("\nproblem #(int): ");
            	p_num = sc.nextInt();
            	// p_num = 5;
            	
            	if(p_num == 1) {
            		
            		query = get_p1_query();
            		
            		ResultSet rs = stmt.executeQuery(query);
            		String[] col_arr = {"model", "speed", "ram", "hd", "screen", "price", "maker"};
            		print_table(rs, col_arr);
                    
            	} else if (p_num == 2) {
            		
            		query_arr = get_p2_query_arr();
            		ResultSet rs = stmt.executeQuery(query_arr[0]);
            		
            		if(rs.next()){ // exist
            			System.out.println("Warning: there is PC with that model number");
            		} else { // non exist
            			stmt.executeUpdate(query_arr[1]);
            			stmt.executeUpdate(query_arr[2]);
            			
            			ResultSet rs_pdt = stmt.executeQuery(query_arr[3]);
            			String[] pdt_col_arr = {"maker", "model", "type"};
            			print_table(rs_pdt, pdt_col_arr);
            			
            			ResultSet rs_pc = stmt.executeQuery(query_arr[4]);
            			String[] pc_col_arr = {"model", "speed", "ram", "hd", "price"};
            			print_table(rs_pc, pc_col_arr);

            		}
            	} else if (p_num == 3) {
            		
            		query = get_p3_query();
            		
            		ResultSet rs = stmt.executeQuery(query);
            		rs.next();
            		String[] col_arr = {"maker", "model", "ram"};
            		String[] first_row = get_row_arr(rs, col_arr.length);
            		System.out.println(arr2str(col_arr, "\t"));
            		System.out.println(arr2str(first_row, "\t"));
            		
            	} else if (p_num == 4) {
            		
            		query_arr = get_p4_query_arr();
            		
            		ResultSet rs_pc = stmt.executeQuery(query_arr[0]);
        			String[] pc_col_arr = {"model", "p-type", "speed", "ram", "hd", "price"};
        			print_table(rs_pc, pc_col_arr);

            		ResultSet rs_lt = stmt.executeQuery(query_arr[1]);
        			String[] lt_col_arr = {"model", "p-type", "speed", "ram", "hd", "screen", "price"};
        			print_table(rs_lt, lt_col_arr);
        			
            		ResultSet rs_pt = stmt.executeQuery(query_arr[2]);
        			String[] pt_col_arr = {"model", "p-type", "color", "type", "price"};
        			print_table(rs_pt, pt_col_arr);		
        			
            	} else if (p_num == 5) {
            		
            		query = get_p5_query();
            		ResultSet rs = stmt.executeQuery(query);
            		rs.next();
            		String[] col_arr = {"PC", "Printer"};
            		String[] first_row = get_row_arr(rs, col_arr.length);
            		System.out.println(arr2str(col_arr, "\t"));
            		System.out.println(arr2str(first_row, "\t"));
            		
            	} else {
            		
            		System.out.println("Wrong number!");
            		
            	}
            }
		} catch (Exception e){
			e.printStackTrace();
		} finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (Exception e) { }
		}
		
		
	}
	
	/* help function */
	private static void print_table(ResultSet rs, String[] col_arr) throws SQLException{
		System.out.println(arr2str(col_arr, "\t"));
		while (rs.next()) {
        	String[] row = get_row_arr(rs, col_arr.length);
        	System.out.println(arr2str(row, "\t"));
        }
		System.out.println();
	}
	
	private static String[] get_row_arr(ResultSet rs, int colnum) throws SQLException{
    	String[] row = new String[colnum];
    	for (int i = 1; i <= colnum; ++i) {
    		row[i-1] = rs.getString(i);
    	}
		return row;
	}
	
	private static String arr2str(String[] arr, String concat) {
		StringBuilder strBuilder = new StringBuilder();
		String prefix = "";
		for(int i = 0; i < arr.length; i++){
			strBuilder.append(prefix);
			prefix = concat;
			strBuilder.append(arr[i]);
		}
		return strBuilder.toString();
	}

}
