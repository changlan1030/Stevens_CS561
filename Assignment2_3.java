/**
 * Header Comments:
 * 
 * Author Name: Lan Chang
 * ID: 10405758
 * Course: CS-561
 * Language: Java
 * Software: Eclipse
 * Project Name: Programming Assignment 2_3
 * This program is to generate report based on the following queries:
 * For customer and product, count for each month,
 * how many sales of the previous and how many sales of the following month
 * had quantities between that month¡¯s average sale and maximum sale.
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

public class Assignment2_3 {
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
		
		HashMap<String, Result3> report3 = new HashMap<String, Result3> ();
		// HashMap(report3) to store: the max, sum and count sale of each month,
		// and the number of the sales of the previous and following month that
		// had quantities between average sale and maximum sale of this month
		// key = prod + month, value = result3
		
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		}
		
		catch(Exception e) {
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}
		
		try {
			Connection conn1 = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");
			
			Statement stmt1 = conn1.createStatement();
			ResultSet rs1 = stmt1.executeQuery("SELECT * FROM Sales");
			
			while (rs1.next()) {
				// get the original data
				String prod = rs1.getString("prod");
				int month = Integer.parseInt(rs1.getString("month"));
				int quant = Integer.parseInt(rs1.getString("quant"));
				String key = prod + changeMonth(month);
				
				Result3 result3 = new Result3(prod, month, 0, 0, 0, 0, 0);
				
				// add data if there is not the same prod + month in HashMap
				if (!report3.containsKey(key)) {
					report3.put(key, result3);
				}
				
				// for each data existing in the HashMap, check
				for (HashMap.Entry <String, Result3> entry : report3.entrySet()) {
					// if the new one have the same key, change the value of sum and count
					if (entry.getKey().equals(key)) {
						entry.getValue().setSumCount(quant);
						// if the new one's quant is bigger, change the maximum
						if (entry.getValue().getMax() < quant) {
							entry.getValue().setMax(quant);
						}
					}
				}
			}
		}
		
		catch(SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
		
		try {
			Connection conn2 = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");
			
			Statement stmt2 = conn2.createStatement();
			ResultSet rs2 = stmt2.executeQuery("SELECT * FROM Sales");
			
			while (rs2.next()) {
				// get the original data
				String prod = rs2.getString("prod");
				int month = Integer.parseInt(rs2.getString("month"));
				int quant = Integer.parseInt(rs2.getString("quant"));
				
				// for each data existing in the HashMap, check
				for (HashMap.Entry <String, Result3> entry : report3.entrySet()) {
					// if this existing data has the same prod, and the quant is between max and avg
					if (entry.getValue().getProd().equals(prod) && entry.getValue().getAvg() <= quant && entry.getValue().getMax() >= quant) {
						// if the existing data's month is after the month_temp, change the number of the sales of following month
						if (month == entry.getValue().getMonth() + 1) {
							entry.getValue().setNumA();
						}
						// if the existing data's month is before the month_temp, change the number of the sales of previous month
						if (month == entry.getValue().getMonth() - 1) {
							entry.getValue().setNumB();
						}
					}
				}
			}
		}
		
		catch(SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
		
		// sort by cust, prod and month
		List<HashMap.Entry<String, Result3>> report3_order = new ArrayList<HashMap.Entry<String, Result3>>(report3.entrySet());
		Collections.sort(report3_order, new Comparator<HashMap.Entry<String, Result3>>() {
			@Override
			public int compare(Entry<String, Result3> arg0, Entry<String, Result3> arg1) {
				return arg0.getKey().compareTo(arg1.getKey());
			}
		});
		
		// output
		System.out.println("");
		System.out.println("");
		System.out.println("PRODUCT  MONTH  BEFORE_TOT  AFTER_TOT");
		System.out.println("=======  =====  ==========  =========");
		for (HashMap.Entry<String, Result3> entry : report3_order) {
			entry.getValue().print();
		}
	}
}

class Result3 {
	private String prod;
	private int month, max, sum, count, numB, numA;
	
	public Result3(String prod, int month, int max, int sum, int count, int numB, int numA) {
		this.prod = prod;
		this.month = month;
		this.max = max;
		this.sum = sum;
		this.count = count;
		this.numB = numB;
		this.numA = numA;
	}
	public String getProd() {
		return prod;
	}
	public int getMonth() {
		return month;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int q) {
		max = q;
	}
	public void setSumCount(int q) {
		sum += q;
		count++;
	}
	public void setNumB() {
		numB++;
	}
	public void setNumA() {
		numA++;
	}
	public double getAvg() {
		return (double)(sum) / (double)(count);
	}
	public String getNum(int num) {
		if (num == 0) {
			return "<null>";
		}
		else {
			return num + "";
		}
	}
	public void print() {
		System.out.printf("%-7s  ", prod);
		System.out.printf("%5s  ", month);
		System.out.printf("%10s  ", getNum(numB));
		System.out.printf("%9s", getNum(numA));
		System.out.println("");
	}
}