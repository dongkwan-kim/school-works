<!--index.html
	Please, don't touch this file
 -->
<?php 
	include('includes/header.html');
?>
<hr>
<p><a href='Config/insertPC.php'>Insert a new PC</a></p>
<p><a href='problems/findCenterPrice.php'>Find the center price</a></p>
<?php 
	include('Config/db.connect.php');
	if (!PEAR::isError($conn)){
		echo '<hr>';
		include('Config/printTables.php');
		print_table($conn,'product');
		echo '<hr>';
		print_table($conn,'pc');
		$conn->disconnect();
	}
	include('includes/footer.html');
?>