<?php
	#db.connect.php

	//reference: http://www.oracle.com/webfolder/technetwork/tutorials/obe/db/oow10/php_db/php_db.htm

	//This file provides the database access information
	//This file also establishes a connection to your Oracle database

	//Set the database access information as constants
	require_once('DB.php');
	DEFINE('DB_USER','s20160000');
	DEFINE('DB_PASSWORD','s20160000');
	DEFINE('DB_ADDR','oci8://'.DB_USER.':'.DB_PASSWORD.'@dbclick.kaist.ac.kr:1521/orcl');

	$conn = DB::connect(DB_ADDR);
	if (PEAR::isError($conn) && !isset($validPrint)) {
	    /*
	     * This is not what you would really want to do in
	     * your program.  It merely demonstrates what kinds
	     * of data you can get back from error objects.
	     */
	    echo 'Standard Message: ' . $conn->getMessage() . "<br/>";
	    echo 'Standard Code: ' . $conn->getCode() . "<br/>";
	    echo 'DBMS/User Message: ' . $conn->getUserInfo() . "<br/>";
	    echo 'DBMS/Debug Message: ' . $conn->getDebugInfo() . "<br/>";
	}

	function print_error($conn){
		if (DB::isError($conn)) {
	    /*
	     * This is not what you would really want to do in
	     * your program.  It merely demonstrates what kinds
	     * of data you can get back from error objects.
	     */
	    echo 'Standard Message: ' . $conn->getMessage() . "<br/>";
	    echo 'Standard Code: ' . $conn->getCode() . "<br/>";
	    echo 'DBMS/User Message: ' . $conn->getUserInfo() . "<br/>";
	    echo 'DBMS/Debug Message: ' . $conn->getDebugInfo() . "<br/>";
	}	
	}
?>