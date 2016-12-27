--- create a trigger 'CheckTypePC'
--- You must use / after the creation
--- problem 2 done
CREATE OR REPLACE TRIGGER CheckNumProduct
BEFORE INSERT ON PRODUCT
FOR EACH ROW
DECLARE
	maker_cnt INTEGER;
BEGIN
	SELECT COUNT (*) INTO maker_cnt
	FROM PRODUCT
	WHERE maker = :new.maker;

	IF(maker_cnt > 9) THEN
		RAISE_APPLICATION_ERROR(-20000, 'max total is 10');
	END iF;
END;

/
--- COMMIT DML COMMANDS
COMMIT;