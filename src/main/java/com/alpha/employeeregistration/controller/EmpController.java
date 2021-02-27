package com.alpha.employeeregistration.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.alpha.employeeregistration.dao.EmployeeRepository;
import com.alpha.employeeregistration.dao.OOFRepository;
import com.alpha.employeeregistration.model.Employee;
import com.alpha.employeeregistration.model.OutofOffice;

import com.alpha.employeeregistration.services.NotificationServiceImpl;

@Controller
public class EmpController {

	private Logger logger = LoggerFactory.getLogger(EmpController.class);
	@Autowired
	
	private EmployeeRepository empRepo;
	
	
 
	
	String baseUrl;
			

	@Autowired
	private OOFRepository oofRepo;

	@Autowired
	private NotificationServiceImpl notification;

	@Value("${spring.signature}")
	private String signature;
	
	@Value("${spring.companyid}")
	private String company;

	@Value("${spring.location}")
	private String location;

	List<String> designationlist = Arrays.asList("", "Junior Engineer", "Software Engineer", "QA Engineer",
			"JAVA Developer", "SQL Developer");

	List<String> departmentlist = Arrays.asList("", "Development", "Sales", "Marketing", "HR", "Finance");

	List<String> categorylist = Arrays.asList("", "Sick Leave", "Casual Leave", "Marriage Leave", "Maternity Leave");

	@RequestMapping("/")
	public String homepage( Model model) {
		baseUrl=ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
	System.out.println(baseUrl);
	System.out.println();
		model.addAttribute("employee", new Employee());
		return "index";

	}

	@RequestMapping("/register")
	public String registor(Model model) {
		model.addAttribute("employee", new Employee());

		model.addAttribute("designationlist", designationlist);
		model.addAttribute("departmentlist", departmentlist);

		return "register_form";

	}

	@PostMapping("/signup")
	public String insertEmployee(Employee emp, Model model) {

		Employee exitsingemployee = empRepo.findByEmail(emp.getEmail());
		String message;

		if (exitsingemployee == null) {
			
 		
			String to = emp.getEmail();
			String subject = "Registration";

			Map<String, Object> templatemodel = new HashMap<String, Object>();
			templatemodel.put("name", emp.getFirstname() + " " + emp.getLastname());
			templatemodel.put("signature", signature);
			templatemodel.put("location", location);
			
			System.out.println(emp);
			empRepo.save(emp);

			try {

				notification.sendRegistrationMail(to, subject, templatemodel);
				// notification.sendRegMail(emp);
			} catch (Exception e) {
				logger.info("Error sending mail..................");

			}
			message = "Registered successfully.......";

		} else {
			message = "Employee already exists.......";
		}

		model.addAttribute("message", message);
		model.addAttribute("url", "/");
		model.addAttribute("urlcontent", "Login");

		return "registration_response";

	}
 

	@GetMapping("/dashboard")
	public String employeeDashboard(Employee emp, Model model) {

		Employee employee = empRepo.findByEmailPassword(emp.getEmail(), emp.getPassword());
		
		Employee employeeemail = empRepo.findByEmail(emp.getEmail());
		if (employee != null) {
			model.addAttribute("employee", employee);
			model.addAttribute("name", employee.getFirstname() + " " + employee.getLastname());
			model.addAttribute("designationlist", designationlist);
			model.addAttribute("departmentlist", departmentlist);
			return "employee_dashboard";
		} else if (employeeemail != null) {
			String message = "Incorrect Password...........";
			model.addAttribute("message", message);
			model.addAttribute("url", "/");
			model.addAttribute("urlcontent", "Login");

			return "registration_response";
		}

		else {
			String message = "User doesn't exits";
			model.addAttribute("message", message);
			model.addAttribute("url", "/");
			model.addAttribute("urlcontent", "Login");

			return "registration_response";
		}

	}
	
  
	@GetMapping("/{empid}")
	public String updateForm(@PathVariable Long empid, Model model) {

		Employee employee = empRepo.findById(empid).orElseThrow(() -> new ResourceAccessException("Employee not found"));
	 
		model.addAttribute("employee", employee);
		
		model.addAttribute("designationlist", designationlist);
		model.addAttribute("departmentlist", departmentlist);
		return "employee_edit";

	}

	@GetMapping("/admin/{empid}")
	public String adminUpdateForm(@PathVariable Long empid, Model model) {

		Employee employee = empRepo.findById(empid).orElseThrow(() -> new ResourceAccessException("Employee not found"));

		model.addAttribute("employee", employee);
		model.addAttribute("designationlist", designationlist);
		model.addAttribute("departmentlist", departmentlist);
		return "admin_edit";

	}


	@PostMapping("/update/{empid}")
	public String updateEmployee(Employee employee,@PathVariable long empid, Model model) {
		
		Employee employeeexists= empRepo.findById(empid).orElseThrow(() -> new ResourceAccessException("Employee not found"));
		 
		
		
		employeeexists.setFirstname(employee.getFirstname());
		employeeexists.setLastname(employee.getLastname());
		employeeexists.setDob(employee.getDob());
		employeeexists.setDesignation(employee.getDesignation());
		employeeexists.setDepartment(employee.getDepartment());
		employeeexists.setUpdateddate(new Date());
		 
		
		String to = employeeexists.getEmail();
		String subject = "Profile Updated";

		Map<String, Object> templatemodel = new HashMap<String, Object>();
		templatemodel.put("name", employeeexists.getFirstname() + " " + employeeexists.getLastname());
		templatemodel.put("signature", signature);
		templatemodel.put("location", location);
	
		empRepo.save(employeeexists);
		try {
			notification.sendEmployeeUpdateMail(to, subject, templatemodel);
		} catch (Exception e) {
			logger.info("Error sending mail..................");

		}

		String message = "Successfully updated.......";
		model.addAttribute("message", message);
 
		return "response";

	}
	
	
	@PostMapping("/admin/update/{empid}")
	public String updateEmployeeByAdmin(Employee employee,@PathVariable long empid, Model model) {
		
		Employee employeeexists= empRepo.findById(empid).orElseThrow(() -> new ResourceAccessException("Employee not found"));
		 
		
		employeeexists.setFirstname(employee.getFirstname());
		employeeexists.setLastname(employee.getLastname());
		employeeexists.setDob(employee.getDob());
		employeeexists.setDesignation(employee.getDesignation());
		employeeexists.setDepartment(employee.getDepartment());
		employeeexists.setStatus(employee.getStatus());
		employeeexists.setAdmin(employee.getAdmin());
		employeeexists.setUpdateddate(new Date());
		
		 
		
		String to = employeeexists.getEmail();
		String subject = "Profile Updated";

		Map<String, Object> templatemodel = new HashMap<String, Object>();
		templatemodel.put("name", employeeexists.getFirstname() + " " + employeeexists.getLastname());
		templatemodel.put("signature", signature);
		templatemodel.put("location", location);
	
		empRepo.save(employeeexists);
		try {
			notification.sendEmployeeUpdateMail(to, subject, templatemodel);
		} catch (Exception e) {
			logger.info("Error sending mail..................");

		}

		String message = "Successfully updated.......";
		model.addAttribute("message", message);
 

		return "response";

	}
	

	@GetMapping("/adminAccessRequest/{empid}")
	public String adminAccessRequest(@PathVariable Long empid,Model model) {
		Employee emp= empRepo.findById(empid).orElseThrow(() -> new ResourceAccessException("Employee not found"));
		String to = company;
		String subject = "Admin access request";
		
		System.out.println(baseUrl);
		System.out.println();

		Map<String, Object> templatemodel = new HashMap<String, Object>();
		templatemodel.put("name", emp.getFirstname() + " " + emp.getLastname());
		templatemodel.put("empid", emp.getEmpid());
		templatemodel.put("url",baseUrl);
		templatemodel.put("signature", signature);
		templatemodel.put("location", location);
	 
		empRepo.save(emp);
		
		try {
			notification.sendAdminAccessRequestMail(to, subject, templatemodel);
		} catch (Exception e) {
			logger.info("Error sending mail..................");

		}
		String message = "Your request sent successfully....";
		 
		model.addAttribute("message", message);
 
		return "response";
 

	}


	
	@GetMapping("/adminAccess/{empid}")
	public String adminAccessGranted(@PathVariable Long empid,Model model) {
		Employee emp= empRepo.findById(empid).orElseThrow(() -> new ResourceAccessException("Employee not found"));
		
		emp.setAdmin("Yes");
		emp.setUpdateddate(new Date());
		
		String to = emp.getEmail();
		String subject = "Admin access granted";
		
		Map<String, Object> templatemodel = new HashMap<String, Object>();
		templatemodel.put("name", emp.getFirstname() + " " + emp.getLastname());
		templatemodel.put("signature", signature);
		templatemodel.put("location", location);
	 
		empRepo.save(emp);
		
		try {
			notification.sendEmployeeUpdateMail(to, subject, templatemodel);
		} catch (Exception e) {
			logger.info("Error sending mail..................");

		}

		String message = "Thank you!";
		 
		model.addAttribute("message", message);
 

		return "response";

	}


	@GetMapping("/employeesList/{empid}")
	public String employeesList(@PathVariable Long empid, Model model) {

		List<Employee> employeesList = empRepo.findAllforAdmin(empid);

		System.out.println(employeesList);

		model.addAttribute("employeesList", employeesList);

		return "employees_list";

	}

	@GetMapping("/oof/{empid}")
	public String oof(@PathVariable Long empid, Model model) {

		Employee employee = empRepo.findById(empid).orElseThrow(() -> new ResourceAccessException("Employee not found"));

		model.addAttribute("name", employee.getFirstname() + " " + employee.getLastname());
		model.addAttribute("employee", employee);
		model.addAttribute("oof", new OutofOffice());

		model.addAttribute("categorylist", categorylist);

		return "out_of_office";
	}

	@PostMapping("/oof/submit/{empid}")
	public String insertOOF(@PathVariable Long empid, OutofOffice oof, Model model) throws ParseException {

		Employee employee = empRepo.findById(empid).orElseThrow(() -> new ResourceAccessException("Employee not found"));

		oof.setEmpid(empid);
		oof.setCreatedby(employee.getFirstname() + " " + employee.getLastname());
		oof.setStatus("Submitted");

		String startDate = oof.getStartdate();
		String endDate = oof.getEnddate();

		System.out.println(startDate + " " + endDate);

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

		Date startdateformatted = dateformat.parse(startDate);
		Date enddateformatted = dateformat.parse(endDate);

		long duration = enddateformatted.getTime() - startdateformatted.getTime();

		int noofdays = (int) TimeUnit.MILLISECONDS.toDays(duration);

		System.out.println("No od days :" + noofdays);
		oof.setDays(noofdays + 1);

		String to = employee.getEmail();
		String subject = "Leave Request Submitted";

		Map<String, Object> templatemodel = new HashMap<String, Object>();
		templatemodel.put("name", employee.getFirstname() + " " + employee.getLastname());
		templatemodel.put("category", oof.getCategory());
		templatemodel.put("days", oof.getDays());
		templatemodel.put("leaveperiod", oof.getStartdate() + " to " + oof.getEnddate());
		templatemodel.put("reason", oof.getReason());
		templatemodel.put("submittedby", employee.getFirstname() + " " + employee.getLastname());
		templatemodel.put("signature", signature);
		templatemodel.put("location", location);
		 
		System.out.println( templatemodel);

		oofRepo.save(oof);

		try {
			notification.sendOOFSubmittedMail(to, subject, templatemodel);
			;
		} catch (Exception e) {
			logger.info("Error sending mail..................");
		}

		String message = "Your request submitted successfully.";
		model.addAttribute("message", message);
 

		return "response";

	}

	@GetMapping("/oof/requests/{empid}")
	public String oofRequestList(@PathVariable Long empid, Model model) {

		Employee employee = empRepo.findById(empid).orElseThrow(() -> new ResourceAccessException("Employee not found"));

		List<OutofOffice> oofList = oofRepo.findByEmpID(empid);

		model.addAttribute("name", employee.getFirstname() + " " + employee.getLastname());
		model.addAttribute("oofList", oofList);

		return "oof_request_lists";
	}

	@GetMapping("/oof/employeesList/{empid}")
	public String requestsList(@PathVariable Long empid, Model model) {

		List<OutofOffice> oofLists = oofRepo.findAllforAdmin(empid);

		model.addAttribute("oofLists", oofLists);

		return "oof_others_request_lists";

	}

	@PutMapping("/oof/{id}")
	public void updateOOF(@PathVariable Long id) {

		OutofOffice oof = oofRepo.findById(id).orElseThrow(() -> new ResourceAccessException("Out of office not exists"));
		oof.setStatus("Approved");
		oof.setUpdateddate(new Date());

		oofRepo.save(oof);

	}

}
