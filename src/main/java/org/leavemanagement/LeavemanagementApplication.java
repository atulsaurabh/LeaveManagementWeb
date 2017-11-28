package org.leavemanagement;



import org.leavemanagement.config.LeaveManagementConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(LeaveManagementConfiguration.class)
public class LeavemanagementApplication extends SpringBootServletInitializer
{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(LeavemanagementApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(LeavemanagementApplication.class, args);
	}
}
