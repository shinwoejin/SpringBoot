CREATE OR REPLACE PROCEDURE getMember(
    p_userid IN member.userid%TYPE, 
    p_curvar OUT SYS_REFCURSOR
)
IS
    result_cur SYS_REFCURSOR;  
BEGIN
    OPEN result_cur FOR SELECT * FROM member WHERE userid=p_userid;
    p_curvar := result_cur;
END;






CREATE OR REPLACE PROCEDURE selectBoard(
    p_startNum IN NUMBER,
    p_endNum IN NUMBER,
    p_curvar OUT SYS_REFCURSOR
)
IS
    temp_cur SYS_REFCURSOR;   
    vs_num NUMBER;    
    vs_rownum NUMBER;   
    vs_cnt NUMBER;     
BEGIN
    OPEN temp_cur FOR   SELECT * FROM (
        SELECT * FROM (
        SELECT  b.num  , rownum as rn   from ((SELECT * FROM board ORDER BY num DESC) b )
        ) WHERE rn >= p_startNum
        ) WHERE rn <= p_endNum;
    LOOP
        FETCH  temp_cur INTO vs_num, vs_rownum;
        EXIT WHEN temp_cur%NOTFOUND;
        select count(*) into vs_cnt from reply where boardnum = vs_num;  
        update board set replycnt = vs_cnt where num=vs_num;   
    END LOOP;
    COMMIT;
    OPEN p_curvar FOR   select * from (
        select * from (
        select b.* , rownum as rn  from((SELECT * FROM BOARD ORDER BY NUM DESC) b)
        ) where rn>=p_startNum
        ) where rn<=p_endNum;
END;


CREATE OR REPLACE PROCEDURE getAllCount(
    p_cnt OUT NUMBER
)
IS
    v_cnt NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_cnt FROM BOARD;
    p_cnt:=v_cnt;
END;








CREATE OR REPLACE PROCEDURE insertMember(
    p_userid IN member.userid%TYPE,
    p_pwd  IN member.pwd%TYPE,
    p_name  IN member.name%TYPE,
    p_email  IN member.email%TYPE,
    p_phone  IN member.phone%TYPE     )
IS
BEGIN
    insert into member(userid, pwd, name, email, phone) values( p_userid, p_pwd, p_name, p_email, p_phone );
    commit;    
END;





CREATE OR REPLACE PROCEDURE updateMember(
    p_userid IN member.userid%TYPE,
    p_pwd  IN member.pwd%TYPE,
    p_name  IN member.name%TYPE,
    p_email  IN member.email%TYPE,
    p_phone  IN member.phone%TYPE     )
IS
BEGIN
    update member set pwd=p_pwd, name=p_name, email=p_email, phone=p_phone where userid=p_userid;
    commit;    
END;




CREATE OR REPLACE PROCEDURE plusReadCount(
    p_num IN board.num%TYPE )
IS
BEGIN
    update board set readcount = readcount + 1 where num = p_num;
    commit;
END;

CREATE OR REPLACE PROCEDURE boardView(
    p_num IN board.num%TYPE ,
    p_cur1 OUT SYS_REFCURSOR, 
    p_cur2 OUT SYS_REFCURSOR   )
IS
BEGIN
    OPEN p_cur1 FOR  
        SELECT * FROM BOARD WHERE num=p_num ORDER BY num DESC;
    OPEN p_cur2 FOR  
        SELECT * FROM REPLY where boardnum=p_num ORDER BY replynum DESC;
END;

select * from reply
select * from board






CREATE OR REPLACE PROCEDURE insertReply(
    p_boardnum IN reply.boardnum%TYPE, 
    p_userid IN reply.userid%TYPE,
    p_content IN reply.content%TYPE )
IS
BEGIN
    insert into reply( replynum, boardnum, userid, content ) 
    values( reply_seq.nextVal, p_boardnum, p_userid, p_content );
    commit;
END;








CREATE OR REPLACE PROCEDURE deleteReply(
    p_replynum IN reply.replynum%TYPE )
IS
BEGIN
    delete from reply where replynum=p_replynum;
    commit;
END;










CREATE OR REPLACE PROCEDURE getBoard(
    p_num IN board.num%TYPE ,
    p_cur OUT SYS_REFCURSOR   )
IS
BEGIN
    OPEN p_cur FOR  
        SELECT * FROM BOARD WHERE num=p_num ORDER BY num DESC;
END;




create or replace PROCEDURE  updateBoard(
    p_num IN BOARD.NUM%TYPE,
    p_userid IN BOARD.USERID%TYPE,
    p_pass IN BOARD.PASS%TYPE,
    p_email IN BOARD.EMAIL%TYPE,
    p_title IN BOARD.TITLE%TYPE,
    p_content IN BOARD.CONTENT%TYPE,
    p_imgfilename IN BOARD.IMGFILENAME%TYPE
)
IS
BEGIN
    UPDATE board SET pass = p_pass, userid = p_userid, email = p_email, 
    title = p_title, content = p_content, imgfilename=p_imgfilename where num = p_num;
    commit;
END;





create or replace PROCEDURE  removeBoard(
    p_num IN BOARD.NUM%TYPE
)
IS
BEGIN
    DELETE FROM board where num = p_num;
    commit;
END;




create or replace PROCEDURE  insertBoard(
    p_userid IN BOARD.USERID%TYPE,
    p_pass IN BOARD.PASS%TYPE,
    p_email IN BOARD.EMAIL%TYPE,
    p_title IN BOARD.TITLE%TYPE,
    p_content IN BOARD.CONTENT%TYPE,
    p_imgfilename IN BOARD.IMGFILENAME%TYPE
)
IS
BEGIN
    INSERT INTO board( num, pass, userid, email, title, content, imgfilename)
    VALUES( board_seq.nextVal,  p_pass,  p_userid,  p_email,  p_title, p_content, p_imgfilename);
    commit;
END;






