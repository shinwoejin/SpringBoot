package com.ezen.spg11.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ezen.spg11.dao.IUserDao;
import com.ezen.spg11.dto.UserDto;

@Controller
public class UserController {

	@Autowired
	IUserDao udao;
	
	@RequestMapping("/")
    public String root(Model model){
		List<UserDto> list = udao.list();
		// 인터페이스의 추상메서드를 호출하면, 인터페이스가 해당 메서드를 xml파일에서 실행해주는 형식.
		// xml 에는 메서드가 아닌 실행가능한 무언가가 있습니다.
		model.addAttribute("users", list);
		return "userlist";
	}
	
	
}






