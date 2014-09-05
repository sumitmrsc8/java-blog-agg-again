package cz.jiripinkas.jba.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import cz.jiripinkas.jba.repository.BlogRepository;
import cz.jiripinkas.jba.repository.RoleRepository;
import cz.jiripinkas.jba.repository.UserRepository;
import cz.jiripinkas.jba.repository.ItemRepository;

import java.util.*;

import javax.transaction.Transactional;

import cz.jiripinkas.jba.entity.*;

@Service
@Transactional
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BlogRepository blogRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	public List<User> findAll(){
		
		return userRepository.findAll();
	} 
	
	public User findOne(int id){
			return  userRepository.findOne(id);
		
	}
	
@Transactional
	public User findOneWithBlog(int id) {
		User user=findOne(id);
		List<Blog> blogs=blogRepository.findByUser(user);
		for(Blog blog:blogs){
				List<Item> items= itemRepository.findByBlog(blog, new PageRequest(0, 10,Direction.DESC,"publishedDate"));
				blog.setItems(items);
				
			
		}
		user.setBlogs(blogs);
		return user;
	}

	public void save(User user) {
		user.setEnabled(true);
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
		
		user.setPassword(encoder.encode(user.getPassword()));
//		userRepository.save(user);

		List<Role> roles = new ArrayList<Role>();
		roles.add(roleRepository.findByName("ROLE_USER"));
		user.setRoles(roles);
		userRepository.save(user);
	}
	
	public User findOneWithBlogs(String name){
		
		User user=	userRepository.findByName(name);
		return findOneWithBlog(user.getId());
	}

	public void delete(int id) {
		userRepository.delete(id);;
	}
}
