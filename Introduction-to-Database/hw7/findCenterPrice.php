<?php
	function cal_center_price($conn){
		$price=0;
		//implement
		// DO NOT USE A PL/SQL STORED FUNCTION
		$query = "SELECT price FROM PC";
		
		$abs_sum;
		$min_sum = -1;
		$res_1 = $conn->query($query);
		while($i_price = $res_1->fetchRow()[0]){
			$abs_sum = 0;
			$res_2 = $conn->query($query);
			while($j_price = $res_2->fetchRow()[0]){
				$abs_sum += abs($j_price - $i_price);
			}
			if($min_sum == -1 or $min_sum > $abs_sum){
				$min_sum = $abs_sum;
				$price = $i_price;
			}
		}

		return $price;
	}
?>
<?php
	$page_title = 'CS360 / '.basename(__FILE__);
	include('../includes/header.html');	
	include('../Config/db.connect.php');
	if (!PEAR::isError($conn)){
		echo '<p>The result of finding center price in PC table</p>';
		$conn->autoCommit(true);		

		//Calculate by using CLI	
		$time_start = microtime(true);			
		$price1= cal_center_price($conn);
		$execution_time = microtime(true) - $time_start;
		echo '<p>Execution time of the direct calculation: ',$execution_time,' seconds </p>';

		//Calculate by using PL/SQL	
		$time_start = microtime(true);
		$res = $conn->query("select FindCenterPrice from dual");
		$price2= $res->fetchRow()[0];
		$execution_time = microtime(true) - $time_start;
		echo '<p>Execution time of the stored function: ',$execution_time,' seconds </p>';
		$price = $price1 == $price2 ? $price1 : -1;

		if(DB::isError($res)){
			print_error($res);
		}
		echo '<p>The center price : ',$price,'</p>';

		$conn->disconnect();
	}
	include('../includes/footer.html');
?>
