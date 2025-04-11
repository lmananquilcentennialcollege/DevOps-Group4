package com.example.devops;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private ProgramRepository programRepository;

	@Autowired
	private EnrollmentRepository enrollmentRepository; 

	// Create student
	public Student registerStudent(Student student) {
		return studentRepository.save(student);
	}

	// Validate user credentials
	public boolean validateUser(String username, String password) {
		Optional<Student> studentOptional = studentRepository.findByUsername(username);
		if (studentOptional.isPresent()) {
			Student student = studentOptional.get();
			
			// Directly compare the plain text password
			return password.equals(student.getPassword());
		}
		return false; // User not found
	}

	public Optional<Program> findProgramById(Long programCode) {
		return programRepository.findById(programCode);
	}

	public List<Program> findAllProgram() {
		return programRepository.findAll();
	}

	public Enrollment saveEnrollment(Enrollment enrollment) {
		return enrollmentRepository.save(enrollment);
	}

	public Optional<Student> findByUsername(String username) {
		return studentRepository.findByUsername(username);
	}

	public Optional<Student> findStudentById(Long studentId) {
		return studentRepository.findById(studentId);
	}

	public Student updateStudent(Student student) {
		return studentRepository.save(student); // Save updated student details
	}
	
	
}
