package com.example.web.jdbc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private StudentDbUtil studentDbUtil;
	
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;
	
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		// create our student db util ... and pass in the conn pool / datasource
		try {
		studentDbUtil = new StudentDbUtil(dataSource);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// list the students ... in MVC fastion
		
		try {
			
			// Read the command parameter
			String theCommand = request.getParameter("command");
			
			if (theCommand == null) {
				theCommand = "LIST";
			}
			// route the approriate method
			switch (theCommand) {
				
			case "LIST":
				listStudents(request, response);
				break;
				
			case "ADD":
				addStudent(request, response);
				break;
				
			case "LOAD":
				loadStudent(request, response);
				break;
			
			case "UPDATE":
				updateStudent(request, response);
				break;
				
			case "DELETE":
				deleteStudent(request, response);
				break;
				
			default:
				listStudents(request, response);
			}
		
		} catch (Exception exc) {
			throw new ServletException(exc);
		}
	}		



	private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// read student id
		int id = Integer.parseInt(request.getParameter("studentID"));
		
		// delete student from database
		studentDbUtil.deleteStudent(id);
	
		// send them bak to list student (view)
		listStudents(request, response);
		
		
	}


	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Get student ID info
		int id = Integer.parseInt(request.getParameter("studentId"));
		
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		// create new student object
		Student theStudent = new Student(id, firstName, lastName, email);
			
		// update student in database 
		studentDbUtil.updateStudent(theStudent);
		
		// send reponse to JSP page
		listStudents(request, response);
	}


	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Get student ID info from form data
		String id = request.getParameter("studentID");
		
		// Get student from database (db util)
		Student theStudent = studentDbUtil.getStudent(id);
		
		// place student in request attribute
		request.setAttribute("THE_STUDENT", theStudent);
		
		// pass to JSP
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request, response);
	}


	private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// creat Student object from passing parameter
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		Student theStudent = new Student(firstName, lastName, email);
		
		// add student to database 
		studentDbUtil.addStudent(theStudent);
		
		// send response to JSP page (view)
		listStudents(request, response);
	}


	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		// get students from db util
		List<Student> listOfStudent = studentDbUtil.getStudent();
		
		// add students to the request
		request.setAttribute("STUDENT_LIST", listOfStudent);
		
		// send to JSP page (view)
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-student.jsp");
		dispatcher.forward(request, response);
	}
	
}
