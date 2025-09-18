<?php

require_once '../includes/dbOperation.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['name']) && isset($_POST['email']) && isset($_POST['password'])) {
        $db = new DbOperation();
        if ($db->registerUser($_POST['name'], $_POST['email'], $_POST['password'])) {
            $response['error'] = false;
            $response['message'] = "Registrasi berhasil, menunggu persetujuan admin.";
        } else {
            $response['error'] = true;
            $response['message'] = "Gagal registrasi (email mungkin sudah dipakai).";
        }
    } else {
        $response['error'] = true;
        $response['message'] = "Parameter tidak lengkap.";
    }
} else {
    $response['error'] = true;
    $response['message'] = "Invalid Request.";
}

echo json_encode($response);



// $response = array();

// if ($_SERVER['REQUEST_METHOD'] == 'POST') {

//     if (
//         isset($_POST['username']) &&
//         isset($_POST['email']) &&
//         isset($_POST['password'])
//     ) {
//         // Operate data further
//         $db = new dbOperation();

//         $result = $db->createUser(
//                 $_POST['username'],
//                 $_POST['password'],
//                 $_POST['email']
//                 );

//         if ($result == 1) {
//             $response['error'] = false;
//             $response['message'] = "User registered successfully";
//         } elseif($result == 2) {
//             $response['error'] = true;
//             $response['message'] = "Some error occurred, please try again";
//         } elseif($result == 0) {
//             $response['error'] = true;
//             $response['message'] = "It seems you are already registered, please choose a 
//             different email and username";
//         }

//     } else {
//         $response['error'] = true;
//         $response['message'] = "Required fields are missing";
//     }

// } else {
//     $response['error'] = true;
//     $response['message'] = "Invalid Request";
// }

// echo json_encode($response);

?>
