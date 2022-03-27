package com.ezen.spm17.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ezen.spm17.dto.MemberVO;
import com.ezen.spm17.service.MemberService;

@Controller
public class MemberController {

	@Autowired
	MemberService ms;
	
	@RequestMapping(value="/loginForm")
	public String loginForm() {
		return "member/login";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login( @ModelAttribute("dto") @Valid MemberVO membervo,
			BindingResult result, 
			HttpServletRequest request,
			Model model) {
		System.out.println(membervo.getUserid());
		if( result.getFieldError("userid") != null ) {
			model.addAttribute("message" , "아이디를 입력하세요");
			return "member/login";
		}else if( result.getFieldError("pwd") != null ) {
			model.addAttribute("message" , "패스워드를 입력하세요");
			return "member/login";
		}else {
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("userid", membervo.getUserid() );
			paramMap.put("ref_cursor", null);
			ms.getMember(paramMap);
			ArrayList< HashMap<String,Object> > list 
				= (ArrayList<HashMap<String, Object>>) paramMap.get("ref_cursor");
			if(list.size() == 0) {  // 입력한 아이디 없다면
				model.addAttribute("message" , "아이디가 없어요");
				return "member/login";
			}
			HashMap<String, Object> mvo = list.get(0);
			if(mvo.get("PWD")==null) {
				model.addAttribute("message" , "관리자에게 문의하세요");
				return "member/login";
			}else if( membervo.getPwd().equals( (String)mvo.get("PWD") ) ) {
				HttpSession session = request.getSession();
				session.setAttribute("loginUser", mvo);
				return "redirect:/";
			}else {
				model.addAttribute("message" , "비번이 안맞아요");
				return "member/login";
			}
		}
	}
	
	
	@RequestMapping(value="/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute("loginUser");
		return "redirect:/";
	}
	
	
	
	@RequestMapping(value="/contract")
	public String contract() {
		return "member/contract";
	}
	
	
	@RequestMapping(value="/joinForm", method=RequestMethod.POST)
	public String join_form() {
		return "member/joinForm";
	}
	
	
	@RequestMapping("/idCheckForm")
	public String id_check_form( @RequestParam("userid") String userid,
			Model model, HttpServletRequest request ) {
		 
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put( "ref_cursor", null );
		paramMap.put("userid", userid);
		
		ms.getMember(paramMap);	 
		
		ArrayList< HashMap<String,Object> > list 
			= (ArrayList<HashMap<String, Object>>) paramMap.get("ref_cursor");
		
		if(list.size() == 0) model.addAttribute("result", -1);
		else model.addAttribute("result", 1);
		
		model.addAttribute("userid", userid);
		return "member/idcheck";
	}
	
	
	
	
	@RequestMapping(value="/findZipNum")
	public String find_zip( HttpServletRequest request , Model model) {
		String dong=request.getParameter("dong");
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		if(dong != null && dong.trim().equals("")==false){			
			paramMap.put( "ref_cursor", null );
			paramMap.put("dong", dong);
			
			ms.selectAddressByDong(paramMap);
			
			ArrayList< HashMap<String,Object> > list 
				= (ArrayList<HashMap<String, Object>>) paramMap.get("ref_cursor");
			
			System.out.println(list.size() + dong);
			
			model.addAttribute("addressList" , list);
		}
		return "member/findZipNum";
	}
	
	
	
	
	@RequestMapping(value = "/join", method=RequestMethod.POST)
	public String join( @ModelAttribute("dto") @Valid MemberVO membervo,
			BindingResult result,
			@RequestParam(value="reid", required=false) String reid,
			@RequestParam(value="pwdCheck", required=false) String pwdCheck,
			HttpServletRequest request,
			Model model ) {
		
		// 밸리데이션 적용후 해쉬맵에 내용을 담아서 회원가입을 실행합니다.
		model.addAttribute("reid", reid);
				
		if( result.getFieldError("userid") != null ) {
			model.addAttribute("message", result.getFieldError("userid").getDefaultMessage() );
			return "member/joinForm";
		} else if( result.getFieldError("pwd") != null ) {
			model.addAttribute("message", result.getFieldError("pwd").getDefaultMessage() );
			return "member/joinForm";
		} else if( result.getFieldError("name") != null ) {
			model.addAttribute("message", result.getFieldError("name").getDefaultMessage() );
			return "member/joinForm";
		} else if( result.getFieldError("email") != null ) {
			model.addAttribute("message", result.getFieldError("email").getDefaultMessage() );
			return "member/joinForm";
		} else if( result.getFieldError("phone") != null ) {
			model.addAttribute("message", result.getFieldError("phone").getDefaultMessage() );
			return "member/joinForm";
		} else if( reid == null || (   reid != null && !reid.equals(membervo.getUserid() )  )  ) {
			model.addAttribute("message", "아이디 중복체크를 하지 않으셨습니다");
			return "member/joinForm";
		} else if( pwdCheck == null || (  pwdCheck != null && !pwdCheck.equals(membervo.getPwd() ) ) ) {
			model.addAttribute("message", "비밀번호 확인 일치하지 않습니다");
			return "member/joinForm";
		}
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userid", membervo.getUserid() );
		paramMap.put("pwd", membervo.getPwd() );
		paramMap.put("name", membervo.getName() );
		paramMap.put("email", membervo.getEmail() );
		paramMap.put("phone", membervo.getPhone() );
		paramMap.put("zip_num", membervo.getZip_num() );
		paramMap.put("address", membervo.getAddress() );
		paramMap.put("address2", membervo.getAddress2() );
		
		ms.insertMember( paramMap );
		
		model.addAttribute("message", "회원가입이 완료되었어요. 로그인하세요");
		return "member/login";
	}
	
	
	
	
	@RequestMapping(value = "/memberEditForm")
	public String memberEditForm( HttpServletRequest request, Model model ) {
		
		MemberVO dto = new MemberVO();
		HttpSession session = request.getSession();
		HashMap<String, Object> loginUser 
			= (HashMap<String, Object>)session.getAttribute("loginUser");
		dto.setUserid( (String)loginUser.get("USERID") );
		dto.setName( (String)loginUser.get("NAME") );
		dto.setEmail( (String)loginUser.get("EMAIL") );
		dto.setPhone( (String)loginUser.get("PHONE") );
		dto.setZip_num( (String)loginUser.get("ZIP_NUM") );
		dto.setAddress( (String)loginUser.get("ADDRESS") );
		dto.setAddress2( (String)loginUser.get("ADDRESS2") );
		
		model.addAttribute("dto" , dto);
		
		return "member/memberUpdateForm";
	}
	
	
	
	
	@RequestMapping(value = "/memberUpdate", method=RequestMethod.POST)
	public String memberUpdate( @ModelAttribute("dto") @Valid MemberVO membervo,
			BindingResult result,
			@RequestParam(value="pwdCheck", required=false) String pwdCheck,
			HttpServletRequest request,
			Model model ) {
		
		if( result.getFieldError("pwd") != null ) {
			model.addAttribute("message", result.getFieldError("pwd").getDefaultMessage() );
			return "member/memberUpdateForm";
		} else if( result.getFieldError("name") != null ) {
			model.addAttribute("message", result.getFieldError("name").getDefaultMessage() );
			return "member/memberUpdateForm";
		} else if( result.getFieldError("email") != null ) {
			model.addAttribute("message", result.getFieldError("email").getDefaultMessage() );
			return "member/memberUpdateForm";
		} else if( result.getFieldError("phone") != null ) {
			model.addAttribute("message", result.getFieldError("phone").getDefaultMessage() );
			return "member/memberUpdateForm";
		} else if( pwdCheck == null || (  pwdCheck != null && !pwdCheck.equals(membervo.getPwd() ) ) ) {
			model.addAttribute("message", "비밀번호 확인 일치하지 않습니다");
			return "member/memberUpdateForm";
		}
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("USERID", membervo.getUserid() );
		paramMap.put("PWD", membervo.getPwd() );
		paramMap.put("NAME", membervo.getName() );
		paramMap.put("EMAIL", membervo.getEmail() );
		paramMap.put("PHONE", membervo.getPhone() );
		paramMap.put("ZIP_NUM", membervo.getZip_num() );
		paramMap.put("ADDRESS", membervo.getAddress() );
		paramMap.put("ADDRESS2", membervo.getAddress2() );
		
		ms.updateMember( paramMap );
		
		HttpSession session = request.getSession();
		session.setAttribute("loginUser", paramMap);

		return "redirect:/";
	}
	
}














