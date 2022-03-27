package com.ezen.spg07;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ValidController {

	@RequestMapping("/")
    public String main() {
    	return "startPage";       
    }
	
	@RequestMapping("/create")
	public String insert( @ModelAttribute("dto") ContentDto contentDto, Model model, 
		BindingResult result) {
		
		ContentValidator validator = new ContentValidator();
		validator.validate(contentDto, result);
		
		if (result.hasErrors()) {
			if( result.getFieldError("writer")!=null)
				model.addAttribute("message", "writer 를 써주세요" );
			else 
				model.addAttribute("message", "content 를 써주세요" );
			return "startPage";
		}
		else return "DonePage";
	}
}
