package com.example.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {
	private DataSource dataSource;
	
	public StudentDbUtil(DataSource theDataSource) {
		dataSource = theDataSource;
	}
	
	public List<Student> getStudent() throws Exception {
		
		List<Student> students = new ArrayList<>();
		
;		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
		// Step 1: set up connection
		myConn = dataSource.getConnection();
		
		// Step 2: create sql statement
		String sql = "select * from student order by last_name";
		myStmt = myConn.createStatement();
		
		// Step 3: execute query
		myRs = myStmt.executeQuery(sql);
		
		// Step 4: process result
		while (myRs.next()) {
			
			// Retrieve data from result set row
			int id = myRs.getInt("id");
			String firstName = myRs.getString("first_name");
			String lastName = myRs.getString("last_name");
			String email = myRs.getString("email");
			
			// Create Student object 
			Student tempStudent = new Student(id, firstName, lastName, email);
			
			// Add student object to list
			students.add(tempStudent);
		}
		
		
		
		} finally {
			// Step 5: close JDBC objects
			close(myConn, myStmt, myRs);
					
		}
		return students;
		
	}

	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		// Close connection
		try {
			if (myConn != null) {
				myConn.close();
			}
			if (myStmt != null) {
				myStmt.close();
			}
			if (myRs != null) {
				myRs.close();
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}			
	}

	public void addStudent(Student theStudent) throws Exception{
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			
			// Get db connection
			myConn = dataSource.getConnection();
			
			// create sql for insert
			String sql = "insert into student "
					+ "(first_name, last_name, email) "
					+ "values (?, ?, ?)";
			
			myStmt = myConn.prepareStatement(sql);
			// set the param values for the student
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			
			// execute sql insert
			myStmt.execute();
			
		} finally {
		
			// clean up JDBC objects
			close(myConn, myStmt, null);
		}
		
	}

	public Student getStudent(String id) throws Exception{
		Student theStudent = null;
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		
		try {
			// convert student id tp in
			int theStudentId = Integer.parseInt(id);
			// get connection to database 
			myConn = dataSource.getConnection();
			// create query to get student with id
			String sql = "select * from student where id=?";
					
			// create prepared statement
			myStmt = myConn.prepareStatement(sql);
			
			// set params
			myStmt.setInt(1, theStudentId);
			//System.out.println(theStudentId);
			// execute statment
			myRs = myStmt.executeQuery();
			
			// retrieve data from result set row
			if (myRs.next()) {
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				theStudent = new Student(theStudentId, firstName, lastName, email);
			} else {
				throw new Exception ("Could not find student id: " + theStudentId);
			}
			
			return theStudent;
			
		} finally {
			// clean up the connection
			close(myConn, myStmt, myRs);
		}
		
		
	}

	public void updateStudent(Student theStudent) throws Exception {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			// open connection to database 	
			myConn = dataSource.getConnection();
			
			// create SQL update statement
			String sql = "update student "
					+ "set first_name=?, last_name=?, email=? "
					+ "where id=?";
			
			// prepare statement
			myStmt = myConn.prepareStatement(sql);
			
			// set parameter
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			myStmt.setInt(4, theStudent.getId());
			
			// execute SQL statement
			myStmt.execute();
		
		} finally {
			// close connection
			close(myConn, myStmt, null);
		}
		
	}

	public void deleteStudent(int id) throws Exception{
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		try {
		// open connection to data source
		myConn = dataSource.getConnection();
		// create sql
		String sql = "delete from student where id=?";
		// prepare statement
		myStmt = myConn.prepareStatement(sql);
		
		// set parameter
		myStmt.setInt(1, id);
		
		// execute statement
		myStmt.execute();
		
		} finally {
			close(myConn, myStmt, null);
		}
	}

}














