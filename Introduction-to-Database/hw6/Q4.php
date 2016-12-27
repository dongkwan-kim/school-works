<?php 
	$page_title = 'CS360 HW6 / '.basename(__FILE__);
	include('../includes/header.html');
?>
<p>Find the cheapest "system" (PC plus printer or Laptop plus printer) that is within the "budget" (total price of a PC (or a Laptop) and printer), and minimum speed.</p>
<p>Make the printer a color printer (color = 1) if possible.</p>
<p><b>No blanks are allowed.</b></p>
<form action="result4.php" method="get">
	<ul>budget: <input type="number" name="budget" required></ul>
	<ul>speed: <input type="number" name="speed" step="0.01" required></ul>
	<input type="submit" value="Find the PC or the Laptop">
</form>
<?php 
	include('../includes/footer.html');
?>