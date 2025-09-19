<?php 

class dbOperation {
    private $conn;

    function __construct() {
        require_once dirname(__FILE__) . '/dbConnect.php';

        $db = new dbConnect();
        $this->conn = $db->connect();
    }

//registerUser
    public function registerUser($name, $email, $password, $role = "students") {
        $hashed_password = password_hash($password, PASSWORD_BCRYPT);
        $stmt = $this->conn->prepare("INSERT INTO users 
        (name, email, password, role, approval_status) VALUES (?, ?, ?, ?, 'pending')");
        $stmt->bind_param("ssss", $name, $email, $hashed_password, $role);
        return $stmt->execute();
    }


   
public function loginUser($identifier, $password){
    $stmt = $this->conn->prepare("SELECT id, name, email, password FROM users WHERE email=? OR name=?");
    $stmt->bind_param("ss", $identifier, $identifier);
    $stmt->execute();
    $result = $stmt->get_result()->fetch_assoc();

    if ($result && password_verify($password, $result['password'])) {
        // jangan return password
        unset($result['password']);
        return $result;
    }
    return false;
}


    public function getCommodities() {
        $result = $this->conn->query("SELECT * FROM commodities");
        $commodities = [];
        while ($row = $result->fetch_assoc()) {
            $commodities[] = $row;
        }
        return $commodities;
    }

    public function borrowCommodity($student_id, $commodity_id, $quantity, $tujuan) {
        $stmt = $this->conn->prepare("INSERT INTO borrowings (student_id, 
        commodity_id, borrow_date, tujuan, status) VALUES (?, ?, CURDATE(), ?, ?, 'pending')");
        $stmt->bind_param("iiis", $student_id, $commodity_id, $quantity, $tujuan);
        return $stmt->execute();
    }

    public function returnCommodity($borrowing_id, $condition, $photo) {
        $stmt = $this->conn->prepare("UPDATE borrowings SET status='returned', 
        return_date=CURDATE(), return_condition=?, return_photo=? WHERE id=?");
        $stmt->bind_param("ssi", $condition, $photo, $borrowing_id);
        return $stmt->execute();
    }



    
}

?>