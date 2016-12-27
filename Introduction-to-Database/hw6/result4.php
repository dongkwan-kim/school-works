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
		

		$query_system = "SELECT COM.model, Printer.model, COM.speed, COM.price, Printer.price, Printer.color".
				" FROM (SELECT model, price, speed FROM PC UNION SELECT model, price, speed FROM Laptop) COM, Printer".
				" WHERE COM.speed >= $speed AND COM.price+Printer.price <= $budget".
				" ORDER BY COM.price+Printer.price asc, Printer.color desc";
		$result_system = $conn->query($query_system);
		$row_system = $result_system->fetchRow();
		
		$model_com = $row_system[0];
		$model_printer = $row_system[1];

		$query_comtype = "SELECT type FROM product WHERE model=$model_com";
		$result_comtype = $conn->query($query_comtype);
		$row_comtype = $result_comtype->fetchRow();
		$comtype = $row_comtype[0];

		$query_comspec = "SELECT * FROM $comtype WHERE model=$model_com";
		$result_comspec = $conn->query($query_comspec);
		$row_comspec = $result_comspec->fetchRow();
		$arr_com = get_spec_arr($row_comspec, $comtype);

		$query_printerspec = "SELECT * FROM Printer WHERE model=$model_printer";
		$result_printerspec = $conn->query($query_printerspec);
		$row_printerspec = $result_printerspec->fetchRow();
		$arr_printer = get_spec_arr($row_printerspec, "printer");

		$queryResult = array(
			ucase($comtype) => $arr_com,
			"Printer" => $arr_printer
		);

		return $queryResult;
	}

	function get_total($system){
		$total = 0;
		foreach($system as $value){
			$total += $value["PRICE"];
		}
		return $total;
	}

	function get_spec_arr($row_spec, $type){
		if($type == "laptop"){
			$row_arr = array(
				"MODEL" => $row_spec[0],
				"SPEED" => $row_spec[1],
				"RAM" => $row_spec[2],
				"HD" => $row_spec[3],
				"SCREEN" => $row_spec[4],
				"PRICE" => $row_spec[5]
			);
		} else if ($type == "pc") {
			$row_arr = array(
				"MODEL" => $row_spec[0],
				"SPEED" => $row_spec[1],
				"RAM" => $row_spec[2],
				"HD" => $row_spec[3],
				"PRICE" => $row_spec[4]
			);
		} else if ($type == "printer") {
			$row_arr = array(
				"MODEL" => $row_spec[0],
				"color" => $row_spec[1],
				"type" => $row_spec[2],
				"PRICE" => $row_spec[3]
			);
		}
		return $row_arr;
	}

	function ucase($com){
		if($com == "pc"){
			return "PC";
		} else if ($com == "laptop"){
			return "Laptop";
		}
	}

	function print_p_v2($arr, $type){
		echo $type . "> ";
		foreach($arr as $key=>$value){
			echo $key . ": " . $value . " ";
		}
		echo "<br>";
	}

?>
<?php
	if(!isset($validPrint)){
		$page_title = 'CS360 HW6 / '.basename(__FILE__);
		include('../includes/header.html');	
		include('../Config/db.connect.php');
		if (!PEAR::isError($conn)){

			$budget = $_GET["budget"];
			$speed = $_GET["speed"];

			$system = find_system($conn,$budget,$speed);

			echo "Budget: $budget Speed: $speed <br><br>";
			foreach($system as $key=>$value){
				print_p_v2($value, $key);
			}
			echo "<br>";
			echo "Total price: " . get_total($system);
			
			$conn->disconnect();
		}
		include('../includes/footer.html');
	}
?>
