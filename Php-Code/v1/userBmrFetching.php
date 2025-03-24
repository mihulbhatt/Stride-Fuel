<?php

require_once '../includes/DBConnect.php';  

// Create database connection instance and get the mysqli connection
$db = new DbConnect();
$conn = $db->connect();

$response = array();

if (isset($_POST['username'])) {
    $username = $_POST['username'];

    // Prepare the SQL statement to retrieve the calorie goal (BMR) for the given username
    $stmt = $conn->prepare("SELECT calorie_goal FROM users WHERE username = ?");
    if (!$stmt) {
        $response['error'] = true;
        $response['message'] = "Failed to prepare statement: " . $conn->error;
        echo json_encode($response);
        exit;
    }

    $stmt->bind_param("s", var: $username);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $stmt->bind_result($calorie_goal);
        $stmt->fetch();
        $response['error'] = false;
        $response['message'] = "BMR retrieved successfully";
        $response['bmr'] = $calorie_goal;
    } else {
        $response['error'] = true;
        $response['message'] = "User not found or BMR not set";
    }
    $stmt->close();
} else {
    $response['error'] = true;
    $response['message'] = "Required parameter (username) not provided";
}

echo json_encode($response);
?>
