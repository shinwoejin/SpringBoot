<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<table width="500" cellpadding="0" cellspacing="0" border="1">
	<form action="update" method="post">
		<tr><td>작성자</td>
			<td><input type="text" name="writer" size="100" value="${dto.writer}">
			<input type="hidden" name="id" value="${dto.id}"></td>
		</tr>
		<tr><td>제목</td><td>
			<input type="text" name="title" size="100" 
	</form>
</table>
</body>
</html>