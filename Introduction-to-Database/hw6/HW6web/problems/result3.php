<?php
	#result3.php

	/** Implement the function 'insert_Laptop'
		- Explanation: insert laptop info into tables Product and PC if there is no Laptop with that model number.
		- Input: db connection info($conn), laptop info($maker,$model,$speed,$ram,$hd,$screen,$price)
		- Output: true if the insertion is success, false otherwise
	*/
	function insert_Laptop($conn,$maker,$model,$speed,$ram,$hd,$screen,$price){
		$queryResult = true;

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
