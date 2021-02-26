package com.alpha.employeeregistration.services;

import java.util.Map;

import javax.mail.MessagingException;



public interface EmailService {

	
	void Simpleemail(String to,String subject,String text);
	
	void SendHTMLMail(String to,String subject,String htmlbody) throws MessagingException;
	
	void SendMailUsingThymeleafTemplate(String to,String subject,Map<String, Object> templatemodel) throws MessagingException;
	
	
	
	void sendRegistrationMail(String to,String subject,Map<String, Object> templatemodel) throws MessagingException;
	
	void sendAdminAccessRequestMail(String to,String subject,Map<String, Object> templatemodel) throws MessagingException;

	
	void sendEmployeeUpdateMail(String to,String subject,Map<String, Object> templatemodel) throws MessagingException;

	
	void sendOOFSubmittedMail(String to,String subject,Map<String, Object> templatemodel) throws MessagingException;
	
	void sendOOFApprovedMail(String to,String subject,Map<String, Object> templatemodel) throws MessagingException;
	
	void sendOOFRejectedMail(String to,String subject,Map<String, Object> templatemodel) throws MessagingException;



}


