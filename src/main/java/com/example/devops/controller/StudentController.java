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
import com.example.devops.model.Enrollment;
import com.example.devops.model.LoginBean;
import com.example.devops.model.Program;
import com.example.devops.model.Student;

import jakarta.servlet.http.HttpSession;

@Controller
public class StudentController {

	@Autowired
	private StudentService studentService;

	@Autowired
	private HttpSession session;

	@GetMapping("/")
	public String showRegistrationForm(Model model) {
		model.addAttribute("student", new Student());
		model.addAttribute("loginForm", new LoginBean()); 
		return "index"; 
	}

	@PostMapping("/students/login")
	public String login(@ModelAttribute LoginBean loginForm, Model model, HttpSession session) {
		
		if (studentService.validateUser(loginForm.getUsername(), loginForm.getPassword())) {
			
			Optional<Student> studentOpt = studentService.findByUsername(loginForm.getUsername());
			if (studentOpt.isPresent()) {
				session.setAttribute("studentId", studentOpt.get().getStudentId()); 
			}
			return "redirect:/students/programs"; 
		}

		model.addAttribute("student", new Student()); 
		model.addAttribute("loginForm", loginForm);
		model.addAttribute("loginError", "Invalid username or password"); 
		return "index"; 
	}

	@PostMapping("/students/register")
	public String registerStudent(@ModelAttribute Student student) {
		studentService.registerStudent(student);
		return "success"; 
	}

	@GetMapping("/students/programs")
	public String listPrograms(Model model) {
		model.addAttribute("programs", studentService.findAllProgram());
		return "programs"; 
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
		model.addAttribute("totalFee", totalFee);

		return "confirmation"; 
	}

	@PostMapping("/students/save")
	public String saveEnrollment(@RequestParam("selectedPrograms") List<String> selectedProgramCodes,
			@RequestParam("programFees") List<Double> programFees, 
			@RequestParam("totalfee") double totalFee, Model model) {

		Long studentId = (Long) session.getAttribute("studentId");

		if (studentId != null) {
			for (int i = 0; i < selectedProgramCodes.size(); i++) {
				String code = selectedProgramCodes.get(i);
				Double fee = programFees.get(i); 

				try {
					Long programCode = Long.valueOf(code);

					Enrollment enrollment = new Enrollment();
					enrollment.setStudentId(studentId);
					enrollment.setProgramCode(programCode);
					enrollment.setAmountPaid(fee); 
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
			Optional<Student> studentOpt = studentService.findStudentById(studentId); 
			if (studentOpt.isPresent()) {
				model.addAttribute("student", studentOpt.get());
				return "profile"; 
			}
		}
		model.addAttribute("error", "User not found");
		return "error"; 
	}

	@PostMapping("/students/update")
	public String updateProfile(@ModelAttribute Student student, HttpSession session) {
		Long studentId = (Long) session.getAttribute("studentId");
		if (studentId != null) {
			Optional<Student> existingStudentOpt = studentService.findStudentById(studentId);
			if (existingStudentOpt.isPresent()) {
				Student existingStudent = existingStudentOpt.get();

				if (student.getPassword() == null || student.getPassword().isEmpty()) {
					student.setPassword(existingStudent.getPassword());
				}

				student.setStudentId(studentId);
				studentService.updateStudent(student);
				return "enrollSuccess"; 
			}
		}
		return "error"; 
	}

}
