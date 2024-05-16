package com.example.admin;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.internet.MimeMessage;

@RestController

@CrossOrigin(origins = {"http://localhost:4200"}) // to whoem (clients) we r providing acess
@RequestMapping("admin")
public class AppUserController {
	@Autowired
	private AppUserRepository appUserRepository;
	@Autowired
	private JavaMailSender javaMailSender;
	@PostMapping("signup")
	public ResponseEntity<AppUserStatusDTO>  save (@RequestBody AppUser appuser) throws Exception
	{
		AppUserStatusDTO dto = new AppUserStatusDTO();
		dto.setStatus(false);
		dto.setMessage("Signup failed. try again");
		appuser.setStatus(1);
		String code = generateActivationKey();
		appuser.setActivationKey(code);
		//appuser.getAddress().setAppUser(appuser);
		
		appUserRepository.save(appuser);
		sendMail(appuser.getUsername(), code);
		dto.setStatus(true);
		dto.setMessage("Signup Sucessfull. we have sent a mail to" + appuser.getUsername() + "pls go to the same mail account and activate the same");
		return ResponseEntity.ok(dto);
		
	}
	
	@GetMapping("signup/activate/{username}/{code}")
	public ResponseEntity<String> activate(@PathVariable String username, @PathVariable String code)
	
	{
		String message = "Activation Failed";
		Optional<AppUser> optional = appUserRepository.findById(username);
		if(optional.isPresent())
		{
			AppUser appUser = optional.get();
			if(appUser.getActivationKey().equals(code))
			{
				message = "Activation sucess";
				appUser.setStatus(2);
				appUserRepository.save(appUser);
			}
		}
		return ResponseEntity.ok(message);
	}
	private String generateActivationKey()
	{
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
		double random;
		char c1 ;
		for(int i = 1; i <= 20; i++)
		{
			random = Math.random(); // b/n 0 - 1
			c1 = str.charAt((int)(random * str.length())); // 0.5 * 62
			sb.append(c1);
		}
		return sb.toString();
	}
	
	@PostMapping("login")    
	public ResponseEntity<LoginStatusDTO> login(@RequestBody AppUser appUser) 
	{
	    LoginStatusDTO dto = new LoginStatusDTO();
	    dto.setStatus(false);
	    dto.setMessage("Login Failed. try again");
		Optional<AppUser> optional = appUserRepository.findById(appUser.getUsername());

			if(optional.isPresent() )
			{
				AppUser dbUser = optional.get();
				if(dbUser.getStatus() == 2)
			{
				if( dbUser.getPassword().equals(appUser.getPassword()))
				{
					dto.setStatus(true);
					dto.setMessage("Login Sucessful");
				}
			}
			else
			{
				dto.setMessage("Your email account not activated. pls activate first before login");
			}
			
		}
		return ResponseEntity.ok(dto);
	  }	

	
	private void sendMail(String username, String code) throws Exception
	{
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(username);
		helper.setSubject("Activation your email account");
		helper.setText("<h1><a href='http://localhost:9090/admin/signup/activate/" + username + "/" + code +"'>Click to activate your email account </a></h1>", true);
		javaMailSender.send(message);
	}
	@PostMapping("changePassword")
	public ResponseEntity<ChangePasswordDTO> changePassword(@RequestBody AppUser appUser)
	{
		ChangePasswordDTO dto = new ChangePasswordDTO();
		dto.setStatus(false);
		dto.setMessage("Change Passsword failed. try again");
		Optional<AppUser> optional = appUserRepository.findById(appUser.getUsername());
		if(optional.isPresent())
		{
			AppUser dbUser = optional.get();
			dbUser.setPassword(appUser.getPassword());
			appUserRepository.save(dbUser);
			dto.setStatus(true);
			dto.setMessage("Password Changed Sucessfully");
			
		}

		return ResponseEntity.ok(dto);
	}
	@PostMapping("forgottenPassword")
	public ResponseEntity<ForgottenPasswordDTO> forgottenPassword(@RequestBody AppUser appUser) throws Exception
	{
		ForgottenPasswordDTO dto = new ForgottenPasswordDTO();
		dto.setStatus(false);
		dto.setMessage("Forgotten Passsword failed. try again");
		Optional<AppUser> optional = appUserRepository.findById(appUser.getUsername());
		if(optional.isPresent())
		{
			AppUser dbUser = optional.get();
			String emailId = appUser.getUsername();
			String password = appUser.getPassword();
			//sendMailWithPassword(emailId, password);
			sendMailWithPasswordResetLink(emailId);
			dto.setStatus(true);
			//dto.setMessage("Password Sent To Your Email Account.");
			dto.setMessage("Password Reset link Sent To Your Email Account.");
		}
			return ResponseEntity.ok(dto);
	}
	
	private void sendMailWithPassword(String emailId, String password) throws Exception
	{
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(emailId);
		helper.setSubject("password to your  account in the admin app");
		helper.setText("<h1>Password:" + password + "</h1>", true);
		javaMailSender.send(message);
		
	}
	private void sendMailWithPasswordResetLink(String emailId) throws Exception
	{
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(emailId);
		helper.setSubject("password to your  account in the admin app");
		helper.setText("<h1><a href='http://localhost:9090/admin/resetPassword/" + emailId + "'>Reset Password Link</a></h1>" , true);
		javaMailSender.send(message);
		
	}
	@GetMapping("resetPassword/{emailId}")
	public ResponseEntity<String> resetPassword(@PathVariable String emailId) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<form action='http://localhost:9090/admin/resetPasswordSave' method='get'>");
		sb.append("<input type='hidden' name='username' value='" + emailId + "'><br>");
		sb.append("Enter New Password: <input type='password' name='password'><br>");
		sb.append("Confirm New Password: <input type='password' name='confirmPassword'><br>");
		sb.append("<input type='submit' value='submit' onclick='return validate()'>");
		sb.append("</form>");
		sb.append("<div id='error'></div>");
		sb.append("<script>");
		sb.append("function validate(){");
		sb.append("if(document.form[0].password.value != document.form[0].confirmPassword.value) {");
		sb.append("document.getElementById('error').innerHTML = 'Password and Confirm Password are not same';");
		sb.append("return false;");
		sb.append("}");
		sb.append("}");
		sb.append("</script>");
		return ResponseEntity.ok(sb.toString());
	}
	
	@GetMapping("resetPasswordSave")
	public ResponseEntity<String> resetPasswordSave(@RequestParam String username, @RequestParam String password) 
	{
		String message = "Password Reset Failed";
		Optional<AppUser> optional = appUserRepository.findById(username);
		if(optional.isPresent())
		{
			AppUser dbUser = optional.get();
			dbUser.setPassword(password);
			appUserRepository.save(dbUser);
			message = "Password has been Reset Sucessfully";
		}
		
		
		return ResponseEntity.ok(message);
	}
	
	@GetMapping("getUser/{username}")
	public ResponseEntity<AppUser> getUser(@PathVariable String username)
	{
		return ResponseEntity.ok(appUserRepository.findById(username).get());
	}
	
	@PostMapping("saveAppUserDetails")
	public ResponseEntity<AppUserStatusDTO>  saveAppUserDetails (@RequestBody AppUser appuser) 
	{
		AppUserStatusDTO dto = new AppUserStatusDTO();
		dto.setStatus(false);
		dto.setMessage("App User Deatails failed. try again");
	//	appuser.setStatus(1);
		appuser.getAddress().setAppUser(appuser);
		
		appUserRepository.save(appuser);
		System.out.println("updated sucessfully");
		dto.setStatus(true);
		dto.setMessage("App User details saved sucessfully");
		return ResponseEntity.ok(dto);
		
	}
		
	
}
//if(optional.isPresent() && optional.get().getPassword().equals(appUser.getPassword()))