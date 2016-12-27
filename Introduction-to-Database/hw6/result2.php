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

		
		$query = "SELECT maker, model, ram, hd, price FROM PC".
				" NATURAL JOIN (SELECT maker, model FROM product)".
				" ORDER BY abs(price - $price)";

		$cnt = 0;
		$result = $conn->query($query);
		while ($row = $result->fetchRow()) {
			$row_arr = array(
				"MAKER" => $row[0],
				"MODEL" => $row[1],
				"RAM" => $row[2],
				"HD" => $row[3],
				"PRICE" => $row[4]
			);
    		array_push($queryResult, $row_arr);
    		$cnt++;
    		if($cnt >= 3){
    			break;
    		}
		}
		
		return $queryResult;
	}

	function print_p($arr, $word){
		$cname = array_keys($arr[0]);
		echo " | ";
		for($i = 0; $i < count($cname); $i++){
			echo make_str_long_again($cname[$i], $word);
			echo " | ";
		}
		echo "<br>";
		for($i = 0; $i < count($arr); $i++){
			foreach($cname as $key){
				echo  make_str_long_again($arr[$i][$key], $word);
				echo " | ";
			}
			echo "<br>";
		}
	}

	function make_str_long_again($str, $want_len){
		$strlen = strlen($str);
		if($want_len > $strlen){
			for($i = 0; $i < $want_len - $strlen; $i++){
				$str = $str . "&nbsp;";
			}
		}
		return $str;
	}

?>
<?php
	if(!isset($validPrint)){
		$page_title = 'CS360 HW6 / '.basename(__FILE__);
		include('../includes/header.html');	
		include('../Config/db.connect.php');
		if (!PEAR::isError($conn)){
			
			$price = $_GET['price'];
			$result = find_3PCs($conn, $price);
			print_p($result, 5);

			$conn->disconnect();
		}
		include('../includes/footer.html');
	}
?>
