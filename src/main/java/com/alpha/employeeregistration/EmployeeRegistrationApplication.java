package com.alpha.employeeregistration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;

import com.alpha.employeeregistration.dao.EmployeeRepository;
import com.alpha.employeeregistration.dao.OOFRepository;
import com.alpha.employeeregistration.model.Employee;


@SpringBootApplication
public class EmployeeRegistrationApplication implements CommandLineRunner
{

	public static void main(String[] args) {
		SpringApplication.run(EmployeeRegistrationApplication.class, args);	
		
	}
	
//	
//	@Bean
//	public PasswordEncoder encoder() {
//	    return new BCryptPasswordEncoder();
//	}
	
	@Autowired
	private EmployeeRepository empRepo;
	
	@Autowired
	private OOFRepository oofRepo;

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
//		String str="06/16/1994";
//		DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
//	 	Date date = format.parse(str);
//		Employee emp=new Employee("Alpha","K A", "alphaka94@gmail.com", "12345678", date, "Developer", "SoftwareEngineer");
//		
//		empRepo.save(emp);
		
		
		
	}

}
