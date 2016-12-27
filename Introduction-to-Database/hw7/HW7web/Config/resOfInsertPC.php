<?php
	$page_title = 'CS360 / '.basename(__FILE__);
	include('../includes/header.html');	
	include('../Config/db.connect.php');
	if (!PEAR::isError($conn)){
		echo '<p>The result of insertion</p>';
		$conn->autoCommit(true);			
		$maker= $_REQUEST['maker'];
		$model= $_REQUEST['model'];
		$speed= $_REQUEST['speed'];
		$ram= $_REQUEST['ram'];
		$hd= $_REQUEST['hd'];
		$price = $_REQUEST['price'];
		$bindvars = array($maker,$model,$speed,$ram,$hd,$price);

		$stmt = $conn->prepare("begin insert_pc(?,?,?,?,?,?); end;");
		$res = $conn->execute($stmt,$bindvars);
		if(DB::isError($res)){
			$err_stmt = 'insertion fail';
			if(strpos($res->getUserInfo(),'20001') !== false){
				echo '<p>',$err_stmt,' - duplication</p>';
			}
			elseif (strpos($res->getUserInfo(),'20000') !== false) {
				echo '<p>',$err_stmt,' - the maker has more than 10 products.</p>';			
			}else{
				print_error($res);
			}
		}else{
			echo '<p>Insertion success</p>';
		}
		$conn->disconnect();
	}
	include('../includes/footer.html');
?>
