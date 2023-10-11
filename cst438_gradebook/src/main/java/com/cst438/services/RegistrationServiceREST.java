package com.cst438.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Enrollment;

@Service
@ConditionalOnProperty(prefix = "registration", name = "service", havingValue = "rest")
@RestController
public class RegistrationServiceREST implements RegistrationService {

	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}") 
	String registration_url;
	
	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}
	
	@Override	
	public void sendFinalGrades(int course_id , FinalGradeDTO[] grades) { 
		System.out.println("Hellpo");
		String urlString = registration_url + "/" + course_id;
		//TODO use restTemplate to send final grades to registration service
         restTemplate.put(urlString, grades);
//	if (response.getStatusCodeValue() == 200) {
//		// update database
//		System.out.println("sent");
//	} else {
//		// error.
//		System.out.println(
//                  "Error: unable to post multiply_level "+
//                   response.getStatusCodeValue());
//	}
		
	}
	
	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	
	/*
	 * endpoint used by registration service to add an enrollment to an existing
	 * course.
	 */
	@PostMapping("/enrollment")
	@Transactional
	public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
		
		// Receive message from registration service to enroll a student into a course.
		Enrollment enrollment = new Enrollment();
		Optional<Course> course = courseRepository.findById(enrollmentDTO.courseId());
		enrollment.setCourse(course.get());
		enrollment.setStudentName(enrollmentDTO.studentName());
		enrollment.setStudentEmail(enrollmentDTO.studentEmail());
		enrollment.setId(enrollmentDTO.id());
		
		enrollmentRepository.save(enrollment);
		System.out.println("GradeBook addEnrollment "+ enrollmentDTO);
		
		//TODO remove following statement when complete.
		return enrollmentDTO;
		
	}

}
