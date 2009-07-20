<?php  
$date_start = $_GET['date_start'];
$date_end = $_GET['date_end'];

$url = "https://csc.ccbchurch.com/app/api.php?srv=public_calendar_listing";

if ($date_start != null) {
  	$url .= "&date_start=" . $date_start;
	if ($date_end != null) {
		$url .= "&date_end=" . $date_end;
	}
}

//echo $url; //Debug
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL,$url);
curl_setopt($ch, CURLOPT_VERBOSE, 1);
curl_setopt($ch, CURLOPT_RETURNTRANSFER,1);
curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, '1');
//curl_setopt($ch, CURLOPT_CAINFO, getcwd().'/ccbcert.crt'); //a ref to the CA for CCB if SSL isn't configured properly
curl_setopt($ch, CURLOPT_USERPWD,"username:password");
$returned = curl_exec($ch);
curl_close ($ch);
echo $returned;

?>


