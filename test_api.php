<?php
// Test script to verify API endpoints
echo "Testing API endpoints...\n\n";

// Test 1: get_pending_borrowings.php
echo "1. Testing get_pending_borrowings.php:\n";
$ch = curl_init('http://192.168.4.143/API_PHP/v1/get_pending_borrowings.php');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_TIMEOUT, 10);
$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

echo "HTTP Code: $http_code\n";
if ($http_code == 200) {
    $data = json_decode($response, true);
    echo "Status: " . $data['status'] . "\n";
    echo "Count: " . $data['count'] . "\n";
    if ($data['count'] > 0) {
        echo "Sample data:\n";
        print_r($data['data'][0]);
    }
} else {
    echo "Error response: $response\n";
}

echo "\n\n";

// Test 2: update_status.php
echo "2. Testing update_status.php:\n";
$ch = curl_init('http://192.168.4.143/API_PHP/v1/update_status.php');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query([
    'id' => 1,
    'status' => 'approved'
]));
curl_setopt($ch, CURLOPT_TIMEOUT, 10);
$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

echo "HTTP Code: $http_code\n";
if ($http_code == 200) {
    $data = json_decode($response, true);
    echo "Status: " . $data['status'] . "\n";
    echo "Message: " . $data['message'] . "\n";
} else {
    echo "Error response: $response\n";
}
