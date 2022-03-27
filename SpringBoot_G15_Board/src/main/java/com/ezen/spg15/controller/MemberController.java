package com.ezen.spg15.controller;

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
import org.springframework.web.servlet.ModelAndView;

import com.ezen.spg15.dto.MemberVO;
import com.ezen.spg15.service.MemberService;

@Controller
public class MemberController {

	@Autowired
	MemberService ms;
	
	
	@RequestMapping("/")
	public String index() {
		return "member/loginForm";
	}
	
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login( @ModelAttribute("dto") @Valid MemberVO membervo , BindingResult result, 
			HttpServletRequest request, Model model ) {
		
		if( result.getFieldError("userid") != null ) {
			model.addAttribute("message", result.getFieldError("userid").getDefaultMessage() );
			return "member/loginForm";
		}else if( result.getFieldError("pwd")!=null) {
			model.addAttribute("message", result.getFieldError("pwd").getDefaultMessage() );
			return "member/loginForm";
		}
		
		MemberVO mvo = ms.getMember( membervo.getUserid() );

		if( mvo == null ) {
			model.addAttribute("message", "아이디가 없습니다");
			return "member/loginForm";
		}else if( mvo.getPwd() == null ) {
			model.addAttribute("message", "비밀번호 오류. 관리자에게 문의하세요");
			return "member/loginForm";
		}else if( !mvo.getPwd().equals( membervo.getPwd() ) ) {
			model.addAttribute("message", "비밀번호가 맞지않습니다");
			return "member/loginForm";
		}else if( mvo.getPwd().equals( membervo.getPwd() ) ) {
			HttpSession session = request.getSession();
			session.setAttribute("loginUser", mvo );
			return "redirect:/main";
		}else {
			model.addAttribute("message", "무슨이유인지 모르겠지만 로그인 안돼요");
			return "member/loginForm";
		}
	}
	
	
	@RequestMapping("/memberJoinForm")
	public String join_form( ) {
		return "member/memberJoinForm";
	}
	
	
	@RequestMapping("/idcheck")
	public ModelAndView idcheck( @RequestParam("userid") String userid ) {
		
		ModelAndView mav = new ModelAndView();
		
		MemberVO mvo = ms.getMember(userid);
		if( mvo == null ) mav.addObject("result" , -1);
		else mav.addObject("result", 1);
		
		mav.addObject("userid", userid);
		mav.setViewName("member/idcheck");
		
		return mav;
	}
	
	
	
	
	@RequestMapping(value="/memberJoin", method=RequestMethod.POST)
	public ModelAndView memberJoin( 
			@ModelAttribute("dto") @Valid MemberVO membervo,
			BindingResult result, 
			@RequestParam("re_id") String reid, 
			@RequestParam("pwd_check") String pwchk, 
			Model model ) {
		
		ModelAndView mav = new ModelAndView();
		// 밸리데이션으로 전송된 값들을 점검하고, 널이나 빈칸이 있으면  memberJoinForm.jsp로 되돌아 가세요
		mav.setViewName("member/memberJoinForm");  // 되돌아갈페이지의 기본은 회원가입 페이지
		if( reid != null && reid.equals("") ) mav.addObject("re_id", reid);
		
		if( result.getFieldError("userid")!=null) 
			mav.addObject("message", "아이디 입력하세요");
		else if( result.getFieldError("pw") != null ) 
			mav.addObject("message", "비밀번호 입력하세요");
		else if( result.getFieldError("name") != null ) 
			mav.addObject("message", result.getFieldError("name").getDefaultMessage() );
		else if( !membervo.getUserid().equals(reid)) 
			mav.addObject("message","아이디 중복체크가 되지 않았습니다");
		else if( !membervo.getPwd().equals(pwchk)) 
			mav.addObject("message","비밀번호 확인이 일치하시 않습니다.");
		else {  
			ms.insertMember( membervo );
			mav.addObject("message", "회원가입이 완료되었습니다. 로그인 하세요");
			mav.setViewName("member/loginForm"); // 정상 회원가입이 이루어졌을때 로그인폼으로 이동 목적지를 바꿉니다
		}
		// MemberVO 로 자동되지 않는 전달인수 -  pwd_check , re_id  들은 별도의 변수로 전달받고, 별도로 이상유무를
		// 체크하고 이상이 있을시 memberJoinForm.jsp로 되돌아 가세요
		// 모두 이상이 없다고 점검이 되면 회월 가입하고, 회원가입 완료라는 메세지와 함께 loginForm.jsp 로 되돌아 가세요
		return mav;
	}
	
	
	

	@RequestMapping("/memberEditForm")
	public ModelAndView mem_edit_form(Model model, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		MemberVO dto = (MemberVO)session.getAttribute("loginUser");
		mav.addObject("dto", dto);
		mav.setViewName("member/memberEditForm");
		return mav;
	}
	
	
	
	@RequestMapping(value="/memberEdit" , method=RequestMethod.POST)
	public String memberEdit( 
			@ModelAttribute("dto") @Valid MemberVO membervo ,
			BindingResult result, 
			@RequestParam("pwd_check") String pwchk,  
			Model model, 
			HttpServletRequest request ) {
		
		String url = "member/memberEditForm";
		
		if( result.getFieldError("pwd") != null )
			model.addAttribute("message" , "비밀번호 입력하세요");
		else if( result.getFieldError("name") != null )
			model.addAttribute("message" , "이름 입력하세요");
		else if( !membervo.getPwd().equals(pwchk)) 
			model.addAttribute("message","비밀번호 확인이 일치하시 않습니다.");
		else { 
			ms.updateMember(membervo);
			HttpSession session = request.getSession();
			session.setAttribute("loginUser", membervo);
			url = "redirect:/main";
		} 
		return url;
	}
	
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
		return "redirect:/";
	}
	
}


















