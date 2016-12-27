<?php
	function print_table($conn,$table_name){
		echo '<p> TABLE : ',$table_name,'</p>';
		$conn->setFetchMode(DB_FETCHMODE_ASSOC);
		$res = $conn->query("select * from ".$table_name.' order by model');		
		$i =0;
		$isFirst = true;
		while($row = $res->fetchRow()){
			if($isFirst){
				echo '<ul>';			
				echo '<li style="width: 90px;"> no. </li>';
				foreach($row as $k=>$v){
					echo '<li style="width: 90px;">',$k,'</li>';
				}
				echo '</ul>';
				$isFirst = false;			
			}
			echo '<ul>';			
			echo '<li style="width: 90px;">',++$i,'</li>';
			foreach($row as $k=>$v){
				echo '<li style="width: 90px;">',$v,'</li>';
			}
			echo '</ul>';
		}
	}
?>
