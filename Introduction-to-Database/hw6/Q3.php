<?php 
	$page_title = 'CS360 HW6 / '.basename(__FILE__);
	include('../includes/header.html');
?>
<p>Insert new Laptop information into tables Product and Laptop if there is no Laptop with that model number.</p>
<p><b>No blanks are allowed.</b></p>
<form action="result3.php" method="get">

	<ul>manufacturer: <input type="text" name="maker" required></ul>
	<ul>model number: <input type="number" name="model" required></ul>
	<ul>speed: <input type="number" name="speed" step="0.01" max="3.00" required></ul>
	<ul>ram:
		<select name="ram">
			<option value="1024">1024</option>
			<option value="2048">2048</option>
			<option value="4096">4096</option>
			<option value="8192">8192</option>
		</select>
	</ul>
	<ul>hd: <input type="number" name="hd" required></ul>
	<ul>screen: <input type="number" name="screen" step="0.1" required></ul>
	<ul>price: <input type="number" name="price" required></ul>
	<input type="submit" value="Insert a Laptop">

</form>
<?php 
	include('../includes/footer.html');
?>