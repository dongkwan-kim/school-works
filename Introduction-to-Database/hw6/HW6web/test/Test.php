<!--Test.php
	Please, don't touch this file
 -->
<?php 
	$page_title = 'CS360 HW6 / '.basename(__FILE__);
	include('../includes/header.html');	
	$validPrint = false;
	include('../Config/db.connect.php');	
	$score = array(
			0 => 0,
			1 => 0,
			2 => 0,
			3 => 0,
			4 => 0,
			5 => 0
		);
	$maxScore = array(
			0 => 25,
			1 => 15,
			2 => 15,
			3 => 15,
			4 => 0,
			5 => 0
		); 
	if (PEAR::isError($conn)) {		
		$score[0] = 0;
	}
	else {
		$score[0] = 10;
		include('../problems/dbConnTest.php');	
		$arrTables =get_user_tables($conn);
		$ans_arrTables = array(
			"TABLE_NAME" => array(
					0 => "PRODUCT",
					1 => "PC",
					2 => "LAPTOP",
					3 => "PRINTER"
				)
			);
		if($arrTables == $ans_arrTables){
			$score[0] = $score[0] + 15;
		}
	}
	echo 'The score of p1 : ',$score[0], '/',$maxScore[0],'<br/>';
	if(!PEAR::isError($conn)){
		include('../problems/result2.php');	
		$arrTables =find_3PCs($conn,500);
		$ans_arrTables = 	array(
				0 => array(
						"MAKER" => "C",
						"MODEL" => 1007,
						"RAM" => 1024,
						"HD" => 200,
						"PRICE" => 510
					),
				1 => array(
						"MAKER" => "A",
						"MODEL" => 1003,
						"RAM" => 512,
						"HD" => 80,
						"PRICE" => 478
					),
				2 => array(
						"MAKER" => "E",
						"MODEL" => 1013,
						"RAM" => 512,
						"HD" => 80,
						"PRICE" => 529
					)
				);
		if($arrTables == $ans_arrTables){
			$score[1]=15;
		}
	}
	echo 'The score of p2 : ', $score[1], '/',$maxScore[1],'<br/>';

	if(!PEAR::isError($conn)){
		$conn2 = oci_connect(DB_USER, DB_PASSWORD, '//dbclick.kaist.ac.kr:1521/orcl');
		include('../problems/result3.php');	
		$query = oci_parse($conn2,'select * from Laptop');
		oci_execute($query);
		$nrows = oci_fetch_all($query,$res,0,-1,OCI_FETCHSTATEMENT_BY_ROW);
		$ans = insert_Laptop($conn,'E',2001,1,1,1,1,1);
		$query = oci_parse($conn2,'select * from Laptop');
		oci_execute($query);
		$newNrows = oci_fetch_all($query,$res,0,-1,OCI_FETCHSTATEMENT_BY_ROW);		
		if($ans && ($newNrows - $nrows>0)){
			$score[2]=$score[2]+7.5;
		}elseif(!$ans && ($newNrows - $nrows==0)){
			$score[2]=$score[2]+7.5;
		}
		$query = oci_parse($conn2,'select * from Laptop');
		oci_execute($query);
		$nrows = oci_fetch_all($query,$res,0,-1,OCI_FETCHSTATEMENT_BY_ROW);
		$query = oci_parse($conn2,'select * from Product');
		oci_execute($query);
		$nrows2 = oci_fetch_all($query,$res,0,-1,OCI_FETCHSTATEMENT_BY_ROW);
		$ans = insert_Laptop($conn,"G",2011,2.00,4096,500,24,3000);
		$query = oci_parse($conn2,'select * from Laptop');
		oci_execute($query);
		$newNrows = oci_fetch_all($query,$res,0,-1,OCI_FETCHSTATEMENT_BY_ROW);		
		$query = oci_parse($conn2,'select * from Product');
		oci_execute($query);
		$newNrows2 = oci_fetch_all($query,$res,0,-1,OCI_FETCHSTATEMENT_BY_ROW);
		if($ans && ($newNrows - $nrows>0)&& ($newNrows2 - $nrows2>0)){
			$score[2]=$score[2]+7.5;
		}elseif(!$ans && ($newNrows - $nrows==0)){
			$score[2]=$score[2]+7.5;
		}
		oci_close($conn2);
	}
	echo 'The score of p3 : ', $score[2], '/',$maxScore[2],'<br/>';

	if(!PEAR::isError($conn)){
		include('../problems/result4.php');	
		$arrTables =find_system($conn,2000,2);
		$ans_arrTables = array(
					"PC" => array(
						"MODEL" => 1007,
						"SPEED" => 2.2,
						"RAM" => 1024,
						"HD" => 200,
						"PRICE" => 510
					),
					"Printer" => array(
						"MODEL" => 3001,
						"color" => 1,
						"type" => 'ink-jet',
						"PRICE" => 99
					)
				);
		if($arrTables == $ans_arrTables){
			$score[3]=15;
		}
	}
	echo 'The score of p4 : ', $score[3], '/',$maxScore[3],'<br/>';

	echo '<p>The total score : ', array_sum($score), '/',array_sum($maxScore), '</p>';
	echo '<p>Make sure that this score is <b>not</b> your HW6 score. This test is for just checking your codes</p>';
	if(!PEAR::isError($conn)){$conn->disconnect();}
	include('../includes/footer.html');
?>