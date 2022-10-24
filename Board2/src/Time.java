import java.util.ArrayList;
import java.util.Date;

public class Time {
	public static void main(String[] args) {
		ArrayList<TimeRecord> tr = new ArrayList<>();

		Date d = new Date();
		
//		System.out.print(1900 + d.getYear() + ".");
//		System.out.print(d.getMonth() + ".");
//		System.out.print(d.getDate() + " ");
//		System.out.print(d.getHours() );
//		System.out.print(":");
//		System.out.print(d.getMinutes() );
//		System.out.print(":");
//		System.out.print(d.getSeconds() );
		
		String repleTime = 1900 + d.getYear() + "." + d.getMonth() + "." + d.getDate() + " " + d.getHours()
		+ ":" + d.getMinutes() + ":" + d.getSeconds();
//		System.out.println(repleTime);
		
		tr.add(new TimeRecord(repleTime) );
		System.out.println(tr.get(0).time);
	}
}
