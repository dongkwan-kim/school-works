<?php 
	$page_title = 'CS360 / '.basename(__FILE__);
	include('../includes/header.html');
?>
<p>Insert new PC information by using the stored procedure 'insert_pc'</p>
<form action="resOfInsertPC.php" method="get">
	<!--Implement an input form -->
	<ul>
		<li>Manufacturer : </li>
		<li><input type="text" name="maker" size="20" maxlength="10" /></li>
	</ul>
	<ul>
		<li>Model : </li>
		<li><input type="text" name="model" size="20" maxlength="10" /></li>
	</ul>	
	<ul>
		<li>Speed:  </li>
		<li><input type="text" name="speed" size="20" maxlength="10" /></li>
	</ul>
	<ul>
		<li>RAM :   </li>
		<li><select name = "ram">
		<option value=1024>1GB</option>
		<option value=2048>2GB</option>
		<option value=4096>4GB</option>
		<option value=8192>8GB</option>
	</select></li>
	</ul>
	<ul>
		<li>Hard disk size : </li>
		<li><input type="text" name="hd" size="20" maxlength="10" /></li>
	</ul>
	<ul>
		<li>Price :  </li>
		<li><input type="text" name="price" size="20" maxlength="10" /></li>
	</ul>
	<ul>
		<li>　</li>
		<li><input type="submit" value="Insert a new PC"/></li>
	</ul>
</form>
<?php 
	include('../includes/footer.html');
?>