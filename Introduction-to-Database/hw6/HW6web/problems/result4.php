<?php
	#result4.php

	/** Implement the function 'find_system'
		- Explanation: Return the infomation (all the attributes) of the cheapest system.
		- Input: db connection info($conn), system info($budget,$speed)
		- Output: an array of the system
			For example, the form of output value is
				array(
					"PC" => array(
						"MODEL" => 1007,
						"SPEED" => 2.20,
						"RAM" => 1024,
						"HD" => 200,
						"PRICE" => 510
					),
					"Printer" => array(
						"MODEL" => 3003,
						"color" => 1,
						"type" => 'laser',
						"PRICE" => 899
					)
				);
			or
				array(
					"Laptop" => array(
						"MODEL" => 2003,
						"SPEED" => 1.80,
						"RAM" => 512,
						"HD" => 60,
						"SCREEN" => 15.4,
						"PRICE" => 549
					),
					"Printer" => array(
						"MODEL" => 3003,
						"color" => 1,
						"type" => 'laser',
						"PRICE" => 899
					)
				);			
	*/
	function find_system($conn,$budget,$speed){
		$queryResult = array();

		//implement..

		return $queryResult;
	}
?>
<?php
	if(!isset($validPrint)){
		$page_title = 'CS360 HW6 / '.basename(__FILE__);
		include('../includes/header.html');	
		include('../Config/db.connect.php');
		if (!PEAR::isError($conn)){

			/* Implement an ouput screen*/

			$conn->disconnect();
		}
		include('../includes/footer.html');
	}
?>
