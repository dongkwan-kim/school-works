--- create a procedure inserts_pc
--- You must use / after the creation
--- problem 3 done
CREATE OR REPLACE PROCEDURE insert_pc(
	v_maker IN VARCHAR2,
	v_model IN NUMBER,
	v_speed IN NUMBER,
	v_ram IN NUMBER,
	v_hd IN NUMBER,
	v_price IN NUMBER
)
IS
BEGIN

INSERT INTO PRODUCT (maker, model, type) VALUES (v_maker, v_model, 'pc');
INSERT INTO PC (model, speed, ram, hd, price) VALUES (v_model, v_speed, v_ram, v_hd, v_price);

EXCEPTION
	WHEN DUP_VAL_ON_INDEX THEN
		RAISE_APPLICATION_ERROR(-20001, 'Duplication value error');

END;

/

--- COMMIT DML COMMANDS
COMMIT;