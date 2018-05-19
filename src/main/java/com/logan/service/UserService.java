package com.logan.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.logan.domain.Users;
import com.logan.payload.ApiResponse;
import com.logan.repository.UsersRepository;
import com.logan.security.UserPrincipal;
import com.logan.security.payload.SignUpRequest;

@Service
@Transactional
public class UserService implements UserDetailsService {
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public Users getUsers(int loginId){
		Optional<Users> user = usersRepository.findById(loginId);
		return user.get();
	}
	
	public List<Users> getUsers(){
		List<Users> user = usersRepository.findAll();
		return user;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = usersRepository.getUserByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username of "+username));
		return UserPrincipal.create(user);
	}
	
	public ApiResponse signup(SignUpRequest signUpRequest) {
		Optional<Users> existinguser = usersRepository.getUserByUsername(signUpRequest.getUsername());
		
		if( !existinguser.isPresent() ) {
			Users user = new Users();
			user.setName(signUpRequest.getName());
			user.setUsername(signUpRequest.getUsername());
			user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
			usersRepository.save(user);
			return new ApiResponse(true, "Account Cresated");
		}else {
			return new ApiResponse(false, "Username Already Exist");
		}
		
	}

}
