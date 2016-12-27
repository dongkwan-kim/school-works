--- create a function 'FindCenterPrice'
--- You must use / after the creation
--- problem 4 done
CREATE OR REPLACE FUNCTION findCenterPrice
RETURN NUMBER
IS
	abs_sum NUMBER;
	min_sum NUMBER := -1;
	center_val NUMBER;
BEGIN
	FOR i_cursor IN (SELECT price FROM PC)
	LOOP
		abs_sum := 0;
		FOR j_cursor IN (SELECT price FROM PC)
		LOOP
			abs_sum := abs_sum + ABS(j_cursor.price - i_cursor.price);
		END LOOP;
		IF (min_sum = -1 OR min_sum > abs_sum) THEN
			min_sum := abs_sum;
			center_val := i_cursor.price;
		END IF;
	END LOOP;
	RETURN center_val;
END;


/


--- COMMIT DML COMMANDS
COMMIT;