package com.project.shopping.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.project.shopping.dto.UpdateUserDto;
import com.project.shopping.entities.User;
import com.project.shopping.repos.CommentRepository;
import com.project.shopping.repos.PostRepository;
import com.project.shopping.repos.UserRepository;

@Service
public class UserService {

	UserRepository userRepository;
	CommentRepository commentRepository;
	PostRepository postRepository;

	public UserService(UserRepository userRepository,
			CommentRepository commentRepository, PostRepository postRepository) {
		this.userRepository = userRepository;
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User saveOneUser(User newUser) {
		return userRepository.save(newUser);
	}

	public User getOneUserById(Long userId) {
		return userRepository.findById(userId).orElse(null);
	}

	public User getOneUserByIdWithFields(Long userId, List<String> fields) {
		System.out.println("desiredFields = " + fields);

		return userRepository.findUserByUserIdWithFields(userId, fields);
	}

	public User updateOneUser(Long userId, UpdateUserDto newUser) {
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			User foundUser = user.get();
			BeanUtils.copyProperties(newUser, foundUser); // excluding non-updatable fields
			userRepository.save(foundUser);
			return foundUser;
		} else
			return null;
	}

	public User updateOneUserAsAdmin(Long userId, User newUser) {
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			User foundUser = user.get();
			foundUser.setUserName(newUser.getUserName());
			foundUser.setPassword(newUser.getPassword());
			foundUser.setAvatar(newUser.getAvatar());
			userRepository.save(foundUser);
			return foundUser;
		} else
			return null;
	}

	public void deleteById(Long userId) {
		try {
			userRepository.deleteById(userId);
		} catch (EmptyResultDataAccessException e) { // user zaten yok, db'den empty result gelmiş
			System.out.println("User " + userId + " doesn't exist"); // istersek loglayabiliriz
		}
	}

	public User getOneUserByUserName(String userName) {
		return userRepository.findByUserName(userName);
	}

	public List<Object> getUserActivity(Long userId) {
		List<Long> postIds = postRepository.findTopByUserId(userId);
		if (postIds.isEmpty())
			return null;
		List<Object> comments = commentRepository.findUserCommentsByPostId(postIds);
		List<Object> result = new ArrayList<>();
		result.addAll(comments);
		return result;
	}

}
