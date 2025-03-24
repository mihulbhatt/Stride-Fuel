<?php

require_once '../includes/DBConnect.php';  // database connection

$response = array();
$db = new DbConnect();
$conn = $db->connect();

if(isset($_POST['username']) && isset($_POST['calorie_goal'])){
    $username = $_POST['username'];
    $calorie_goal = $_POST['calorie_goal'];

    // Prepare and execute SQL query (use prepared statements to prevent SQL injection)
    $stmt = $conn->prepare("UPDATE users SET calorie_goal = ? WHERE username = ?");
    $stmt->bind_param("is", $calorie_goal, $username);

    if($stmt->execute()){
        $response['error'] = false;
        $response['message'] = 'Calorie goal updated successfully';
    } else {
        $response['error'] = true;
        $response['message'] = 'Failed to update calorie goal';
    }
    $stmt->close();
} else {
    $response['error'] = true;
    $response['message'] = 'Required parameters not provided';
}

// Return JSON response
echo json_encode($response);
?>
