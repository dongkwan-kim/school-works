<?php
	#result2.php	

	/** Implement the function 'find_3PCs'
		- Explanation: Return at most 3 PCs whose prices are closet to the inputted price.
		- Input: db connection info($conn), a price($price)
		- Output: an array of the specifications of 3PCs (that is, the maker, model number, ram, hd, and price)
				For example, the form of output value is
				array(
				0 => array(
						"MAKER" => "A",
						"MODEL" => 1001,
						"RAM" => 1024,
						"HD" => 250,
						"PRICE" => 2114
					),
				1 => array(
						"MAKER" => "A",
						"MODEL" => 1002,
						"RAM" => 512,
						"HD" => 250,
						"PRICE" => 995
					)
				);
	*/
	function find_3PCs($conn,$price){
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
