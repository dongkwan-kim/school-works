--- create a procedure inserts_pc
--- You must use / after the creation
CREATE OR REPLACE PROCEDURE insert_pc(
	v_maker IN VARCHAR2,
	v_model IN NUMBER,
	v_speed IN NUMBER,
	v_ram IN NUMBER,
	v_hd IN NUMBER,
	v_price IN NUMBER
)

/

--- COMMIT DML COMMANDS
COMMIT;