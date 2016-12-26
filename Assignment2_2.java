/**
 * Header Comments:
 * 
 * Author Name: Lan Chang
 * ID: 10405758
 * Course: CS-561
 * Language: Java
 * Software: Eclipse
 * Project Name: Programming Assignment 2_2
 * This program is to generate report based on the following queries:
 * For customer and product, show the average sales before and after each month.
 * 
 * Running Step:
 * 1. Run Eclipse.
 * 2. Add external JAR "postgresql-9.4-1203.jdbc4" into Eclipse:
 *    click "Project" and go to "Properties"
 *    select "Java Build Path"
 *    select "Libraries"
 *    click "Add External JARs"
 *    select the location of JDBC
 *    click "OK"
 * 3. Run program:
 *    click "Run"
 * 
 * Postgresql Database Setting:
 * 1. usr = "postgres"
 * 2. pwd = "123456"
 * 3. url = "jdbc:postgresql://localhost:5432/"
 */

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

public class Assignment2_2 {
	// change the month 1, 2...12 into 01, 02...12
	public static String changeMonth(int month) {
		if (month < 10) {
			return "0" + month;
		}
		else {
			return "" + month;
		}
	}
	
	public static void main(String[] args) {
		String usr = "postgres";
		String pwd = "123456";
		String url = "jdbc:postgresql://localhost:5432/";
		
		HashMap<String, Result2> report2 = new HashMap<String, Result2> ();
		// HashMap(report2) to store: the sum and count sale before and after each month
		// key = cust + prod + month, value = result2
		
		HashMap<String, Comb> temp = new HashMap<String, Comb> ();
		// HashMap(temp) to store: all the cust, prod and month combination
		// key = cust + prod + month, value = comb
		
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		}
		
		catch(Exception e) {
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}
		
		try {
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");
			
			while (rs.next()) {
				// get the original data
				String cust = rs.getString("cust");
				String prod = rs.getString("prod");
				int month = Integer.parseInt(rs.getString("month"));
				int quant = Integer.parseInt(rs.getString("quant"));
				
				// get the before and after month
				int monthB = month - 1;
				int monthA = month + 1;
				
				// for each data existing in the HashMap, check
				for (HashMap.Entry <String, Result2> entry : report2.entrySet()) {
					// if the existing data has the same cust and prod with the new one,
					if (entry.getValue().getCust().equals(cust) && entry.getValue().getProd().equals(prod)) {
						// if the existing data's month equals the new one's month_be,
						// change the sum and count for "after" of this existing data
						if (entry.getValue().getMonth().equals(monthB + "")) {
							entry.getValue().setSumCountA(quant);
						}
						// if the existing data's month equals the new one's month_af,
						// change the sum and count for "before" of this existing data
						if (entry.getValue().getMonth().equals(monthA + "")) {
							entry.getValue().setSumCountB(quant);
						}
					}
				}
				
				// add data if there is not the same cust + prod + month_be in HashMap,
				// and change the sum and count for "after" of this data
				if (!report2.containsKey(cust + prod + changeMonth(monthB))) {
					Result2 result2B = new Result2(cust, prod, monthB + "", 0, 0, 0, 0);
					result2B.setSumCountA(quant);
					report2.put(cust + prod + changeMonth(monthB), result2B);
				}
				// add data if there is not the same cust + prod + month_af in HashMap,
				// and change the sum and count for "before" of this data
				if (!report2.containsKey(cust + prod + changeMonth(monthA))) {
					Result2 result2A = new Result2(cust, prod, monthA + "", 0, 0, 0, 0);
					result2A.setSumCountB(quant);
					report2.put(cust + prod + changeMonth(monthA), result2A);
				}
				
				// add the cust, prod and month combination to the HashMap(temp)
				for (int i = 1; i <= 12; i++) {
					if (!temp.containsKey(cust + prod + changeMonth(i))) {
						temp.put(cust + prod + changeMonth(i), new Comb(cust, prod, i));
					}
				}
			}
		}
		
		catch(SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
		
		// add the rest of cust, prod and month combination to the HashMap
		for (HashMap.Entry <String, Comb> entry : temp.entrySet()) {
			if (!report2.containsKey(entry.getKey())) {
				report2.put(entry.getKey(), new Result2(entry.getValue().getCust(), entry.getValue().getProd(), entry.getValue().getMonth() + "", 0, 0, 0, 0));
			}
		}
		
		// sort by cust, prod and month
		List<HashMap.Entry<String, Result2>> report2_order = new ArrayList<HashMap.Entry<String, Result2>>(report2.entrySet());
		Collections.sort(report2_order, new Comparator<HashMap.Entry<String, Result2>>() {
			@Override
			public int compare(Entry<String, Result2> arg0, Entry<String, Result2> arg1) {
				return arg0.getKey().compareTo(arg1.getKey());
			}
		});
		
		// output
		System.out.println("");
		System.out.println("");
		System.out.println("CUSTOMER  PRODUCT  MONTH  BEFORE_AVG  AFTER_AVG");
		System.out.println("========  =======  =====  ==========  =========");
		for (HashMap.Entry<String, Result2> entry : report2_order) {
			entry.getValue().print();
		}
	}
}

class Result2 {
	private String cust, prod, month;
	private int sumB, countB, sumA, countA;
	
	public Result2(String cust, String prod, String month, int sumB, int countB, int sumA, int countA) {
		this.cust = cust;
		this.prod = prod;
		this.month = month;
		this.sumB = sumB;
		this.countB = countB;
		this.sumA = sumA;
		this.countA = countA;
	}
	public String getCust() {
		return cust;
	}
	public String getProd() {
		return prod;
	}
	public String getMonth() {
		return month;
	}
	public void setSumCountB(int q) {
		sumB += q;
		countB++;
	}
	public void setSumCountA(int q) {
		sumA += q;
		countA++;
	}
	public String getAvg(int sum, int count) {
		if (count == 0) {
			return "<null>";
		}
		else {
			return (int)((double)(sum) / (double)(count) + 0.5) + "";
		}
	}
	public void print() {
		if (Integer.parseInt(month) >= 1 && Integer.parseInt(month) <= 12) {
			System.out.printf("%-8s  ", cust);
			System.out.printf("%-7s  ", prod);
			System.out.printf("%5s  ", month);
			System.out.printf("%10s  ", getAvg(sumB, countB));
			System.out.printf("%9s", getAvg(sumA, countA));
			System.out.println("");
		}
	}
}

class Comb {
	private String cust, prod;
	private int month;
	
	public Comb(String cust, String prod, int month) {
		this.cust = cust;
		this.prod = prod;
		this.month = month;
	}
	public String getCust() {
		return cust;
	}
	public String getProd() {
		return prod;
	}
	public int getMonth() {
		return month;
	}
}