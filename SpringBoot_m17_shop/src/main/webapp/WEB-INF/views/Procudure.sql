CREATE OR REPLACE PROCEDURE getBestNewProduct(
    p_cur1 OUT SYS_REFCURSOR, 
    p_cur2 OUT SYS_REFCURSOR   )
IS
BEGIN
    OPEN p_cur1 FOR  
        SELECT * FROM new_pro_view;
    OPEN p_cur2 FOR  
        SELECT * FROM best_pro_view;
END;






CREATE OR REPLACE PROCEDURE getMember_s(
    p_userid IN member.userid%TYPE, 
    p_curvar OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_curvar FOR SELECT * FROM member WHERE userid=p_userid;
END;







CREATE OR REPLACE PROCEDURE getAddress_s(
    p_dong IN member.address%TYPE, 
    p_curvar OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_curvar FOR SELECT * FROM address WHERE dong LIKE '%'||p_dong||'%';
END;




select * from member





CREATE OR REPLACE PROCEDURE insertMember_s(
    p_userid IN member.userid%TYPE,
    p_pwd  IN member.pwd%TYPE,
    p_name  IN member.name%TYPE,
    p_email  IN member.email%TYPE,
    p_phone  IN member.phone%TYPE,
    p_zip_num IN member.zip_num%TYPE,
    p_address IN member.address%TYPE,
    p_address2 IN member.address2%TYPE )
IS
BEGIN
    insert into member(userid, pwd, name, email, phone, zip_num, address, address2) 
    values( p_userid, p_pwd, p_name, p_email, p_phone, p_zip_num, p_address, p_address2 );
    commit;    
END;







CREATE OR REPLACE PROCEDURE updateMember_s(
    p_userid IN member.userid%TYPE,
    p_pwd  IN member.pwd%TYPE,
    p_name  IN member.name%TYPE,
    p_email  IN member.email%TYPE,
    p_phone  IN member.phone%TYPE,
    p_zip_num IN member.zip_num%TYPE,
    p_address IN member.address%TYPE,
    p_address2 IN member.address2%TYPE )
IS
BEGIN
    update member set pwd=p_pwd, name=p_name, email=p_email, phone=p_phone, 
    zip_num=p_zip_num, address=p_address, address2=p_address2 where userid=p_userid;
    commit;    
END;







CREATE OR REPLACE PROCEDURE getKindList(
    p_kind IN product.kind%TYPE, 
    p_cur OUT SYS_REFCURSOR   )
IS
BEGIN
    OPEN p_cur FOR SELECT * FROM product where kind=p_kind;
END;




CREATE OR REPLACE PROCEDURE getProduct(
    p_pseq IN product.pseq%TYPE, 
    p_cur OUT SYS_REFCURSOR   )
IS
BEGIN
    OPEN p_cur FOR SELECT * FROM product where pseq=p_pseq;
END;




CREATE OR REPLACE PROCEDURE insertCart(
    p_id IN cart.id%TYPE,
    p_pseq  IN cart.pseq%TYPE,
    p_quantity  IN cart.quantity%TYPE )
IS
BEGIN
    insert into cart( cseq, id, pseq, quantity ) 
    values( cart_seq.nextVal, p_id, p_pseq, p_quantity );
    commit;    
END;


select * from cart_view;

CREATE OR REPLACE PROCEDURE listCart(
    p_id IN cart.id%TYPE, 
    p_cur OUT SYS_REFCURSOR   )
IS
BEGIN
    OPEN p_cur FOR SELECT * FROM cart_view where id=p_id;
END;






CREATE OR REPLACE PROCEDURE deleteCart(
    p_cseq  IN cart.cseq%TYPE   )
IS
BEGIN
    delete from cart where cseq = p_cseq;
    commit;    
END;



select * from cart;




CREATE OR REPLACE PROCEDURE insertOrder(
    p_id  IN  ORDERS.ID%TYPE,
    p_oseq  OUT  ORDERS.OSEQ%TYPE  )
IS
    v_oseq ORDERS.OSEQ%TYPE;
    temp_cur SYS_REFCURSOR;
    v_cseq CART.CSEQ%TYPE;
    v_pseq CART.PSEQ%TYPE;
    v_quantity CART.QUANTITY%TYPE;
BEGIN
    -- orders 테이블에 레코드 추가
    INSERT INTO ORDERS(oseq, id) VALUES( orders_seq.nextVal, p_id);
    -- orders 테이블에서 가장 큰 oseq 조회
    SELECT MAX(oseq) INTO v_oseq FROM ORDERS;
    -- cart  테이블에서  id  로 목록 조회
    OPEN temp_cur FOR SELECT cseq, pseq, quantity FROM CART WHERE id=p_id and result='1';
    -- 목록과  oseq  로 order_detail 테이블에 레코드 추가
    LOOP
        FETCH temp_cur INTO v_cseq, v_pseq, v_quantity;    -- 조회한 카트의 목록에서 하나씩 꺼내서 처리
        EXIT WHEN temp_cur%NOTFOUND;    -- 조회한 카트의 목록이 모두 소진할때까지
        INSERT INTO order_detail( odseq, oseq, pseq, quantity )
        VALUES( order_detail_seq.nextVal, v_oseq, v_pseq,  v_quantity );   -- order_detail 테이블에 레코드 추가
        DELETE FROM CART WHERE cseq = v_cseq;
    END LOOP;
    COMMIT;
    -- oseq 값을 OUT 변수에 저장
    p_oseq := v_oseq;
END;







CREATE OR REPLACE PROCEDURE listOrderByOseq(
    p_oseq IN orders.oseq%TYPE, 
    p_cur OUT SYS_REFCURSOR   )
IS
BEGIN
    OPEN p_cur FOR SELECT * FROM order_view where oseq=p_oseq;
END;






CREATE OR REPLACE PROCEDURE listOrderByIdIng (
    p_id IN   ORDERS.id%TYPE,
    p_rc   OUT     SYS_REFCURSOR )
IS
BEGIN
    OPEN p_rc FOR
        SELECT distinct oseq FROM ORDER_VIEW WHERE id=p_id and result='1' order by oseq desc;
END;




CREATE OR REPLACE PROCEDURE listOrderByIdAll (
    p_id IN   ORDERS.id%TYPE,
    p_rc   OUT     SYS_REFCURSOR )
IS
BEGIN
    OPEN p_rc FOR
        SELECT DISTINCT oseq FROM (SELECT oseq, id FROM ORDER_VIEW ORDER BY result, oseq desc) WHERE id=p_id;
END;



CREATE OR REPLACE PROCEDURE insertOrderOne(
    p_id  IN  ORDERS.ID%TYPE,
    p_pseq IN ORDER_DETAIL.PSEQ%TYPE,
    p_quantity IN ORDER_DETAIL.QUANTITY%TYPE,
    p_oseq  OUT  ORDERS.OSEQ%TYPE  )
IS
    v_oseq ORDERS.OSEQ%TYPE;
BEGIN
    INSERT INTO ORDERS(oseq, id) VALUES( orders_seq.nextVal, p_id);
    SELECT MAX(oseq) INTO v_oseq FROM ORDERS;
    INSERT INTO order_detail( odseq, oseq, pseq, quantity )
    VALUES( order_detail_seq.nextVal, v_oseq, p_pseq, p_quantity );
    COMMIT;
    p_oseq := v_oseq;
END;




CREATE OR REPLACE PROCEDURE listQna (
    p_userid IN   Qna.id%TYPE,
    p_rc   OUT     SYS_REFCURSOR )
IS
BEGIN
    OPEN p_rc FOR
        SELECT * FROM qna WHERE id=p_userid;
END;

select * from qna;





CREATE OR REPLACE PROCEDURE getQna (
    p_qseq IN   Qna.qseq%TYPE,
    p_rc   OUT     SYS_REFCURSOR )
IS
BEGIN
    OPEN p_rc FOR
        SELECT * FROM qna WHERE qseq=p_qseq;
END;





CREATE OR REPLACE PROCEDURE insertQna(
    p_userid IN qna.id%TYPE,
    p_subject  IN qna.subject%TYPE,
    p_content  IN qna.content%TYPE )
IS
BEGIN
    insert into qna(qseq, id, subject, content) 
    values( qna_seq.nextVal, p_userid, p_subject, p_content );
    commit;    
END;





CREATE OR REPLACE PROCEDURE getAdmin(
    p_id IN   worker.id%TYPE,
    p_rc   OUT     SYS_REFCURSOR )
IS
BEGIN
    OPEN p_rc FOR
        select * from worker where id=p_id;
END;



CREATE OR REPLACE PROCEDURE getAllCountProduct (  
    p_key IN product.name%TYPE,
    p_cnt  OUT NUMBER  )
IS
    v_cnt NUMBER;
BEGIN
    SELECT count(*) as cnt into v_cnt FROM PRODUCT WHERE name like '%'||p_key||'%';
    p_cnt := v_cnt;
END;

CREATE OR REPLACE PROCEDURE getProductList (
    p_startNum NUMBER,
    p_endNum NUMBER,
    p_key PRODUCT.NAME%TYPE,
    p_rc   OUT     SYS_REFCURSOR )
IS
BEGIN
    OPEN p_rc FOR
        select * from (
        select * from (
        select rownum as rn, p.* from((select * from product where name like '%'||p_key||'%' order by pseq desc) p)
        ) where rn>=p_startNum
        ) where rn<=p_endNum;
END;















