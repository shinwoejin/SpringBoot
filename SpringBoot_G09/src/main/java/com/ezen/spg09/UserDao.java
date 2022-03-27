package com.ezen.spg09;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

	@Autowired
	private JdbcTemplate template;
	
	// 해당 클래스를 servlet-context.xml 에 bean 으로 넣어 놓아야하는 수고스러움이 있는 옛날방식
	/*UserDao( Datasource .... ){   
		template = new JdbcTemplate(DataSource);
	}*/
	
	public List<UserDto> list() {

		String sql = "select * from myuser";
		
		List<UserDto> list = template.query(
				sql, 
				new BeanPropertyRowMapper<UserDto>(UserDto.class)
		);
		//  ResultSet 사용없이  검색 결과 레코드의 필드를 Dto 변수에 넣고 list 에 add 동작을 실행합니다
		// 결과 레코드 갯수 만큼 실행합니다
		return list;
	}

}
