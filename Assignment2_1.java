/**
 * Header Comments:
 * 
 * Author Name: Lan Chang
 * ID: 10405758
 * Course: CS-561
 * Language: Java
 * Software: Eclipse
 * Project Name: Programming Assignment 2_1
 * This program is to generate report based on the following queries:
 * For each customer and product, compute:
 * (1) the average sale of this customer and this product,
 * (2) the average sale of this customer and other product,
 * (3) the average sale of other customer and this product.
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

public class Assignment2_1 {
	// get the average
	public static String getAvg(int sum, int count) {
		if (count == 0) {
			return "<null>";
		}
		else {
			return (int)((double)(sum) / (double)(count) + 0.5) + "";
		}
	}
	
	// sort by key
	public static List<HashMap.Entry<String, Result1>> sort(HashMap<String, Result1> hm) {
		List<HashMap.Entry<String, Result1>> order = new ArrayList<HashMap.Entry<String, Result1>>(hm.entrySet());
		Collections.sort(order, new Comparator<HashMap.Entry<String, Result1>>() {
			@Override
			public int compare(Entry<String, Result1> arg0, Entry<String, Result1> arg1) {
				return arg0.getKey().compareTo(arg1.getKey());
			}
		});
		return order;
	}
	
	public static void main(String[] args) {
		String usr = "postgres";
		String pwd = "123456";
		String url = "jdbc:postgresql://localhost:5432/";
		
		HashMap<String, Result1> theAvg = new HashMap<String, Result1> ();
		HashMap<String, Result1> otherProdAvg = new HashMap<String, Result1> ();
		HashMap<String, Result1> otherCustAvg = new HashMap<String, Result1> ();
		// HashMap(theAvg) to store the sum and count of each customer and product combination
		// HashMap(otherProdAvg) to store the sum and count of each customer
		// HashMap(otherCustAvg) to store the sum and count of each product
		
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
				int quant = Integer.parseInt(rs.getString("quant"));
				
				// add data if there is not the same key(cust + prod, cust, prod) in HashMap(theAvg, otherProdAvg, otherCustAvg)
				if (!theAvg.containsKey(cust + prod)) {
					theAvg.put(cust + prod, new Result1(cust, prod, 0, 0));
				}
				if (!otherProdAvg.containsKey(cust)) {
					otherProdAvg.put(cust, new Result1(cust, null, 0, 0));
				}
				if (!otherCustAvg.containsKey(prod)) {
					otherCustAvg.put(prod, new Result1(null, prod, 0, 0));
				}
				
				// for each data existing in the HashMap, check if the new one have the same key, change the value of sum and count
				for (HashMap.Entry <String, Result1> entry : theAvg.entrySet()) {
					if (entry.getKey().equals(cust + prod)) {
						entry.getValue().setSumCount(quant);
					}
				}
				for (HashMap.Entry <String, Result1> entry : otherProdAvg.entrySet()) {
					if (entry.getKey().equals(cust)) {
						entry.getValue().setSumCount(quant);
					}
				}
				for (HashMap.Entry <String, Result1> entry : otherCustAvg.entrySet()) {
					if (entry.getKey().equals(prod)) {
						entry.getValue().setSumCount(quant);
					}
				}
			}
		}
		
		catch(SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
		
		// sort
		List<HashMap.Entry<String, Result1>> otherProdAvg_order = sort(otherProdAvg);
		List<HashMap.Entry<String, Result1>> otherCustAvg_order = sort(otherCustAvg);
		
		// output
		System.out.println("");
		System.out.println("");
		System.out.println("CUSTOMER  PRODUCT  THE_AVG  OTHER_PROD_AVG  OTHER_CUST_AVG");
		System.out.println("========  =======  =======  ==============  ==============");
		for (HashMap.Entry<String, Result1> entry1 : otherProdAvg_order) {
			for (HashMap.Entry<String, Result1> entry2 : otherCustAvg_order) {
				// print cust and prod
				String resultCust = entry1.getValue().getCust();
				String resultProd = entry2.getValue().getProd();
				System.out.printf("%-8s  ", resultCust);
				System.out.printf("%-7s  ", resultProd);
				
				// initialize the sum and count
				int theSum = 0;
				int theCount = 0;
				int otherProdSum = entry1.getValue().getSum();
				int otherProdCount = entry1.getValue().getCount();
				int otherCustSum = entry2.getValue().getSum();
				int otherCustCount = entry2.getValue().getCount();
				
				for (HashMap.Entry<String, Result1> entry3 : theAvg.entrySet()) {
					// if theAvg has the same cust with otherProdAvg and the same prod with otherCustAvg
					if (entry3.getValue().getCust().equals(resultCust) && entry3.getValue().getProd().equals(resultProd)) {
						theSum = entry3.getValue().getSum();
						theCount = entry3.getValue().getCount();
						otherProdSum = entry1.getValue().getSum() - theSum;
						otherProdCount = entry1.getValue().getCount() - theCount;
						otherCustSum = entry2.getValue().getSum() - theSum;
						otherCustCount = entry2.getValue().getCount() - theCount;
					}
				}
				
				// print the_avg, other_prod_avg, other_cust_avg
				System.out.printf("%7s  ", getAvg(theSum, theCount));
				System.out.printf("%14s  ", getAvg(otherProdSum, otherProdCount));
				System.out.printf("%14s", getAvg(otherCustSum, otherCustCount));
				System.out.println("");
			}
		}
	}
}

class Result1 {
	private String cust, prod;
	private int sum, count;
	
	public Result1(String cust, String prod, int sum, int count) {
		this.cust = cust;
		this.prod = prod;
		this.sum = sum;
		this.count = count;
	}
	public String getCust() {
		return cust;
	}
	public String getProd() {
		return prod;
	}
	public int getSum() {
		return sum;
	}
	public int getCount() {
		return count;
	}
	public void setSumCount(int q) {
		sum += q;
		count++;
	}
}