package com.cst438.controllers;

import java.io.Console;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin 
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@GetMapping("/assignment")
	public AssignmentDTO[] getAllAssignmentsForInstructor() {
		// get all assignments for this instructor
		System.out.println("HI!");
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
		for (int i=0; i<assignments.size(); i++) {
			Assignment as = assignments.get(i);
			AssignmentDTO dto = new AssignmentDTO(
					as.getId(), 
					as.getName(), 
					as.getDueDate().toString(), 
					as.getCourse().getTitle(), 
					as.getCourse().getCourse_id());
			result[i]=dto;
		}
		return result;
	}
	
	@GetMapping("/assignment/{assignmentId}")
	public AssignmentDTO getAssignmentInfo(@PathVariable int assignmentId) {
		// get all assignments for this instructor
		System.out.println("HI!");
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		Assignment as = new Assignment();
		as = checkAssignment(assignmentId);
		System.out.println(as.toString());
		AssignmentDTO dto = new AssignmentDTO(
				as.getId(), 
				as.getName(), 
				as.getDueDate().toString(), 
				as.getCourse().getTitle(), 
				as.getCourse().getCourse_id());
		System.out.println(dto);
		return dto;
	}
	
	@PostMapping("/assignment/new")
	@Transactional
	public void createAssignment(@RequestParam("id") int courseId, @RequestParam("name") String name, @RequestParam("due") String date) {
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		Course course = courseRepository.findById(courseId).orElse(null);
		
		if (!course.getInstructor().equals(instructorEmail)) {
			throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not Authorized. " );
		}
		if(course != null) {
			Assignment assignment = new Assignment();
			assignment.setDueDate(Date.valueOf(date));
			assignment.setName(name);
			assignment.setCourse(course);
			assignmentRepository.save(assignment);
		}
		else {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Assignment cannot be created for this course. " + courseId);
		}
		
	}
	
	@PutMapping("/assignment/update/")
	@Transactional
	public void updateAssignmentName(@RequestParam("id") Integer assignmentId, @RequestParam("name") String name, @RequestParam("date") String date) {
		Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null); 
		String email = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		if (!assignment.getCourse().getInstructor().equals(email)) {
			throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not Authorized. " );
		}
		if(assignment != null) {
			Date d = new java.sql.Date(0);
			d = Date.valueOf(date);
			assignment.setName(name);
			assignment.setId(assignmentId);
			assignment.setDueDate(d);
			assignmentRepository.save(assignment);
		}
		else {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Cannot Update Assignment " + assignmentId);
		}
		
	}
	
	

	@DeleteMapping("/assignment/{assignmentId}")
    @Transactional
    public void deleteAssignment(@PathVariable int assignmentId) {
		String email = "dwisneski@csumb.edu";  // user name (should be instructor's email)
		System.out.println("DELETING!");
		if(email.equals("dwisneski@csumb.edu")) {
			//Find assignment by ID
			Assignment assignment = new Assignment();
			assignment = checkAssignment(assignmentId);
			assignmentRepository.delete(assignment);	
		}
		return;
	}
	
	private Assignment checkAssignment(int assignmentId) {
		// get assignment 
		Optional<Assignment> search = assignmentRepository.findById(assignmentId);
		System.out.println("WOAH!");
		Assignment assignment = new Assignment();
		if(search.isPresent()) {
			assignment = search.get();
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
		}
		
		return assignment;
		
		
	}
}
