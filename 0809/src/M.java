import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class M {
	public static void main(String[] args) {
		ProcBoard p = new ProcBoard();
		Display d = new Display();
		d.showTitle();
		
		p.run();
//		dbRun();
	}
	
	static private void dbRun() {
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_cat", "root", "admin");
			Statement st = con.createStatement();
			ResultSet result = null;
			result = st.executeQuery("select*from mk1 where num = 1");
//			st.executeUpdate("insert into mk1 values();");
			while (result.next() ) {
				String name = result.getString("tm");
				System.out.println(name);
			}
		} catch(SQLException e) {
			System.out.println("SQLException" + e.getMessage() );
		}
	}
}