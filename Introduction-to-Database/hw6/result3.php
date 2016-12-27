<?php
	#result3.php

	/** Implement the function 'insert_Laptop'
		- Explanation: insert laptop info into tables Product and PC if there is no Laptop with that model number.
		- Input: db connection info($conn), laptop info($maker,$model,$speed,$ram,$hd,$screen,$price)
		- Output: true if the insertion is success, false otherwise
	*/
	function insert_Laptop($conn,$maker,$model,$speed,$ram,$hd,$screen,$price){
		$queryResult = true;

		$query_ask_model = "SELECT * FROM laptop WHERE model = $model";
		$result_ask_model = $conn->query($query_ask_model);
		if ($row = $result_ask_model->fetchRow()) {
			$queryResult = false;
		} else {

			$query_insert_product = "INSERT INTO PRODUCT (maker, model, type)".
									" VALUES('$maker', $model, 'laptop')";
			$query_insert_laptop = "INSERT INTO LAPTOP (model, speed, ram, hd, screen, price)".
									" VALUES($model, $speed, $ram, $hd, $screen, $price)";
			$conn->query($query_insert_product);
			$conn->query($query_insert_laptop);
		}

		return $queryResult;
	}
?>
<?php
	if(!isset($validPrint)){
		$page_title = 'CS360 HW6 / '.basename(__FILE__);
		include('../includes/header.html');	
		include('../Config/db.connect.php');
		if (!PEAR::isError($conn)){

			$maker = $_GET["maker"];
			$model = $_GET["model"];
			$speed = $_GET["speed"];
			$ram = $_GET["ram"];
			$hd = $_GET["hd"];
			$screen = $_GET["screen"];
			$price = $_GET["price"];

			$insert_success = insert_Laptop($conn,$maker,$model,$speed,$ram,$hd,$screen,$price);
			if($insert_success){
				echo "The laptop with ($maker,$model,$speed,$ram,$hd,$screen,$price) is inserted";
			} else{
				echo "The laptop with ($maker,$model,$speed,$ram,$hd,$screen,$price) cannot be inserted <br>";
				echo "The model $model already exists";
			}


			$conn->disconnect();
		}
		include('../includes/footer.html');
	}
?>
