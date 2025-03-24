<?php
// Set the response type to JSON
header("Content-Type: application/json");

// Checking if the required POST parameter 'userId' is provided
if (empty($_POST['userId'])) {
    echo json_encode(["error" => "No userId provided"]);
    exit;
}

// Retrieving the userId from POST parameters
$userId = $_POST['userId'];

// Included the database connection class and establish a connection
include_once '../includes/DBConnect.php';
$db = new DbConnect();
$mysqli = $db->connect();

// Set the default timezone
date_default_timezone_set('Asia/Kolkata');

// Calculate today's boundaries as Unix timestamps
$startOfDayUnix = strtotime("today midnight");    // Today at 00:00:00
$endOfDayUnix   = strtotime("tomorrow midnight");   // Tomorrow at 00:00:00

// Convert the Unix timestamps to DATETIME strings (format: YYYY-MM-DD HH:MM:SS)
$startOfDay = date("Y-m-d H:i:s", $startOfDayUnix);
$endOfDay   = date("Y-m-d H:i:s", $endOfDayUnix);

// Prepare the SQL query to fetch today's meal entries for this user
$sql = "SELECT * FROM user_meals 
        WHERE user_id = ? 
          AND meal_date >= ? 
          AND meal_date < ? 
        ORDER BY meal_date DESC";
$stmt = $mysqli->prepare($sql);

if (!$stmt) {
    echo json_encode(["error" => "Failed to prepare statement: " . $mysqli->error]);
    $mysqli->close();
    exit;
}

// Bind parameters: "i" for integer (user_id) and "s" for strings (startOfDay and endOfDay)
$stmt->bind_param("iss", $userId, $startOfDay, $endOfDay);
$stmt->execute();

// Get the result set from the executed statement
$result = $stmt->get_result();

// Fetch all rows into an array
$meals = [];
while ($row = $result->fetch_assoc()) {
    $meals[] = $row;
}

// Clean up: close the statement and the database connection
$stmt->close();
$mysqli->close();

// Return the JSON-encoded result with a "meals" key
echo json_encode(["meals" => $meals]);
?>
