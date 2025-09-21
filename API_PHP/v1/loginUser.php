    <?php

    require_once '../includes/dbOperation.php';

    $response = array();

   if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['identifier']) && isset($_POST['password'])) {
        $db = new DbOperation();
        $user = $db->loginUser($_POST['identifier'], $_POST['password']);

        if ($user) {
            $response['error'] = false;
            $response['user'] = $user;
        } else {
            $response['error'] = true;
            $response['message'] = "Email/Username atau password salah.";
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
    ?>