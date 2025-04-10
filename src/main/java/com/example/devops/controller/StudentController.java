package com.example.devops.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.devops.StudentService;
import com.example.devops.model.LoginBean;
import com.example.devops.model.Program;
import com.example.devops.model.Student;

import jakarta.servlet.http.HttpSession;

@Controller
public class StudentController {

	@Autowired
	private StudentService studentService;

	// Inject HttpSession
	@Autowired
	private HttpSession session;

	// Show registration form
	@GetMapping("/")
	public String showRegistrationForm(Model model) {
		model.addAttribute("student", new Student()); // Add an empty student object for registration
		model.addAttribute("loginForm", new LoginBean()); // Add an empty login form object
		return "index"; // Return index.html
	}

	@PostMapping("/students/login")
	public String login(@ModelAttribute LoginBean loginForm, Model model, HttpSession session) {
		// Verify user credentials
		if (studentService.validateUser(loginForm.getUsername(), loginForm.getPassword())) {
			// Successful login
			Optional<Student> studentOpt = studentService.findByUsername(loginForm.getUsername());
			if (studentOpt.isPresent()) {
				session.setAttribute("studentId", studentOpt.get().getStudentId()); // Store student ID in session
			}
			return "redirect:/students/programs"; // Redirect to the students' list page
		}

		// If login fails, return to the index page with an error message
		model.addAttribute("student", new Student()); // Add an empty student object for registration
		model.addAttribute("loginForm", loginForm); // Retain the login form values
		model.addAttribute("loginError", "Invalid username or password"); // Set the error message
		return "index"; // Return to index.html to show the login form again
	}

	// Handle registration form submission
	@PostMapping("/students/register")
	public String registerStudent(@ModelAttribute Student student) {
		studentService.registerStudent(student);
		return "success"; // Redirect after successful registration
	}

	// List all programs
	@GetMapping("/students/programs")
	public String listPrograms(Model model) {
		model.addAttribute("programs", studentService.findAllProgram());
		return "programs"; // Thymeleaf template to display students
	}

	@PostMapping("/students/confirmation")
	public String confirmPrograms(@RequestParam("selectedPrograms") List<String> selectedProgramCodes, Model model) {
		List<Program> selectedPrograms = new ArrayList<>();
		for (String code : selectedProgramCodes) {
			Optional<Program> programOptional = studentService.findProgramById(Long.valueOf(code));
			programOptional.ifPresent(selectedPrograms::add);
		}

		double totalFee = selectedPrograms.stream().mapToDouble(Program::getFee).sum();

		model.addAttribute("selectedPrograms", selectedPrograms);
		model.addAttribute("totalFee", totalFee);

		// Store the totalFee in the model to pass it to the confirmation page if needed
		model.addAttribute("totalFee", totalFee);

		return "confirmation"; // Return the confirmation view (which already exists)
	}

	@PostMapping("/students/save")
	public String saveEnrollment(@RequestParam("selectedPrograms") List<String> selectedProgramCodes,
			@RequestParam("programFees") List<Double> programFees, // Receive program fees
			@RequestParam("totalfee") double totalFee, Model model) {

		// Retrieve the student ID from the session
		Long studentId = (Long) session.getAttribute("studentId");

		if (studentId != null) {
			for (int i = 0; i < selectedProgramCodes.size(); i++) {
				String code = selectedProgramCodes.get(i);
				Double fee = programFees.get(i); // Get the fee associated with the program code

				try {
					Long programCode = Long.valueOf(code);

					Enrollment enrollment = new Enrollment();
					enrollment.setStudentId(studentId);
					enrollment.setProgramCode(programCode);
					enrollment.setAmountPaid(fee); // Set fee from the programFees list
					enrollment.setStartDate(new Date());
					enrollment.setStatus("Enrolled");

					studentService.saveEnrollment(enrollment);
				} catch (NumberFormatException e) {
					model.addAttribute("error", "Invalid program code: " + code);
					return "error";
				}
			}

			return "enrollSuccess";
		} else {
			model.addAttribute("error", "User not found");
			return "error";
		}
	}

	@GetMapping("/students/profile")
	public String editProfile(Model model, HttpSession session) {
		Long studentId = (Long) session.getAttribute("studentId");
		if (studentId != null) {
			Optional<Student> studentOpt = studentService.findStudentById(studentId); // Fetch student by ID
			if (studentOpt.isPresent()) {
				model.addAttribute("student", studentOpt.get());
				return "profile"; // Return profile.html
			}
		}
		model.addAttribute("error", "User not found");
		return "error"; // Redirect to error page if student not found
	}

	@PostMapping("/students/update")
	public String updateProfile(@ModelAttribute Student student, HttpSession session) {
		Long studentId = (Long) session.getAttribute("studentId");
		if (studentId != null) {
			Optional<Student> existingStudentOpt = studentService.findStudentById(studentId);
			if (existingStudentOpt.isPresent()) {
				Student existingStudent = existingStudentOpt.get();

				// Retain existing password if the new one is not provided
				if (student.getPassword() == null || student.getPassword().isEmpty()) {
					student.setPassword(existingStudent.getPassword());
				}

				// Update the student details
				student.setStudentId(studentId);
				studentService.updateStudent(student);
				return "enrollSuccess"; // Redirect to success page after update
			}
		}
		return "error"; // Handle error case
	}

}
