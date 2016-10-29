/**
 * Header Comments:
 * 
 * Author Name: Lan Chang
 * ID: 10405758
 * Course: CS-561
 * Language: Java
 * Software: Eclipse
 * Project Name: Programming Assignment 1
 * This program is to generate 2 separate reports based on the following queries
 * (one report for query #1 and another for query #2):
 * a. For each product, compute the maximum and minimum sales quantities
 *    along with the corresponding customers, dates and states.
 *    For each product, also compute the average sales quantity.
 * b. For each combination of customer and product,
 *    output the maximum sales quantities for CT and minimum sales quantities for NY and NJ,
 *    and display the corresponding dates.
 *    Furthermore, for CT, include only the sales that occurred between 2000 and 2005;
 *    for NY and NJ, include all sales.
 * 
 * Data Structure:
 * HashMap<Key, Value>
 * Reason using HashMap:
 *   HashMap is a good structure to store the data.
 *   Key: there is only one key in each HashMap:
 *     for query 1, the key is product;
 *     for query 2, the key is the combination of customer and product.
 *   Value: we can define the value using any variable in each HashMap:
 *     for query 1, I define an object named Result1 containing:
 *       prod, max_q, max_cust, max_date, max_state, min_q, min_cust, min_date, min_state, count, sum.
 *     for query 2, I define an object named Result2 containing:
 *       cust, prod, ct_max, ct_date, ny_min, ny_date, nj_min, nj_date.
 * 
 * Algorithm(Pseudo code):
 * max, min, avg:
 *   for each data existing in the HashMap for query 1
 *     if (key(new data) = key(existing data) and quant(new data) > max(existing data)) then
 *       max(existing data) = quant(new data)
 *     if (key(new data) = key(existing data) and quant(new data) < min(existing data)) then
 *       min(existing data) = quant(new data)
 *     if (key(new data) = key(existing data)) then
 *       sum = sum + quant(new data)
 *       count = count + 1
 *       // avg = sum / count
 * ct_max, ny_min, nj_min:
 *   for each data existing in the HashMap for query 2
 *     if (key(new data) = key(existing data), state = CT, quant(new data) > ct_max(existing data), 2000 <= year <= 2005) then
 *       ct_max(existing data) = quant(new data)
 *     if (key(new data) = key(existing data), state = NY, quant(new data) < ny_min(existing data)) then
 *       ny_min(existing data) = quant(new data)
 *     if (key(new data) = key(existing data), state = NJ, quant(new data) < nj_min(existing data)) then
 *       nj_min(existing data) = quant(new data)
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

public class Assignment1 {
	// transform (year, month, day) into date(mm/dd/yyyy)
	public static String date(String year, String month, String day) {
		if (Integer.parseInt(month) < 10) {
			month = "0" + month;
		}
		if (Integer.parseInt(day) < 10) {
			day = "0" + day;
		}
		return month + "/" + day + "/" + year;
	}	
	
	public static void main(String[] args) {
		String usr = "postgres";
		String pwd = "123456";
		String url = "jdbc:postgresql://localhost:5432/";
		
		HashMap<String, Result1> report1 = new HashMap<String, Result1> ();
		// HashMap(report1) to store the maximum, minimum and average data of each product
		// key = prod, value = result1
		
		HashMap<String, Result2> report2 = new HashMap<String, Result2> ();
		// HashMap(report2) to store the maximum in CT, minimum in NY and NJ data of each product and customer combination
		// key = comb(prod + cust), value = result2
		
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
				String cust = rs.getString("cust");
				String prod = rs.getString("prod");
				String state = rs.getString("state");
				int quant = Integer.parseInt(rs.getString("quant"));
				String year = rs.getString("year");
				String month = rs.getString("month");
				String day = rs.getString("day");
				String date = date(year, month, day);
				String comb = cust + prod;
				// get the original data
				
				Result1 result1 = new Result1(prod, -1, null, null, null, Integer.MAX_VALUE, null, null, null, 0, 0);
				Result2 result2 = new Result2(cust, prod, -1, null, Integer.MAX_VALUE, null, Integer.MAX_VALUE, null);
				// Result1(prod, max_q, max_cust, max_date, max_state, min_q, min_cust, min_date, min_state, count, sum)
				// Result2(cust, prod, ct_max, ct_date, ny_min, ny_date, nj_min, nj_date)
				
				// add data if there is not the same key(prod or comb) in HashMap(report1 or report2)
				if (!report1.containsKey(prod)) {
					report1.put(prod, result1);
				}
				if (!report2.containsKey(comb)) {
					report2.put(comb, result2);
				}
				
				// for each data existing in the HashMap(report1), check
				for (HashMap.Entry <String, Result1> entry : report1.entrySet()) {
					// if the new one have the same key and quant is bigger, change the value(quant, cust, date, state) of maximum
					if (entry.getKey().equals(prod) && entry.getValue().getMaxQ() < quant) {
						entry.getValue().setMax(quant, cust, date, state);
					}
					// if the new one have the same key and quant is smaller, change the value(quant, cust, date, state) of minimum
					if (entry.getKey().equals(prod) && entry.getValue().getMinQ() > quant) {
						entry.getValue().setMin(quant, cust, date, state);
					}
					// if the new one have the same key, change the value of sum and count
					if (entry.getKey().equals(prod)) {
						entry.getValue().setSumCount(quant);
					}
				}
				
				// for each data existing in the HashMap(report2), check
				for (HashMap.Entry <String, Result2> entry : report2.entrySet()) {
					// if the new one have the same key, state is CT, quant is bigger, 2000 <= year <= 2005, change the value(quant, date) of CT
					if (entry.getKey().equals(comb) && state.equals("CT") && entry.getValue().getCTQ() < quant && Integer.parseInt(year) >= 2000 && Integer.parseInt(year) <= 2005) {
						entry.getValue().setCTmax(quant, date);
					}
					// if the new one have the same key, state is NY, quant is smaller, change the value(quant, date) of NY
					if (entry.getKey().equals(comb) && state.equals("NY") && entry.getValue().getNYQ() > quant) {
						entry.getValue().setNYmin(quant, date);
					}
					// if the new one have the same key, state is NJ, quant is smaller, change the value(quant, date) of NJ
					if (entry.getKey().equals(comb) && state.equals("NJ") && entry.getValue().getNJQ() > quant) {
						entry.getValue().setNJmin(quant, date);
					}
				}
			}
		}
		
		catch(SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
		
		// sort by key(prod)
		List<HashMap.Entry<String, Result1>> report1_order = new ArrayList<HashMap.Entry<String, Result1>>(report1.entrySet());
		Collections.sort(report1_order, new Comparator<HashMap.Entry<String, Result1>>() {
			@Override
			public int compare(Entry<String, Result1> arg0, Entry<String, Result1> arg1) {
				return arg0.getKey().compareTo(arg1.getKey());
			}
		});
		
		// output
		System.out.println("");
		System.out.println("");
		System.out.println("PRODUCT   MAX_Q  CUSTOMER  DATE        ST  MIN_Q  CUSTOMER  DATE        ST  AVG_Q");
		System.out.println("========  =====  ========  ==========  ==  =====  ========  ==========  ==  =====");
		for (HashMap.Entry<String, Result1> entry : report1_order) {
			entry.getValue().print();
		}	
		
		// sort by key(prod_cust)
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
		System.out.println("CUSTOMER  PRODUCT   CT_MAX  DATE        NY_MIN  DATE        NJ_MIN  DATE");
		System.out.println("========  ========  ======  ==========  ======  ==========  ======  ==========");
		for (HashMap.Entry<String, Result2> entry : report2_order) {
			entry.getValue().print();
		}
	}
}

class Result1 {
	private String prod, max_cust, max_date, max_state, min_cust, min_date, min_state;
	private int max_q, min_q, count, sum;
	
	public Result1() {
	}
	public Result1(String prod, int max_q, String max_cust, String max_date, String max_state, int min_q, String min_cust, String min_date, String min_state, int count, int sum) {
		this.prod = prod;
		this.max_q = max_q;
		this.max_cust = max_cust;
		this.max_date = max_date;
		this.max_state = max_state;
		this.min_q = min_q;
		this.min_cust = min_cust;
		this.min_date = min_date;
		this.min_state = min_state;
		this.count = count;
		this.sum = sum;
	}
	public int getMaxQ() {
		return max_q;
	}
	public int getMinQ() {
		return min_q;
	}
	public void setMax(int max_q, String max_cust, String max_date, String max_state) {
		this.max_q = max_q;
		this.max_cust = max_cust;
		this.max_date = max_date;
		this.max_state = max_state;
	}
	public void setMin(int min_q, String min_cust, String min_date, String min_state) {
		this.min_q = min_q;
		this.min_cust = min_cust;
		this.min_date = min_date;
		this.min_state = min_state;
	}
	public void setSumCount(int q) {
		sum += q;
		count++;
	}
	public void print() {
		System.out.printf("%-8s  ", prod);
		System.out.printf("%5s  ", max_q);
		System.out.printf("%-8s  ", max_cust);
		System.out.printf("%10s  ", max_date);
		System.out.printf("%-2s  ", max_state);
		System.out.printf("%5s  ", min_q);
		System.out.printf("%-8s  ", min_cust);
		System.out.printf("%10s  ", min_date);
		System.out.printf("%-2s  ", min_state);
		System.out.printf("%5s", sum / count);
		System.out.println("");
	}
}

class Result2 {
	private String cust, prod, ct_date, ny_date, nj_date;
	private int ct_max, ny_min, nj_min;
	
	public Result2() {
	}
	public Result2(String cust, String prod, int ct_max, String ct_date, int ny_min, String ny_date, int nj_min, String nj_date) {
		this.cust = cust;
		this.prod = prod;
		this.ct_max = ct_max;
		this.ct_date = ct_date;
		this.ny_min = ny_min;
		this.ny_date = ny_date;
		this.nj_min = nj_min;
		this.nj_date = nj_date;
	}
	public int getCTQ() {
		return ct_max;
	}
	public int getNYQ() {
		return ny_min;
	}
	public int getNJQ() {
		return nj_min;
	}
	public void setCTmax(int ct_max, String ct_date) {
		this.ct_max = ct_max;
		this.ct_date = ct_date;
	}
	public void setNYmin(int ny_min, String ny_date) {
		this.ny_min = ny_min;
		this.ny_date = ny_date;
	}
	public void setNJmin(int nj_min, String nj_date) {
		this.nj_min = nj_min;
		this.nj_date = nj_date;
	}
	public void print() {
		System.out.printf("%-8s  ", cust);
		System.out.printf("%-8s  ", prod);
		if (ct_max == -1) {
			System.out.printf("%6s  ", "null");
		}
		else {
			System.out.printf("%6s  ", ct_max);
		}
		System.out.printf("%10s  ", ct_date);
		if (ny_min == Integer.MAX_VALUE) {
			System.out.printf("%6s  ", "null");
		}
		else {
			System.out.printf("%6s  ", ny_min);
		}
		System.out.printf("%10s  ", ny_date);
		if (nj_min == Integer.MAX_VALUE) {
			System.out.printf("%6s  ", "null");
		}
		else {
			System.out.printf("%6s  ", nj_min);
		}
		System.out.printf("%10s", nj_date);
		System.out.println("");
	}
}