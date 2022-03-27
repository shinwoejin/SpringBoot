package com.ezen.spg15.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletContext;
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

import com.ezen.spg15.dto.BoardVO;
import com.ezen.spg15.dto.Paging;
import com.ezen.spg15.dto.ReplyVO;
import com.ezen.spg15.service.BoardService;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

@Controller
public class BoardController {

	@Autowired
	BoardService bs;
	
	@Autowired
	ServletContext context;
	
	@RequestMapping("/main")
	public ModelAndView goMain(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		if( session.getAttribute("loginUser") == null)	
			mav.setViewName("loginform");
		else {
			
			int page = 1;
			
			if( request.getParameter("page") != null ) {
				page = Integer.parseInt( request.getParameter("page") );
				session.setAttribute("page", page);
			} else if( session.getAttribute("page") != null ) {
				page = (int) session.getAttribute("page");
			} else {
				session.removeAttribute("page");
			}
			
			Paging paging = new Paging();
			paging.setPage(page);
			int count = bs.getAllCount();
			paging.setTotalCount( count );
			paging.paging();
			
			mav.addObject( "boardList" , bs.selectBoardAll( paging ) );
			mav.addObject( "paging" , paging );
			mav.setViewName("board/main");
		}
		return mav;
	}
	
	
	@RequestMapping("/boardWriteForm")
	public String write_form(HttpServletRequest request) {
		
		String url = "board/boardWriteForm";
		
		HttpSession session = request.getSession();
		if( session.getAttribute("loginUser") == null)	url="member/loginform";		
		
		return url;
	}
	
	
	@RequestMapping("/selectimg")
	public String selectimg() {
		return "board/selectimg";
	}
	
	
	@RequestMapping(value="/fileupload" , method = RequestMethod.POST)
	public String fileupload(Model model, HttpServletRequest request) {
		String path = context.getRealPath("/upload");		
		try {
			MultipartRequest multi = new MultipartRequest(
					request, path, 5*1024*1024, "UTF-8", new DefaultFileRenamePolicy()
			);
			// 전송된 파일은 업로드 되고, 파일 이름은  모델에 저장합니다
			model.addAttribute("image", multi.getFilesystemName("image") );
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "board/completupload";
	}
	
	
	
	@RequestMapping(value="boardWrite", method = RequestMethod.POST)
	public String board_write( 
			@ModelAttribute("dto") @Valid BoardVO boardvo, 
			BindingResult result, 
			Model model,	
			HttpServletRequest request) {
		
		String url = "board/boardWriteForm";
		if( result.getFieldError("pass") != null ) 
			model.addAttribute("message", result.getFieldError("pass").getDefaultMessage() );
		else if( result.getFieldError("title")!=null)
			model.addAttribute("message", result.getFieldError("title").getDefaultMessage() );
		else if( result.getFieldError("content")!=null)
			model.addAttribute("message", result.getFieldError("content").getDefaultMessage() );
		else {
			
			bs.insertBoard(boardvo);
			url = "redirect:/main";
		}
		return url;
	}
	
	
	/*
	@RequestMapping(value="boardWrite", method = RequestMethod.POST)
	public String board_write( BoardVO boardvo, Model model,	HttpServletRequest request) {
				
		if( result.hasErrors() ) {
			System.out.println("pass : " + result.getFieldError("pass").getDefaultMessage() );
			System.out.println("title : " + result.getFieldError("title").getDefaultMessage() );
			System.out.println("content : " + result.getFieldError("content").getDefaultMessage() );
		}
		
		String path = context.getRealPath("/upload");
		try {
			MultipartRequest multi = new MultipartRequest(
					request, path, 5*1024*1024, "UTF-8", new DefaultFileRenamePolicy()
			);
			BoardVO dto = new BoardVO();
			dto.setPass( multi.getParameter("pass") );
			dto.setUserid( multi.getParameter("userid") );
			dto.setEmail( multi.getParameter("email") );
			dto.setTitle( multi.getParameter("title") );
			dto.setContent( multi.getParameter("content") );
			
			model.addAttribute("dto" , dto);
			
			// MultipartRequest 로 전달인수를 모두 전달받은 후에, 밸리데이션에 적용합니다.
			if( dto.getPass() == null || dto.getPass().equals("") ) {
				model.addAttribute("message","비밀번호는 수정삭제시 필요합니다");
				return "board/boardWriteForm.jsp";
			} else if( dto.getTitle() == null || dto.getTitle().equals("") ) {
				model.addAttribute("message","제목을 입력하세요");
				return "board/boardWriteForm.jsp";
			} else if( dto.getContent() == null || dto.getContent().equals("")) {
				model.addAttribute("message","내용을 입력하세요");
				return "board/boardWriteForm.jsp";
			}
			
			if( multi.getFilesystemName("image") == null ) dto.setImgfilename("");
			else dto.setImgfilename( multi.getFilesystemName("image") );
			
			bs.insertBoard( dto );
		} catch (IOException e) {  e.printStackTrace();
		}
		
		return "redirect:/main";
	}
	*/

	
	@RequestMapping("/boardView")
	public ModelAndView boardView( @RequestParam("num") int num,  
			HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView();
		
		HashMap<String, Object> resultMap = bs.boardView(num);
		// bs.boardView(num); 에서  조회수 늘리고, 게시물 조회하고, 댓글리스트 조회
		BoardVO mvo = (BoardVO)resultMap.get("board");
		mav.addObject("board" ,  mvo );
		mav.addObject("replyList", resultMap.get("replyList") );
		
		mav.setViewName("board/boardView");		
		
		return mav;
	}
	
	
	
	
	
	@RequestMapping("/addReply")
	public String addReply(
			/* ReplyVO replyvo , */  @RequestParam("boardnum") int boardnum, 
			@RequestParam("userid") String userid, 
			@RequestParam("content") String content, HttpServletRequest request) {
		
		// 댓글 추가
		ReplyVO rvo = new ReplyVO();
		rvo.setUserid( userid );
		rvo.setContent(content);
		rvo.setBoardnum(boardnum);
		
		bs.insertReply( rvo );
		
		return "redirect:/boardViewWithoutCount?num=" + boardnum;
 	}
	
	// boardViewWithoutCount  리퀘스트의 메서드 추가
	@RequestMapping("/boardViewWithoutCount")
	public ModelAndView boardViewWithoutCount( @RequestParam("num") int num,  
			HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView();
		
		HashMap<String, Object> resultMap = bs.boardViewWithoutCount(num);
		BoardVO mvo = (BoardVO)resultMap.get("board");
		mav.addObject("board" ,  mvo );
		mav.addObject("replyList", resultMap.get("replyList") );
		
		mav.setViewName("board/boardView");		
		
		return mav;
	}
	
	
	
	
	@RequestMapping("/deleteReply")
	public String reply_delete( @RequestParam("num") int num, 
			@RequestParam("boardnum") int boardnum,
			HttpServletRequest request) {
		bs.deleteReply(num);
		return "redirect:/boardViewWithoutCount?num=" + boardnum;
	}
	
	
	
	@RequestMapping("/boardEditForm")
	public String board_edit_form(Model model, HttpServletRequest request) {
		String num = request.getParameter("num");
		model.addAttribute("num", num);
		return "board/boardCheckPassForm";
	}
	
	@RequestMapping("/boardEdit")
	public String board_edit(@RequestParam("num") int num,
			@RequestParam("pass") String pass, 
			Model model, HttpServletRequest request) {

		BoardVO bvo = bs.getBoard(num);
		model.addAttribute("num", num);
		
		if(pass.equals(bvo.getPass()) ) {
			return "board/boardCheckPass";
		}	else {
			model.addAttribute("message", "비밀번호가 맞지 않습니다. 확인해주세요");
			return "board/boardCheckPassForm";
		}
	}
	
	
	
	
	@RequestMapping("boardUpdateForm")
	public String board_update_form(@RequestParam("num") int num,
			Model model, HttpServletRequest request) {
		BoardVO bvo = bs.getBoard(num);
		model.addAttribute("num", num);
		model.addAttribute("dto", bvo);
		return "board/boardEditForm";
	}
	
	
	
	
	@RequestMapping(value="/boardUpdate", method = RequestMethod.POST)
	public String boardUpdate( 
			@ModelAttribute("dto") @Valid BoardVO boardvo,
			BindingResult result, 
			@RequestParam("oldfilename") String oldfilename, 
			HttpServletRequest request, Model model) {
		
		String url = "board/boardEditForm";
		
		if( result.getFieldError("pass")!=null) 
			model.addAttribute("message" , "비밀번호는 게시물 수정 삭제시 필요합니다");
		else  if(result.getFieldError("title")!=null) 
			model.addAttribute("message" , "제목은 필수입력 사항입니다");
		else if(result.getFieldError("content")!=null) 
			model.addAttribute("message" , "게시물 내용은 비워둘수 없습니다.");
		else {
			if( boardvo.getImgfilename()==null || boardvo.getImgfilename().equals("") )	
				boardvo.setImgfilename(oldfilename);
			bs.updateBoard( boardvo );
			url = "redirect:/boardViewWithoutCount?num=" + boardvo.getNum();
		}	
		
		return url;
	}
	
	
	
	@RequestMapping("boardDeleteForm")
	public String board_delete_form(@RequestParam("num") int num,
			Model model, HttpServletRequest request) {
		model.addAttribute("num", num);
		return "board/boardCheckPassForm";
	}
	
	
	@RequestMapping("boardDelete")
	public String board_delete(Model model, HttpServletRequest request) {
		int num = Integer.parseInt(request.getParameter("num"));

		//bdao.deleteBoard(num);
		//bdao.deleteReply(num);
		bs.removeBoard(num);
		
		return "redirect:/main";
	}
	
	
}













